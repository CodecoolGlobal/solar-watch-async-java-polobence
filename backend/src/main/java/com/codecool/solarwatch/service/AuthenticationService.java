package com.codecool.solarwatch.service;

import com.codecool.solarwatch.model.auth.AuthRequest;
import com.codecool.solarwatch.model.auth.AuthResponse;
import com.codecool.solarwatch.model.user.UserEntity;
import com.codecool.solarwatch.repository.UserRepository;
import com.codecool.solarwatch.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationService(UserRepository userRepository,
                               AuthenticationManager authenticationManager,
                               JwtUtil jwtUtil,
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse registerUser(AuthRequest authRequest) {
        if (userRepository.existsByUsername(authRequest.getUsername())) {
            return new AuthResponse(null, null, "Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(authRequest.getEmail())) {
            return new AuthResponse(null, null, "Error: Email is already in use!");
        }

        UserEntity user = new UserEntity(
                authRequest.getUsername(),
                passwordEncoder.encode(authRequest.getPassword()),
                authRequest.getEmail()
        );

        userRepository.save(user);

        return new AuthResponse(
                generateToken(authRequest.getUsername()),
                user.getUsername(),
                "User registered successfully!"
        );
    }

    public AuthResponse authenticateUser(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return new AuthResponse(
                generateToken(authRequest.getUsername()),
                authRequest.getUsername(),
                "Login successful!"
        );
    }

    private String generateToken(String username) {
        return jwtUtil.generateToken(
                (org.springframework.security.core.userdetails.UserDetails) userRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found with username: " + username))
        );
    }
}
