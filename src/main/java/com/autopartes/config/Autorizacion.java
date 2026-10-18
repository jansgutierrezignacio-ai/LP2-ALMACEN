package com.autopartes.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Reglas de autorización por rol (control de acceso basado en el tipo de usuario).
 *
 * El sistema define tres roles en la tabla TiposUsuario:
 * <ul>
 *   <li>1 = Administrador: acceso total a todos los módulos.</li>
 *   <li>2 = Vendedor: administra los clientes y consulta el stock y las ventas.</li>
 *   <li>3 = Almacenero: administra los productos y proveedores y revisa la bitácora.</li>
 * </ul>
 *
 * Cada módulo tiene un conjunto de roles con permiso de LECTURA y otro con permiso
 * de ESCRITURA. Las operaciones de escritura se identifican por el parámetro
 * {@code type} de la petición (registrar, editar, eliminar, ...); el resto de las
 * operaciones se consideran de solo lectura.
 */
public final class Autorizacion {

    public static final int ADMINISTRADOR = 1;
    public static final int VENDEDOR = 2;
    public static final int ALMACENERO = 3;

    /** Operaciones que modifican información (el resto se consideran de lectura). */
    private static final Set<String> OPERACIONES_ESCRITURA = new HashSet<>(Arrays.asList(
            "registrar", "editar", "eliminar", "nuevo", "infoedit"));

    private Autorizacion() {
    }

    /**
     * Indica si la ruta corresponde a un módulo del sistema (conocido por las
     * reglas de autorización). Las rutas que no son ningún módulo conocido no
     * se interceptan aquí: se dejan pasar para que Spring resuelva (y, si no
     * hay manejador, genere el 404 con su página de error).
     *
     * @param path ruta servlet (sin el contexto)
     * @return {@code true} si la ruta pertenece a un módulo conocido
     */
    public static boolean esModuloConocido(String path) {
        return moduloDe(path) != null;
    }

    /**
     * Indica si el rol indicado puede ejecutar la operación solicitada sobre la ruta.
     *
     * @param path   ruta servlet (sin el contexto de la aplicación), por ejemplo {@code /productos}
     * @param type   valor del parámetro {@code type} de la petición (puede ser {@code null})
     * @param idTipo rol del usuario en sesión (1, 2 o 3)
     * @return {@code true} si el acceso está permitido
     */
    public static boolean permite(String path, String type, int idTipo) {
        String modulo = moduloDe(path);
        if (modulo == null) {
            return false;
        }
        if (esEscritura(type)) {
            return puedeEscribir(modulo, idTipo);
        }
        return puedeLeer(modulo, idTipo);
    }

    /** Módulo lógico al que pertenece la ruta (la primera parte de la URL). */
    private static String moduloDe(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        if (path.equals("/") || path.startsWith("/inicio")) return "/inicio";
        if (path.startsWith("/usuarios")) return "/usuarios";
        if (path.startsWith("/productos")) return "/productos";
        if (path.startsWith("/proveedores")) return "/proveedores";
        if (path.startsWith("/clientes")) return "/clientes";
        if (path.startsWith("/movimientos")) return "/movimientos";
        if (path.startsWith("/reportes")) return "/reportes";
        return null;
    }

    private static boolean esEscritura(String type) {
        return type != null && OPERACIONES_ESCRITURA.contains(type.toLowerCase());
    }

    private static boolean puedeLeer(String modulo, int idTipo) {
        switch (modulo) {
            case "/inicio":
                return true;
            case "/productos":
                return true; // los tres roles pueden consultar el stock
            case "/usuarios":
                return idTipo == ADMINISTRADOR;
            case "/proveedores":
            case "/movimientos":
                return idTipo == ADMINISTRADOR || idTipo == ALMACENERO;
            case "/clientes":
            case "/reportes":
                return idTipo == ADMINISTRADOR || idTipo == VENDEDOR;
            default:
                return false;
        }
    }

    private static boolean puedeEscribir(String modulo, int idTipo) {
        switch (modulo) {
            case "/usuarios":
                return idTipo == ADMINISTRADOR;
            case "/productos":
            case "/proveedores":
                return idTipo == ADMINISTRADOR || idTipo == ALMACENERO;
            case "/clientes":
                return idTipo == ADMINISTRADOR || idTipo == VENDEDOR;
            default:
                return false; // movimientos y reportes son de solo lectura
        }
    }
}
