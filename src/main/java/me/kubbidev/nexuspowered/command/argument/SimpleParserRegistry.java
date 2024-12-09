package me.kubbidev.nexuspowered.command.argument;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SimpleParserRegistry implements ArgumentParserRegistry {
    private final Map<TypeToken<?>, List<ArgumentParser<?>>> parsers = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull <T> Optional<ArgumentParser<T>> find(@NotNull TypeToken<T> type) {
        Objects.requireNonNull(type, "type");
        List<ArgumentParser<?>> parsers = this.parsers.get(type);
        if (parsers == null || parsers.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of((ArgumentParser<T>) parsers.get(0));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public @NotNull <T> Collection<ArgumentParser<T>> findAll(@NotNull TypeToken<T> type) {
        Objects.requireNonNull(type, "type");
        List<ArgumentParser<?>> parsers = this.parsers.get(type);
        if (parsers == null || parsers.isEmpty()) {
            return ImmutableList.of();
        }

        return (Collection) Collections.unmodifiableList(parsers);
    }

    @Override
    public <T> void register(@NotNull TypeToken<T> type, @NotNull ArgumentParser<T> parser) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(parser, "parser");
        List<ArgumentParser<?>> list = this.parsers.computeIfAbsent(type, t -> new CopyOnWriteArrayList<>());
        if (!list.contains(parser)) {
            list.add(parser);
        }
    }
}