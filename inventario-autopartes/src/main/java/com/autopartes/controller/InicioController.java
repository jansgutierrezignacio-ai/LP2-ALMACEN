package com.autopartes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.autopartes.entity.Usuario;
import com.autopartes.repository.IClienteRepository;
import com.autopartes.repository.IMovimientoRepository;
import com.autopartes.repository.IProductoRepository;
import com.autopartes.repository.IProveedorRepository;
import com.autopartes.repository.IReporteRepository;
import com.autopartes.repository.IUsuarioRepository;
import com.autopartes.repository.IVentaRepository;

@Controller
public class InicioController {

    @Autowired
    private IClienteRepository clienteRepository;

    @Autowired
    private IProductoRepository productoRepository;

    @Autowired
    private IProveedorRepository proveedorRepository;

    @Autowired
    private IReporteRepository reporteRepository;

    @Autowired
    private IMovimientoRepository movimientoRepository;

    @Autowired
    private IUsuarioRepository usuarioRepository; 
    
    @Autowired
    private IVentaRepository ventaRepository;

    @GetMapping({"/", "/inicio"})
    public String inicio(Authentication authentication, Model model) {
        // 1. Usuario autenticado y su rol calculado desde la BD
        if (authentication != null) {
            String username = authentication.getName();
            model.addAttribute("usuario", username);

            // Buscamos al usuario para obtener su rol limpio
            Usuario user = usuarioRepository.findByUsername(username);
            if (user != null) {
                model.addAttribute("rolUsuarioActual", user.getNombreRolTexto());
            } else {
                model.addAttribute("rolUsuarioActual", "Almacenero");
            }
        }

        // 2. Totales reales desde la base de datos
        long totalClientes = clienteRepository.count();
        long totalProveedores = proveedorRepository.count();
        long totalStock = productoRepository.count();
        long totalReporte = reporteRepository.contarTotalVentas();
        long totalMovimientos = movimientoRepository.count();
        long totalVentas = ventaRepository.count();

        // 3. Pasamos las variables al HTML (inicio.html)
        model.addAttribute("totalClientes", totalClientes);
        model.addAttribute("totalProveedores", totalProveedores);
        model.addAttribute("totalStock", totalStock);
        model.addAttribute("totalReporte", totalReporte);
        model.addAttribute("totalMovimientos", totalMovimientos);
        model.addAttribute("totalVentas", totalVentas);

        return "inicio"; 
    }
}