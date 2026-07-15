package com.proyectocuidadoras.controlcuidadoras.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoCuidadora {
    private String nombre;
    private int diasTrabajados;
    private double totalDolares;
    private double totalBolivares;
}