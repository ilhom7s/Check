package com.unversity.programm.exceptions;

public class AlreadyInBaseException extends RuntimeException {
    public AlreadyInBaseException(String message) {
        super(message);
    }

    public AlreadyInBaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
