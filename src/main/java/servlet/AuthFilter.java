package servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
@WebFilter("/*")

public class AuthFilter implements Filter {

	private static final List<String> RUTAS_PUBLICAS = Arrays.asList(
			"/login.jsp",
			"/errorLogin.jsp",
			"/ServletUsuario"
	);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException { }

	@Override
	public void destroy() { }

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		String ctx = request.getContextPath();
		String uri = request.getRequestURI();
		String path = uri.substring(ctx.length());

		boolean esRecursoEstatico = path.startsWith("/CSS/")
				|| path.startsWith("/JS/")
				|| path.startsWith("/img/")
				|| path.startsWith("/IMG/")
				|| path.startsWith("/WEB-INF/");

		boolean esPublica = path.equals("/") || path.isEmpty() || esRecursoEstatico
				|| RUTAS_PUBLICAS.contains(path);

		if (esPublica) {
			chain.doFilter(req, resp);
			return;
		}

		HttpSession session = request.getSession(false);
		boolean autenticado = session != null && session.getAttribute("usuario") != null;

		if (autenticado) {
			chain.doFilter(req, resp);
		} else {
			response.sendRedirect(ctx + "/login.jsp");
		}
	}
}
