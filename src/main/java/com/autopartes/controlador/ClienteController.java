package com.autopartes.controlador;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import interfaces.IntCliente;
import interfaces.IntMovimiento;
import modelo.Cliente;
import modelo.Movimiento;
import modelo.Usuario;

@Controller
public class ClienteController {

    @Autowired
    private IntCliente clienteService;

    @Autowired
    private IntMovimiento movimientoService;

    @GetMapping(value = "/clientes", params = "!type")
    public String listar(Model model) {
        return listarCliente(model);
    }

    @GetMapping(value = "/clientes", params = "type=listar")
    public String listarPorTipo(Model model) {
        return listarCliente(model);
    }

    @GetMapping(value = "/clientes", params = "type=nuevo")
    public String nuevo(Model model) {
        cargarListado(model);
        model.addAttribute("activeTab", "agregar");
        return "clientes";
    }

    @GetMapping(value = "/clientes", params = "type=buscar")
    public String buscar(@RequestParam(value = "txtBuscar", required = false) String txtBuscar, Model model) {
        String texto = txtBuscar == null ? "" : txtBuscar;
        List<Cliente> data = clienteService.buscarClienteByNombre(texto);
        model.addAttribute("dataCliente", data);
        model.addAttribute("cantReg", data.size());
        model.addAttribute("txtBuscar", texto);
        return "clientes";
    }

    @GetMapping(value = "/clientes", params = "type=info")
    public String info(@RequestParam("id") int id, Model model) {
        cargarListado(model);
        model.addAttribute("dataClienteInfo", clienteService.obtenerCliente(id));
        return "clientes";
    }

    @GetMapping(value = "/clientes", params = "type=infoEdit")
    public String infoEdit(@RequestParam("id") int id, Model model) {
        cargarListado(model);
        model.addAttribute("clienteDataEdit", clienteService.obtenerCliente(id));
        model.addAttribute("activeTab", "editar");
        return "clientes";
    }

    @PostMapping(value = "/clientes", params = "type=registrar")
    public String registrar(@RequestParam("txtDni") String dni,
                            @RequestParam("txtNombre") String nombre,
                            @RequestParam("txtApellido") String apellido,
                            @RequestParam("txtTelefono") String telefono,
                            HttpSession session, Model model) {
        Cliente cliente = new Cliente();
        cliente.setDni(dni);
        cliente.setNombre(nombre);
        cliente.setApellido(apellido);
        cliente.setTelefono(telefono);

        int value = clienteService.crearCliente(cliente);
        if (value == 1) {
            registrarMovimiento(session, "REGISTRAR", "Se registró cliente: " + nombre + " " + apellido);
            model.addAttribute("mensajeOk", "Cliente registrado correctamente");
            return listarCliente(model);
        }
        model.addAttribute("mensaje", "Ocurrió un problema al registrar");
        return "msgError";
    }

    @PostMapping(value = "/clientes", params = "type=editar")
    public String editar(@RequestParam("idCliente") int idCliente,
                         @RequestParam("txtDni") String dni,
                         @RequestParam("txtNombre") String nombre,
                         @RequestParam("txtApellido") String apellido,
                         @RequestParam("txtTelefono") String telefono,
                         HttpSession session, Model model) {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(idCliente);
        cliente.setDni(dni);
        cliente.setNombre(nombre);
        cliente.setApellido(apellido);
        cliente.setTelefono(telefono);

        int value = clienteService.editarCliente(cliente);
        if (value == 1) {
            registrarMovimiento(session, "EDITAR", "Se editó cliente ID: " + idCliente);
            model.addAttribute("mensajeOk", "Cliente actualizado correctamente");
            return listarCliente(model);
        }
        model.addAttribute("mensaje", "Ocurrió un problema al actualizar");
        return "msgError";
    }

    // Eliminación por POST (no por enlace) para evitar operaciones destructivas por CSRF.
    @PostMapping(value = "/clientes", params = "type=eliminar")
    public String eliminar(@RequestParam("id") int id, HttpSession session, Model model) {
        int value = clienteService.eliminarCliente(id);
        if (value == 1) {
            registrarMovimiento(session, "ELIMINAR", "Se eliminó cliente ID: " + id);
            model.addAttribute("mensajeOk", "Cliente eliminado correctamente");
            return listarCliente(model);
        }
        model.addAttribute("mensaje", "Ocurrió un problema al eliminar");
        return "msgError";
    }

    private String listarCliente(Model model) {
        cargarListado(model);
        return "clientes";
    }

    private void cargarListado(Model model) {
        List<Cliente> dataCliente = clienteService.listarCliente();
        model.addAttribute("dataCliente", dataCliente);
        model.addAttribute("cantReg", dataCliente.size());
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
