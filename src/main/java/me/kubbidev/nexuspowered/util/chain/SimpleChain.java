package me.kubbidev.nexuspowered.util.chain;

import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@NotNullByDefault
class SimpleChain<T> implements Chain<T> {

    @Nullable
    private T object;

    SimpleChain(@Nullable T object) {
        this.object = object;
    }

    @Override
    public Chain<T> apply(Consumer<? super T> action) {
        Objects.requireNonNull(action, "action");
        action.accept(this.object);
        return this;
    }

    @Override
    public Chain<T> applyIf(Predicate<? super T> test, Consumer<? super T> action) {
        Objects.requireNonNull(test, "test");
        Objects.requireNonNull(action, "action");
        if (test.test(this.object)) {
            action.accept(this.object);
        }
        return this;
    }

    @Override
    public Chain<T> applyIfNotNull(Consumer<? super T> action) {
        Objects.requireNonNull(action, "action");
        if (this.object != null) {
            action.accept(this.object);
        }
        return this;
    }

    @Override
    public Chain<T> orElse(Predicate<? super T> test, T failValue) {
        Objects.requireNonNull(test, "test");
        if (!test.test(this.object)) {
            this.object = failValue;
        }
        return this;
    }

    @Override
    public Chain<T> orElseIfNull(T otherValue) {
        if (this.object == null) {
            this.object = otherValue;
        }
        return this;
    }

    @Override
    public Chain<T> orElseGet(Predicate<? super T> test, Supplier<? extends T> failSupplier) {
        Objects.requireNonNull(test, "test");
        Objects.requireNonNull(failSupplier, "failSupplier");
        if (!test.test(this.object)) {
            this.object = failSupplier.get();
        }
        return this;
    }

    @Override
    public Chain<T> orElseGetIfNull(Supplier<? extends T> supplier) {
        Objects.requireNonNull(supplier, "supplier");
        if (this.object == null) {
            this.object = supplier.get();
        }
        return this;
    }

    @Override
    public <R> Chain<R> ifElse(Predicate<? super T> test, R passValue, R failValue) {
        Objects.requireNonNull(test, "test");
        return test.test(this.object) ? map(s -> passValue) : map(s -> failValue);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> Chain<R> map(Function<? super T, ? extends R> mapper) {
        Objects.requireNonNull(mapper, "mapper");

        R result = mapper.apply(this.object);

        // try to reduce unnecessary instance creation
        if (result == this.object) {
            return (Chain<R>) this;
        } else {
            return new SimpleChain<>(result);
        }
    }

    @Override
    public <R> Chain<R> mapOrElse(Predicate<? super T> test, Function<? super T, ? extends R> passedMapper, R otherValue) {
        Objects.requireNonNull(test, "test");
        Objects.requireNonNull(passedMapper, "passedMapper");

        return mapOrElse(test, passedMapper, s -> otherValue);
    }

    @Override
    public <R> Chain<R> mapOrElse(Predicate<? super T> test, Function<? super T, ? extends R> passedMapper, Function<? super T, ? extends R> failedMapper) {
        Objects.requireNonNull(test, "test");
        Objects.requireNonNull(passedMapper, "passedMapper");
        Objects.requireNonNull(failedMapper, "failedMapper");

        return test.test(this.object) ? map(passedMapper) : map(failedMapper);
    }

    @Override
    public <R> Chain<R> mapNullSafe(Function<? super T, ? extends R> nonNullMapper, R otherValue) {
        Objects.requireNonNull(nonNullMapper, "nonNullMapper");
        return mapNullSafeGet(nonNullMapper, () -> otherValue);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> Chain<R> mapNullSafeGet(Function<? super T, ? extends R> nonNullMapper, Supplier<? extends R> nullSupplier) {
        Objects.requireNonNull(nonNullMapper, "nonNullMapper");
        Objects.requireNonNull(nullSupplier, "nullSupplier");

        final R result = this.object != null ? nonNullMapper.apply(this.object) : nullSupplier.get();
        // try to reduce unnecessary instance creation
        if (result == this.object) {
            return (Chain<R>) this;
        } else {
            return new SimpleChain<>(result);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> Chain<R> flatMap(Function<? super T, ? extends Chain<? extends R>> mapper) {
        Objects.requireNonNull(mapper, "mapper");
        return (Chain<R>) mapper.apply(this.object);
    }

    @Override
    public Optional<T> end() {
        return Optional.ofNullable(this.object);
    }

    @Override
    public @Nullable T endOrNull() {
        return this.object;
    }
}