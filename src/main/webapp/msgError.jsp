<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<title>Mensaje del Sistema</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
<style>
	body {
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		min-height: 100vh;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	.msg-card {
		background: #fff;
		padding: 2.5rem;
		border-radius: 15px;
		box-shadow: 0 10px 25px rgba(0,0,0,0.2);
		max-width: 480px;
		width: 100%;
		text-align: center;
	}
	.msg-card .icon {
		font-size: 3.5rem;
		color: #f0ad4e;
		margin-bottom: 1rem;
	}
</style>
</head>
<body>
	<div class="msg-card">
		<i class="bi bi-info-circle-fill icon"></i>
		<h3 class="mb-3">${requestScope.mensaje != null ? requestScope.mensaje : sessionScope.mensaje}</h3>
		<p class="text-muted mb-4">Si el problema persiste, consulte con el administrador del sistema.</p>
		<div class="d-flex gap-2 justify-content-center">
			<a href="javascript:history.back()" class="btn btn-outline-secondary">
				<i class="bi bi-arrow-left"></i> Regresar
			</a>
			<a href="Inicio.jsp" class="btn btn-primary">
				<i class="bi bi-house"></i> Ir al Inicio
			</a>
		</div>
	</div>
</body>
</html>
