package me.kubbidev.nexuspowered.cooldown;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

class ComposedCooldownMapImpl<I, O> implements ComposedCooldownMap<I, O> {

    private final Cooldown                  base;
    private final LoadingCache<O, Cooldown> cache;
    private final Function<I, O>            composeFunction;

    ComposedCooldownMapImpl(Cooldown base, Function<I, O> composeFunction) {
        this.base = base;
        this.composeFunction = composeFunction;
        this.cache = CacheBuilder.newBuilder()
            // remove from the cache 10 seconds after the cooldown expires
            .expireAfterAccess(base.getTimeout() + 10000L, TimeUnit.MILLISECONDS)
            .build(new CacheLoader<>() {
                @Override
                public @NotNull Cooldown load(@NotNull O key) {
                    return base.copy();
                }
            });
    }

    @Override
    public @NotNull Cooldown getBase() {
        return this.base;
    }

    public @NotNull Cooldown get(@NotNull I key) {
        Objects.requireNonNull(key, "key");
        return this.cache.getUnchecked(this.composeFunction.apply(key));
    }

    @Override
    public void put(@NotNull O key, @NotNull Cooldown cooldown) {
        Objects.requireNonNull(key, "key");
        Preconditions.checkArgument(cooldown.getTimeout() == this.base.getTimeout(), "different timeout");
        this.cache.put(key, cooldown);
    }

    public @NotNull Map<O, Cooldown> getAll() {
        return this.cache.asMap();
    }
}