<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
 Si la página se abre directamente (sin pasar por ServletInicio) cargamos
 los contadores desde la BD de forma perezosa mediante un redirect interno.
--%>
<c:if test="${totalClientes == null}">
	<jsp:forward page="ServletInicio"/>
</c:if>
<!DOCTYPE html>
<html lang="es">
<head>
	<meta charset="UTF-8">
	<title>Inicio - Sistema de Almacén</title>

    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">

	<!-- Estilos -->
    <link rel="stylesheet" href="CSS/General.css">

</head>
<body>
	<div class="d-flex">

    <!-- Sidebar -->
    <div class="sidebar p-3">
        <h4 class="text-white mb-4">Inventario</h4>

        <ul class="nav flex-column">
            <li class="nav-item"><a class="nav-link active" href="ServletInicio"><i class="bi bi-speedometer2"></i> Dashboard</a></li>
            <li class="nav-item"><a class="nav-link" href="ServletCliente?type=listar"><i class="bi bi-people"></i> Clientes</a></li>
            <li class="nav-item"><a class="nav-link" href="ServletProveedor?type=listar"><i class="bi bi-truck"></i> Proveedores</a></li>
            <li class="nav-item"><a class="nav-link" href="ServletProducto?type=listar"><i class="bi bi-box"></i> Productos</a></li>
            <li class="nav-item"><a class="nav-link" href="ServletReporte?type=listar"><i class="bi bi-graph-up-arrow"></i> Reporte</a></li>    
            <li class="nav-item"><a class="nav-link" href="ServletMovimiento?type=listar"><i class="bi bi-clock-history"></i> Movimientos</a></li>
            <li class="nav-item"><a class="nav-link" href="ServletUsuario?type=logout"><i class="bi bi-box-arrow-right"></i> Cerrar Sesión</a></li>
        </ul>
    </div>

    <!-- Contenido -->
    <div class="content flex-grow-1">

        <!-- Topbar -->
        <div class="topbar d-flex justify-content-between align-items-center px-4">
            <h5>Dashboard</h5>
            <span class="text-white">
            	<i class="bi bi-person-circle"></i>
            	${sessionScope.usuario.username} (${sessionScope.usuario.nombreTipo != null ? sessionScope.usuario.nombreTipo : 'Usuario'})
            </span>
        </div>

        <div class="container-fluid mt-4">

            <!-- Cards -->
            <div class="row g-3">
                <div class="col-md-3">
                	<a href="ServletCliente?type=listar" style="text-decoration: none;">
                    	<div class="card card-blue card-action">
                        	<i class="bi bi-people"></i>
                        	<h6>Clientes</h6>
                        	<h3>${totalClientes}</h3>
                    	</div>
                    </a>
                </div>

                <div class="col-md-3">
                	<a href="ServletProveedor?type=listar" style="text-decoration: none;">
                    	<div class="card card-orange card-action">
                        	<i class="bi bi-truck"></i>
                        	<h6>Proveedores</h6>
                        	<h3>${totalProveedores}</h3>
                    	</div>
                    </a>
                </div>

                <div class="col-md-3">
                	<a href="ServletProducto?type=listar" style="text-decoration: none;">
                    	<div class="card card-purple card-action">
                        	<i class="bi bi-box"></i>
                        	<h6>Productos</h6>
                        	<h3>${totalProductos}</h3>
                    	</div>
                    </a>
                </div>
                
                <div class="col-md-3">
    				<a href="ServletReporte?type=listar" style="text-decoration: none;">
        				<div class="card card-brown card-action">
            				<i class="bi bi-graph-up-arrow"></i>
            				<h6>Reporte</h6>
            				<h3>${totalReporte}</h3>
        				</div>
    				</a>
				</div>

                <div class="col-md-3">
    				<a href="ServletMovimiento?type=listar" style="text-decoration: none;">
        				<div class="card card-gray card-action">
            				<i class="bi bi-clock-history"></i>
            				<h6>Movimientos</h6>
            				<h3>${totalMovimientos}</h3>
        				</div>
    				</a>
				</div>
				
                <div class="col-md-3">
                	<a href="ServletProducto?type=listar" style="text-decoration: none;">
                    	<div class="card card-darkblue card-action">
                        	<i class="bi bi-box-seam"></i>
                        	<h6>Existencia total</h6>
                        	<h3>${totalStock}</h3>
                    	</div>
                    </a>
                </div>
                
			</div>

		</div>
	</div>
	</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
