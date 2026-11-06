-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: barchello_db
-- ------------------------------------------------------
-- Server version	5.5.5-10.4.32-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `areaproduccion`
--

DROP TABLE IF EXISTS `areaproduccion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `areaproduccion` (
  `idArea` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `tipo` enum('cocina','bar','cafetería') NOT NULL,
  `estado` enum('operativa','mantenimiento') NOT NULL DEFAULT 'operativa',
  `fechaCreacion` datetime DEFAULT current_timestamp(),
  `usuarioCreacion` varchar(50) NOT NULL,
  `fechaModificacion` datetime DEFAULT NULL,
  `usuarioModificacion` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`idArea`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `categoria`
--

DROP TABLE IF EXISTS `categoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categoria` (
  `idCategoria` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) DEFAULT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `fechaCreacion` datetime DEFAULT current_timestamp(),
  `usuarioCreacion` varchar(50) DEFAULT 'Sistema',
  `fechaModificacion` datetime DEFAULT NULL,
  `usuarioModificacion` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`idCategoria`),
  UNIQUE KEY `idx_categoria_nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cliente`
--

DROP TABLE IF EXISTS `cliente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cliente` (
  `idCliente` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `correo` varchar(100) DEFAULT NULL,
  `identificacion` varchar(20) NOT NULL,
  `fechaRegistro` datetime NOT NULL DEFAULT current_timestamp(),
  `estado` enum('activo','inactivo') NOT NULL DEFAULT 'activo',
  `fechaCreacion` datetime DEFAULT current_timestamp(),
  `usuarioCreacion` varchar(50) NOT NULL,
  `fechaModificacion` datetime DEFAULT NULL,
  `usuarioModificacion` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`idCliente`),
  UNIQUE KEY `idx_cliente_identificacion` (`identificacion`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `detallefactura`
--

DROP TABLE IF EXISTS `detallefactura`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detallefactura` (
  `idDetalle` int(11) NOT NULL AUTO_INCREMENT,
  `idFactura` int(11) NOT NULL,
  `idProducto` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL,
  `precioUnitario` decimal(10,2) NOT NULL,
  `precioHistorico` int(11) DEFAULT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  `fechaCreacion` datetime DEFAULT current_timestamp(),
  `usuarioCreacion` varchar(50) NOT NULL,
  PRIMARY KEY (`idDetalle`),
  KEY `fk_detfactura_factura` (`idFactura`),
  KEY `fk_detfactura_producto` (`idProducto`),
  KEY `fk_detfactura_histprecio` (`precioHistorico`),
  CONSTRAINT `fk_detfactura_factura` FOREIGN KEY (`idFactura`) REFERENCES `factura` (`idFactura`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_detfactura_histprecio` FOREIGN KEY (`precioHistorico`) REFERENCES `historialprecios` (`idPrecio`) ON UPDATE CASCADE,
  CONSTRAINT `fk_detfactura_producto` FOREIGN KEY (`idProducto`) REFERENCES `producto` (`idProducto`) ON UPDATE CASCADE,
  CONSTRAINT `chk_detfactura_cantidad` CHECK (`cantidad` > 0),
  CONSTRAINT `chk_detfactura_precio` CHECK (`precioUnitario` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `detallepedido`
--

DROP TABLE IF EXISTS `detallepedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detallepedido` (
  `idPedido` int(11) NOT NULL,
  `idProducto` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL,
  `estado` enum('pendiente','en preparación','listo','servido') NOT NULL DEFAULT 'pendiente',
  `observaciones` varchar(255) DEFAULT NULL,
  `idArea` int(11) NOT NULL,
  `fechaCreacion` datetime DEFAULT current_timestamp(),
  `usuarioCreacion` varchar(50) NOT NULL,
  `fechaModificacion` datetime DEFAULT NULL,
  `usuarioModificacion` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`idPedido`,`idProducto`),
  KEY `fk_detallepedido_producto` (`idProducto`),
  KEY `fk_detallepedido_area` (`idArea`),
  CONSTRAINT `fk_detallepedido_area` FOREIGN KEY (`idArea`) REFERENCES `areaproduccion` (`idArea`) ON UPDATE CASCADE,
  CONSTRAINT `fk_detallepedido_pedido` FOREIGN KEY (`idPedido`) REFERENCES `pedido` (`idPedido`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_detallepedido_producto` FOREIGN KEY (`idProducto`) REFERENCES `producto` (`idProducto`) ON UPDATE CASCADE,
  CONSTRAINT `chk_detallepedido_cantidad` CHECK (`cantidad` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `detallesordencompra`
--

DROP TABLE IF EXISTS `detallesordencompra`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detallesordencompra` (
  `idDetalle` int(11) NOT NULL AUTO_INCREMENT,
  `idOrden` int(11) NOT NULL,
  `idProducto` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL,
  `precioUnitario` decimal(10,2) NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  `fechaCreacion` datetime DEFAULT current_timestamp(),
  `usuarioCreacion` varchar(50) NOT NULL,
  PRIMARY KEY (`idDetalle`),
  KEY `fk_detorden_orden` (`idOrden`),
  KEY `fk_detorden_producto` (`idProducto`),
  CONSTRAINT `fk_detorden_orden` FOREIGN KEY (`idOrden`) REFERENCES `ordenescompra` (`idOrden`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_detorden_producto` FOREIGN KEY (`idProducto`) REFERENCES `producto` (`idProducto`) ON UPDATE CASCADE,
  CONSTRAINT `chk_detorden_cantidad` CHECK (`cantidad` > 0),
  CONSTRAINT `chk_detorden_precio` CHECK (`precioUnitario` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `factura`
--

DROP TABLE IF EXISTS `factura`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `factura` (
  `idFactura` int(11) NOT NULL AUTO_INCREMENT,
  `idPedido` int(11) NOT NULL,
  `fechaHora` datetime NOT NULL DEFAULT current_timestamp(),
  `total` decimal(10,2) NOT NULL,
  `estado` enum('emitida','pagada','anulada') NOT NULL DEFAULT 'emitida',
  `idMetodoPago` int(11) NOT NULL,
  `idUsuario` int(11) DEFAULT NULL,
  `fechaCreacion` datetime DEFAULT current_timestamp(),
  `usuarioCreacion` varchar(50) NOT NULL,
  `fechaModificacion` datetime DEFAULT NULL,
  `usuarioModificacion` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`idFactura`),
  UNIQUE KEY `idx_factura_pedido` (`idPedido`),
  KEY `fk_factura_metodopago` (`idMetodoPago`),
  KEY `fk_factura_usuario` (`idUsuario`),
  CONSTRAINT `fk_factura_metodopago` FOREIGN KEY (`idMetodoPago`) REFERENCES `metodopago` (`idMetodo`) ON UPDATE CASCADE,
  CONSTRAINT `fk_factura_pedido` FOREIGN KEY (`idPedido`) REFERENCES `pedido` (`idPedido`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_factura_usuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `chk_factura_total` CHECK (`total` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `historialprecios`
--

DROP TABLE IF EXISTS `historialprecios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `historialprecios` (
  `idPrecio` int(11) NOT NULL AUTO_INCREMENT,
  `idProducto` int(11) NOT NULL,
  `precio` decimal(10,2) NOT NULL,
  `fechaInicio` date NOT NULL,
  `fechaFin` date DEFAULT NULL,
  `idUsuario` int(11) DEFAULT NULL,
  `fechaCreacion` datetime DEFAULT current_timestamp(),
  `usuarioCreacion` varchar(50) NOT NULL,
  PRIMARY KEY (`idPrecio`),
  KEY `fk_histprecios_producto` (`idProducto`),
  KEY `fk_histprecios_usuario` (`idUsuario`),
  CONSTRAINT `fk_histprecios_producto` FOREIGN KEY (`idProducto`) REFERENCES `producto` (`idProducto`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_histprecios_usuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `chk_histprecios_precio` CHECK (`precio` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `inventario`
--

DROP TABLE IF EXISTS `inventario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventario` (
  `idInventario` int(11) NOT NULL AUTO_INCREMENT,
  `idProducto` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL,
  `fechaRegistro` datetime NOT NULL DEFAULT current_timestamp(),
  `idUsuario` int(11) DEFAULT NULL,
  `tipoRegistro` enum('inicial','ajuste','compra','uso','devolución') NOT NULL DEFAULT 'inicial',
  `fechaCreacion` datetime DEFAULT current_timestamp(),
  `usuarioCreacion` varchar(50) NOT NULL,
  PRIMARY KEY (`idInventario`),
  KEY `fk_inventario_producto` (`idProducto`),
  KEY `fk_inventario_usuario` (`idUsuario`),
  CONSTRAINT `fk_inventario_producto` FOREIGN KEY (`idProducto`) REFERENCES `producto` (`idProducto`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_inventario_usuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `chk_inventario_cantidad` CHECK (`cantidad` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `menu`
--

DROP TABLE IF EXISTS `menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `menu` (
  `idMenu` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `fechaInicio` date NOT NULL,
  `fechaFin` date DEFAULT NULL,
  `estado` enum('activo','inactivo') NOT NULL DEFAULT 'activo',
  `fechaCreacion` datetime DEFAULT current_timestamp(),
  `usuarioCreacion` varchar(50) NOT NULL,
  `fechaModificacion` datetime DEFAULT NULL,
  `usuarioModificacion` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`idMenu`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mesa`
--

DROP TABLE IF EXISTS `mesa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mesa` (
  `idMesa` int(11) NOT NULL AUTO_INCREMENT,
  `numero` int(11) NOT NULL,
  `capacidad` int(11) NOT NULL,
  `ubicacion` enum('zona fumadores','zona no fumadores','terraza') NOT NULL,
  `estado` enum('disponible','ocupada','reservada') NOT NULL DEFAULT 'disponible',
  `fechaCreacion` datetime DEFAULT current_timestamp(),
  `usuarioCreacion` varchar(50) NOT NULL,
  `fechaModificacion` datetime DEFAULT NULL,
  `usuarioModificacion` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`idMesa`),
  UNIQUE KEY `idx_mesa_numero` (`numero`),
  CONSTRAINT `chk_mesa_capacidad` CHECK (`capacidad` between 2 and 12)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `metodopago`
--

DROP TABLE IF EXISTS `metodopago`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `metodopago` (
  `idMetodo` int(11) NOT NULL AUTO_INCREMENT,
  `tipo` enum('efectivo','tarjeta crédito','tarjeta débito','transferencia') NOT NULL,
  `descripcion` varchar(100) DEFAULT NULL,
  `fechaCreacion` datetime DEFAULT current_timestamp(),
  `usuarioCreacion` varchar(50) NOT NULL,
  `fechaModificacion` datetime DEFAULT NULL,
  `usuarioModificacion` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`idMetodo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `movimientosinventario`
--

DROP TABLE IF EXISTS `movimientosinventario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movimientosinventario` (
  `idMovimiento` int(11) NOT NULL AUTO_INCREMENT,
  `idProducto` int(11) NOT NULL,
  `tipoMovimiento` enum('entrada','salida') NOT NULL,
  `cantidad` int(11) NOT NULL,
  `fechaHora` datetime NOT NULL DEFAULT current_timestamp(),
  `referenciaId` int(11) DEFAULT NULL,
  `idUsuario` int(11) DEFAULT NULL,
  `motivo` varchar(255) DEFAULT NULL,
  `fechaCreacion` datetime DEFAULT current_timestamp(),
  `usuarioCreacion` varchar(50) NOT NULL,
  PRIMARY KEY (`idMovimiento`),
  KEY `fk_movinv_producto` (`idProducto`),
  KEY `fk_movinv_usuario` (`idUsuario`),
  CONSTRAINT `fk_movinv_producto` FOREIGN KEY (`idProducto`) REFERENCES `producto` (`idProducto`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_movinv_usuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `chk_movinv_cantidad` CHECK (`cantidad` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ordenescompra`
--

DROP TABLE IF EXISTS `ordenescompra`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ordenescompra` (
  `idOrden` int(11) NOT NULL AUTO_INCREMENT,
  `idProveedor` int(11) NOT NULL,
  `fechaSolicitud` date NOT NULL,
  `fechaEntrega` date DEFAULT NULL,
  `estado` enum('pendiente','recibida','parcial') NOT NULL DEFAULT 'pendiente',
  `idUsuario` int(11) DEFAULT NULL,
  `observaciones` varchar(255) DEFAULT NULL,
  `fechaCreacion` datetime DEFAULT current_timestamp(),
  `usuarioCreacion` varchar(50) NOT NULL,
  `fechaModificacion` datetime DEFAULT NULL,
  `usuarioModificacion` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`idOrden`),
  KEY `fk_ordenes_proveedor` (`idProveedor`),
  KEY `fk_ordenes_usuario` (`idUsuario`),
  CONSTRAINT `fk_ordenes_proveedor` FOREIGN KEY (`idProveedor`) REFERENCES `proveedor` (`idProveedor`) ON UPDATE CASCADE,
  CONSTRAINT `fk_ordenes_usuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pedido`
--

DROP TABLE IF EXISTS `pedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pedido` (
  `idPedido` int(11) NOT NULL AUTO_INCREMENT,
  `fechaHora` datetime NOT NULL DEFAULT current_timestamp(),
  `estado` enum('en proceso','completado','cancelado','en cocina') NOT NULL DEFAULT 'en proceso',
  `observaciones` varchar(255) DEFAULT NULL,
  `idMesa` int(11) NOT NULL,
  `idUsuario` int(11) DEFAULT NULL,
  `idReserva` int(11) DEFAULT NULL,
  `fechaCreacion` datetime DEFAULT current_timestamp(),
  `usuarioCreacion` varchar(50) NOT NULL,
  `fechaModificacion` datetime DEFAULT NULL,
  `usuarioModificacion` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`idPedido`),
  KEY `fk_pedido_mesa` (`idMesa`),
  KEY `fk_pedido_usuario` (`idUsuario`),
  KEY `fk_pedido_reserva` (`idReserva`),
  CONSTRAINT `fk_pedido_mesa` FOREIGN KEY (`idMesa`) REFERENCES `mesa` (`idMesa`) ON UPDATE CASCADE,
  CONSTRAINT `fk_pedido_reserva` FOREIGN KEY (`idReserva`) REFERENCES `reserva` (`idReserva`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_pedido_usuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `producto`
--

DROP TABLE IF EXISTS `producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto` (
  `idProducto` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) DEFAULT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `precioVenta` decimal(10,2) NOT NULL,
  `estado` varchar(255) DEFAULT NULL,
  `idCategoria` int(11) NOT NULL,
  `idMenu` int(11) DEFAULT NULL,
  `imagenUrl` varchar(255) DEFAULT NULL,
  `esBebida` tinyint(1) NOT NULL DEFAULT 0,
  `stockMinimo` int(11) NOT NULL DEFAULT 0,
  `fechaCreacion` datetime DEFAULT current_timestamp(),
  `usuarioCreacion` varchar(50) DEFAULT 'Sistema',
  `fechaModificacion` datetime DEFAULT NULL,
  `usuarioModificacion` varchar(50) DEFAULT NULL,
  `es_bebida` bit(1) DEFAULT NULL,
  `imagen_url` varchar(255) DEFAULT NULL,
  `precio_venta` decimal(38,2) DEFAULT NULL,
  `stock_minimo` int(11) DEFAULT NULL,
  `id_categoria` int(11) DEFAULT NULL,
  PRIMARY KEY (`idProducto`),
  KEY `fk_producto_categoria` (`idCategoria`),
  KEY `fk_producto_menu` (`idMenu`),
  CONSTRAINT `fk_producto_categoria` FOREIGN KEY (`idCategoria`) REFERENCES `categoria` (`idCategoria`) ON UPDATE CASCADE,
  CONSTRAINT `fk_producto_menu` FOREIGN KEY (`idMenu`) REFERENCES `menu` (`idMenu`) ON UPDATE CASCADE,
  CONSTRAINT `chk_producto_precio` CHECK (`precioVenta` > 0)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `productoproveedor`
--

DROP TABLE IF EXISTS `productoproveedor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `productoproveedor` (
  `idProducto` int(11) NOT NULL,
  `idProveedor` int(11) NOT NULL,
  `precioCompra` decimal(10,2) NOT NULL,
  `plazoEntrega` int(11) DEFAULT NULL,
  `estado` enum('activo','inactivo') NOT NULL DEFAULT 'activo',
  `fechaCreacion` datetime DEFAULT current_timestamp(),
  `usuarioCreacion` varchar(50) NOT NULL,
  PRIMARY KEY (`idProducto`,`idProveedor`),
  KEY `fk_prodprov_proveedor` (`idProveedor`),
  CONSTRAINT `fk_prodprov_producto` FOREIGN KEY (`idProducto`) REFERENCES `producto` (`idProducto`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_prodprov_proveedor` FOREIGN KEY (`idProveedor`) REFERENCES `proveedor` (`idProveedor`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_prodprov_precio` CHECK (`precioCompra` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `proveedor`
--

DROP TABLE IF EXISTS `proveedor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `proveedor` (
  `idProveedor` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `contacto` varchar(100) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `correo` varchar(100) DEFAULT NULL,
  `direccion` varchar(200) DEFAULT NULL,
  `estado` enum('activo','inactivo') NOT NULL DEFAULT 'activo',
  `fechaCreacion` datetime DEFAULT current_timestamp(),
  `usuarioCreacion` varchar(50) NOT NULL,
  `fechaModificacion` datetime DEFAULT NULL,
  `usuarioModificacion` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`idProveedor`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reserva`
--

DROP TABLE IF EXISTS `reserva`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reserva` (
  `idReserva` int(11) NOT NULL AUTO_INCREMENT,
  `fechaReserva` date NOT NULL,
  `horaReserva` time NOT NULL,
  `numeroPersonas` int(11) NOT NULL,
  `estado` enum('confirmada','cancelada','completada') NOT NULL DEFAULT 'confirmada',
  `observaciones` varchar(255) DEFAULT NULL,
  `idCliente` int(11) NOT NULL,
  `idMesa` int(11) NOT NULL,
  `fechaCreacion` datetime DEFAULT current_timestamp(),
  `usuarioCreacion` varchar(50) NOT NULL,
  `fechaModificacion` datetime DEFAULT NULL,
  `usuarioModificacion` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`idReserva`),
  KEY `fk_reserva_cliente` (`idCliente`),
  KEY `fk_reserva_mesa` (`idMesa`),
  CONSTRAINT `fk_reserva_cliente` FOREIGN KEY (`idCliente`) REFERENCES `cliente` (`idCliente`) ON UPDATE CASCADE,
  CONSTRAINT `fk_reserva_mesa` FOREIGN KEY (`idMesa`) REFERENCES `mesa` (`idMesa`) ON UPDATE CASCADE,
  CONSTRAINT `chk_reserva_personas` CHECK (`numeroPersonas` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `idUsuario` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `apellido` varchar(50) NOT NULL,
  `rol` enum('mesero','cajero','administrador','cocinero','bartender') NOT NULL,
  `usuarioLogin` varchar(50) NOT NULL,
  `contrasena` varchar(255) NOT NULL,
  `estado` enum('activo','inactivo') NOT NULL DEFAULT 'activo',
  `fechaCreacion` datetime DEFAULT current_timestamp(),
  `usuarioCreacion` varchar(50) NOT NULL,
  `fechaModificacion` datetime DEFAULT NULL,
  `usuarioModificacion` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`idUsuario`),
  UNIQUE KEY `idx_usuario_login` (`usuarioLogin`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-06 17:46:25
