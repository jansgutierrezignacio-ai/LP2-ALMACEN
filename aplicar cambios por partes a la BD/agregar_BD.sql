UPDATE usuarios SET idTipo = 3 WHERE username = 'marcovillarruel';

INSERT INTO usuarios (username, clave, idTipo) 
VALUES ('kazz', '123456', 2);


ALTER TABLE producto 
DROP FOREIGN KEY fk_prod_prov;

ALTER TABLE producto 
ADD CONSTRAINT fk_prod_prov 
FOREIGN KEY (id_proveedor) REFERENCES proveedor(id_proveedor) 
ON DELETE CASCADE;



-- Para los productos ligados a ventas:
ALTER TABLE detalleventa 
DROP FOREIGN KEY fk_detalle_prod;

ALTER TABLE detalleventa 
ADD CONSTRAINT fk_detalle_prod 
FOREIGN KEY (id_producto) REFERENCES producto(id_producto) 
ON DELETE CASCADE;

-- Para los productos ligados a proveedores:
ALTER TABLE producto 
DROP FOREIGN KEY fk_prod_prov;

ALTER TABLE producto 
ADD CONSTRAINT fk_prod_prov 
FOREIGN KEY (id_proveedor) REFERENCES proveedor(id_proveedor) 
ON DELETE CASCADE;

-- opcional/recomendado:
INSERT INTO proveedor (ruc, razon_social, telefono, estado) VALUES
('20100100102', 'Importaciones y Repuestos El Sol S.A.C.', '987654322', 1),
('20100100103', 'Autopartes y Motores del Perú S.R.L.', '987654323', 1),
('20100100104', 'Distribuidora Frenos y Embragues E.I.R.L.', '987654324', 1),
('20100100105', 'Comercializadora Automotriz Global S.A.', '987654325', 1),
('20100100106', 'Sistemas de Suspensión del Norte S.A.C.', '987654326', 1),
('20100100107', 'Lubricantes y Filtros Industriales S.R.L.', '987654327', 1),
('20100100108', 'Repuestos Eléctricos Automotrices E.I.R.L.', '987654328', 1),
('20100100109', 'Multirepuestos y Baterías del Pacífico S.A.', '987654329', 1),
('20100100110', 'Importadora de Neumáticos San Martín S.A.C.', '987654330', 1),
('20100100111', 'Accesorios y Bujías del Sur S.R.L.', '987654331', 1),
('20100100112', 'Distribuidora de Correas y Fajas E.I.R.L.', '987654332', 1),
('20100100113', 'Tecnología Automotriz Integral S.A.', '987654333', 1),
('20100100114', 'Sistemas de Inyección diésel S.A.C.', '987654334', 1),
('20100100115', 'Comercial La Casa del Amortiguador S.R.L.', '987654335', 1),
('20100100116', 'Importaciones Mecánicas del Perú E.I.R.L.', '987654336', 1),
('20100100117', 'Soluciones en Escapes y Silenciadores S.A.', '987654337', 1),
('20100100118', 'Autopartes y Radiadores El Inka S.A.C.', '987654338', 1),
('20100100119', 'Distribuidora de Iluminación Led Auto S.R.L.', '987654339', 1),
('20100100120', 'Central de Empaquetaduras y Retenes E.I.R.L.', '987654340', 1),
('20100100121', 'Consorcio Automotriz del Norte S.A.', '987654341', 1);


INSERT INTO producto (codigo, nombre, descripcion, precio, stock, id_proveedor) VALUES
('PRD-101', 'Filtro de Aceite Sintético', 'Filtro de aceite para motores a gasolina y diésel de alto rendimiento', 35.50, 45, 104),
('PRD-102', 'Juego de Pastillas de Freno', 'Pastillas cerámicas delanteras con alta resistencia al calor', 120.00, 30, 105),
('PRD-103', 'Kit de Embrague Completo', 'Incluye plato, disco y rodaje de empuje para transmisión manual', 450.00, 15, 106),
('PRD-104', 'Bomba de Agua Multiválvula', 'Bomba de agua de aleación de aluminio para sistema de refrigeración', 185.00, 20, 107),
('PRD-105', 'Amortiguador Delantero Gas', 'Amortiguador presurizado con nitrógeno para mayor estabilidad', 210.00, 25, 108),
('PRD-106', 'Aceite Multigrado 10W-40 (4L)', 'Lubricante sintético para protección del motor contra desgaste', 115.00, 50, 109),
('PRD-107', 'Alternador 12V 90A', 'Alternador reforzado para sistema eléctrico automotriz', 380.00, 12, 110),
('PRD-108', 'Batería Automotriz 12V 60Ah', 'Batería libre de mantenimiento de libre descarga lenta', 320.00, 18, 111),
('PRD-109', 'Llanta Radiales 195/65 R15', 'Neumático de alta adherencia para piso seco y mojado', 260.00, 40, 112),
('PRD-110', 'Juego de Bujías de Iridio (4U)', 'Bujías de encendido rápido y optimización de consumo', 95.00, 35, 113),
('PRD-111', 'Faja de Distribución (Timing)', 'Correa dentada de caucho HNBR reforzado con fibra de vidrio', 85.00, 28, 114),
('PRD-112', 'Filtro de Aire de Motor', 'Filtro de alto flujo de aire para protección contra partículas', 42.00, 60, 115),
('PRD-113', 'Inyector de Combustible Diésel', 'Inyector de alta precisión Common Rail para motores diésel', 290.00, 14, 116),
('PRD-114', 'Resorte de Suspensión Reforzado', 'Espirales traseras para amortiguación de carga pesada', 165.00, 22, 117),
('PRD-115', 'Empaquetadura de Culata', 'Junta metálica de culata resistente a altas temperaturas', 75.00, 30, 118),
('PRD-116', 'Silenciador de Escape Trasero', 'Silenciador aluminizado de bajo nivel de ruido', 195.00, 10, 119),
('PRD-117', 'Radiador de Aluminio Soldado', 'Radiador de enfreimiento de motor de alta disipación térmica', 340.00, 16, 120),
('PRD-118', 'Faro Delantero LED H4 (Par)', 'Kit de focos LED de alta potencia luz blanca 6000K', 130.00, 25, 121),
('PRD-119', 'Juego de Retenes de Motor', 'Set completo de retenes de cigüeñal y árbol de levas', 68.00, 40, 122),
('PRD-120', 'Disco de Freno Ventilado (Par)', 'Discos de freno delanteros de hierro fundido nodular', 220.00, 18, 123);

INSERT INTO cliente (dni, nombre, apellido, telefono) VALUES
('42158963', 'Carlos', 'Gomez', '987654321'),
('71234567', 'Ana', 'Torres', '912345678'),
('45892314', 'Luis', 'Ramirez', '923456789'),
('73891245', 'Sofia', 'Fernandez', '934567891'),
('40981273', 'Jorge', 'Castro', '945678912'),
('72345689', 'Lucia', 'Mendoza', '956789123'),
('41892374', 'Miguel', 'Rojas', '967891234'),
('70983421', 'Valeria', 'Silva', '978912345'),
('43125689', 'Daniel', 'Vargas', '989123456'),
('74561283', 'Camila', 'Morales', '990123457'),
('42893412', 'Fernando', 'Paredes', '911234568'),
('71982345', 'Diana', 'Flores', '922345679'),
('40128934', 'Ricardo', 'Espinoza', '933456780'),
('73451298', 'Gabriela', 'Chavez', '944567891'),
('41567892', 'Rosa', 'Medina', '955678902'),
('72893415', 'Jose', 'Huaman', '966789013');

SELECT * FROM usuarios;

