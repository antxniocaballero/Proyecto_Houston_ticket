package com.example.proyecto_houston_ticket.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Clase de utilidad para el cifrado y verificación de contraseñas mediante SHA-256 con sal aleatoria (salt).
 * Garantiza que las contraseñas nunca se almacenen ni comparen en texto plano.
 *
 * Estructura del valor retornado por generarHash: [SAL_BASE64]:[HASH_BASE64]
 */
public class PasswordUtil {

    // Tamaño de la sal aleatoria en bytes (16 bytes = 128 bits de entropía)
    private static final int SALT_BYTES = 16;
    private static final String SEPARADOR = ":";

    /**
     * Genera una sal aleatoria única y calcula el hash SHA-256 de la contraseña combinada con dicha sal.
     *
     * @param password Contraseña en texto plano a cifrar.
     * @return Cadena formateada como "SAL_BASE64:HASH_BASE64" para almacenar en la BD.
     */
    public static String generarHash(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }

        try {
            // 1. Generar una sal aleatoria criptográficamente segura
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_BYTES];
            random.nextBytes(salt);

            // 2. Calcular digest SHA-256 combinando la sal y la contraseña
            byte[] hash = calcularSHA256(password, salt);

            // 3. Codificar sal y hash en Base64 para almacenar como texto
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hash);

            return saltBase64 + SEPARADOR + hashBase64;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al inicializar el algoritmo SHA-256 para el hash", e);
        }
    }

    /**
     * Comprueba si una contraseña introducida en texto plano coincide con un hash almacenado previamente.
     *
     * @param passwordIngresada Contraseña en texto plano ingresada por el usuario.
     * @param hashAlmacenado Cadena cifrada "SAL_BASE64:HASH_BASE64" guardada en la base de datos.
     * @return true si la contraseña coincide con el hash, false de lo contrario.
     */
    public static boolean verificarPassword(String passwordIngresada, String hashAlmacenado) {
        if (passwordIngresada == null || hashAlmacenado == null || !hashAlmacenado.contains(SEPARADOR)) {
            return false;
        }

        try {
            // 1. Separar la sal y el hash almacenados
            String[] partes = hashAlmacenado.split(SEPARADOR);
            if (partes.length != 2) {
                return false;
            }

            byte[] salt = Base64.getDecoder().decode(partes[0]);
            String hashOriginalBase64 = partes[1];

            // 2. Calcular el hash de la contraseña ingresada utilizando la misma sal
            byte[] hashNuevo = calcularSHA256(passwordIngresada, salt);
            String hashNuevoBase64 = Base64.getEncoder().encodeToString(hashNuevo);

            // 3. Comparar ambos hashes
            return hashOriginalBase64.equals(hashNuevoBase64);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Método auxiliar interno para calcular el digest de SHA-256 dados una contraseña y una sal.
     */
    private static byte[] calcularSHA256(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();
        digest.update(salt);
        return digest.digest(password.getBytes(StandardCharsets.UTF_8));
    }
}
