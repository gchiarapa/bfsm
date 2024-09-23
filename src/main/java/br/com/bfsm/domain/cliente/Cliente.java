package br.com.bfsm.domain.cliente;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import lombok.Data;

@Data
@Entity
@Table(name = "cliente", schema = "bank")
public class Cliente {
	
	public Cliente(@Valid CadastroCliente cadastroCliente) {
		this.nome = cadastroCliente.nome();
		this.endereco = cadastroCliente.endereco();
	}
	
	public Cliente() {

	}

	public Cliente(AtualizaCliente clienteAtualizacao) {
		this.id = clienteAtualizacao.id();
		this.nome = clienteAtualizacao.nome();
		this.endereco = clienteAtualizacao.endereco();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	String nome;
	
	String endereco;

}
