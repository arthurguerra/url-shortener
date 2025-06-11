package com.arthur.urlshortener.auth.exception;

public class WeakPasswordException extends AuthException {
    public WeakPasswordException(String message) {
        super(message);
    }
}
