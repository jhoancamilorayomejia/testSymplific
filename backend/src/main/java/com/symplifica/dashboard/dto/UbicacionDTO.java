package com.symplifica.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UbicacionDTO {
    private String latitud;
    private String longitud;
    private String nombreCompleto;
}