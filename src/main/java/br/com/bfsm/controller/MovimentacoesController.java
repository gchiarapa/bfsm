package br.com.bfsm.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

import br.com.bfsm.cliente.CadastroCliente;
import br.com.bfsm.cliente.DetalhesCliente;
import br.com.bfsm.model.Cliente;
import br.com.bfsm.model.Movimentacoes;
import br.com.bfsm.movimentacao.AtualizarMovimentacao;
import br.com.bfsm.movimentacao.DetalhesMovimentacao;
import br.com.bfsm.movimentacoes.DetalhesMovimentacoes;
import br.com.bfsm.movimentacoes.MovimentacoesDadosCadastro;
import br.com.bfsm.repository.ClienteRepository;
import br.com.bfsm.service.MovimentacoesService;


@RestController
@RequestMapping("/movimentacoes")
public class MovimentacoesController {
	
	@Autowired
	MovimentacoesService movimentacoesService;
	
	@Autowired
	ClienteRepository clienteRepo;
	
	private static final Logger log = LoggerFactory.getLogger(MovimentacoesController.class);
	
	@PostMapping(value = "/adicionar", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity cadastrar(@RequestBody MovimentacoesDadosCadastro movimentacoesDados, 
			UriComponentsBuilder uriBuilder) {
		
		log.debug("Valores recebidos: [Tipo] [Data] [Valor] [IdCliente]" + movimentacoesDados.toString());
		
		Cliente cliente = new Cliente();
		Movimentacoes movimentacoes = new Movimentacoes(movimentacoesDados);
		
		cliente.setId(movimentacoesDados.clienteId());

		movimentacoes.setCliente(cliente);
		
		String status = movimentacoesService.salvarMovimentacao(movimentacoes);
		
		if(status == "OK") {
			var uri = uriBuilder.path("/movimentacoes/{id}").buildAndExpand(movimentacoes.getId()).toUri();
			return ResponseEntity.created(uri).body(new DetalhesMovimentacoes(movimentacoes));
		} else {
			return ResponseEntity.internalServerError().build();			
		}
	}
	
	@GetMapping("{id}")
	public ResponseEntity buscar(@RequestParam Long movimentacaoId) {
		
		log.info("Iniciando busca do id: [id] " + movimentacaoId);
		
		Optional<Movimentacoes> buscarMovimentacaoPeloId = movimentacoesService.buscarMovimentacaoPeloId(movimentacaoId);
		
		if(buscarMovimentacaoPeloId.isPresent()) {
			log.info("O id: [id] " + movimentacaoId + " foi localizado");
			return ResponseEntity.ok().body(new DetalhesMovimentacao(buscarMovimentacaoPeloId.get()));
		} else {
			return ResponseEntity.notFound().build();
		}
		
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity remover(@RequestParam Long movimentacaoId) {
		
		log.debug("Iniciando remocao do id: [id] " + movimentacaoId);
		
		String removerPeloId = movimentacoesService.removerPeloId(movimentacaoId);
		
		if(removerPeloId == "OK") {
			log.debug("remocao do id: [id] " + movimentacaoId + " efetuada com sucesso");
			return ResponseEntity.noContent().build();
		} else if(removerPeloId == "404") {
			log.debug("O id: [id] " + movimentacaoId + " não foi localizado");
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.internalServerError().build();
		}
		
	}
	
	@PutMapping("/atualizar")
	public ResponseEntity putMethodName(@RequestBody AtualizarMovimentacao movimentacaoAtualizacao, UriComponentsBuilder uriBuilder) {
//		log.info("Tentativa de adicionar cliente - Sessão: {}", SecurityContextHolder.getContext().getAuthentication());
		log.debug("Valores recebidos: [nome] [endereco] " + movimentacaoAtualizacao.toString());
		
		Movimentacoes movimentacao = new Movimentacoes(movimentacaoAtualizacao);
		
		String status = movimentacoesService.atualizar(movimentacao);
		
		if(status == "OK") {
			var uri = uriBuilder.path("/movimentacao/{id}").buildAndExpand(movimentacao.getId()).toUri();
			return ResponseEntity.created(uri).body(new DetalhesMovimentacao(movimentacao));
		} else if(status == "404") {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.internalServerError().build();			
		}
	}

}
