package com.symplifica.dashboard.controllers;

import com.symplifica.dashboard.dto.BeneficiosConUbicacionResponse;
import com.symplifica.dashboard.model.Beneficio;
import com.symplifica.dashboard.services.BeneficioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empleados/{idEmpleado}/beneficios")
@RequiredArgsConstructor
public class BeneficioController {

    private final BeneficioService beneficioService;

    @GetMapping
    public BeneficiosConUbicacionResponse listar(@PathVariable Long idEmpleado) {
    return beneficioService.obtenerConUbicacion(idEmpleado);
}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Beneficio crear(@PathVariable Long idEmpleado, @Valid @RequestBody Beneficio beneficio) {
        return beneficioService.crear(idEmpleado, beneficio);
    }

   
}