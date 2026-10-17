DROP DATABASE IF EXISTS BD_Almacen;
CREATE DATABASE IF NOT EXISTS BD_Almacen;
USE BD_Almacen;

-- ═══════════
-- TABLAS BASE
-- ═══════════

CREATE TABLE TiposUsuario (
    idTipo INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(100) NOT NULL
);

CREATE TABLE Usuarios (
    idUsuario INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50)  NOT NULL,
    clave VARCHAR(100) NOT NULL,
    nombres varchar(50),
    apellidos varchar(50),
    idTipo INT,
    CONSTRAINT fk_usuario_tipo FOREIGN KEY (idTipo) REFERENCES TiposUsuario (idTipo)
);

CREATE TABLE Proveedor (
    id_proveedor INT PRIMARY KEY AUTO_INCREMENT,
    ruc VARCHAR(11) UNIQUE NOT NULL,
    razon_social VARCHAR(150) NOT NULL,
    telefono VARCHAR(15),
    estado INT DEFAULT 1
);

CREATE TABLE Cliente (
    id_cliente INT PRIMARY KEY AUTO_INCREMENT,
    dni VARCHAR(9) UNIQUE NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    telefono VARCHAR(9)
);

-- TABLA PRODUCTO AJUSTADA PARA EL CÓDIGO JAVA
CREATE TABLE Producto (
    id_producto INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(20) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(200) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL,
    id_proveedor INT NOT NULL,
    CONSTRAINT fk_prod_prov FOREIGN KEY (id_proveedor) REFERENCES Proveedor(id_proveedor)
);

CREATE TABLE Venta (
    id_venta INT PRIMARY KEY AUTO_INCREMENT,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    idUsuario INT NOT NULL,
    id_cliente INT NOT NULL,
    total DECIMAL(10,2) DEFAULT 0.00,
    CONSTRAINT fk_venta_user FOREIGN KEY (idUsuario) REFERENCES Usuarios(idUsuario),
    CONSTRAINT fk_venta_cliente FOREIGN KEY (id_cliente) REFERENCES Cliente(id_cliente)
);

CREATE TABLE DetalleVenta (
    id_detalle INT PRIMARY KEY AUTO_INCREMENT,
    id_venta INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_detalle_venta FOREIGN KEY (id_venta) REFERENCES Venta(id_venta),
    CONSTRAINT fk_detalle_prod FOREIGN KEY (id_producto) REFERENCES Producto(id_producto)
);

CREATE TABLE Movimientos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(50),
    descripcion VARCHAR(255),
    fecha DATETIME,
    usuario VARCHAR(100)
);
-- ═══════════════
-- DATOS DE PRUEBA
-- ═══════════════

INSERT INTO TiposUsuario (descripcion) 
VALUES 
('Administrador'),
('Vendedor'),
('Almacenero');

INSERT INTO Usuarios (username, clave, idTipo) 
VALUES 
('jesusjuan', '123456', 1),
('marcovillarruel','123456',1);

INSERT INTO Proveedor (ruc, razon_social, telefono, estado) 
VALUES 
('20100100101', 'Distribuidora Automotriz S.A.', '987654321', 1),
('20100100102', 'Repuestos Lima Norte', '987654322', 1),
('20100100103', 'Autopartes El Rápido', '987654323', 1),
('20100100104', 'Importaciones Toyota SAC', '987654324', 1),
('20100100105', 'Frenos y Motores Perú', '987654325', 1),
('20100100106', 'Rodamientos Industriales', '987654326', 1),
('20100100107', 'Lubricantes Castro', '987654327', 1),
('20100100108', 'Baterías Enerjet SAC', '987654328', 1),
('20100100109', 'Radiadores Union', '987654329', 1),
('20100100110', 'Amortiguadores Gabriel', '987654330', 1);

INSERT INTO Cliente (dni, nombre, apellido, telefono) 
VALUES  
('70712586', 'Juan', 'Perez', '999888777'), 
('70712545', 'Maria', 'Lopez', '999444555'), 
('70254136', 'Pedro', 'Castillo', '98995234'),
('70712587', 'Ana', 'Garcia', '999444745'), 
('77712586', 'Luis', 'Torres', '999447888'), 
('75768590', 'Elena', 'Castro', '978546213'),
('70712552', 'Hugo', 'Silva', '999444666');

