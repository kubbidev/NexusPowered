package me.kubbidev.nexuspowered.metadata;

import com.google.common.reflect.TypeToken;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNullByDefault;

@NotNullByDefault
final class MetadataKeyImpl<T> implements MetadataKey<T> {

    private final String       id;
    private final TypeToken<T> type;

    MetadataKeyImpl(String id, TypeToken<T> type) {
        this.id = id.toLowerCase();
        this.type = type;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public @NotNull TypeToken<T> getType() {
        return this.type;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T cast(Object object) throws ClassCastException {
        Objects.requireNonNull(object, "object");
        return (T) this.type.getRawType().cast(object);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MetadataKeyImpl && ((MetadataKeyImpl<?>) obj).getId().equals(this.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}