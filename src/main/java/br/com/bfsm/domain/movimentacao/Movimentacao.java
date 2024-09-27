package br.com.bfsm.domain.movimentacao;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.bfsm.domain.cliente.Cliente;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import lombok.Data;

@Data
@Entity
@Table(name = "movimentacoes", schema = "bank")
public class Movimentacao {
	
	public Movimentacao(@Valid DadosCadastroMovimentacao movimentacoesDados) {
		this.data = movimentacoesDados.data();
		this.tipo = movimentacoesDados.tipo();
		this.valor = movimentacoesDados.valor();
	}

	public Movimentacao(AtualizarMovimentacao movimentacaoAtualizacao) {
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
	
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	public
	LocalDateTime data;
	
	public String valor;
	
	@ManyToOne
	@JoinColumn(name = "id_cliente")
	public Cliente cliente;

}
