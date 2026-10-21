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

-- Forzar utf8mb4 en la sesión del cliente. Sin esto, al importar con el cliente
-- mysql (que arranca en latin1 si el locale no es UTF-8) los acentos del archivo
-- se reinterpretan byte a byte y quedan doblemente codificados:
--   "Batería" -> "BaterÃ­a"
-- Debe ir ANTES de crear/insertar para que los literales se lean bien.
SET NAMES utf8mb4;

DROP DATABASE IF EXISTS BD_ALMACEN;
CREATE DATABASE BD_ALMACEN
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE BD_ALMACEN;

-- ----------------------------------------------------------------------------------------------------
--                                              Tablas
-- ----------------------------------------------------------------------------------------------------

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


-- ----------------------------------------------------------------------------------------------------
--                                          Datos semilla
-- ----------------------------------------------------------------------------------------------------


-- Roles -------------------------------------------------------------------------------------------
INSERT INTO TiposUsuario (descripcion) VALUES
    ('Administrador'),
    ('Vendedor'),
    ('Almacenero');

-- Usuarios ----------------------------------------------------------------------------------------
-- Originales del script (ambos Administrador). Las claves se guardan como hash
-- BCrypt ($2a$10$..., 60 caracteres): la BD no almacena texto plano. La clave
-- real de los cinco usuarios sigue siendo 123456.
INSERT INTO Usuarios (username, clave, idTipo) VALUES
    ('jesusjuan',      '$2a$10$rEUtCY592nM0wvLS74gscOFXwemdSEWafzrWZq7fB7d3wTrfg4CHG', 1),
    ('marcovillarruel','$2a$10$71HA4WJIottlpYipY.UxuOG/Wgqvg.PsNd6NLAJpPQJcGk1HQH73q', 1);

-- Usuarios de prueba: uno por rol, para probar el inicio de sesión y el
-- comportamiento por tipo de usuario (clave 123456 cifrada con BCrypt).
INSERT INTO Usuarios (username, clave, idTipo, nombres, apellidos) VALUES
    ('admin',      '$2a$10$mSnSed5DIfRl7mdF2WUyseLShI.WkxHcVwGkafCD4XaBGP4u62cdO', 1, 'Administrador', 'General'),
    ('vendedor',   '$2a$10$xQHcDgIxcfAYOCveditE1eHgNHGpmhpwKUwYMyg5Rj068LCNtulnq', 2, 'Vendedor',      'General'),
    ('almacenero', '$2a$10$5hD5qqO0d6zlMPWWaQ/C..cbUzQOVGirQE4AF4UoEP7X9f4.o2M3u', 3, 'Almacenero',    'General');

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

-- Datos de prueba portados del equipo (agregar_BD.sql), adaptados a este esquema:
-- RUCs y teléfonos renumerados (+9) para no chocar con los 10 proveedores anteriores.
INSERT INTO Proveedor (ruc, razon_social, telefono, estado) VALUES
    ('20100100111', 'Importaciones y Repuestos El Sol S.A.C.',    '987654331', 1),
    ('20100100112', 'Autopartes y Motores del Perú S.R.L.',       '987654332', 1),
    ('20100100113', 'Distribuidora Frenos y Embragues E.I.R.L.',  '987654333', 1),
    ('20100100114', 'Comercializadora Automotriz Global S.A.',    '987654334', 1),
    ('20100100115', 'Sistemas de Suspensión del Norte S.A.C.',    '987654335', 1),
    ('20100100116', 'Lubricantes y Filtros Industriales S.R.L.',  '987654336', 1),
    ('20100100117', 'Repuestos Eléctricos Automotrices E.I.R.L.', '987654337', 1),
    ('20100100118', 'Multirepuestos y Baterías del Pacífico S.A.','987654338', 1),
    ('20100100119', 'Importadora de Neumáticos San Martín S.A.C.','987654339', 1),
    ('20100100120', 'Accesorios y Bujías del Sur S.R.L.',         '987654340', 1),
    ('20100100121', 'Distribuidora de Correas y Fajas E.I.R.L.',  '987654341', 1),
    ('20100100122', 'Tecnología Automotriz Integral S.A.',        '987654342', 1),
    ('20100100123', 'Sistemas de Inyección diésel S.A.C.',        '987654343', 1),
    ('20100100124', 'Comercial La Casa del Amortiguador S.R.L.',  '987654344', 1),
    ('20100100125', 'Importaciones Mecánicas del Perú E.I.R.L.',  '987654345', 1),
    ('20100100126', 'Soluciones en Escapes y Silenciadores S.A.', '987654346', 1),
    ('20100100127', 'Autopartes y Radiadores El Inka S.A.C.',     '987654347', 1),
    ('20100100128', 'Distribuidora de Iluminación Led Auto S.R.L.','987654348', 1),
    ('20100100129', 'Central de Empaquetaduras y Retenes E.I.R.L.','987654349', 1),
    ('20100100130', 'Consorcio Automotriz del Norte S.A.',        '987654350', 1);

