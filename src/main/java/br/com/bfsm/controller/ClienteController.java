package br.com.bfsm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.bfsm.model.Cliente;
import br.com.bfsm.service.ClienteService;

@RestController
@RequestMapping(path = "/cliente")
public class ClienteController {
	
	@Autowired
	ClienteService clienteService;
	
	private static final Logger log = LoggerFactory.getLogger(ClienteController.class);
	
	@PostMapping(value = "/adicionar")
	public @ResponseBody String adicionarCliente(@RequestParam String nome, @RequestParam String endereco) {
		
//		log.info("Tentativa de adicionar cliente - Sessão: {}", SecurityContextHolder.getContext().getAuthentication());
		log.debug("Valores recebidos: [nome] [endereco] " + nome, endereco);
		
		Cliente cliente = new Cliente();
		
		cliente.setNome(nome);
		cliente.setEndereco(endereco);
		
		String status = clienteService.salvarCliente(cliente);
		
		return status;
	}

}
