package com.arthur.urlshortener.auth.entity;

import com.arthur.urlshortener.auth.exception.InvalidEmailException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.regex.Pattern;

@Embeddable
public class Email {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    @Column(name = "email", nullable = false, unique = true)
    private String value;

    protected Email() {
    }

    public Email (String value) {
        if (value == null || !EMAIL_PATTERN.matcher(value).matches()) {
            throw new InvalidEmailException("Invalid email: " + value);
        }
        this.value = value.toLowerCase();
    }

    public String getValue() {
        return value;
    }
}
