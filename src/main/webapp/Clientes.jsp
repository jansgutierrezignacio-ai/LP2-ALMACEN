<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- Si se entra directo a la página sin pasar por el servlet, cargamos datos --%>
<c:if test="${dataCliente == null}">
	<jsp:forward page="ServletCliente?type=listar"/>
</c:if>
<!DOCTYPE html>
<html lang="es">
<head>
	<meta charset="UTF-8">
	<title>Clientes</title>

    <!-- Bootstrap -->
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

    <!-- Sidebar -->
    <div class="sidebar p-3">
        <h4 class="text-white mb-4">Inventario</h4>
        <ul class="nav flex-column">
            <li class="nav-item"><a class="nav-link" href="ServletInicio"><i class="bi bi-speedometer2"></i> Dashboard</a></li>
            <li class="nav-item"><a class="nav-link active" href="ServletCliente?type=listar"><i class="bi bi-people"></i> Clientes</a></li>
            <li class="nav-item"><a class="nav-link" href="ServletProveedor?type=listar"><i class="bi bi-truck"></i> Proveedores</a></li>
            <li class="nav-item"><a class="nav-link" href="ServletProducto?type=listar"><i class="bi bi-box"></i> Productos</a></li>
            <li class="nav-item"><a class="nav-link" href="ServletReporte?type=listar"><i class="bi bi-graph-up-arrow"></i> Reportes</a></li>
            <li class="nav-item"><a class="nav-link" href="ServletMovimiento?type=listar"><i class="bi bi-clock-history"></i> Movimientos</a></li>
            <li class="nav-item"><a class="nav-link" href="ServletUsuario?type=logout"><i class="bi bi-box-arrow-right"></i> Cerrar Sesión</a></li>
        </ul>
    </div>

    <!-- Contenido -->
    <div class="content flex-grow-1 p-3">

        <!-- Topbar -->
        <div class="topbar d-flex justify-content-between align-items-center px-4">
            <h5>Cliente</h5>
            <span class="text-white">Usuario: ${sessionScope.usuario.username}</span>
        </div>

        <c:if test="${not empty mensajeOk}">
        	<div class="alert alert-success alert-dismissible fade show mt-3" role="alert">
        		<i class="bi bi-check-circle"></i> ${mensajeOk}
        		<button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        	</div>
        </c:if>

		<!-- NavBar Cards -->
        <div class="nav nav-pills d-flex flex-nowrap overflow-auto gap-3 p-2" role="tablist">
			<div class="nav-link ${activeTab != 'agregar' ? 'active' : ''} card card-gray text-center p-3" data-bs-toggle="pill" data-bs-target="#listar">
    			<i class="bi bi-card-list fs-3"></i>
    			<div>Listar Cliente</div>
			</div>
			<div class="nav-link ${activeTab == 'agregar' ? 'active' : ''} card card-gray text-center p-3" data-bs-toggle="pill" data-bs-target="#agregar">
    			<i class="bi bi-person-plus fs-3"></i>
    			<div>Agregar Cliente</div>
			</div>
			<div class="nav-link card card-gray text-center p-3" data-bs-toggle="pill" data-bs-target="#consultar">
    			<i class="bi bi-search fs-3"></i>
    			<div>Consultar Cliente</div>
			</div>
		</div>

		<!-- Paneles de contenido (tab-content) -->
		<div class="tab-content">

			<!-- PANEL LISTAR -->
			<div class="tab-pane fade ${activeTab != 'agregar' ? 'show active' : ''}" id="listar">
				<div class="d-flex justify-content-between align-items-center mb-3">
					<h5>Listado de Clientes (${cantReg != null ? cantReg : 0})</h5>
					<form action="ServletCliente" method="get" class="d-flex gap-2">
						<input type="hidden" name="type" value="buscar"/>
						<input type="text" name="txtBuscar" value="${txtBuscar}" class="form-control" placeholder="Buscar por nombre...">
						<button type="submit" class="btn btn-primary"><i class="bi bi-search"></i></button>
						<a href="ServletCliente?type=listar" class="btn btn-outline-secondary"><i class="bi bi-arrow-clockwise"></i></a>
					</form>
				</div>
				<table class="table table-striped table-hover">
					<thead class="table-dark">
						<tr>
							<th>ID</th><th>DNI</th><th>Nombre</th><th>Apellido</th><th>Teléfono</th><th>Acciones</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="c" items="${dataCliente}">
							<tr>
								<td>${c.idCliente}</td>
								<td>${c.dni}</td>
								<td>${c.nombre}</td>
								<td>${c.apellido}</td>
								<td>${c.telefono}</td>
								<td>
									<a href="ServletCliente?type=infoEdit&id=${c.idCliente}" class="btn btn-sm btn-warning" title="Editar"><i class="bi bi-pencil"></i></a>
									<a href="ServletCliente?type=eliminar&id=${c.idCliente}" class="btn btn-sm btn-danger" title="Eliminar"
									   onclick="return confirm('¿Eliminar este cliente?');"><i class="bi bi-trash"></i></a>
								</td>
							</tr>
						</c:forEach>
						<c:if test="${empty dataCliente}">
							<tr><td colspan="6" class="text-center text-muted">No hay clientes registrados.</td></tr>
						</c:if>
					</tbody>
				</table>
			</div>

			<!-- PANEL AGREGAR -->
			<div class="tab-pane fade ${activeTab == 'agregar' ? 'show active' : ''}" id="agregar">
				<h5 class="mb-3">Nuevo Cliente</h5>
				<form action="ServletCliente" method="post" class="row g-3">
					<input type="hidden" name="type" value="registrar"/>
					<div class="col-md-6">
						<label class="form-label">DNI</label>
						<input type="text" name="txtDni" class="form-control" maxlength="9" required>
					</div>
					<div class="col-md-6">
						<label class="form-label">Teléfono</label>
						<input type="text" name="txtTelefono" class="form-control" maxlength="9" required>
					</div>
					<div class="col-md-6">
						<label class="form-label">Nombre</label>
						<input type="text" name="txtNombre" class="form-control" required>
					</div>
					<div class="col-md-6">
						<label class="form-label">Apellido</label>
						<input type="text" name="txtApellido" class="form-control" required>
					</div>
					<div class="col-12">
						<button type="submit" class="btn btn-success"><i class="bi bi-save"></i> Registrar</button>
						<button type="reset" class="btn btn-secondary">Limpiar</button>
					</div>
				</form>
			</div>

			<!-- PANEL CONSULTAR -->
			<div class="tab-pane fade" id="consultar">
				<h5 class="mb-3">Consultar Cliente</h5>
				<form action="ServletCliente" method="get" class="d-flex gap-2 mb-3">
					<input type="hidden" name="type" value="buscar"/>
					<input type="text" name="txtBuscar" class="form-control" placeholder="Buscar cliente por nombre o apellido...">
					<button type="submit" class="btn btn-primary">Buscar</button>
				</form>
				<p class="text-muted">Los resultados se mostrarán en la pestaña "Listar Cliente".</p>
			</div>
		</div>
    </div>
