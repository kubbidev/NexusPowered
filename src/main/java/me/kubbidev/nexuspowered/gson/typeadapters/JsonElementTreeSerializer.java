package me.kubbidev.nexuspowered.gson.typeadapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import me.kubbidev.nexuspowered.datatree.DataTree;
import me.kubbidev.nexuspowered.datatree.GsonDataTree;

public final class JsonElementTreeSerializer implements JsonSerializer<DataTree>, JsonDeserializer<DataTree> {

    public static final JsonElementTreeSerializer INSTANCE = new JsonElementTreeSerializer();

    private JsonElementTreeSerializer() {
    }

    @Override
    public DataTree deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
        return DataTree.from(json);
    }

    @Override
    public JsonElement serialize(DataTree src, Type typeOfSrc, JsonSerializationContext context) {
        return ((GsonDataTree) src).element();
    }
}