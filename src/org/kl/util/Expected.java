package org.kl.util;

import java.util.Objects;

public final class Expected<T, E extends Throwable> {
    private final T value;
    private final E error;

    private Expected() {
        this.value = null;
        this.error = null;
    }

    private Expected(T value) {
        this.value = Objects.requireNonNull(value);
        this.error = null;
    }

    private Expected(E error) {
        this.error = Objects.requireNonNull(error);
        this.value = null;
    }

    public static <T, E extends Throwable> Expected<T, E> of(T value) {
        return new Expected<>(value);
    }

    public static <T, E extends Throwable> Expected<T, E> of(E error) {
        return new Expected<>(error);
    }

    public boolean isValue() {
        return value != null;
    }

    public boolean isError() {
        return error != null;
    }

    public T value() {
        return value;
    }

    public E error() {
        return error;
    }
}