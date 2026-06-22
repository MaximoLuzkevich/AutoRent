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
    public_id VARCHAR(255),
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
-- DATOS INICIALES DE PRUEBA
-- Password para todos: 123456
-- =====================================================

INSERT IGNORE INTO usuario (nombre, email, password, telefono, activo)
VALUES
('Cliente Prueba', 'cliente@test.com', '$2a$10$TnS8a6xZAZrZIqosz51FA.vIrxCC4dUgVQ9k9LFVUvcBWdS4A3W7y', '1122334455', TRUE),
('Propietario Prueba', 'propietario@test.com', '$2a$10$TnS8a6xZAZrZIqosz51FA.vIrxCC4dUgVQ9k9LFVUvcBWdS4A3W7y', '1133445566', TRUE),
('Administrador Prueba', 'admin@test.com', '$2a$10$TnS8a6xZAZrZIqosz51FA.vIrxCC4dUgVQ9k9LFVUvcBWdS4A3W7y', '1199999999', TRUE),
('Administrador Propietario Prueba', 'adminprop@test.com', '$2a$10$TnS8a6xZAZrZIqosz51FA.vIrxCC4dUgVQ9k9LFVUvcBWdS4A3W7y', '1188888888', TRUE);

-- Roles asignados para probar cada tipo de usuario.
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

-- Datos de propietario para los usuarios que pueden publicar autos.
INSERT IGNORE INTO perfil_propietario (id_usuario, dni, cuit, direccion, ciudad, provincia, verificado)
SELECT u.id_usuario, '30111222', '20-30111222-3', 'Av. Corrientes 1234', 'Buenos Aires', 'Buenos Aires', TRUE
FROM usuario u
WHERE u.email = 'propietario@test.com';

INSERT IGNORE INTO perfil_propietario (id_usuario, dni, cuit, direccion, ciudad, provincia, verificado)
SELECT u.id_usuario, '32999888', '20-32999888-7', 'Av. Santa Fe 2222', 'Buenos Aires', 'Buenos Aires', TRUE
FROM usuario u
WHERE u.email = 'adminprop@test.com';

-- Autos iniciales publicados en la plataforma.
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
    'AB123CD',
    'Blanco',
    5,
    4,
    'AUTOMATICA',
    'NAFTA',
    10000.00,
    'Sedan compacto, comodo y de bajo consumo para uso urbano.',
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
    'AC624TL',
    'Gris',
    5,
    5,
    'MANUAL',
    'NAFTA',
    13500.00,
    'SUV compacta para viajes cortos o familiares.',
    'Buenos Aires',
    'Buenos Aires',
    'Av. Corrientes 1800',
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
    'AD321ON',
    'Azul',
    5,
    4,
    'AUTOMATICA',
    'NAFTA',
    12000.00,
    'Hatchback urbano con buen equipamiento y manejo sencillo.',
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
    'AF404EV',
    'Rojo',
    5,
    4,
    'AUTOMATICA',
    'ELECTRICO',
    28000.00,
    'Sedan electrico premium con caja automatica y excelente autonomia.',
    'Buenos Aires',
    'Buenos Aires',
    'Av. Santa Fe 2500',
    u.id_usuario,
    c.id_categoria
FROM usuario u
JOIN categoria_auto c ON c.nombre = 'ELECTRICO'
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
    'Ferrari',
    'SF90 Spider',
    2023,
    'AG900SF',
    'Rojo',
    2,
    2,
    'AUTOMATICA',
    'NAFTA',
    95000.00,
    'Deportivo premium descapotable para alquileres especiales.',
    'Buenos Aires',
    'Buenos Aires',
    'Av. Libertador 4100',
    u.id_usuario,
    c.id_categoria
FROM usuario u
JOIN categoria_auto c ON c.nombre = 'PREMIUM'
WHERE u.email = 'adminprop@test.com';

-- Reservas de ejemplo del cliente de prueba.
INSERT INTO reserva (fecha_inicio, fecha_fin, precio_total, estado, id_cliente, id_auto)
SELECT '2026-05-01', '2026-05-05', 40000.00, 'FINALIZADA', cliente.id_usuario, auto_publicado.id_auto
FROM usuario cliente
JOIN auto auto_publicado ON auto_publicado.patente = 'AB123CD'
WHERE cliente.email = 'cliente@test.com'
  AND NOT EXISTS (
      SELECT 1
      FROM reserva r
      WHERE r.id_cliente = cliente.id_usuario
        AND r.id_auto = auto_publicado.id_auto
        AND r.fecha_inicio = '2026-05-01'
        AND r.fecha_fin = '2026-05-05'
  );

