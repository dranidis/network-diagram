package com.se.netdiagram;

public class CircularDependencyException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public CircularDependencyException(String errorMessage) {
        super(errorMessage);
    }
}