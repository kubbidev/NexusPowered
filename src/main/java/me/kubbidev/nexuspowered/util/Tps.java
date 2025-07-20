package me.kubbidev.nexuspowered.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.function.Supplier;
import me.kubbidev.nexuspowered.gson.GsonSerializable;
import me.kubbidev.nexuspowered.gson.GsonBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * Encapsulates the servers "ticks per second" (TPS) reading.
 */
public final class Tps implements GsonSerializable {

    private static final Supplier<double[]> SUPPLIER = Bukkit::getTPS;
    private final        double             avg1;
    private final        double             avg5;
    private final        double             avg15;
    private final        double[]           asArray;

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

    public static boolean isReadSupported() {
        return SUPPLIER != null;
    }

    public static Tps read() {
        if (!isReadSupported()) {
            throw new UnsupportedOperationException("Unable to supply server tps");
        }
        return new Tps(SUPPLIER.get());
    }

    public static Component format(double tps) {
        TextComponent.Builder builder = Component.text();
        if (tps > 18.0) {
            builder.color(NamedTextColor.GREEN);
        } else if (tps > 16.0) {
            builder.color(NamedTextColor.YELLOW);
        } else {
            builder.color(NamedTextColor.RED);
        }

        builder.append(Component.text(Math.min(Math.round(tps * 100.0) / 100.0, 20.0)));
        if (tps > 20.0) {
            builder.append(Component.text('*'));
        }

        return builder.build();
    }

    public static Tps deserialize(JsonElement element) {
        JsonObject object = element.getAsJsonObject();
        return new Tps(
            object.get("avg1").getAsDouble(),
            object.get("avg5").getAsDouble(),
            object.get("avg15").getAsDouble()
        );
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
    public @NotNull JsonElement serialize() {
        return GsonBuilder.object()
            .add("avg1", this.avg1)
            .add("avg5", this.avg5)
            .add("avg15", this.avg15)
            .build();
    }
}