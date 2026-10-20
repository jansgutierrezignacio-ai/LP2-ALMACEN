package com.autopartes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.autopartes.entity.Movimiento;
import java.util.List;

@Repository
public interface IMovimientoRepository extends JpaRepository<Movimiento, Integer> {

    List<Movimiento> findByTipoIgnoreCase(String tipo);

    List<Movimiento> findByUsuarioContainingIgnoreCase(String usuario);

}