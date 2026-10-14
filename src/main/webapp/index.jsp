<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%--
 Página raíz: si el usuario ya inició sesión, lo redirige al dashboard;
 si no, al formulario de login.
--%>
<%
    if (session != null && session.getAttribute("usuario") != null) {
        response.sendRedirect("ServletInicio");
    } else {
        response.sendRedirect("login.jsp");
    }
%>
