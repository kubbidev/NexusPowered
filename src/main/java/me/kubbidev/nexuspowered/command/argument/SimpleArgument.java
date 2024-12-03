package me.kubbidev.nexuspowered.command.argument;

import me.kubbidev.nexuspowered.util.annotation.NotNullByDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@NotNullByDefault
public class SimpleArgument implements Argument {
    protected final int index;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected final Optional<String> value;

    public SimpleArgument(int index, @Nullable String value) {
        this.index = index;
        this.value = Optional.ofNullable(value);
    }

    @Override
    public int index() {
        return this.index;
    }

    @Override
    public @NotNull Optional<String> value() {
        return this.value;
    }

    @Override
    public boolean isPresent() {
        return this.value.isPresent();
    }
}