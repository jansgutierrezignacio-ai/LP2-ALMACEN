package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

import modelo.Usuario;
import procesos.GestionUsuario;

/**
 * Servlet implementation class ServletUsuario
 */
@WebServlet("/ServletUsuario")
public class ServletUsuario extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public ServletUsuario() {
		super();
	}

	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String type = request.getParameter("type");
		if (type == null) {
			response.sendRedirect("login.jsp");
			return;
		}
		switch (type) {
			case "login":login(request, response); break;
			case "listar": listarUsuario(request, response); break;
			case "registrar": registrarUsuario(request,response); break;				
			case "nuevo": nuevoUsuario(request, response); break;
			case "logout": logout(request, response); break;
			default:
				request.setAttribute("mensaje", "Ocurrió un problema");
				request.getRequestDispatcher("/msgError.jsp").forward(request, response);
		}
	}

	protected void login(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = request.getParameter("txtUsername");
		String clave = request.getParameter("txtClave");
		GestionUsuario model = new GestionUsuario();
		Usuario usuario = model.login(username, clave);
		if (usuario != null) {
			HttpSession session = request.getSession(true);
			session.setAttribute("usuario", usuario);
			response.sendRedirect("Inicio.jsp");
		} else {
			request.setAttribute("mensaje", "No existe usuario y/o clave");
			request.getRequestDispatcher("errorLogin.jsp").forward(request, response);
		}
	}
	
	
	
	private void nuevoUsuario(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    request.getRequestDispatcher("usuarioNuevo.jsp").forward(request, response);
	}

	private void listarUsuario(HttpServletRequest request, HttpServletResponse response) 
	        throws ServletException, IOException {
	    GestionUsuario model = new GestionUsuario();
	    List<Usuario> data = model.listarUsuario();
	    
	    request.setAttribute("data", data);
	    request.setAttribute("cantReg", data.size());
	    
	    // IMPORTANTE: Aquí enviamos la lista a la página usuarioList.jsp
	    request.getRequestDispatcher("usuarioList.jsp").forward(request, response);
	}
	
	private void registrarUsuario(HttpServletRequest request, HttpServletResponse response) 
	        throws ServletException, IOException {
	    
	    Usuario usuario = new Usuario();    
	    usuario.setUsername(request.getParameter("txtUsername"));
	    usuario.setClave(request.getParameter("txtClave"));
	    usuario.setIdTipo(Integer.parseInt(request.getParameter("cboTipo")));
	    usuario.setNombres(request.getParameter("txtNombres"));
	    usuario.setApellidos(request.getParameter("txtApellidos"));

	    GestionUsuario model = new GestionUsuario();
	    int value = model.crearUsuario(usuario);        

	    if(value > 0) {
	        
	        HttpSession session = request.getSession(true);
	        
	        session.setAttribute("usuario", usuario);
	        
	        response.sendRedirect("Inicio.jsp");
	        
	    } else {
	        request.setAttribute("mensaje", "No se pudo registrar el usuario");
	        request.getRequestDispatcher("msgError.jsp").forward(request, response);
	    }
	}
	protected void logout(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		response.sendRedirect("login.jsp");

	}
}