</div>

<!-- MODAL EDITAR (se abre automáticamente si hay clienteDataEdit) -->
<c:if test="${clienteDataEdit != null}">
	<div class="modal fade" id="modalEditarCliente" tabindex="-1" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<form action="ServletCliente" method="post">
					<div class="modal-header">
						<h5 class="modal-title"><i class="bi bi-pencil"></i> Editar Cliente</h5>
						<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
					</div>
					<div class="modal-body">
						<input type="hidden" name="type" value="editar"/>
						<input type="hidden" name="idCliente" value="${clienteDataEdit.idCliente}"/>
						<div class="mb-3">
							<label class="form-label">DNI</label>
							<input type="text" name="txtDni" class="form-control" value="${clienteDataEdit.dni}" maxlength="9" required>
						</div>
						<div class="mb-3">
							<label class="form-label">Nombre</label>
							<input type="text" name="txtNombre" class="form-control" value="${clienteDataEdit.nombre}" required>
						</div>
						<div class="mb-3">
							<label class="form-label">Apellido</label>
							<input type="text" name="txtApellido" class="form-control" value="${clienteDataEdit.apellido}" required>
						</div>
						<div class="mb-3">
							<label class="form-label">Teléfono</label>
							<input type="text" name="txtTelefono" class="form-control" value="${clienteDataEdit.telefono}" maxlength="9" required>
						</div>
					</div>
					<div class="modal-footer">
						<a href="ServletCliente?type=listar" class="btn btn-secondary">Cancelar</a>
						<button type="submit" class="btn btn-primary"><i class="bi bi-save"></i> Guardar cambios</button>
					</div>
				</form>
			</div>
		</div>
	</div>
	<script>
		document.addEventListener('DOMContentLoaded', function () {
			var modal = new bootstrap.Modal(document.getElementById('modalEditarCliente'));
			modal.show();
		});
	</script>
</c:if>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
