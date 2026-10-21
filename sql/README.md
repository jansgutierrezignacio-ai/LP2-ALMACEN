# Reconstrucción de la base de datos — BD_ALMACEN

Esta carpeta contiene la base de datos del proyecto **Comercial Autopartes SAC**
(LP2 - Cibertec), reconstruida de forma completa y robusta a partir de:

- El script original del equipo (se conserva localmente, fuera del repositorio).
- El **código Java real** (`procesos/Gestion*.java`, `procesos/GestionUsuario.java`,
  `src/main/resources/application.properties`).

## Archivos

| Archivo | Descripción |
|---|---|
| `BD_ALMACEN.sql` | **Script completo**: base de datos, 8 tablas, datos semilla y 31 procedimientos almacenados. |
| `README.md` | Este documento. |

> El script original del equipo no se versiona: sus tablas, columnas, tipos, claves
> y procedimientos quedaron cubiertos por `BD_ALMACEN.sql`.

## Nombre de la base de datos

Se unificó el nombre a **`BD_ALMACEN`** (decisión tomada tras confirmación con el usuario).

- Original: `BD_Almacen` (script del equipo y `persistence.xml`)
- Código anterior: `LP2_ALMACEN` (MysqlConexion.java, application.properties)
- Unificado: **`BD_ALMACEN`** (BD + código + informe)

El código fue actualizado para usar `BD_ALMACEN` y las credenciales se externalizaron
al archivo `.env` (no versionado) o a variables de entorno.

## Cambios respecto del script original

 1. Base de datos renombrada a `BD_ALMACEN` (unificada con el código y el informe).
 2. Charset `utf8mb4` / collation `utf8mb4_unicode_ci` (soporta acentos: "Batería", "El Rápido"…).
 3. Corregido `sp_contarMovimientos`: usaba `FROM movimientos` (minúsculas) y en Linux
    MySQL es *case-sensitive* (`lower_case_table_names = 0`); la tabla real es `Movimientos`.
 4. Agregado `sp_RegistrarUsuario(...)`: el código lo invocaba pero **no existía** (incongruencia B12).
 5. Añadidos usuarios de prueba (uno por rol) para poder iniciar sesión y probar: `admin`, `vendedor`, `almacenero` (clave `123456`).
 6. Tablas con `ENGINE=InnoDB` y `DROP PROCEDURE IF EXISTS` antes de cada procedimiento (re-ejecutable).
 7. **Claves cifradas con BCrypt**: la columna `clave` guarda hashes `$2a$10$...`
     de 60 caracteres (cabe en `VARCHAR(100)`) en lugar de texto plano. Los cinco
     usuarios semilla tienen la clave real `123456`; la verificación la hace la
     aplicación con `PasswordEncoder.matches()` (ver
     `com/autopartes/config/PasswordConfig.java` y `procesos/GestionUsuario.login`).
     Para generar un hash nuevo (p. ej. al añadir usuarios al script):
     `new BCryptPasswordEncoder().encode("clave")`.

El resto de tablas, columnas, tipos, claves y procedimientos se mantiene fiel al original.

## Cómo importar

Contenedor podman usado: **`cibertec-mysql`** (MySQL 9.6, puerto 3306).
Los datos persisten en un volumen nombrado (`/var/lib/mysql`).

```bash
# Con el contenedor levantado: podman start cibertec-mysql
podman exec -i cibertec-mysql mysql -uroot -p"$MYSQL_ROOT_PASSWORD" < sql/BD_ALMACEN.sql

# Instalación local (cliente mysql):
mysql -h 127.0.0.1 -P 3306 -u root -p < sql/BD_ALMACEN.sql
```

Al final del script se imprimen dos verificaciones: el conteo de filas por tabla y
la lista de procedimientos almacenados creados.

## Usuario de la base de datos

Se creó el usuario **`zeth`** (caching_sha2_password) con la contraseña generada.
Las credenciales están en **`LP2-ALMACEN/.env`** (archivo ignorado por git):

```
MYSQL_USER=zeth
MYSQL_PASSWORD=<generada>
```

```bash
mysql -h 127.0.0.1 -P 3306 -u zeth -p'<MYSQL_PASSWORD>' BD_ALMACEN
```

> Nota: el cliente `mysql`/`mariadb` del sistema es MariaDB 11.x. Para usar una GUI
> (DBeaver, MySQL Workbench, etc.) basta con los mismos datos de `.env`.

## Datos y procedimientos

- **Tablas (8):** TiposUsuario, Usuarios, Proveedor, Cliente, Producto, Venta, DetalleVenta, Movimientos.
- **Roles:** 1 Administrador, 2 Vendedor, 3 Almacenero.
- **Procedimientos (31):** clientes (6), proveedores (7), productos (7), reportes (3),
  movimientos (3), usuarios (1: `sp_RegistrarUsuario`), ventas (4: `sp_registrarVenta`,
  `sp_registrarDetalleVenta`, `sp_reducirStockProducto`, `sp_listarVentas`).
