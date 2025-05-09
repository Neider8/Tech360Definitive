-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Versión del servidor:         10.4.32-MariaDB - mariadb.org binary distribution
-- SO del servidor:              Win64
-- HeidiSQL Versión:             12.10.0.7000
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Volcando estructura de base de datos para telastech360

-- Volcando estructura para tabla telastech360.bodega
CREATE TABLE IF NOT EXISTS `bodega` (
  `bodega_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `capacidad_maxima` int(11) NOT NULL,
  `fecha_creacion` datetime(6) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `tipo_bodega` enum('MATERIA_PRIMA','PRODUCTO_TERMINADO','TEMPORAL') NOT NULL,
  `ubicacion` varchar(200) NOT NULL,
  `estado_id` bigint(20) NOT NULL,
  `responsable_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`bodega_id`),
  KEY `FK9rgg13h7d6ruu0uoo5il4ppu` (`estado_id`),
  KEY `FKsu4lygfk5q1k67p2cxb0mbqwn` (`responsable_id`),
  CONSTRAINT `FK9rgg13h7d6ruu0uoo5il4ppu` FOREIGN KEY (`estado_id`) REFERENCES `estado` (`estado_id`),
  CONSTRAINT `FKsu4lygfk5q1k67p2cxb0mbqwn` FOREIGN KEY (`responsable_id`) REFERENCES `usuario` (`usuario_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla telastech360.categoria
CREATE TABLE IF NOT EXISTS `categoria` (
  `categoria_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `descripcion` text DEFAULT NULL,
  `nombre` varchar(100) NOT NULL,
  PRIMARY KEY (`categoria_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla telastech360.cliente_interno
CREATE TABLE IF NOT EXISTS `cliente_interno` (
  `cliente_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `codigo_interno` varchar(20) NOT NULL,
  `fecha_registro` datetime(6) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `presupuesto_anual` decimal(12,2) DEFAULT NULL,
  `tipo` enum('ALMACEN','DEPARTAMENTO','OTROS','TIENDA_FISICA') NOT NULL,
  `ubicacion` varchar(200) DEFAULT NULL,
  `responsable_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`cliente_id`),
  UNIQUE KEY `uk_cliente_codigo` (`codigo_interno`),
  KEY `FKc182orq50ja5i50ngblxxf2g6` (`responsable_id`),
  CONSTRAINT `FKc182orq50ja5i50ngblxxf2g6` FOREIGN KEY (`responsable_id`) REFERENCES `usuario` (`usuario_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla telastech360.estado
CREATE TABLE IF NOT EXISTS `estado` (
  `estado_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tipo_estado` enum('ACTIVO','CANCELADO','INACTIVO','PENDIENTE','PEDIDO','ITEM') DEFAULT NULL,
  `valor` varchar(50) NOT NULL,
  PRIMARY KEY (`estado_id`),
  UNIQUE KEY `uk_estado_tipo_valor` (`tipo_estado`,`valor`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla telastech360.factura
CREATE TABLE IF NOT EXISTS `factura` (
  `factura_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tipo_movimiento` enum('COMPRA','VENTA') NOT NULL,
  `total` decimal(12,2) NOT NULL,
  `pedido_id` bigint(20) NOT NULL,
  PRIMARY KEY (`factura_id`),
  KEY `FKn6q9mbkc0n4g1uux57clh2bq0` (`pedido_id`),
  CONSTRAINT `FKn6q9mbkc0n4g1uux57clh2bq0` FOREIGN KEY (`pedido_id`) REFERENCES `pedido` (`pedido_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla telastech360.item
CREATE TABLE IF NOT EXISTS `item` (
  `item_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `codigo` varchar(100) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `fecha_ingreso` datetime(6) NOT NULL,
  `fecha_vencimiento` date DEFAULT NULL,
  `nombre` varchar(100) NOT NULL,
  `precio_unitario` decimal(12,4) NOT NULL,
  `stock_maximo` int(11) NOT NULL,
  `stock_minimo` int(11) NOT NULL,
  `unidad_medida` varchar(50) NOT NULL,
  `bodega_id` bigint(20) NOT NULL,
  `categoria_id` bigint(20) DEFAULT NULL,
  `estado_id` bigint(20) DEFAULT NULL,
  `proveedor_id` bigint(20) DEFAULT NULL,
  `usuario_id` bigint(20) NOT NULL,
  `stock_actual` int(11) NOT NULL,
  PRIMARY KEY (`item_id`),
  UNIQUE KEY `uk_item_codigo` (`codigo`),
  KEY `FK1s3tv1cjou05p1dognldxmr7l` (`bodega_id`),
  KEY `FKgunwc9qk1hc1yt3qk6urk2gfh` (`categoria_id`),
  KEY `FKcvrrn0v014j5ab0ssh1bjctn8` (`estado_id`),
  KEY `FKedwhbprton8jmcrifs5xn1qm9` (`proveedor_id`),
  KEY `FKpmv00mpj3ufvwmkh24x4j6j65` (`usuario_id`),
  CONSTRAINT `FK1s3tv1cjou05p1dognldxmr7l` FOREIGN KEY (`bodega_id`) REFERENCES `bodega` (`bodega_id`),
  CONSTRAINT `FKcvrrn0v014j5ab0ssh1bjctn8` FOREIGN KEY (`estado_id`) REFERENCES `estado` (`estado_id`),
  CONSTRAINT `FKedwhbprton8jmcrifs5xn1qm9` FOREIGN KEY (`proveedor_id`) REFERENCES `proveedor` (`proveedor_id`),
  CONSTRAINT `FKgunwc9qk1hc1yt3qk6urk2gfh` FOREIGN KEY (`categoria_id`) REFERENCES `categoria` (`categoria_id`),
  CONSTRAINT `FKpmv00mpj3ufvwmkh24x4j6j65` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`usuario_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla telastech360.materia_prima
CREATE TABLE IF NOT EXISTS `materia_prima` (
  `ancho_rollo` decimal(5,2) DEFAULT NULL,
  `peso_metro` decimal(5,2) DEFAULT NULL,
  `tipo_material` enum('BOTON','CIERRE','ETIQUETA','HILO','OTROS','TELA') NOT NULL,
  `materia_prima_id` bigint(20) NOT NULL,
  `proveedor_tela_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`materia_prima_id`),
  KEY `FKnufnkd54kh01u827q0skaqi1e` (`proveedor_tela_id`),
  CONSTRAINT `FKl0nbt15yb618c88dhydb6lmg0` FOREIGN KEY (`materia_prima_id`) REFERENCES `item` (`item_id`),
  CONSTRAINT `FKnufnkd54kh01u827q0skaqi1e` FOREIGN KEY (`proveedor_tela_id`) REFERENCES `proveedor` (`proveedor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla telastech360.pedido
CREATE TABLE IF NOT EXISTS `pedido` (
  `pedido_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `fecha_pedido` datetime(6) NOT NULL,
  `cliente_id` bigint(20) DEFAULT NULL,
  `estado_id` bigint(20) NOT NULL,
  PRIMARY KEY (`pedido_id`),
  KEY `FK17t7l7pn9gvbn51k4cms4g0fq` (`cliente_id`),
  KEY `FKlpuc2kd4q97wd68te94hcw8sl` (`estado_id`),
  CONSTRAINT `FK17t7l7pn9gvbn51k4cms4g0fq` FOREIGN KEY (`cliente_id`) REFERENCES `cliente_interno` (`cliente_id`),
  CONSTRAINT `FKlpuc2kd4q97wd68te94hcw8sl` FOREIGN KEY (`estado_id`) REFERENCES `estado` (`estado_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla telastech360.pedido_detalle
CREATE TABLE IF NOT EXISTS `pedido_detalle` (
  `cantidad` int(11) NOT NULL,
  `precio_unitario` decimal(12,4) NOT NULL,
  `item_id` bigint(20) NOT NULL,
  `pedido_id` bigint(20) NOT NULL,
  PRIMARY KEY (`item_id`,`pedido_id`),
  KEY `FKhuvcqbd92kc4eqypgqmyi17cb` (`pedido_id`),
  CONSTRAINT `FKhju3j2kajh2dpxaj8h7q3foqt` FOREIGN KEY (`item_id`) REFERENCES `item` (`item_id`),
  CONSTRAINT `FKhuvcqbd92kc4eqypgqmyi17cb` FOREIGN KEY (`pedido_id`) REFERENCES `pedido` (`pedido_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla telastech360.permiso
CREATE TABLE IF NOT EXISTS `permiso` (
  `permiso_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(200) DEFAULT NULL,
  `nombre` varchar(255) NOT NULL,
  PRIMARY KEY (`permiso_id`),
  UNIQUE KEY `UKnwe6lkk7x7sbw94xcmbwgvycu` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla telastech360.producto
CREATE TABLE IF NOT EXISTS `producto` (
  `color` varchar(50) NOT NULL,
  `composicion` text DEFAULT NULL,
  `fecha_fabricacion` date NOT NULL,
  `talla` enum('L','M','S','UNICA','XL','XS','XXL') NOT NULL,
  `temporada` varchar(20) DEFAULT NULL,
  `tipo_prenda` enum('CAMISA','CHAQUETA','OTROS','PANTALON','VESTIDO') NOT NULL,
  `item_id` bigint(20) NOT NULL,
  PRIMARY KEY (`item_id`),
  CONSTRAINT `FKb7ibtmhqwgw9ijjw3jdgem1lt` FOREIGN KEY (`item_id`) REFERENCES `item` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla telastech360.proveedor
CREATE TABLE IF NOT EXISTS `proveedor` (
  `proveedor_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `direccion` varchar(200) DEFAULT NULL,
  `email` varchar(100) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `telefono` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`proveedor_id`),
  UNIQUE KEY `UKou6oi9246xevkpv4u4r0jge2t` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla telastech360.rol
CREATE TABLE IF NOT EXISTS `rol` (
  `rol_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(200) DEFAULT NULL,
  `nombre` varchar(255) NOT NULL,
  PRIMARY KEY (`rol_id`),
  UNIQUE KEY `UK43kr6s7bts1wqfv43f7jd87kp` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla telastech360.rol_permiso
CREATE TABLE IF NOT EXISTS `rol_permiso` (
  `permiso_id` bigint(20) NOT NULL,
  `rol_id` bigint(20) NOT NULL,
  PRIMARY KEY (`permiso_id`,`rol_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla telastech360.usuario
CREATE TABLE IF NOT EXISTS `usuario` (
  `usuario_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(100) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `rol_id` bigint(20) DEFAULT NULL,
  `estado` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`usuario_id`),
  UNIQUE KEY `UK5171l57faosmj8myawaucatdw` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- La exportación de datos fue deseleccionada.

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
