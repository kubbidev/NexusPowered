package me.kubbidev.nexuspowered.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.Reader;
import java.util.Objects;
import me.kubbidev.nexuspowered.datatree.DataTree;
import me.kubbidev.nexuspowered.gson.typeadapters.BukkitSerializableAdapterFactory;
import me.kubbidev.nexuspowered.gson.typeadapters.GsonSerializableAdapterFactory;
import me.kubbidev.nexuspowered.gson.typeadapters.JsonElementTreeSerializer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;

/**
 * Provides static instances of Gson
 */
public final class GsonProvider {

    private static final Gson NORMAL = GsonComponentSerializer.gson().populator().apply(new GsonBuilder()
            .registerTypeHierarchyAdapter(DataTree.class, JsonElementTreeSerializer.INSTANCE)
            .registerTypeAdapterFactory(GsonSerializableAdapterFactory.INSTANCE)
            .registerTypeAdapterFactory(BukkitSerializableAdapterFactory.INSTANCE)
            .serializeNulls()
            .disableHtmlEscaping())
        .create();

    private static final Gson PRETTY_PRINT = GsonComponentSerializer.gson().populator().apply(new GsonBuilder()
            .registerTypeHierarchyAdapter(DataTree.class, JsonElementTreeSerializer.INSTANCE)
            .registerTypeAdapterFactory(GsonSerializableAdapterFactory.INSTANCE)
            .registerTypeAdapterFactory(BukkitSerializableAdapterFactory.INSTANCE)
            .serializeNulls()
            .disableHtmlEscaping()
            .setPrettyPrinting())
        .create();

    private GsonProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static @NotNull Gson normal() {
        return NORMAL;
    }

    public static @NotNull Gson prettyPrinting() {
        return PRETTY_PRINT;
    }

    public static @NotNull JsonObject readObject(@NotNull Reader reader) {
        return JsonParser.parseReader(reader).getAsJsonObject();
    }

    public static @NotNull JsonObject readObject(@NotNull String s) {
        return JsonParser.parseString(s).getAsJsonObject();
    }

    public static void writeObject(@NotNull Appendable writer, @NotNull JsonObject object) {
        normal().toJson(object, writer);
    }

    public static void writeObjectPretty(@NotNull Appendable writer, @NotNull JsonObject object) {
        prettyPrinting().toJson(object, writer);
    }

    public static void writeElement(@NotNull Appendable writer, @NotNull JsonElement element) {
        normal().toJson(element, writer);
    }

    public static void writeElementPretty(@NotNull Appendable writer, @NotNull JsonElement element) {
        prettyPrinting().toJson(element, writer);
    }

    public static @NotNull String toString(@NotNull JsonElement element) {
        return Objects.requireNonNull(normal().toJson(element));
    }

    public static @NotNull String toStringPretty(@NotNull JsonElement element) {
        return Objects.requireNonNull(prettyPrinting().toJson(element));
    }

}