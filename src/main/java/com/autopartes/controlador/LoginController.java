package com.autopartes.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    // 1. Muestra la pantalla de inicio de sesión cuando entras a la raíz o a /login
    @GetMapping({"/", "/login"})
    public String mostrarLogin() {
        return "login"; 
    }

    // 2. Procesa los datos cuando el usuario presiona el botón "Ingresar"
    @PostMapping("/login")
    public String procesarLogin(
            @RequestParam("username") String username, 
            @RequestParam("password") String password, 
            Model model) {
        
        // REGLA DE VALIDACIÓN (Puedes cambiar "admin")
        if ("admin".equals(username) && "123456".equals(password)) {
            
            return "Inicio";
        } else {
            // Si los datos son incorrectos, envía un mensaje de error a la vista login.jsp
            model.addAttribute("error", "Usuario o contraseña incorrectos.");
            return "login"; 
        }
    }

    // 3. Permite cerrar sesión y regresar al login de forma limpia
    @GetMapping("/logout")
    public String cerrarSesion() {
        return "redirect:/login";
    }
}
