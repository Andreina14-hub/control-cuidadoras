package com.proyectocuidadoras.controlcuidadoras.repository;

import com.proyectocuidadoras.controlcuidadoras.model.Jornada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface JornadaRepository extends JpaRepository<Jornada, Long> {

    Optional<Jornada> findByFecha(LocalDate fecha);

    // 🔴 ¡ESTA ES LA LÍNEA QUE FALTA EN TU ARCHIVO! 🔴
    void deleteByFecha(LocalDate fecha);
}
