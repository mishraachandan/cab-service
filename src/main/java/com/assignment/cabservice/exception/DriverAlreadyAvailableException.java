package com.assignment.cabservice.exception;

public class DriverAlreadyAvailableException extends RuntimeException {

    private String message;
    public DriverAlreadyAvailableException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
