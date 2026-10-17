package com.autopartes.controlador;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import interfaces.IntUsuario;
import modelo.Usuario;

@Controller
public class LoginController {

    @Autowired
    private IntUsuario usuarioService;

    // Muestra la pantalla de acceso en la raíz y en /login
    @GetMapping({"/", "/login"})
    public String mostrarLogin() {
        return "login";
    }

    // Valida las credenciales contra la base de datos y crea la sesión
    @PostMapping("/login")
    public String procesarLogin(@RequestParam("username") String username,
                                @RequestParam("password") String password,
                                HttpSession session, Model model) {
        Usuario usuario = usuarioService.login(username, password);
        if (usuario != null) {
            session.setAttribute("usuario", usuario);
            return "redirect:/inicio";
        }
        model.addAttribute("mensaje", "No existe usuario y/o clave");
        return "errorLogin";
    }

    // Cierra la sesión y regresa al formulario de acceso
    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login";
    }
}
