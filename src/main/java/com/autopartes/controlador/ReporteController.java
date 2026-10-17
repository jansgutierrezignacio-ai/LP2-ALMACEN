package com.autopartes.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import interfaces.IntReporte;
import modelo.Reporte;

@Controller
public class ReporteController {

    @Autowired
    private IntReporte reporteService;

    // Muestra el formulario de reportes sin datos.
    @GetMapping(value = "/reportes", params = "!type")
    public String formulario() {
        return "Reporte";
    }

    // Consulta de ventas por rango de fechas.
    @GetMapping(value = "/reportes", params = "type=listar")
    public String listar(@RequestParam(value = "txtFechaInicio", required = false) String fechaInicio,
                         @RequestParam(value = "txtFechaFin", required = false) String fechaFin,
                         Model model) {
        List<Reporte> lista;
        if (fechaInicio == null || fechaInicio.trim().isEmpty()
                || fechaFin == null || fechaFin.trim().isEmpty()) {
            // Sin rango de fechas no se consulta el procedimiento.
            lista = java.util.Collections.emptyList();
        } else {
            lista = reporteService.listarReportePorFechas(fechaInicio, fechaFin);
        }
        model.addAttribute("dataReporte", lista);
        model.addAttribute("fInicio", fechaInicio);
        model.addAttribute("fFin", fechaFin);
        return "Reporte";
    }

    // Detalle de una venta seleccionada.
    @GetMapping(value = "/reportes", params = "type=detalle")
    public String detalle(@RequestParam("id") int idVenta, Model model) {
        List<Reporte> detalle = reporteService.obtenerDetalleVenta(idVenta);
        model.addAttribute("detalleVenta", detalle);
        model.addAttribute("nroVenta", idVenta);
        return "Reporte";
    }
}
