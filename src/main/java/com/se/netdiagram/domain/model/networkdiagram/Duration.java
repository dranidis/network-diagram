package com.se.netdiagram.domain.model.networkdiagram;

public class Duration {

    private int value;

    public Duration(int value) {
        if (value < 0)
            throw new IllegalArgumentException("Duration must be positive");
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static Duration ofDays(int days) {
        return new Duration(days);
    }

    public static Duration ofWeeks(int weeks) {
        return new Duration(weeks * 7);
    }

}