-- Clietes -----------------------------------------------------------------------------------------
INSERT INTO Cliente (dni, nombre, apellido, telefono) VALUES
    ('70712586', 'Juan',  'Perez',   '999888777'),
    ('70712545', 'Maria', 'Lopez',   '999444555'),
    ('70254136', 'Pedro', 'Castillo','98995234'),
    ('70712587', 'Ana',   'Garcia',  '999444745'),
    ('77712586', 'Luis',  'Torres',  '999447888'),
    ('75768590', 'Elena', 'Castro',  '978546213'),
    ('70712552', 'Hugo',  'Silva',   '999444666');

-- Datos de prueba portados del equipo (agregar_BD.sql): 16 clientes adicionales.
INSERT INTO Cliente (dni, nombre, apellido, telefono) VALUES
    ('42158963', 'Carlos',  'Gomez',    '987654321'),
    ('71234567', 'Ana',     'Torres',   '912345678'),
    ('45892314', 'Luis',    'Ramirez',  '923456789'),
    ('73891245', 'Sofia',   'Fernandez','934567891'),
    ('40981273', 'Jorge',   'Castro',   '945678912'),
    ('72345689', 'Lucia',   'Mendoza',  '956789123'),
    ('41892374', 'Miguel',  'Rojas',    '967891234'),
    ('70983421', 'Valeria', 'Silva',    '978912345'),
    ('43125689', 'Daniel',  'Vargas',   '989123456'),
    ('74561283', 'Camila',  'Morales',  '990123457'),
    ('42893412', 'Fernando','Paredes',  '911234568'),
    ('71982345', 'Diana',   'Flores',   '922345679'),
    ('40128934', 'Ricardo', 'Espinoza', '933456780'),
    ('73451298', 'Gabriela','Chavez',   '944567891'),
    ('41567892', 'Rosa',    'Medina',   '955678902'),
    ('72893415', 'Jose',    'Huaman',   '966789013');

-- Productos ---------------------------------------------------------------------------------------
INSERT INTO Producto (codigo, nombre, descripcion, precio, stock, id_proveedor) VALUES
    ('PART-001', 'Pastillas de Freno',     'Pastillas de freno para frenos de disco', 85.50,  148, 1),
    ('PART-002', 'Filtro de Aire',         'Filtro de aire para motor',               45.00,   95, 2),
    ('PART-003', 'Batería 12V',            'Batería automotriz 12V 60Ah',            320.00,   32, 8),
    ('PART-004', 'Amortiguador Frontal',   'Amortiguador para suspensión frontal',   180.50,   18, 10);

