package br.com.bfsm.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.bfsm.domain.cliente.Cliente;
import br.com.bfsm.repository.ClienteRepository;

@Service
public class ClienteService {
	private static final Logger log = LoggerFactory.getLogger(ClienteService.class);

	@Autowired
	ClienteRepository clienteRepo;

	public String salvar(Cliente cliente) {

		String status = "";
		try {
			clienteRepo.save(cliente);
			log.debug("cliente cadastrado com sucesso!");
			status = "OK";
		} catch (Exception e) {
			log.error("Erro para cadastrar cliente: " + e.getMessage());
			status = "NOK";
		}

		return status;
	}

	public Optional<Cliente> buscarClientePeloId(Long clienteId) {

		Optional<Cliente> cliente = java.util.Optional.empty();
		try {
			cliente = clienteRepo.findById(clienteId);
			return cliente;
		} catch (Exception e) {
			log.error("Erro para localizar cliente: " + e.getMessage());
			return cliente;
		}

	}

	public String removerPeloId(Long clienteId) {

		String status = "";

		try {
			boolean exists = clienteRepo.existsById(clienteId);
			if (exists) {
				clienteRepo.deleteById(clienteId);
				return status = "OK";
			} else {
				log.debug("cliente não localizado !");
				return status = "404";
			}
		} catch (Exception e) {
			log.error("Erro para remover cliente: " + e.getMessage());
			return status = e.getMessage();
		}

	}

	public String atualizar(Cliente cliente) {

		String status = "";
		try {
			boolean existsById = clienteRepo.existsById(cliente.getId());
			if (existsById) {
				clienteRepo.save(cliente);
				log.debug("cliente atualizado com sucesso!");
				status = "OK";				
			} else {
				log.debug("cliente não localizado!");
				status = "404";
			}
		} catch (Exception e) {
			log.error("Erro para atualizar cliente: " + e.getMessage());
			status = "NOK";
		}

		return status;
	}

}
