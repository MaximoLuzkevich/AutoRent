CREATE DATABASE IF NOT EXISTS db_autoRent;
USE db_autoRent;

-- =====================================================
-- ROLES DEL SISTEMA
-- =====================================================

CREATE TABLE IF NOT EXISTS rol (
    id_rol INT PRIMARY KEY AUTO_INCREMENT,
    nombre ENUM('CLIENTE', 'PROPIETARIO', 'ADMINISTRADOR') NOT NULL UNIQUE
);

-- =====================================================
-- USUARIOS
-- =====================================================

CREATE TABLE IF NOT EXISTS usuario (
    id_usuario INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    telefono VARCHAR(30),
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

-- Un usuario puede tener varios roles.
-- Todos los usuarios nuevos deberían tener CLIENTE por defecto.
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

-- =====================================================
-- DATOS EXTRA DEL PROPIETARIO
-- =====================================================

-- Esta tabla NO representa otro usuario.
-- Representa los datos extra que completa alguien cuando toca:
-- "Conviértete en propietario".
CREATE TABLE IF NOT EXISTS perfil_propietario (
    id_usuario INT PRIMARY KEY,
    dni VARCHAR(30),
    cuit VARCHAR(30),
    direccion VARCHAR(150),
    ciudad VARCHAR(100),
    provincia VARCHAR(100),
    fecha_alta DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    verificado BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_perfil_propietario_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
        ON DELETE CASCADE
);

-- =====================================================
-- CATEGORÍAS DE AUTOS
-- =====================================================

CREATE TABLE IF NOT EXISTS categoria_auto (
    id_categoria INT PRIMARY KEY AUTO_INCREMENT,
    nombre ENUM('ECONOMICO', 'PREMIUM', 'SUV', 'ELECTRICO', 'UTILITARIO') NOT NULL UNIQUE,
    descripcion TEXT
);

-- =====================================================
-- AUTOS
-- =====================================================

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

-- =====================================================
-- IMÁGENES DEL AUTO
-- =====================================================

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

-- =====================================================
-- RESERVAS
-- =====================================================

CREATE TABLE IF NOT EXISTS reserva (
    id_reserva INT PRIMARY KEY AUTO_INCREMENT,

    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,

    precio_total DECIMAL(10,2) NOT NULL CHECK (precio_total > 0),

    estado ENUM('PENDIENTE', 'CONFIRMADA', 'CANCELADA', 'FINALIZADA') 
        NOT NULL DEFAULT 'PENDIENTE',

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

-- =====================================================
-- PAGOS
-- =====================================================

CREATE TABLE IF NOT EXISTS pago (
    id_pago INT PRIMARY KEY AUTO_INCREMENT,

    monto DECIMAL(10,2) NOT NULL CHECK (monto > 0),

    metodo_pago ENUM('EFECTIVO', 'TARJETA', 'TRANSFERENCIA', 'MERCADO_PAGO') NOT NULL,

    estado ENUM('PENDIENTE', 'APROBADO', 'RECHAZADO') 
        NOT NULL DEFAULT 'PENDIENTE',

    fecha_pago DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    id_reserva INT NOT NULL,

    CONSTRAINT fk_pago_reserva
        FOREIGN KEY (id_reserva) REFERENCES reserva(id_reserva)
        ON DELETE CASCADE
);

-- =====================================================
-- REVIEWS
-- =====================================================

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
-- ROLES
-- =====================================================

INSERT IGNORE INTO rol (nombre)
VALUES
('CLIENTE'),
('PROPIETARIO'),
('ADMINISTRADOR');

-- =====================================================
-- CATEGORIAS DE AUTOS
-- =====================================================
INSERT IGNORE INTO categoria_auto (nombre, descripcion)
VALUES
('ECONOMICO', 'Autos simples y accesibles para uso diario.'),
('PREMIUM', 'Autos de gama alta con mayor comodidad.'),
('SUV', 'Autos amplios para viajes o familias.'),
('ELECTRICO', 'Autos electricos o de bajas emisiones.'),
('UTILITARIO', 'Vehiculos para carga o trabajo.');

-- =====================================================
-- USUARIOS Y DATOS DE PRUEBA
-- Password para todos: 123456
-- =====================================================

INSERT IGNORE INTO usuario (nombre, email, password, telefono)
VALUES
('Cliente Demo', 'cliente@test.com', '$2a$10$TnS8a6xZAZrZIqosz51FA.vIrxCC4dUgVQ9k9LFVUvcBWdS4A3W7y', '1122334455'),
('Propietario Demo', 'propietario@test.com', '$2a$10$TnS8a6xZAZrZIqosz51FA.vIrxCC4dUgVQ9k9LFVUvcBWdS4A3W7y', '1133445566'),
('Admin Demo', 'admin@test.com', '$2a$10$TnS8a6xZAZrZIqosz51FA.vIrxCC4dUgVQ9k9LFVUvcBWdS4A3W7y', '1199999999');

INSERT IGNORE INTO usuario_rol (id_usuario, id_rol)
SELECT u.id_usuario, r.id_rol
FROM usuario u
JOIN rol r ON r.nombre = 'CLIENTE'
WHERE u.email IN ('cliente@test.com', 'propietario@test.com', 'admin@test.com');

INSERT IGNORE INTO usuario_rol (id_usuario, id_rol)
SELECT u.id_usuario, r.id_rol
FROM usuario u
JOIN rol r ON r.nombre = 'PROPIETARIO'
WHERE u.email = 'propietario@test.com';

INSERT IGNORE INTO usuario_rol (id_usuario, id_rol)
SELECT u.id_usuario, r.id_rol
FROM usuario u
JOIN rol r ON r.nombre = 'ADMINISTRADOR'
WHERE u.email = 'admin@test.com';

INSERT IGNORE INTO perfil_propietario (id_usuario, dni, cuit, direccion, ciudad, provincia, verificado)
SELECT id_usuario, '30111222', '20-30111222-3', 'Av. Corrientes 1234', 'Buenos Aires', 'Buenos Aires', TRUE
FROM usuario
WHERE email = 'propietario@test.com';

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

