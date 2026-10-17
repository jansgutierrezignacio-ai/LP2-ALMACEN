-- ═════════════════════════════════════════════════════════════════════════════
--  BASE DE DATOS : BD_ALMACEN
--  PROYECTO      : Comercial Autopartes SAC  (LP2 - Cibertec)
--  MOTOR         : MySQL 8 / 9  (InnoDB, utf8mb4)
--  AUTOR         : Reconstrucción completa a partir de
--                  - sql/BD_Almacen(ProyectoFinal)-ContarReporteVenta.sql (script original)
--                  - código Java real (procesos/Gestion*.java, util/MysqlConexion.java,
--                    resources/application.properties)
--  ─────────────────────────────────────────────────────────────────────────────
--  NOTAS DE LA RECONSTRUCCIÓN (cambios respecto del script original):
--   1. Nombre ÚNICO de la base de datos: BD_ALMACEN. Antes había inconsistencia
--      (incongruencia B02): el código usaba "LP2_ALMACEN" y el script original
--      "BD_Almacen". Se unificó en BD_ALMACEN y se actualizaron el código
--      (MysqlConexion.java, application.properties) y el informe.
--   2. Charset utf8mb4 / collation utf8mb4_unicode_ci para admitir los acentos
--      que ya existen en los datos ("Batería", "El Rápido", etc.).
--   3. Se corrigió el error de mayúsculas de sp_contarMovimientos:
--      el original hacía "FROM movimientos" (minúsculas) y en Linux MySQL es
--      case-sensitive (lower_case_table_names=0) => la tabla real es "Movimientos".
--   4. Se agregó el procedimiento faltante sp_RegistrarUsuario(...) porque
--      GestionUsuario.crearUsuario() lo invoca y NO existía (incongruencia B12).
--   El resto de tablas, columnas, tipos, claves y procedimientos se mantiene
--   fiel al script original y al uso real del código.
-- ═════════════════════════════════════════════════════════════════════════════

DROP DATABASE IF EXISTS BD_ALMACEN;
CREATE DATABASE BD_ALMACEN
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE BD_ALMACEN;

----------------------------------------------------------------------------------------------------
--                                              Tablas
----------------------------------------------------------------------------------------------------

