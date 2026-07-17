package com.symplifica.dashboard.controllers;

import com.symplifica.dashboard.dto.LoginRequest;
import com.symplifica.dashboard.dto.LoginResponse;
import com.symplifica.dashboard.model.User;
import com.symplifica.dashboard.repository.UserRepository;
import com.symplifica.dashboard.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        String token = jwtUtil.generarToken(user.getEmail(), user.getRol());
        return ResponseEntity.ok(new LoginResponse(token, user.getEmail(), user.getRol()));
    }
}