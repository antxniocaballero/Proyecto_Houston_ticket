package com.example.proyecto_houston_ticket.servicio;

import android.content.Context;

import com.example.proyecto_houston_ticket.data.UsuarioDAO;
import com.example.proyecto_houston_ticket.modelo.Rol;
import com.example.proyecto_houston_ticket.modelo.Usuario;
import com.example.proyecto_houston_ticket.util.PasswordUtil;

import java.util.HashMap;
import java.util.Map;

public class AuthService {
    private final UsuarioDAO usuarioDAO;

    private static final Map<String, Integer> intentosFallidos = new HashMap<>();
    private static final Map<String, Long> bloqueoHasta = new HashMap<>();
    private static final int MAX_INTENTOS = 5;
    private static final long TIEMPO_BLOQUEO_MS = 5 * 60 * 1000;

    public AuthService(Context context) {
        this.usuarioDAO = new UsuarioDAO(context);
    }

    public AuthService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public Usuario autenticar(String email, String password) {
        if (email == null || password == null) return null;

        String emailNorm = email.trim().toLowerCase();

        if (estaBloqueado(emailNorm)) {
            throw new IllegalStateException("Demasiados intentos fallidos. Cuenta bloqueada temporalmente por 5 minutos.");
        }

        Usuario usuario = usuarioDAO.buscarPorEmail(emailNorm);
        if (usuario == null || !PasswordUtil.checkPassword(password, usuario.getPasswordHash())) {
            registrarIntentoFallido(emailNorm);
            return null;
        }

        if (!usuario.isActivo()) {
            throw new IllegalStateException("Cuenta desactivada, contacta con un administrador");
        }

        intentosFallidos.remove(emailNorm);
        bloqueoHasta.remove(emailNorm);

        return usuario;
    }

    public boolean registrarCliente(String nombre, String email, String password, String repetirPassword, StringBuilder mensajeError) {
        if (nombre == null || !nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]{2,50}$")) {
            mensajeError.append("El nombre debe tener entre 2 y 50 caracteres y contener solo letras y espacios. ");
            return false;
        }

        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$") || email.length() > 100) {
            mensajeError.append("El email no es válido o supera los 100 caracteres. ");
            return false;
        }

        if (password == null || password.length() < 8 || !password.matches(".*[a-zA-Z].*") || !password.matches(".*\\d.*")) {
            mensajeError.append("La contraseña debe tener al menos 8 caracteres, una letra y un número. ");
            return false;
        }

        if (!password.equals(repetirPassword)) {
            mensajeError.append("Las contraseñas no coinciden. ");
            return false;
        }

        String emailNorm = email.trim().toLowerCase();
        if (usuarioDAO.buscarPorEmail(emailNorm) != null) {
            mensajeError.append("El email ya está registrado. ");
            return false;
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombre(nombre.trim());
        nuevo.setEmail(emailNorm);
        nuevo.setPasswordHash(PasswordUtil.hashPassword(password));
        nuevo.setRol(Rol.CLIENTE);
        nuevo.setActivo(true);

        return usuarioDAO.registrar(nuevo);
    }

    private boolean estaBloqueado(String email) {
        if (bloqueoHasta.containsKey(email)) {
            if (System.currentTimeMillis() < bloqueoHasta.get(email)) {
                return true;
            } else {
                bloqueoHasta.remove(email);
                intentosFallidos.remove(email);
            }
        }
        return false;
    }

    private void registrarIntentoFallido(String email) {
        int intentos = intentosFallidos.getOrDefault(email, 0) + 1;
        intentosFallidos.put(email, intentos);
        if (intentos >= MAX_INTENTOS) {
            bloqueoHasta.put(email, System.currentTimeMillis() + TIEMPO_BLOQUEO_MS);
        }
    }
}
