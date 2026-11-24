package com.example.proyecto_houston_ticket.api;

import com.example.proyecto_houston_ticket.modelo.Ticket;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Cliente de red HTTP para comunicación con el API REST Backend MySQL.
 */
public class ApiClient {

    private static final String BASE_URL = "http://10.0.2.2:8080/api/tickets"; // URL estándar emulador -> localhost

    /**
     * Envía una petición POST al servidor para guardar el ticket en MySQL.
     * Estructura JSON enviada:
     * {
     *   "titulo": "...",
     *   "descripcion": "...",
     *   "categoria": "...",
     *   "prioridad": "...",
     *   "estado": "ABIERTO",
     *   "usuario_id": 1
     * }
     */
    public static boolean enviarTicketApi(Ticket ticket) {
        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("titulo", ticket.getTitulo());
            jsonParam.put("descripcion", ticket.getDescripcion());
            jsonParam.put("categoria", ticket.getCategoria());
            jsonParam.put("prioridad", ticket.getPrioridad());
            jsonParam.put("estado", ticket.getEstado());
            jsonParam.put("usuario_id", ticket.getCreadoPor());

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonParam.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            return (code == 200 || code == 201);
        } catch (Exception e) {
            return false;
        }
    }
}
