package com.autopartes.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import interfaces.IntUsuario;
import modelo.Usuario;

/**
 * Gestión de usuarios y roles. Todas las rutas de este controlador están
 * restringidas al rol Administrador por {@code com.autopartes.config.AuthFilter}.
 */
@Controller
public class UsuarioController {

    @Autowired
    private IntUsuario usuarioService;

    // Listado de usuarios.
    @GetMapping(value = "/usuarios", params = "!type")
    public String listar(Model model) {
        return listarUsuarios(model);
    }

    @GetMapping(value = "/usuarios", params = "type=listar")
    public String listarPorTipo(Model model) {
        return listarUsuarios(model);
    }

    private String listarUsuarios(Model model) {
        List<Usuario> data = usuarioService.listarUsuario();
        model.addAttribute("data", data);
        model.addAttribute("cantReg", data.size());
        return "usuarioList";
    }

    // Formulario de registro de usuario.
    @GetMapping("/usuarios/nuevo")
    public String nuevo() {
        return "usuarioNuevo";
    }

    @GetMapping(value = "/usuarios", params = "type=nuevo")
    public String nuevoPorTipo() {
        return "usuarioNuevo";
    }

    // Alta de usuario: vuelve al listado (no inicia sesión con la cuenta creada).
    @PostMapping(value = "/usuarios", params = "type=registrar")
    public String registrar(@RequestParam("txtUsername") String username,
                            @RequestParam("txtClave") String clave,
                            @RequestParam("cboTipo") int idTipo,
                            @RequestParam(value = "txtNombres", required = false) String nombres,
                            @RequestParam(value = "txtApellidos", required = false) String apellidos,
                            RedirectAttributes redirectAttributes, Model model) {
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setClave(clave);
        usuario.setIdTipo(idTipo);
        usuario.setNombres(nombres);
        usuario.setApellidos(apellidos);

        int value = usuarioService.crearUsuario(usuario);
        if (value > 0) {
            redirectAttributes.addFlashAttribute("mensajeOk", "Usuario registrado correctamente");
            return "redirect:/usuarios";
        }
        model.addAttribute("mensaje", "No se pudo registrar el usuario");
        return "msgError";
    }
}
