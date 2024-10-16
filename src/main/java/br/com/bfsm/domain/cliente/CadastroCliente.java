package br.com.bfsm.domain.cliente;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CadastroCliente(
		
		@NotNull
		String nome,
		
		@NotNull
		String endereco,
		
		@NotNull
		String saldo,
		
		@NotEmpty
	    int ativo
		) {

}
