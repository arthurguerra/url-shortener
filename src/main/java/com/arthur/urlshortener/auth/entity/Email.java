package com.arthur.urlshortener.auth.entity;

import com.arthur.urlshortener.auth.exception.InvalidEmailException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.regex.Pattern;

@Embeddable
public class Email {

    private static final String EMAIL_REGEX =
            "^" +
            "(?=.{1,320}$)" +
            "(?=([^@]{1,64})@)" +
            "(?=[^@]*@(.{1,253})$)" +
            "^[a-zA-Z0-9]+([._-][a-zA-Z0-9]+)*@[a-zA-Z0-9]+([.-][a-zA-Z0-9]+)*\\.[a-zA-Z]{2,}$";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

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
