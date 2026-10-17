package com.autopartes.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AuthFilter extends OncePerRequestFilter {

    private static final List<String> RUTAS_PUBLICAS = Arrays.asList(
            "/login",
            "/errorLogin.jsp",
            "/usuarios/nuevo",
            "/usuarios",
            "/CSS/",
            "/JS/",
            "/img/",
            "/IMG/",
            "/favicon.ico"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String ctx = request.getContextPath();
        String uri = request.getRequestURI();
        String path = uri.substring(ctx.length());

        boolean esRecursoEstatico = path.startsWith("/CSS/")
                || path.startsWith("/JS/")
                || path.startsWith("/img/")
                || path.startsWith("/IMG/");

        boolean esPublica = path.equals("/") || path.isEmpty() || esRecursoEstatico
                || path.equals("/login")
                || path.startsWith("/login/")
                || path.equals("/errorLogin.jsp")
                || path.equals("/usuarios/nuevo")
                || path.equals("/usuarios");

        // Permitir solicitudes GET y POST a /login y a /usuarios con parámetros
        if (esPublica && path.equals("/login") && "GET".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }
        if (esPublica && path.equals("/login") && "POST".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }
        if (path.equals("/usuarios") && ("GET".equalsIgnoreCase(request.getMethod()) || "POST".equalsIgnoreCase(request.getMethod()))) {
            // Permitir /usuarios (listar/registrar) por público? Origen: /ServletUsuario era público.
            // Mantenemos comportamiento original para no romper el flujo de registro.
            chain.doFilter(request, response);
            return;
        }
        if (path.equals("/usuarios/nuevo") && "GET".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }
        if (esRecursoEstatico) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        boolean autenticado = session != null && session.getAttribute("usuario") != null;

        if (autenticado) {
            chain.doFilter(request, response);
        } else {
            response.sendRedirect(ctx.isEmpty() ? "/login" : (ctx + "/login"));
        }
    }
}
