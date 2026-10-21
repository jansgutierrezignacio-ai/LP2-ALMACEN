package com.autopartes.controlador;

import java.io.IOException;
import java.util.List;

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

    /** Ventas por pagina en el listado del reporte. */
    private static final int TAMANO_PAGINA = 10;

    @Autowired
    private IntReporte reporteService;

    @Autowired
    private IntReportePdf reportePdfService;

    // Al entrar a /reportes se muestra directamente el listado, para no obligar
    // a elegir un rango de fechas antes de ver datos.
    @GetMapping(value = "/reportes", params = "!type")
    public String formulario() {
        return "redirect:/reportes?type=listar";
    }

    /**
     * Listado de ventas. Sin fechas muestra TODAS las ventas (paginadas); con
     * ambas fechas filtra por rango. La paginación se hace aquí porque los
     * procedimientos devuelven el conjunto completo y el volumen es pequeño.
     */
    @GetMapping(value = "/reportes", params = "type=listar")
    public String listar(@RequestParam(value = "txtFechaInicio", required = false) String fechaInicio,
                         @RequestParam(value = "txtFechaFin", required = false) String fechaFin,
                         @RequestParam(value = "pagina", required = false, defaultValue = "1") int pagina,
                         Model model) {
        boolean conFiltro = fechaInicio != null && !fechaInicio.trim().isEmpty()
                && fechaFin != null && !fechaFin.trim().isEmpty();
        List<Reporte> todas = conFiltro
                ? reporteService.listarReportePorFechas(fechaInicio, fechaFin)
                : reporteService.listarTodasLasVentas();

        int totalRegistros = todas.size();
        int totalPaginas = Math.max(1, (int) Math.ceil(totalRegistros / (double) TAMANO_PAGINA));
        // Si piden una página fuera de rango (o negativa) se ajusta a la válida.
        int paginaActual = Math.min(Math.max(pagina, 1), totalPaginas);
        int desde = (paginaActual - 1) * TAMANO_PAGINA;
        int hasta = Math.min(desde + TAMANO_PAGINA, totalRegistros);
        List<Reporte> paginaDatos = desde >= totalRegistros
                ? java.util.Collections.emptyList()
                : todas.subList(desde, hasta);

        model.addAttribute("dataReporte", paginaDatos);
        model.addAttribute("fInicio", fechaInicio);
        model.addAttribute("fFin", fechaFin);
        model.addAttribute("conFiltro", conFiltro);
        model.addAttribute("paginaActual", paginaActual);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("totalRegistros", totalRegistros);
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

    // Descarga en PDF del reporte de ventas (JasperReports). Si se indica un
    // rango de fechas se filtra; si no, se exportan todas las ventas.
    // Usa los mismos datos que la consulta en pantalla; la autorización
    // (/reportes, operación de lectura) la aplica AuthFilter como a cualquier ruta.
    @GetMapping("/reportes/pdf")
    public void descargarPorFechas(@RequestParam(value = "txtFechaInicio", required = false) String fechaInicio,
                                   @RequestParam(value = "txtFechaFin", required = false) String fechaFin,
                                   HttpServletResponse response) throws IOException {
        boolean conFiltro = fechaInicio != null && !fechaInicio.trim().isEmpty()
                && fechaFin != null && !fechaFin.trim().isEmpty();
        List<Reporte> datos = conFiltro
                ? reporteService.listarReportePorFechas(fechaInicio, fechaFin)
                : reporteService.listarTodasLasVentas();
        // La plantilla muestra "Del <inicio> al <fin>"; con "-" imprime "Todas las ventas".
        String etiquetaInicio = conFiltro ? fechaInicio : "-";
        String etiquetaFin = conFiltro ? fechaFin : "-";
        byte[] pdf = reportePdfService.generarVentasPorFechas(datos, etiquetaInicio, etiquetaFin);
        String nombre = conFiltro
                ? "reporte_ventas_" + fechaInicio + "_" + fechaFin + ".pdf"
                : "reporte_ventas_todas.pdf";
        enviarPdf(response, pdf, nombre);
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
