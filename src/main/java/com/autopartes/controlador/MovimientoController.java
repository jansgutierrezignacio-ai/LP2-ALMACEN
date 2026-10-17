package com.autopartes.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import interfaces.IntMovimiento;
import modelo.Movimiento;

@Controller
public class MovimientoController {

    @Autowired
    private IntMovimiento movimientoService;

    // Bitácora de movimientos (auditoría de las operaciones).
    @GetMapping(value = "/movimientos", params = "!type")
    public String listar(Model model) {
        return listarMovimientos(model);
    }

    @GetMapping(value = "/movimientos", params = "type=listar")
    public String listarPorTipo(Model model) {
        return listarMovimientos(model);
    }

    private String listarMovimientos(Model model) {
        List<Movimiento> lista = movimientoService.listarMovimientos();
        model.addAttribute("dataMovimiento", lista);
        model.addAttribute("cantReg", lista.size());
        return "Movimientos";
    }
}
