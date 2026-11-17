package com.example.proyecto_houston_ticket.servicio;

import com.example.proyecto_houston_ticket.data.UsuarioDAO;
import com.example.proyecto_houston_ticket.modelo.Usuario;
import com.example.proyecto_houston_ticket.util.PasswordUtil;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Pruebas unitarias para la clase AuthService utilizando un Stub de UsuarioDAO.
 */
public class AuthServiceTest {

    private FakeUsuarioDAO fakeDAO;
    private AuthService authService;

    // Stub de UsuarioDAO sin dependencias de base de datos ni Android
    private static class FakeUsuarioDAO extends UsuarioDAO {
        private final Map<String, Usuario> usuariosDB = new HashMap<>();

        public FakeUsuarioDAO() {
            super(null);
        }

        @Override
        public Usuario buscarPorCorreo(String correo) {
            return usuariosDB.get(correo);
        }

        @Override
        public boolean existeCorreo(String correo) {
            return usuariosDB.containsKey(correo);
        }

        @Override
        public long insertar(Usuario usuario) {
            usuariosDB.put(usuario.getCorreo(), usuario);
            return 1L;
        }
    }

    @Before
    public void setUp() {
        fakeDAO = new FakeUsuarioDAO();
        authService = new AuthService(fakeDAO);
    }

    @Test
    public void testRegistroDatosValidosCreaClienteActivo() {
        StringBuilder errores = new StringBuilder();
        boolean resultado = authService.registrarCliente("Juan Pérez", "nuevo@houston.local", "Admin1234", "Admin1234", errores);

        assertTrue(resultado);
        assertEquals("", errores.toString());
        assertNotNull(fakeDAO.buscarPorCorreo("nuevo@houston.local"));
    }

    @Test
    public void testRegistroEmailRepetidoFalla() {
        Usuario existente = new Usuario();
        existente.setCorreo("existente@houston.local");
        fakeDAO.insertar(existente);

        StringBuilder errores = new StringBuilder();
        boolean resultado = authService.registrarCliente("Juan Pérez", "existente@houston.local", "Admin1234", "Admin1234", errores);

        assertFalse(resultado);
        assertTrue(errores.toString().contains("correo electrónico ya está registrado"));
    }

    @Test
    public void testRegistroPasswordCortaFalla() {
        StringBuilder errores = new StringBuilder();
        boolean resultado = authService.registrarCliente("Juan Pérez", "test@houston.local", "Ab1", "Ab1", errores);

        assertFalse(resultado);
        assertTrue(errores.toString().contains("al menos 6 caracteres"));
    }

    @Test
    public void testLoginPasswordIncorrectaFalla() {
        Usuario u = new Usuario();
        u.setCorreo("user@houston.local");
        u.setPasswordHash(PasswordUtil.generarHash("Correcta123"));
        u.setActivo(true);
        fakeDAO.insertar(u);

        try {
            authService.login("user@houston.local", "Incorrecta123");
            assertTrue("Debería haber lanzado IllegalArgumentException", false);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("incorrectos"));
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testLoginUsuarioInactivoFalla() {
        Usuario u = new Usuario();
        u.setCorreo("inactivo@houston.local");
        u.setPasswordHash(PasswordUtil.generarHash("Admin1234"));
        u.setActivo(false);
        fakeDAO.insertar(u);

        authService.login("inactivo@houston.local", "Admin1234");
    }
}