INSERT INTO reserva (fecha_inicio, fecha_fin, precio_total, estado, id_cliente, id_auto)
SELECT '2026-06-20', '2026-06-23', 40500.00, 'PENDIENTE', cliente.id_usuario, auto_publicado.id_auto
FROM usuario cliente
JOIN auto auto_publicado ON auto_publicado.patente = 'AC624TL'
WHERE cliente.email = 'cliente@test.com'
  AND NOT EXISTS (
      SELECT 1
      FROM reserva r
      WHERE r.id_cliente = cliente.id_usuario
        AND r.id_auto = auto_publicado.id_auto
        AND r.fecha_inicio = '2026-06-20'
        AND r.fecha_fin = '2026-06-23'
  );

INSERT INTO reserva (fecha_inicio, fecha_fin, precio_total, estado, id_cliente, id_auto)
SELECT '2026-06-25', '2026-06-27', 24000.00, 'CONFIRMADA', cliente.id_usuario, auto_publicado.id_auto
FROM usuario cliente
JOIN auto auto_publicado ON auto_publicado.patente = 'AD321ON'
WHERE cliente.email = 'cliente@test.com'
  AND NOT EXISTS (
      SELECT 1
      FROM reserva r
      WHERE r.id_cliente = cliente.id_usuario
        AND r.id_auto = auto_publicado.id_auto
        AND r.fecha_inicio = '2026-06-25'
        AND r.fecha_fin = '2026-06-27'
  );

INSERT INTO reserva (fecha_inicio, fecha_fin, precio_total, estado, id_cliente, id_auto)
SELECT '2026-05-10', '2026-05-12', 56000.00, 'FINALIZADA', cliente.id_usuario, auto_publicado.id_auto
FROM usuario cliente
JOIN auto auto_publicado ON auto_publicado.patente = 'AF404EV'
WHERE cliente.email = 'cliente@test.com'
  AND NOT EXISTS (
      SELECT 1
      FROM reserva r
      WHERE r.id_cliente = cliente.id_usuario
        AND r.id_auto = auto_publicado.id_auto
        AND r.fecha_inicio = '2026-05-10'
        AND r.fecha_fin = '2026-05-12'
  );

INSERT INTO reserva (fecha_inicio, fecha_fin, precio_total, estado, id_cliente, id_auto)
SELECT '2026-08-01', '2026-08-03', 20000.00, 'PENDIENTE', cliente.id_usuario, auto_publicado.id_auto
FROM usuario cliente
JOIN auto auto_publicado ON auto_publicado.patente = 'AB123CD'
WHERE cliente.email = 'cliente@test.com'
  AND NOT EXISTS (
      SELECT 1
      FROM reserva r
      WHERE r.id_cliente = cliente.id_usuario
        AND r.id_auto = auto_publicado.id_auto
        AND r.fecha_inicio = '2026-08-01'
        AND r.fecha_fin = '2026-08-03'
  );

-- Pagos de ejemplo. Solo hay pagos para reservas confirmadas o finalizadas.
INSERT INTO pago (monto, metodo_pago, estado, id_reserva)
SELECT r.precio_total, 'MERCADO_PAGO', 'APROBADO', r.id_reserva
FROM reserva r
JOIN usuario cliente ON cliente.id_usuario = r.id_cliente
JOIN auto auto_publicado ON auto_publicado.id_auto = r.id_auto
WHERE cliente.email = 'cliente@test.com'
  AND auto_publicado.patente = 'AB123CD'
  AND r.fecha_inicio = '2026-05-01'
  AND r.fecha_fin = '2026-05-05'
  AND NOT EXISTS (
      SELECT 1
      FROM pago p
      WHERE p.id_reserva = r.id_reserva
        AND p.metodo_pago = 'MERCADO_PAGO'
  );

INSERT INTO pago (monto, metodo_pago, estado, id_reserva)
SELECT r.precio_total, 'MERCADO_PAGO', 'APROBADO', r.id_reserva
FROM reserva r
JOIN usuario cliente ON cliente.id_usuario = r.id_cliente
JOIN auto auto_publicado ON auto_publicado.id_auto = r.id_auto
WHERE cliente.email = 'cliente@test.com'
  AND auto_publicado.patente = 'AD321ON'
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
JOIN auto auto_publicado ON auto_publicado.id_auto = r.id_auto
WHERE cliente.email = 'cliente@test.com'
  AND auto_publicado.patente = 'AF404EV'
  AND r.fecha_inicio = '2026-05-10'
  AND r.fecha_fin = '2026-05-12'
  AND NOT EXISTS (
      SELECT 1
      FROM pago p
      WHERE p.id_reserva = r.id_reserva
        AND p.metodo_pago = 'TARJETA'
  );

