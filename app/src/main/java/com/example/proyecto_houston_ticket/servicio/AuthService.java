package com.example.proyecto_houston_ticket.servicio;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.proyecto_houston_ticket.data.UsuarioDAO;
import com.example.proyecto_houston_ticket.modelo.Rol;
import com.example.proyecto_houston_ticket.modelo.Usuario;
import com.example.proyecto_houston_ticket.ui.AdminActivity;
import com.example.proyecto_houston_ticket.ui.ClienteActivity;
import com.example.proyecto_houston_ticket.ui.LoginActivity;
import com.example.proyecto_houston_ticket.ui.TecnicoActivity;
import com.example.proyecto_houston_ticket.util.PasswordUtil;
import com.example.proyecto_houston_ticket.util.Validador;

/**
 * Servicio encargado de gestionar la lógica de autenticación, registros, control de sesiones
 * en SharedPreferences y la redirección de pantallas según el rol de cada usuario.
 */
public class AuthService {

    private static final String PREF_NAME = "HoustonSession";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_ROLE = "user_role";

    private final Context context;
    private final UsuarioDAO usuarioDAO;

    public AuthService(Context context) {
        this.context = context;
        this.usuarioDAO = new UsuarioDAO(context);
    }

    public AuthService(UsuarioDAO usuarioDAO) {
        this.context = null;
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * Realiza el registro de un nuevo usuario en la aplicación con rol CLIENTE por defecto.
     *
     * @param nombre Nombre completo.
     * @param correo Correo electrónico.
     * @param password Contraseña elegida.
     * @param repetirPassword Confirmación de la contraseña.
     * @param errorOut StringBuilder para capturar los mensajes de error de validación.
     * @return true si el registro fue exitoso, false de lo contrario.
     */
    public boolean registrarCliente(String nombre, String correo, String password, String repetirPassword, StringBuilder errorOut) {
        if (nombre == null || nombre.trim().isEmpty()) {
            errorOut.append("El nombre es obligatorio.");
            return false;
        }

        if (!Validador.esCorreoValido(correo)) {
            errorOut.append("El formato del correo electrónico no es válido.");
            return false;
        }

        if (!Validador.esPasswordValida(password)) {
            errorOut.append("La contraseña debe tener al menos 6 caracteres.");
            return false;
        }

        if (!password.equals(repetirPassword)) {
            errorOut.append("Las contraseñas no coinciden.");
            return false;
        }

        String correoNormalizado = correo.trim().toLowerCase();

        // Comprobar existencia previa de correo
        if (usuarioDAO.existeCorreo(correoNormalizado)) {
            errorOut.append("El correo electrónico ya está registrado.");
            return false;
        }

        // Generar hash con sal usando PasswordUtil
        String passwordHash = PasswordUtil.generarHash(password);

        // Crear usuario con rol CLIENTE y activo por defecto
        Usuario nuevoUsuario = new Usuario(nombre.trim(), correoNormalizado, passwordHash, Rol.CLIENTE, true);

        long result = usuarioDAO.insertar(nuevoUsuario);
        return result != -1;
    }

    /**
     * Autentica a un usuario verificando sus credenciales e indicando su estado.
     *
     * @param correo Correo ingresado.
     * @param password Contraseña en texto plano.
     * @return El objeto Usuario si las credenciales son correctas y está activo.
     * @throws IllegalStateException Si el usuario existe pero está inactivo.
     * @throws IllegalArgumentException Si las credenciales son incorrectas o no existe.
     */
    public Usuario login(String correo, String password) throws IllegalStateException, IllegalArgumentException {
        if (!Validador.esCorreoValido(correo) || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Correo o contraseña incorrectos.");
        }

        Usuario usuario = usuarioDAO.buscarPorCorreo(correo.trim().toLowerCase());

        if (usuario == null || !PasswordUtil.verificarPassword(password, usuario.getPasswordHash())) {
            throw new IllegalArgumentException("Correo o contraseña incorrectos.");
        }

        if (!usuario.isActivo()) {
            throw new IllegalStateException("Cuenta desactivada. Contacta con el administrador.");
        }

        // Si la autenticación es exitosa, guardar sesión en memoria/SharedPreferences
        if (context != null) {
            guardarSesion(usuario);
        }

        return usuario;
    }

    /**
     * Guarda los datos esenciales de la sesión del usuario en SharedPreferences.
     */
    public void guardarSesion(Usuario usuario) {
        if (context == null || usuario == null) return;

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(KEY_USER_ID, usuario.getId());
        editor.putString(KEY_USER_NAME, usuario.getNombre());
        editor.putString(KEY_USER_EMAIL, usuario.getCorreo());
        editor.putString(KEY_USER_ROLE, usuario.getRol().name());
        editor.apply();
    }

    /**
     * Comprueba si existe una sesión de usuario guardada y activa.
     */
    public boolean haySesionActiva() {
        if (context == null) return false;
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.contains(KEY_USER_ID) && prefs.getLong(KEY_USER_ID, -1) != -1;
    }

    /**
     * Obtiene el rol almacenado en la sesión actual.
     */
    public Rol obtenerRolSesion() {
        if (context == null) return Rol.CLIENTE;
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String rolStr = prefs.getString(KEY_USER_ROLE, "CLIENTE");
        return Rol.deCadena(rolStr);
    }

    /**
     * Cierra la sesión activa eliminando las credenciales de SharedPreferences.
     */
    public void cerrarSesion() {
        if (context == null) return;
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    /**
     * Determina la clase de Activity correspondiente según el rol del usuario.
     *
     * @param rol Rol del usuario.
     * @return La clase de la Activity de destino (ClienteActivity, TecnicoActivity o AdminActivity).
     */
    public Class<?> obtenerActividadPorRol(Rol rol) {
        if (rol == null) return LoginActivity.class;

        switch (rol) {
            case TECNICO:
                return TecnicoActivity.class;
            case ADMIN:
                return AdminActivity.class;
            case CLIENTE:
            default:
                return ClienteActivity.class;
        }
    }
}
