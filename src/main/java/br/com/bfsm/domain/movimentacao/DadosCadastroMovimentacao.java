package br.com.bfsm.domain.movimentacao;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;


public record DadosCadastroMovimentacao(
		
		@NotNull
		String tipo,
		
		@NotNull
		@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
		LocalDateTime data,
		
		@NotNull
		String valor,
		
		@NotNull
		Long clienteId) {
	
	

}