-- Opiniones cargadas sobre autos que ya tuvieron alquileres.
INSERT IGNORE INTO review (puntuacion, comentario, id_cliente, id_auto)
SELECT 5, 'Muy buen auto, limpio y comodo para viajar.', cliente.id_usuario, auto_publicado.id_auto
FROM usuario cliente
JOIN auto auto_publicado ON auto_publicado.patente = 'AB123CD'
WHERE cliente.email = 'cliente@test.com';

INSERT IGNORE INTO review (puntuacion, comentario, id_cliente, id_auto)
SELECT 4, 'Muy comodo para moverse por la ciudad.', cliente.id_usuario, auto_publicado.id_auto
FROM usuario cliente
JOIN auto auto_publicado ON auto_publicado.patente = 'AF404EV'
WHERE cliente.email = 'cliente@test.com';

-- Imagenes iniciales. La primera imagen de cada auto queda como principal.
INSERT INTO imagen_auto (nombre_archivo, url_imagen, principal, id_auto)
SELECT datos.nombre_archivo, datos.url_imagen, datos.principal, a.id_auto
FROM auto a
JOIN (
    SELECT 'AB123CD' AS patente, 'corolla-frente.png' AS nombre_archivo, '/img/autos-iniciales/corolla-frente.png' AS url_imagen, TRUE AS principal
    UNION ALL SELECT 'AB123CD', 'corolla-derecha.png', '/img/autos-iniciales/corolla-derecha.png', FALSE
    UNION ALL SELECT 'AB123CD', 'corolla-izquierda.png', '/img/autos-iniciales/corolla-izquierda.png', FALSE
    UNION ALL SELECT 'AB123CD', 'corolla-atras.png', '/img/autos-iniciales/corolla-atras.png', FALSE
    UNION ALL SELECT 'AC624TL', 'eco-frente.png', '/img/autos-iniciales/eco-frente.png', TRUE
    UNION ALL SELECT 'AC624TL', 'eco-derecha.png', '/img/autos-iniciales/eco-derecha.png', FALSE
    UNION ALL SELECT 'AC624TL', 'eco-izquierda.png', '/img/autos-iniciales/eco-izquierda.png', FALSE
    UNION ALL SELECT 'AC624TL', 'eco-atras.png', '/img/autos-iniciales/eco-atras.png', FALSE
    UNION ALL SELECT 'AD321ON', 'onix-frente.png', '/img/autos-iniciales/onix-frente.png', TRUE
    UNION ALL SELECT 'AD321ON', 'onix-derecha.png', '/img/autos-iniciales/onix-derecha.png', FALSE
    UNION ALL SELECT 'AD321ON', 'onix-izquierda.png', '/img/autos-iniciales/onix-izquierda.png', FALSE
    UNION ALL SELECT 'AD321ON', 'onix-atras.png', '/img/autos-iniciales/onix-atras.png', FALSE
    UNION ALL SELECT 'AF404EV', 'tesla-frente.png', '/img/autos-iniciales/tesla-frente.png', TRUE
    UNION ALL SELECT 'AF404EV', 'tesla-derecha.png', '/img/autos-iniciales/tesla-derecha.png', FALSE
    UNION ALL SELECT 'AF404EV', 'tesla-izquierda.png', '/img/autos-iniciales/tesla-izquierda.png', FALSE
    UNION ALL SELECT 'AF404EV', 'tesla-atras.png', '/img/autos-iniciales/tesla-atras.png', FALSE
    UNION ALL SELECT 'AG900SF', 'ferrari-frente.png', '/img/autos-iniciales/ferrari-frente.png', TRUE
    UNION ALL SELECT 'AG900SF', 'ferrari-derecha.png', '/img/autos-iniciales/ferrari-derecha.png', FALSE
    UNION ALL SELECT 'AG900SF', 'ferrari-izquierda.png', '/img/autos-iniciales/ferrari-izquierda.png', FALSE
    UNION ALL SELECT 'AG900SF', 'ferrari-atras.png', '/img/autos-iniciales/ferrari-atras.png', FALSE
) datos ON datos.patente = a.patente
WHERE NOT EXISTS (
    SELECT 1
    FROM imagen_auto i
    WHERE i.id_auto = a.id_auto
      AND i.url_imagen = datos.url_imagen
);
