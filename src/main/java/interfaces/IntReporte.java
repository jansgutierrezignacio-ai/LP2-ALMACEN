package interfaces;

import java.util.List;
import modelo.Reporte;

public interface IntReporte {
    public List<Reporte> listarReportePorFechas(String inicio, String fin);
    public List<Reporte> obtenerDetalleVenta(int idVenta);
    public int contarReporteVenta();
}