package br.com.bfsm.domain.movimentacao;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import br.com.bfsm.domain.cambio.TaxaCambio;
import br.com.bfsm.domain.cliente.Cliente;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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
		this.moeda = movimentacoesDados.moeda();
		this.categoria = movimentacoesDados.categoria();
	}

	public Movimentacoes(AtualizarMovimentacao movimentacaoAtualizacao) {
		this.id = movimentacaoAtualizacao.id();
		this.data = movimentacaoAtualizacao.data();
		this.tipo = movimentacaoAtualizacao.tipo();
		this.valor = movimentacaoAtualizacao.valor();
		this.cliente.setId(movimentacaoAtualizacao.clienteId());
		this.moeda = movimentacaoAtualizacao.moeda();
		this.categoria = movimentacaoAtualizacao.categoria();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;
	
	public String tipo;
	
	public LocalDateTime data;
	
	public String valor;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cliente_id")
	@JsonIncludeProperties({"id", "nome"})
	public Cliente cliente;
	
	@OneToOne
	@JoinColumn(name = "id_moeda")
	public Moeda moeda;
	
	@OneToOne
	@JoinColumn(name = "id_categoria")
	public Categoria categoria;
	
	@Transient
	public TaxaCambio cambio;

}
