package br.com.bfsm.domain.cliente;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CadastroCliente(
		
		@NotNull
		String nome,
		
		@NotNull
		String endereco,
		
		@NotNull
		BigDecimal saldo,
		
		@NotEmpty
	    int ativo
		) {

}
