package net.johanbasson.rail;

import com.google.common.collect.ImmutableSet;
import net.johanbasson.rail.func.Function2;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Result<S, F> {

    public static <S,F> Result<S, F> success(S value) {
        return new Success<>(value);
    }

    public static <S, F> Result<S, F> failure(F value) {
        return new Failure<>(value);
    }

    public abstract Result<S,F> onSuccess(Consumer<S> consumer);

    public abstract Result<S, F> onFailure(Consumer<F> consumer);

    public abstract boolean isFailure();

    public abstract boolean isSuccess();

    public abstract <T, F> Result<T, F> then(Function<S, Result<T, F>> f);

    public abstract <T, F1> Result<T, F1> convert(Function<S, T> f);

    abstract S get();

    abstract F getError();

    abstract <E> Result<S, E> mapError(Function<F, E> mapper);

    public static final class Builder<T1, T2, E> {

        private final Result<T1, E> result1;
        private final Result<T2, E> result2;

        public Builder(Result<T1, E> result1, Result<T2, E> result2) {
            this.result1 = result1;
            this.result2 = result2;
        }

        public <U> Result<U, ImmutableSet<E>> ap(Function2<T1, T2, U> mapper) {
            if (result1.isSuccess()) {
                if (result2.isSuccess()) {
                    return Result.success(mapper.apply(result1.get(), result2.get()));
                } else {
                    return Result.failure(ImmutableSet.of(result2.getError()));
                }
            } else {
                if (result2.isSuccess()) {
                    return Result.failure(ImmutableSet.of(result1.getError()));
                } else {
                    return Result.failure(ImmutableSet.of(result1.getError(), result2.getError()));
                }
            }
        }
    }

    public static <U, S1, S2, E> Builder<S1, S2, E> combine(Result<S1, E> res1, Result<S2, E> res2) {
        return new Builder<>(res1, res2);
    }

    private static class Success<S, F> extends Result<S, F> {
        private final S value;

        public Success(S value) {
            this.value = Objects.requireNonNull(value, "Success cannot be null");
        }

        @Override
        public Result<S, F> onSuccess(Consumer<S> consumer) {
            consumer.accept(value);
            return this;
        }

        @Override
        public Result<S, F> onFailure(Consumer<F> consumer) {
            return this;
        }

        @Override
        public boolean isFailure() {
            return false;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public <T, F1> Result<T, F1> then(Function<S, Result<T, F1>> f) {
            return f.apply(value);
        }

        @Override
        public <T, F1> Result<T, F1> convert(Function<S, T> f) {
            return new Success<>(f.apply(value));
        }

        @Override
        S get() {
            return value;
        }

        @Override
        F getError() {
            throw new RuntimeException("Cannot get error from success");
        }

        @Override
        <E> Result<S, E> mapError(Function<F, E> mapper) {
            return new Success<>(value);
        }
    }

    private static class Failure<S, F> extends Result<S, F> {
        private final F value;

        public Failure(F value) {
            this.value = Objects.requireNonNull(value, "Failure cannot be null");
        }

        @Override
        public Result<S, F> onSuccess(Consumer<S> consumer) {
            return this;
        }

        @Override
        public Result<S, F> onFailure(Consumer<F> consumer) {
            consumer.accept(value);
            return this;
        }

        @Override
        public boolean isFailure() {
            return true;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public <T, F1> Result<T, F1> then(Function<S, Result<T, F1>> f) {
            return new Failure(value);
        }

        @Override
        public <T, F1> Result<T, F1> convert(Function<S, T> f) {
            return new Failure(value);
        }

        @Override
        S get() {
            throw new RuntimeException("Cannot get success value from failure");
        }

        @Override
        F getError() {
            return value;
        }

        @Override
        <E> Result<S, E> mapError(Function<F, E> mapper) {
            return Result.failure(mapper.apply(value));
        }

    }
}
