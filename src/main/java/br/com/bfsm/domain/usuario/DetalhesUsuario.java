package br.com.bfsm.domain.usuario;

import org.springframework.http.ResponseEntity;

public record DetalhesUsuario(
		String login,
		
		Long id,
		boolean ativo
		) {

	public DetalhesUsuario(Usuario novoUsuario) {
		this(novoUsuario.getLogin(), novoUsuario.id, novoUsuario.ativo);
	}

	public DetalhesUsuario(ResponseEntity<DetalhesUsuario> cadastrarUsuario) {
		this(cadastrarUsuario.getBody().login, cadastrarUsuario.getBody().id, cadastrarUsuario.getBody().ativo);
	}
	
	

}
