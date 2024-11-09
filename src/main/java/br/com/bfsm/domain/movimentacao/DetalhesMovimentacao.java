package br.com.bfsm.domain.movimentacao;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import br.com.bfsm.domain.cambio.TaxaCambio;
import br.com.bfsm.domain.cliente.Cliente;

public record DetalhesMovimentacao(
		
		Long id,
		
		String tipo,
		
		@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
		LocalDateTime data,
		
		String valor,
		
		Cliente cliente,
		Moeda moeda,
		Categoria categoria,
		TaxaCambio cambio
		) {
	
	public DetalhesMovimentacao(Movimentacoes movimentacoes) {
		this(movimentacoes.id, movimentacoes.tipo, movimentacoes.data, 
				movimentacoes.valor, movimentacoes.cliente, movimentacoes.moeda, movimentacoes.categoria, movimentacoes.cambio);
	}
	
	
}
