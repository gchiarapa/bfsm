package br.com.bfsm.domain.cliente;

import java.math.BigDecimal;

public record DetalhesCliente(
		
		Long id, 
		
		String nome,
		
		String endereco,
		
		BigDecimal saldo,
		boolean ativo
		) {

	public DetalhesCliente(Cliente cliente) {
		this(cliente.getId(), cliente.getNome(), cliente.getEndereco(), cliente.getSaldo(), cliente.isAtivo());
	}

}
