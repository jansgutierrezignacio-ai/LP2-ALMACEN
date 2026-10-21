package com.autopartes.controlador;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import interfaces.IntCliente;
import interfaces.IntMovimiento;
import interfaces.IntProducto;
import interfaces.IntVenta;
import modelo.ItemCarrito;
import modelo.Movimiento;
import modelo.Producto;
import modelo.Usuario;

/**
 * Punto de venta: carrito de compras en la sesión HTTP y confirmación
 * atómica de la venta (cabecera + detalle + descuento de stock en una sola
 * transacción, ver {@code procesos.GestionVenta}). Las rutas de este
 * controlador están restringidas a Administrador y Vendedor por
 * {@code com.autopartes.config.Autorizacion}.
 */
@Controller
public class VentaController {

    @Autowired
    private IntVenta ventaService;

    @Autowired
    private IntProducto productoService;

    @Autowired
    private IntCliente clienteService;

    @Autowired
    private IntMovimiento movimientoService;

    // Pantalla del punto de venta (carrito, catálogo, clientes e historial).
    @GetMapping(value = "/ventas", params = "!type")
    public String listar(Model model, HttpSession session) {
        return mostrarVentas(model, session);
    }

    @GetMapping(value = "/ventas", params = "type=listar")
    public String listarPorTipo(Model model, HttpSession session) {
        return mostrarVentas(model, session);
    }

    private String mostrarVentas(Model model, HttpSession session) {
        List<ItemCarrito> carrito = obtenerCarrito(session);
        model.addAttribute("carrito", carrito);
        model.addAttribute("totalVenta", totalCarrito(carrito));
        model.addAttribute("listaProductos", productoService.listarProducto());
        model.addAttribute("listaClientes", clienteService.listarCliente());
        model.addAttribute("historialVentas", ventaService.listarVentas());
        return "Ventas";
    }

    // Agrega un producto al carrito (valida cantidad y stock disponible,
    // acumulando la cantidad si el producto ya estaba en el carrito).
    @PostMapping(value = "/ventas", params = "type=agregar")
    public String agregarAlCarrito(@RequestParam("idProducto") int idProducto,
                                   @RequestParam("cantidad") int cantidad,
                                   HttpSession session, RedirectAttributes flash) {
        if (cantidad <= 0) {
            flash.addFlashAttribute("mensaje", "La cantidad debe ser mayor a 0.");
            return "redirect:/ventas";
        }
        Producto producto = productoService.obtenerProducto(idProducto);
        if (producto == null) {
            flash.addFlashAttribute("mensaje", "Producto no encontrado.");
            return "redirect:/ventas";
        }

        List<ItemCarrito> carrito = obtenerCarrito(session);
        int cantidadAcumulada = cantidad;
        for (ItemCarrito item : carrito) {
            if (item.getIdProducto() == idProducto) {
                cantidadAcumulada += item.getCantidad();
            }
        }
        if (producto.getStock() < cantidadAcumulada) {
            flash.addFlashAttribute("mensaje", "Stock insuficiente. Stock disponible: " + producto.getStock());
            return "redirect:/ventas";
        }

        boolean existe = false;
        for (ItemCarrito item : carrito) {
            if (item.getIdProducto() == idProducto) {
                item.setCantidad(cantidadAcumulada);
                existe = true;
                break;
            }
        }
        if (!existe) {
            carrito.add(new ItemCarrito(producto.getIdProducto(), producto.getCodigo(),
                    producto.getNombre(), cantidad, producto.getPrecio()));
        }
        session.setAttribute("carrito", carrito);
        return "redirect:/ventas";
    }

    // Quita un producto del carrito antes de confirmar.
    @PostMapping(value = "/ventas", params = "type=eliminar")
    public String eliminarDelCarrito(@RequestParam("idProducto") int idProducto, HttpSession session) {
        List<ItemCarrito> carrito = obtenerCarrito(session);
        carrito.removeIf(item -> item.getIdProducto() == idProducto);
        session.setAttribute("carrito", carrito);
        return "redirect:/ventas";
    }

    // Confirma la venta completa y registra la acción en la bitácora.
    @PostMapping(value = "/ventas", params = "type=confirmar")
    public String confirmarVenta(@RequestParam("idCliente") int idCliente,
                                 HttpSession session, RedirectAttributes flash) {
        List<ItemCarrito> carrito = obtenerCarrito(session);
        if (carrito.isEmpty()) {
            flash.addFlashAttribute("mensaje", "El carrito de ventas está vacío.");
            return "redirect:/ventas";
        }
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        int idVenta = ventaService.confirmarVenta(carrito, idCliente, usuario.getIdUsuario());
        registrarMovimiento(session, "VENTA", "Se registró venta ID: " + idVenta
                + " por " + carrito.size() + " item(s)");
        session.removeAttribute("carrito");
        flash.addFlashAttribute("mensajeOk", "Venta #" + idVenta + " registrada y stock descontado");
        return "redirect:/ventas";
    }

    // ---- Helpers ----

    @SuppressWarnings("unchecked")
    private List<ItemCarrito> obtenerCarrito(HttpSession session) {
        List<ItemCarrito> carrito = (List<ItemCarrito>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
            session.setAttribute("carrito", carrito);
        }
        return carrito;
    }

    private double totalCarrito(List<ItemCarrito> carrito) {
        double total = 0;
        for (ItemCarrito item : carrito) {
            total += item.getSubtotal();
        }
        return total;
    }

    private void registrarMovimiento(HttpSession session, String tipo, String descripcion) {
        Usuario u = (Usuario) session.getAttribute("usuario");
        Movimiento m = new Movimiento();
        m.setTipo(tipo);
        m.setDescripcion(descripcion);
        m.setUsuario(u != null ? u.getUsername() : null);
        movimientoService.registrarMovimiento(m);
    }
}
