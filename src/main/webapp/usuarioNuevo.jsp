<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Gestión de Usuario - Registro</title>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">

<style>
    body {
        background-color: #f8f9fa;
        padding-top: 50px;
    }
    .cdr-formulario {
        background: white;
        padding: 30px;
        border-radius: 10px;
        box-shadow: 0px 0px 15px rgba(0,0,0,0.1);
    }
    label.error {
        color: #dc3545;
        font-size: 0.85rem;
        margin-top: 5px;
        display: block;
    }
    input.error {
        border: 1px solid #dc3545;
    }
</style>

<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.4.0/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-validate/1.19.0/jquery.validate.min.js"></script>

<script type="text/javascript">
$(document).ready(function() {
    $("form[name='frmNuevo']").validate({
        rules: {
            txtUsername: { required: true, minlength: 6 },
            txtClave: { required: true, minlength: 6 },
            txtNombres: { required: true },
            txtApellidos: { required: true }
        },
        messages: {
            txtUsername: {
                required: "El username es obligatorio",
                minlength: "Debe tener al menos 6 caracteres"
            },
            txtClave: {
                required: "La clave es obligatoria",
                minlength: "La clave debe tener al menos 6 caracteres"
            },
            txtNombres: {
                required: "Ingrese los nombres"
            },
            txtApellidos: {
                required: "Ingrese los apellidos"
            }
        },
        errorElement: "label",
        highlight: function(element) {
            $(element).addClass("error");
        },
        unhighlight: function(element) {
            $(element).removeClass("error");
        }
    });
});
</script>

</head>
<body>

<div class="container">
    <div class="row justify-content-center">
        <div class="col-lg-6 col-md-8 cdr-formulario">
            <h4 class="text-center">Formulario de Registro de Usuario</h4>
            <hr>
            
            <form action="ServletUsuario" method="post" name="frmNuevo">
                <input type="hidden" name="type" value="registrar">
                
                <div class="mb-3 row">
                    <label class="col-sm-3 col-form-label">Username</label>
                    <div class="col-sm-9">
                        <input type="text" class="form-control" name="txtUsername" id="txtUsername">
                    </div>
                </div>
            
                <div class="mb-3 row">
                    <label class="col-sm-3 col-form-label">Clave</label>
                    <div class="col-sm-9">
                        <input type="password" class="form-control" name="txtClave" id="txtClave">
                    </div>
                </div>
                            
                <div class="mb-3 row">
                    <label class="col-sm-3 col-form-label">Tipo</label>
                    <div class="col-sm-9">
                        <select class="form-select" name="cboTipo">
                            <option value="1">Administrador</option>
                            <option value="2">Vendedor</option>
                            <option value="3">Almacenero</option>
                        </select>
                    </div>
                </div>
                
                <div class="mb-3 row">
                    <label class="col-sm-3 col-form-label">Nombres</label>
                    <div class="col-sm-9">
                        <input type="text" class="form-control" name="txtNombres">
                    </div>
                </div>
                
                <div class="mb-4 row">
                    <label class="col-sm-3 col-form-label">Apellidos</label>
                    <div class="col-sm-9">
                        <input type="text" class="form-control" name="txtApellidos">
                    </div>
                </div>
                
                <hr>
                
                <div class="d-flex justify-content-between">
                    <a href="login.jsp" class="btn btn-secondary">Regresar</a>
                    <button type="submit" class="btn btn-success">Registrar Usuario</button>
                </div>
            </form>
        </div>
    </div>
</div>

</body>
</html>