INSERT INTO Producto (codigo, nombre, descripcion, precio, stock, id_proveedor)
VALUES 
('PART-001', 'Pastillas de Freno', 'Pastillas de freno para frenos de disco', 85.50, 148, 1),
('PART-002', 'Filtro de Aire', 'Filtro de aire para motor', 45.00, 95, 2),
('PART-003', 'Batería 12V', 'Batería automotriz 12V 60Ah', 320.00, 32, 8),
('PART-004', 'Amortiguador Frontal', 'Amortiguador para suspensión frontal', 180.50, 18, 10);

INSERT INTO Venta (idUsuario, id_cliente, total) 
VALUES 
(1, 2, 410.50),
(2, 3, 320.00),
(1, 1, 85.50);

INSERT INTO DetalleVenta (id_venta, id_producto, cantidad, precio_unitario, total) 
VALUES 
(1, 2, 2, 45.00, 90.00),
(1, 3, 1, 320.50, 320.50),
(2, 3, 1, 320.00, 320.00),
(3, 1, 1, 85.50, 85.50);

-- ═══════════════════════════
-- STORED PROCEDURES - CLIENTE
-- ═══════════════════════════

-- Listar Cliente
DELIMITER //
CREATE PROCEDURE sp_listarCliente()
BEGIN
    SELECT id_cliente, dni, nombre, apellido, telefono FROM Cliente;
END //
DELIMITER ;

-- Registrar Cliente
DELIMITER //
CREATE PROCEDURE sp_registrarCliente(
    IN p_dni VARCHAR(9),
    IN p_nombre VARCHAR(50),
    IN p_apellido VARCHAR(50),
    IN p_telf VARCHAR(9)
)
BEGIN
    INSERT INTO Cliente(dni, nombre, apellido, telefono)
    VALUES(p_dni, p_nombre, p_apellido, p_telf);
END //
DELIMITER ;

-- Obtener Cliente
DELIMITER //
CREATE PROCEDURE sp_obtenerCliente(IN p_id INT)
BEGIN
    SELECT id_cliente, dni, nombre, apellido, telefono 
    FROM Cliente 
    WHERE id_cliente = p_id;
END //
DELIMITER ;

-- Actualizar Cliente
DELIMITER //
CREATE PROCEDURE sp_actualizarCliente(
    IN p_id INT,
    IN p_dni VARCHAR(9),
    IN p_nombre VARCHAR(50),
    IN p_apellido VARCHAR(50),
    IN p_telf VARCHAR(9)
)
BEGIN
    UPDATE Cliente
    SET dni = p_dni, nombre = p_nombre, apellido = p_apellido, telefono = p_telf
    WHERE id_cliente = p_id;
END //
DELIMITER ;

-- Eliminar Cliente
DELIMITER //
CREATE PROCEDURE sp_eliminarCliente(IN p_id INT)
BEGIN
    DELETE FROM Cliente WHERE id_cliente = p_id;
END //
DELIMITER ;

-- Buscar Cliente By Nombre
DELIMITER //
CREATE PROCEDURE sp_buscarClienteByNombre(IN p_texto VARCHAR(100))
BEGIN
    SELECT id_cliente, dni, nombre, apellido, telefono 
    FROM Cliente
    WHERE nombre LIKE CONCAT('%', p_texto, '%') 
       OR apellido LIKE CONCAT('%', p_texto, '%');
END //
DELIMITER ;

-- ═════════════════════════════
-- STORED PROCEDURES - PROVEEDOR
-- ═════════════════════════════

-- Listar Proveedor
DELIMITER //
CREATE PROCEDURE sp_listarProveedor()
BEGIN
    SELECT id_proveedor, ruc, razon_social, telefono, estado 
    FROM Proveedor;
END //
DELIMITER ;

-- Registrar Proveedor
DELIMITER //
CREATE PROCEDURE sp_registrarProveedor(
    IN p_ruc VARCHAR(11),
    IN p_razon_social VARCHAR(150),
    IN p_telefono VARCHAR(15),
    IN p_estado INT
)
BEGIN
    INSERT INTO Proveedor(ruc, razon_social, telefono, estado)
    VALUES(p_ruc, p_razon_social, p_telefono, p_estado);
