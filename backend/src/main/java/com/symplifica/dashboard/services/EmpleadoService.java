package com.symplifica.dashboard.services;

import com.symplifica.dashboard.exception.EmpleadoNoEncontradoException;
import com.symplifica.dashboard.model.Empleado;
import com.symplifica.dashboard.repository.EmpleadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;

    public List<Empleado> listarTodos() {
        return empleadoRepository.findAll();
    }

    public Empleado crear(Empleado empleado) {
        return empleadoRepository.save(empleado);
    }

    public Empleado buscarPorId(Long id) {
    return empleadoRepository.findById(id)
            .orElseThrow(() -> new EmpleadoNoEncontradoException(id));
    }

    public Empleado actualizar(Long id, Empleado datos) {
    Empleado empleado = buscarPorId(id); // reutiliza el 404 que ya tienes

    empleado.setNombre(datos.getNombre());
    empleado.setApellido(datos.getApellido());
    empleado.setEmail(datos.getEmail());
    empleado.setCargo(datos.getCargo());
    empleado.setCiudad(datos.getCiudad());
    empleado.setDireccion(datos.getDireccion());
    empleado.setFechaIngreso(datos.getFechaIngreso());

    return empleadoRepository.save(empleado);
}

public void eliminar(Long id) {
    Empleado empleado = buscarPorId(id);
    empleadoRepository.delete(empleado);
}
}