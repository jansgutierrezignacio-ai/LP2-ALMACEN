package com.autopartes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.autopartes.entity.Usuario;
import com.autopartes.repository.IUsuarioRepository;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @ModelAttribute("usuario")
    public String getUsername(Authentication authentication) {
        if (authentication != null) {
            return authentication.getName();
        }
        return "Usuario";
    }

    @ModelAttribute("rolUsuarioActual")
    public String getRolUsuarioActual(Authentication authentication) {
        if (authentication != null) {
            String username = authentication.getName();
            Usuario user = usuarioRepository.findByUsername(username);
            if (user != null) {
                return user.getNombreRolTexto();
            }
        }
        return "Almacenero";
    }
}