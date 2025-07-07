package com.pablospring.miapi.presentation.controller;

import com.pablospring.miapi.payload.ApiResponse;
import com.pablospring.miapi.presentation.dto.auth.RegisterRequest;
import com.pablospring.miapi.presentation.dto.auth.LoginRequest;
import com.pablospring.miapi.presentation.dto.auth.LoginResponse;
import com.pablospring.miapi.presentation.dto.auth.TokenResponse;
import com.pablospring.miapi.service.AuthService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("Usuario registrado correctamente");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("No se proporcionó el refresh token"));
        }

        String refreshToken = authHeader.substring(7);

        try {
            TokenResponse tokenResponse = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(ApiResponse.success(tokenResponse));
        } catch (JwtException | UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(ex.getMessage()));
        }
    }
}
