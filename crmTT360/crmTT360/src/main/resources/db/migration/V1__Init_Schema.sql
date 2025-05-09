-- Flyway Migration Script: V1__Init_Schema.sql
-- Description: Initial schema creation for crmTT360 application

-- Tabla rol
CREATE TABLE rol (
    rol_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE,
    descripcion VARCHAR(200)
);

-- Tabla permiso
CREATE TABLE permiso (
    permiso_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE,
    descripcion VARCHAR(200)
);

-- Tabla de unión rol_permiso
CREATE TABLE rol_permiso (
    rol_id BIGINT NOT NULL,
    permiso_id BIGINT NOT NULL,
    PRIMARY KEY (rol_id, permiso_id),
    FOREIGN KEY (rol_id) REFERENCES rol(rol_id) ON DELETE CASCADE,
    FOREIGN KEY (permiso_id) REFERENCES permiso(permiso_id) ON DELETE CASCADE
);

-- Tabla usuario
CREATE TABLE usuario (
    usuario_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    estado VARCHAR(20),
    rol_id BIGINT,
    FOREIGN KEY (rol_id) REFERENCES rol(rol_id) ON DELETE SET NULL
);

-- Tabla estado
CREATE TABLE estado (
    estado_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_estado ENUM('ACTIVO', 'INACTIVO', 'PENDIENTE', 'CANCELADO', 'PEDIDO', 'ITEM') NOT NULL,
    valor VARCHAR(50) NOT NULL,
    UNIQUE KEY uk_estado_tipo_valor (tipo_estado, valor)
);

-- Tabla proveedor
CREATE TABLE proveedor (
    proveedor_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(200),
    telefono VARCHAR(15),
    email VARCHAR(100) NOT NULL UNIQUE
);

-- Tabla categoria
CREATE TABLE categoria (
    categoria_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT
);

-- Tabla bodega
CREATE TABLE bodega (
    bodega_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    tipo_bodega ENUM('MATERIA_PRIMA', 'PRODUCTO_TERMINADO', 'TEMPORAL') NOT NULL,
    capacidad_maxima INT NOT NULL,
    ubicacion VARCHAR(200) NOT NULL,
    responsable_id BIGINT,
    estado_id BIGINT NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (responsable_id) REFERENCES usuario(usuario_id),
    FOREIGN KEY (estado_id) REFERENCES estado(estado_id)
);

-- Tabla item (Padre)
CREATE TABLE item (
    item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_item VARCHAR(20) NOT NULL, -- Columna discriminadora
    codigo VARCHAR(100) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    unidad_medida VARCHAR(50) NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    stock_disponible INT NOT NULL, -- ÚNICA COLUMNA DE STOCK AHORA
    stock_minimo INT NOT NULL,
    stock_maximo INT,
    fecha_ingreso TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_vencimiento DATE,
    estado_id BIGINT NOT NULL,
    proveedor_id BIGINT NOT NULL,
    categoria_id BIGINT NOT NULL,
    bodega_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    FOREIGN KEY (estado_id) REFERENCES estado(estado_id),
    FOREIGN KEY (proveedor_id) REFERENCES proveedor(proveedor_id),
    FOREIGN KEY (categoria_id) REFERENCES categoria(categoria_id),
    FOREIGN KEY (bodega_id) REFERENCES bodega(bodega_id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(usuario_id)
);

-- Tabla materia_prima (Hija de item)
CREATE TABLE materia_prima (
    item_id BIGINT PRIMARY KEY, -- FK a item.item_id
    tipo_material ENUM('TELA', 'HILO', 'BOTON', 'CIERRE', 'ETIQUETA', 'OTROS') NOT NULL,
    ancho_rollo DECIMAL(5, 2),
    peso_metro DECIMAL(5, 2),
    proveedor_tela_id BIGINT,
    FOREIGN KEY (item_id) REFERENCES item(item_id) ON DELETE CASCADE,
    FOREIGN KEY (proveedor_tela_id) REFERENCES proveedor(proveedor_id)
);

-- Tabla producto (Hija de item)
CREATE TABLE producto (
    item_id BIGINT PRIMARY KEY, -- FK a item.item_id
    tipo_prenda ENUM('CAMISA', 'PANTALON', 'VESTIDO', 'CHAQUETA', 'OTROS') NOT NULL,
    talla ENUM('XS', 'S', 'M', 'L', 'XL', 'XXL', 'UNICA') NOT NULL,
    color VARCHAR(50) NOT NULL,
    temporada VARCHAR(50),
    composicion VARCHAR(100) NOT NULL,
    fecha_fabricacion DATE NOT NULL,
    FOREIGN KEY (item_id) REFERENCES item(item_id) ON DELETE CASCADE
);

-- Tabla cliente_interno
CREATE TABLE cliente_interno (
    cliente_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo_interno VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    tipo ENUM('INTERNO', 'EXTERNO') NOT NULL,
    responsable_id BIGINT,
    ubicacion VARCHAR(200),
    presupuesto_anual DECIMAL(12, 2),
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (responsable_id) REFERENCES usuario(usuario_id)
);

-- Tabla pedido
CREATE TABLE pedido (
    pedido_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha_pedido TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_fin TIMESTAMP NULL,
    cliente_id BIGINT,
    estado_id BIGINT NOT NULL,
    FOREIGN KEY (cliente_id) REFERENCES cliente_interno(cliente_id),
    FOREIGN KEY (estado_id) REFERENCES estado(estado_id)
);

-- Tabla pedido_detalle
CREATE TABLE pedido_detalle (
    pedido_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(12, 4) NOT NULL,
    PRIMARY KEY (pedido_id, item_id),
    FOREIGN KEY (pedido_id) REFERENCES pedido(pedido_id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES item(item_id)
);

-- Tabla factura
CREATE TABLE factura (
    factura_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    tipo_movimiento ENUM('VENTA', 'COMPRA') NOT NULL,
    total DECIMAL(12, 2) NOT NULL,
    estado_pago BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pedido_id) REFERENCES pedido(pedido_id)
);

-- Índices adicionales
CREATE INDEX idx_usuario_email ON usuario(email);
CREATE INDEX idx_item_nombre ON item(nombre);
CREATE INDEX idx_item_bodega_id ON item(bodega_id);
CREATE INDEX idx_pedido_cliente_id ON pedido(cliente_id);
CREATE INDEX idx_pedido_estado_id ON pedido(estado_id);