package com.symplifica.dashboard.exception;

public class EmailYaRegistradoException extends RuntimeException {
    public EmailYaRegistradoException(String email) {
        super("Ya existe una cuenta con el correo: " + email);
    }
}