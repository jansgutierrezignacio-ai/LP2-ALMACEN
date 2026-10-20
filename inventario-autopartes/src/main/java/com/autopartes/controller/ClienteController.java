package com.autopartes.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.autopartes.entity.Cliente;
import com.autopartes.repository.IClienteRepository;
import com.autopartes.config.MovimientoService;
import jakarta.servlet.http.HttpSession;

@Controller
public class ClienteController {

    private final IClienteRepository repositorio;
    private final MovimientoService movimientoService; // Inyectamos el servicio de movimientos

    public ClienteController(IClienteRepository repositorio, MovimientoService movimientoService) {
        this.repositorio = repositorio;
        this.movimientoService = movimientoService;
    }

    // Mapea tanto /clientes como /clientes/listar
    @GetMapping({"/clientes", "/clientes/listar"})
    public String listarCliente(Model model) {
        List<Cliente> dataCliente = repositorio.listarClientesSP();
        model.addAttribute("dataCliente", dataCliente);
        model.addAttribute("cantReg", dataCliente.size());
        model.addAttribute("cliente", new Cliente()); 
        return "Clientes"; 
    }

    @GetMapping("/clientes/nuevo")
    public String nuevoCliente(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("activeTab", "agregar");
        List<Cliente> dataCliente = repositorio.listarClientesSP();
        model.addAttribute("dataCliente", dataCliente);
        model.addAttribute("cantReg", dataCliente.size());
        return "Clientes";
    }

    @PostMapping("/clientes/registrar")
    public String registrarCliente(@ModelAttribute Cliente cliente, Model model, HttpSession session) {
        int value = repositorio.registrarClienteSP(
            cliente.getDni(), 
            cliente.getNombre(), 
            cliente.getApellido(), 
            cliente.getTelefono()
        );

        if (value > 0) {
            // REGISTRAR MOVIMIENTO
            movimientoService.registrar(
                "REGISTRAR", 
                "CLIENTES", 
                "Se registró cliente: " + cliente.getNombre() + " " + cliente.getApellido(), 
                obtenerUsuarioActual(session)
            );

            model.addAttribute("mensajeOk", "Cliente registrado correctamente");
        } else {
            model.addAttribute("mensaje", "Ocurrió un problema al registrar");
        }
        return "redirect:/clientes/listar";
    }

    @GetMapping("/clientes/buscar")
    public String buscarCliente(@RequestParam(value = "txtBuscar", defaultValue = "") String texto, Model model) {
        List<Cliente> data = repositorio.buscarClienteByNombreSP(texto);
        model.addAttribute("dataCliente", data);
        model.addAttribute("cantReg", data.size());
        model.addAttribute("txtBuscar", texto);
        model.addAttribute("cliente", new Cliente());
        return "Clientes";
    }

    @GetMapping("/clientes/infoEdit")
    public String obtenerClienteEdit(@RequestParam("id") int id, Model model) {
        Cliente cliente = repositorio.findById(id).orElse(null);
        model.addAttribute("clienteDataEdit", cliente);
        
        List<Cliente> dataCliente = repositorio.listarClientesSP();
        model.addAttribute("dataCliente", dataCliente);
        model.addAttribute("cantReg", dataCliente.size());
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("activeTab", "editar");
        return "Clientes";
    }

    @PostMapping("/clientes/editar")
    public String editarCliente(@ModelAttribute Cliente cliente, Model model, HttpSession session) {
        int value = repositorio.actualizarClienteSP(
            cliente.getIdCliente(),
            cliente.getDni(), 
            cliente.getNombre(), 
            cliente.getApellido(), 
            cliente.getTelefono()
        );

        if (value > 0) {
            // REGISTRAR MOVIMIENTO DE ACTUALIZACIÓN
            movimientoService.registrar(
                "ACTUALIZAR", 
                "CLIENTES", 
                "Se actualizó cliente: " + cliente.getNombre() + " " + cliente.getApellido(), 
                obtenerUsuarioActual(session)
            );

            model.addAttribute("mensajeOk", "Cliente actualizado correctamente");
        } else {
            model.addAttribute("mensaje", "Ocurrió un problema al actualizar");
        }
        return "redirect:/clientes/listar";
    }

    @GetMapping("/clientes/eliminar")
    public String eliminarCliente(@RequestParam("id") int id, Model model, HttpSession session) {
        // Buscamos los datos del cliente antes de eliminarlo para detallar el movimiento
        Cliente cli = repositorio.findById(id).orElse(null);
        
        int value = repositorio.eliminarClienteSP(id);
        if (value > 0) {
            // REGISTRAR MOVIMIENTO DE ELIMINACIÓN
            String nombreCli = (cli != null) ? cli.getNombre() + " " + cli.getApellido() : "ID " + id;
            movimientoService.registrar(
                "ELIMINAR", 
                "CLIENTES", 
                "Se eliminó cliente: " + nombreCli, 
                obtenerUsuarioActual(session)
            );

            model.addAttribute("mensajeOk", "Cliente eliminado correctamente");
        } else {
            model.addAttribute("mensaje", "Ocurrió un problema al eliminar");
        }
        return "redirect:/clientes/listar";
    }

    // Método auxiliar para extraer el usuario de la sesión
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