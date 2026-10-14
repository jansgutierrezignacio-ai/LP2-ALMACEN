<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:if test="${dataProveedor == null}">
	<jsp:forward page="ServletProveedor?type=listar"/>
</c:if>
<!DOCTYPE html>
<html lang="es">
<head>
	<meta charset="UTF-8">
	<title>Proveedores</title>

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
            <li class="nav-item"><a class="nav-link" href="ServletCliente?type=listar"><i class="bi bi-people"></i> Clientes</a></li>
            <li class="nav-item"><a class="nav-link active" href="ServletProveedor?type=listar"><i class="bi bi-truck"></i> Proveedores</a></li>
            <li class="nav-item"><a class="nav-link" href="ServletProducto?type=listar"><i class="bi bi-box"></i> Productos</a></li>
            <li class="nav-item"><a class="nav-link" href="ServletReporte?type=listar"><i class="bi bi-graph-up-arrow"></i> Reportes</a></li>
            <li class="nav-item"><a class="nav-link" href="ServletMovimiento?type=listar"><i class="bi bi-clock-history"></i> Movimientos</a></li>   
            <li class="nav-item"><a class="nav-link" href="ServletUsuario?type=logout"><i class="bi bi-box-arrow-right"></i> Cerrar Sesión</a></li>
        </ul>
    </div>

    <div class="content flex-grow-1 p-3">
        <div class="topbar d-flex justify-content-between align-items-center px-4">
            <h5>Proveedor</h5>
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
    			<i class="bi bi-card-list fs-3"></i><div>Listar Proveedor</div>
			</div>
			<div class="nav-link ${activeTab == 'agregar' ? 'active' : ''} card card-gray text-center p-3" data-bs-toggle="pill" data-bs-target="#agregar">
    			<i class="bi bi-plus-circle fs-3"></i><div>Agregar Proveedor</div>
			</div>
			<div class="nav-link card card-gray text-center p-3" data-bs-toggle="pill" data-bs-target="#consultar">
    			<i class="bi bi-search fs-3"></i><div>Consultar Proveedor</div>
			</div>
		</div>

		<div class="tab-content">

			<!-- LISTAR -->
			<div class="tab-pane fade ${activeTab != 'agregar' ? 'show active' : ''}" id="listar">
				<div class="d-flex justify-content-between align-items-center mb-3">
					<h5>Listado de Proveedores (${cantReg != null ? cantReg : 0})</h5>
					<form action="ServletProveedor" method="get" class="d-flex gap-2">
						<input type="hidden" name="type" value="buscar"/>
						<input type="text" name="txtBuscar" value="${txtBuscar}" class="form-control" placeholder="Buscar por razón social...">
						<button type="submit" class="btn btn-primary"><i class="bi bi-search"></i></button>
						<a href="ServletProveedor?type=listar" class="btn btn-outline-secondary"><i class="bi bi-arrow-clockwise"></i></a>
					</form>
				</div>
				<table class="table table-striped table-hover">
					<thead class="table-dark">
						<tr><th>ID</th><th>RUC</th><th>Razón Social</th><th>Teléfono</th><th>Estado</th><th>Acciones</th></tr>
					</thead>
					<tbody>
						<c:forEach var="p" items="${dataProveedor}">
							<tr>
								<td>${p.id_proveedor}</td>
								<td>${p.ruc}</td>
								<td>${p.razon_social}</td>
								<td>${p.telefono}</td>
								<td>
									<c:choose>
										<c:when test="${p.estado == 1}"><span class="badge bg-success">Activo</span></c:when>
										<c:otherwise><span class="badge bg-secondary">Inactivo</span></c:otherwise>
									</c:choose>
								</td>
								<td>
									<a href="ServletProveedor?type=infoEdit&id=${p.id_proveedor}" class="btn btn-sm btn-warning"><i class="bi bi-pencil"></i></a>
									<a href="ServletProveedor?type=eliminar&id=${p.id_proveedor}" class="btn btn-sm btn-danger"
									   onclick="return confirm('¿Eliminar este proveedor?');"><i class="bi bi-trash"></i></a>
								</td>
							</tr>
						</c:forEach>
						<c:if test="${empty dataProveedor}">
							<tr><td colspan="6" class="text-center text-muted">No hay proveedores registrados.</td></tr>
						</c:if>
					</tbody>
				</table>
			</div>

			<!-- AGREGAR -->
			<div class="tab-pane fade ${activeTab == 'agregar' ? 'show active' : ''}" id="agregar">
				<h5 class="mb-3">Nuevo Proveedor</h5>
				<form action="ServletProveedor" method="post" class="row g-3">
					<input type="hidden" name="type" value="registrar"/>
					<div class="col-md-6">
						<label class="form-label">RUC</label>
						<input type="text" name="txtRuc" class="form-control" maxlength="11" required>
					</div>
					<div class="col-md-6">
						<label class="form-label">Razón Social</label>
						<input type="text" name="txtRazonSocial" class="form-control" required>
					</div>
					<div class="col-md-6">
						<label class="form-label">Teléfono</label>
						<input type="text" name="txtTelefono" class="form-control" maxlength="15" required>
					</div>
					<div class="col-md-6">
						<label class="form-label">Estado</label>
						<select name="txtEstado" class="form-select">
							<option value="1" selected>Activo</option>
							<option value="0">Inactivo</option>
						</select>
					</div>
					<div class="col-12">
						<button type="submit" class="btn btn-success"><i class="bi bi-save"></i> Registrar</button>
						<button type="reset" class="btn btn-secondary">Limpiar</button>
					</div>
				</form>
			</div>

			<!-- CONSULTAR -->
			<div class="tab-pane fade" id="consultar">
				<h5 class="mb-3">Consultar Proveedor</h5>
				<form action="ServletProveedor" method="get" class="d-flex gap-2 mb-3">
					<input type="hidden" name="type" value="buscar"/>
					<input type="text" name="txtBuscar" class="form-control" placeholder="Buscar proveedor por razón social...">
					<button type="submit" class="btn btn-primary">Buscar</button>
				</form>
			</div>
		</div>
    </div>
