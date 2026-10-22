<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

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
					<c:if test="${not empty idVentaRecien}">
						<a href="reportes/boleta?id=${idVentaRecien}" class="btn btn-sm btn-outline-success ms-2">
							<i class="bi bi-file-earmark-pdf"></i> Descargar boleta
						</a>
					</c:if>
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

				<!-- ================= IZQUIERDA: catálogo + historial ================= -->
				<div class="col-xl-8">

					<div class="card shadow-sm mb-4">
						<div class="card-header card-header-dark d-flex justify-content-between align-items-center">
							<span><i class="bi bi-box-seam"></i> Catálogo de productos</span>
							<span class="badge bg-light text-dark">${fn:length(listaProductos)} producto(s)</span>
						</div>

						<div class="card-body pb-0">
							<form action="ventas" method="get" class="input-group mb-3">
								<span class="input-group-text bg-white"><i class="bi bi-search"></i></span>
								<input type="text" name="q" value="${fn:escapeXml(q)}" class="form-control"
								       placeholder="Buscar por nombre, descripción o código..." autofocus>
								<button type="submit" class="btn btn-dark">Buscar</button>
								<c:if test="${not empty q}">
									<a href="ventas" class="btn btn-outline-secondary" title="Limpiar búsqueda">
										<i class="bi bi-x-lg"></i>
									</a>
								</c:if>
							</form>
						</div>

						<div class="table-responsive catalogo-scroll">
							<table class="table table-custom table-hover align-middle mb-0">
								<thead>
									<tr>
										<th>Código</th>
										<th>Producto</th>
										<th class="text-end">Precio</th>
										<th class="text-center">Stock</th>
										<th class="text-end">Agregar</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${listaProductos}" var="p">
										<c:set var="cantidadCarrito" value="${enCarrito[p.idProducto]}"/>
										<c:set var="stockDisponible" value="${p.stock - cantidadCarrito}"/>
										<tr class="${stockDisponible > 0 ? 'fila-producto' : ''}"
											<c:if test="${stockDisponible > 0}">onclick="document.getElementById('agregar-${p.idProducto}').requestSubmit();"</c:if>>
											<td class="text-muted small text-nowrap">${p.codigo}</td>
											<td>${p.nombre}</td>
											<td class="text-end text-nowrap">S/ <fmt:formatNumber value="${p.precio}" type="number" minFractionDigits="2" maxFractionDigits="2"/></td>
											<td class="text-center">
												<span class="badge ${stockDisponible <= 0 ? 'bg-secondary' : (stockDisponible <= 5 ? 'bg-danger' : (stockDisponible <= 15 ? 'bg-warning text-dark' : 'bg-success'))}"
												      title="En el carrito: ${empty cantidadCarrito ? 0 : cantidadCarrito}">${stockDisponible}</span>
											</td>
											<td class="text-end">
												<form id="agregar-${p.idProducto}" action="ventas" method="post"
												      class="d-inline" onclick="event.stopPropagation();">
													<input type="hidden" name="type" value="agregar">
													<input type="hidden" name="idProducto" value="${p.idProducto}">
													<input type="hidden" name="cantidad" value="1">
													<input type="hidden" name="q" value="${fn:escapeXml(q)}">
													<input type="hidden" class="cliente-recordado" name="idCliente" value="${idClienteSeleccionado}">
													<button type="submit" class="btn btn-sm btn-success" ${stockDisponible <= 0 ? 'disabled' : ''}
													        title="Agregar 1 unidad">
														<i class="bi bi-plus-lg"></i>
													</button>
												</form>
											</td>
										</tr>
									</c:forEach>

									<c:if test="${empty listaProductos}">
										<tr>
											<td colspan="5" class="text-center text-muted py-5">
												<i class="bi bi-search fs-2 d-block mb-1"></i>
												<c:choose>
													<c:when test="${not empty q}">
														No se encontraron productos para "<strong>${fn:escapeXml(q)}</strong>".
													</c:when>
													<c:otherwise>No hay productos registrados.</c:otherwise>
												</c:choose>
											</td>
										</tr>
									</c:if>
								</tbody>
							</table>
						</div>
					</div>

					<!-- Historial (colapsable) -->
					<div class="card shadow-sm">
						<div class="card-header card-header-dark d-flex justify-content-between align-items-center"
						     data-bs-toggle="collapse" data-bs-target="#historialVentas" role="button" aria-expanded="true">
							<span><i class="bi bi-clock-history"></i> Historial reciente de ventas</span>
							<i class="bi bi-chevron-down"></i>
						</div>
						<div class="collapse show" id="historialVentas">
							<ul class="list-group list-group-flush">
								<c:forEach items="${historialVentas}" var="h">
									<li class="list-group-item d-flex justify-content-between align-items-center">
										<div>
											<strong>Venta #${h.idVenta}</strong>
											<small class="text-muted d-block">${h.fecha} &middot; ${h.nombreCliente} &middot; ${h.username}</small>
										</div>
										<div class="d-flex align-items-center gap-2">
											<span class="badge bg-success fs-6">S/ <fmt:formatNumber value="${h.total}" type="number" minFractionDigits="2" maxFractionDigits="2"/></span>
											<a href="reportes/boleta?id=${h.idVenta}" class="btn btn-sm btn-outline-danger"
											   title="Descargar boleta en PDF">
												<i class="bi bi-file-earmark-pdf"></i>
											</a>
										</div>
									</li>
								</c:forEach>
								<c:if test="${empty historialVentas}">
									<li class="list-group-item text-muted text-center">Sin ventas registradas.</li>
								</c:if>
							</ul>
						</div>
					</div>
				</div>

				<!-- ================= DERECHA: carrito, cliente y boleta (sticky) ================= -->
				<div class="col-xl-4">
					<div class="pos-resumen">

						<div class="card shadow-sm mb-3">
							<div class="card-header card-header-teal d-flex justify-content-between align-items-center">
								<span><i class="bi bi-cart3"></i> Venta actual</span>
								<span class="badge bg-light text-dark">${fn:length(carrito)} ítem(s)</span>
							</div>

							<c:choose>
								<c:when test="${empty carrito}">
									<div class="card-body text-center text-muted py-5">
										<i class="bi bi-cart-x fs-1 d-block mb-2"></i>
										Selecciona productos del catálogo.
									</div>
								</c:when>
								<c:otherwise>
									<ul class="list-group list-group-flush">
										<c:forEach items="${carrito}" var="item">
											<li class="list-group-item">
												<div class="d-flex justify-content-between align-items-start">
													<div class="me-2">
														<div class="fw-semibold">${item.nombre}</div>
														<small class="text-muted">${item.codigo} · S/ <fmt:formatNumber value="${item.precioUnitario}" type="number" minFractionDigits="2" maxFractionDigits="2"/></small>
													</div>
													<form action="ventas" method="post" class="m-0">
														<input type="hidden" name="type" value="eliminar">
														<input type="hidden" name="idProducto" value="${item.idProducto}">
														<input type="hidden" name="q" value="${fn:escapeXml(q)}">
														<input type="hidden" class="cliente-recordado" name="idCliente" value="${idClienteSeleccionado}">
														<button type="submit" class="btn btn-sm btn-link text-danger p-0" title="Quitar del carrito">
															<i class="bi bi-x-lg"></i>
														</button>
													</form>
												</div>

												<div class="d-flex justify-content-between align-items-center mt-2">
													<div class="d-flex align-items-center gap-1">
														<form action="ventas" method="post" class="m-0">
															<input type="hidden" name="type" value="actualizar">
															<input type="hidden" name="idProducto" value="${item.idProducto}">
															<input type="hidden" name="cantidad" value="${item.cantidad - 1}">
															<input type="hidden" name="q" value="${fn:escapeXml(q)}">
															<input type="hidden" class="cliente-recordado" name="idCliente" value="${idClienteSeleccionado}">
															<button type="submit" class="btn btn-sm btn-outline-secondary px-2" title="Quitar uno">&minus;</button>
														</form>
														<span class="pos-cantidad badge bg-light text-dark border">${item.cantidad}</span>
														<form action="ventas" method="post" class="m-0">
															<input type="hidden" name="type" value="actualizar">
															<input type="hidden" name="idProducto" value="${item.idProducto}">
															<input type="hidden" name="cantidad" value="${item.cantidad + 1}">
															<input type="hidden" name="q" value="${fn:escapeXml(q)}">
															<input type="hidden" class="cliente-recordado" name="idCliente" value="${idClienteSeleccionado}">
															<button type="submit" class="btn btn-sm btn-outline-secondary px-2" title="Agregar uno">+</button>
														</form>
													</div>
													<span class="fw-bold text-nowrap ms-2">S/ <fmt:formatNumber value="${item.subtotal}" type="number" minFractionDigits="2" maxFractionDigits="2"/></span>
												</div>
											</li>
										</c:forEach>
									</ul>
									<div class="card-footer bg-white border-0 d-flex justify-content-between align-items-center pt-3">
										<span class="text-uppercase text-muted fw-bold small">Total</span>
										<span class="fs-4 fw-bold text-success">S/ <fmt:formatNumber value="${totalVenta}" type="number" minFractionDigits="2" maxFractionDigits="2"/></span>
									</div>
								</c:otherwise>
							</c:choose>
						</div>

						<form action="ventas" method="post">
							<input type="hidden" name="type" value="confirmar"/>

							<div class="card shadow-sm border-primary mb-3">
								<div class="card-header card-header-blue">
									<i class="bi bi-person-badge"></i> Cliente
								</div>
								<div class="card-body">
									<input type="text" id="filtroCliente" class="form-control form-control-sm mb-2"
									       placeholder="Buscar cliente..." autocomplete="off">
									<div class="lista-clientes" id="listaClientes">
										<c:forEach items="${listaClientes}" var="c">
											<label class="cliente-opcion"
											       data-nombre="${fn:escapeXml(c.nombre)} ${fn:escapeXml(c.apellido)}">
												<input class="form-check-input m-0" type="radio" name="idCliente" value="${c.idCliente}"
												       ${c.idCliente == idClienteSeleccionado ? 'checked' : ''}>
												<i class="bi bi-person"></i>
												<span>${c.nombre} ${c.apellido}</span>
											</label>
										</c:forEach>
									</div>
									<div id="sinClientes" class="form-text text-danger d-none">Sin coincidencias.</div>
									<div id="ayudaCliente" class="form-text text-warning d-none">Selecciona un cliente para confirmar.</div>
								</div>
							</div>

							<div class="card shadow-sm">
								<div class="card-header card-header-dark d-flex justify-content-between align-items-center">
									<span><i class="bi bi-receipt-cutoff"></i> Boleta de venta</span>
									<span class="badge bg-light text-dark">Vista previa</span>
								</div>
								<div class="card-body">
									<div class="boleta">
										<div class="text-center">
											<div class="fw-bold">COMERCIAL AUTOPARTES S.A.C.</div>
											<small class="d-block text-muted">Boleta de venta</small>
											<small class="d-block text-muted">N.&deg; pendiente de asignar</small>
										</div>
										<div class="boleta-separador"></div>
										<div class="boleta-dato">
											<span>Cliente</span>
											<span id="boletaCliente">&mdash;</span>
										</div>
										<div class="boleta-dato">
											<span>Vendedor</span>
											<span>${sessionScope.usuario.username}</span>
										</div>
										<div class="boleta-dato">
											<span>Fecha</span>
											<span><fmt:formatDate value="${fechaHoy}" pattern="dd/MM/yyyy HH:mm"/></span>
										</div>
										<c:if test="${not empty carrito}">
											<div class="boleta-separador"></div>
											<ul class="boleta-items">
												<c:forEach items="${carrito}" var="item">
													<li>
														<span class="boleta-cant">${item.cantidad}&times;</span>
														<span class="boleta-nombre" title="${fn:escapeXml(item.nombre)}">${item.nombre}</span>
														<span class="boleta-importe"><fmt:formatNumber value="${item.subtotal}" type="number" minFractionDigits="2" maxFractionDigits="2"/></span>
													</li>
												</c:forEach>
											</ul>
										</c:if>
										<div class="boleta-separador"></div>
										<div class="boleta-total">
											<span>TOTAL</span>
											<span>S/ <fmt:formatNumber value="${totalVenta}" type="number" minFractionDigits="2" maxFractionDigits="2"/></span>
										</div>
										<small class="d-block text-center text-muted mt-2">Documento de control interno</small>
									</div>
								</div>
								<div class="card-footer bg-white border-0 pt-3">
									<button type="submit" id="btnConfirmar" class="btn btn-primary w-100 btn-lg"
									        data-carrito-vacio="${empty carrito}" ${empty carrito ? 'disabled' : ''}>
										<i class="bi bi-check-circle-fill"></i> Confirmar venta
									</button>
									<small class="text-muted d-block text-center mt-2">
										El stock se descuenta al confirmar.
									</small>
								</div>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
	// Lista de clientes: filtro en vivo, seleccion resaltada y boton de
	// confirmar deshabilitado hasta que haya cliente y carrito.
	(function () {
		var filtro = document.getElementById('filtroCliente');
		var lista = document.getElementById('listaClientes');
		var aviso = document.getElementById('sinClientes');
		var ayuda = document.getElementById('ayudaCliente');
		var boton = document.getElementById('btnConfirmar');
		var boletaCliente = document.getElementById('boletaCliente');
		if (!filtro || !lista) {
			return;
		}
		var filas = Array.prototype.slice.call(lista.querySelectorAll('.cliente-opcion'));
		var recordados = Array.prototype.slice.call(document.querySelectorAll('input.cliente-recordado'));
		var carritoVacio = boton ? boton.getAttribute('data-carrito-vacio') === 'true' : false;

		// Cada alta/baja del carrito recarga la pagina: el cliente elegido viaja
		// en estos campos ocultos para que el servidor lo recuerde en la sesion.
		function recordarCliente(valor) {
			recordados.forEach(function (campo) {
				campo.value = valor;
			});
		}

		function marcarActivo() {
			filas.forEach(function (fila) {
				var radio = fila.querySelector('input[name="idCliente"]');
				fila.classList.toggle('activo', !!(radio && radio.checked));
			});
			// La boleta muestra el nombre del cliente en cuanto se elige.
			if (boletaCliente) {
				var marcado = lista.querySelector('input[name="idCliente"]:checked');
				boletaCliente.textContent = marcado
					? marcado.parentElement.getAttribute('data-nombre')
					: '—';
			}
		}

		function refrescarConfirmar() {
			if (!boton) {
				return;
			}
			var hayCliente = !!lista.querySelector('input[name="idCliente"]:checked');
			boton.disabled = carritoVacio || !hayCliente;
			if (ayuda) {
				ayuda.classList.toggle('d-none', hayCliente || carritoVacio);
			}
		}

		filtro.addEventListener('input', function () {
			var texto = filtro.value.trim().toLowerCase();
			var visibles = 0;
			filas.forEach(function (fila) {
				var nombre = (fila.getAttribute('data-nombre') || '').toLowerCase();
				var coincide = texto === '' || nombre.indexOf(texto) !== -1;
				fila.classList.toggle('d-none', !coincide);
				if (coincide) {
					visibles++;
				}
			});
			if (aviso) {
				aviso.classList.toggle('d-none', visibles !== 0);
			}
		});

		lista.addEventListener('change', function (e) {
			if (e.target && e.target.name === 'idCliente') {
				marcarActivo();
				recordarCliente(e.target.value);
				refrescarConfirmar();
			}
		});

		// Enter no debe enviar el formulario de confirmacion de la venta.
		filtro.addEventListener('keydown', function (e) {
			if (e.key === 'Enter') {
				e.preventDefault();
			}
		});

		marcarActivo();
		refrescarConfirmar();
	})();
</script>
</body>
</html>
