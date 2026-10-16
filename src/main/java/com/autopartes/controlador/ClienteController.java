package com.autopartes.controlador;

import interfaces.IntCliente;
import modelo.Cliente;       
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class ClienteController {

    @Autowired
    private IntCliente clienteService;

    @GetMapping("/clientes")
    public String listarClientes(Model model) {
        
        
        List<Cliente> lista = clienteService.listarCliente(); 
        
        model.addAttribute("listaClientes", lista);
        return "Clientes"; 
    }
}
