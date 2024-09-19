package br.com.bfsm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.bfsm.model.Cliente;
import br.com.bfsm.repository.ClienteRepository;

@Service
public class ClienteService {
	private static final Logger log = LoggerFactory.getLogger(ClienteService.class);
	
	@Autowired
	ClienteRepository clienteRepo;
	
	
	public String salvarCliente(Cliente cliente) {
		
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

}
