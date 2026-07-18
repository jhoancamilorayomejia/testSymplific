package com.symplifica.dashboard.exception;

public class TokenInvalidoException extends RuntimeException {
    public TokenInvalidoException() {
        super("El enlace de recuperación no es válido o ha expirado");
    }
}