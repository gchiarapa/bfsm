package br.com.bfsm.domain.usuario;

import jakarta.validation.constraints.NotNull;

public record AtualizaUsuario(
		
		@NotNull
		Long id,
		
		String login,
		
		String senha,
		int ativo
		) {

}
