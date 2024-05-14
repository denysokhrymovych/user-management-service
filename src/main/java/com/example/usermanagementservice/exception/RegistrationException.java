package com.example.usermanagementservice.exception;

public class RegistrationException extends Exception {
    public RegistrationException(String message) {
        super(message);
    }

    public RegistrationException(String message, Throwable ex) {
        super(message, ex);
    }
}
