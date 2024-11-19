package br.com.bfsm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
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
import br.com.bfsm.infra.exception.ClienteException;
import br.com.bfsm.service.ClienteService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;



@RestController
@RequestMapping("/cliente")
@SecurityRequirement(name = "bearer-key")
public class ClienteController {
	
	@Autowired
	ClienteService clienteService;
	
	private static final Logger log = LoggerFactory.getLogger(ClienteController.class);
	
	@PostMapping(value = "/adicionar")
	public ResponseEntity cadatrar(@RequestBody @Valid CadastroCliente cadastroCliente, UriComponentsBuilder uriBuilder) {
		
//		log.info("Tentativa de adicionar cliente - Sessão: {}", SecurityContextHolder.getContext().getAuthentication());
		log.info("Valores recebidos: " + cadastroCliente.toString());
		

		
		try {
			Cliente cliente = clienteService.salvar(cadastroCliente);
			var uri = uriBuilder.path("/cliente/{id}").buildAndExpand(cliente.getId()).toUri();
			return ResponseEntity.created(uri).body(new DetalhesCliente(cliente));
		} catch (ClienteException e) {
			ProblemDetail forStatusAndDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), "Erro para cadastrar: " + e.getMessage());
			return ResponseEntity.of(forStatusAndDetail).build();
		}
		
	}
	
	@GetMapping()
	public ResponseEntity buscar(@RequestParam Long clienteId, @RequestParam(defaultValue = "1") boolean ativo) {
		
		log.info("Iniciando busca do id: [id] " + clienteId);
		
		try {
			Cliente buscarClientePeloId = clienteService.buscarClientePeloId(clienteId, ativo);
			log.info("O id: [id] " + clienteId + " foi localizado");
			return ResponseEntity.ok().body(new DetalhesCliente(buscarClientePeloId));			
		} catch (ClienteException e) {
			return ResponseEntity.internalServerError().build();
		} catch (EntityNotFoundException e) {
			log.error("O id: [id] " + clienteId + " não foi localizado");
			ProblemDetail forStatusAndDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), "Cliente não localizado");
			return ResponseEntity.of(forStatusAndDetail).build();
		} catch (Exception e) {
			ProblemDetail forStatusAndDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), "Erro para buscar: " + e.getMessage());
			return ResponseEntity.of(forStatusAndDetail).build();
		}
		
	}
	
	@DeleteMapping()
	public ResponseEntity remover(@RequestParam Long clienteId) {
		
		log.info("Iniciando remocao do id: [id] " + clienteId);
		
		String removerPeloId;
		try {
			removerPeloId = clienteService.removerPeloId(clienteId);
			log.info("remocao do id: [id] " + clienteId + " efetuada com sucesso");
			return ResponseEntity.noContent().build();
		} catch (ClienteException e) {
			return ResponseEntity.internalServerError().build();
		} catch (EntityNotFoundException e) {
			log.error("O id: [id] " + clienteId + " não foi localizado");
			ProblemDetail forStatusAndDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), "Cliente não localizado");
			return ResponseEntity.of(forStatusAndDetail).build();
		} catch (Exception e) {
			ProblemDetail forStatusAndDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), "Erro para remover: " + e.getMessage());
			return ResponseEntity.of(forStatusAndDetail).build();
		}
	}
	
	@PutMapping("/atualizar")
	public ResponseEntity atualizar(@RequestBody AtualizaCliente clienteAtualizacao, UriComponentsBuilder uriBuilder) {
//		log.info("Tentativa de adicionar cliente - Sessão: {}", SecurityContextHolder.getContext().getAuthentication());
		log.info("Valores recebidos: [nome] [endereco] " + clienteAtualizacao.toString());
		
		Cliente cliente = new Cliente(clienteAtualizacao);
		
		try {
			cliente = clienteService.atualizar(cliente);
			var uri = uriBuilder.path("/cliente/{id}").buildAndExpand(cliente.getId()).toUri();
			return ResponseEntity.created(uri).body(new DetalhesCliente(cliente));
		} catch (ClienteException e) {
			ProblemDetail forStatusAndDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), "Erro para atualizar: " + e.getMessage());
			return ResponseEntity.of(forStatusAndDetail).build();	
		} catch (EntityNotFoundException e) {
			log.error("O cliente id " + cliente.getId() + " não foi localizado");
			ProblemDetail forStatusAndDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), "Cliente não localizado");
			return ResponseEntity.of(forStatusAndDetail).build();
		} catch (Exception e) {
			ProblemDetail forStatusAndDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), "Erro para atualizar: " + e.getMessage());
			return ResponseEntity.of(forStatusAndDetail).build();	
		}
		
	}

}
