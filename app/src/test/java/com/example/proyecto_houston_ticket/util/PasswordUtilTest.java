package com.example.proyecto_houston_ticket.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PasswordUtilTest {

    @Test
    public void testHashPasswordGeneraHashDiferente() {
        String pass = "Password123";
        String hash = PasswordUtil.hashPassword(pass);
        assertNotNull(hash);
        assertNotEquals(pass, hash);
    }

    @Test
    public void testCheckPasswordCorrectaEIncorrecta() {
        String pass = "Password123";
        String hash = PasswordUtil.hashPassword(pass);

        assertTrue(PasswordUtil.checkPassword(pass, hash));
        assertFalse(PasswordUtil.checkPassword("PasswordIncorrecta", hash));
    }
}
