package com.se.netdiagram;

import java.util.OptionalLong;

public class Util {

    public static OptionalLong max(OptionalLong oldMax, OptionalLong newValue) {
        if (oldMax.getAsLong() < newValue.getAsLong()) {
            return newValue;
        }
        return oldMax;
    }

    public static OptionalLong min(OptionalLong oldMin, OptionalLong newValue) {
        if (oldMin.getAsLong() > newValue.getAsLong()) {
            return newValue;
        }
        return oldMin;
    }

}
