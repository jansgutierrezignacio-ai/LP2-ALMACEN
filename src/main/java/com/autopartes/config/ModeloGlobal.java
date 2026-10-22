package com.autopartes.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import modelo.Usuario;

/**
 * Atributos comunes a todas las vistas Thymeleaf.
 *
 * Antes estos valores se calculaban con scriptlets y {@code <c:set>} dentro de
 * {@code menu.jsp}; con Thymeleaf las plantillas no comparten ese estado, así
 * que se exponen desde un único punto:
 * <ul>
 *   <li>{@code rolNombre}: nombre legible del rol del usuario en sesión, usado
 *       en la insignia de la barra superior ({@code Administrador}, {@code
 *       Vendedor} o {@code Almacenero}).</li>
 *   <li>{@code seccion}: primera parte de la ruta solicitada (por ejemplo
 *       {@code /productos}), que el menú lateral usa para marcar la opción
 *       activa.</li>
 * </ul>
 * Si no hay sesión (pantalla de login) ambos valores quedan vacíos o con la
 * ruta real, sin crear una sesión nueva.
 */
@ControllerAdvice
public class ModeloGlobal {

    @ModelAttribute
    public void atributosComunes(HttpServletRequest request, Model model) {
        model.addAttribute("rolNombre", nombreRol(request));
        model.addAttribute("seccion", seccion(request));
    }

    private String nombreRol(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Usuario usuario = session == null ? null : (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "";
        }
        switch (usuario.getIdTipo()) {
            case Autorizacion.ADMINISTRADOR:
                return "Administrador";
            case Autorizacion.VENDEDOR:
                return "Vendedor";
            case Autorizacion.ALMACENERO:
                return "Almacenero";
            default:
                return "Usuario";
        }
    }

    /**
     * Primera parte de la ruta, sin el contexto de la aplicación:
     * {@code /productos?type=listar} -> {@code /productos}.
     */
    private String seccion(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String ctx = request.getContextPath();
        if (ctx != null && !ctx.isEmpty() && uri.startsWith(ctx)) {
            uri = uri.substring(ctx.length());
        }
        if (uri == null || uri.isEmpty()) {
            return "/";
        }
        int corte = uri.indexOf('/', 1);
        return corte > 0 ? uri.substring(0, corte) : uri;
    }
}
