package br.com.bfsm.domain.movimentacao;

import java.time.LocalDateTime;

import br.com.bfsm.domain.cliente.Cliente;

public record AtualizarMovimentacao(
		Long id,
		
		String tipo,
				
		LocalDateTime data,
		
		String valor,
		
		Cliente cliente
		
		) {

}
