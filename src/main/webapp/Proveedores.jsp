<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${dataProveedor == null}">
	<jsp:forward page="proveedores?type=listar"/>
</c:if>
<!DOCTYPE html>
<html lang="es">
<head>
	<meta charset="UTF-8">
	<title>Proveedores</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="CSS/General.css">
    <link rel="stylesheet" href="CSS/modern.css">
</head>
<body>
<div class="d-flex">

    <%@ include file="menu.jsp" %>

    <div class="content flex-grow-1 p-3">
        <div class="topbar d-flex justify-content-between align-items-center px-4 mb-4">
            <h5><i class="bi bi-truck me-2"></i>Proveedores</h5>
            <span class="badge-role">
            	<i class="bi bi-person-circle me-1"></i>
            	${sessionScope.usuario.username} · ${rolNombre}
            </span>
        </div>

        <c:if test="${not empty mensajeOk}">
        	<div class="alert alert-success alert-dismissible fade show mt-3" role="alert">
        		<i class="bi bi-check-circle"></i> ${mensajeOk}
        		<button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        	</div>
        </c:if>

        <div class="nav nav-pills nav-pills-custom d-flex flex-nowrap overflow-auto gap-3 p-2" role="tablist">
			<div class="nav-link ${activeTab != 'agregar' ? 'active' : ''} text-center p-3" data-bs-toggle="pill" data-bs-target="#listar">
    			<i class="bi bi-card-list fs-3"></i><div>Listar Proveedor</div>
			</div>
			<div class="nav-link ${activeTab == 'agregar' ? 'active' : ''} text-center p-3" data-bs-toggle="pill" data-bs-target="#agregar">
    			<i class="bi bi-plus-circle fs-3"></i><div>Agregar Proveedor</div>
			</div>
			<div class="nav-link text-center p-3" data-bs-toggle="pill" data-bs-target="#consultar">
    			<i class="bi bi-search fs-3"></i><div>Consultar Proveedor</div>
			</div>
		</div>

		<div class="tab-content">

			<!-- LISTAR -->
			<div class="tab-pane fade ${activeTab != 'agregar' ? 'show active' : ''}" id="listar">
				<div class="d-flex justify-content-between align-items-center mb-3">
					<h5>Listado de Proveedores (${cantReg != null ? cantReg : 0})</h5>
					<form action="proveedores" method="get" class="d-flex gap-2">
						<input type="hidden" name="type" value="buscar"/>
						<input type="text" name="txtBuscar" value="${txtBuscar}" class="form-control" placeholder="Buscar por razón social...">
						<button type="submit" class="btn btn-primary"><i class="bi bi-search"></i></button>
						<a href="proveedores?type=listar" class="btn btn-outline-secondary"><i class="bi bi-arrow-clockwise"></i></a>
					</form>
				</div>
				<table class="table table-custom table-hover">
					<thead>
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
									<a href="proveedores?type=infoEdit&id=${p.id_proveedor}" class="btn btn-sm btn-warning"><i class="bi bi-pencil"></i></a>
									<form action="proveedores" method="post" class="d-inline" onsubmit="return confirm('¿Eliminar este proveedor?');">
										<input type="hidden" name="type" value="eliminar"/>
										<input type="hidden" name="id" value="${p.id_proveedor}"/>
										<button type="submit" class="btn btn-sm btn-danger"><i class="bi bi-trash"></i></button>
									</form>
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
				<form action="proveedores" method="post" class="row g-3">
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
				<form action="proveedores" method="get" class="d-flex gap-2 mb-3">
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
				<form action="proveedores" method="post">
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
						<a href="proveedores?type=listar" class="btn btn-secondary">Cancelar</a>
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
