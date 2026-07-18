package com.symplifica.dashboard.services;

import com.symplifica.dashboard.exception.EmpleadoNoEncontradoException;
import com.symplifica.dashboard.model.Empleado;
import com.symplifica.dashboard.repository.EmpleadoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpleadoServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private EmpleadoService empleadoService;

    @Test
    void deberiaListarTodosLosEmpleados() {
        when(empleadoRepository.findAll()).thenReturn(List.of(new Empleado(), new Empleado()));

        List<Empleado> resultado = empleadoService.listarTodos();

        assertThat(resultado).hasSize(2);
    }

    @Test
    void deberiaCrearUnEmpleado() {
        Empleado nuevo = new Empleado();
        nuevo.setNombre("Laura");
        when(empleadoRepository.save(nuevo)).thenReturn(nuevo);

        Empleado resultado = empleadoService.crear(nuevo);

        assertThat(resultado.getNombre()).isEqualTo("Laura");
        verify(empleadoRepository).save(nuevo);
    }

    @Test
    void deberiaLanzarExcepcionSiEmpleadoNoExiste() {
        when(empleadoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> empleadoService.buscarPorId(99L))
                .isInstanceOf(EmpleadoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deberiaActualizarLosDatosDeUnEmpleadoExistente() {
        Empleado existente = new Empleado();
        existente.setId(1L);
        existente.setNombre("Camilo");

        Empleado datosNuevos = new Empleado();
        datosNuevos.setNombre("Camilo Actualizado");
        datosNuevos.setCiudad("Cali");

        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(empleadoRepository.save(any(Empleado.class))).thenAnswer(inv -> inv.getArgument(0));

        Empleado resultado = empleadoService.actualizar(1L, datosNuevos);

        assertThat(resultado.getNombre()).isEqualTo("Camilo Actualizado");
        assertThat(resultado.getCiudad()).isEqualTo("Cali");
    }

    @Test
    void deberiaEliminarUnEmpleadoExistente() {
        Empleado existente = new Empleado();
        existente.setId(1L);
        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(existente));

        empleadoService.eliminar(1L);

        verify(empleadoRepository).delete(existente);
    }
}