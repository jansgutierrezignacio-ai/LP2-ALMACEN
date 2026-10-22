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
import interfaces.IntVenta;
import modelo.Reporte;
import modelo.Venta;

@Controller
public class ReporteController {

    /** Ventas por pagina en el listado del reporte. */
    private static final int TAMANO_PAGINA = 10;

    /**
     * Tope de filas para generar el PDF. JasperReports arma el documento
     * completo en memoria, asi que se acota. Medido en este equipo: 20.000
     * ventas ~ 466 paginas, ~3,6 MB, ~7,6 s y un pico de ~240 MB de RAM;
     * con 24.000 ya sube a ~270 MB y con 50.000 a ~450 MB. Se deja en 20.000
     * para no pasar de ~300 MB de consumo extra.
     */
    private static final int MAX_FILAS_PDF = 20000;

    @Autowired
    private IntReporte reporteService;

    @Autowired
    private IntReportePdf reportePdfService;

    @Autowired
    private IntVenta ventaService;

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
                         @RequestParam(value = "limite", required = false) Integer limite,
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
        model.addAttribute("maxPdf", MAX_FILAS_PDF);
        // Si se llega aquí tras rechazar un PDF demasiado grande, se muestra el aviso.
        model.addAttribute("avisoLimite", limite);
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
                                   HttpServletRequest request,
                                   HttpServletResponse response) throws IOException {
        boolean conFiltro = fechaInicio != null && !fechaInicio.trim().isEmpty()
                && fechaFin != null && !fechaFin.trim().isEmpty();

        // Sin filtro se cuenta ANTES de consultar: si hay miles de ventas no se
        // materializan solo para descubrir que superan el tope.
        if (!conFiltro) {
            int totalVentas = reporteService.contarReporteVenta();
            if (totalVentas > MAX_FILAS_PDF) {
                response.sendRedirect(urlAvisoLimite(request, totalVentas, null, null));
                return;
            }
        }

        List<Reporte> datos = conFiltro
                ? reporteService.listarReportePorFechas(fechaInicio, fechaFin)
                : reporteService.listarTodasLasVentas();

        // Con filtro, el único modo de saber el tamaño es la lista ya cargada.
        if (datos.size() > MAX_FILAS_PDF) {
            response.sendRedirect(urlAvisoLimite(request, datos.size(), fechaInicio, fechaFin));
            return;
        }

        // La plantilla muestra "Del <inicio> al <fin>"; con "-" imprime "Todas las ventas".
        String etiquetaInicio = conFiltro ? fechaInicio : "-";
        String etiquetaFin = conFiltro ? fechaFin : "-";
        byte[] pdf = reportePdfService.generarVentasPorFechas(datos, etiquetaInicio, etiquetaFin);
        String nombre = conFiltro
                ? "reporte_ventas_" + fechaInicio + "_" + fechaFin + ".pdf"
                : "reporte_ventas_todas.pdf";
        enviarPdf(response, pdf, nombre);
    }

    /**
     * Vuelve al listado con un aviso de que el PDF supera el tope. Se conserva
     * el rango de fechas para que el usuario solo tenga que ajustarlo.
     */
    private String urlAvisoLimite(HttpServletRequest request, int total, String inicio, String fin) {
        StringBuilder url = new StringBuilder(request.getContextPath());
        url.append("/reportes?type=listar&limite=").append(total);
        if (inicio != null && !inicio.trim().isEmpty()) {
            url.append("&txtFechaInicio=").append(inicio);
        }
        if (fin != null && !fin.trim().isEmpty()) {
            url.append("&txtFechaFin=").append(fin);
        }
        return url.toString();
    }

    // Descarga en PDF del detalle de una venta (JasperReports).
    @GetMapping("/reportes/pdf/detalle")
    public void descargarDetalle(@RequestParam("id") int idVenta,
                                 HttpServletResponse response) throws IOException {
        List<Reporte> datos = reporteService.obtenerDetalleVenta(idVenta);
        byte[] pdf = reportePdfService.generarDetalleVenta(datos, idVenta);
        enviarPdf(response, pdf, "reporte_venta_" + idVenta + ".pdf");
    }

    /**
     * Boleta (comprobante) de una venta en PDF. La cabecera (cliente, fecha y
     * vendedor) sale de sp_listarVentas y las líneas de sp_ReporteDetalleVenta,
     * las mismas consultas que alimentan el historial y el reporte.
     */
    @GetMapping("/reportes/boleta")
    public void descargarBoleta(@RequestParam("id") int idVenta,
                                HttpServletResponse response) throws IOException {
        Venta venta = null;
        for (Venta v : ventaService.listarVentas()) {
            if (v.getIdVenta() == idVenta) {
                venta = v;
                break;
            }
        }
        if (venta == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Venta no encontrada");
            return;
        }
        List<Reporte> datos = reporteService.obtenerDetalleVenta(idVenta);
        byte[] pdf = reportePdfService.generarBoleta(datos, venta);
        enviarPdf(response, pdf, "boleta_venta_" + idVenta + ".pdf");
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
