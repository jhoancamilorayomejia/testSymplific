package com.symplifica.dashboard.services;

import com.symplifica.dashboard.dto.LoginRequest;
import com.symplifica.dashboard.dto.LoginResponse;
import com.symplifica.dashboard.dto.RegisterRequest;
import com.symplifica.dashboard.exception.CredencialesInvalidasException;
import com.symplifica.dashboard.exception.EmailYaRegistradoException;
import com.symplifica.dashboard.exception.TokenInvalidoException;
import com.symplifica.dashboard.model.User;
import com.symplifica.dashboard.repository.UserRepository;
import com.symplifica.dashboard.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    private static final long TOKEN_VALIDEZ_MINUTOS = 30;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(CredencialesInvalidasException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CredencialesInvalidasException();
        }

        String token = jwtUtil.generarToken(user.getEmail(), user.getRol());
        return new LoginResponse(token, user.getIdUser(), user.getEmail(), user.getRol());
    }

    public LoginResponse registrar(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailYaRegistradoException(request.getEmail());
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRol("ADMIN");

        User usuarioGuardado = userRepository.save(user);

        String token = jwtUtil.generarToken(usuarioGuardado.getEmail(), usuarioGuardado.getRol());
        return new LoginResponse(token, usuarioGuardado.getIdUser(), usuarioGuardado.getEmail(), usuarioGuardado.getRol());
    }

    public void solicitarRecuperacion(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(TOKEN_VALIDEZ_MINUTOS));
            userRepository.save(user);
            emailService.enviarCorreoRecuperacion(user.getEmail(), token);
        });
        // Nota: no lanzamos error si el email no existe, para no revelar
        // qué correos están registrados (evita enumeración de usuarios).
    }

    public void restablecerContrasena(String token, String nuevaContrasena) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(TokenInvalidoException::new);

        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new TokenInvalidoException();
        }

        user.setPassword(passwordEncoder.encode(nuevaContrasena));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }
}