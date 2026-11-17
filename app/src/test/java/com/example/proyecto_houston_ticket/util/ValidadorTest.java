package com.example.proyecto_houston_ticket.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Pruebas unitarias para la clase Validador.
 * Evalúa las reglas de negocio para formato de correo y longitud de contraseña.
 */
public class ValidadorTest {

    @Test
    public void testCorreoValidoEInvalido() {
        // En JUnit unit test local de Java, Patterns.EMAIL_ADDRESS requiere mock o patrones regex estándar.
        // Verificación básica de estructura no nula y válida.
        assertTrue(Validador.esCorreoValido("usuario@houston.local"));
        assertFalse(Validador.esCorreoValido("correo_sin_arroba"));
        assertFalse(Validador.esCorreoValido(null));
    }

    @Test
    public void testPasswordValidaEInvalida() {
        // Mínimo 6 caracteres
        assertTrue(Validador.esPasswordValida("123456"));
        assertTrue(Validador.esPasswordValida("PasswordSegura"));

        assertFalse(Validador.esPasswordValida("12345"));
        assertFalse(Validador.esPasswordValida("abc"));
        assertFalse(Validador.esPasswordValida(null));
    }
}
