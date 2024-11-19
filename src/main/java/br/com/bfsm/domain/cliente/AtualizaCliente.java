package br.com.bfsm.domain.cliente;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

public record AtualizaCliente(
		
		@NotNull
		Long id,
		String nome,
		String endereco,
		BigDecimal saldo,
		int ativo
		) {

}
