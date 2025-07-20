package me.kubbidev.nexuspowered.command.argument;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

@NotNullByDefault
public class SimpleArgument implements Argument {

    protected final int    index;
    @Nullable
    protected final String value;

    public SimpleArgument(int index, @Nullable String value) {
        this.index = index;
        this.value = value;
    }

    @Override
    public int index() {
        return this.index;
    }

    @Override
    public @NotNull Optional<String> value() {
        return Optional.ofNullable(this.value);
    }

    @Override
    public boolean isPresent() {
        return this.value != null;
    }
}