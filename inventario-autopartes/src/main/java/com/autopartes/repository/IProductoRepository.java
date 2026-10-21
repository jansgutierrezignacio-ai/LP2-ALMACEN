package com.autopartes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import com.autopartes.entity.Producto;
import java.util.List;

@Repository
public interface IProductoRepository extends JpaRepository<Producto, Integer> {

    Producto findByCodigo(String codigo);

    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}