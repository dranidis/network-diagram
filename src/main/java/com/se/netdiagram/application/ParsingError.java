package com.se.netdiagram.application;

public class ParsingError extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ParsingError(String errorMessage) {
        super(errorMessage);
    }
}
