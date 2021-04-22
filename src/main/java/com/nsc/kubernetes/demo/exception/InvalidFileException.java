package com.nsc.kubernetes.demo.exception;

public class InvalidFileException extends RuntimeException {

    public InvalidFileException(String message) {
        super(message);
    }

    public InvalidFileException(String message, Exception e) {
        super(message, e);
    }
}
