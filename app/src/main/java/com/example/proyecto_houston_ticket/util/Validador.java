package com.example.proyecto_houston_ticket.util;

import java.util.regex.Pattern;

/**
 * Clase de utilidad para validar campos de entrada en los formularios de registro y login.
 */
public class Validador {

    // Patrón estándar RFC 5322 simplificado para validación portable de correo electrónico
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    /**
     * Valida si un correo electrónico tiene un formato correcto.
     *
     * @param correo Cadena con el correo a validar.
     * @return true si el correo no es nulo y cumple el patrón estándar de email.
     */
    public static boolean esCorreoValido(String correo) {
        if (correo == null) {
            return false;
        }
        String correoLimpio = correo.trim();
        return !correoLimpio.isEmpty() && EMAIL_PATTERN.matcher(correoLimpio).matches();
    }

    /**
     * Valida si una contraseña cumple la longitud mínima de 6 caracteres.
     *
     * @param password Cadena con la contraseña a validar.
     * @return true si la contraseña tiene al menos 6 caracteres.
     */
    public static boolean esPasswordValida(String password) {
        return password != null && password.trim().length() >= 6;
    }
}
