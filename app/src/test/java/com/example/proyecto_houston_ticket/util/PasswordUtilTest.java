package com.example.proyecto_houston_ticket.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Pruebas unitarias para la clase PasswordUtil.
 * Verifica la generación correcta de hashes con sal y su posterior comprobación.
 */
public class PasswordUtilTest {

    @Test
    public void testGenerarHashDiferenteATextoPlano() {
        String password = "Password123";
        String hash = PasswordUtil.generarHash(password);

        assertNotNull(hash);
        assertNotEquals(password, hash);
        assertTrue(hash.contains(":"));
    }

    @Test
    public void testVerificarPasswordCorrectaEIncorrecta() {
        String passwordValida = "MiClaveSecreta1";
        String hashGenerado = PasswordUtil.generarHash(passwordValida);

        // La clave correcta debe ser aceptada por la verificación del hash
        assertTrue(PasswordUtil.verificarPassword(passwordValida, hashGenerado));

        // Una clave incorrecta debe ser rechazada
        assertFalse(PasswordUtil.verificarPassword("ClaveErronea999", hashGenerado));
    }
}
