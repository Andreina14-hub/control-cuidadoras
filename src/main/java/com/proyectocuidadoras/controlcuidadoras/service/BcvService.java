package com.proyectocuidadoras.controlcuidadoras.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

@Service
public class BcvService {

    private static final String BCV_URL = "https://www.bcv.org.ve/";

    public Double obtenerTasaEuro() {
        try {
            // Nos conectamos a la web oficial del BCV simulando ser un navegador común
            Document doc = Jsoup.connect(BCV_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000) // 10 segundos de tolerancia de conexión
                    .get();

            // Buscamos el contenedor específico del Euro en el HTML del BCV usando select().first()
            // .select() nos devuelve una lista de elementos, y con .first() tomamos el primero.
            Element

            euroContainer = doc.select("#euro").first();

            if (euroContainer != null) {
                // Buscamos la etiqueta strong que contiene el valor numérico
                Element


                valorElement = euroContainer.select("strong").first();
                if (valorElement != null) {
                    String valorTexto = valorElement.text().trim(); // Ejemplo: "46,50320000"

                    // Reemplazamos la coma por punto para poder convertirlo a Double en Java
                    valorTexto = valorTexto.replace(",", ".");

                    return Double.parseDouble(valorTexto);
                }
            }

            throw new RuntimeException("No se encontró el formato esperado para la tasa del Euro en el HTML.");

        } catch (Exception e) {
            System.err.println("❌ Error al conectar o extraer datos del BCV: " + e.getMessage());
            // Retornamos un valor de respaldo (fallback) en caso de que la web del BCV esté caída
            return 46.50;
        }
    }
}