-- Tipos de usuarios (roles) -----------------------------------------------------------------------
CREATE TABLE TiposUsuario (
    idTipo      INT          NOT NULL AUTO_INCREMENT,
    descripcion VARCHAR(100) NOT NULL,
    PRIMARY KEY (idTipo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Usuarios del sistema ----------------------------------------------------------------------------
CREATE TABLE Usuarios (
    idUsuario INT          NOT NULL AUTO_INCREMENT,
    username  VARCHAR(50)  NOT NULL,
    clave     VARCHAR(100) NOT NULL,
    nombres   VARCHAR(50)  NULL,
    apellidos VARCHAR(50)  NULL,
    idTipo    INT          NULL,
    PRIMARY KEY (idUsuario),
    CONSTRAINT fk_usuario_tipo FOREIGN KEY (idTipo) REFERENCES TiposUsuario (idTipo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Proveedores -------------------------------------------------------------------------------------
CREATE TABLE Proveedor (
    id_proveedor INT          NOT NULL AUTO_INCREMENT,
    ruc          VARCHAR(11)  NOT NULL,
    razon_social VARCHAR(150) NOT NULL,
    telefono     VARCHAR(15)  NULL,
    estado       INT          DEFAULT 1,
    PRIMARY KEY (id_proveedor),
    UNIQUE KEY uk_proveedor_ruc (ruc)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Clientes ----------------------------------------------------------------------------------------
CREATE TABLE Cliente (
    id_cliente INT         NOT NULL AUTO_INCREMENT,
    dni        VARCHAR(9)  NOT NULL,
    nombre     VARCHAR(50) NOT NULL,
    apellido   VARCHAR(50) NOT NULL,
    telefono   VARCHAR(9)  NULL,
    PRIMARY KEY (id_cliente),
    UNIQUE KEY uk_cliente_dni (dni)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Productos (autopartes) --------------------------------------------------------------------------
CREATE TABLE Producto (
    id_producto  INT           NOT NULL AUTO_INCREMENT,
    codigo       VARCHAR(20)   NOT NULL,
    nombre       VARCHAR(100)  NOT NULL,
    descripcion  VARCHAR(200)  NOT NULL,
    precio       DECIMAL(10,2) NOT NULL,
    stock        INT           NOT NULL,
    id_proveedor INT           NOT NULL,
    PRIMARY KEY (id_producto),
    UNIQUE KEY uk_producto_codigo (codigo),
    CONSTRAINT fk_prod_prov FOREIGN KEY (id_proveedor) REFERENCES Proveedor (id_proveedor)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Ventas (cabecera) -------------------------------------------------------------------------------
CREATE TABLE Venta (
    id_venta   INT           NOT NULL AUTO_INCREMENT,
    fecha      DATETIME      DEFAULT CURRENT_TIMESTAMP,
    idUsuario  INT           NOT NULL,
    id_cliente INT           NOT NULL,
    total      DECIMAL(10,2) DEFAULT 0.00,
    PRIMARY KEY (id_venta),
    CONSTRAINT fk_venta_user    FOREIGN KEY (idUsuario)  REFERENCES Usuarios (idUsuario),
    CONSTRAINT fk_venta_cliente FOREIGN KEY (id_cliente) REFERENCES Cliente (id_cliente)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Detalle de venta --------------------------------------------------------------------------------
CREATE TABLE DetalleVenta (
    id_detalle      INT           NOT NULL AUTO_INCREMENT,
    id_venta        INT           NOT NULL,
    id_producto     INT           NOT NULL,
    cantidad        INT           NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    total           DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id_detalle),
    CONSTRAINT fk_detalle_venta FOREIGN KEY (id_venta)    REFERENCES Venta (id_venta),
    CONSTRAINT fk_detalle_prod  FOREIGN KEY (id_producto) REFERENCES Producto (id_producto)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Movimientos (auditoria de operaciones) ----------------------------------------------------------
CREATE TABLE Movimientos (
    id          INT          NOT NULL AUTO_INCREMENT,
    tipo        VARCHAR(50)  NULL,
    descripcion VARCHAR(255) NULL,
    fecha       DATETIME     NULL,
    usuario     VARCHAR(100) NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


----------------------------------------------------------------------------------------------------
--                                          Datos semilla
----------------------------------------------------------------------------------------------------


-- Roles -------------------------------------------------------------------------------------------
INSERT INTO TiposUsuario (descripcion) VALUES
    ('Administrador'),
    ('Vendedor'),
    ('Almacenero');

-- Usuarios ----------------------------------------------------------------------------------------
-- Originales del script (ambos Administrador)
INSERT INTO Usuarios (username, clave, idTipo) VALUES
    ('jesusjuan',     '123456', 1),
    ('marcovillarruel','123456', 1);

-- Usuarios de prueba: uno por rol, para probar el inicio de sesión y el
-- comportamiento por tipo de usuario (clave 123456).
INSERT INTO Usuarios (username, clave, idTipo, nombres, apellidos) VALUES
    ('admin',      '123456', 1, 'Administrador', 'General'),
    ('vendedor',   '123456', 2, 'Vendedor',      'General'),
    ('almacenero', '123456', 3, 'Almacenero',    'General');

-- Proveedores -------------------------------------------------------------------------------------
INSERT INTO Proveedor (ruc, razon_social, telefono, estado) VALUES
    ('20100100101', 'Distribuidora Automotriz S.A.', '987654321', 1),
    ('20100100102', 'Repuestos Lima Norte',          '987654322', 1),
    ('20100100103', 'Autopartes El Rápido',          '987654323', 1),
    ('20100100104', 'Importaciones Toyota SAC',      '987654324', 1),
    ('20100100105', 'Frenos y Motores Perú',         '987654325', 1),
    ('20100100106', 'Rodamientos Industriales',      '987654326', 1),
    ('20100100107', 'Lubricantes Castro',            '987654327', 1),
    ('20100100108', 'Baterías Enerjet SAC',          '987654328', 1),
    ('20100100109', 'Radiadores Union',              '987654329', 1),
    ('20100100110', 'Amortiguadores Gabriel',        '987654330', 1);

-- Clietes -----------------------------------------------------------------------------------------
INSERT INTO Cliente (dni, nombre, apellido, telefono) VALUES
    ('70712586', 'Juan',  'Perez',   '999888777'),
    ('70712545', 'Maria', 'Lopez',   '999444555'),
    ('70254136', 'Pedro', 'Castillo','98995234'),
    ('70712587', 'Ana',   'Garcia',  '999444745'),
    ('77712586', 'Luis',  'Torres',  '999447888'),
    ('75768590', 'Elena', 'Castro',  '978546213'),
    ('70712552', 'Hugo',  'Silva',   '999444666');

-- Productos ---------------------------------------------------------------------------------------
INSERT INTO Producto (codigo, nombre, descripcion, precio, stock, id_proveedor) VALUES
    ('PART-001', 'Pastillas de Freno',     'Pastillas de freno para frenos de disco', 85.50,  148, 1),
    ('PART-002', 'Filtro de Aire',         'Filtro de aire para motor',               45.00,   95, 2),
    ('PART-003', 'Batería 12V',            'Batería automotriz 12V 60Ah',            320.00,   32, 8),
    ('PART-004', 'Amortiguador Frontal',   'Amortiguador para suspensión frontal',   180.50,   18, 10);

-- Ventas ------------------------------------------------------------------------------------------
INSERT INTO Venta (idUsuario, id_cliente, total) VALUES
    (1, 2, 410.50),
    (2, 3, 320.00),
    (1, 1,  85.50);

-- Detalle de venta --------------------------------------------------------------------------------
INSERT INTO DetalleVenta (id_venta, id_producto, cantidad, precio_unitario, total) VALUES
    (1, 2, 2,  45.00,  90.00),
    (1, 3, 1, 320.50, 320.50),
    (2, 3, 1, 320.00, 320.00),
    (3, 1, 1,  85.50,  85.50);

-- Movimientos (de ejemplo) ------------------------------------------------------------------------
INSERT INTO Movimientos (tipo, descripcion, fecha, usuario) VALUES
    ('Registro',  'Registro inicial del producto PART-001', NOW(), 'admin'),
    ('Venta',     'Venta registrada con id 1',              NOW(), 'vendedor');


----------------------------------------------------------------------------------------------------
--                                    Procedimientos almacenados
----------------------------------------------------------------------------------------------------

DELIMITER $$

-- Cliente -----------------------------------------------------------------------------------------

DROP PROCEDURE IF EXISTS sp_listarCliente $$
CREATE PROCEDURE sp_listarCliente()
BEGIN
    SELECT id_cliente, dni, nombre, apellido, telefono FROM Cliente;
END $$

DROP PROCEDURE IF EXISTS sp_registrarCliente $$
CREATE PROCEDURE sp_registrarCliente(
    IN p_dni      VARCHAR(9),
    IN p_nombre   VARCHAR(50),
    IN p_apellido VARCHAR(50),
    IN p_telf     VARCHAR(9)
)
BEGIN
    INSERT INTO Cliente(dni, nombre, apellido, telefono)
    VALUES(p_dni, p_nombre, p_apellido, p_telf);
END $$

DROP PROCEDURE IF EXISTS sp_obtenerCliente $$
CREATE PROCEDURE sp_obtenerCliente(IN p_id INT)
BEGIN
    SELECT id_cliente, dni, nombre, apellido, telefono
    FROM Cliente
    WHERE id_cliente = p_id;
END $$

DROP PROCEDURE IF EXISTS sp_actualizarCliente $$
CREATE PROCEDURE sp_actualizarCliente(
    IN p_id       INT,
    IN p_dni      VARCHAR(9),
    IN p_nombre   VARCHAR(50),
    IN p_apellido VARCHAR(50),
    IN p_telf     VARCHAR(9)
)
BEGIN
    UPDATE Cliente
    SET dni = p_dni, nombre = p_nombre, apellido = p_apellido, telefono = p_telf
    WHERE id_cliente = p_id;
END $$

DROP PROCEDURE IF EXISTS sp_eliminarCliente $$
CREATE PROCEDURE sp_eliminarCliente(IN p_id INT)
BEGIN
    DELETE FROM Cliente WHERE id_cliente = p_id;
END $$

DROP PROCEDURE IF EXISTS sp_buscarClienteByNombre $$
CREATE PROCEDURE sp_buscarClienteByNombre(IN p_texto VARCHAR(100))
BEGIN
    SELECT id_cliente, dni, nombre, apellido, telefono
    FROM Cliente
    WHERE nombre   LIKE CONCAT('%', p_texto, '%')
       OR apellido LIKE CONCAT('%', p_texto, '%');
END $$


-- Proveedor ---------------------------------------------------------------------------------------

DROP PROCEDURE IF EXISTS sp_listarProveedor $$
CREATE PROCEDURE sp_listarProveedor()
BEGIN
    SELECT id_proveedor, ruc, razon_social, telefono, estado
    FROM Proveedor;
END $$

DROP PROCEDURE IF EXISTS sp_registrarProveedor $$
CREATE PROCEDURE sp_registrarProveedor(
    IN p_ruc          VARCHAR(11),
    IN p_razon_social VARCHAR(150),
    IN p_telefono     VARCHAR(15),
    IN p_estado       INT
)
BEGIN
    INSERT INTO Proveedor(ruc, razon_social, telefono, estado)
    VALUES(p_ruc, p_razon_social, p_telefono, p_estado);
END $$

DROP PROCEDURE IF EXISTS sp_obtenerProveedor $$
CREATE PROCEDURE sp_obtenerProveedor(IN p_id INT)
BEGIN
    SELECT id_proveedor, ruc, razon_social, telefono, estado
    FROM Proveedor
    WHERE id_proveedor = p_id;
END $$

DROP PROCEDURE IF EXISTS sp_actualizarProveedor $$
CREATE PROCEDURE sp_actualizarProveedor(
    IN p_id           INT,
    IN p_ruc          VARCHAR(11),
    IN p_razon_social VARCHAR(150),
    IN p_telefono     VARCHAR(15),
    IN p_estado       INT
)
BEGIN
    UPDATE Proveedor
    SET ruc = p_ruc, razon_social = p_razon_social, telefono = p_telefono, estado = p_estado
    WHERE id_proveedor = p_id;
END $$

DROP PROCEDURE IF EXISTS sp_eliminarProveedor $$
CREATE PROCEDURE sp_eliminarProveedor(IN p_id INT)
BEGIN
    DELETE FROM Proveedor WHERE id_proveedor = p_id;
END $$

DROP PROCEDURE IF EXISTS sp_buscarProveedor $$
CREATE PROCEDURE sp_buscarProveedor(IN p_ruc VARCHAR(11))
BEGIN
    SELECT id_proveedor, ruc, razon_social, telefono, estado
    FROM Proveedor
    WHERE ruc LIKE CONCAT('%', p_ruc, '%');
END $$

DROP PROCEDURE IF EXISTS sp_buscarProveedorByNombre $$
CREATE PROCEDURE sp_buscarProveedorByNombre(IN p_texto VARCHAR(150))
BEGIN
    SELECT id_proveedor, ruc, razon_social, telefono, estado
    FROM Proveedor
    WHERE razon_social LIKE CONCAT('%', p_texto, '%');
END $$

-- Producto ----------------------------------------------------------------------------------------

DROP PROCEDURE IF EXISTS sp_listarProducto $$
CREATE PROCEDURE sp_listarProducto()
BEGIN
    SELECT p.id_producto, p.codigo, p.nombre, p.descripcion, p.precio,
           p.stock, p.id_proveedor, prov.razon_social
    FROM Producto p
    INNER JOIN Proveedor prov ON p.id_proveedor = prov.id_proveedor;
END $$

DROP PROCEDURE IF EXISTS sp_registrarProducto $$
CREATE PROCEDURE sp_registrarProducto(
    IN p_codigo       VARCHAR(20),
    IN p_nombre       VARCHAR(100),
    IN p_descripcion  VARCHAR(200),
    IN p_precio       DECIMAL(10,2),
    IN p_stock        INT,
    IN p_id_proveedor INT
)
BEGIN
    INSERT INTO Producto(codigo, nombre, descripcion, precio, stock, id_proveedor)
    VALUES(p_codigo, p_nombre, p_descripcion, p_precio, p_stock, p_id_proveedor);
END $$

DROP PROCEDURE IF EXISTS sp_obtenerProducto $$
CREATE PROCEDURE sp_obtenerProducto(IN p_id INT)
BEGIN
    SELECT p.id_producto, p.codigo, p.nombre, p.descripcion, p.precio,
           p.stock, p.id_proveedor, prov.razon_social
    FROM Producto p
    INNER JOIN Proveedor prov ON p.id_proveedor = prov.id_proveedor
    WHERE p.id_producto = p_id;
END $$

DROP PROCEDURE IF EXISTS sp_actualizarProducto $$
CREATE PROCEDURE sp_actualizarProducto(
    IN p_id           INT,
    IN p_codigo       VARCHAR(20),
    IN p_nombre       VARCHAR(100),
    IN p_descripcion  VARCHAR(200),
    IN p_precio       DECIMAL(10,2),
    IN p_stock        INT,
    IN p_id_proveedor INT
)
BEGIN
    UPDATE Producto
    SET codigo = p_codigo, nombre = p_nombre, descripcion = p_descripcion,
        precio = p_precio, stock = p_stock, id_proveedor = p_id_proveedor
    WHERE id_producto = p_id;
END $$

DROP PROCEDURE IF EXISTS sp_eliminarProducto $$
CREATE PROCEDURE sp_eliminarProducto(IN p_id INT)
BEGIN
    DELETE FROM Producto WHERE id_producto = p_id;
END $$

DROP PROCEDURE IF EXISTS sp_buscarProducto $$
CREATE PROCEDURE sp_buscarProducto(IN p_codigo VARCHAR(20))
BEGIN
    SELECT p.id_producto, p.codigo, p.nombre, p.descripcion, p.precio,
           p.stock, p.id_proveedor, prov.razon_social
    FROM Producto p
    INNER JOIN Proveedor prov ON p.id_proveedor = prov.id_proveedor
    WHERE p.codigo LIKE CONCAT('%', p_codigo, '%');
END $$

DROP PROCEDURE IF EXISTS sp_buscarProductoByNombre $$
CREATE PROCEDURE sp_buscarProductoByNombre(IN p_texto VARCHAR(100))
BEGIN
    SELECT p.id_producto, p.codigo, p.nombre, p.descripcion, p.precio,
           p.stock, p.id_proveedor, prov.razon_social
    FROM Producto p
    INNER JOIN Proveedor prov ON p.id_proveedor = prov.id_proveedor
    WHERE p.nombre      LIKE CONCAT('%', p_texto, '%')
       OR p.descripcion LIKE CONCAT('%', p_texto, '%');
END $$

-- Reportes de venta -------------------------------------------------------------------------------

DROP PROCEDURE IF EXISTS sp_ReporteDetalleVenta $$
CREATE PROCEDURE sp_ReporteDetalleVenta(IN p_id_venta INT)
BEGIN
    SELECT
        dv.id_detalle      AS 'ITEM',
        p.nombre           AS 'PRODUCTO',
        dv.cantidad        AS 'CANTIDAD',
        dv.precio_unitario AS 'PRECIO UNIT.',
        dv.total           AS 'TOTAL'
    FROM DetalleVenta dv
    INNER JOIN Producto p ON dv.id_producto = p.id_producto
    WHERE dv.id_venta = p_id_venta;
END $$

DROP PROCEDURE IF EXISTS sp_ReporteVentasPorFechas $$
CREATE PROCEDURE sp_ReporteVentasPorFechas(
    IN p_fecha_inicio DATE,
    IN p_fecha_fin    DATE
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
END $$

DROP PROCEDURE IF EXISTS sp_contarVentas $$
CREATE PROCEDURE sp_contarVentas()
BEGIN
    SELECT COUNT(*) FROM Venta;
END $$

-- Movimientos -------------------------------------------------------------------------------------

DROP PROCEDURE IF EXISTS sp_registrarMovimiento $$
CREATE PROCEDURE sp_registrarMovimiento(
    IN p_tipo        VARCHAR(50),
    IN p_descripcion VARCHAR(255),
    IN p_usuario     VARCHAR(100)
)
BEGIN
    INSERT INTO Movimientos(tipo, descripcion, fecha, usuario)
    VALUES(p_tipo, p_descripcion, NOW(), p_usuario);
END $$

DROP PROCEDURE IF EXISTS sp_listarMovimientos $$
CREATE PROCEDURE sp_listarMovimientos()
BEGIN
    SELECT m.id, m.tipo, m.descripcion, m.fecha, m.usuario
    FROM Movimientos m
    ORDER BY m.fecha DESC;
END $$

DROP PROCEDURE IF EXISTS sp_contarMovimientos $$
CREATE PROCEDURE sp_contarMovimientos()
BEGIN
    SELECT COUNT(*) FROM Movimientos;
END $$

-- Usuarios ----------------------------------------------------------------------------------------
-- PROCEDIMIENTO NUEVO: el código (GestionUsuario.crearUsuario) lo invoca pero
-- no existía en el script original (incongruencia B12).

DROP PROCEDURE IF EXISTS sp_RegistrarUsuario $$
CREATE PROCEDURE sp_RegistrarUsuario(
    IN p_username  VARCHAR(50),
    IN p_clave     VARCHAR(100),
    IN p_idTipo    INT,
    IN p_nombres   VARCHAR(50),
    IN p_apellidos VARCHAR(50)
)
BEGIN
    INSERT INTO Usuarios(username, clave, idTipo, nombres, apellidos)
    VALUES(p_username, p_clave, p_idTipo, p_nombres, p_apellidos);
END $$

DELIMITER ;

----------------------------------------------------------------------------------------------------
--                                          Verificaciones
----------------------------------------------------------------------------------------------------

SELECT 'TiposUsuario' AS tabla, COUNT(*) AS filas FROM TiposUsuario
    UNION ALL SELECT 'Usuarios',      COUNT(*) FROM Usuarios
    UNION ALL SELECT 'Proveedor',     COUNT(*) FROM Proveedor
    UNION ALL SELECT 'Cliente',       COUNT(*) FROM Cliente
    UNION ALL SELECT 'Producto',      COUNT(*) FROM Producto
    UNION ALL SELECT 'Venta',         COUNT(*) FROM Venta
    UNION ALL SELECT 'DetalleVenta',  COUNT(*) FROM DetalleVenta
    UNION ALL SELECT 'Movimientos',   COUNT(*) FROM Movimientos;

-- Procedimientos almacenados creados
SELECT ROUTINE_NAME AS procedimiento
FROM information_schema.ROUTINES
WHERE ROUTINE_SCHEMA = 'BD_ALMACEN' AND ROUTINE_TYPE = 'PROCEDURE'
ORDER BY ROUTINE_NAME;
