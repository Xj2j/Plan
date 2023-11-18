package ru.xj2j.plan.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.xj2j.plan.dto.AuthRequest;
import ru.xj2j.plan.dto.AuthResponse;
import ru.xj2j.plan.dto.RefreshTokenRequest;
import ru.xj2j.plan.model.User;
import ru.xj2j.plan.service.AuthService;
import ru.xj2j.plan.service.JwtService;

import javax.validation.Valid;

@RequestMapping("/api/v1/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthService authService;

    public AuthenticationController(JwtService jwtService, AuthService authService) {
        this.jwtService = jwtService;
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@Valid @RequestBody AuthRequest registerUserDto) {
        User registeredUser = authService.signup(registerUserDto);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody AuthRequest authRequest) {
        AuthResponse authResponse = authService.authenticate(authRequest);

        //String jwtToken = jwtService.generateToken(authenticatedUser);

        //AuthResponse authResponse = new AuthResponse();
        //authResponse.setToken(jwtToken);
        //authResponse.setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        AuthResponse authResponse = authService.refreshToken(refreshTokenRequest);

        return ResponseEntity.ok(authResponse);
    }
}
