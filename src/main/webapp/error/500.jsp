<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>500 - Error del servidor</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/CSS/Error.css" rel="stylesheet">
</head>
<body>
    <div class="error-card error-500">
        <i class="bi bi-exclamation-triangle-fill error-icon"></i>
        <p class="error-code">500</p>
        <h3>Error del servidor</h3>
        <p class="error-detail">Ocurrió un problema inesperado al procesar tu solicitud. Intenta nuevamente en unos minutos.</p>
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
