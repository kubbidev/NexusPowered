package me.kubbidev.nexuspowered.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.kubbidev.nexuspowered.gson.GsonSerializable;
import me.kubbidev.nexuspowered.gson.JsonBuilder;
import me.kubbidev.nexuspowered.reflect.ServerReflection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.function.Supplier;

/**
 * Encapsulates the servers "ticks per second" (TPS) reading.
 */
public final class Tps implements GsonSerializable {
    private static final Supplier<double[]> SUPPLIER = getSupplier();

    private static Supplier<double[]> getSupplier() {
        try {
            Field recentTps = ServerReflection.nmsClass("MinecraftServer")
                    .getDeclaredField("recentTps");

            Class<?> craftServer = ServerReflection.obcClass("CraftServer");
            Object minecraftServer = craftServer.getDeclaredMethod("getServer")
                    .invoke(craftServer.cast(Bukkit.getServer()));
            return () -> {
                try {
                    return (double[]) recentTps.get(minecraftServer);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            };
        } catch (Exception e) {
            // ignore
        }

        return null;
    }

    public static boolean isReadSupported() {
        return SUPPLIER != null;
    }

    public static Tps read() {
        if (!isReadSupported()) {
            throw new UnsupportedOperationException("Unable to supply server tps");
        }
        return new Tps(SUPPLIER.get());
    }

    private final double avg1;
    private final double avg5;
    private final double avg15;
    private final double[] asArray;

    public Tps(double avg1, double avg5, double avg15) {
        this.avg1 = avg1;
        this.avg5 = avg5;
        this.avg15 = avg15;
        this.asArray = new double[]{avg1, avg5, avg15};
    }

    public Tps(double[] values) {
        this.avg1 = values[0];
        this.avg5 = values[1];
        this.avg15 = values[2];
        this.asArray = values;
    }

    public double avg1() {
        return this.avg1;
    }

    public double avg5() {
        return this.avg5;
    }

    public double avg15() {
        return this.avg15;
    }

    public double[] asArray() {
        return this.asArray;
    }

    @Override
    public String toString() {
        return String.join(", ", format(this.avg1), format(this.avg5), format(this.avg15));
    }

    public static String format(double tps) {
        StringBuilder sb = new StringBuilder();
        if (tps > 18.0) {
            sb.append(ChatColor.GREEN);
        } else if (tps > 16.0) {
            sb.append(ChatColor.YELLOW);
        } else {
            sb.append(ChatColor.RED);
        }

        sb.append(Math.min(Math.round(tps * 100.0) / 100.0, 20.0));
        if (tps > 20.0) {
            sb.append('*');
        }

        return sb.toString();
    }

    @Override
    public @NotNull JsonElement serialize() {
        return JsonBuilder.object()
                .add("avg1", this.avg1)
                .add("avg5", this.avg5)
                .add("avg15", this.avg15)
                .build();
    }

    public static Tps deserialize(JsonElement element) {
        JsonObject object = element.getAsJsonObject();
        return new Tps(
                object.get("avg1").getAsDouble(),
                object.get("avg5").getAsDouble(),
                object.get("avg15").getAsDouble()
        );
    }
}