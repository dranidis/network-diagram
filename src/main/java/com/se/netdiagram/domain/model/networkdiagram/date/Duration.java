package com.se.netdiagram.domain.model.networkdiagram.date;

public class Duration {

    private long value;

    public Duration(long value) {
        if (value < 0)
            throw new IllegalArgumentException("Duration must be positive");
        this.value = value;
    }

    public long value() {
        return value;
    }

    public static Duration difference(long asLong, long asLong2) {
        return new Duration(asLong - asLong2);
    }

    // public static Duration ofDays(int days) {
    //     return new Duration(days);
    // }

    // public static Duration ofWeeks(int weeks) {
    //     return new Duration(weeks * 7);
    // }

}
