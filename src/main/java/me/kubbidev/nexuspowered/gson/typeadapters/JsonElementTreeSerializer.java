package me.kubbidev.nexuspowered.gson.typeadapters;

import com.google.gson.*;
import me.kubbidev.nexuspowered.datatree.DataTree;
import me.kubbidev.nexuspowered.datatree.GsonDataTree;

import java.lang.reflect.Type;

public final class JsonElementTreeSerializer implements JsonSerializer<DataTree>, JsonDeserializer<DataTree> {
    public static final JsonElementTreeSerializer INSTANCE = new JsonElementTreeSerializer();

    private JsonElementTreeSerializer() {

    }

    @Override
    public DataTree deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return DataTree.from(json);
    }

    @Override
    public JsonElement serialize(DataTree src, Type typeOfSrc, JsonSerializationContext context) {
        return ((GsonDataTree) src).getElement();
    }
}