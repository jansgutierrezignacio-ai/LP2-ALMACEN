<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${dataMovimiento == null}">
	<jsp:forward page="movimientos?type=listar"/>
</c:if>

<!DOCTYPE html>
<html lang="es">
<head>
	<meta charset="UTF-8">
	<title>Movimientos</title>

    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="CSS/General.css">
    <link rel="stylesheet" href="CSS/modern.css">
</head>

<body>
<div class="d-flex">

    <%@ include file="menu.jsp" %>

    <!-- Contenido -->
    <div class="content flex-grow-1 p-3">

        <!-- Topbar -->
        <div class="topbar d-flex justify-content-between align-items-center px-4 mb-4">
            <h5><i class="bi bi-clock-history me-2"></i>Movimientos</h5>
            <span class="badge-role">
            	<i class="bi bi-person-circle me-1"></i>
            	${sessionScope.usuario.username} · ${rolNombre}
            </span>
        </div>

        <!-- NAV -->
        <div class="nav nav-pills nav-pills-custom d-flex flex-nowrap gap-3 p-2">
			<div class="nav-link active text-center p-3">
    			<i class="bi bi-card-list fs-3"></i>
    			<div>Historial de Movimientos</div>
			</div>
		</div>

		<!-- TAB CONTENT -->
		<div class="tab-content">

			<!-- LISTAR -->
			<div class="tab-pane fade show active">

				<div class="d-flex justify-content-between align-items-center mb-3">
					<h5>Listado de Movimientos (${cantReg != null ? cantReg : 0})</h5>
				</div>

				<table class="table table-custom table-hover">
					<thead>
						<tr>
							<th>ID</th>
							<th>Tipo</th>
							<th>Descripción</th>
							<th>Fecha</th>
							<th>Usuario</th>
						</tr>
					</thead>

					<tbody>
						<c:forEach var="m" items="${dataMovimiento}">
							<tr>
								<td>${m.idMovimiento}</td>
								<td><span class="badge-movement badge-general">${m.tipo}</span></td>
								<td>${m.descripcion}</td>
								<td>${m.fecha}</td>
								<td>${m.usuario}</td>
							</tr>
						</c:forEach>

						<c:if test="${empty dataMovimiento}">
							<tr>
								<td colspan="5" class="text-center text-muted">
									No hay movimientos registrados.
								</td>
							</tr>
						</c:if>
					</tbody>
				</table>

			</div>
		</div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>