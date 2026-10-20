package com.autopartes.config;

import java.util.Date;
import org.springframework.stereotype.Service;

import com.autopartes.entity.Movimiento;
import com.autopartes.repository.IMovimientoRepository;

@Service
public class MovimientoService {

    private final IMovimientoRepository movimientoRepository;

    public MovimientoService(IMovimientoRepository movimientoRepository) {
        this.movimientoRepository = movimientoRepository;
    }

    // Nota: El parámetro 'modulo' lo omitimos o ignoramos aquí si tu entidad no tiene esa columna, 
    // pero mantenemos la firma limpia para la descripción, tipo y usuario.
    public void registrar(String tipo, String modulo, String descripcion, String usuario) {
        try {
            Movimiento mov = new Movimiento();
            mov.setTipo(tipo);
            mov.setDescripcion(descripcion);
            mov.setFecha(new Date()); // Compatible con java.util.Date de tu entidad
            mov.setUsuario(usuario != null ? usuario : "Administrador");

            movimientoRepository.save(mov);
        } catch (Exception e) {
            System.err.println("Error al registrar el movimiento: " + e.getMessage());
        }
    }
}