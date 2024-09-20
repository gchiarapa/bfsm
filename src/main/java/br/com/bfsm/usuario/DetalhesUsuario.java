package br.com.bfsm.usuario;

import br.com.bfsm.model.Usuario;

public record DetalhesUsuario(
		String login
		) {

	public DetalhesUsuario(Usuario novoUsuario) {
		this(novoUsuario.getLogin());
	}

}
