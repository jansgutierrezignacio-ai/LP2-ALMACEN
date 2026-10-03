package servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import modelo.Proveedor;
import modelo.Movimiento;
import modelo.Usuario;
import procesos.GestionProveedor;
import procesos.GestionMovimiento;

@WebServlet("/ServletProveedor")
public class ServletProveedor extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public ServletProveedor() {
		super();
	}

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String type = request.getParameter("type");
		if (type == null) type = "listar";
		try {
			switch (type) {
			case "listar":    listarProveedor(request, response); break;
			case "nuevo":     nuevoProveedor(request, response); break;
			case "registrar": registrarProveedor(request, response); break;
			case "buscar":    buscarProveedor(request, response); break;
			case "info":      obtenerProveedor(request, response); break;
			case "infoEdit":  obtenerProveedorEdit(request, response); break;
			case "editar":    editarProveedor(request, response); break;
			case "eliminar":  eliminarProveedor(request, response); break;
			default:
				request.setAttribute("mensaje", "Acción no permitida");
				request.getRequestDispatcher("/msgError.jsp").forward(request, response);
			}
		} catch (NumberFormatException e) {
			request.setAttribute("mensaje", "Parámetro inválido");
			request.getRequestDispatcher("/msgError.jsp").forward(request, response);
		}
	}

	private void listarProveedor(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		GestionProveedor model = new GestionProveedor();
		List<Proveedor> dataProveedor = model.listarProveedor();
		request.setAttribute("dataProveedor", dataProveedor);
		request.setAttribute("cantReg", dataProveedor.size());
		request.getRequestDispatcher("/Proveedores.jsp").forward(request, response);
	}

	private void nuevoProveedor(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		GestionProveedor model = new GestionProveedor();
		List<Proveedor> dataProveedor = model.listarProveedor();
		request.setAttribute("dataProveedor", dataProveedor);
		request.setAttribute("cantReg", dataProveedor.size());
		request.setAttribute("activeTab", "agregar");
		request.getRequestDispatcher("/Proveedores.jsp").forward(request, response);
	}

	protected void registrarProveedor(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String ruc = request.getParameter("txtRuc");
		String razonSocial = request.getParameter("txtRazonSocial");
		String telefono = request.getParameter("txtTelefono");
		int estado = 1; // activo por defecto
		String estadoParam = request.getParameter("txtEstado");
		if (estadoParam != null && !estadoParam.isEmpty()) {
			try { estado = Integer.parseInt(estadoParam); } catch (NumberFormatException ignore) {}
		}

		Proveedor proveedor = new Proveedor();
		proveedor.setRuc(ruc);
		proveedor.setRazon_social(razonSocial);
		proveedor.setTelefono(telefono);
		proveedor.setEstado(estado);

		GestionProveedor model = new GestionProveedor();
		int value = model.crearProveedor(proveedor);

		if (value == 1) {
			//Registrar Movimiento Registrar
			GestionMovimiento mov = new GestionMovimiento();
	        Movimiento m = new Movimiento();
	        m.setTipo("REGISTRAR");
	        m.setDescripcion("Se registró proveedor: " + razonSocial);
	        
	        //Usuario desde sesión
	        HttpSession session = request.getSession();
	        Usuario u = (Usuario) session.getAttribute("usuario");
	        m.setUsuario(u.getUsername());
	        mov.registrarMovimiento(m);
	        
			request.setAttribute("mensajeOk", "Proveedor registrado correctamente");
			listarProveedor(request, response);
		} else {
			request.setAttribute("mensaje", "Ocurrió un problema al registrar el proveedor");
			request.getRequestDispatcher("/msgError.jsp").forward(request, response);
		}
	}

	private void buscarProveedor(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		GestionProveedor model = new GestionProveedor();
		String texto = request.getParameter("txtBuscar");
		if (texto == null) texto = "";

		List<Proveedor> data = model.buscarProveedorByNombre(texto);
		request.setAttribute("dataProveedor", data);
		request.setAttribute("cantReg", data.size());
		request.getRequestDispatcher("/Proveedores.jsp").forward(request, response);
	}

	protected void obtenerProveedor(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int id = Integer.parseInt(request.getParameter("id"));
		GestionProveedor model = new GestionProveedor();
		Proveedor proveedor = model.obtenerProveedor(id);

		request.setAttribute("proveedorInfo", proveedor);
		List<Proveedor> dataProveedor = model.listarProveedor();
		request.setAttribute("dataProveedor", dataProveedor);
		request.setAttribute("cantReg", dataProveedor.size());
		request.getRequestDispatcher("/Proveedores.jsp").forward(request, response);
	}

	private void obtenerProveedorEdit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int id = Integer.parseInt(request.getParameter("id"));
		GestionProveedor model = new GestionProveedor();
		Proveedor proveedor = model.obtenerProveedor(id);

		request.setAttribute("proveedorDataEdit", proveedor);
		// También cargamos el listado para mantener la tabla visible
		List<Proveedor> dataProveedor = model.listarProveedor();
		request.setAttribute("dataProveedor", dataProveedor);
		request.setAttribute("cantReg", dataProveedor.size());
		request.getRequestDispatcher("/Proveedores.jsp").forward(request, response);
	}

	protected void editarProveedor(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int idProveedor = Integer.parseInt(request.getParameter("idProveedor"));
		String ruc = request.getParameter("txtRuc");
		String razonSocial = request.getParameter("txtRazonSocial");
		String telefono = request.getParameter("txtTelefono");
		int estado = 1;
		String estadoParam = request.getParameter("txtEstado");
		if (estadoParam != null && !estadoParam.isEmpty()) {
			try { estado = Integer.parseInt(estadoParam); } catch (NumberFormatException ignore) {}
		}

		Proveedor proveedor = new Proveedor();
		proveedor.setId_proveedor(idProveedor);
		proveedor.setRuc(ruc);
		proveedor.setRazon_social(razonSocial);
		proveedor.setTelefono(telefono);
		proveedor.setEstado(estado);

		GestionProveedor model = new GestionProveedor();
		int value = model.editarProveedor(proveedor);

		if (value == 1) {
			//Registrar Movimiento Editar
			GestionMovimiento mov = new GestionMovimiento();
	        Movimiento m = new Movimiento();
	        m.setTipo("EDITAR");
	        m.setDescripcion("Se editó producto ID: " + idProveedor);

	        //Usuario desde sesión
	        HttpSession session = request.getSession();
	        Usuario u = (Usuario) session.getAttribute("usuario");
	        m.setUsuario(u.getUsername());
	        mov.registrarMovimiento(m);
	        
			request.setAttribute("mensajeOk", "Proveedor actualizado correctamente");
			listarProveedor(request, response);
		} else {
			request.setAttribute("mensaje", "Ocurrió un problema al actualizar el proveedor");
			request.getRequestDispatcher("/msgError.jsp").forward(request, response);
		}
	}

	protected void eliminarProveedor(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int id = Integer.parseInt(request.getParameter("id"));
		GestionProveedor model = new GestionProveedor();
		int value = model.eliminarProveedor(id);

		if (value == 1) {
			//Registrar Movimiento Eliminar
			GestionMovimiento mov = new GestionMovimiento();
	        Movimiento m = new Movimiento();
	        m.setTipo("ELIMINAR");
	        m.setDescripcion("Se eliminó proveedor ID: " + id);

	        //Usuario desde sesión
	        HttpSession session = request.getSession();
	        Usuario u = (Usuario) session.getAttribute("usuario");
	        m.setUsuario(u.getUsername());
	        mov.registrarMovimiento(m);
	        
			request.setAttribute("mensajeOk", "Proveedor eliminado correctamente");
			listarProveedor(request, response);
		} else {
			request.setAttribute("mensaje", "Ocurrió un problema al eliminar el proveedor");
			request.getRequestDispatcher("/msgError.jsp").forward(request, response);
		}
	}
}
