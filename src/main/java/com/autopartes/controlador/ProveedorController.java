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
import interfaces.IntProveedor;
import modelo.Movimiento;
import modelo.Proveedor;
import modelo.Usuario;

@Controller
public class ProveedorController {

    @Autowired
    private IntProveedor proveedorService;

    @Autowired
    private IntMovimiento movimientoService;

    @GetMapping(value = "/proveedores", params = "!type")
    public String listar(Model model) {
        return listarProveedor(model);
    }

    @GetMapping(value = "/proveedores", params = "type=listar")
    public String listarPorTipo(Model model) {
        return listarProveedor(model);
    }

    @GetMapping(value = "/proveedores", params = "type=nuevo")
    public String nuevo(Model model) {
        cargarListado(model);
        model.addAttribute("activeTab", "agregar");
        return "proveedores";
    }

    @GetMapping(value = "/proveedores", params = "type=buscar")
    public String buscar(@RequestParam(value = "txtBuscar", required = false) String txtBuscar, Model model) {
        String texto = txtBuscar == null ? "" : txtBuscar;
        List<Proveedor> data = proveedorService.buscarProveedorByNombre(texto);
        model.addAttribute("dataProveedor", data);
        model.addAttribute("cantReg", data.size());
        return "proveedores";
    }

    @GetMapping(value = "/proveedores", params = "type=info")
    public String info(@RequestParam("id") int id, Model model) {
        cargarListado(model);
        model.addAttribute("proveedorInfo", proveedorService.obtenerProveedor(id));
        return "proveedores";
    }

    @GetMapping(value = "/proveedores", params = "type=infoEdit")
    public String infoEdit(@RequestParam("id") int id, Model model) {
        cargarListado(model);
        model.addAttribute("proveedorDataEdit", proveedorService.obtenerProveedor(id));
        return "proveedores";
    }

    @PostMapping(value = "/proveedores", params = "type=registrar")
    public String registrar(@RequestParam("txtRuc") String ruc,
                            @RequestParam("txtRazonSocial") String razonSocial,
                            @RequestParam("txtTelefono") String telefono,
                            @RequestParam(value = "txtEstado", required = false, defaultValue = "1") int estado,
                            HttpSession session, Model model) {
        Proveedor proveedor = new Proveedor();
        proveedor.setRuc(ruc);
        proveedor.setRazon_social(razonSocial);
        proveedor.setTelefono(telefono);
        proveedor.setEstado(estado);

        int value = proveedorService.crearProveedor(proveedor);
        if (value == 1) {
            registrarMovimiento(session, "REGISTRAR", "Se registró proveedor: " + razonSocial);
            model.addAttribute("mensajeOk", "Proveedor registrado correctamente");
            return listarProveedor(model);
        }
        model.addAttribute("mensaje", "Ocurrió un problema al registrar el proveedor");
        return "msgError";
    }

    @PostMapping(value = "/proveedores", params = "type=editar")
    public String editar(@RequestParam("idProveedor") int idProveedor,
                         @RequestParam("txtRuc") String ruc,
                         @RequestParam("txtRazonSocial") String razonSocial,
                         @RequestParam("txtTelefono") String telefono,
                         @RequestParam(value = "txtEstado", required = false, defaultValue = "1") int estado,
                         HttpSession session, Model model) {
        Proveedor proveedor = new Proveedor();
        proveedor.setId_proveedor(idProveedor);
        proveedor.setRuc(ruc);
        proveedor.setRazon_social(razonSocial);
        proveedor.setTelefono(telefono);
        proveedor.setEstado(estado);

        int value = proveedorService.editarProveedor(proveedor);
        if (value == 1) {
            registrarMovimiento(session, "EDITAR", "Se editó proveedor ID: " + idProveedor);
            model.addAttribute("mensajeOk", "Proveedor actualizado correctamente");
            return listarProveedor(model);
        }
        model.addAttribute("mensaje", "Ocurrió un problema al actualizar el proveedor");
        return "msgError";
    }

    // Eliminación por POST (no por enlace) para evitar operaciones destructivas por CSRF.
    @PostMapping(value = "/proveedores", params = "type=eliminar")
    public String eliminar(@RequestParam("id") int id, HttpSession session, Model model) {
        int value = proveedorService.eliminarProveedor(id);
        if (value == 1) {
            registrarMovimiento(session, "ELIMINAR", "Se eliminó proveedor ID: " + id);
            model.addAttribute("mensajeOk", "Proveedor eliminado correctamente");
            return listarProveedor(model);
        }
        model.addAttribute("mensaje", "Ocurrió un problema al eliminar el proveedor");
        return "msgError";
    }

    private String listarProveedor(Model model) {
        cargarListado(model);
        return "proveedores";
    }

    private void cargarListado(Model model) {
        List<Proveedor> dataProveedor = proveedorService.listarProveedor();
        model.addAttribute("dataProveedor", dataProveedor);
        model.addAttribute("cantReg", dataProveedor.size());
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
