<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>404 - Página no encontrada</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/CSS/Error.css" rel="stylesheet">
</head>
<body>
    <div class="error-card error-404">
        <i class="bi bi-question-circle-fill error-icon"></i>
        <p class="error-code">404</p>
        <h3>Página no encontrada</h3>
        <p class="error-detail">La página que buscas no existe o fue movida. Verifica la dirección e intenta de nuevo.</p>
        <div class="d-flex gap-2 justify-content-center">
            <a href="javascript:history.back()" class="btn btn-outline-secondary">
                <i class="bi bi-arrow-left"></i> Regresar
            </a>
            <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">
                <i class="bi bi-box-arrow-in-right"></i> Iniciar Sesión
            </a>
        </div>
    </div>
</body>
</html>
