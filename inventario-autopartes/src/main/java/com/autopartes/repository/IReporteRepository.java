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

    // Consulta general para listar todas las ventas con sus detalles (usando LEFT JOIN para incluir ventas antiguas)
    @Query(value = "SELECT v.id_venta, v.fecha, " +
                   "COALESCE(CONCAT(c.nombre, ' ', c.apellido), 'Cliente Desconocido') AS cliente, " +
                   "COALESCE(p.nombre, 'Sin detalle / Varios') AS producto, " +
                   "COALESCE(dv.cantidad, 0) AS cantidad, " +
                   "COALESCE(dv.precio_unitario, 0.0) AS precio_unitario, " +
                   "v.total " +
                   "FROM venta v " +
                   "LEFT JOIN cliente c ON v.id_cliente = c.id_cliente " +
                   "LEFT JOIN detalleventa dv ON v.id_venta = dv.id_venta " +
                   "LEFT JOIN producto p ON dv.id_producto = p.id_producto " +
                   "ORDER BY v.id_venta DESC", nativeQuery = true)
    List<Object[]> findAllReportes();

    // Método para filtrar por rango de fechas utilizando también LEFT JOIN
    @Query(value = "SELECT v.id_venta, v.fecha, " +
                   "COALESCE(CONCAT(c.nombre, ' ', c.apellido), 'Cliente Desconocido') AS cliente, " +
                   "COALESCE(p.nombre, 'Sin detalle / Varios') AS producto, " +
                   "COALESCE(dv.cantidad, 0) AS cantidad, " +
                   "COALESCE(dv.precio_unitario, 0.0) AS precio_unitario, " +
                   "v.total " +
                   "FROM venta v " +
                   "LEFT JOIN cliente c ON v.id_cliente = c.id_cliente " +
                   "LEFT JOIN detalleventa dv ON v.id_venta = dv.id_venta " +
                   "LEFT JOIN producto p ON dv.id_producto = p.id_producto " +
                   "WHERE STR_TO_DATE(v.fecha, '%d/%m/%Y') BETWEEN :fechaInicio AND :fechaFin " +
                   "ORDER BY v.id_venta DESC", nativeQuery = true)
    List<Object[]> generarReporteVentasPorFechas(
        @Param("fechaInicio") Date fechaInicio, 
        @Param("fechaFin") Date fechaFin
    );

    @Query(value = "SELECT COUNT(*) FROM venta", nativeQuery = true)
    long contarTotalVentas();
}