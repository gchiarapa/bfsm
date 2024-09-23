package br.com.bfsm.infra.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

import br.com.bfsm.domain.usuario.Usuario;

@Service
public class TokenService {
	
	private static final Logger log = LoggerFactory.getLogger(TokenService.class);
	
	@Value("${api.token.jwt.secret}")
	private String secret;
	
	public String gerarToken(Usuario usuario) {
		
		String token = "";
		
		try {
			var algoritmo = Algorithm.HMAC256(secret);

		    token = JWT.create()
		        .withIssuer("bfsm")
		        .withSubject(usuario.getLogin())
		        .withExpiresAt(dataExpiracao())
		        .sign(algoritmo);
		    log.info("Token gerado com sucesso ");
		} catch (JWTCreationException exception){
		    log.error("Erro para gerar token " + exception.getMessage());
		}
		return token;
	}

	private Instant dataExpiracao() {
		
		return LocalDateTime.now().plusMinutes(30).toInstant(ZoneOffset.of("-03:00"));
	}

}
