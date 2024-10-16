package br.com.bfsm.infra.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.bfsm.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private UsuarioRepository usuarioRepo;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		var token = recuperarToken(request);
		
		if(token != null) {
			var subject = tokenService.getSubject(token);
			var usuario = usuarioRepo.findByLogin(subject);
			
			Authentication authorization = new UsernamePasswordAuthenticationToken(usuario.get(), null, usuario.get().getAuthorities());
			
			SecurityContextHolder.getContext().setAuthentication(authorization);
		}
		
		filterChain.doFilter(request, response);
		
	}

	private String recuperarToken(HttpServletRequest request) {
		
		var authHeader = request.getHeader("Authorization");
		
		if(authHeader != null) {
			return authHeader.replace("Bearer ", "");
		}
		
		return null;
	}
	
	

}
