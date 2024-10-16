package br.com.bfsm.domain.cliente;

import jakarta.validation.constraints.NotNull;

public record AtualizaCliente(
		
		@NotNull
		Long id,
		String nome,
		String endereco,
		String saldo,
		int ativo
		) {

}
