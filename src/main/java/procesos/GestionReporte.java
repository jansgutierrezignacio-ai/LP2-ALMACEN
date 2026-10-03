package procesos;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import interfaces.IntReporte;
import modelo.Reporte;
import util.MysqlConexion;

public class GestionReporte implements IntReporte {

    @Override
    public List<Reporte> listarReportePorFechas(String inicio, String fin) {
        List<Reporte> lista = new ArrayList<>();
        Connection cn = null;
        CallableStatement csm = null;
        ResultSet rs = null;
        
        try {
            cn = MysqlConexion.getConexion();
            String sql = "{call sp_ReporteVentasPorFechas(?, ?)}";
            csm = cn.prepareCall(sql);
            csm.setString(1, inicio);
            csm.setString(2, fin);
            rs = csm.executeQuery();
            
            while (rs.next()) {
                Reporte r = new Reporte();
                r.setIdVenta(rs.getInt("id_venta"));
                r.setFecha(rs.getString("fecha"));
                r.setNombreCliente(rs.getString("cliente"));
                r.setTotal(rs.getDouble("total"));
                lista.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cerrarRecursos(cn, csm, rs);
        }
        return lista;
    }

    @Override
    public List<Reporte> obtenerDetalleVenta(int idVenta) {
        List<Reporte> lista = new ArrayList<>();
        Connection cn = null;
        CallableStatement csm = null;
        ResultSet rs = null;
        
        try {
            cn = MysqlConexion.getConexion();
            String sql = "{call sp_ReporteDetalleVenta(?)}";
            csm = cn.prepareCall(sql);
            csm.setInt(1, idVenta);
            rs = csm.executeQuery();
            
            while (rs.next()) {
                Reporte r = new Reporte();
                r.setNombreProducto(rs.getString("PRODUCTO"));
                r.setCantidad(rs.getInt("CANTIDAD"));
                r.setPrecioUnit(rs.getDouble("PRECIO UNIT."));
                r.setTotal(rs.getDouble("TOTAL"));
                lista.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cerrarRecursos(cn, csm, rs);
        }
        return lista;
    }
    
    @Override
    public int contarReporteVenta() {
        int total = 0;
        Connection cn = null;
        CallableStatement cs = null;
        ResultSet rs = null;

        try {
            cn = MysqlConexion.getConexion();
            cs = cn.prepareCall("{CALL sp_contarVentas()}");
            rs = cs.executeQuery();

            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (cs != null) cs.close();
                if (cn != null) cn.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        
        return total;
    }

    private void cerrarRecursos(Connection cn, CallableStatement csm, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (csm != null) csm.close();
            if (cn != null) cn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}