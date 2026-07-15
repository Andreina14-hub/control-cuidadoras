package com.proyectocuidadoras.controlcuidadoras.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cuidadoras")
public class Cuidadora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Double tarifaPorDia;

    // 1. Constructor vacío (Obligatorio para Hibernate)
    public Cuidadora() {}

    // 2. Constructor con parámetros (El que está causando el error en tu controlador)
    public Cuidadora(String nombre, Double tarifaPorDia) {
        this.nombre = nombre;
        this.tarifaPorDia = tarifaPorDia;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Double getTarifaPorDia() { return tarifaPorDia; }
    public void setTarifaPorDia(Double tarifaPorDia) { this.tarifaPorDia = tarifaPorDia; }
}