package br.com.bfsm.domain.usuario;

public record DetalhesUsuario(
		String login
		) {

	public DetalhesUsuario(Usuario novoUsuario) {
		this(novoUsuario.getLogin());
	}

}