</div>

<!-- MODAL EDITAR -->
<c:if test="${proveedorDataEdit != null}">
	<div class="modal fade" id="modalEditarProveedor" tabindex="-1" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<form action="ServletProveedor" method="post">
					<div class="modal-header">
						<h5 class="modal-title"><i class="bi bi-pencil"></i> Editar Proveedor</h5>
						<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
					</div>
					<div class="modal-body">
						<input type="hidden" name="type" value="editar"/>
						<input type="hidden" name="idProveedor" value="${proveedorDataEdit.id_proveedor}"/>
						<div class="mb-3">
							<label class="form-label">RUC</label>
							<input type="text" name="txtRuc" class="form-control" value="${proveedorDataEdit.ruc}" maxlength="11" required>
						</div>
						<div class="mb-3">
							<label class="form-label">Razón Social</label>
							<input type="text" name="txtRazonSocial" class="form-control" value="${proveedorDataEdit.razon_social}" required>
						</div>
						<div class="mb-3">
							<label class="form-label">Teléfono</label>
							<input type="text" name="txtTelefono" class="form-control" value="${proveedorDataEdit.telefono}" maxlength="15" required>
						</div>
						<div class="mb-3">
							<label class="form-label">Estado</label>
							<select name="txtEstado" class="form-select">
								<option value="1" ${proveedorDataEdit.estado == 1 ? 'selected' : ''}>Activo</option>
								<option value="0" ${proveedorDataEdit.estado == 0 ? 'selected' : ''}>Inactivo</option>
							</select>
						</div>
					</div>
					<div class="modal-footer">
						<a href="ServletProveedor?type=listar" class="btn btn-secondary">Cancelar</a>
						<button type="submit" class="btn btn-primary"><i class="bi bi-save"></i> Guardar cambios</button>
					</div>
				</form>
			</div>
		</div>
	</div>
	<script>
		document.addEventListener('DOMContentLoaded', function () {
			var modal = new bootstrap.Modal(document.getElementById('modalEditarProveedor'));
			modal.show();
		});
	</script>
</c:if>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
