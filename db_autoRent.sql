CREATE DATABASE IF NOT EXISTS db_autoRent;
USE db_autoRent;

-- =====================================================
-- ROLES DEL SISTEMA
-- =====================================================

CREATE TABLE rol (
    id_rol INT PRIMARY KEY AUTO_INCREMENT,
    nombre ENUM('CLIENTE', 'PROPIETARIO', 'ADMINISTRADOR') NOT NULL UNIQUE
);

-- =====================================================
-- USUARIOS
-- =====================================================

CREATE TABLE usuario (
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
CREATE TABLE usuario_rol (
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
CREATE TABLE perfil_propietario (
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

CREATE TABLE categoria_auto (
    id_categoria INT PRIMARY KEY AUTO_INCREMENT,
    nombre ENUM('ECONOMICO', 'PREMIUM', 'SUV', 'ELECTRICO', 'UTILITARIO') NOT NULL UNIQUE,
    descripcion TEXT
);

-- =====================================================
-- AUTOS
-- =====================================================

CREATE TABLE auto (
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

CREATE TABLE imagen_auto (
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

CREATE TABLE reserva (
    id_reserva INT PRIMARY KEY AUTO_INCREMENT,

    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,

    precio_total DECIMAL(10,2) NOT NULL CHECK (precio_total >= 0),

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

CREATE TABLE pago (
    id_pago INT PRIMARY KEY AUTO_INCREMENT,

    monto DECIMAL(10,2) NOT NULL CHECK (monto >= 0),

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

CREATE TABLE review (
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

INSERT INTO rol (nombre)
VALUES
('CLIENTE'),
('PROPIETARIO'),
('ADMINISTRADOR');

-- =====================================================
-- CATEGORIAS DE AUTOS
-- =====================================================
INSERT INTO categoria_auto (nombre, descripcion)
VALUES
('ECONOMICO', 'Autos simples y accesibles para uso diario.'),
('PREMIUM', 'Autos de gama alta con mayor comodidad.'),
('SUV', 'Autos amplios para viajes o familias.'),
('ELECTRICO', 'Autos electricos o de bajas emisiones.'),
('UTILITARIO', 'Vehiculos para carga o trabajo.');
