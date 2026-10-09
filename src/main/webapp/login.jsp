<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Sistema de Almacén</title>
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.5.0/font/bootstrap-icons.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600&display=swap" rel="stylesheet">
    
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            font-family: 'Poppins', sans-serif;
            height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0;
        }
        .login-card {
            background: #ffffff;
            padding: 2.5rem;
            border-radius: 15px;
            box-shadow: 0 15px 35px rgba(0,0,0,0.3);
            width: 100%;
            max-width: 420px;
        }
        .login-card h3 {
            font-weight: 600;
            color: #333;
            margin-bottom: 0.5rem;
            text-align: center;
        }
        .form-control {
            border-radius: 8px;
            padding: 0.75rem;
            border: 1px solid #ced4da;
        }
        .btn-primary {
            background: #764ba2;
            border: none;
            border-radius: 8px;
            padding: 0.75rem;
            font-weight: 600;
            transition: all 0.3s ease;
        }
        .btn-primary:hover {
            background: #5a3782;
            transform: translateY(-1px);
        }
        .btn-outline-secondary {
            border-radius: 8px;
            padding: 0.75rem;
            font-weight: 600;
        }
        /* Estilos de validación JQuery */
        label.error {
            color: #dc3545;
            font-size: 0.85rem;
            margin-top: 5px;
            display: block;
        }
        input.error {
            border-color: #dc3545 !important;
        }
        .link-productos {
            text-decoration: none;
            color: #764ba2;
            font-size: 0.9rem;
            transition: 0.3s;
        }
        .link-productos:hover {
            text-decoration: underline;
        }
    </style>

    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.4.0/jquery.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-validate/1.19.0/jquery.validate.min.js"></script>

    <script type="text/javascript">
        $(document).ready(function() {
            $("form[name='frmLogin']").validate({
                rules: {
                    txtUsername: { required: true, minlength: 4 },
                    txtClave: { required: true, minlength: 6 }
                },
                messages: {			
                    txtUsername: {
                        required: "Ingresa tu usuario",
                        minlength: "Mínimo 4 caracteres"
                    },				
                    txtClave: {
                        required: "Ingresa tu contraseña",
                        minlength: "Mínimo 6 caracteres"
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

    <div class="login-card">
        <h3>Bienvenido</h3>
        <p class="text-center text-muted mb-4">Inicia sesión en el Almacén</p>
        
        <form action="ServletUsuario" method="post" name="frmLogin">
            <input type="hidden" name="type" value="login"/>
            
            <div class="mb-3">
                <label class="form-label">Usuario</label>
                <div class="input-group">
                    <input class="form-control" type="text" name="txtUsername" placeholder="Nombre de usuario">
                </div>
            </div>
            
            <div class="mb-4">
                <label class="form-label">Contraseña</label>
                <input class="form-control" type="password" name="txtClave" placeholder="********">
            </div>
            
            <div class="d-grid gap-2">
                <button type="submit" class="btn btn-primary">
                    <i class="bi bi-box-arrow-in-right"></i> Iniciar Sesión
                </button>
            </div>

            <div class="text-center mt-3">
            <a href="ServletUsuario?type=nuevo" class="btn btn-outline-secondary" type="nuevo">Registrarse</a>
            </div>
            
            <div class="mt-4 text-center border-top pt-3">
                <small class="text-muted">Sistema de grupo>
            </div>
        </form>
    </div>

</body>
</html>