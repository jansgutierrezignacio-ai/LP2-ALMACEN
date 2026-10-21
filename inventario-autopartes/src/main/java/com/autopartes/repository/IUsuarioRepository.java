package com.autopartes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.autopartes.entity.Usuario;
import java.util.List;

@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, Integer> {

    Usuario findByUsernameAndClave(String username, String clave);

    Usuario findByUsername(String username);

    List<Usuario> findByIdTipo(int idTipo);

}