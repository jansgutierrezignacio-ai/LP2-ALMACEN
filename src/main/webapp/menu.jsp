<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--
    Menú lateral reutilizable. Se incluye en las vistas del sistema con
    <%@ include file="menu.jsp" %> y muestra únicamente los módulos que el rol
    del usuario en sesión (idTipo: 1=Administrador, 2=Vendedor, 3=Almacenero)
    tiene permitidos. El módulo activo se deduce de la URI solicitada.
--%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<%--
    Spring MVC resuelve la vista con un forward interno al JSP, así que dentro
    de la vista `pageContext.request.requestURI` ya apunta al propio .jsp
    (p. ej. "/Inicio.jsp") y no a la URL pedida. Para marcar la sección activa
    se usa la URI original que guarda el forward; si no existe (acceso directo
    al JSP), se cae al requestURI normal. Se pasa a minúsculas para comparar
    sin depender de mayúsculas.
--%>
<c:set var="uri" value="${requestScope['javax.servlet.forward.request_uri']}" />
<c:if test="${empty uri}">
    <c:set var="uri" value="${pageContext.request.requestURI}" />
</c:if>
<c:set var="uri" value="${fn:toLowerCase(uri)}" />
<c:set var="rol" value="${sessionScope.usuario.idTipo}" />
<c:set var="esAdmin" value="${rol == 1}" />
<c:set var="esVendedor" value="${rol == 2}" />
<c:set var="esAlmacenero" value="${rol == 3}" />
<%-- Nombre del rol para la insignia de la barra superior de cada vista. --%>
<c:set var="rolNombre" value="${rol == 1 ? 'Administrador' : (rol == 2 ? 'Vendedor' : 'Almacenero')}" />

<div class="sidebar p-3 d-flex flex-column">
    <div class="brand-title fs-4 mb-4 text-center">
        <i class="bi bi-box-seam"></i> Inventario
    </div>
    <ul class="nav flex-column mb-auto">
        <li class="nav-item"><a class="nav-link ${fn:contains(uri, '/inicio') ? 'active' : ''}" href="${ctx}/inicio"><i class="bi bi-speedometer2"></i> Dashboard</a></li>

        <c:if test="${esAdmin || esVendedor}">
            <li class="nav-item"><a class="nav-link ${fn:contains(uri, '/clientes') ? 'active' : ''}" href="${ctx}/clientes?type=listar"><i class="bi bi-people"></i> Clientes</a></li>
        </c:if>

        <c:if test="${esAdmin || esAlmacenero}">
            <li class="nav-item"><a class="nav-link ${fn:contains(uri, '/proveedores') ? 'active' : ''}" href="${ctx}/proveedores?type=listar"><i class="bi bi-truck"></i> Proveedores</a></li>
        </c:if>

        <li class="nav-item"><a class="nav-link ${fn:contains(uri, '/productos') ? 'active' : ''}" href="${ctx}/productos?type=listar"><i class="bi bi-box"></i> Productos</a></li>

        <c:if test="${esAdmin || esVendedor}">
            <li class="nav-item"><a class="nav-link ${fn:contains(uri, '/ventas') ? 'active' : ''}" href="${ctx}/ventas"><i class="bi bi-cart-check"></i> Ventas</a></li>
        </c:if>

        <c:if test="${esAdmin || esVendedor}">
            <li class="nav-item"><a class="nav-link ${fn:contains(uri, '/reportes') ? 'active' : ''}" href="${ctx}/reportes?type=listar"><i class="bi bi-graph-up-arrow"></i> Reporte</a></li>
        </c:if>

        <c:if test="${esAdmin || esAlmacenero}">
            <li class="nav-item"><a class="nav-link ${fn:contains(uri, '/movimientos') ? 'active' : ''}" href="${ctx}/movimientos?type=listar"><i class="bi bi-clock-history"></i> Movimientos</a></li>
        </c:if>

        <c:if test="${esAdmin}">
            <li class="nav-item"><a class="nav-link ${fn:contains(uri, '/usuarios') ? 'active' : ''}" href="${ctx}/usuarios"><i class="bi bi-person-badge"></i> Usuarios</a></li>
        </c:if>

        <li class="nav-item mt-2 pt-3 border-top border-secondary">
            <form action="${ctx}/logout" method="post" class="m-0">
                <button type="submit" class="nav-link w-100 text-start border-0 bg-transparent"><i class="bi bi-box-arrow-right"></i> Cerrar Sesión</button>
            </form>
        </li>
    </ul>
</div>
