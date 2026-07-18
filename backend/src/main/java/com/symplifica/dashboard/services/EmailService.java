package com.symplifica.dashboard.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public void enviarCorreoRecuperacion(String destinatario, String token) {
        String enlace = frontendUrl + "/reset-password?token=" + token;

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject("Symplifica Dashboard — Recuperar contraseña");
        mensaje.setText(
                "Recibimos una solicitud para restablecer tu contraseña.\n\n" +
                "Haz clic en el siguiente enlace para continuar (válido por 30 minutos):\n" +
                enlace + "\n\n" +
                "Si no solicitaste esto, puedes ignorar este correo."
        );
        mailSender.send(mensaje);
    }
}