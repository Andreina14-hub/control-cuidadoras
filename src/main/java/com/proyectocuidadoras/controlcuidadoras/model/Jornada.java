package com.proyectocuidadoras.controlcuidadoras.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "jornadas")
public class Jornada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;

    @ManyToOne
    @JoinColumn(name = "cuidadora_id")
    private Cuidadora cuidadora; // <-- Este es el atributo

    // Constructor vacío
    public Jornada() {}

    // Constructor con parámetros
    public Jornada(LocalDate fecha, Cuidadora cuidadora) {
        this.fecha = fecha;
        this.cuidadora = cuidadora;
    }

    // --- GETTERS Y SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    // 🔴 ¡ESTE ES EL MÉTODO QUE FALTA O ESTÁ MAL ESCRITO! 🔴
    public Cuidadora getCuidadora() {
        return cuidadora;
    }

    public void setCuidadora(Cuidadora cuidadora) {
        this.cuidadora = cuidadora;
    }
}