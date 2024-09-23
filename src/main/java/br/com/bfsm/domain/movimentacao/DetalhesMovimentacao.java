package br.com.bfsm.domain.movimentacao;

import java.time.LocalDateTime;

import br.com.bfsm.domain.cliente.Cliente;

public record DetalhesMovimentacao(
		
		Long id,
		
		String tipo,
		
		LocalDateTime data,
		
		String valor,
		
		Cliente cliente) {
	
	public DetalhesMovimentacao(Movimentacao movimentacoes) {
		this(movimentacoes.id, movimentacoes.tipo, movimentacoes.data, movimentacoes.valor, movimentacoes.cliente);
	}

}
