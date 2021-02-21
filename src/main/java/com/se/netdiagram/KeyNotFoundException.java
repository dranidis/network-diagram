package com.se.netdiagram;

public class KeyNotFoundException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public KeyNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}