package com.autopartes.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import modelo.Usuario;

/**
 * Filtro de autenticación y autorización. Intercepta una sola vez cada petición y:
 * <ol>
 *   <li>deja pasar las rutas públicas (login y recursos estáticos);</li>
 *   <li>exige una sesión autenticada (atributo {@code usuario});</li>
 *   <li>verifica, mediante {@link Autorizacion}, que el rol del usuario permita
 *       ejecutar la operación solicitada (control de acceso por rol).</li>
 * </ol>
 * Si no hay sesión, redirige al login; si el rol no tiene permiso, regresa al panel.
 */
@Component
public class AuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String ctx = request.getContextPath();
        String path = request.getRequestURI().substring(ctx.length());

        if (esPublica(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        Usuario usuario = session == null ? null : (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            response.sendRedirect(ctx + "/login");
            return;
        }

        // El cierre de sesión está disponible para cualquier usuario autenticado.
        if ("/logout".equals(path)) {
            chain.doFilter(request, response);
            return;
        }

        String type = request.getParameter("type");
        // Solo se aplica el control de acceso a módulos conocidos. Las rutas
        // desconocidas se dejan pasar para que Spring genere el 404 (con su
        // página de error) en lugar de redirigirlas al panel.
        if (Autorizacion.esModuloConocido(path) && !Autorizacion.permite(path, type, usuario.getIdTipo())) {
            response.sendRedirect(ctx + "/inicio");
            return;
        }

        chain.doFilter(request, response);
    }

    /** Rutas accesibles sin sesión: login, recursos estáticos, error de credenciales y la página de error. */
    private boolean esPublica(String path) {
        return path.isEmpty()
                || path.equals("/")
                || path.equals("/login")
                || path.equals("/errorLogin.jsp")
                || path.equals("/error")
                || path.startsWith("/CSS/")
                || path.startsWith("/JS/")
                || path.startsWith("/img/")
                || path.startsWith("/IMG/")
                || path.equals("/favicon.ico");
    }
}
