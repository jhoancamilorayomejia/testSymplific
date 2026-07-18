package com.symplifica.dashboard.controllers;

import com.symplifica.dashboard.dto.ForgotPasswordRequest;
import com.symplifica.dashboard.dto.LoginRequest;
import com.symplifica.dashboard.dto.LoginResponse;
import com.symplifica.dashboard.dto.RegisterRequest;
import com.symplifica.dashboard.dto.ResetPasswordRequest;
import com.symplifica.dashboard.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.solicitarRecuperacion(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "Si el correo existe, se envió un enlace de recuperación"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.restablecerContrasena(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente"));
    }

    @PostMapping("/register")
@ResponseStatus(HttpStatus.CREATED)
public LoginResponse register(@Valid @RequestBody RegisterRequest request) {
    return authService.registrar(request);
}
}