<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<title>Listado de Usuarios</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
<link rel="stylesheet" href="CSS/General.css">
</head>
<body>
<div class="d-flex">
    <div class="sidebar p-3">
        <h4 class="text-white mb-4">Inventario</h4>
        <ul class="nav flex-column">
            <li class="nav-item"><a class="nav-link" href="inicio"><i class="bi bi-speedometer2"></i> Dashboard</a></li>
            <li class="nav-item"><a class="nav-link" href="clientes?type=listar"><i class="bi bi-people"></i> Clientes</a></li>
            <li class="nav-item"><a class="nav-link" href="proveedores?type=listar"><i class="bi bi-truck"></i> Proveedores</a></li>
            <li class="nav-item"><a class="nav-link" href="productos?type=listar"><i class="bi bi-box"></i> Productos</a></li>
            <li class="nav-item"><a class="nav-link" href="reportes?type=listar"><i class="bi bi-graph-up-arrow"></i> Reportes</a></li>
            <li class="nav-item"><a class="nav-link" href="movimientos?type=listar"><i class="bi bi-clock-history"></i> Movimientos</a></li>
            <li class="nav-item"><a class="nav-link" href="logout"><i class="bi bi-box-arrow-right"></i> Cerrar Sesión</a></li>
        </ul>
    </div>
    <div class="content flex-grow-1 p-3">
        <div class="topbar d-flex justify-content-between align-items-center px-4 mb-3">
            <h5>Usuarios</h5>
            <span class="text-white">Usuario: ${sessionScope.usuario.username}</span>
        </div>
        <div class="card p-4 shadow-sm">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h5>Listado de Usuarios (${cantReg != null ? cantReg : 0})</h5>
            </div>
            <div class="table-responsive">
                <table class="table table-striped table-hover">
                    <thead class="table-dark">
                        <tr>
                            <th>ID</th>
                            <th>Username</th>
                            <th>Tipo</th>
                            <th>Nombres</th>
                            <th>Apellidos</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="u" items="${data}">
                            <tr>
                                <td>${u.idUsuario}</td>
                                <td>${u.username}</td>
                                <td>${u.nombreTipo != null ? u.nombreTipo : u.idTipo}</td>
                                <td>${u.nombres}</td>
                                <td>${u.apellidos}</td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty data}">
                            <tr><td colspan="5" class="text-center text-muted">No hay usuarios registrados.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
            <div class="text-end">
                <a href="usuarios/nuevo" class="btn btn-primary">Registrar nuevo usuario</a>
            </div>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
