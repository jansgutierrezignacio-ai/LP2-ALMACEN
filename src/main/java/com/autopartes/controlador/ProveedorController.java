package com.autopartes.controlador;

import interfaces.IntProveedor; 
import modelo.Proveedor;      
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class ProveedorController {

    @Autowired
    private IntProveedor proveedorService;

    @GetMapping("/proveedores")
    public String listarProveedores(Model model) {
        
        // Asumimos que sigue el mismo patrón en singular: listarProveedor()
        List<Proveedor> lista = proveedorService.listarProveedor(); 
        
        model.addAttribute("listaProveedores", lista);
        return "Proveedores";
    }
}
