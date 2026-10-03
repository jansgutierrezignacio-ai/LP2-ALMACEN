package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import modelo.Movimiento;
import procesos.GestionMovimiento;

import java.io.IOException;
import java.util.List;

@WebServlet("/ServletMovimiento")
public class ServletMovimiento extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ServletMovimiento() {
        super();
    }

    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String type = request.getParameter("type");

        if (type.equals("listar")) {
            listarMovimientos(request, response);
        }
    }
	private void listarMovimientos(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        GestionMovimiento gm = new GestionMovimiento();
        List<Movimiento> lista = gm.listarMovimientos();

        request.setAttribute("dataMovimiento", lista);
        request.setAttribute("cantReg", lista.size());

        request.getRequestDispatcher("/Movimientos.jsp").forward(request, response);
    }
}
