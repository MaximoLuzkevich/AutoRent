CREATE DATABASE IF NOT EXISTS db_autoRent
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_autoRent;

-- =====================================================
-- TABLAS
-- =====================================================

CREATE TABLE IF NOT EXISTS rol (
    id_rol INT PRIMARY KEY AUTO_INCREMENT,
    nombre ENUM('CLIENTE', 'PROPIETARIO', 'ADMINISTRADOR') NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS usuario (
    id_usuario INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    telefono VARCHAR(30),
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS usuario_rol (
    id_usuario INT NOT NULL,
    id_rol INT NOT NULL,

    PRIMARY KEY (id_usuario, id_rol),

    CONSTRAINT fk_usuario_rol_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
        ON DELETE CASCADE,

    CONSTRAINT fk_usuario_rol_rol
        FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
);

CREATE TABLE IF NOT EXISTS perfil_propietario (
    id_usuario INT PRIMARY KEY,
    dni VARCHAR(30),
    cuit VARCHAR(30),
    direccion VARCHAR(150),
    ciudad VARCHAR(100),
    provincia VARCHAR(100),
    fecha_alta DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    verificado BOOLEAN NOT NULL DEFAULT FALSE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_perfil_propietario_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS categoria_auto (
    id_categoria INT PRIMARY KEY AUTO_INCREMENT,
    nombre ENUM('ECONOMICO', 'PREMIUM', 'SUV', 'ELECTRICO', 'UTILITARIO') NOT NULL UNIQUE,
    descripcion TEXT
);

CREATE TABLE IF NOT EXISTS auto (
    id_auto INT PRIMARY KEY AUTO_INCREMENT,
    marca VARCHAR(100) NOT NULL,
    modelo VARCHAR(100) NOT NULL,
    anio INT NOT NULL,
    patente VARCHAR(20) NOT NULL UNIQUE,
    color VARCHAR(50),
    capacidad_pasajeros INT NOT NULL CHECK (capacidad_pasajeros > 0),
    cantidad_puertas INT NOT NULL CHECK (cantidad_puertas > 0),
    transmision ENUM('MANUAL', 'AUTOMATICA') NOT NULL,
    combustible ENUM('NAFTA', 'DIESEL', 'ELECTRICO', 'HIBRIDO') NOT NULL,
    precio_dia DECIMAL(10,2) NOT NULL CHECK (precio_dia > 0),
    descripcion TEXT,
    ciudad VARCHAR(100) NOT NULL,
    provincia VARCHAR(100),
    direccion_retiro VARCHAR(150) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_publicacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_propietario INT NOT NULL,
    id_categoria INT NOT NULL,

    CONSTRAINT fk_auto_propietario
        FOREIGN KEY (id_propietario) REFERENCES usuario(id_usuario),

    CONSTRAINT fk_auto_categoria
        FOREIGN KEY (id_categoria) REFERENCES categoria_auto(id_categoria)
);

CREATE TABLE IF NOT EXISTS imagen_auto (
    id_imagen INT PRIMARY KEY AUTO_INCREMENT,
    nombre_archivo VARCHAR(255) NOT NULL,
    url_imagen VARCHAR(500) NOT NULL,
    principal BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_carga DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_auto INT NOT NULL,

    CONSTRAINT fk_imagen_auto
        FOREIGN KEY (id_auto) REFERENCES auto(id_auto)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reserva (
    id_reserva INT PRIMARY KEY AUTO_INCREMENT,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    precio_total DECIMAL(10,2) NOT NULL CHECK (precio_total > 0),
    estado ENUM('PENDIENTE', 'CONFIRMADA', 'CANCELADA', 'FINALIZADA') NOT NULL DEFAULT 'PENDIENTE',
    fecha_reserva DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_cliente INT NOT NULL,
    id_auto INT NOT NULL,

    CONSTRAINT chk_reserva_fechas
        CHECK (fecha_fin > fecha_inicio),

    CONSTRAINT fk_reserva_cliente
        FOREIGN KEY (id_cliente) REFERENCES usuario(id_usuario),

    CONSTRAINT fk_reserva_auto
        FOREIGN KEY (id_auto) REFERENCES auto(id_auto)
);

CREATE TABLE IF NOT EXISTS pago (
    id_pago INT PRIMARY KEY AUTO_INCREMENT,
    monto DECIMAL(10,2) NOT NULL CHECK (monto > 0),
    metodo_pago ENUM('EFECTIVO', 'TARJETA', 'MERCADO_PAGO') NOT NULL,
    estado ENUM('PENDIENTE', 'APROBADO', 'RECHAZADO') NOT NULL DEFAULT 'PENDIENTE',
    fecha_pago DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_reserva INT NOT NULL,

    CONSTRAINT fk_pago_reserva
        FOREIGN KEY (id_reserva) REFERENCES reserva(id_reserva)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS review (
    id_review INT PRIMARY KEY AUTO_INCREMENT,
    puntuacion INT NOT NULL CHECK (puntuacion BETWEEN 1 AND 5),
    comentario TEXT,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_cliente INT NOT NULL,
    id_auto INT NOT NULL,

    CONSTRAINT fk_review_cliente
        FOREIGN KEY (id_cliente) REFERENCES usuario(id_usuario),

    CONSTRAINT fk_review_auto
        FOREIGN KEY (id_auto) REFERENCES auto(id_auto)
        ON DELETE CASCADE,

    CONSTRAINT uq_review_cliente_auto
        UNIQUE (id_cliente, id_auto)
);

-- =====================================================
-- DATOS BASE
-- =====================================================

INSERT IGNORE INTO rol (nombre)
VALUES
('CLIENTE'),
('PROPIETARIO'),
('ADMINISTRADOR');

INSERT IGNORE INTO categoria_auto (nombre, descripcion)
VALUES
('ECONOMICO', 'Autos simples y accesibles para uso diario.'),
('PREMIUM', 'Autos de gama alta con mayor comodidad.'),
('SUV', 'Autos amplios para viajes o familias.'),
('ELECTRICO', 'Autos electricos o de bajas emisiones.'),
('UTILITARIO', 'Vehiculos para carga o trabajo.');

-- =====================================================
-- DATOS DEMO
-- Password para todos: 123456
-- =====================================================

INSERT INTO usuario (nombre, email, password, telefono, activo)
VALUES
('Cliente Demo', 'cliente@test.com', '$2a$10$TnS8a6xZAZrZIqosz51FA.vIrxCC4dUgVQ9k9LFVUvcBWdS4A3W7y', '1122334455', TRUE),
('Propietario Demo', 'propietario@test.com', '$2a$10$TnS8a6xZAZrZIqosz51FA.vIrxCC4dUgVQ9k9LFVUvcBWdS4A3W7y', '1133445566', TRUE),
('Admin Demo', 'admin@test.com', '$2a$10$TnS8a6xZAZrZIqosz51FA.vIrxCC4dUgVQ9k9LFVUvcBWdS4A3W7y', '1199999999', TRUE),
('Admin Propietario Demo', 'adminprop@test.com', '$2a$10$TnS8a6xZAZrZIqosz51FA.vIrxCC4dUgVQ9k9LFVUvcBWdS4A3W7y', '1188888888', TRUE)
ON DUPLICATE KEY UPDATE
    nombre = VALUES(nombre),
    password = VALUES(password),
    telefono = VALUES(telefono),
    activo = TRUE;

INSERT IGNORE INTO usuario_rol (id_usuario, id_rol)
SELECT u.id_usuario, r.id_rol
FROM usuario u
JOIN rol r ON r.nombre = 'CLIENTE'
WHERE u.email IN ('cliente@test.com', 'propietario@test.com', 'admin@test.com', 'adminprop@test.com');

INSERT IGNORE INTO usuario_rol (id_usuario, id_rol)
SELECT u.id_usuario, r.id_rol
FROM usuario u
JOIN rol r ON r.nombre = 'PROPIETARIO'
WHERE u.email = 'propietario@test.com';

INSERT IGNORE INTO usuario_rol (id_usuario, id_rol)
SELECT u.id_usuario, r.id_rol
FROM usuario u
JOIN rol r ON r.nombre = 'PROPIETARIO'
WHERE u.email = 'adminprop@test.com';

INSERT IGNORE INTO usuario_rol (id_usuario, id_rol)
SELECT u.id_usuario, r.id_rol
FROM usuario u
JOIN rol r ON r.nombre = 'ADMINISTRADOR'
WHERE u.email IN ('admin@test.com', 'adminprop@test.com');

INSERT IGNORE INTO perfil_propietario (id_usuario, dni, cuit, direccion, ciudad, provincia, verificado)
SELECT u.id_usuario, '30111222', '20-30111222-3', 'Av. Corrientes 1234', 'Buenos Aires', 'Buenos Aires', TRUE
FROM usuario u
WHERE u.email = 'propietario@test.com';

INSERT IGNORE INTO perfil_propietario (id_usuario, dni, cuit, direccion, ciudad, provincia, verificado)
SELECT u.id_usuario, '32999888', '20-32999888-7', 'Av. Santa Fe 2222', 'Buenos Aires', 'Buenos Aires', TRUE
FROM usuario u
WHERE u.email = 'adminprop@test.com';

INSERT IGNORE INTO auto (
    marca,
    modelo,
    anio,
    patente,
    color,
    capacidad_pasajeros,
    cantidad_puertas,
    transmision,
    combustible,
    precio_dia,
    descripcion,
    ciudad,
    provincia,
    direccion_retiro,
    id_propietario,
    id_categoria
)
SELECT
    'Toyota',
    'Corolla',
    2022,
    'DEMO123',
    'Blanco',
    5,
    4,
    'AUTOMATICA',
    'NAFTA',
    10000.00,
    'Auto de prueba para la defensa del proyecto.',
    'Buenos Aires',
    'Buenos Aires',
    'Av. Corrientes 1234',
    u.id_usuario,
    c.id_categoria
FROM usuario u
JOIN categoria_auto c ON c.nombre = 'ECONOMICO'
WHERE u.email = 'propietario@test.com';

INSERT IGNORE INTO auto (
    marca,
    modelo,
    anio,
    patente,
    color,
    capacidad_pasajeros,
    cantidad_puertas,
    transmision,
    combustible,
    precio_dia,
    descripcion,
    ciudad,
    provincia,
    direccion_retiro,
    id_propietario,
    id_categoria
)
SELECT
    'Ford',
    'EcoSport',
    2021,
    'PROP456',
    'Gris',
    5,
    5,
    'MANUAL',
    'NAFTA',
    13500.00,
    'SUV compacta para viajes cortos o familiares.',
    'Cordoba',
    'Cordoba',
    'Av. Colon 850',
    u.id_usuario,
    c.id_categoria
FROM usuario u
JOIN categoria_auto c ON c.nombre = 'SUV'
WHERE u.email = 'propietario@test.com';

INSERT IGNORE INTO auto (
    marca,
    modelo,
    anio,
    patente,
    color,
    capacidad_pasajeros,
    cantidad_puertas,
    transmision,
    combustible,
    precio_dia,
    descripcion,
    ciudad,
    provincia,
    direccion_retiro,
    id_propietario,
    id_categoria
)
SELECT
    'Chevrolet',
    'Onix',
    2023,
    'ADM111',
    'Azul',
    5,
    4,
    'AUTOMATICA',
    'NAFTA',
    12000.00,
    'Auto urbano publicado por un administrador propietario.',
    'Buenos Aires',
    'Buenos Aires',
    'Av. Santa Fe 2222',
    u.id_usuario,
    c.id_categoria
FROM usuario u
JOIN categoria_auto c ON c.nombre = 'ECONOMICO'
WHERE u.email = 'adminprop@test.com';

INSERT IGNORE INTO auto (
    marca,
    modelo,
    anio,
    patente,
    color,
    capacidad_pasajeros,
    cantidad_puertas,
    transmision,
    combustible,
    precio_dia,
    descripcion,
    ciudad,
    provincia,
    direccion_retiro,
    id_propietario,
    id_categoria
)
SELECT
    'Tesla',
    'Model 3',
    2022,
    'ADM222',
    'Rojo',
    5,
    4,
    'AUTOMATICA',
    'ELECTRICO',
    28000.00,
    'Auto electrico premium publicado por un administrador propietario.',
    'Rosario',
    'Santa Fe',
    'Bv. Oroño 1200',
    u.id_usuario,
    c.id_categoria
FROM usuario u
JOIN categoria_auto c ON c.nombre = 'ELECTRICO'
WHERE u.email = 'adminprop@test.com';

INSERT INTO reserva (fecha_inicio, fecha_fin, precio_total, estado, id_cliente, id_auto)
SELECT '2026-05-01', '2026-05-05', 40000.00, 'FINALIZADA', cliente.id_usuario, auto_demo.id_auto
FROM usuario cliente
JOIN auto auto_demo ON auto_demo.patente = 'DEMO123'
WHERE cliente.email = 'cliente@test.com'
  AND NOT EXISTS (
      SELECT 1
      FROM reserva r
      WHERE r.id_cliente = cliente.id_usuario
        AND r.id_auto = auto_demo.id_auto
        AND r.fecha_inicio = '2026-05-01'
        AND r.fecha_fin = '2026-05-05'
  );

INSERT INTO reserva (fecha_inicio, fecha_fin, precio_total, estado, id_cliente, id_auto)
SELECT '2026-06-20', '2026-06-23', 40500.00, 'PENDIENTE', cliente.id_usuario, auto_demo.id_auto
FROM usuario cliente
JOIN auto auto_demo ON auto_demo.patente = 'PROP456'
WHERE cliente.email = 'cliente@test.com'
  AND NOT EXISTS (
      SELECT 1
      FROM reserva r
      WHERE r.id_cliente = cliente.id_usuario
        AND r.id_auto = auto_demo.id_auto
        AND r.fecha_inicio = '2026-06-20'
        AND r.fecha_fin = '2026-06-23'
  );

INSERT INTO reserva (fecha_inicio, fecha_fin, precio_total, estado, id_cliente, id_auto)
SELECT '2026-06-25', '2026-06-27', 24000.00, 'CONFIRMADA', cliente.id_usuario, auto_demo.id_auto
FROM usuario cliente
JOIN auto auto_demo ON auto_demo.patente = 'ADM111'
WHERE cliente.email = 'cliente@test.com'
  AND NOT EXISTS (
      SELECT 1
      FROM reserva r
      WHERE r.id_cliente = cliente.id_usuario
        AND r.id_auto = auto_demo.id_auto
        AND r.fecha_inicio = '2026-06-25'
        AND r.fecha_fin = '2026-06-27'
  );

INSERT INTO reserva (fecha_inicio, fecha_fin, precio_total, estado, id_cliente, id_auto)
SELECT '2026-05-10', '2026-05-12', 56000.00, 'FINALIZADA', cliente.id_usuario, auto_demo.id_auto
FROM usuario cliente
JOIN auto auto_demo ON auto_demo.patente = 'ADM222'
WHERE cliente.email = 'cliente@test.com'
  AND NOT EXISTS (
      SELECT 1
      FROM reserva r
      WHERE r.id_cliente = cliente.id_usuario
        AND r.id_auto = auto_demo.id_auto
        AND r.fecha_inicio = '2026-05-10'
        AND r.fecha_fin = '2026-05-12'
  );

INSERT INTO reserva (fecha_inicio, fecha_fin, precio_total, estado, id_cliente, id_auto)
SELECT '2026-08-01', '2026-08-03', 20000.00, 'PENDIENTE', cliente.id_usuario, auto_demo.id_auto
FROM usuario cliente
JOIN auto auto_demo ON auto_demo.patente = 'DEMO123'
WHERE cliente.email = 'cliente@test.com'
  AND NOT EXISTS (
      SELECT 1
      FROM reserva r
      WHERE r.id_cliente = cliente.id_usuario
        AND r.id_auto = auto_demo.id_auto
        AND r.fecha_inicio = '2026-08-01'
        AND r.fecha_fin = '2026-08-03'
  );

INSERT INTO pago (monto, metodo_pago, estado, id_reserva)
SELECT r.precio_total, 'MERCADO_PAGO', 'APROBADO', r.id_reserva
FROM reserva r
JOIN usuario cliente ON cliente.id_usuario = r.id_cliente
JOIN auto auto_demo ON auto_demo.id_auto = r.id_auto
WHERE cliente.email = 'cliente@test.com'
  AND auto_demo.patente = 'DEMO123'
  AND r.fecha_inicio = '2026-05-01'
  AND r.fecha_fin = '2026-05-05'
  AND NOT EXISTS (
      SELECT 1
      FROM pago p
      WHERE p.id_reserva = r.id_reserva
        AND p.metodo_pago = 'MERCADO_PAGO'
  );

INSERT INTO pago (monto, metodo_pago, estado, id_reserva)
SELECT r.precio_total, 'TARJETA', 'PENDIENTE', r.id_reserva
FROM reserva r
JOIN usuario cliente ON cliente.id_usuario = r.id_cliente
JOIN auto auto_demo ON auto_demo.id_auto = r.id_auto
WHERE cliente.email = 'cliente@test.com'
  AND auto_demo.patente = 'PROP456'
  AND r.fecha_inicio = '2026-06-20'
  AND r.fecha_fin = '2026-06-23'
  AND NOT EXISTS (
      SELECT 1
      FROM pago p
      WHERE p.id_reserva = r.id_reserva
        AND p.metodo_pago = 'TARJETA'
  );

INSERT INTO pago (monto, metodo_pago, estado, id_reserva)
SELECT r.precio_total, 'MERCADO_PAGO', 'APROBADO', r.id_reserva
FROM reserva r
JOIN usuario cliente ON cliente.id_usuario = r.id_cliente
JOIN auto auto_demo ON auto_demo.id_auto = r.id_auto
WHERE cliente.email = 'cliente@test.com'
  AND auto_demo.patente = 'ADM111'
  AND r.fecha_inicio = '2026-06-25'
  AND r.fecha_fin = '2026-06-27'
  AND NOT EXISTS (
      SELECT 1
      FROM pago p
      WHERE p.id_reserva = r.id_reserva
        AND p.metodo_pago = 'MERCADO_PAGO'
  );

INSERT INTO pago (monto, metodo_pago, estado, id_reserva)
SELECT r.precio_total, 'TARJETA', 'APROBADO', r.id_reserva
FROM reserva r
JOIN usuario cliente ON cliente.id_usuario = r.id_cliente
JOIN auto auto_demo ON auto_demo.id_auto = r.id_auto
WHERE cliente.email = 'cliente@test.com'
  AND auto_demo.patente = 'ADM222'
  AND r.fecha_inicio = '2026-05-10'
  AND r.fecha_fin = '2026-05-12'
  AND NOT EXISTS (
      SELECT 1
      FROM pago p
      WHERE p.id_reserva = r.id_reserva
        AND p.metodo_pago = 'TARJETA'
  );

INSERT IGNORE INTO review (puntuacion, comentario, id_cliente, id_auto)
SELECT 5, 'Muy buen auto para el alquiler de prueba.', cliente.id_usuario, auto_demo.id_auto
FROM usuario cliente
JOIN auto auto_demo ON auto_demo.patente = 'DEMO123'
WHERE cliente.email = 'cliente@test.com';

INSERT IGNORE INTO review (puntuacion, comentario, id_cliente, id_auto)
SELECT 4, 'Muy comodo para moverse por la ciudad.', cliente.id_usuario, auto_demo.id_auto
FROM usuario cliente
JOIN auto auto_demo ON auto_demo.patente = 'ADM222'
WHERE cliente.email = 'cliente@test.com';

INSERT INTO imagen_auto (nombre_archivo, url_imagen, principal, id_auto)
SELECT 'toyota-corolla.jpg', 'https://placehold.co/800x450/png?text=Toyota+Corolla', TRUE, a.id_auto
FROM auto a
WHERE a.patente = 'DEMO123'
  AND NOT EXISTS (
      SELECT 1
      FROM imagen_auto i
      WHERE i.id_auto = a.id_auto
        AND i.url_imagen = 'https://placehold.co/800x450/png?text=Toyota+Corolla'
  );

INSERT INTO imagen_auto (nombre_archivo, url_imagen, principal, id_auto)
SELECT 'ford-ecosport.jpg', 'https://placehold.co/800x450/png?text=Ford+EcoSport', TRUE, a.id_auto
FROM auto a
WHERE a.patente = 'PROP456'
  AND NOT EXISTS (
      SELECT 1
      FROM imagen_auto i
      WHERE i.id_auto = a.id_auto
        AND i.url_imagen = 'https://placehold.co/800x450/png?text=Ford+EcoSport'
  );

INSERT INTO imagen_auto (nombre_archivo, url_imagen, principal, id_auto)
SELECT 'chevrolet-onix.jpg', 'https://placehold.co/800x450/png?text=Chevrolet+Onix', TRUE, a.id_auto
FROM auto a
WHERE a.patente = 'ADM111'
  AND NOT EXISTS (
      SELECT 1
      FROM imagen_auto i
      WHERE i.id_auto = a.id_auto
        AND i.url_imagen = 'https://placehold.co/800x450/png?text=Chevrolet+Onix'
  );

INSERT INTO imagen_auto (nombre_archivo, url_imagen, principal, id_auto)
SELECT 'tesla-model-3.jpg', 'https://placehold.co/800x450/png?text=Tesla+Model+3', TRUE, a.id_auto
FROM auto a
WHERE a.patente = 'ADM222'
  AND NOT EXISTS (
      SELECT 1
      FROM imagen_auto i
      WHERE i.id_auto = a.id_auto
        AND i.url_imagen = 'https://placehold.co/800x450/png?text=Tesla+Model+3'
  );
