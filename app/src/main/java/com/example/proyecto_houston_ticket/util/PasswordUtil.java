package com.example.proyecto_houston_ticket.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    public static String hashPassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("La contraseña no puede ser nula");
        }
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPassword(String password, String hashed) {
        if (password == null || hashed == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(password, hashed);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
