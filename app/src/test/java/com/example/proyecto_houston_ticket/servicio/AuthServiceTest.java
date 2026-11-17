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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class AuthServiceTest {

    private FakeUsuarioDAO fakeDAO;
    private AuthService authService;

    // Stub de UsuarioDAO sin necesidad de Mockito ni ByteBuddy
    private static class FakeUsuarioDAO extends UsuarioDAO {
        private final Map<String, Usuario> usuariosDB = new HashMap<>();

        public FakeUsuarioDAO() {
            super(null);
        }

        @Override
        public Usuario buscarPorEmail(String email) {
            return usuariosDB.get(email);
        }

        @Override
        public boolean registrar(Usuario usuario) {
            usuariosDB.put(usuario.getEmail(), usuario);
            return true;
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
        assertNotNull(fakeDAO.buscarPorEmail("nuevo@houston.local"));
    }

    @Test
    public void testRegistroEmailRepetidoFalla() {
        Usuario existente = new Usuario();
        existente.setEmail("existente@houston.local");
        fakeDAO.registrar(existente);

        StringBuilder errores = new StringBuilder();
        boolean resultado = authService.registrarCliente("Juan Pérez", "existente@houston.local", "Admin1234", "Admin1234", errores);

        assertFalse(resultado);
        assertTrue(errores.toString().contains("email ya está registrado"));
    }

    @Test
    public void testRegistroPasswordCortaFalla() {
        StringBuilder errores = new StringBuilder();
        boolean resultado = authService.registrarCliente("Juan Pérez", "test@houston.local", "Ab1", "Ab1", errores);

        assertFalse(resultado);
        assertTrue(errores.toString().contains("contraseña debe tener al menos 8 caracteres"));
    }

    @Test
    public void testRegistroPasswordSinNumeroFalla() {
        StringBuilder errores = new StringBuilder();
        boolean resultado = authService.registrarCliente("Juan Pérez", "test@houston.local", "SoloLetras", "SoloLetras", errores);

        assertFalse(resultado);
        assertTrue(errores.toString().contains("una letra y un número"));
    }

    @Test
    public void testLoginPasswordIncorrectaFalla() {
        Usuario u = new Usuario();
        u.setEmail("user@houston.local");
        u.setPasswordHash(PasswordUtil.hashPassword("Correcta123"));
        u.setActivo(true);
        fakeDAO.registrar(u);

        Usuario resultado = authService.autenticar("user@houston.local", "Incorrecta123");
        assertNull(resultado);
    }

    @Test(expected = IllegalStateException.class)
    public void testLoginUsuarioInactivoFalla() {
        Usuario u = new Usuario();
        u.setEmail("inactivo@houston.local");
        u.setPasswordHash(PasswordUtil.hashPassword("Admin1234"));
        u.setActivo(false);
        fakeDAO.registrar(u);

        authService.autenticar("inactivo@houston.local", "Admin1234");
    }
}
