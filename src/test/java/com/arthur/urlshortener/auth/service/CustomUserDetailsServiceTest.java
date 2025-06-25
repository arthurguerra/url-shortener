package com.arthur.urlshortener.auth.service;

import com.arthur.urlshortener.auth.entity.Email;
import com.arthur.urlshortener.auth.entity.Password;
import com.arthur.urlshortener.auth.entity.Role;
import com.arthur.urlshortener.auth.entity.User;
import com.arthur.urlshortener.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        String testPassword = "securePassword123";
        when(passwordEncoder.encode(testPassword)).thenReturn(testPassword);
        String testEmail = "test@example.com";
        Email email = new Email(testEmail);
        Password password = Password.encode(testPassword, passwordEncoder);
        User user = new User(email, password, Set.of(Role.USER));

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(testEmail);

        assertNotNull(userDetails);
        assertEquals(testEmail, userDetails.getUsername());
        assertEquals(password.getHash(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().contains(
                new SimpleGrantedAuthority("ROLE_" + Role.USER.name())));
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());

        verify(userRepository, times(1)).findByEmail(any(Email.class));
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUserNotFound() {
        String nonExistentEmail = "nonexistent@example.com";
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(nonExistentEmail)
        );

        assertEquals("User not found with email: " + nonExistentEmail, exception.getMessage());
        verify(userRepository, times(1)).findByEmail(any(Email.class));
    }
}