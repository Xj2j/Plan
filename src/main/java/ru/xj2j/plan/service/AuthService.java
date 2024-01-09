package ru.xj2j.plan.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.xj2j.plan.dto.AuthRequest;
import ru.xj2j.plan.dto.AuthResponse;
import ru.xj2j.plan.dto.RefreshTokenRequest;
import ru.xj2j.plan.model.Role;
import ru.xj2j.plan.model.User;
import ru.xj2j.plan.repository.UserRepository;

import java.util.HashMap;

@Service
public class AuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User signup(AuthRequest input) {
        User user = new User();
        user.setEmail(input.getEmail());
        //user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(input.getPassword()));

        return userRepository.save(user);
    }

    public AuthResponse authenticate(AuthRequest input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        var user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        var jwt = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(new HashMap<>(),user);

        AuthResponse response = new AuthResponse();
        response.setToken(jwt);
        response.setRefreshToken(refreshToken);
        return response;
    }

    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String email = jwtService.extractUsername(refreshTokenRequest.getToken());
        User user = userRepository.findByEmail(email).orElseThrow();
        if (jwtService.isTokenValid(refreshTokenRequest.getToken(), user)) {
            var jwt = jwtService.generateToken(user);
            AuthResponse response = new AuthResponse();
            response.setToken(jwt);
            response.setRefreshToken(refreshTokenRequest.getToken());
            return response;
        }
        return null;
    }
}
