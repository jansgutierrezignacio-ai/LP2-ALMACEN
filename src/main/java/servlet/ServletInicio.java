package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import procesos.GestionCliente;
import procesos.GestionProducto;
import procesos.GestionProveedor;
import procesos.GestionReporte;
import procesos.GestionMovimiento;


@WebServlet("/ServletInicio")
public class ServletInicio extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public ServletInicio() {
		super();
	}

	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		int totalClientes = 0, totalProveedores = 0, totalProductos = 0, totalStock = 0,totalReporte = 0, totalMovimientos = 0;
		try {
			totalClientes = new GestionCliente().listarCliente().size();
		} catch (Exception e) { e.printStackTrace(); }
		try {
			totalProveedores = new GestionProveedor().listarProveedor().size();
		} catch (Exception e) { e.printStackTrace(); }
		try {
			java.util.List<modelo.Producto> listP = new GestionProducto().listarProducto();
			totalProductos = listP.size();
			for (modelo.Producto p : listP) totalStock += p.getStock();
		} catch (Exception e) { e.printStackTrace(); }
		try {
			totalReporte = new GestionReporte().contarReporteVenta();
		} catch (Exception e) {
		    e.printStackTrace();
		}
		try {
		    totalMovimientos = new GestionMovimiento().contarMovimientos();
		} catch (Exception e) { e.printStackTrace(); }

		request.setAttribute("totalClientes", totalClientes);
		request.setAttribute("totalProveedores", totalProveedores);
		request.setAttribute("totalProductos", totalProductos);
		request.setAttribute("totalStock", totalStock);
		request.setAttribute("totalReporte", totalReporte);
		request.setAttribute("totalMovimientos", totalMovimientos);

		request.getRequestDispatcher("/Inicio.jsp").forward(request, response);
	}
}
