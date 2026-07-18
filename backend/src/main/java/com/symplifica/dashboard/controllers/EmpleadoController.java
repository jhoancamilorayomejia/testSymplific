package com.symplifica.dashboard.controllers;

import com.symplifica.dashboard.model.Empleado;
import com.symplifica.dashboard.model.User;
import com.symplifica.dashboard.repository.UserRepository;
import com.symplifica.dashboard.services.EmpleadoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
@RequiredArgsConstructor
public class EmpleadoController {

    private final EmpleadoService empleadoService;
    private final UserRepository userRepository;

    @GetMapping
    public List<Empleado> listar() {
        return empleadoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Empleado obtener(@PathVariable Long id) {
        return empleadoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Empleado crear(@Valid @RequestBody Empleado empleado, Authentication authentication) {
        String emailUsuarioActual = authentication.getName();
        User usuario = userRepository.findByEmail(emailUsuarioActual)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        empleado.setIdUserRegistro(usuario.getIdUser());
        return empleadoService.crear(empleado);
    }

    @PutMapping("/{id}")
    public Empleado actualizar(@PathVariable Long id, @Valid @RequestBody Empleado empleado) {
        return empleadoService.actualizar(id, empleado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        empleadoService.eliminar(id);
    }
}