package interfaces;

import java.util.List;

import modelo.Reporte;
import modelo.Venta;

/**
 * Generación de los dos reportes del sistema en formato PDF con
 * JasperReports, a partir de las mismas consultas que alimentan la vista
 * de reportes (sp_ReporteVentasPorFechas y sp_ReporteDetalleVenta).
 */
public interface IntReportePdf {

    /** PDF del reporte de ventas por rango de fechas. */
    public byte[] generarVentasPorFechas(List<Reporte> datos, String fechaInicio, String fechaFin);

    /** PDF del detalle de productos de una venta. */
    public byte[] generarDetalleVenta(List<Reporte> datos, int idVenta);

    /** PDF de la boleta (comprobante) de una venta, con su cabecera. */
    public byte[] generarBoleta(List<Reporte> datos, Venta venta);
}
