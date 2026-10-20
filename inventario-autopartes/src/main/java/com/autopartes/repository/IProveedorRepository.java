package com.autopartes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.autopartes.entity.Proveedor;
import java.util.List;

@Repository
public interface IProveedorRepository extends JpaRepository<Proveedor, Integer> {

    Proveedor findByRuc(String ruc);

    List<Proveedor> findByRazonSocialContainingIgnoreCase(String razonSocial);

    List<Proveedor> findByEstado(int estado);

}