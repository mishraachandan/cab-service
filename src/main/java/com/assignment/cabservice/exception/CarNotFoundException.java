package com.assignment.cabservice.exception;

public class CarNotFoundException extends RuntimeException {

         private String message;
        public CarNotFoundException(String message) {
            super(message);
            this.message = message;
        }

    public String getMessage() {
        return message;
    }


    }