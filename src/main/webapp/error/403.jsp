<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>403 - Acceso denegado</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/CSS/Error.css" rel="stylesheet">
</head>
<body>
    <div class="error-card error-403">
        <i class="bi bi-shield-lock-fill error-icon"></i>
        <p class="error-code">403</p>
        <h3>Acceso denegado</h3>
        <p class="error-detail">No tienes permisos para acceder a este módulo. Ingresa con una cuenta autorizada.</p>
        <div class="d-flex gap-2 justify-content-center">
            <a href="javascript:history.back()" class="btn btn-outline-secondary">
                <i class="bi bi-arrow-left"></i> Regresar
            </a>
            <a href="${pageContext.request.contextPath}/inicio" class="btn btn-primary">
                <i class="bi bi-house"></i> Ir al Inicio
            </a>
        </div>
    </div>
</body>
</html>
