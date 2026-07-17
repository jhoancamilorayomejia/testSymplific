package com.symplifica.dashboard.services;

import com.symplifica.dashboard.client.NominatimClient;
import com.symplifica.dashboard.dto.BeneficiosConUbicacionResponse;
import com.symplifica.dashboard.dto.UbicacionDTO;
import com.symplifica.dashboard.model.Beneficio;
import com.symplifica.dashboard.model.Empleado;
import com.symplifica.dashboard.repository.BeneficioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BeneficioService {

    private final BeneficioRepository beneficioRepository;
    private final EmpleadoService empleadoService;
    private final NominatimClient nominatimClient;

    public List<Beneficio> listarPorEmpleado(Long idEmpleado) {
        return beneficioRepository.findByEmpleadoId(idEmpleado);
    }

    public Beneficio crear(Long idEmpleado, Beneficio beneficio) {
        Empleado empleado = empleadoService.buscarPorId(idEmpleado);
        beneficio.setEmpleado(empleado);
        return beneficioRepository.save(beneficio);
    }

    public BeneficiosConUbicacionResponse obtenerConUbicacion(Long idEmpleado) {
        Empleado empleado = empleadoService.buscarPorId(idEmpleado);
        List<Beneficio> beneficios = beneficioRepository.findByEmpleadoId(idEmpleado);
        UbicacionDTO ubicacion = nominatimClient.buscarUbicacionPorCiudad(empleado.getCiudad());
        return new BeneficiosConUbicacionResponse(beneficios, ubicacion);
    }
}