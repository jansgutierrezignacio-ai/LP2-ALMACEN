<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error - Sistema de Almacén</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/CSS/Error.css" rel="stylesheet">
</head>
<body>
    <div class="error-card error-generico">
        <i class="bi bi-exclamation-circle-fill error-icon"></i>
        <p class="error-code">${status != null ? status : 'Error'}</p>
        <h3>Ha ocurrido un error</h3>
        <p class="error-detail">Se presentó un inconveniente al procesar la solicitud. Si el problema persiste, contacta al administrador.</p>
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
