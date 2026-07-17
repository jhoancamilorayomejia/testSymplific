package com.symplifica.dashboard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "beneficios")
@Data
public class Beneficio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado", nullable = false)
    @JsonIgnore
    private Empleado empleado;

    @NotBlank
    @Column(name = "nombre_beneficio", nullable = false, length = 150)
    private String nombreBeneficio;

    @NotNull
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;
}