package com.autopartes.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

import interfaces.IntCliente;
import interfaces.IntMovimiento;
import interfaces.IntProducto;
import interfaces.IntProveedor;
import interfaces.IntReporte;
import modelo.Producto;

@Controller
public class InicioController {

    @Autowired
    private IntCliente clienteService;

    @Autowired
    private IntProveedor proveedorService;

    @Autowired
    private IntProducto productoService;

    @Autowired
    private IntReporte reporteService;

    @Autowired
    private IntMovimiento movimientoService;

    // Panel de inicio (dashboard): indicadores de los módulos principales.
    @GetMapping("/inicio")
    public String inicio(Model model) {
        int totalClientes = 0, totalProveedores = 0, totalProductos = 0, totalStock = 0;
        int totalReporte = 0, totalMovimientos = 0;

        try {
            totalClientes = clienteService.listarCliente().size();
        } catch (Exception e) { e.printStackTrace(); }

        try {
            totalProveedores = proveedorService.listarProveedor().size();
        } catch (Exception e) { e.printStackTrace(); }

        try {
            List<Producto> listP = productoService.listarProducto();
            totalProductos = listP.size();
            for (Producto p : listP) {
                totalStock += p.getStock();
            }
        } catch (Exception e) { e.printStackTrace(); }

        try {
            totalReporte = reporteService.contarReporteVenta();
        } catch (Exception e) { e.printStackTrace(); }

        try {
            totalMovimientos = movimientoService.contarMovimientos();
        } catch (Exception e) { e.printStackTrace(); }

        model.addAttribute("totalClientes", totalClientes);
        model.addAttribute("totalProveedores", totalProveedores);
        model.addAttribute("totalProductos", totalProductos);
        model.addAttribute("totalStock", totalStock);
        model.addAttribute("totalReporte", totalReporte);
        model.addAttribute("totalMovimientos", totalMovimientos);

        return "Inicio";
    }
}
