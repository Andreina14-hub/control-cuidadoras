package com.proyectocuidadoras.controlcuidadoras.controller;

import com.proyectocuidadoras.controlcuidadoras.model.Cuidadora;
import com.proyectocuidadoras.controlcuidadoras.repository.CuidadoraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api") // <-- Esto asegura que responda a "/api/cuidadoras"
public class CuidadoraController {

    @Autowired
    private CuidadoraRepository cuidadoraRepository;

    // Este método se ejecuta al iniciar la aplicación.
    // Si la base de datos está vacía, registra a tus cuidadoras automáticamente.
    @PostConstruct
    public void inicializarCuidadoras() {
        if (cuidadoraRepository.count() == 0) {
            List<Cuidadora> iniciales = Arrays.asList(
                    new Cuidadora( "Sandra", 30.0),
                    new Cuidadora("Jacqueline", 30.0),
                    new Cuidadora("Rosa", 30.0)

            );
            cuidadoraRepository.saveAll(iniciales);
            System.out.println("👉 Base de datos inicializada con las 6 cuidadoras por defecto.");
        }
    }

    // Devuelve la lista de cuidadoras en formato JSON al Javascript
    @GetMapping("/cuidadoras")
    public List<Cuidadora> obtenerTodas() {
        return cuidadoraRepository.findAll();
    }
}
