<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:if test="${dataProducto == null}">
	<jsp:forward page="ServletProducto?type=listar"/>
</c:if>
<!DOCTYPE html>
<html lang="es">
<head>
	<meta charset="UTF-8">
	<title>Productos</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="CSS/General.css">

    <style>
    	.nav .card { min-width: 180px; cursor: pointer; border: none; }
		.nav-link.active { transform: scale(1.05); box-shadow: 0 4px 10px rgba(0,0,0,0.2); }
		.nav-link { color: white; }
		.tab-content { padding: 1.5rem; background: #fff; border-radius: 8px; margin-top: 1rem; }
    </style>
</head>
<body>
<div class="d-flex">

    <div class="sidebar p-3">
        <h4 class="text-white mb-4">Inventario</h4>
        <ul class="nav flex-column">
            <li class="nav-item"><a class="nav-link" href="ServletInicio"><i class="bi bi-speedometer2"></i> Dashboard</a></li>
            <li class="nav-item"><a class="nav-link" href="ServletCliente?type=listar"><i class="bi bi-people"></i> Clientes</a></li>
            <li class="nav-item"><a class="nav-link" href="ServletProveedor?type=listar"><i class="bi bi-truck"></i> Proveedores</a></li>
            <li class="nav-item"><a class="nav-link active" href="ServletProducto?type=listar"><i class="bi bi-box"></i> Productos</a></li>
            <li class="nav-item"><a class="nav-link" href="ServletReporte?type=listar"><i class="bi bi-graph-up-arrow"></i> Reportes</a></li>
            <li class="nav-item"><a class="nav-link" href="ServletMovimiento?type=listar"><i class="bi bi-clock-history"></i> Movimientos</a></li>  
            <li class="nav-item"><a class="nav-link" href="ServletUsuario?type=logout"><i class="bi bi-box-arrow-right"></i> Cerrar Sesión</a></li>
        </ul>
    </div>

    <div class="content flex-grow-1 p-3">
        <div class="topbar d-flex justify-content-between align-items-center px-4">
            <h5>Producto</h5>
            <span class="text-white">Usuario: ${sessionScope.usuario.username}</span>
        </div>

        <c:if test="${not empty mensajeOk}">
        	<div class="alert alert-success alert-dismissible fade show mt-3" role="alert">
        		<i class="bi bi-check-circle"></i> ${mensajeOk}
        		<button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        	</div>
        </c:if>

        <div class="nav nav-pills d-flex flex-nowrap overflow-auto gap-3 p-2" role="tablist">
			<div class="nav-link ${activeTab != 'agregar' ? 'active' : ''} card card-gray text-center p-3" data-bs-toggle="pill" data-bs-target="#listar">
    			<i class="bi bi-card-list fs-3"></i><div>Listar Producto</div>
			</div>
			<div class="nav-link ${activeTab == 'agregar' ? 'active' : ''} card card-gray text-center p-3" data-bs-toggle="pill" data-bs-target="#agregar">
    			<i class="bi bi-plus-circle fs-3"></i><div>Agregar Producto</div>
			</div>
			<div class="nav-link card card-gray text-center p-3" data-bs-toggle="pill" data-bs-target="#consultar">
    			<i class="bi bi-search fs-3"></i><div>Consultar Producto</div>
			</div>
		</div>

		<div class="tab-content">

			<!-- LISTAR -->
			<div class="tab-pane fade ${activeTab != 'agregar' ? 'show active' : ''}" id="listar">
				<div class="d-flex justify-content-between align-items-center mb-3">
					<h5>Listado de Productos (${cantReg != null ? cantReg : 0})</h5>
					<form action="ServletProducto" method="get" class="d-flex gap-2">
						<input type="hidden" name="type" value="buscar"/>
						<input type="text" name="txtBuscar" value="${txtBuscar}" class="form-control" placeholder="Buscar por nombre...">
						<button type="submit" class="btn btn-primary"><i class="bi bi-search"></i></button>
						<a href="ServletProducto?type=listar" class="btn btn-outline-secondary"><i class="bi bi-arrow-clockwise"></i></a>
					</form>
				</div>
				<table class="table table-striped table-hover">
					<thead class="table-dark">
						<tr>
							<th>ID</th><th>Código</th><th>Nombre</th><th>Descripción</th>
							<th>Precio</th><th>Stock</th><th>Proveedor</th><th>Acciones</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="p" items="${dataProducto}">
							<tr>
								<td>${p.idProducto}</td>
								<td>${p.codigo}</td>
								<td>${p.nombre}</td>
								<td>${p.descripcion}</td>
								<td>S/ <fmt:formatNumber value="${p.precio}" minFractionDigits="2" maxFractionDigits="2"/></td>
								<td>
									<c:choose>
										<c:when test="${p.stock == 0}"><span class="badge bg-danger">${p.stock}</span></c:when>
										<c:when test="${p.stock < 5}"><span class="badge bg-warning text-dark">${p.stock}</span></c:when>
										<c:otherwise><span class="badge bg-success">${p.stock}</span></c:otherwise>
									</c:choose>
								</td>
								<td>${p.nombreProveedor != null ? p.nombreProveedor : p.idProveedor}</td>
								<td>
									<a href="ServletProducto?type=infoEdit&id=${p.idProducto}" class="btn btn-sm btn-warning"><i class="bi bi-pencil"></i></a>
									<a href="ServletProducto?type=eliminar&id=${p.idProducto}" class="btn btn-sm btn-danger"
									   onclick="return confirm('¿Eliminar este producto?');"><i class="bi bi-trash"></i></a>
								</td>
							</tr>
						</c:forEach>
						<c:if test="${empty dataProducto}">
							<tr><td colspan="8" class="text-center text-muted">No hay productos registrados.</td></tr>
						</c:if>
					</tbody>
				</table>
			</div>

			<!-- AGREGAR -->
			<div class="tab-pane fade ${activeTab == 'agregar' ? 'show active' : ''}" id="agregar">
				<h5 class="mb-3">Nuevo Producto</h5>
				<form action="ServletProducto" method="post" class="row g-3">
					<input type="hidden" name="type" value="registrar"/>
					<div class="col-md-4">
						<label class="form-label">Código</label>
						<input type="text" name="txtCodigo" class="form-control" required>
					</div>
					<div class="col-md-8">
						<label class="form-label">Nombre</label>
						<input type="text" name="txtNombre" class="form-control" required>
					</div>
					<div class="col-12">
						<label class="form-label">Descripción</label>
						<textarea name="txtDescripcion" class="form-control" rows="2" required></textarea>
					</div>
					<div class="col-md-4">
						<label class="form-label">Precio (S/)</label>
						<input type="number" step="0.01" min="0" name="txtPrecio" class="form-control" required>
					</div>
					<div class="col-md-4">
						<label class="form-label">Stock</label>
						<input type="number" min="0" name="txtStock" class="form-control" required>
					</div>
					<div class="col-md-4">
						<label class="form-label">Proveedor</label>
						<select name="cboProveedor" class="form-select" required>
							<option value="">-- Seleccionar --</option>
							<c:forEach var="prov" items="${listProveedor}">
								<option value="${prov.id_proveedor}">${prov.razon_social}</option>
							</c:forEach>
						</select>
					</div>
					<div class="col-12">
						<button type="submit" class="btn btn-success"><i class="bi bi-save"></i> Registrar</button>
						<button type="reset" class="btn btn-secondary">Limpiar</button>
					</div>
				</form>
				<c:if test="${empty listProveedor}">
					<div class="alert alert-warning mt-3">
						No hay proveedores cargados. <a href="ServletProveedor?type=listar">Registrar un proveedor</a> primero.
					</div>
				</c:if>
			</div>

			<!-- CONSULTAR -->
			<div class="tab-pane fade" id="consultar">
				<h5 class="mb-3">Consultar Producto</h5>
				<form action="ServletProducto" method="get" class="d-flex gap-2 mb-3">
					<input type="hidden" name="type" value="buscar"/>
					<input type="text" name="txtBuscar" class="form-control" placeholder="Buscar producto por nombre o descripción...">
					<button type="submit" class="btn btn-primary">Buscar</button>
				</form>
				<p class="text-muted">Los resultados se mostrarán en la pestaña "Listar Producto".</p>
			</div>
		</div>
    </div>
</div>

<!-- MODAL EDITAR -->
<c:if test="${productoDataEdit != null}">
	<div class="modal fade" id="modalEditarProducto" tabindex="-1" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<form action="ServletProducto" method="post">
					<div class="modal-header">
						<h5 class="modal-title"><i class="bi bi-pencil"></i> Editar Producto</h5>
						<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
					</div>
					<div class="modal-body row g-3">
						<input type="hidden" name="type" value="editar"/>
						<input type="hidden" name="idProducto" value="${productoDataEdit.idProducto}"/>
						<div class="col-md-4">
							<label class="form-label">Código</label>
							<input type="text" name="txtCodigo" class="form-control" value="${productoDataEdit.codigo}" required>
						</div>
						<div class="col-md-8">
							<label class="form-label">Nombre</label>
							<input type="text" name="txtNombre" class="form-control" value="${productoDataEdit.nombre}" required>
						</div>
						<div class="col-12">
							<label class="form-label">Descripción</label>
							<textarea name="txtDescripcion" class="form-control" rows="2" required>${productoDataEdit.descripcion}</textarea>
						</div>
						<div class="col-md-4">
							<label class="form-label">Precio (S/)</label>
							<input type="number" step="0.01" min="0" name="txtPrecio" class="form-control" value="${productoDataEdit.precio}" required>
						</div>
						<div class="col-md-4">
							<label class="form-label">Stock</label>
							<input type="number" min="0" name="txtStock" class="form-control" value="${productoDataEdit.stock}" required>
						</div>
						<div class="col-md-4">
							<label class="form-label">Proveedor</label>
							<select name="cboProveedor" class="form-select" required>
								<c:forEach var="prov" items="${listProveedor}">
									<option value="${prov.id_proveedor}" ${prov.id_proveedor == productoDataEdit.idProveedor ? 'selected' : ''}>
										${prov.razon_social}
									</option>
								</c:forEach>
							</select>
						</div>
					</div>
					<div class="modal-footer">
						<a href="ServletProducto?type=listar" class="btn btn-secondary">Cancelar</a>
						<button type="submit" class="btn btn-primary"><i class="bi bi-save"></i> Guardar cambios</button>
					</div>
				</form>
			</div>
		</div>
	</div>
	<script>
		document.addEventListener('DOMContentLoaded', function () {
			var modal = new bootstrap.Modal(document.getElementById('modalEditarProducto'));
			modal.show();
		});
	</script>
</c:if>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
