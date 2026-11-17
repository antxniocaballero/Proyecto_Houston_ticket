# Houston, tenemos un ticket - Proyecto Base (2º DAM)

Esqueleto de la aplicación "Houston, tenemos un ticket" implementado para Android (Java 17, SQLite y BCrypt) y preparado estructuralmente para Web (Servlets / Tomcat).

## Contenido del Proyecto
- **Capa de Modelo:** `Rol.java`, `Usuario.java`.
- **Capa de Datos:** `DatabaseHelper.java`, `UsuarioDAO.java` (consultas SQLite con `PreparedStatement` / `ContentValues`).
- **Capa de Servicios:** `AuthService.java` (autenticación, registro público de clientes y control de intentos fallidos en memoria).
- **Capa de Utilidades:** `PasswordUtil.java` (hashing seguro con jBCrypt).
- **Vistas / UI:** `MainActivity`, `LoginActivity`, `RegistroActivity`, `ClienteActivity`, `TecnicoActivity`, `AdminActivity`.
- **Base de Datos SQL:** `houston_base.sql` (script de creación e inserts para MySQL).
- **Pruebas Unitarias:** `PasswordUtilTest`, `AuthServiceTest` con JUnit 5 / JUnit 4 y Mockito.

## Usuarios de Prueba Creados
- **Administrador:** `admin@houston.local` / `Admin1234` (Rol: ADMIN)
- **Técnico:** `tecnico@houston.local` / `Tecnico1234` (Rol: TECNICO)
- **Cliente:** `cliente@houston.local` / `Cliente1234` (Rol: CLIENTE)

## Requisitos de Ejecución
- Android Studio Jellyfish / Ladybug / Koala o superior.
- Java 17.
- Gradle con soporte para la librería `jbcrypt` (incluida en `build.gradle.kts`).
