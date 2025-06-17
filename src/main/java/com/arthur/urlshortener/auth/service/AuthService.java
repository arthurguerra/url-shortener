package com.arthur.urlshortener.auth.service;

import com.arthur.urlshortener.auth.dto.LoginRequest;
import com.arthur.urlshortener.auth.dto.LoginResponse;
import com.arthur.urlshortener.auth.dto.RegisterRequest;
import com.arthur.urlshortener.auth.dto.RegisterResponse;
import com.arthur.urlshortener.auth.entity.Email;
import com.arthur.urlshortener.auth.entity.Password;
import com.arthur.urlshortener.auth.entity.Role;
import com.arthur.urlshortener.auth.entity.User;
import com.arthur.urlshortener.auth.exception.EmailAlreadyExistsException;
import com.arthur.urlshortener.auth.repository.UserRepository;
import com.arthur.urlshortener.auth.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public RegisterResponse registerUser(RegisterRequest request) {
        Email email = new Email(request.email());

        if (userRepository.existsByEmailValueIgnoreCase(email.getValue())) {
            throw new EmailAlreadyExistsException("There is already a user registered with this email");
        }

        Password password = Password.encode(request.password(), passwordEncoder);

        User user = new User(email, password, Set.of(Role.USER));
        userRepository.save(user);

        return new RegisterResponse("User " + request.email() + " registered successfully");
    }


    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails.getUsername());
        Long expiresIn = jwtUtil.getExpirationMs();

        return new LoginResponse(token, "Bearer", expiresIn);
    }
}
