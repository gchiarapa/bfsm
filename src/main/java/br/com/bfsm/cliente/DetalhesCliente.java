package br.com.bfsm.cliente;

import br.com.bfsm.model.Cliente;

public record DetalhesCliente(
		
		Long id, 
		
		String nome,
		
		String endereco
		) {

	public DetalhesCliente(Cliente cliente) {
		this(cliente.getId(), cliente.getNome(), cliente.getEndereco());
	}

}
