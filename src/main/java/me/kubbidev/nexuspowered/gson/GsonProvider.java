package me.kubbidev.nexuspowered.gson;

import com.google.gson.*;
import me.kubbidev.nexuspowered.datatree.DataTree;
import me.kubbidev.nexuspowered.gson.typeadapters.BukkitSerializableAdapterFactory;
import me.kubbidev.nexuspowered.gson.typeadapters.GsonSerializableAdapterFactory;
import me.kubbidev.nexuspowered.gson.typeadapters.JsonElementTreeSerializer;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;
import java.util.Objects;

/**
 * Provides static instances of Gson
 */
public final class GsonProvider {

    private static final Gson NORMAL = new GsonBuilder()
            .registerTypeHierarchyAdapter(DataTree.class, JsonElementTreeSerializer.INSTANCE)
            .registerTypeAdapterFactory(GsonSerializableAdapterFactory.INSTANCE)
            .registerTypeAdapterFactory(BukkitSerializableAdapterFactory.INSTANCE)
            .serializeNulls()
            .disableHtmlEscaping()
            .create();

    private static final Gson PRETTY_PRINT = new GsonBuilder()
            .registerTypeHierarchyAdapter(DataTree.class, JsonElementTreeSerializer.INSTANCE)
            .registerTypeAdapterFactory(GsonSerializableAdapterFactory.INSTANCE)
            .registerTypeAdapterFactory(BukkitSerializableAdapterFactory.INSTANCE)
            .serializeNulls()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    private static final JsonParser NORMAL_PARSER = new JsonParser();

    @NotNull
    public static JsonParser parser() {
        return NORMAL_PARSER;
    }

    @NotNull
    public static Gson normal() {
        return NORMAL;
    }

    @NotNull
    public static Gson prettyPrinting() {
        return PRETTY_PRINT;
    }

    @NotNull
    public static JsonObject readObject(@NotNull Reader reader) {
        return NORMAL_PARSER.parse(reader).getAsJsonObject();
    }

    @NotNull
    public static JsonObject readObject(@NotNull String s) {
        return NORMAL_PARSER.parse(s).getAsJsonObject();
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

    @NotNull
    public static String toString(@NotNull JsonElement element) {
        return Objects.requireNonNull(normal().toJson(element));
    }

    @NotNull
    public static String toStringPretty(@NotNull JsonElement element) {
        return Objects.requireNonNull(prettyPrinting().toJson(element));
    }

    private GsonProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}