package me.kubbidev.nexuspowered.cooldown;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

class CooldownMapImpl<T> implements CooldownMap<T> {
    private final Map<T, Cooldown> cache = new ConcurrentHashMap<>();

    @NotNull
    public Optional<Cooldown> get(@NotNull T key) {
        return Optional.ofNullable(this.cache.get(key));
    }

    @Override
    public void put(@NotNull T key, @NotNull Cooldown cooldown) {
        Objects.requireNonNull(key, "key");
        this.cache.put(key, cooldown);
    }

    @NotNull
    public Map<T, Cooldown> getAll() {
        return ImmutableMap.copyOf(this.cache);
    }
}