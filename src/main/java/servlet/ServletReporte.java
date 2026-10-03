package servlet;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import modelo.Reporte;
import procesos.GestionReporte;

@WebServlet("/ServletReporte")
public class ServletReporte extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public ServletReporte() {
        super();
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String type = request.getParameter("type");
        
        if (type == null) {
            request.getRequestDispatcher("/Reportes.jsp").forward(request, response);
            return;
        }

        switch (type) {
            case "listar": 
                listarReporteFechas(request, response); 
                break;
            case "detalle": 
                verDetalleVenta(request, response); 
                break;
        }
    }

    private void listarReporteFechas(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fechaInicio = request.getParameter("txtFechaInicio");
        String fechaFin = request.getParameter("txtFechaFin");

        GestionReporte model = new GestionReporte();
        List<Reporte> lista = model.listarReportePorFechas(fechaInicio, fechaFin);

        request.setAttribute("dataReporte", lista);
        request.setAttribute("fInicio", fechaInicio);
        request.setAttribute("fFin", fechaFin);
        
        request.getRequestDispatcher("/Reporte.jsp").forward(request, response);
    }

    private void verDetalleVenta(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int idVenta = Integer.parseInt(request.getParameter("id"));
        
        GestionReporte model = new GestionReporte();
        List<Reporte> detalle = model.obtenerDetalleVenta(idVenta);

        request.setAttribute("detalleVenta", detalle);
        request.setAttribute("nroVenta", idVenta);
        
        request.getRequestDispatcher("/Reporte.jsp").forward(request, response);
    }
}