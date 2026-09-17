package pt.seixal.carlos.security.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtTokenFilter extends GenericFilterBean {

	@Autowired
	private JwtTokenProvider tokenProvider;

	public JwtTokenFilter(JwtTokenProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filter)
			throws IOException, ServletException {

		var token = tokenProvider.resolveToken((HttpServletRequest) request);

		try {
			if (org.apache.commons.lang3.StringUtils.isNotBlank(token) && tokenProvider.validateToken(token)) {
				Authentication authentication = tokenProvider.getAuthentication(token);
				if (authentication != null) {
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			}
			filter.doFilter(request, response);

		} catch (com.auth0.jwt.exceptions.TokenExpiredException e) {
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			httpResponse.setContentType("application/json;charset=UTF-8");
			httpResponse.getWriter().write("{\"message\": \"O token JWT expirou. Por favor, faça login novamente.\"}");

		} catch (Exception e) {
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
			httpResponse.setContentType("application/json;charset=UTF-8");
			httpResponse.getWriter().write("{\"message\": \"Token inválido ou sem autorização.\"}");
		}
	}

}
