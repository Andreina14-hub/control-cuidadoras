package com.proyectocuidadoras.controlcuidadoras.repository;

import com.proyectocuidadoras.controlcuidadoras.model.Cuidadora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuidadoraRepository extends JpaRepository<Cuidadora, Long> {
}