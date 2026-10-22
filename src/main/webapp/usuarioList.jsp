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
<link rel="stylesheet" href="CSS/modern.css">
</head>
<body>
<div class="d-flex">
    <%@ include file="menu.jsp" %>
    <div class="content flex-grow-1 p-3">
        <div class="topbar d-flex justify-content-between align-items-center px-4 mb-4">
            <h5><i class="bi bi-person-badge me-2"></i>Usuarios</h5>
            <span class="badge-role">
                <i class="bi bi-person-circle me-1"></i>
                ${sessionScope.usuario.username} · ${rolNombre}
            </span>
        </div>
        <c:if test="${not empty mensajeOk}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="bi bi-check-circle"></i> ${mensajeOk}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        <div class="card-content">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h5>Listado de Usuarios (${cantReg != null ? cantReg : 0})</h5>
            </div>
            <div class="table-responsive">
                <table class="table table-custom table-hover">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Username</th>
                            <th>Tipo</th>
                            <th>Nombres</th>
                            <th>Apellidos</th>
                            <th class="text-end">Acciones</th>
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
                                <td class="text-end">
                                    <a href="${pageContext.request.contextPath}/usuarios?type=infoEdit&id=${u.idUsuario}"
                                       class="btn btn-sm btn-outline-primary">
                                        <i class="bi bi-pencil-square"></i> Editar
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty data}">
                            <tr><td colspan="6" class="text-center text-muted">No hay usuarios registrados.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
            <div class="text-end">
                <a href="${pageContext.request.contextPath}/usuarios/nuevo" class="btn btn-primary">Registrar nuevo usuario</a>
            </div>
        </div>
    </div>
</div>

<!-- MODAL EDITAR (se abre automáticamente si hay usuarioDataEdit) -->
<c:if test="${usuarioDataEdit != null}">
    <div class="modal fade" id="modalEditarUsuario" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <form action="${pageContext.request.contextPath}/usuarios" method="post">
                    <div class="modal-header">
                        <h5 class="modal-title"><i class="bi bi-pencil"></i> Editar Usuario</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <input type="hidden" name="type" value="editar"/>
                        <input type="hidden" name="idUsuario" value="${usuarioDataEdit.idUsuario}"/>
                        <div class="mb-3">
                            <label class="form-label">Username</label>
                            <input type="text" name="txtUsername" class="form-control"
                                   value="${usuarioDataEdit.username}" minlength="6" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Clave</label>
                            <input type="password" name="txtClave" class="form-control"
                                   placeholder="Dejar en blanco para conservar la actual" minlength="6">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Tipo</label>
                            <select class="form-select" name="cboTipo">
                                <option value="1" <c:if test="${usuarioDataEdit.idTipo == 1}">selected</c:if>>Administrador</option>
                                <option value="2" <c:if test="${usuarioDataEdit.idTipo == 2}">selected</c:if>>Vendedor</option>
                                <option value="3" <c:if test="${usuarioDataEdit.idTipo == 3}">selected</c:if>>Almacenero</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Nombres</label>
                            <input type="text" name="txtNombres" class="form-control"
                                   value="${usuarioDataEdit.nombres}" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Apellidos</label>
                            <input type="text" name="txtApellidos" class="form-control"
                                   value="${usuarioDataEdit.apellidos}" required>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <a href="${pageContext.request.contextPath}/usuarios" class="btn btn-secondary">Cancelar</a>
                        <button type="submit" class="btn btn-primary"><i class="bi bi-save"></i> Guardar cambios</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <script>
        document.addEventListener('DOMContentLoaded', function () {
            var modal = new bootstrap.Modal(document.getElementById('modalEditarUsuario'));
            modal.show();
        });
    </script>
</c:if>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
