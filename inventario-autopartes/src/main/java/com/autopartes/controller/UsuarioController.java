package com.autopartes.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.autopartes.entity.Usuario;
import com.autopartes.repository.IUsuarioRepository;

@Controller
public class UsuarioController {

    private final IUsuarioRepository repositorio;

    public UsuarioController(IUsuarioRepository repositorio) {
        this.repositorio = repositorio;
    }

    @GetMapping("/usuarios/listar")
    public String listarUsuarios(Model model) {
        List<Usuario> dataUsuario = repositorio.findAll();
        model.addAttribute("dataUsuario", dataUsuario);
        model.addAttribute("cantReg", dataUsuario.size());
        return "Usuarios"; // Apuntará a Usuarios.html en templates
    }

    @GetMapping("/usuarios/nuevo")
    public String nuevoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("activeTab", "agregar");
        List<Usuario> dataUsuario = repositorio.findAll();
        model.addAttribute("dataUsuario", dataUsuario);
        model.addAttribute("cantReg", dataUsuario.size());
        return "Usuarios";
    }

    @PostMapping("/usuarios/registrar")
    public String registrarUsuario(@ModelAttribute Usuario usuario, Model model) {
        try {
            repositorio.save(usuario);
            model.addAttribute("mensajeOk", "Usuario registrado correctamente");
        } catch (Exception e) {
            model.addAttribute("mensaje", "Ocurrió un problema al registrar el usuario");
        }
        return "redirect:/usuarios/listar";
    }

    @GetMapping("/usuarios/infoEdit")
    public String obtenerUsuarioEdit(@RequestParam("id") int id, Model model) {
        Usuario usuario = repositorio.findById(id).orElse(null);
        model.addAttribute("usuarioDataEdit", usuario);
        
        List<Usuario> dataUsuario = repositorio.findAll();
        model.addAttribute("dataUsuario", dataUsuario);
        model.addAttribute("cantReg", dataUsuario.size());
        model.addAttribute("activeTab", "editar");
        return "Usuarios";
    }

    @PostMapping("/usuarios/editar")
    public String editarUsuario(@ModelAttribute Usuario usuario, Model model) {
        try {
            repositorio.save(usuario);
            model.addAttribute("mensajeOk", "Usuario actualizado correctamente");
        } catch (Exception e) {
            model.addAttribute("mensaje", "Ocurrió un problema al actualizar");
        }
        return "redirect:/usuarios/listar";
    }

    @GetMapping("/usuarios/eliminar")
    public String eliminarUsuario(@RequestParam("id") int id, Model model) {
        try {
            repositorio.deleteById(id);
            model.addAttribute("mensajeOk", "Usuario eliminado correctamente");
        } catch (Exception e) {
            model.addAttribute("mensaje", "No se pudo eliminar el usuario");
        }
        return "redirect:/usuarios/listar";
    }
}