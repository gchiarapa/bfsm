package br.com.bfsm.movimentacao;

import java.time.LocalDateTime;

import br.com.bfsm.model.Cliente;
import br.com.bfsm.model.Movimentacoes;

public record DetalhesMovimentacao(
		
		String tipo,
		
		LocalDateTime data,
		
		String valor,
		
		Cliente cliente
		) {

	public DetalhesMovimentacao(Movimentacoes movimentacao) {
		this(movimentacao.tipo, movimentacao.data, movimentacao.valor, movimentacao.cliente);
	}

}
