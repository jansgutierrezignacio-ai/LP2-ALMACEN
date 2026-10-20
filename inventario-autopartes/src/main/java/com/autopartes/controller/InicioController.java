package com.autopartes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.autopartes.repository.IClienteRepository;
import com.autopartes.repository.IProductoRepository;
import com.autopartes.repository.IProveedorRepository;
import com.autopartes.repository.IReporteRepository;
import com.autopartes.repository.IMovimientoRepository;

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

    @GetMapping({"/", "/inicio"})
    public String inicio(Authentication authentication, Model model) {
        // 1. Usuario autenticado para la barra superior
        if (authentication != null) {
            model.addAttribute("usuario", authentication.getName());
        }

        // 2. Totales reales desde la base de datos
        long totalClientes = clienteRepository.count();
        long totalProveedores = proveedorRepository.count();
        long totalStock = productoRepository.count();
        long totalReporte = reporteRepository.contarTotalVentas();
        long totalMovimientos = movimientoRepository.count();

        // 3. Pasamos las variables al HTML (inicio.html)
        model.addAttribute("totalClientes", totalClientes);
        model.addAttribute("totalProveedores", totalProveedores);
        model.addAttribute("totalStock", totalStock);
        model.addAttribute("totalReporte", totalReporte);
        model.addAttribute("totalMovimientos", totalMovimientos);

        return "inicio"; 
    }
}