package br.com.bfsm.domain.movimentacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.bfsm.domain.cambio.TaxaCambio;
import br.com.bfsm.domain.cliente.Cliente;

public record DetalhesMovimentacao(
		
		Long id,
		
		String tipo,
		
		@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
		LocalDateTime data,
		
		BigDecimal valor,
		
		Cliente cliente,
		Moeda moeda,
		Categoria categoria,
		TaxaCambio cambio,
		boolean ativo
		) {
	
	public DetalhesMovimentacao(Movimentacoes movimentacoes) {
		this(movimentacoes.id, movimentacoes.tipo, movimentacoes.data, 
				movimentacoes.valor, movimentacoes.cliente, movimentacoes.moeda, movimentacoes.categoria, movimentacoes.cambio, movimentacoes.ativo);
	}
	
	
}
