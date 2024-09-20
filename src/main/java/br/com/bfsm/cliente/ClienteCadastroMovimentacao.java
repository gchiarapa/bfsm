package br.com.bfsm.cliente;

import jakarta.validation.constraints.NotNull;

public record ClienteCadastroMovimentacao(
		
		@NotNull
		Integer id
		
		) {

}
