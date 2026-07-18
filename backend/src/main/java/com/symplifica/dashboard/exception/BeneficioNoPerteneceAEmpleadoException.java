package com.symplifica.dashboard.exception;

public class BeneficioNoPerteneceAEmpleadoException extends RuntimeException {
    public BeneficioNoPerteneceAEmpleadoException(Long idBeneficio, Long idEmpleado) {
        super("El beneficio " + idBeneficio + " no pertenece al empleado " + idEmpleado);
    }
}