-- Datos de prueba portados del equipo (agregar_BD.sql): 20 productos adicionales,
-- cada uno asociado a uno de los 20 proveedores nuevos (ids 11 a 30).
INSERT INTO Producto (codigo, nombre, descripcion, precio, stock, id_proveedor) VALUES
    ('PRD-101', 'Filtro de Aceite Sintético',   'Filtro de aceite para motores a gasolina y diésel de alto rendimiento',  35.50, 45, 11),
    ('PRD-102', 'Juego de Pastillas de Freno',  'Pastillas cerámicas delanteras con alta resistencia al calor',           120.00, 30, 12),
    ('PRD-103', 'Kit de Embrague Completo',     'Incluye plato, disco y rodaje de empuje para transmisión manual',        450.00, 15, 13),
    ('PRD-104', 'Bomba de Agua Multiválvula',   'Bomba de agua de aleación de aluminio para sistema de refrigeración',    185.00, 20, 14),
    ('PRD-105', 'Amortiguador Delantero Gas',   'Amortiguador presurizado con nitrógeno para mayor estabilidad',          210.00, 25, 15),
    ('PRD-106', 'Aceite Multigrado 10W-40 (4L)','Lubricante sintético para protección del motor contra desgaste',          115.00, 50, 16),
    ('PRD-107', 'Alternador 12V 90A',           'Alternador reforzado para sistema eléctrico automotriz',                  380.00, 12, 17),
    ('PRD-108', 'Batería Automotriz 12V 60Ah',  'Batería libre de mantenimiento de libre descarga lenta',                  320.00, 18, 18),
    ('PRD-109', 'Llanta Radiales 195/65 R15',   'Neumático de alta adherencia para piso seco y mojado',                    260.00, 40, 19),
    ('PRD-110', 'Juego de Bujías de Iridio (4U)','Bujías de encendido rápido y optimización de consumo',                   95.00, 35, 20),
    ('PRD-111', 'Faja de Distribución (Timing)', 'Correa dentada de caucho HNBR reforzado con fibra de vidrio',            85.00, 28, 21),
    ('PRD-112', 'Filtro de Aire de Motor',      'Filtro de alto flujo de aire para protección contra partículas',          42.00, 60, 22),
    ('PRD-113', 'Inyector de Combustible Diésel','Inyector de alta precisión Common Rail para motores diésel',            290.00, 14, 23),
    ('PRD-114', 'Resorte de Suspensión Reforzado','Espirales traseras para amortiguación de carga pesada',                165.00, 22, 24),
    ('PRD-115', 'Empaquetadura de Culata',      'Junta metálica de culata resistente a altas temperaturas',               75.00, 30, 25),
    ('PRD-116', 'Silenciador de Escape Trasero', 'Silenciador aluminizado de bajo nivel de ruido',                        195.00, 10, 26),
    ('PRD-117', 'Radiador de Aluminio Soldado',  'Radiador de enfriamiento de motor de alta disipación térmica',          340.00, 16, 27),
    ('PRD-118', 'Faro Delantero LED H4 (Par)',  'Kit de focos LED de alta potencia luz blanca 6000K',                     130.00, 25, 28),
    ('PRD-119', 'Juego de Retenes de Motor',    'Set completo de retenes de cigüeñal y árbol de levas',                    68.00, 40, 29),
    ('PRD-120', 'Disco de Freno Ventilado (Par)','Discos de freno delanteros de hierro fundido nodular',                  220.00, 18, 30);

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


-- ----------------------------------------------------------------------------------------------------
--                                    Procedimientos almacenados
-- ----------------------------------------------------------------------------------------------------

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

-- Ventas (punto de venta) -------------------------------------------------------------------------------------
-- sp_registrarVenta: inserta la cabecera de la venta y devuelve el id generado.
DROP PROCEDURE IF EXISTS sp_registrarVenta $$
CREATE PROCEDURE sp_registrarVenta(
    IN p_idUsuario INT,
    IN p_idCliente INT,
    IN p_total     DECIMAL(10,2)
)
BEGIN
    INSERT INTO Venta (idUsuario, id_cliente, total, fecha)
    VALUES (p_idUsuario, p_idCliente, p_total, NOW());
    SELECT LAST_INSERT_ID() AS idVenta;
END $$

-- sp_registrarDetalleVenta: inserta una fila del detalle de la venta.
DROP PROCEDURE IF EXISTS sp_registrarDetalleVenta $$
CREATE PROCEDURE sp_registrarDetalleVenta(
    IN p_idVenta        INT,
    IN p_idProducto     INT,
    IN p_cantidad       INT,
    IN p_precioUnitario DECIMAL(10,2),
    IN p_total          DECIMAL(10,2)
)
BEGIN
    INSERT INTO DetalleVenta (id_venta, id_producto, cantidad, precio_unitario, total)
    VALUES (p_idVenta, p_idProducto, p_cantidad, p_precioUnitario, p_total);
END $$

-- sp_reducirStockProducto: descuenta el stock vendido (se ejecuta por cada item confirmado).
DROP PROCEDURE IF EXISTS sp_reducirStockProducto $$
CREATE PROCEDURE sp_reducirStockProducto(
    IN p_idProducto INT,
    IN p_cantidad   INT
)
BEGIN
    UPDATE Producto
       SET stock = stock - p_cantidad
     WHERE id_producto = p_idProducto;
END $$

-- sp_listarVentas: historial de ventas con cliente y usuario (para la vista y la bitácora).
DROP PROCEDURE IF EXISTS sp_listarVentas $$
CREATE PROCEDURE sp_listarVentas()
BEGIN
    SELECT v.id_venta,
           v.fecha,
           CONCAT(c.nombre, ' ', c.apellido) AS cliente,
           u.username                        AS usuario,
           v.total
      FROM Venta      v
      INNER JOIN Cliente  c ON v.id_cliente = c.id_cliente
      INNER JOIN Usuarios u ON v.idUsuario  = u.idUsuario
     ORDER BY v.id_venta DESC;
END $$

DELIMITER ;

-- ----------------------------------------------------------------------------------------------------
--                                          Verificaciones
-- ----------------------------------------------------------------------------------------------------

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
