package br.com.bfsm.domain.movimentacao;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.bfsm.domain.cliente.Cliente;
import jakarta.validation.constraints.NotNull;

public record AtualizarMovimentacao(
		
		@NotNull
		Long id,
		
		String tipo,
		
		@NotNull
		@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
		LocalDateTime data,
		
		String valor,
		

		Cliente cliente,
		
		String moeda
		
		) {

}
