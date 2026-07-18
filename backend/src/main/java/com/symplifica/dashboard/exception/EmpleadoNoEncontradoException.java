package com.symplifica.dashboard.exception;

public class EmpleadoNoEncontradoException extends RuntimeException {
    public EmpleadoNoEncontradoException(Long id) {
        super("Empleado no encontrado: " + id);
    }
}