package br.com.bfsm.domain.cliente;

public record DetalhesCliente(
		
		Long id, 
		
		String nome,
		
		String endereco
		) {

	public DetalhesCliente(Cliente cliente) {
		this(cliente.getId(), cliente.getNome(), cliente.getEndereco());
	}

}
