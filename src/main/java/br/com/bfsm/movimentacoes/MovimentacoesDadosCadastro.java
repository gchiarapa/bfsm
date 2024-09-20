package br.com.bfsm.movimentacoes;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;


public record MovimentacoesDadosCadastro(
		
		String tipo,
		
		@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
		LocalDateTime data,
		
		String valor,
		
		Long clienteId) {
	
	

}
