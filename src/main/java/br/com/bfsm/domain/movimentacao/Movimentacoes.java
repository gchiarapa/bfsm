package br.com.bfsm.domain.movimentacao;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.bfsm.domain.cliente.Cliente;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "movimentacoes", schema = "bank")
public class Movimentacoes {
	

	public Movimentacoes(@Valid DadosCadastroMovimentacao movimentacoesDados) {
		this.data = movimentacoesDados.data();
		this.tipo = movimentacoesDados.tipo();
		this.valor = movimentacoesDados.valor();
	}

	public Movimentacoes(AtualizarMovimentacao movimentacaoAtualizacao) {
		this.id = movimentacaoAtualizacao.id();
		this.data = movimentacaoAtualizacao.data();
		this.tipo = movimentacaoAtualizacao.tipo();
		this.valor = movimentacaoAtualizacao.valor();
		this.cliente = movimentacaoAtualizacao.cliente();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public
	Long id;
	
	public String tipo;
	
	public
	LocalDateTime data;
	
	public String valor;
	
	@ManyToOne(fetch = FetchType.LAZY)
	public Cliente cliente;
	
	@Enumerated(EnumType.STRING)
	public Moeda moeda;

}
