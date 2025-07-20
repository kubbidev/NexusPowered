package me.kubbidev.nexuspowered.metadata;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import me.kubbidev.nexuspowered.cache.LoadingMap;
import org.jetbrains.annotations.NotNull;

/**
 * A basic implementation of {@link MetadataRegistry} using a LoadingCache.
 *
 * @param <T> the type
 */
public class AbstractMetadataRegistry<T> implements MetadataRegistry<T> {

    private static final Function<?, MetadataMap>   LOADER = new Loader<>();
    protected final      LoadingMap<T, MetadataMap> cache  = LoadingMap.of(getLoader());

    @SuppressWarnings("unchecked")
    private static <T> Function<T, MetadataMap> getLoader() {
        return (Function<T, MetadataMap>) LOADER;
    }

    @Override
    public @NotNull MetadataMap provide(@NotNull T id) {
        Objects.requireNonNull(id, "id");
        return Objects.requireNonNull(this.cache.get(id));
    }

    @Override
    public @NotNull Optional<MetadataMap> get(@NotNull T id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(this.cache.getIfPresent(id));
    }

    @Override
    public void remove(@NotNull T id) {
        MetadataMap map = this.cache.remove(id);
        if (map != null) {
            map.clear();
        }
    }

    @Override
    public void cleanup() {
        // MetadataMap#isEmpty also removes expired values
        this.cache.values().removeIf(MetadataMap::isEmpty);
    }

    private static final class Loader<T> implements Function<T, MetadataMap> {

        @Override
        public MetadataMap apply(T t) {
            return MetadataMap.create();
        }
    }
}