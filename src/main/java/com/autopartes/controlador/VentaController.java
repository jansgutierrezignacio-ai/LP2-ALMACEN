package com.autopartes.controlador;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public String listar(@RequestParam(value = "q", required = false) String q,
                         Model model, HttpSession session) {
        return mostrarVentas(model, session, q);
    }

    @GetMapping(value = "/ventas", params = "type=listar")
    public String listarPorTipo(@RequestParam(value = "q", required = false) String q,
                                Model model, HttpSession session) {
        return mostrarVentas(model, session, q);
    }

    private String mostrarVentas(Model model, HttpSession session, String q) {
        List<ItemCarrito> carrito = obtenerCarrito(session);
        String termino = q == null ? "" : q.trim();
        model.addAttribute("carrito", carrito);
        model.addAttribute("enCarrito", cantidadesEnCarrito(carrito));
        model.addAttribute("totalVenta", totalCarrito(carrito));
        model.addAttribute("listaProductos", buscarProductos(termino));
        model.addAttribute("q", termino);
        model.addAttribute("listaClientes", clienteService.listarCliente());
        model.addAttribute("idClienteSeleccionado", session.getAttribute("idClienteSeleccionado"));
        model.addAttribute("fechaHoy", new java.util.Date());
        model.addAttribute("historialVentas", ventaService.listarVentas());
        return "Ventas";
    }

    /**
     * Catálogo a mostrar: completo, o filtrado por nombre/descripción y código.
     * Se combinan los dos procedimientos porque sp_buscarProductoByNombre no
     * mira el código y sp_buscarProducto solo busca por código.
     */
    private List<Producto> buscarProductos(String termino) {
        if (termino.isEmpty()) {
            return productoService.listarProducto();
        }
        List<Producto> encontrados = new ArrayList<>(productoService.buscarProductoByNombre(termino));
        for (Producto p : productoService.buscarProducto(termino)) {
            boolean repetido = false;
            for (Producto ya : encontrados) {
                if (ya.getIdProducto() == p.getIdProducto()) {
                    repetido = true;
                    break;
                }
            }
            if (!repetido) {
                encontrados.add(p);
            }
        }
        return encontrados;
    }

    /** Redirige al punto de venta conservando la búsqueda del catálogo. */
    private String redirigirVentas(String q) {
        if (q == null || q.trim().isEmpty()) {
            return "redirect:/ventas";
        }
        return "redirect:/ventas?q=" + URLEncoder.encode(q.trim(), StandardCharsets.UTF_8);
    }

    // Agrega un producto al carrito (valida cantidad y stock disponible,
    // acumulando la cantidad si el producto ya estaba en el carrito).
    @PostMapping(value = "/ventas", params = "type=agregar")
    public String agregarAlCarrito(@RequestParam("idProducto") int idProducto,
                                   @RequestParam("cantidad") int cantidad,
                                   @RequestParam(value = "q", required = false) String q,
                                   @RequestParam(value = "idCliente", required = false) Integer idCliente,
                                   HttpSession session, RedirectAttributes flash) {
        recordarCliente(session, idCliente);
        if (cantidad <= 0) {
            flash.addFlashAttribute("mensaje", "La cantidad debe ser mayor a 0.");
            return redirigirVentas(q);
        }
        Producto producto = productoService.obtenerProducto(idProducto);
        if (producto == null) {
            flash.addFlashAttribute("mensaje", "Producto no encontrado.");
            return redirigirVentas(q);
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
            return redirigirVentas(q);
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
        return redirigirVentas(q);
    }

    // Cambia la cantidad de un producto ya agregado (botones + / - del carrito).
    // Con cantidad 0 o menos se quita del carrito.
    @PostMapping(value = "/ventas", params = "type=actualizar")
    public String actualizarCantidad(@RequestParam("idProducto") int idProducto,
                                     @RequestParam("cantidad") int cantidad,
                                     @RequestParam(value = "q", required = false) String q,
                                     @RequestParam(value = "idCliente", required = false) Integer idCliente,
                                     HttpSession session, RedirectAttributes flash) {
        recordarCliente(session, idCliente);
        List<ItemCarrito> carrito = obtenerCarrito(session);
        if (cantidad <= 0) {
            carrito.removeIf(item -> item.getIdProducto() == idProducto);
            session.setAttribute("carrito", carrito);
            return redirigirVentas(q);
        }
        Producto producto = productoService.obtenerProducto(idProducto);
        if (producto == null) {
            flash.addFlashAttribute("mensaje", "Producto no encontrado.");
            return redirigirVentas(q);
        }
        if (producto.getStock() < cantidad) {
            flash.addFlashAttribute("mensaje", "Stock insuficiente. Stock disponible: " + producto.getStock());
            return redirigirVentas(q);
        }
        for (ItemCarrito item : carrito) {
            if (item.getIdProducto() == idProducto) {
                item.setCantidad(cantidad);
                break;
            }
        }
        session.setAttribute("carrito", carrito);
        return redirigirVentas(q);
    }

    // Quita un producto del carrito antes de confirmar.
    @PostMapping(value = "/ventas", params = "type=eliminar")
    public String eliminarDelCarrito(@RequestParam("idProducto") int idProducto,
                                     @RequestParam(value = "q", required = false) String q,
                                     @RequestParam(value = "idCliente", required = false) Integer idCliente,
                                     HttpSession session) {
        recordarCliente(session, idCliente);
        List<ItemCarrito> carrito = obtenerCarrito(session);
        carrito.removeIf(item -> item.getIdProducto() == idProducto);
        session.setAttribute("carrito", carrito);
        return redirigirVentas(q);
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
        session.removeAttribute("idClienteSeleccionado");
        flash.addFlashAttribute("mensajeOk", "Venta #" + idVenta + " registrada y stock descontado");
        flash.addFlashAttribute("idVentaRecien", idVenta);
        return "redirect:/ventas";
    }

    // ---- Helpers ----

    /**
     * Guarda el cliente elegido mientras se arma el carrito: cada producto que
     * se agrega o quita recarga la pagina y, sin esto, la seleccion se perdia.
     */
    private void recordarCliente(HttpSession session, Integer idCliente) {
        if (idCliente != null && idCliente > 0) {
            session.setAttribute("idClienteSeleccionado", idCliente);
        }
    }

    /**
     * Cantidad de cada producto que ya esta en el carrito. El catalogo muestra
     * el stock real menos esta cantidad, asi el numero baja al agregar y vuelve
     * al quitar, sin tocar la base de datos (el stock se descuenta al confirmar).
     */
    private Map<Integer, Integer> cantidadesEnCarrito(List<ItemCarrito> carrito) {
        Map<Integer, Integer> cantidades = new HashMap<>();
        for (ItemCarrito item : carrito) {
            cantidades.put(item.getIdProducto(), item.getCantidad());
        }
        return cantidades;
    }

    @SuppressWarnings("unchecked")
    private List<ItemCarrito> obtenerCarrito(HttpSession session) {
        List<ItemCarrito> carrito = (List<ItemCarrito>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
            session.setAttribute("carrito", carrito);
        }
        return carrito;
    }

    private double totalCarrito(List<ItemCarrito> carrito) {        double total = 0;
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
