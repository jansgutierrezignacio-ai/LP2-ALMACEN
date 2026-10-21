package com.autopartes.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.autopartes.entity.Producto;
import com.autopartes.repository.IProductoRepository;
import com.autopartes.config.MovimientoService;
import jakarta.servlet.http.HttpSession;

@Controller
public class ProductoController {

    private final IProductoRepository repositorio;
    private final MovimientoService movimientoService;

    public ProductoController(IProductoRepository repositorio, MovimientoService movimientoService) {
        this.repositorio = repositorio;
        this.movimientoService = movimientoService;
    }

    @GetMapping({"/productos", "/productos/listar"})
    public String listarProductos(Model model) {
        List<Producto> dataProducto = repositorio.findAll();
        model.addAttribute("dataProducto", dataProducto);
        model.addAttribute("cantReg", dataProducto.size());
        model.addAttribute("producto", new Producto());
        return "Productos"; 
    }

    @GetMapping("/productos/nuevo")
    public String nuevoProducto(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("activeTab", "agregar");
        List<Producto> dataProducto = repositorio.findAll();
        model.addAttribute("dataProducto", dataProducto);
        model.addAttribute("cantReg", dataProducto.size());
        return "Productos";
    }

    @PostMapping("/productos/registrar")
    public String registrarProducto(@ModelAttribute Producto producto, Model model, HttpSession session) {
        try {
            repositorio.save(producto);
            
            // Formato idéntico al de tu imagen
            movimientoService.registrar(
                "REGISTRAR", 
                "PRODUCTOS", 
                "Se registró producto: " + producto.getNombre(), 
                obtenerUsuarioActual(session)
            );

            model.addAttribute("mensajeOk", "Producto registrado correctamente");
        } catch (Exception e) {
            model.addAttribute("mensaje", "Ocurrió un problema al registrar el producto (Verifica el código único)");
        }
        return "redirect:/productos/listar";
    }

    @GetMapping("/productos/buscar")
    public String buscarProducto(@RequestParam(value = "txtBuscar", defaultValue = "") String texto, Model model) {
        List<Producto> data = repositorio.findByNombreContainingIgnoreCase(texto);
        model.addAttribute("dataProducto", data);
        model.addAttribute("cantReg", data.size());
        model.addAttribute("txtBuscar", texto);
        model.addAttribute("producto", new Producto());
        return "Productos";
    }

    @GetMapping("/productos/infoEdit")
    public String obtenerProductoEdit(@RequestParam("id") int id, Model model) {
        Producto producto = repositorio.findById(id).orElse(null);
        model.addAttribute("productoDataEdit", producto);
        
        List<Producto> dataProducto = repositorio.findAll();
        model.addAttribute("dataProducto", dataProducto);
        model.addAttribute("cantReg", dataProducto.size());
        model.addAttribute("producto", new Producto());
        model.addAttribute("activeTab", "editar");
        return "Productos";
    }

    @PostMapping("/productos/editar")
    public String editarProducto(@ModelAttribute Producto producto, Model model, HttpSession session) {
        try {
            repositorio.save(producto);
            
            // Formato de actualización
            movimientoService.registrar(
                "ACTUALIZAR", 
                "PRODUCTOS", 
                "Se actualizó producto: " + producto.getNombre(), 
                obtenerUsuarioActual(session)
            );

            model.addAttribute("mensajeOk", "Producto actualizado correctamente");
        } catch (Exception e) {
            model.addAttribute("mensaje", "Ocurrió un problema al actualizar el producto");
        }
        return "redirect:/productos/listar";
    }

    @GetMapping("/productos/eliminar")
    public String eliminarProducto(@RequestParam("id") int id, Model model, HttpSession session) {
        try {
            Producto prod = repositorio.findById(id).orElse(null);
            repositorio.deleteById(id);
            
            if (prod != null) {
                // Formato de eliminación
                movimientoService.registrar(
                    "ELIMINAR", 
                    "PRODUCTOS", 
                    "Se eliminó producto: " + prod.getNombre(), 
                    obtenerUsuarioActual(session)
                );
            }

            model.addAttribute("mensajeOk", "Producto eliminado correctamente");
        } catch (Exception e) {
            model.addAttribute("mensaje", "No se pudo eliminar el producto");
        }
        return "redirect:/productos/listar";
    }

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