END //
DELIMITER ;

-- Obtener Proveedor
DELIMITER //
CREATE PROCEDURE sp_obtenerProveedor(IN p_id INT)
BEGIN
    SELECT id_proveedor, ruc, razon_social, telefono, estado
    FROM Proveedor
    WHERE id_proveedor = p_id;
END //
DELIMITER ;

-- Actualizar Proveedor
DELIMITER //
CREATE PROCEDURE sp_actualizarProveedor(
    IN p_id INT,
    IN p_ruc VARCHAR(11),
    IN p_razon_social VARCHAR(150),
    IN p_telefono VARCHAR(15),
    IN p_estado INT
)
BEGIN
    UPDATE Proveedor
    SET ruc = p_ruc, razon_social = p_razon_social, telefono = p_telefono, estado = p_estado
    WHERE id_proveedor = p_id;
END //
DELIMITER ;

-- Eliminar Proveedor
DELIMITER //
CREATE PROCEDURE sp_eliminarProveedor(IN p_id INT)
BEGIN
    DELETE FROM Proveedor WHERE id_proveedor = p_id;
END //
DELIMITER ;

-- Buscar Proveedor
DELIMITER //
CREATE PROCEDURE sp_buscarProveedor(IN p_ruc VARCHAR(11))
BEGIN
    SELECT id_proveedor, ruc, razon_social, telefono, estado 
    FROM Proveedor
    WHERE ruc LIKE CONCAT('%', p_ruc, '%');
END //
DELIMITER ;

-- Buscar Proveedor By Nombre
DELIMITER //
CREATE PROCEDURE sp_buscarProveedorByNombre(IN p_texto VARCHAR(150))
BEGIN
    SELECT id_proveedor, ruc, razon_social, telefono, estado 
    FROM Proveedor
    WHERE razon_social LIKE CONCAT('%', p_texto, '%');
END //
DELIMITER ;

-- ════════════════════════════
-- STORED PROCEDURES - PRODUCTO
-- ════════════════════════════

-- Listar Producto
DELIMITER //
CREATE PROCEDURE sp_listarProducto()
BEGIN
    SELECT p.id_producto, p.codigo, p.nombre, p.descripcion, p.precio, 
           p.stock, p.id_proveedor, prov.razon_social
    FROM Producto p
    INNER JOIN Proveedor prov ON p.id_proveedor = prov.id_proveedor;
END //
DELIMITER ;

-- Registrar Producto
DELIMITER //
CREATE PROCEDURE sp_registrarProducto(
    IN p_codigo VARCHAR(20),
    IN p_nombre VARCHAR(100),
    IN p_descripcion VARCHAR(200),
    IN p_precio DECIMAL(10,2),
    IN p_stock INT,
    IN p_id_proveedor INT
)
BEGIN
    INSERT INTO Producto(codigo, nombre, descripcion, precio, stock, id_proveedor)
    VALUES(p_codigo, p_nombre, p_descripcion, p_precio, p_stock, p_id_proveedor);
END //
DELIMITER ;

-- Obtener Producto
DELIMITER //
CREATE PROCEDURE sp_obtenerProducto(IN p_id INT)
BEGIN
    SELECT p.id_producto, p.codigo, p.nombre, p.descripcion, p.precio, 
           p.stock, p.id_proveedor, prov.razon_social
    FROM Producto p
    INNER JOIN Proveedor prov ON p.id_proveedor = prov.id_proveedor
    WHERE p.id_producto = p_id;
END //
DELIMITER ;

-- Actualizar Producto
DELIMITER //
CREATE PROCEDURE sp_actualizarProducto(
    IN p_id INT,
    IN p_codigo VARCHAR(20),
    IN p_nombre VARCHAR(100),
    IN p_descripcion VARCHAR(200),
    IN p_precio DECIMAL(10,2),
    IN p_stock INT,
    IN p_id_proveedor INT
)
BEGIN
    UPDATE Producto
    SET codigo = p_codigo, nombre = p_nombre, descripcion = p_descripcion, 
        precio = p_precio, stock = p_stock, id_proveedor = p_id_proveedor
    WHERE id_producto = p_id;
END //
DELIMITER ;

-- Eliminar Producto
DELIMITER //
CREATE PROCEDURE sp_eliminarProducto(IN p_id INT)
BEGIN
    DELETE FROM Producto WHERE id_producto = p_id;
