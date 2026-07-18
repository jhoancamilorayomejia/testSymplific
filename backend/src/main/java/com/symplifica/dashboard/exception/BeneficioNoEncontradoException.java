package com.symplifica.dashboard.exception;

public class BeneficioNoEncontradoException extends RuntimeException {
    public BeneficioNoEncontradoException(Long id) {
        super("Beneficio no encontrado: " + id);
    }
}