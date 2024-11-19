package br.com.bfsm.domain.movimentacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.type.NumericBooleanConverter;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import br.com.bfsm.domain.cambio.TaxaCambio;
import br.com.bfsm.domain.cliente.Cliente;
import jakarta.persistence.Convert;
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
		this.ativo = movimentacoesDados.ativo();
	}

	public Movimentacoes(AtualizarMovimentacao movimentacaoAtualizacao) {
		
		cliente = new Cliente();
		cliente.setId(movimentacaoAtualizacao.clienteAId());
		
		moeda = new Moeda();
		moeda.setId(movimentacaoAtualizacao.moedaId());
		
		categoria = new Categoria();
		categoria.setId(movimentacaoAtualizacao.categoriaId());
		
		this.id = movimentacaoAtualizacao.id();
		this.data = movimentacaoAtualizacao.data();
		this.tipo = movimentacaoAtualizacao.tipo();
		this.valor = movimentacaoAtualizacao.valor();
		this.setCliente(cliente);
		this.setMoeda(moeda);
		this.setCategoria(categoria);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;
	
	public String tipo;
	
	public LocalDateTime data;
	
	public BigDecimal valor;
	
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
		
	@Convert(converter = NumericBooleanConverter.class)
    boolean ativo;

}
