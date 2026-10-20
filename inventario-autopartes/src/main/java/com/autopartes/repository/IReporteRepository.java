package com.autopartes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.autopartes.entity.Reporte;
import java.util.Date;
import java.util.List;

@Repository
public interface IReporteRepository extends JpaRepository<Reporte, Integer> {

    // Consulta general directa
    @Query(value = "SELECT v.id_venta, v.fecha, c.nombre AS cliente, p.nombre AS producto, dv.cantidad, dv.precio_unitario, dv.total " +
                   "FROM venta v " +
                   "JOIN cliente c ON v.id_cliente = c.id_cliente " +
                   "JOIN detalleventa dv ON v.id_venta = dv.id_venta " +
                   "JOIN producto p ON dv.id_producto = p.id_producto", nativeQuery = true)
    List<Object[]> findAllReportes();

    // Reemplazamos la llamada al procedimiento por una consulta directa con filtro de fechas
    @Query(value = "SELECT v.id_venta, v.fecha, c.nombre AS cliente, p.nombre AS producto, dv.cantidad, dv.precio_unitario, dv.total " +
                   "FROM venta v " +
                   "JOIN cliente c ON v.id_cliente = c.id_cliente " +
                   "JOIN detalleventa dv ON v.id_venta = dv.id_venta " +
                   "JOIN producto p ON dv.id_producto = p.id_producto " +
                   "WHERE DATE(v.fecha) BETWEEN :fechaInicio AND :fechaFin " +
                   "ORDER BY v.fecha ASC", nativeQuery = true)
    List<Object[]> generarReporteVentasPorFechas(
        @Param("fechaInicio") Date fechaInicio, 
        @Param("fechaFin") Date fechaFin
    );

    @Query(value = "CALL sp_ReporteDetalleVenta(:idVenta)", nativeQuery = true)
    List<Object[]> generarReporteDetalleVenta(@Param("idVenta") int idVenta);

    @Query(value = "SELECT COUNT(*) FROM venta", nativeQuery = true)
    long contarTotalVentas();
}