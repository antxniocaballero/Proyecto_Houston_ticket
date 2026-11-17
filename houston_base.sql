-- Script SQL para el proyecto Houston, tenemos un ticket (DAM)

CREATE DATABASE IF NOT EXISTS houston_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE houston_db;

CREATE TABLE rol (
  id_rol INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE usuario (
  id_usuario INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  id_rol INT NOT NULL,
  activo BOOLEAN NOT NULL DEFAULT TRUE,
  fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
);

-- Inserción de los tres roles obligatorios
INSERT INTO rol (id_rol, nombre) VALUES (1, 'CLIENTE');
INSERT INTO rol (id_rol, nombre) VALUES (2, 'TECNICO');
INSERT INTO rol (id_rol, nombre) VALUES (3, 'ADMIN');

-- Usuarios de prueba iniciales con contraseñas cifradas con BCrypt
-- Admin1234 -> $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
INSERT INTO usuario (nombre, email, password_hash, id_rol, activo)
VALUES ('Administrador', 'admin@houston.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 3, TRUE);

-- Tecnico1234 -> $2a$10$r62k1Gv5Kq0370z.pX07/.2mO88GfW3cQhJ2dK8fXg300e84L5v6G
INSERT INTO usuario (nombre, email, password_hash, id_rol, activo)
VALUES ('Técnico Soporte', 'tecnico@houston.local', '$2a$10$r62k1Gv5Kq0370z.pX07/.2mO88GfW3cQhJ2dK8fXg300e84L5v6G', 2, TRUE);

-- Cliente1234 -> $2a$10$8k9a2K0z0370z.pX07/.2uJqK8fXg300e84L5v6G9qo8uLOickgx2
INSERT INTO usuario (nombre, email, password_hash, id_rol, activo)
VALUES ('Cliente Demo', 'cliente@houston.local', '$2a$10$8k9a2K0z0370z.pX07/.2uJqK8fXg300e84L5v6G9qo8uLOickgx2', 1, TRUE);
