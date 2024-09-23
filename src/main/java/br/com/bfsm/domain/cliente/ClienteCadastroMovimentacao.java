package br.com.bfsm.domain.cliente;

import jakarta.validation.constraints.NotNull;

public record ClienteCadastroMovimentacao(
		
		@NotNull
		Integer id
		
		) {

}
