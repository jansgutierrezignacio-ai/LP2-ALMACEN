<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${listaProductos == null}">
	<jsp:forward page="ventas"/>
</c:if>

<!DOCTYPE html>
<html lang="es">
<head>
	<meta charset="UTF-8">
	<title>Punto de Venta</title>

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
			<h5><i class="bi bi-cart-check me-2"></i>Punto de Venta</h5>
			<span class="badge-role">
				<i class="bi bi-person-circle me-1"></i>
				${sessionScope.usuario.username} · ${rolNombre}
			</span>
		</div>

		<div class="container-fluid p-4">

			<c:if test="${not empty mensajeOk}">
				<div class="alert alert-success alert-dismissible fade show" role="alert">
					<i class="bi bi-check-circle"></i> ${mensajeOk}
					<button type="button" class="btn-close" data-bs-dismiss="alert"></button>
				</div>
			</c:if>
			<c:if test="${not empty mensaje}">
				<div class="alert alert-danger alert-dismissible fade show" role="alert">
					<i class="bi bi-exclamation-triangle"></i> ${mensaje}
					<button type="button" class="btn-close" data-bs-dismiss="alert"></button>
				</div>
			</c:if>

			<div class="row g-4">

				<!-- COLUMNA IZQUIERDA: agregar producto y carrito -->
				<div class="col-lg-7">

					<div class="card shadow-sm mb-4">
						<div class="card-header card-header-dark">
							<i class="bi bi-cart-plus"></i> Agregar producto a la venta
						</div>
						<div class="card-body">
							<form action="ventas" method="post" class="row g-3">
								<input type="hidden" name="type" value="agregar"/>
								<div class="col-md-7">
									<label class="form-label">Seleccionar producto</label>
									<select name="idProducto" class="form-select" required>
										<option value="" selected disabled>-- Elegir producto --</option>
										<c:forEach items="${listaProductos}" var="p">
											<option value="${p.idProducto}">${p.nombre} | Stock: ${p.stock} | S/ <fmt:formatNumber value="${p.precio}" type="number" minFractionDigits="2" maxFractionDigits="2"/></option>
										</c:forEach>
									</select>
								</div>
								<div class="col-md-3">
									<label class="form-label">Cantidad</label>
									<input type="number" name="cantidad" value="1" min="1" class="form-control" required>
								</div>
								<div class="col-md-2 d-flex align-items-end">
									<button type="submit" class="btn btn-success w-100"><i class="bi bi-plus-lg"></i> Añadir</button>
								</div>
							</form>
						</div>
					</div>

					<!-- Carrito -->
					<div class="card shadow-sm">
						<div class="card-header card-header-teal">
							<i class="bi bi-list-check"></i> Detalle de la venta actual
						</div>
						<div class="card-body p-0">
							<div class="table-responsive">
								<table class="table table-custom table-hover align-middle mb-0">
									<thead>
										<tr>
											<th>Código</th>
											<th>Producto</th>
											<th>Precio</th>
											<th>Cant.</th>
											<th>Subtotal</th>
											<th class="text-center">Acción</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach items="${carrito}" var="item">
											<tr>
												<td>${item.codigo}</td>
												<td>${item.nombre}</td>
												<td>S/ <fmt:formatNumber value="${item.precioUnitario}" type="number" minFractionDigits="2" maxFractionDigits="2"/></td>
												<td>${item.cantidad}</td>
												<td>S/ <fmt:formatNumber value="${item.subtotal}" type="number" minFractionDigits="2" maxFractionDigits="2"/></td>
												<td class="text-center">
													<form action="ventas" method="post" class="d-inline">
														<input type="hidden" name="type" value="eliminar"/>
														<input type="hidden" name="idProducto" value="${item.idProducto}"/>
														<button type="submit" class="btn btn-sm btn-outline-danger" title="Quitar del carrito"><i class="bi bi-trash"></i></button>
													</form>
												</td>
											</tr>
										</c:forEach>
										<c:if test="${empty carrito}">
											<tr>
												<td colspan="6" class="text-center text-muted py-4">
													<i class="bi bi-cart-x fs-2 d-block"></i>
													No hay productos agregados a la venta.
												</td>
											</tr>
										</c:if>
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>

				<!-- COLUMNA DERECHA: confirmación y historial -->
				<div class="col-lg-5">

					<div class="card shadow-sm border-primary mb-4">
						<div class="card-header card-header-blue">
							<i class="bi bi-receipt"></i> Resumen de la transacción
						</div>
						<div class="card-body">
							<form action="ventas" method="post">
								<input type="hidden" name="type" value="confirmar"/>
								<div class="mb-3">
									<label class="form-label">Cliente</label>
									<select name="idCliente" class="form-select" required>
										<option value="" selected disabled>-- Seleccionar cliente --</option>
										<c:forEach items="${listaClientes}" var="c">
											<option value="${c.idCliente}">${c.nombre} ${c.apellido}</option>
										</c:forEach>
									</select>
								</div>

								<div class="total-box text-center mb-3">
									<span class="text-muted text-uppercase fw-bold d-block">Total a pagar</span>
									<h2 class="text-primary m-0">S/ <fmt:formatNumber value="${totalVenta}" type="number" minFractionDigits="2" maxFractionDigits="2"/></h2>
								</div>

								<button type="submit" class="btn btn-primary w-100 btn-lg" <c:if test="${empty carrito}">disabled</c:if>>
									<i class="bi bi-check-circle-fill"></i> Confirmar venta
								</button>
							</form>
						</div>
					</div>

					<!-- Historial -->
					<div class="card shadow-sm">
						<div class="card-header card-header-dark">
							<i class="bi bi-clock-history"></i> Historial reciente de ventas
						</div>
						<div class="card-body p-0">
							<ul class="list-group list-group-flush">
								<c:forEach items="${historialVentas}" var="h">
									<li class="list-group-item d-flex justify-content-between align-items-center">
										<div>
											<strong>Venta #${h.idVenta}</strong><br>
											<small class="text-muted">${h.fecha} &middot; ${h.nombreCliente} &middot; ${h.username}</small>
										</div>
										<span class="badge bg-success fs-6">S/ <fmt:formatNumber value="${h.total}" type="number" minFractionDigits="2" maxFractionDigits="2"/></span>
									</li>
								</c:forEach>
								<c:if test="${empty historialVentas}">
									<li class="list-group-item text-muted text-center">Sin ventas registradas.</li>
								</c:if>
							</ul>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
