package com.autopartes.controlador;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    // Cifra la clave con BCrypt antes de guardarla (ver PasswordConfig).
    @Autowired
    private PasswordEncoder passwordEncoder;

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

    // Formulario de edición en un modal sobre el listado (mismo patrón que clientes).
    @GetMapping(value = "/usuarios", params = "type=infoEdit")
    public String infoEdit(@RequestParam("id") int id, Model model) {
        Usuario usuario = usuarioService.obtenerUsuario(id);
        if (usuario == null) {
            model.addAttribute("mensaje", "El usuario solicitado no existe");
            return "msgError";
        }
        model.addAttribute("usuarioDataEdit", usuario);
        return listarUsuarios(model);
    }

    // Actualización de usuario. Si la clave llega vacía se conserva la actual.
    @PostMapping(value = "/usuarios", params = "type=editar")
    public String editar(@RequestParam("idUsuario") int idUsuario,
                         @RequestParam("txtUsername") String username,
                         @RequestParam(value = "txtClave", required = false) String clave,
                         @RequestParam("cboTipo") int idTipo,
                         @RequestParam(value = "txtNombres", required = false) String nombres,
                         @RequestParam(value = "txtApellidos", required = false) String apellidos,
                         HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        Usuario actual = usuarioService.obtenerUsuario(idUsuario);
        if (actual == null) {
            model.addAttribute("mensaje", "El usuario solicitado no existe");
            return "msgError";
        }

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);
        usuario.setUsername(username);
        usuario.setClave(clave == null || clave.trim().isEmpty()
                ? actual.getClave()
                : passwordEncoder.encode(clave.trim()));
        usuario.setIdTipo(idTipo);
        usuario.setNombres(nombres);
        usuario.setApellidos(apellidos);

        int value = usuarioService.editarUsuario(usuario);
        if (value > 0) {
            // Si el administrador editó su propia cuenta, la sesión debe reflejar
            // los nuevos datos sin obligarlo a volver a iniciar sesión.
            Usuario enSesion = (Usuario) session.getAttribute("usuario");
            if (enSesion != null && enSesion.getIdUsuario() == idUsuario) {
                enSesion.setUsername(username);
                enSesion.setNombres(nombres);
                enSesion.setApellidos(apellidos);
                enSesion.setIdTipo(idTipo);
                enSesion.setClave(usuario.getClave());
            }
            redirectAttributes.addFlashAttribute("mensajeOk", "Usuario actualizado correctamente");
            return "redirect:/usuarios";
        }
        model.addAttribute("mensaje", "No se pudo actualizar el usuario");
        return "msgError";
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
        usuario.setClave(passwordEncoder.encode(clave));
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
