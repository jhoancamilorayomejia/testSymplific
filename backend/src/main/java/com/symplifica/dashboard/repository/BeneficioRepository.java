package com.symplifica.dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.symplifica.dashboard.model.Beneficio;

import java.util.List;

public interface BeneficioRepository extends JpaRepository<Beneficio, Long> {
    List<Beneficio> findByEmpleadoId(Long idEmpleado);
}