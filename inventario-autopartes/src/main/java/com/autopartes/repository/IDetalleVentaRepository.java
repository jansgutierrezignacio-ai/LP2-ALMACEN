package com.autopartes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.autopartes.entity.DetalleVenta;
import java.util.List;

public interface IDetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {
    List<DetalleVenta> findByIdVenta(Integer idVenta);
}