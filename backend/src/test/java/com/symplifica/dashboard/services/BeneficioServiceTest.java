package com.symplifica.dashboard.services;

import com.symplifica.dashboard.exception.BeneficioNoEncontradoException;
import com.symplifica.dashboard.exception.BeneficioNoPerteneceAEmpleadoException;
import com.symplifica.dashboard.model.Beneficio;
import com.symplifica.dashboard.model.Empleado;
import com.symplifica.dashboard.repository.BeneficioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BeneficioServiceTest {

    @Mock private BeneficioRepository beneficioRepository;
    @Mock private EmpleadoService empleadoService;
    @Mock private com.symplifica.dashboard.client.NominatimClient nominatimClient;

    @InjectMocks
    private BeneficioService beneficioService;

    @Test
    void deberiaLanzarExcepcionSiBeneficioNoExiste() {
        when(beneficioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> beneficioService.eliminar(10L, 1L))
                .isInstanceOf(BeneficioNoEncontradoException.class);
    }

    @Test
    void deberiaLanzarExcepcionSiBeneficioNoPerteneceAlEmpleado() {
        Empleado otroEmpleado = new Empleado();
        otroEmpleado.setId(99L);

        Beneficio beneficio = new Beneficio();
        beneficio.setId(1L);
        beneficio.setEmpleado(otroEmpleado);

        when(beneficioRepository.findById(1L)).thenReturn(Optional.of(beneficio));

        // Intento borrar el beneficio 1 pero diciendo que es del empleado 10 (no lo es)
        assertThatThrownBy(() -> beneficioService.eliminar(10L, 1L))
                .isInstanceOf(BeneficioNoPerteneceAEmpleadoException.class);
    }
}