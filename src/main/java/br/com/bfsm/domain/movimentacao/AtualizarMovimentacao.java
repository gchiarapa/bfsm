package br.com.bfsm.domain.movimentacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;

public record AtualizarMovimentacao(
		
		@NotNull
		Long id,
		
		String tipo,
		
		@NotNull
		@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
		LocalDateTime data,
		
		BigDecimal valor,
		

		@NotNull
		Long clienteAId,
		
		Long clienteBId,
		
		Long moedaId,
		
		Long categoriaId
		
		) {

}
