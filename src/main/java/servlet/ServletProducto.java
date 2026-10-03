package servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import modelo.Producto;
import modelo.Proveedor;
import modelo.Movimiento;
import modelo.Usuario;
import procesos.GestionProducto;
import procesos.GestionProveedor;
import procesos.GestionMovimiento;

@WebServlet("/ServletProducto")
public class ServletProducto extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public ServletProducto() {
		super();
	}

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String type = request.getParameter("type");
		if (type == null) type = "listar";
		try {
			switch (type) {
			case "listar":    listarProducto(request, response); break;
			case "nuevo":     nuevoProducto(request, response); break;
			case "registrar": registrarProducto(request, response); break;
			case "buscar":    buscarProducto(request, response); break;
			case "info":      obtenerProducto(request, response); break;
			case "infoEdit":  obtenerProductoEdit(request, response); break;
			case "editar":    editarProducto(request, response); break;
			case "eliminar":  eliminarProducto(request, response); break;
			default:
				request.setAttribute("mensaje", "Acción no permitida");
				request.getRequestDispatcher("/msgError.jsp").forward(request, response);
			}
		} catch (NumberFormatException e) {
			request.setAttribute("mensaje", "Parámetro inválido");
			request.getRequestDispatcher("/msgError.jsp").forward(request, response);
		}
	}

	private void listarProducto(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		GestionProducto model = new GestionProducto();
		List<Producto> dataProducto = model.listarProducto();
		// Siempre cargamos proveedores para que el formulario "Agregar" esté listo
		GestionProveedor modelProv = new GestionProveedor();
		List<Proveedor> listProveedor = modelProv.listarProveedor();
		request.setAttribute("listProveedor", listProveedor);
		request.setAttribute("dataProducto", dataProducto);
		request.setAttribute("cantReg", dataProducto.size());
		request.getRequestDispatcher("/Productos.jsp").forward(request, response);
	}

	private void nuevoProducto(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Lista de proveedores para el <select>
		GestionProveedor modelProv = new GestionProveedor();
		List<Proveedor> listProveedor = modelProv.listarProveedor();
		// También cargamos productos para mantener el tab Listar funcional
		GestionProducto model = new GestionProducto();
		List<Producto> dataProducto = model.listarProducto();
		request.setAttribute("dataProducto", dataProducto);
		request.setAttribute("cantReg", dataProducto.size());
		request.setAttribute("listProveedor", listProveedor);
		request.setAttribute("activeTab", "agregar");
		request.getRequestDispatcher("/Productos.jsp").forward(request, response);
	}

	protected void registrarProducto(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String codigo = request.getParameter("txtCodigo");
		String nombre = request.getParameter("txtNombre");
		String descripcion = request.getParameter("txtDescripcion");
		double precio = 0;
		int stock = 0;
		int idProveedor = 0;
		try {
			precio = Double.parseDouble(request.getParameter("txtPrecio"));
			stock = Integer.parseInt(request.getParameter("txtStock"));
			idProveedor = Integer.parseInt(request.getParameter("cboProveedor"));
		} catch (NumberFormatException e) {
			request.setAttribute("mensaje", "Datos numéricos inválidos");
			request.getRequestDispatcher("/msgError.jsp").forward(request, response);
			return;
		}

		Producto producto = new Producto();
		producto.setCodigo(codigo);
		producto.setNombre(nombre);
		producto.setDescripcion(descripcion);
		producto.setPrecio(precio);
		producto.setStock(stock);
		producto.setIdProveedor(idProveedor);

		GestionProducto model = new GestionProducto();
		int value = model.crearProducto(producto);

		if (value == 1) {
			//Registrar Movimiento Registrar
			GestionMovimiento mov = new GestionMovimiento();
	        Movimiento m = new Movimiento();
	        m.setTipo("REGISTRAR");
	        m.setDescripcion("Se registró producto: " + nombre);
	        
	        //Usuario desde sesión
	        HttpSession session = request.getSession();
	        Usuario u = (Usuario) session.getAttribute("usuario");
	        m.setUsuario(u.getUsername());
	        mov.registrarMovimiento(m);
	        
			request.setAttribute("mensajeOk", "Producto registrado correctamente");
			listarProducto(request, response);
		} else {
			request.setAttribute("mensaje", "Ocurrió un problema al registrar el producto");
			request.getRequestDispatcher("/msgError.jsp").forward(request, response);
		}
	}

	private void buscarProducto(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		GestionProducto model = new GestionProducto();
		String texto = request.getParameter("txtBuscar");
		if (texto == null) texto = "";

		List<Producto> data = model.buscarProductoByNombre(texto);
		request.setAttribute("dataProducto", data);
		request.setAttribute("cantReg", data.size());
		request.getRequestDispatcher("/Productos.jsp").forward(request, response);
	}

	protected void obtenerProducto(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int id = Integer.parseInt(request.getParameter("id"));
		GestionProducto model = new GestionProducto();
		Producto producto = model.obtenerProducto(id);

		request.setAttribute("productoInfo", producto);
		List<Producto> dataProducto = model.listarProducto();
		request.setAttribute("dataProducto", dataProducto);
		request.setAttribute("cantReg", dataProducto.size());
		request.getRequestDispatcher("/Productos.jsp").forward(request, response);
	}

	private void obtenerProductoEdit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int id = Integer.parseInt(request.getParameter("id"));
		GestionProducto model = new GestionProducto();
		Producto producto = model.obtenerProducto(id);

		// También cargar proveedores para el select
		GestionProveedor modelProv = new GestionProveedor();
		List<Proveedor> listProveedor = modelProv.listarProveedor();

		// Mantener la tabla visible
		List<Producto> dataProducto = model.listarProducto();
		request.setAttribute("dataProducto", dataProducto);
		request.setAttribute("cantReg", dataProducto.size());
		request.setAttribute("productoDataEdit", producto);
		request.setAttribute("listProveedor", listProveedor);
		request.getRequestDispatcher("/Productos.jsp").forward(request, response);
	}

	protected void editarProducto(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int idProducto = Integer.parseInt(request.getParameter("idProducto"));
		String codigo = request.getParameter("txtCodigo");
		String nombre = request.getParameter("txtNombre");
		String descripcion = request.getParameter("txtDescripcion");
		double precio = 0;
		int stock = 0;
		int idProveedor = 0;
		try {
			precio = Double.parseDouble(request.getParameter("txtPrecio"));
			stock = Integer.parseInt(request.getParameter("txtStock"));
			idProveedor = Integer.parseInt(request.getParameter("cboProveedor"));
		} catch (NumberFormatException e) {
			request.setAttribute("mensaje", "Datos numéricos inválidos");
			request.getRequestDispatcher("/msgError.jsp").forward(request, response);
			return;
		}

		Producto producto = new Producto();
		producto.setIdProducto(idProducto);
		producto.setCodigo(codigo);
		producto.setNombre(nombre);
		producto.setDescripcion(descripcion);
		producto.setPrecio(precio);
		producto.setStock(stock);
		producto.setIdProveedor(idProveedor);

		GestionProducto model = new GestionProducto();
		int value = model.editarProducto(producto);

		if (value == 1) {
			//Registrar Movimiento Editar
			GestionMovimiento mov = new GestionMovimiento();
	        Movimiento m = new Movimiento();
	        m.setTipo("EDITAR");
	        m.setDescripcion("Se editó producto ID: " + nombre + " - " + idProducto);

	        //Usuario desde sesión
	        HttpSession session = request.getSession();
	        Usuario u = (Usuario) session.getAttribute("usuario");
	        m.setUsuario(u.getUsername());
	        mov.registrarMovimiento(m);
	        
			request.setAttribute("mensajeOk", "Producto actualizado correctamente");
			listarProducto(request, response);
		} else {
			request.setAttribute("mensaje", "Ocurrió un problema al actualizar el producto");
			request.getRequestDispatcher("/msgError.jsp").forward(request, response);
		}
	}

	protected void eliminarProducto(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int id = Integer.parseInt(request.getParameter("id"));
		GestionProducto model = new GestionProducto();
		int value = model.eliminarProducto(id);

		if (value == 1) {
			//Registrar Movimiento Eliminar
			GestionMovimiento mov = new GestionMovimiento();
	        Movimiento m = new Movimiento();
	        m.setTipo("ELIMINAR");
	        m.setDescripcion("Se eliminó producto ID: " + id);

	        //Usuario desde sesión
	        HttpSession session = request.getSession();
	        Usuario u = (Usuario) session.getAttribute("usuario");
	        m.setUsuario(u.getUsername());
	        mov.registrarMovimiento(m);
	        
			request.setAttribute("mensajeOk", "Producto eliminado correctamente");
			listarProducto(request, response);
		} else {
			request.setAttribute("mensaje", "Ocurrió un problema al eliminar el producto");
			request.getRequestDispatcher("/msgError.jsp").forward(request, response);
		}
	}
}
