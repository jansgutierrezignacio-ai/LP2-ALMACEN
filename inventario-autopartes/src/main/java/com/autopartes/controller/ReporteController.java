package com.autopartes.controller;

import java.util.Date;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.autopartes.repository.IReporteRepository;

@Controller
public class ReporteController {

    private final IReporteRepository repositorio;

    public ReporteController(IReporteRepository repositorio) {
        this.repositorio = repositorio;
    }

    @GetMapping("/reportes")
    public String listarReportes(Model model) {
        // Usamos una consulta general adaptada a Object[] para mantener compatibilidad con la tabla
        List<Object[]> dataReporte = repositorio.findAllReportes();
        long totalVentas = repositorio.contarTotalVentas();
        
        model.addAttribute("dataReporte", dataReporte);
        model.addAttribute("totalVentas", totalVentas);
        return "Reportes";
    }

    @GetMapping("/reportes/filtrarFechas")
    public String filtrarReporteFechas(
            @RequestParam("fechaInicio") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFin,
            Model model) {
        
        List<Object[]> dataReporte = repositorio.generarReporteVentasPorFechas(fechaInicio, fechaFin);
        long totalVentas = repositorio.contarTotalVentas();
        
        model.addAttribute("dataReporte", dataReporte);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("totalVentas", totalVentas);
        
        return "Reportes";
    }

    @GetMapping("/reportes/detalle")
    public String detalleVenta(@RequestParam("idVenta") int idVenta, Model model) {
        List<Object[]> detalleVenta = repositorio.generarReporteDetalleVenta(idVenta);
        
        model.addAttribute("detalleVenta", detalleVenta);
        model.addAttribute("idVentaBuscada", idVenta);
        
        return "Reportes"; 
    }
}