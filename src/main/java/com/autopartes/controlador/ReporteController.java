package com.autopartes.controlador;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import interfaces.IntReporte;
import interfaces.IntReportePdf;
import modelo.Reporte;

@Controller
public class ReporteController {

    @Autowired
    private IntReporte reporteService;

    @Autowired
    private IntReportePdf reportePdfService;

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

    // Descarga en PDF del reporte de ventas por fechas (JasperReports).
    // Usa los mismos datos que la consulta en pantalla; la autorización
    // (/reportes, operación de lectura) la aplica AuthFilter como a cualquier ruta.
    @GetMapping("/reportes/pdf")
    public void descargarPorFechas(@RequestParam("txtFechaInicio") String fechaInicio,
                                   @RequestParam("txtFechaFin") String fechaFin,
                                   HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (fechaInicio == null || fechaInicio.trim().isEmpty()
                || fechaFin == null || fechaFin.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/reportes");
            return;
        }
        List<Reporte> datos = reporteService.listarReportePorFechas(fechaInicio, fechaFin);
        byte[] pdf = reportePdfService.generarVentasPorFechas(datos, fechaInicio, fechaFin);
        enviarPdf(response, pdf, "reporte_ventas_" + fechaInicio + "_" + fechaFin + ".pdf");
    }

    // Descarga en PDF del detalle de una venta (JasperReports).
    @GetMapping("/reportes/pdf/detalle")
    public void descargarDetalle(@RequestParam("id") int idVenta,
                                 HttpServletResponse response) throws IOException {
        List<Reporte> datos = reporteService.obtenerDetalleVenta(idVenta);
        byte[] pdf = reportePdfService.generarDetalleVenta(datos, idVenta);
        enviarPdf(response, pdf, "reporte_venta_" + idVenta + ".pdf");
    }

    /** Escribe los bytes del PDF en la respuesta como archivo adjunto. */
    private void enviarPdf(HttpServletResponse response, byte[] pdf, String nombreArchivo) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombreArchivo + "\"");
        response.setContentLength(pdf.length);
        response.getOutputStream().write(pdf);
        response.getOutputStream().flush();
    }
}
