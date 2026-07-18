package com.symplifica.dashboard.controllers;

import com.symplifica.dashboard.dto.LoginRequest;
import com.symplifica.dashboard.dto.LoginResponse;
import com.symplifica.dashboard.dto.RegisterRequest;
import com.symplifica.dashboard.exception.CredencialesInvalidasException;
import com.symplifica.dashboard.model.User;
import com.symplifica.dashboard.repository.UserRepository;
import com.symplifica.dashboard.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
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
        .orElseThrow(CredencialesInvalidasException::new);

if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
    throw new CredencialesInvalidasException();
}

        String token = jwtUtil.generarToken(user.getEmail(), user.getRol());
        return ResponseEntity.ok(new LoginResponse(token, user.getEmail(), user.getRol()));
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Ya existe un usuario con ese correo");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRol("ADMIN"); // rol fijo

        User guardado = userRepository.save(user);

        String token = jwtUtil.generarToken(guardado.getEmail(), guardado.getRol());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new LoginResponse(token, guardado.getEmail(), guardado.getRol()));
    }
}