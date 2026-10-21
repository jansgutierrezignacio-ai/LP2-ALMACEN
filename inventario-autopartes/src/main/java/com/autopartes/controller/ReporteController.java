package com.autopartes.controller;

import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.autopartes.repository.IReporteRepository;
import java.util.Date;

@Controller
public class ReporteController {

    private final IReporteRepository reporteRepository;

    public ReporteController(IReporteRepository reporteRepository) {
        this.reporteRepository = reporteRepository;
    }

    // 1. Carga la vista principal con todas las ventas
    @GetMapping("/reportes")
    public String verReportes(Model model) {
        List<Object[]> dataReporte = reporteRepository.findAllReportes();
        
        model.addAttribute("dataReporte", dataReporte);
        model.addAttribute("totalVentas", reporteRepository.contarTotalVentas());
        
        return "reportes"; 
    }

    // 2. Filtra por rango de fechas provenientes del formulario HTML
    @GetMapping("/reportes/filtrarFechas")
    public String filtrarFechas(
            @RequestParam("fechaInicio") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFin,
            Model model) {
        
        List<Object[]> dataReporte = reporteRepository.generarReporteVentasPorFechas(fechaInicio, fechaFin);
        
        model.addAttribute("dataReporte", dataReporte);
        model.addAttribute("totalVentas", dataReporte.size()); // Actualiza el contador con los filtrados
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        
        return "reportes";
    }
}