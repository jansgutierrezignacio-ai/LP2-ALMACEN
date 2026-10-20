package com.autopartes.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.autopartes.entity.Proveedor;
import com.autopartes.repository.IProveedorRepository;
import com.autopartes.config.MovimientoService; // Importamos el servicio
import jakarta.servlet.http.HttpSession;          // Importamos la sesión

@Controller
public class ProveedorController {

    private final IProveedorRepository repositorio;
    private final MovimientoService movimientoService; // Inyectamos el servicio

    public ProveedorController(IProveedorRepository repositorio, MovimientoService movimientoService) {
        this.repositorio = repositorio;
        this.movimientoService = movimientoService;
    }

    // Acepta tanto /proveedores como /proveedores/listar
    @GetMapping({"/proveedores", "/proveedores/listar"})
    public String listarProveedores(Model model) {
        List<Proveedor> dataProveedor = repositorio.findAll();
        model.addAttribute("dataProveedor", dataProveedor);
        model.addAttribute("cantReg", dataProveedor.size());
        model.addAttribute("proveedor", new Proveedor()); 
        return "Proveedores"; 
    }

    @GetMapping("/proveedores/nuevo")
    public String nuevoProveedor(Model model) {
        model.addAttribute("proveedor", new Proveedor());
        model.addAttribute("activeTab", "agregar");
        List<Proveedor> dataProveedor = repositorio.findAll();
        model.addAttribute("dataProveedor", dataProveedor);
        model.addAttribute("cantReg", dataProveedor.size());
        return "Proveedores";
    }

    @PostMapping("/proveedores/registrar")
    public String registrarProveedor(@ModelAttribute Proveedor proveedor, Model model, HttpSession session) {
        try {
            repositorio.save(proveedor);
            
            // 📌 REGISTRAR MOVIMIENTO
            movimientoService.registrar(
                "REGISTRAR", 
                "PROVEEDORES", 
                "Se registró proveedor: " + proveedor.getRazonSocial(), 
                obtenerUsuarioActual(session)
            );

            model.addAttribute("mensajeOk", "Proveedor registrado correctamente");
        } catch (Exception e) {
            model.addAttribute("mensaje", "Ocurrió un problema al registrar el proveedor (Verifica el RUC)");
        }
        return "redirect:/proveedores/listar";
    }

    @GetMapping("/proveedores/buscar")
    public String buscarProveedor(@RequestParam(value = "txtBuscar", defaultValue = "") String texto, Model model) {
        List<Proveedor> data = repositorio.findByRazonSocialContainingIgnoreCase(texto);
        model.addAttribute("dataProveedor", data);
        model.addAttribute("cantReg", data.size());
        model.addAttribute("txtBuscar", texto);
        model.addAttribute("proveedor", new Proveedor());
        return "Proveedores";
    }

    @GetMapping("/proveedores/infoEdit")
    public String obtenerProveedorEdit(@RequestParam("id") int id, Model model) {
        Proveedor proveedor = repositorio.findById(id).orElse(null);
        model.addAttribute("proveedorDataEdit", proveedor);
        
        List<Proveedor> dataProveedor = repositorio.findAll();
        model.addAttribute("dataProveedor", dataProveedor);
        model.addAttribute("cantReg", dataProveedor.size());
        model.addAttribute("proveedor", new Proveedor());
        model.addAttribute("activeTab", "editar");
        return "Proveedores";
    }

    @PostMapping("/proveedores/editar")
    public String editarProveedor(@ModelAttribute Proveedor proveedor, Model model, HttpSession session) {
        try {
            repositorio.save(proveedor);
            
            // 📌 REGISTRAR MOVIMIENTO DE ACTUALIZACIÓN
            movimientoService.registrar(
                "ACTUALIZAR", 
                "PROVEEDORES", 
                "Se actualizó proveedor: " + proveedor.getRazonSocial(), 
                obtenerUsuarioActual(session)
            );

            model.addAttribute("mensajeOk", "Proveedor actualizado correctamente");
        } catch (Exception e) {
            model.addAttribute("mensaje", "Ocurrió un problema al actualizar el proveedor");
        }
        return "redirect:/proveedores/listar";
    }

    @GetMapping("/proveedores/eliminar")
    public String eliminarProveedor(@RequestParam("id") int id, Model model, HttpSession session) {
        try {
            Proveedor prov = repositorio.findById(id).orElse(null);
            repositorio.deleteById(id);
            
            if (prov != null) {
                // 📌 REGISTRAR MOVIMIENTO DE ELIMINACIÓN
                movimientoService.registrar(
                    "ELIMINAR", 
                    "PROVEEDORES", 
                    "Se eliminó proveedor: " + prov.getRazonSocial(), 
                    obtenerUsuarioActual(session)
                );
            }

            model.addAttribute("mensajeOk", "Proveedor eliminado correctamente");
        } catch (Exception e) {
            model.addAttribute("mensaje", "No se pudo eliminar el proveedor");
        }
        return "redirect:/proveedores/listar";
    }

    // Método auxiliar para extraer el usuario de la sesión actual
    private String obtenerUsuarioActual(HttpSession session) {
        if (session != null && session.getAttribute("usuario") != null) {
            Object userObj = session.getAttribute("usuario");
            if (userObj instanceof com.autopartes.entity.Usuario) {
                return ((com.autopartes.entity.Usuario) userObj).getUsername();
            }
        }
        return "Administrador";
    }
}