END //
DELIMITER ;

-- Buscar Producto
DELIMITER //
CREATE PROCEDURE sp_buscarProducto(IN p_codigo VARCHAR(20))
BEGIN
    SELECT p.id_producto, p.codigo, p.nombre, p.descripcion, p.precio, 
           p.stock, p.id_proveedor, prov.razon_social
    FROM Producto p
    INNER JOIN Proveedor prov ON p.id_proveedor = prov.id_proveedor
    WHERE p.codigo LIKE CONCAT('%', p_codigo, '%');
END //
DELIMITER ;

-- Buscar Producto By Nombre
DELIMITER //
CREATE PROCEDURE sp_buscarProductoByNombre(IN p_texto VARCHAR(100))
BEGIN
    SELECT p.id_producto, p.codigo, p.nombre, p.descripcion, p.precio, 
           p.stock, p.id_proveedor, prov.razon_social
    FROM Producto p
    INNER JOIN Proveedor prov ON p.id_proveedor = prov.id_proveedor
    WHERE p.nombre LIKE CONCAT('%', p_texto, '%')
       OR p.descripcion LIKE CONCAT('%', p_texto, '%');
END //
DELIMITER ;

-- ═════════════════════════════════
-- STORED PROCEDURES - REPORTE VENTA
-- ═════════════════════════════════

-- Reporte Detalle Venta
DELIMITER //
CREATE PROCEDURE sp_ReporteDetalleVenta(IN p_id_venta INT)
BEGIN
    SELECT 
        dv.id_detalle AS 'ITEM',
        p.nombre AS 'PRODUCTO',
        dv.cantidad AS 'CANTIDAD',
        dv.precio_unitario AS 'PRECIO UNIT.',
        dv.total AS 'TOTAL'
    FROM DetalleVenta dv
    INNER JOIN Producto p ON dv.id_producto = p.id_producto
    WHERE dv.id_venta = p_id_venta;
END //
DELIMITER ;

-- Reporte Ventas Por Fechas
DELIMITER //
CREATE PROCEDURE sp_ReporteVentasPorFechas(
    IN p_fecha_inicio DATE, 
    IN p_fecha_fin DATE
)
BEGIN
    SELECT 
        v.id_venta,
        v.fecha,
        c.nombre AS cliente,
        v.total
    FROM Venta v
    INNER JOIN Cliente c ON v.id_cliente = c.id_cliente
    WHERE DATE(v.fecha) BETWEEN p_fecha_inicio AND p_fecha_fin
    ORDER BY v.fecha ASC;
END //
DELIMITER ;

-- Contador Reporte Ventas
DELIMITER //
CREATE PROCEDURE sp_contarVentas()
BEGIN
    SELECT COUNT(*) FROM Venta ;
END //
DELIMITER ;

-- ═══════════════════════════════
-- STORED PROCEDURES - MOVIMIENTOS
-- ═══════════════════════════════

-- Registrar Movimientos
DELIMITER //
CREATE PROCEDURE sp_registrarMovimiento(
    IN p_tipo VARCHAR(50),
    IN p_descripcion VARCHAR(255),
    IN p_usuario VARCHAR(100)
)
BEGIN
    INSERT INTO Movimientos(tipo, descripcion, fecha, usuario)
    VALUES(p_tipo, p_descripcion, NOW(), p_usuario);
END //
DELIMITER ;

-- Listar Movimientos
DELIMITER //
CREATE PROCEDURE sp_listarMovimientos()
BEGIN
    SELECT 
        m.id,
        m.tipo,
        m.descripcion,
        m.fecha,
        m.usuario
    FROM Movimientos m
    ORDER BY m.fecha DESC;
END //
DELIMITER ;

-- Contar Movimiento
DELIMITER //
CREATE PROCEDURE sp_contarMovimientos()
BEGIN
    SELECT COUNT(*) FROM movimientos;
END //
DELIMITER ;

-- Querys
SELECT * FROM Usuarios;
SELECT * FROM TiposUsuario;
SELECT * FROM Cliente;
SELECT * FROM Producto;
SELECT * FROM Proveedor;
SELECT * FROM Venta;
SELECT * FROM DetalleVenta;
SELECT * FROM Movimientos;