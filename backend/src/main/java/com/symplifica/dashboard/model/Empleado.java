package com.symplifica.dashboard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "empleados")
@Data
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String apellido;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(length = 100)
    private String cargo;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String ciudad;

    @Column(length = 255)
    private String direccion;

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "id_user_registro")
    private Long idUserRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_registro", insertable = false, updatable = false)
    private User usuarioRegistro;

    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Beneficio> beneficios;
}