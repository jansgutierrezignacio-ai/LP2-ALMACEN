package com.autopartes.controlador;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import interfaces.IntMovimiento;
import interfaces.IntProducto;
import interfaces.IntProveedor;
import modelo.Movimiento;
import modelo.Producto;
import modelo.Usuario;

@Controller
public class ProductoController {

    @Autowired
    private IntProducto productoService;

    @Autowired
    private IntProveedor proveedorService;

    @Autowired
    private IntMovimiento movimientoService;

    // Listar (acceso directo a /productos sin parámetro type)
    @GetMapping(value = "/productos", params = "!type")
    public String listar(Model model) {
        return listarProducto(model);
    }

    @GetMapping(value = "/productos", params = "type=listar")
    public String listarPorTipo(Model model) {
        return listarProducto(model);
    }

    @GetMapping(value = "/productos", params = "type=nuevo")
    public String nuevo(Model model) {
        cargarListado(model);
        model.addAttribute("activeTab", "agregar");
        return "Productos";
    }

    @GetMapping(value = "/productos", params = "type=buscar")
    public String buscar(@RequestParam(value = "txtBuscar", required = false) String txtBuscar, Model model) {
        String texto = txtBuscar == null ? "" : txtBuscar;
        List<Producto> data = productoService.buscarProductoByNombre(texto);
        model.addAttribute("dataProducto", data);
        model.addAttribute("cantReg", data.size());
        model.addAttribute("listProveedor", proveedorService.listarProveedor());
        model.addAttribute("txtBuscar", texto);
        return "Productos";
    }

    @GetMapping(value = "/productos", params = "type=info")
    public String info(@RequestParam("id") int id, Model model) {
        cargarListado(model);
        model.addAttribute("productoInfo", productoService.obtenerProducto(id));
        return "Productos";
    }

    @GetMapping(value = "/productos", params = "type=infoEdit")
    public String infoEdit(@RequestParam("id") int id, Model model) {
        cargarListado(model);
        model.addAttribute("productoDataEdit", productoService.obtenerProducto(id));
        return "Productos";
    }

    @PostMapping(value = "/productos", params = "type=registrar")
    public String registrar(@RequestParam("txtCodigo") String codigo,
                            @RequestParam("txtNombre") String nombre,
                            @RequestParam("txtDescripcion") String descripcion,
                            @RequestParam("txtPrecio") double precio,
                            @RequestParam("txtStock") int stock,
                            @RequestParam("cboProveedor") int idProveedor,
                            HttpSession session, Model model) {
        Producto producto = new Producto();
        producto.setCodigo(codigo);
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setIdProveedor(idProveedor);

        int value = productoService.crearProducto(producto);
        if (value == 1) {
            registrarMovimiento(session, "REGISTRAR", "Se registró producto: " + nombre);
            model.addAttribute("mensajeOk", "Producto registrado correctamente");
            return listarProducto(model);
        }
        model.addAttribute("mensaje", "Ocurrió un problema al registrar el producto");
        return "msgError";
    }

    @PostMapping(value = "/productos", params = "type=editar")
    public String editar(@RequestParam("idProducto") int idProducto,
                         @RequestParam("txtCodigo") String codigo,
                         @RequestParam("txtNombre") String nombre,
                         @RequestParam("txtDescripcion") String descripcion,
                         @RequestParam("txtPrecio") double precio,
                         @RequestParam("txtStock") int stock,
                         @RequestParam("cboProveedor") int idProveedor,
                         HttpSession session, Model model) {
        Producto producto = new Producto();
        producto.setIdProducto(idProducto);
        producto.setCodigo(codigo);
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setIdProveedor(idProveedor);

        int value = productoService.editarProducto(producto);
        if (value == 1) {
            registrarMovimiento(session, "EDITAR", "Se editó producto ID: " + nombre + " - " + idProducto);
            model.addAttribute("mensajeOk", "Producto actualizado correctamente");
            return listarProducto(model);
        }
        model.addAttribute("mensaje", "Ocurrió un problema al actualizar el producto");
        return "msgError";
    }

    // La eliminación se atiende por POST (no por GET) para evitar operaciones
    // destructivas mediante enlaces o precarga del navegador (CSRF).
    @PostMapping(value = "/productos", params = "type=eliminar")
    public String eliminar(@RequestParam("id") int id, HttpSession session, Model model) {
        int value = productoService.eliminarProducto(id);
        if (value == 1) {
            registrarMovimiento(session, "ELIMINAR", "Se eliminó producto ID: " + id);
            model.addAttribute("mensajeOk", "Producto eliminado correctamente");
            return listarProducto(model);
        }
        model.addAttribute("mensaje", "Ocurrió un problema al eliminar el producto");
        return "msgError";
    }

    private String listarProducto(Model model) {
        cargarListado(model);
        return "Productos";
    }

    private void cargarListado(Model model) {
        List<Producto> dataProducto = productoService.listarProducto();
        model.addAttribute("dataProducto", dataProducto);
        model.addAttribute("cantReg", dataProducto.size());
        model.addAttribute("listProveedor", proveedorService.listarProveedor());
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
