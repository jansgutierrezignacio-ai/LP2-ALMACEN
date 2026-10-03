package servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import modelo.Cliente;
import modelo.Movimiento;
import modelo.Usuario;
import procesos.GestionCliente;
import procesos.GestionMovimiento;

@WebServlet("/ServletCliente")
public class ServletCliente extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public ServletCliente() {
        super();
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String type = request.getParameter("type");
		if (type == null) type = "listar";
		try {
			switch (type) {
			case "listar":    listarCliente(request, response); break;
			case "nuevo":     nuevoCliente(request, response); break;
			case "registrar": registrarCliente(request, response); break;
			case "buscar":    buscarCliente(request, response); break;
			case "info":      obtenerCliente(request, response); break;
			case "infoEdit":  obtenerClienteEdit(request, response); break;
			case "editar":    editarCliente(request, response); break;
			case "eliminar":  eliminarCliente(request, response); break;
			default:
				request.setAttribute("mensaje", "Acción no permitida");
				request.getRequestDispatcher("/msgError.jsp").forward(request, response);
			}
		} catch (NumberFormatException e) {
			request.setAttribute("mensaje", "Parámetro inválido");
			request.getRequestDispatcher("/msgError.jsp").forward(request, response);
		}
	}

    private void listarCliente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	GestionCliente model = new GestionCliente();
		List<Cliente> dataCliente = model.listarCliente();
		request.setAttribute("dataCliente", dataCliente);
		request.setAttribute("cantReg", dataCliente.size());
		request.getRequestDispatcher("/Clientes.jsp").forward(request, response);
	}

    private void nuevoCliente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	// Redirigimos al JSP principal, activando la pestaña "Agregar"
    	request.setAttribute("activeTab", "agregar");
		request.getRequestDispatcher("/Clientes.jsp").forward(request, response);
	}

    protected void registrarCliente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String dni = request.getParameter("txtDni");
		String nombre = request.getParameter("txtNombre");
		String apellido = request.getParameter("txtApellido");
		String telefono = request.getParameter("txtTelefono");

		Cliente cliente = new Cliente();
		cliente.setDni(dni);
		cliente.setNombre(nombre);
		cliente.setApellido(apellido);
		cliente.setTelefono(telefono);

		GestionCliente model = new GestionCliente();
		int value = model.crearCliente(cliente);
		
		if(value == 1) {
			//Registrar Movimiento Registrar
			GestionMovimiento mov = new GestionMovimiento();
	        Movimiento m = new Movimiento();
	        m.setTipo("REGISTRAR");
	        m.setDescripcion("Se registró cliente: " + nombre + " " + apellido);
	        
	        //Usuario desde sesión
	        HttpSession session = request.getSession();
	        Usuario u = (Usuario) session.getAttribute("usuario");
	        m.setUsuario(u.getUsername());
	        mov.registrarMovimiento(m);
	        
			request.setAttribute("mensajeOk", "Cliente registrado correctamente");
			listarCliente(request, response);
		} else {
			request.setAttribute("mensaje", "Ocurrió un problema al registrar");
			request.getRequestDispatcher("/msgError.jsp").forward(request, response);
		}
	}

	private void buscarCliente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		GestionCliente model = new GestionCliente();
		String texto = request.getParameter("txtBuscar");
		if(texto == null) texto = "";

		List<Cliente> data = model.buscarClienteByNombre(texto);
		request.setAttribute("dataCliente", data);
		request.setAttribute("cantReg", data.size());
		request.setAttribute("txtBuscar", texto);
		request.getRequestDispatcher("/Clientes.jsp").forward(request, response);
	}

	protected void obtenerCliente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int idCliente = Integer.parseInt(request.getParameter("id"));
		GestionCliente model = new GestionCliente();
		Cliente cliente = model.obtenerCliente(idCliente);

		request.setAttribute("dataClienteInfo", cliente);
		// Volvemos al listado + modal de info
		List<Cliente> dataCliente = model.listarCliente();
		request.setAttribute("dataCliente", dataCliente);
		request.setAttribute("cantReg", dataCliente.size());
		request.getRequestDispatcher("/Clientes.jsp").forward(request, response);
	}

	private void obtenerClienteEdit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int idCliente = Integer.parseInt(request.getParameter("id"));
		GestionCliente model = new GestionCliente();
		Cliente cliente = model.obtenerCliente(idCliente);

		request.setAttribute("clienteDataEdit", cliente);
		// Cargamos también el listado para mantener la tabla visible
		List<Cliente> dataCliente = model.listarCliente();
		request.setAttribute("dataCliente", dataCliente);
		request.setAttribute("cantReg", dataCliente.size());
		request.setAttribute("activeTab", "editar");
		request.getRequestDispatcher("/Clientes.jsp").forward(request, response);
	}

	protected void editarCliente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int idCliente = Integer.parseInt(request.getParameter("idCliente"));
		String dni = request.getParameter("txtDni");
		String nombre = request.getParameter("txtNombre");
		String apellido = request.getParameter("txtApellido");
		String telefono = request.getParameter("txtTelefono");

		Cliente cliente = new Cliente();
		cliente.setIdCliente(idCliente);
		cliente.setDni(dni);
		cliente.setNombre(nombre);
		cliente.setApellido(apellido);
		cliente.setTelefono(telefono);

		GestionCliente model = new GestionCliente();
		int value = model.editarCliente(cliente);

		if(value == 1) {
			//Registrar Movimiento Editar
			GestionMovimiento mov = new GestionMovimiento();
	        Movimiento m = new Movimiento();
	        m.setTipo("EDITAR");
	        m.setDescripcion("Se editó cliente ID: " + idCliente);

	        //Usuario desde sesión
	        HttpSession session = request.getSession();
	        Usuario u = (Usuario) session.getAttribute("usuario");
	        m.setUsuario(u.getUsername());
	        mov.registrarMovimiento(m);
	        
			request.setAttribute("mensajeOk", "Cliente actualizado correctamente");
			listarCliente(request, response);
		} else {
			request.setAttribute("mensaje", "Ocurrió un problema al actualizar");
			request.getRequestDispatcher("/msgError.jsp").forward(request, response);
		}
	}

	protected void eliminarCliente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int idCliente = Integer.parseInt(request.getParameter("id"));
        GestionCliente model = new GestionCliente();
		int value = model.eliminarCliente(idCliente);

		if(value == 1) {
			//Registrar Movimiento Eliminar
			GestionMovimiento mov = new GestionMovimiento();
	        Movimiento m = new Movimiento();
	        m.setTipo("ELIMINAR");
	        m.setDescripcion("Se eliminó cliente ID: " + idCliente);

	        //Usuario desde sesión
	        HttpSession session = request.getSession();
	        Usuario u = (Usuario) session.getAttribute("usuario");
	        m.setUsuario(u.getUsername());
	        mov.registrarMovimiento(m);
	        
			request.setAttribute("mensajeOk", "Cliente eliminado correctamente");
			listarCliente(request, response);
		} else {
			request.setAttribute("mensaje", "Ocurrió un problema al eliminar");
			request.getRequestDispatcher("/msgError.jsp").forward(request, response);
		}
	}
}
