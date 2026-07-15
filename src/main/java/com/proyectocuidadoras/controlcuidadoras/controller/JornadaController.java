package com.proyectocuidadoras.controlcuidadoras.controller;

import com.proyectocuidadoras.controlcuidadoras.model.Cuidadora;
import com.proyectocuidadoras.controlcuidadoras.model.Jornada;
import com.proyectocuidadoras.controlcuidadoras.service.BcvService;
import com.proyectocuidadoras.controlcuidadoras.repository.CuidadoraRepository;
import com.proyectocuidadoras.controlcuidadoras.repository.JornadaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class JornadaController {

    @Autowired
    private JornadaRepository jornadaRepository;

    @Autowired
    private CuidadoraRepository cuidadoraRepository;

    @Autowired
    private BcvService bcvService;

    // Obtiene todas las jornadas registradas para pintarlas en el calendario
    @GetMapping("/jornadas")
    public List<Map<String, Object>> obtenerTodasLasJornadas() {
        List<Jornada> jornadas = jornadaRepository.findAll();
        List<Map<String, Object>> respuesta = new ArrayList<>();

        for (Jornada j : jornadas) {
            Map<String, Object> item = new HashMap<>();
            item.put("fecha", j.getFecha().toString()); // Envía "YYYY-MM-DD"
            item.put("cuidadoraId", j.getCuidadora().getId());
            respuesta.add(item);
        }
        return respuesta;
    }

    // Guarda o actualiza una jornada cuando seleccionas una cuidadora en el calendario
    @PostMapping("/jornadas/asignar")
    public ResponseEntity<?> asignarJornada(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam("cuidadoraId") Long cuidadoraId) {

        Optional<Cuidadora> cuidadoraOpt = cuidadoraRepository.findById(cuidadoraId);
        if (cuidadoraOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Cuidadora no encontrada");
        }

        // Si ya había alguien asignado ese día, lo actualizamos; si no, creamos uno nuevo
        Optional<Jornada> jornadaExistente = jornadaRepository.findByFecha(fecha);
        Jornada jornada;
        if (jornadaExistente.isPresent()) {
            jornada = jornadaExistente.get();
            jornada.setCuidadora(cuidadoraOpt.get());
        } else {
            jornada = new Jornada(fecha, cuidadoraOpt.get());
        }

        jornadaRepository.save(jornada);
        return ResponseEntity.ok().build();
    }

    // Elimina la asignación cuando seleccionas "— Libre —"
    @PostMapping("/jornadas/eliminar")
    @Transactional
    public ResponseEntity<?> eliminarJornada(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        jornadaRepository.deleteByFecha(fecha);
        return ResponseEntity.ok().build();
    }

    // Realiza los cálculos automáticos obteniendo la tasa real de internet o usando la manual
    @GetMapping("/calcular-pago")
    public List<Map<String, Object>> calcularPagos(@RequestParam(value = "tasaBcv", required = false) Double tasaBcv) {

        // Si no se envió ninguna tasa manual desde el frontend, la buscamos automáticamente en el BCV
        if (tasaBcv == null || tasaBcv <= 0) {
            System.out.println("🔄 Buscando tasa del Euro automáticamente en la web del BCV...");
            tasaBcv = bcvService.obtenerTasaEuro();
            System.out.println("✅ Tasa obtenida con éxito: " + tasaBcv);
        }

        List<Cuidadora> cuidadoras = cuidadoraRepository.findAll();
        List<Jornada> jornadas = jornadaRepository.findAll();
        List<Map<String, Object>> resultados = new ArrayList<>();

        final double TARIFA_FIJA_USD = 30.0; // $30 por día trabajado

        for (Cuidadora c : cuidadoras) {
            long diasTrabajados = jornadas.stream()
                    .filter(j -> j.getCuidadora().getId().equals(c.getId()))
                    .count();

            double totalUsd = diasTrabajados * TARIFA_FIJA_USD;
            double totalBs = totalUsd * tasaBcv;

            Map<String, Object> calculo = new HashMap<>();
            calculo.put("nombre", c.getNombre());
            calculo.put("diasTrabajados", diasTrabajados);
            calculo.put("totalDolares", totalUsd);
            calculo.put("totalBolivares", totalBs);
            // Agregamos la tasa utilizada al resultado para mostrarla en el frontend si es necesario
            calculo.put("tasaUtilizada", tasaBcv);

            resultados.add(calculo);
        }

        return resultados;
    }
}