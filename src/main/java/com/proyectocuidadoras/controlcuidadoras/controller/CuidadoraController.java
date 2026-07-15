package com.proyectocuidadoras.controlcuidadoras.controller;

import com.proyectocuidadoras.controlcuidadoras.model.Cuidadora;
import com.proyectocuidadoras.controlcuidadoras.repository.CuidadoraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CuidadoraController {

    @Autowired
    private CuidadoraRepository cuidadoraRepository;

    @PostConstruct
    public void inicializarCuidadoras() {
        if (cuidadoraRepository.count() == 0) {
            List<Cuidadora> iniciales = Arrays.asList(
                    new Cuidadora("Sandra", 30.0),
                    new Cuidadora("Jacqueline", 30.0),
                    new Cuidadora("Rosa", 30.0)
            );
            cuidadoraRepository.saveAll(iniciales);
            System.out.println("👉 Base de datos inicializada con las cuidadoras por defecto.");
        }
    }

    // 1. Obtener todas las cuidadoras
    @GetMapping("/cuidadoras")
    public List<Cuidadora> obtenerTodas() {
        return cuidadoraRepository.findAll();
    }

    // 2. Agregar una nueva cuidadora
    @PostMapping("/cuidadoras")
    public Cuidadora guardarCuidadora(@RequestBody Cuidadora nuevaCuidadora) {
        // Ajustado para usar setTarifaPorDia y getTarifaPorDia
        if (nuevaCuidadora.getTarifaPorDia() == null || nuevaCuidadora.getTarifaPorDia() == 0) {
            nuevaCuidadora.setTarifaPorDia(30.0);
        }
        return cuidadoraRepository.save(nuevaCuidadora);
    }

    // 3. Eliminar una cuidadora por su ID
    @DeleteMapping("/cuidadoras/{id}")
    public ResponseEntity<Void> eliminarCuidadora(@PathVariable Long id) {
        if (cuidadoraRepository.existsById(id)) {
            cuidadoraRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 4. Actualizar la tarifa de todas las cuidadoras (tarifa global)
    @PutMapping("/config/tarifa")
    public ResponseEntity<Void> actualizarTarifaGlobal(@RequestBody TarifaDto tarifaDto) {
        List<Cuidadora> cuidadoras = cuidadoraRepository.findAll();
        for (Cuidadora c : cuidadoras) {
            c.setTarifaPorDia(tarifaDto.getValor()); // Ajustado aquí
        }
        cuidadoraRepository.saveAll(cuidadoras);
        return ResponseEntity.ok().build();
    }

    public static class TarifaDto {
        private Double valor;
        public Double getValor() { return valor; }
        public void setValor(Double valor) { this.valor = valor; }
    }
}