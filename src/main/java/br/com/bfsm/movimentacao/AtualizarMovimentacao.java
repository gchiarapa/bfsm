package br.com.bfsm.movimentacao;

import java.time.LocalDateTime;

import br.com.bfsm.model.Cliente;

public record AtualizarMovimentacao(
		Long id,
		
		String tipo,
				
		LocalDateTime data,
		
		String valor,
		
		Cliente cliente
		
		) {

}
