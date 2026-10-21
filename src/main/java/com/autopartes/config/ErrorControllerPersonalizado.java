package com.autopartes.config;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Manejador de errores HTTP del sitio.
 *
 * Spring Boot enruta a /error cualquier respuesta con status >= 400 (y las
 * excepciones no capturadas). Este controller lee el status de la petición y
 * devuelve la vista JSP dedicada correspondiente (error/404, error/500,
 * error/403) o, si no hay página específica, la genérica error/error.
 *
 * Nota: no hay web.xml (la configuración vive en Spring Boot y en
 * application.properties); quien decide la página de error es esta clase.
 */
@Controller
public class ErrorControllerPersonalizado implements ErrorController {

    @RequestMapping("/error")
    public String manejarError(HttpServletRequest request, Model model) {
        // Status real del error; si no está disponible, 500 por defecto.
        Integer status = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String ruta = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        if (status == null) {
            status = 500;
        }
        model.addAttribute("status", status);
        model.addAttribute("ruta", ruta);

        switch (status) {
            case 404:
                return "error/404";
            case 403:
                return "error/403";
            case 500:
                return "error/500";
            default:
                return "error/error";
        }
    }
}
