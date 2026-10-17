package com.autopartes.config;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class ManejadorErrores {

    // Captura errores de conversión de tipo (números inválidos en parámetros) y
    // muestra la página genérica de error.
    @ExceptionHandler({NumberFormatException.class, MethodArgumentTypeMismatchException.class, ConversionFailedException.class})
    public String parametroInvalido(Model model) {
        model.addAttribute("mensaje", "Parámetro inválido");
        return "msgError";
    }
}
