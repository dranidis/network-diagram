package com.se.netdiagram.domain.model.exceptions;

public class DuplicateTaskKeyException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public DuplicateTaskKeyException(String errorMessage) {
        super(errorMessage);
    }
}
