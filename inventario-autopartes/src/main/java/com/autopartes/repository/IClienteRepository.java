package com.autopartes.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.autopartes.entity.Cliente;

@Repository
public interface IClienteRepository extends JpaRepository<Cliente, Integer> {

    @Query(value = "CALL sp_listarCliente()", nativeQuery = true)
    List<Cliente> listarClientesSP();

    @Modifying
    @Transactional
    @Query(value = "CALL sp_registrarCliente(:dni, :nombre, :apellido, :telefono)", nativeQuery = true)
    int registrarClienteSP(
        @Param("dni") String dni, 
        @Param("nombre") String nombre, 
        @Param("apellido") String apellido, 
        @Param("telefono") String telefono
    );

    @Modifying
    @Transactional
    @Query(value = "CALL sp_actualizarCliente(:id, :dni, :nombre, :apellido, :telefono)", nativeQuery = true)
    int actualizarClienteSP(
        @Param("id") int id, 
        @Param("dni") String dni, 
        @Param("nombre") String nombre, 
        @Param("apellido") String apellido, 
        @Param("telefono") String telefono
    );

    @Modifying
    @Transactional
    @Query(value = "CALL sp_eliminarCliente(:id)", nativeQuery = true)
    int eliminarClienteSP(@Param("id") int id);

    @Query(value = "CALL sp_buscarClienteByNombre(:texto)", nativeQuery = true)
    List<Cliente> buscarClienteByNombreSP(@Param("texto") String texto);
   
}