package com.arthur.urlshortener.auth.exception;

public class InvalidEmailException extends AuthException {
    public InvalidEmailException(String message) {
        super(message);
    }
}
