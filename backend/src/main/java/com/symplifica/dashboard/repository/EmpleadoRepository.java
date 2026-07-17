package com.symplifica.dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.symplifica.dashboard.model.Empleado;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
}