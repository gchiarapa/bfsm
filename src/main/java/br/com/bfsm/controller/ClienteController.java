package br.com.bfsm.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.bfsm.domain.cliente.AtualizaCliente;
import br.com.bfsm.domain.cliente.CadastroCliente;
import br.com.bfsm.domain.cliente.Cliente;
import br.com.bfsm.domain.cliente.DetalhesCliente;
import br.com.bfsm.service.ClienteService;
import jakarta.validation.Valid;



@RestController
@RequestMapping("/cliente")
public class ClienteController {
	
	@Autowired
	ClienteService clienteService;
	
	private static final Logger log = LoggerFactory.getLogger(ClienteController.class);
	
	@PostMapping(value = "/adicionar")
	public ResponseEntity cadatrar(@RequestBody @Valid CadastroCliente cadastroCliente, UriComponentsBuilder uriBuilder) {
		
//		log.info("Tentativa de adicionar cliente - Sessão: {}", SecurityContextHolder.getContext().getAuthentication());
		log.debug("Valores recebidos: [nome] [endereco] " + cadastroCliente.toString());
		
		Cliente cliente = new Cliente(cadastroCliente);
		
		String status = clienteService.salvar(cliente);
		
		if(status == "OK") {
			var uri = uriBuilder.path("/cliente/{id}").buildAndExpand(cliente.getId()).toUri();
			return ResponseEntity.created(uri).body(new DetalhesCliente(cliente));
		} else {
			return ResponseEntity.internalServerError().build();			
		}
	}
	
	@GetMapping("{id}")
	public ResponseEntity buscar(@RequestParam Long clienteId) {
		
		log.info("Iniciando busca do id: [id] " + clienteId);
		
		Optional<Cliente> buscarClientePeloId = clienteService.buscarClientePeloId(clienteId);
		
		if(buscarClientePeloId.isPresent()) {
			log.info("O id: [id] " + clienteId + " foi localizado");
			return ResponseEntity.ok().body(new DetalhesCliente(buscarClientePeloId.get()));
		} else {
			return ResponseEntity.notFound().build();
		}
		
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity remover(@RequestParam Long clienteId) {
		
		log.debug("Iniciando remocao do id: [id] " + clienteId);
		
		String removerPeloId = clienteService.removerPeloId(clienteId);
		
		if(removerPeloId == "OK") {
			log.debug("remocao do id: [id] " + clienteId + " efetuada com sucesso");
			return ResponseEntity.noContent().build();
		} else if(removerPeloId == "404") {
			log.debug("O id: [id] " + clienteId + " não foi localizado");
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.internalServerError().build();
		}
		
	}
	
	@PutMapping("/atualizar")
	public ResponseEntity putMethodName(@RequestBody AtualizaCliente clienteAtualizacao, UriComponentsBuilder uriBuilder) {
//		log.info("Tentativa de adicionar cliente - Sessão: {}", SecurityContextHolder.getContext().getAuthentication());
		log.debug("Valores recebidos: [nome] [endereco] " + clienteAtualizacao.toString());
		
		Cliente cliente = new Cliente(clienteAtualizacao);
		
		String status = clienteService.atualizar(cliente);
		
		if(status == "OK") {
			var uri = uriBuilder.path("/cliente/{id}").buildAndExpand(cliente.getId()).toUri();
			return ResponseEntity.created(uri).body(new DetalhesCliente(cliente));
		} else if(status == "404") {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.internalServerError().build();			
		}
	}
	

}
