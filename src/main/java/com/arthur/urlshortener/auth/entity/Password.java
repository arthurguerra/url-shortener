package com.arthur.urlshortener.auth.entity;

import com.arthur.urlshortener.auth.exception.WeakPasswordException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.springframework.security.crypto.password.PasswordEncoder;

@Embeddable
public class Password {

    @Column(name = "password", nullable = false)
    private String hash;

    protected Password() {
    }

    private Password(String hash) {
        this.hash = hash;
    }

    public static Password encode(String rawPassword, PasswordEncoder encoder) {
        if (rawPassword == null || rawPassword.length() < 8) {
            throw new WeakPasswordException("Password must contain at least 8 characters.");
        }
        return new Password(encoder.encode(rawPassword));
    }

    public boolean matches(String rawPassword, PasswordEncoder encoder) {
        return encoder.matches(rawPassword, this.hash);
    }

    public String getHash() {
        return hash;
    }
}
