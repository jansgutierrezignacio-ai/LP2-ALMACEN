package com.autopartes.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.autopartes.dto.ElementoCarrito;
import com.autopartes.entity.Cliente;
import com.autopartes.entity.DetalleVenta;
import com.autopartes.entity.Producto;
import com.autopartes.entity.Usuario;
import com.autopartes.entity.Venta;
import com.autopartes.repository.IClienteRepository;
import com.autopartes.repository.IDetalleVentaRepository;
import com.autopartes.repository.IProductoRepository;
import com.autopartes.repository.IUsuarioRepository;
import com.autopartes.repository.IVentaRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class VentaController {

    @Autowired
    private IVentaRepository ventaRepository;

    @Autowired
    private IDetalleVentaRepository detalleVentaRepository;

    @Autowired
    private IProductoRepository productoRepository;

    @Autowired
    private IClienteRepository clienteRepository;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @GetMapping("/ventas/listar")
    public String verModuloVentas(HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
        List<ElementoCarrito> carrito = (List<ElementoCarrito>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
            session.setAttribute("carrito", carrito);
        }

        double totalVenta = carrito.stream().mapToDouble(ElementoCarrito::getSubtotal).sum();

        List<Producto> listaProductos = productoRepository.findAll();
        List<Cliente> listaClientes = clienteRepository.findAll();
        List<Venta> historialVentas = ventaRepository.findAll();

        model.addAttribute("carrito", carrito);
        model.addAttribute("totalVenta", totalVenta);
        model.addAttribute("listaProductos", listaProductos);
        model.addAttribute("listaClientes", listaClientes);
        model.addAttribute("historialVentas", historialVentas);

        return "Ventas";
    }

    @PostMapping("/ventas/agregar")
    public String agregarAlCarrito(@RequestParam("idProducto") Integer idProducto,
                                   @RequestParam("cantidad") Integer cantidad,
                                   HttpSession session,
                                   RedirectAttributes flash) {
        Producto prod = productoRepository.findById(idProducto).orElse(null);
        if (prod == null) {
            flash.addFlashAttribute("mensaje", "Producto no encontrado.");
            return "redirect:/ventas/listar";
        }

        if (cantidad <= 0) {
            flash.addFlashAttribute("mensaje", "La cantidad debe ser mayor a 0.");
            return "redirect:/ventas/listar";
        }

        if (prod.getStock() < cantidad) {
            flash.addFlashAttribute("mensaje", "Stock insuficiente. Stock disponible: " + prod.getStock());
            return "redirect:/ventas/listar";
        }

        @SuppressWarnings("unchecked")
        List<ElementoCarrito> carrito = (List<ElementoCarrito>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
        }

        // Si ya está en el carrito, actualizamos cantidad
        boolean existe = false;
        for (ElementoCarrito item : carrito) {
            if (item.getIdProducto().equals(idProducto)) {
                if (prod.getStock() < (item.getCantidad() + cantidad)) {
                    flash.addFlashAttribute("mensaje", "No hay suficiente stock para añadir esa cantidad acumulada.");
                    return "redirect:/ventas/listar";
                }
                item.setCantidad(item.getCantidad() + cantidad);
                existe = true;
                break;
            }
        }

        if (!existe) {
            carrito.add(new ElementoCarrito(prod.getIdProducto(), prod.getCodigo(), prod.getNombre(), cantidad, prod.getPrecio()));
        }

        session.setAttribute("carrito", carrito);
        return "redirect:/ventas/listar";
    }

    @GetMapping("/ventas/eliminarItem")
    public String eliminarItem(@RequestParam("idProducto") Integer idProducto, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<ElementoCarrito> carrito = (List<ElementoCarrito>) session.getAttribute("carrito");
        if (carrito != null) {
            carrito.removeIf(item -> item.getIdProducto().equals(idProducto));
            session.setAttribute("carrito", carrito);
        }
        return "redirect:/ventas/listar";
    }

    @PostMapping("/ventas/confirmar")
    public String confirmarVenta(@RequestParam("idCliente") Integer idCliente,
                                 Authentication authentication,
                                 HttpSession session,
                                 RedirectAttributes flash) {
        @SuppressWarnings("unchecked")
        List<ElementoCarrito> carrito = (List<ElementoCarrito>) session.getAttribute("carrito");
        if (carrito == null || carrito.isEmpty()) {
            flash.addFlashAttribute("mensaje", "El carrito de ventas está vacío.");
            return "redirect:/ventas/listar";
        }

        // Obtener ID del usuario logueado
        Integer idUsuario = 1; // ID por defecto
        if (authentication != null) {
            Usuario u = usuarioRepository.findByUsername(authentication.getName());
            if (u != null) {
                idUsuario = u.getIdUsuario();
            }
        }

        double total = carrito.stream().mapToDouble(ElementoCarrito::getSubtotal).sum();

        // 1. Guardar la Venta principal
        Venta venta = new Venta();
        venta.setFecha(LocalDate.now());
        venta.setIdCliente(idCliente);
        venta.setIdUsuario(idUsuario);
        venta.setTotal(total);
        Venta ventaGuardada = ventaRepository.save(venta);

        // 2. Guardar DetalleVenta y REDUCIR STOCK de cada Producto
        for (ElementoCarrito item : carrito) {
            DetalleVenta detalle = new DetalleVenta();
            detalle.setIdVenta(ventaGuardada.getIdVenta());
            detalle.setIdProducto(item.getIdProducto());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecioUnitario());
            detalle.setTotal(item.getSubtotal());
            detalleVentaRepository.save(detalle);

            // Reducir stock del producto
            Producto producto = productoRepository.findById(item.getIdProducto()).orElse(null);
            if (producto != null) {
                producto.setStock(producto.getStock() - item.getCantidad());
                productoRepository.save(producto);
            }
        }

        // Limpiar el carrito
        session.removeAttribute("carrito");
        flash.addFlashAttribute("mensajeOk", "¡Venta #" + ventaGuardada.getIdVenta() + " registrada con éxito y stock actualizado!");

        return "redirect:/ventas/listar";
    }
}