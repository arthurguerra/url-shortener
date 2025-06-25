package com.arthur.urlshortener.auth.service;

import com.arthur.urlshortener.auth.dto.LoginRequest;
import com.arthur.urlshortener.auth.dto.LoginResponse;
import com.arthur.urlshortener.auth.dto.RegisterRequest;
import com.arthur.urlshortener.auth.dto.RegisterResponse;
import com.arthur.urlshortener.auth.entity.Role;
import com.arthur.urlshortener.auth.entity.User;
import com.arthur.urlshortener.auth.exception.EmailAlreadyExistsException;
import com.arthur.urlshortener.auth.exception.InvalidEmailException;
import com.arthur.urlshortener.auth.exception.WeakPasswordException;
import com.arthur.urlshortener.auth.repository.UserRepository;
import com.arthur.urlshortener.auth.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private AuthService authService;

    private final String validEmail = "valid@email.com";
    private final String validPassword = "password123";

    @Test
    @DisplayName("Should register user successfully")
    void registerUser_shouldRegisterSuccessfully() {
        RegisterRequest request = new RegisterRequest(validEmail, validPassword);
        when(userRepository.existsByEmailValueIgnoreCase(validEmail)).thenReturn(false);
        String encodedPassword = "encodedPassword123";
        when(passwordEncoder.encode(validPassword)).thenReturn(encodedPassword);

        RegisterResponse response = authService.registerUser(request);

        assertNotNull(response);
        assertEquals("User " + validEmail + " registered successfully", response.message());

        verify(userRepository).existsByEmailValueIgnoreCase(validEmail);
        verify(passwordEncoder).encode(validPassword);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(validEmail, savedUser.getEmail().getValue());
        assertEquals(encodedPassword, savedUser.getPassword().getHash());
        assertEquals(Set.of(Role.USER), savedUser.getRoles());

        verifyNoMoreInteractions(userRepository, passwordEncoder);
    }

    @ParameterizedTest(name = "[{index} - {0}]")
    @MethodSource("invalidEmails")
    @DisplayName("Should throw exception if email is invalid")
    void registerUser_invalidEmail_shouldThrowException(String email) {
        RegisterRequest request = new RegisterRequest(email, validPassword);
        InvalidEmailException exception = assertThrows(InvalidEmailException.class,
                () -> authService.registerUser(request));

        assertEquals("Invalid email: " + email, exception.getMessage());
        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Should throw exception if email already exists")
    void registerUser_emailAlreadyExists_shouldThrowException() {
        RegisterRequest request = new RegisterRequest(validEmail, validPassword);
        when(userRepository.existsByEmailValueIgnoreCase(validEmail)).thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class,
                () -> authService.registerUser(request));

        assertEquals("There is already a user registered with this email", exception.getMessage());
        verify(userRepository, times(1)).existsByEmailValueIgnoreCase(validEmail);
        verifyNoMoreInteractions(userRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Should throw exception if password is weak")
    void registerUser_weakPassword_shouldThrowException() {
        RegisterRequest request = new RegisterRequest(validEmail, "abc123");
        when(userRepository.existsByEmailValueIgnoreCase(validEmail)).thenReturn(false);

        WeakPasswordException exception = assertThrows(WeakPasswordException.class,
                () -> authService.registerUser(request));

        assertEquals("Password must contain at least 8 characters.", exception.getMessage());
        verify(userRepository, times(1)).existsByEmailValueIgnoreCase(validEmail);
        verifyNoMoreInteractions(userRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Should return token when credentials are valid")
    void login_shouldReturnToken_whenCredentialsAreValid() {
        LoginRequest loginRequest = new LoginRequest(validEmail, validPassword);
        Authentication authentication = mock(UsernamePasswordAuthenticationToken.class);
        UserDetails userDetails = mock(UserDetails.class);

        doReturn(authentication).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(validEmail);
        String token = "test.jwt.token";
        when(jwtUtil.generateToken(validEmail)).thenReturn(token);
        long tokenExpirationMs = 3600000L;
        when(jwtUtil.getExpirationMs()).thenReturn(tokenExpirationMs);

        LoginResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals(token, response.token());
        assertEquals("Bearer", response.tokenType());
        assertEquals(tokenExpirationMs, response.expiresIn());

        ArgumentCaptor<UsernamePasswordAuthenticationToken> authTokenCaptor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(authTokenCaptor.capture());

        UsernamePasswordAuthenticationToken capturedToken = authTokenCaptor.getValue();
        assertEquals(validEmail, capturedToken.getPrincipal());
        assertEquals(validPassword, capturedToken.getCredentials());

        verify(jwtUtil).generateToken(validEmail);
        verify(jwtUtil).getExpirationMs();
        verifyNoMoreInteractions(authenticationManager, jwtUtil);
    }

    @Test
    void login_shouldThrowException_whenCredentialsAreInvalid() {
        LoginRequest loginRequest = new LoginRequest(validEmail, "wrongPassword");

        doThrow(new BadCredentialsException("Invalid credentials"))
                .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authService.login(loginRequest)
        );

        assertEquals("Invalid credentials", exception.getMessage());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtUtil);
    }

    private static Stream<String> invalidEmails() {
        return Stream.of(
                "",
                " ",
                "   ",
                "invalid",
                "@",
                "@domain.com",
                "user@",
                "user@@domain.com",
                "user@domain@com",
                "user@domain.com@",
                ".user@domain.com",
                "user.@domain.com",
                "user..name@domain.com",
                "user...name@domain.com",
                "user@domain",
                "user@domain.",
                "user@.domain.com",
                "user@domain..com",
                "user@domain.com.",
                "user@domain.c",
                "user@domain.1",
                "user@domain.co.1",
                "user@-domain.com",
                "user@domain-.com",
                "user@sub.-domain.com",
                "user@sub.domain-.com",
                "user@domain.com!",
                "user@domain!.com",
                "user!@domain.com",
                "user@dom ain.com",
                "us er@domain.com",
                "user@domain .com",
                "user@domain. com",
                "user#@domain.com",
                "user$@domain.com",
                "user%@domain.com",
                "user&@domain.com",
                "user*@domain.com",
                "user/@domain.com",
                "user=@domain.com",
                "user?@domain.com",
                "user^@domain.com",
                "user`@domain.com",
                "user{@domain.com",
                "user|@domain.com",
                "user}@domain.com",
                "user~@domain.com",
                "a".repeat(65) + "@domain.com",
                "user@" + "a".repeat(250) + ".com",
                "a".repeat(320) + "@domain.com",
                "user@",
                "@domain.com",
                "user@@domain.com",
                "user@domain,com",
                "user@domain;com",
                "user@domain:com",
                "user@domain com",
                "user@domain/com",
                "user@[domain.com]",
                "user@(domain.com)",
                "(user)@domain.com",
                "[user]@domain.com",
                "\"user\"@domain.com",
                "user'@domain.com",
                "usuário@domain.com",
                "user@domínio.com",
                "user@domain.côm",
                "user@domain.123",
                "user@domain.a1",
                "user@domain.",
                "user@domain.co.",
                ".user.@domain..com",
                "user..name@-domain-.com",
                "@.domain.com.",
                "user@domain.com@extra",

                null
        );
    }
}