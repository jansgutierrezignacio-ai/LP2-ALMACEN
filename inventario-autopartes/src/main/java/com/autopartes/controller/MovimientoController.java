package com.autopartes.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.autopartes.entity.Movimiento;
import com.autopartes.repository.IMovimientoRepository;

@Controller
public class MovimientoController {

    private final IMovimientoRepository repositorio;

    public MovimientoController(IMovimientoRepository repositorio) {
        this.repositorio = repositorio;
    }

    @GetMapping("/movimientos/listar")
    public String listarMovimientos(Model model) {
        List<Movimiento> dataMovimiento = repositorio.findAll();
        model.addAttribute("dataMovimiento", dataMovimiento);
        model.addAttribute("cantReg", dataMovimiento.size());
        return "Movimientos"; 
    }

    @GetMapping("/movimientos/buscar")
    public String buscarMovimientos(@RequestParam(value = "txtBuscar", defaultValue = "") String texto, Model model) {
        List<Movimiento> data = repositorio.findByUsuarioContainingIgnoreCase(texto);
        model.addAttribute("dataMovimiento", data);
        model.addAttribute("cantReg", data.size());
        model.addAttribute("txtBuscar", texto);
        return "Movimientos";
    }
}