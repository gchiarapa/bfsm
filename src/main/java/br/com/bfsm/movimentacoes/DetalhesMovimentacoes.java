package br.com.bfsm.movimentacoes;

import java.time.LocalDateTime;

import br.com.bfsm.model.Cliente;
import br.com.bfsm.model.Movimentacoes;
import jakarta.validation.constraints.NotNull;

public record DetalhesMovimentacoes(
		
		Long id,
		
		String tipo,
		
		LocalDateTime data,
		
		String valor,
		
		Cliente cliente) {
	
	public DetalhesMovimentacoes(Movimentacoes movimentacoes) {
		this(movimentacoes.id, movimentacoes.tipo, movimentacoes.data, movimentacoes.valor, movimentacoes.cliente);
	}

}
