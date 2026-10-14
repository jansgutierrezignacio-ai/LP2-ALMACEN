<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Reportes</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="CSS/General.css">

    <style>
        .nav .card { min-width: 180px; cursor: pointer; border: none; }
        .nav-link.active { transform: scale(1.05); box-shadow: 0 4px 10px rgba(0,0,0,0.2); }
        .nav-link { color: white; }
        .tab-content { padding: 1.5rem; background: #fff; border-radius: 8px; margin-top: 1rem; }
        
        /* Ajuste de color según tu imagen (Verde Esmeralda) */
        .topbar { 
            background: #1abc9c; /* Color verde de la captura */
            color: white; 
            padding: 15px 25px; 
            border-radius: 4px;
        }
        
        .sidebar { min-height: 100vh; width: 250px; background: #212529; }
        .card-gray { background-color: #6c757d; color: white; }
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
            <li class="nav-item"><a class="nav-link" href="ServletProducto?type=listar"><i class="bi bi-box"></i> Productos</a></li>
            <li class="nav-item"><a class="nav-link active" href="ServletReporte?type=listar"><i class="bi bi-graph-up-arrow"></i> Reportes</a></li>
            <li class="nav-item"><a class="nav-link" href="ServletMovimiento?type=listar"><i class="bi bi-clock-history"></i> Movimientos</a></li>
            <li class="nav-item"><a class="nav-link" href="ServletUsuario?type=logout"><i class="bi bi-box-arrow-right"></i> Cerrar Sesión</a></li>
        </ul>
    </div>

    <div class="content flex-grow-1 p-3">
        
        <div class="topbar d-flex justify-content-between align-items-center mb-4">
            <h5 class="m-0">Reporte</h5>
            <span class="text-white">Usuario: ${sessionScope.usuario.username}</span>
        </div>

        <div class="nav nav-pills d-flex flex-nowrap overflow-auto gap-3 p-2" role="tablist">
            <div class="nav-link active card card-gray text-center p-3" data-bs-toggle="pill" data-bs-target="#fechas">
                <i class="bi bi-calendar-range fs-3"></i><div>Ventas por Fechas</div>
            </div>
            <div class="nav-link card card-gray text-center p-3" data-bs-toggle="pill" data-bs-target="#detalle">
                <i class="bi bi-receipt fs-3"></i><div>Detalle de Venta</div>
            </div>
        </div>

        <div class="tab-content shadow-sm border">
            
            <div class="tab-pane fade show active" id="fechas">
                <h6 class="text-muted mb-3 uppercase">Filtrar por rango de tiempo</h6>
                
                <form action="ServletReporte" method="get" class="row g-3 mb-4">
                    <input type="hidden" name="type" value="listar">
                    <div class="col-md-4">
                        <input type="date" name="txtFechaInicio" class="form-control" value="${fInicio}" required>
                    </div>
                    <div class="col-md-4">
                        <input type="date" name="txtFechaFin" class="form-control" value="${fFin}" required>
                    </div>
                    <div class="col-md-4">
                        <button type="submit" class="btn btn-dark w-100"><i class="bi bi-search"></i> Consultar</button>
                    </div>
                </form>

                <table class="table table-hover align-middle">
                    <thead class="table-light">
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
                                    <a href="ServletReporte?type=detalle&id=${r.idVenta}" class="btn btn-sm btn-outline-primary">
                                        <i class="bi bi-zoom-in"></i> Ver Detalle
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty dataReporte}">
                            <tr><td colspan="5" class="text-center py-4 text-muted">No se encontraron registros.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <div class="tab-pane fade" id="detalle">
                <c:choose>
                    <c:when test="${detalleVenta != null}">
                        <h5 class="mb-4">Artículos de la Venta: # ${nroVenta}</h5>
                        <table class="table table-bordered">
                            <thead class="table-light">
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
                        <div class="mt-3">
                            <button class="btn btn-secondary btn-sm" onclick="location.href='ServletReporte?type=listar'"><i class="bi bi-arrow-left"></i> Volver a la lista</button>
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