package com.morphanone.denizenmod.utilities;

import java.util.NoSuchElementException;

public class OptionalFloat {
    private static final OptionalFloat EMPTY = new OptionalFloat();

    private final boolean isPresent;

    private final float value;

    private OptionalFloat() {
        this.isPresent = false;
        this.value = Float.NaN;
    }

    private OptionalFloat(float value) {
        this.isPresent = true;
        this.value = value;
    }

    public static OptionalFloat empty() {
        return EMPTY;
    }

    public static OptionalFloat of(float value) {
        return new OptionalFloat(value);
    }

    public boolean isPresent() {
        return isPresent;
    }

    public float getAsFloat() {
        if (!isPresent) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    public float orElseThrow() {
        if (!isPresent) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof OptionalFloat other) {
            return isPresent && other.isPresent ? Float.compare(value, other.value) == 0 : isPresent == other.isPresent;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return isPresent ? Float.hashCode(value) : 0;
    }

    @Override
    public String toString() {
        return isPresent ? String.format("OptionalFloat[%s]", value) : "OptionalFloat.empty";
    }
}
