package com.autopartes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.autopartes.entity.Venta;

public interface IVentaRepository extends JpaRepository<Venta, Integer> {
    
    // Cambiamos el retorno a Long para que coincida con el COUNT(*) de SQL
    @Query(value = "SELECT COUNT(*) FROM venta WHERE id_cliente = :idCliente", nativeQuery = true)
    Long countByClienteId(@Param("idCliente") Integer idCliente);
}