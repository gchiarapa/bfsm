package br.com.bfsm.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.bfsm.domain.cliente.CadastroCliente;
import br.com.bfsm.domain.cliente.Cliente;
import br.com.bfsm.infra.exception.ClienteException;
import br.com.bfsm.repository.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class ClienteService {
	private static final Logger log = LoggerFactory.getLogger(ClienteService.class);

	@Autowired
	ClienteRepository clienteRepo;

	public Cliente salvar(CadastroCliente cadastroCliente) throws ClienteException {
		
		Cliente cliente = new Cliente(cadastroCliente);

		try {
			clienteRepo.save(cliente);
			log.info("cliente cadastrado com sucesso!");
			return cliente;
		} catch (Exception e) {
			log.error("Erro para cadastrar cliente: " + e.getMessage());
			throw new ClienteException("Erro para cadastrar cliente");
		}

	}

	public Cliente buscarClientePeloId(Long clienteId, int ativo) throws ClienteException {

		Optional<Cliente> clienteExiste = java.util.Optional.empty();
		Cliente cliente = null;
		
		clienteExiste = clienteRepo.findClienteByIdAndAtivo(clienteId, ativo);
		if(clienteExiste.isPresent()) {
			return cliente = new Cliente(clienteExiste.get());
		} else {
			log.info("cliente não localizado !");
			throw new EntityNotFoundException("Cliente não localizado!");
		}

	}
	
	@Transactional
	public String removerPeloId(Long clienteId) throws ClienteException {

		String status = "";

		boolean exists = clienteRepo.existsById(clienteId);
		if (exists) {
//			clienteRepo.deleteById(clienteId);
			clienteRepo.updateClienteAtivoById((byte) 0, clienteId);
			return status = "OK";
		} else {
			log.info("cliente não localizado !");
			throw new EntityNotFoundException("Cliente não localizado!");
		}

	}

	public Cliente atualizar(Cliente cliente) throws ClienteException, EntityNotFoundException {

		boolean existsById = clienteRepo.existsById(cliente.getId());
		if (existsById) {
			clienteRepo.save(cliente);
			log.info("cliente atualizado com sucesso!");
			return cliente;
		} else {
			log.info("cliente não localizado!");
			throw new EntityNotFoundException("Cliente não localizado!");
		}

	}

}
