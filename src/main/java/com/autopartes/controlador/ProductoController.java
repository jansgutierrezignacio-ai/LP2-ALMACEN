package com.autopartes.controlador;

import interfaces.IntProducto;
import modelo.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class ProductoController {

    @Autowired
    private IntProducto productoService;

    @GetMapping("/productos")
    public String listarProductos(Model model) {
        
        
        List<Producto> lista = productoService.listarProducto(); 
        
        model.addAttribute("listaProductos", lista);
        return "Productos"; 
    }
}
