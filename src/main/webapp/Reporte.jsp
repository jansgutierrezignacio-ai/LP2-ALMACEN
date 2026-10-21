<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Reportes</title>
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
            <h5 class="m-0"><i class="bi bi-graph-up-arrow me-2"></i>Reportes</h5>
            <span class="badge-role">
            	<i class="bi bi-person-circle me-1"></i>
            	${sessionScope.usuario.username} · ${rolNombre}
            </span>
        </div>

        <div class="nav nav-pills nav-pills-custom d-flex flex-nowrap overflow-auto gap-3 p-2" role="tablist">
            <div class="nav-link active text-center p-3" data-bs-toggle="pill" data-bs-target="#fechas">
                <i class="bi bi-calendar-range fs-3"></i><div>Ventas por Fechas</div>
            </div>
            <div class="nav-link text-center p-3" data-bs-toggle="pill" data-bs-target="#detalle">
                <i class="bi bi-receipt fs-3"></i><div>Detalle de Venta</div>
            </div>
        </div>

        <div class="tab-content">
            
            <div class="tab-pane fade show active" id="fechas">
                <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
                    <h5 class="m-0">
                        <i class="bi bi-list-ul me-2"></i>Ventas registradas
                        <span class="badge bg-secondary ms-2">${totalRegistros}</span>
                    </h5>
                    <c:if test="${conFiltro}">
                        <span class="text-muted small">
                            <i class="bi bi-funnel"></i> Filtrado: ${fInicio} a ${fFin}
                        </span>
                    </c:if>
                </div>

                <p class="text-muted small mb-3">
                    Se muestran todas las ventas. Puedes filtrar por un rango de fechas si lo necesitas.
                    El PDF admite hasta <fmt:formatNumber value="${maxPdf}" /> ventas por descarga.
                </p>

                <c:if test="${not empty avisoLimite}">
                    <div class="alert alert-warning d-flex align-items-start gap-2" role="alert">
                        <i class="bi bi-exclamation-triangle-fill mt-1"></i>
                        <div>
                            El reporte tiene <strong><fmt:formatNumber value="${avisoLimite}" /></strong> ventas
                            y el PDF admite hasta <strong><fmt:formatNumber value="${maxPdf}" /></strong>.
                            Filtra por un rango de fechas para reducir la descarga.
                        </div>
                    </div>
                </c:if>

                <form action="reportes" method="get" class="row g-3 mb-4">
                    <input type="hidden" name="type" value="listar">
                    <div class="col-md-3">
                        <label class="form-label small text-muted mb-1">Desde (opcional)</label>
                        <input type="date" name="txtFechaInicio" class="form-control" value="${fInicio}">
                    </div>
                    <div class="col-md-3">
                        <label class="form-label small text-muted mb-1">Hasta (opcional)</label>
                        <input type="date" name="txtFechaFin" class="form-control" value="${fFin}">
                    </div>
                    <div class="col-md-2 d-flex align-items-end">
                        <button type="submit" class="btn btn-dark w-100"><i class="bi bi-search"></i> Consultar</button>
                    </div>
                    <div class="col-md-2 d-flex align-items-end">
                        <a href="reportes?type=listar" class="btn btn-outline-secondary w-100"><i class="bi bi-arrow-clockwise"></i> Ver todas</a>
                    </div>
                    <div class="col-md-2 d-flex align-items-end">
                        <c:choose>
                            <c:when test="${totalRegistros > maxPdf}">
                                <span class="btn btn-outline-danger w-100 disabled" aria-disabled="true"
                                      title="El PDF admite hasta ${maxPdf} ventas. Filtra por fechas.">
                                    <i class="bi bi-file-earmark-pdf"></i> PDF
                                </span>
                            </c:when>
                            <c:otherwise>
                                <a href="reportes/pdf?txtFechaInicio=${fInicio}&amp;txtFechaFin=${fFin}" class="btn btn-danger w-100">
                                    <i class="bi bi-file-earmark-pdf-fill"></i> PDF
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </form>

                <table class="table table-custom table-hover align-middle">
                    <thead>
                        <tr>
                            <th>ID Venta</th>
                            <th>Fecha</th>
                            <th>Cliente</th>
                            <th>Monto Total</th>
                            <th class="text-center">Acción</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="r" items="${dataReporte}">
                            <tr>
                                <td># ${r.idVenta}</td>
                                <td>${r.fecha}</td>
                                <td>${r.nombreCliente}</td>
                                <td class="fw-bold text-success">S/ <fmt:formatNumber value="${r.total}" minFractionDigits="2"/></td>
                                <td class="text-center">
                                    <a href="reportes?type=detalle&amp;id=${r.idVenta}" class="btn btn-sm btn-outline-primary">
                                        <i class="bi bi-zoom-in"></i> Ver Detalle
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty dataReporte}">
                            <tr>
                                <td colspan="5" class="text-center py-4 text-muted">
                                    <c:choose>
                                        <c:when test="${conFiltro}">No se encontraron ventas en ese rango de fechas.</c:when>
                                        <c:otherwise>No hay ventas registradas.</c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>

                <c:if test="${totalPaginas > 1}">
                    <nav class="d-flex justify-content-between align-items-center flex-wrap gap-2 mt-3" aria-label="Paginación de ventas">
                        <small class="text-muted">
                            Página ${paginaActual} de ${totalPaginas} · ${totalRegistros} ventas
                        </small>
                        <ul class="pagination pagination-sm m-0">
                            <li class="page-item ${paginaActual <= 1 ? 'disabled' : ''}">
                                <a class="page-link" href="reportes?type=listar&amp;pagina=${paginaActual - 1}&amp;txtFechaInicio=${fInicio}&amp;txtFechaFin=${fFin}">
                                    <i class="bi bi-chevron-left"></i> Anterior
                                </a>
                            </li>
                            <c:forEach var="p" begin="1" end="${totalPaginas}">
                                <li class="page-item ${p == paginaActual ? 'active' : ''}">
                                    <a class="page-link" href="reportes?type=listar&amp;pagina=${p}&amp;txtFechaInicio=${fInicio}&amp;txtFechaFin=${fFin}">${p}</a>
                                </li>
                            </c:forEach>
                            <li class="page-item ${paginaActual >= totalPaginas ? 'disabled' : ''}">
                                <a class="page-link" href="reportes?type=listar&amp;pagina=${paginaActual + 1}&amp;txtFechaInicio=${fInicio}&amp;txtFechaFin=${fFin}">
                                    Siguiente <i class="bi bi-chevron-right"></i>
                                </a>
                            </li>
                        </ul>
                    </nav>
                </c:if>
            </div>

            <div class="tab-pane fade" id="detalle">
                <c:choose>
                    <c:when test="${detalleVenta != null}">
                        <h5 class="mb-4">Artículos de la Venta: # ${nroVenta}</h5>
                        <table class="table table-custom table-hover">
                            <thead>
                                <tr>
                                    <th>Producto</th>
                                    <th class="text-center">Cantidad</th>
                                    <th class="text-end">Precio Unit.</th>
                                    <th class="text-end">Total</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="d" items="${detalleVenta}">
                                    <tr>
                                        <td>${d.nombreProducto}</td>
                                        <td class="text-center">${d.cantidad}</td>
                                        <td class="text-end">S/ <fmt:formatNumber value="${d.precioUnit}" minFractionDigits="2"/></td>
                                        <td class="text-end fw-bold text-primary">S/ <fmt:formatNumber value="${d.total}" minFractionDigits="2"/></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                        <div class="mt-3 d-flex gap-2">
                            <button class="btn btn-secondary btn-sm" onclick="location.href='reportes?type=listar'"><i class="bi bi-arrow-left"></i> Volver a la lista</button>
                            <a href="reportes/pdf/detalle?id=${nroVenta}" class="btn btn-danger btn-sm"><i class="bi bi-file-earmark-pdf-fill"></i> Descargar PDF</a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center p-5">
                            <i class="bi bi-receipt-cutoff fs-1 text-muted"></i>
                            <p class="mt-2 text-muted">Debe seleccionar una venta de la lista para ver su detalle.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<c:if test="${detalleVenta != null}">
    <script>
        document.addEventListener('DOMContentLoaded', function () {
            var triggerEl = document.querySelector('[data-bs-target="#detalle"]');
            var tab = new bootstrap.Tab(triggerEl);
            tab.show();
        });
    </script>
</c:if>

</body>
</html>