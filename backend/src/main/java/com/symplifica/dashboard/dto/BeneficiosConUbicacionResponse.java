package com.symplifica.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.symplifica.dashboard.model.Beneficio;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeneficiosConUbicacionResponse {
    private List<Beneficio> beneficios;
    private UbicacionDTO ubicacion;
}