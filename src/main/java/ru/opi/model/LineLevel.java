package ru.opi.model;

public enum LineLevel {
    ONE(1),
    TWO(2),
    THREE(3);

    private final int value;

    LineLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}