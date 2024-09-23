package br.com.bfsm.domain.usuario;

import jakarta.validation.constraints.NotNull;

public record UsuarioCadastro(
		
		@NotNull
		String login,
		
		@NotNull
		String senha
		) {

}
