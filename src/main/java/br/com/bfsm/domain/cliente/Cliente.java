package br.com.bfsm.domain.cliente;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import br.com.bfsm.domain.movimentacao.Movimentacoes;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cliente", schema = "bank")
public class Cliente {
	

	public Cliente(@Valid Cliente cliente) {
		this.nome = cliente.getNome();
		this.endereco = cliente.getEndereco();
		this.saldo = cliente.getSaldo();
		this.id = cliente.getId();
		this.ativo = cliente.getAtivo();
	}
	
	public Cliente(AtualizaCliente clienteAtualizacao) {
		this.id = clienteAtualizacao.id();
		this.nome = clienteAtualizacao.nome();
		this.endereco = clienteAtualizacao.endereco();
		this.saldo = clienteAtualizacao.saldo();
	}

	public Cliente(CadastroCliente cadastroCliente) {
		this.nome = cadastroCliente.nome();
		this.endereco = cadastroCliente.endereco();
		this.saldo = cadastroCliente.saldo();
		this.ativo = 1;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	String nome;
	
	String endereco;
	
	String saldo;
	
	@OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
	@JsonIgnore
	List<Movimentacoes> movimentacoes;
	
    int ativo;

}
