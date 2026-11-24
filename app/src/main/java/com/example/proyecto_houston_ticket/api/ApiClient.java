package com.example.proyecto_houston_ticket.api;

import com.example.proyecto_houston_ticket.modelo.Comentario;
import com.example.proyecto_houston_ticket.modelo.Ticket;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Cliente de red HTTP para la comunicación con el API REST Backend MySQL.
 */
public class ApiClient {

    private static final String BASE_URL = "http://10.0.2.2:8080/api/tickets";

    /**
     * Petición POST para guardar un nuevo ticket en MySQL.
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

    /**
     * Petición PUT para actualizar los campos modificables de un ticket en MySQL.
     */
    public static boolean actualizarTicketApi(long ticketId, String titulo, String descripcion, String categoria, String prioridad) {
        try {
            URL url = new URL(BASE_URL + "/" + ticketId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("titulo", titulo);
            jsonParam.put("descripcion", descripcion);
            jsonParam.put("categoria", categoria);
            jsonParam.put("prioridad", prioridad);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonParam.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            return (code == 200 || code == 204);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Petición PUT para actualizar el estado de un ticket en MySQL.
     */
    public static boolean actualizarEstadoApi(long ticketId, String nuevoEstado) {
        try {
            URL url = new URL(BASE_URL + "/" + ticketId + "/estado");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("estado", nuevoEstado);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonParam.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            return (code == 200 || code == 204);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Petición PUT para asignar un técnico a un ticket en MySQL.
     */
    public static boolean asignarTecnicoApi(long ticketId, long tecnicoId) {
        try {
            URL url = new URL(BASE_URL + "/" + ticketId + "/tecnico");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("id_tecnico", tecnicoId);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonParam.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            return (code == 200 || code == 204);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Petición DELETE para eliminar un ticket resuelto/cerrado en MySQL.
     */
    public static boolean eliminarTicketApi(long ticketId) {
        try {
            URL url = new URL(BASE_URL + "/" + ticketId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setConnectTimeout(5000);

            int code = conn.getResponseCode();
            return (code == 200 || code == 204);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Petición POST para enviar un nuevo comentario a un ticket en MySQL.
     */
    public static boolean enviarComentarioApi(Comentario comentario) {
        try {
            URL url = new URL("http://10.0.2.2:8080/api/comentarios");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("id_ticket", comentario.getTicketId());
            jsonParam.put("id_usuario", comentario.getUsuarioId());
            jsonParam.put("autor_nombre", comentario.getAutorNombre());
            jsonParam.put("rol_autor", comentario.getRolAutor());
            jsonParam.put("mensaje", comentario.getMensaje());

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
