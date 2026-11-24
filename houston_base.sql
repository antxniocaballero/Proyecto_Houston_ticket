-- Script SQL para el proyecto Houston, tenemos un ticket (Sprint 3 y 4)

CREATE DATABASE IF NOT EXISTS houston_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE houston_db;

-- 1. Tabla de Usuarios
CREATE TABLE usuarios (
  id_usuario INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  rol VARCHAR(20) NOT NULL,
  activo BOOLEAN NOT NULL DEFAULT TRUE,
  fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tabla de Tickets (con asignación a técnico nullable)
CREATE TABLE tickets (
  id INT AUTO_INCREMENT PRIMARY KEY,
  titulo VARCHAR(100) NOT NULL,
  descripcion TEXT NOT NULL,
  categoria VARCHAR(50) NOT NULL,
  prioridad VARCHAR(20) NOT NULL,
  estado VARCHAR(20) NOT NULL DEFAULT 'ABIERTO',
  adjunto VARCHAR(255) NULL,
  creado_por INT NOT NULL,
  id_tecnico INT NULL,
  fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
  fecha_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (creado_por) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
  FOREIGN KEY (id_tecnico) REFERENCES usuarios(id_usuario) ON DELETE SET NULL
);

-- 3. Tabla de Comentarios
CREATE TABLE comentarios (
  id INT AUTO_INCREMENT PRIMARY KEY,
  id_ticket INT NOT NULL,
  id_usuario INT NOT NULL,
  autor_nombre VARCHAR(50) NOT NULL,
  rol_autor VARCHAR(20) NOT NULL,
  mensaje TEXT NOT NULL,
  fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_ticket) REFERENCES tickets(id) ON DELETE CASCADE,
  FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

-- Inserción de usuarios iniciales de prueba
INSERT INTO usuarios (nombre, email, password_hash, rol, activo)
VALUES ('Juan Cliente', 'cliente@houston.local', '$2a$10$8k9a2K0z0370z.pX07/.2uJqK8fXg300e84L5v6G9qo8uLOickgx2', 'CLIENTE', TRUE);

INSERT INTO usuarios (nombre, email, password_hash, rol, activo)
VALUES ('Carlos Técnico', 'tecnico@houston.local', '$2a$10$r62k1Gv5Kq0370z.pX07/.2mO88GfW3cQhJ2dK8fXg300e84L5v6G', 'TECNICO', TRUE);

INSERT INTO usuarios (nombre, email, password_hash, rol, activo)
VALUES ('Ana Administradora', 'admin@houston.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', TRUE);

-- Inserción de tickets de prueba iniciales
INSERT INTO tickets (titulo, descripcion, categoria, prioridad, estado, creado_por, id_tecnico)
VALUES ('Fallo en pantalla de inicio', 'La pantalla se queda congelada al iniciar sesión.', 'Software', 'Alta', 'ABIERTO', 1, NULL);

INSERT INTO tickets (titulo, descripcion, categoria, prioridad, estado, creado_por, id_tecnico)
VALUES ('Teclado no responde', 'La tecla Enter dejó de funcionar tras la actualización.', 'Hardware', 'Media', 'EN_CURSO', 1, 2);

INSERT INTO tickets (titulo, descripcion, categoria, prioridad, estado, creado_por, id_tecnico)
VALUES ('Restablecimiento de correo', 'Solicitud de reinicio de contraseña de correo corporativo.', 'Redes', 'Baja', 'RESUELTO', 1, 2);

-- Inserción de comentarios iniciales de prueba
INSERT INTO comentarios (id_ticket, id_usuario, autor_nombre, rol_autor, mensaje)
VALUES (2, 2, 'Carlos Técnico', 'TECNICO', 'He comenzado a revisar el dispositivo en el taller.');

INSERT INTO comentarios (id_ticket, id_usuario, autor_nombre, rol_autor, mensaje)
VALUES (2, 1, 'Juan Cliente', 'CLIENTE', 'Muchas gracias, quedo atento a cualquier novedad.');
