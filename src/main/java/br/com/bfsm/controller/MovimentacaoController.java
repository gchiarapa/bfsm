package br.com.bfsm.controller;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.bfsm.domain.cliente.Cliente;
import br.com.bfsm.domain.movimentacao.AtualizarMovimentacao;
import br.com.bfsm.domain.movimentacao.DadosCadastroMovimentacao;
import br.com.bfsm.domain.movimentacao.DetalhesMovimentacao;
import br.com.bfsm.domain.movimentacao.Movimentacoes;
import br.com.bfsm.infra.exception.MovimentacoesException;
import br.com.bfsm.infra.exception.ParametrosAusentesException;
import br.com.bfsm.repository.ClienteRepository;
import br.com.bfsm.service.MovimentacoesService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityNotFoundException;


@RestController
@RequestMapping("/movimentacoes")
@SecurityRequirement(name = "bearer-key") 
public class MovimentacaoController {
	
	@Autowired
	MovimentacoesService movimentacoesService;
	
	@Autowired
	ClienteRepository clienteRepo;
	
	private static final Logger log = LoggerFactory.getLogger(MovimentacaoController.class);
	
	
	@PostMapping(value = "/adicionar")
	public ResponseEntity cadastrar(@RequestBody DadosCadastroMovimentacao movimentacoesDados, 
			UriComponentsBuilder uriBuilder) {
		
		log.info("Valores recebidos: [Tipo] [Data] [Valor] [IdCliente]" + movimentacoesDados.toString());
		
		Cliente cliente = new Cliente();
		Movimentacoes movimentacoes = new Movimentacoes(movimentacoesDados);
		
		cliente.setId(movimentacoesDados.clienteId());

		movimentacoes.setCliente(cliente);
		
		try {
			movimentacoesService.salvarMovimentacao(movimentacoes);
			var uri = uriBuilder.path("/movimentacoes/{id}").buildAndExpand(movimentacoes.getId()).toUri();
			return ResponseEntity.created(uri).body(new DetalhesMovimentacao(movimentacoes));
		} catch (MovimentacoesException e) {
			ProblemDetail forStatusAndDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), "Erro para adicionar: " + e.getMessage());
			return ResponseEntity.of(forStatusAndDetail).build();
		} catch (Exception e) {
			ProblemDetail forStatusAndDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), "Erro para adicionar: " + e.getMessage());
			return ResponseEntity.of(forStatusAndDetail).build();
		}
	}
	
	@GetMapping("{id}")
	public ResponseEntity buscar(@RequestParam Long movimentacaoId) {
		
		log.info("Iniciando busca do id: [id] " + movimentacaoId);
		
		Optional<Movimentacoes> buscarMovimentacaoPeloId;
		try {
			buscarMovimentacaoPeloId = movimentacoesService.buscarMovimentacaoPeloId(movimentacaoId);
			log.info("O id: [id] " + movimentacaoId + " foi localizado");
			return ResponseEntity.ok().body(new DetalhesMovimentacao(buscarMovimentacaoPeloId.get()));
		} catch (MovimentacoesException e) {
			e.printStackTrace();
			ProblemDetail forStatusAndDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), "Erro para buscar: " + e.getMessage());
			return ResponseEntity.of(forStatusAndDetail).build();
		} catch (EntityNotFoundException e) {
			log.error("A movimentacao" + movimentacaoId + " não foi localizada");
			ProblemDetail forStatusAndDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), "Movimentação não localizado");
			return ResponseEntity.of(forStatusAndDetail).build();
		}
		
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity remover(@RequestParam Long movimentacaoId) {
		
		log.info("Iniciando remocao do id: [id] " + movimentacaoId);
		
		try {
			movimentacoesService.removerPeloId(movimentacaoId);			
			log.info("remocao do id: [id] " + movimentacaoId + " efetuada com sucesso");
			return ResponseEntity.noContent().build();
		} catch (MovimentacoesException e) {
			ProblemDetail forStatusAndDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), "Erro para remover: " + e.getMessage());
			return ResponseEntity.of(forStatusAndDetail).build();
		} catch (EntityNotFoundException e) {
			log.error("A movimentacao" + movimentacaoId + " não foi localizada");
			ProblemDetail forStatusAndDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), "Movimentação não localizado");
			return ResponseEntity.of(forStatusAndDetail).build();
		}
		
	}
	
	@PutMapping("/atualizar")
	public ResponseEntity atualizar(@RequestBody AtualizarMovimentacao movimentacaoAtualizacao, UriComponentsBuilder uriBuilder) {
//		log.info("Tentativa de adicionar cliente - Sessão: {}", SecurityContextHolder.getContext().getAuthentication());
		log.info("Valores recebidos: [nome] [endereco] " + movimentacaoAtualizacao.toString());
		
		Movimentacoes movimentacao = new Movimentacoes(movimentacaoAtualizacao);
		
		try {
			movimentacoesService.atualizar(movimentacao);
			var uri = uriBuilder.path("/movimentacao/{id}").buildAndExpand(movimentacao.getId()).toUri();
			return ResponseEntity.created(uri).body(new DetalhesMovimentacao(movimentacao));
		} catch (MovimentacoesException e) {
			ProblemDetail forStatusAndDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), "Erro para atualizar: " + e.getMessage());
			return ResponseEntity.of(forStatusAndDetail).build();
		} catch (EntityNotFoundException e) {
			log.error("A movimentacao" + movimentacao.getId() + " não foi localizada");
			ProblemDetail forStatusAndDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), "Movimentação não localizado");
			return ResponseEntity.of(forStatusAndDetail).build();
		}
		
	}
	
	@GetMapping("/relatorio")
	public ResponseEntity<InputStreamResource> relatorio(@RequestParam(required = false) Long movimentacaoId, 
			@RequestParam(required = false, defaultValue = "") String dataInicio, 
			@RequestParam(required = false, defaultValue = "") String dataFim,
			@RequestParam(required = false) Long clienteId, 
			@RequestParam(required = false) Long moedaId,
			@RequestParam(required = false) Long categoriaId
			) throws ParametrosAusentesException {
		
		log.info("Iniciando criação do relatório");
		
		if(null == movimentacaoId && null == dataInicio && null == dataFim &&  null == clienteId && null == categoriaId) {
			throw new ParametrosAusentesException("Parametro(s) inválidos ou não enviados!");
		}
		
		MockMultipartFile relatorio = movimentacoesService.relatorio(movimentacaoId, dataInicio,dataFim, clienteId, moedaId, categoriaId);
		
		if(relatorio != null) {
			log.info("relatório gerado " + relatorio.getName());
			
			
			InputStreamResource relatorioFinal = null;
			try {
				relatorioFinal = new InputStreamResource(relatorio.getInputStream());
			} catch (IOException e) {
				log.error("Erro para gerar relatorio final " + e.getMessage());
				e.printStackTrace();
				ProblemDetail forStatusAndDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), "Erro para gerar relatório: " + e.getMessage());
				return ResponseEntity.of(forStatusAndDetail).build();
			}
			
	        HttpHeaders headers = new HttpHeaders();
	        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + relatorio.getName());
	        headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			
			return new ResponseEntity<InputStreamResource>(relatorioFinal, headers, HttpStatus.OK);
		} else {
			InputStreamResource relatorioFinal = null;
			ProblemDetail forStatusAndDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), "Não foi localizado nenhuma movimentação com os dados fornecidos");
			return ResponseEntity.of(forStatusAndDetail).build();
		}
	}
	
	@GetMapping("/relatorio/resumo")
	public ResponseEntity relatorioResumo(@RequestParam(required = false) Long movimentacaoId, 
			@RequestParam(required = false, defaultValue = "") String dataInicio, 
			@RequestParam(required = false, defaultValue = "") String dataFim, 
			@RequestParam(required = false) Long clienteId, 
			@RequestParam(required = false) Long moedaId, @RequestParam(required = false) Long categoriaId,
			Pageable p) throws ParametrosAusentesException {
		
		log.info("Iniciando criação do relatório");
		
		if(null == movimentacaoId && null == dataInicio && null == dataFim && null == clienteId && null == categoriaId) {
			throw new ParametrosAusentesException("Parametro(s) inválido(s) ou não enviado(s)!");
		}
			
		try {
			Pageable page = PageRequest.of(p.getPageNumber(), p.getPageSize());
			Page<DetalhesMovimentacao> relatorio = movimentacoesService.relatorioResumo(page, movimentacaoId, dataInicio,dataFim, clienteId, moedaId, categoriaId);
			log.info("relatório gerado ");
			return ResponseEntity.ok().body(relatorio);
		} catch (EntityNotFoundException e) {
			ProblemDetail forStatusAndDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), "Não foi localizado nenhuma movimentação com os dados fornecidos");
			return ResponseEntity.of(forStatusAndDetail).build();
		} catch (Exception e) {
			log.error("Erro para gerar relatorio final: " + e.getMessage());
			e.printStackTrace();
			ProblemDetail forStatusAndDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), "Erro para gerar relatório: " + e.getMessage());
			return ResponseEntity.of(forStatusAndDetail).build();
		}
			
	}
	

}
