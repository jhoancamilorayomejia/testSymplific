package com.symplifica.dashboard.services;

import com.symplifica.dashboard.client.NominatimClient;
import com.symplifica.dashboard.dto.BeneficiosConUbicacionResponse;
import com.symplifica.dashboard.dto.UbicacionDTO;
import com.symplifica.dashboard.exception.BeneficioNoEncontradoException;
import com.symplifica.dashboard.exception.BeneficioNoPerteneceAEmpleadoException;
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

    public Beneficio actualizar(Long idEmpleado, Long idBeneficio, Beneficio datos) {
        Beneficio beneficio = obtenerValidado(idEmpleado, idBeneficio);
        beneficio.setNombreBeneficio(datos.getNombreBeneficio());
        beneficio.setMonto(datos.getMonto());
        return beneficioRepository.save(beneficio);
    }

    public void eliminar(Long idEmpleado, Long idBeneficio) {
        Beneficio beneficio = obtenerValidado(idEmpleado, idBeneficio);
        beneficioRepository.delete(beneficio);
    }

    public BeneficiosConUbicacionResponse obtenerConUbicacion(Long idEmpleado) {
        Empleado empleado = empleadoService.buscarPorId(idEmpleado);
        List<Beneficio> beneficios = beneficioRepository.findByEmpleadoId(idEmpleado);
        UbicacionDTO ubicacion = nominatimClient.buscarUbicacionPorCiudad(empleado.getCiudad());
        return new BeneficiosConUbicacionResponse(beneficios, ubicacion);
    }

    private Beneficio obtenerValidado(Long idEmpleado, Long idBeneficio) {
    Beneficio beneficio = beneficioRepository.findById(idBeneficio)
            .orElseThrow(() -> new BeneficioNoEncontradoException(idBeneficio));
    if (!beneficio.getEmpleado().getId().equals(idEmpleado)) {
        throw new BeneficioNoPerteneceAEmpleadoException(idBeneficio, idEmpleado);
    }
    return beneficio;
}
}