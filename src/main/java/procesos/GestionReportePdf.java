package procesos;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import interfaces.IntReportePdf;
import modelo.Reporte;
import modelo.Venta;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Genera los PDF de los reportes con JasperReports:
 * <ol>
 *   <li>compila la plantilla .jrxml (recursos en {@code classpath:/reportes/})
 *       una sola vez y la deja en memoria;</li>
 *   <li>rellena el reporte con los datos de la consulta (se recorren con
 *       JRBeanCollectionDataSource, que usa los getters del DTO {@code Reporte});</li>
 *   <li>exporta el resultado a bytes PDF.</li>
 * </ol>
 * La vista de reportes y este servicio usan los MISMOS procedimientos
 * almacenados, por lo que el PDF coincide con lo mostrado en pantalla.
 */
@Service
public class GestionReportePdf implements IntReportePdf {

    private JasperReport plantillaFechas;
    private JasperReport plantillaDetalle;
    private JasperReport plantillaBoleta;

    @Override
    public byte[] generarVentasPorFechas(List<Reporte> datos, String fechaInicio, String fechaFin) {
        plantillaFechas = compilar("/reportes/ventas_por_fechas.jrxml", plantillaFechas);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("pFechaInicio", fechaInicio);
        parametros.put("pFechaFin", fechaFin);
        return rellenarYExportar(plantillaFechas, datos, parametros);
    }

    @Override
    public byte[] generarDetalleVenta(List<Reporte> datos, int idVenta) {
        plantillaDetalle = compilar("/reportes/detalle_venta.jrxml", plantillaDetalle);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("pIdVenta", idVenta);
        return rellenarYExportar(plantillaDetalle, datos, parametros);
    }

    @Override
    public byte[] generarBoleta(List<Reporte> datos, Venta venta) {
        plantillaBoleta = compilar("/reportes/boleta_venta.jrxml", plantillaBoleta);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("pIdVenta", venta.getIdVenta());
        parametros.put("pCliente", venta.getNombreCliente());
        parametros.put("pFecha", venta.getFecha());
        parametros.put("pVendedor", venta.getUsername());
        return rellenarYExportar(plantillaBoleta, datos, parametros);
    }

    /** Compila la plantilla .jrxml una sola vez (la compilación es lo costoso). */    private JasperReport compilar(String ruta, JasperReport yaCompilada) {
        if (yaCompilada != null) {
            return yaCompilada;
        }
        try (InputStream entrada = getClass().getResourceAsStream(ruta)) {
            if (entrada == null) {
                throw new IllegalStateException("No se encontró la plantilla del reporte: " + ruta);
            }
            return JasperCompileManager.compileReport(entrada);
        } catch (JRException | IOException e) {
            throw new IllegalStateException("No se pudo compilar la plantilla del reporte: " + ruta, e);
        }
    }

    /** Rellena el reporte con los datos y lo exporta a PDF. */
    private byte[] rellenarYExportar(JasperReport plantilla, List<Reporte> datos, Map<String, Object> parametros) {
        try {
            JRBeanCollectionDataSource fuente = new JRBeanCollectionDataSource(datos);
            var impresion = JasperFillManager.fillReport(plantilla, parametros, fuente);
            return JasperExportManager.exportReportToPdf(impresion);
        } catch (JRException e) {
            throw new IllegalStateException("No se pudo generar el PDF del reporte", e);
        }
    }
}
