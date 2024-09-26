package br.com.bfsm.domain.usuario;

import org.springframework.http.ResponseEntity;

public record DetalhesUsuario(
		String login,
		
		Long id
		) {

	public DetalhesUsuario(Usuario novoUsuario) {
		this(novoUsuario.getLogin(), novoUsuario.id);
	}

	public DetalhesUsuario(ResponseEntity<DetalhesUsuario> cadastrarUsuario) {
		this(cadastrarUsuario.getBody().login, cadastrarUsuario.getBody().id);
	}
	
	

}
