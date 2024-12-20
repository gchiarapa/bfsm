package br.com.bfsm.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.data.web.SortDefault;
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
import br.com.bfsm.domain.movimentacao.Categoria;
import br.com.bfsm.domain.movimentacao.DadosCadastroMovimentacao;
import br.com.bfsm.domain.movimentacao.DetalhesMovimentacao;
import br.com.bfsm.domain.movimentacao.Moeda;
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
	
	@Autowired
	private PagedResourcesAssembler<DetalhesMovimentacao> paged;
	
	private static final Logger log = LoggerFactory.getLogger(MovimentacaoController.class);
	
	
	@PostMapping(value = "/adicionar")
	public ResponseEntity cadastrar(@RequestBody DadosCadastroMovimentacao movimentacoesDados, 
			UriComponentsBuilder uriBuilder) {
		
		log.info("Valores recebidos: []" + movimentacoesDados.toString());
		
		Cliente cliente = new Cliente();
		Moeda moeda = new Moeda();
		Categoria categoria = new Categoria();
		Movimentacoes movimentacoes = new Movimentacoes(movimentacoesDados);
		
		cliente.setId(movimentacoesDados.clienteAId());
		moeda.setId(movimentacoesDados.moedaId());
		categoria.setId(movimentacoesDados.categoriaId());

		movimentacoes.setCliente(cliente);
		movimentacoes.setCategoria(categoria);
		movimentacoes.setMoeda(moeda);
		
		try {
			movimentacoesService.salvarMovimentacao(movimentacoes, movimentacoesDados.clienteBId());
			var uri = uriBuilder.path("/movimentacoes/{id}").buildAndExpand(movimentacoes.getId()).toUri();
			return ResponseEntity.created(uri).body(new DetalhesMovimentacao(movimentacoes));
		} catch (EntityNotFoundException e) {
			ProblemDetail forStatusAndDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), "Cliente não localizado: " + e.getMessage());
			return ResponseEntity.of(forStatusAndDetail).build();
		} catch (MovimentacoesException e) {
			ProblemDetail forStatusAndDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), "Erro para adicionar: " + e.getMessage());
			return ResponseEntity.of(forStatusAndDetail).build();
		} catch (Exception e) {
			ProblemDetail forStatusAndDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), "Erro para adicionar: " + e.getMessage());
			return ResponseEntity.of(forStatusAndDetail).build();
		}
	}
	
	@GetMapping()
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
	
	
	@DeleteMapping()
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
			movimentacoesService.atualizar(movimentacao, movimentacaoAtualizacao.clienteBId());
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
			@RequestParam(required = false) Long categoriaId,
			@RequestParam(required = true ,defaultValue = "true") boolean ativo
			) throws ParametrosAusentesException {
		
		log.info("Iniciando criação do relatório");
		
		if(null == movimentacaoId && null == dataInicio && null == dataFim &&  null == clienteId && null == categoriaId) {
			throw new ParametrosAusentesException("Parametro(s) inválidos ou não enviados!");
		}
		

		MockMultipartFile relatorio = movimentacoesService.relatorio(movimentacaoId, dataInicio,dataFim, clienteId, moedaId, categoriaId, ativo);
		
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
			@PageableDefault(page = 0, size = 5) @SortDefault.SortDefaults({@SortDefault(sort = "id", direction = Sort.Direction.DESC)}) Pageable p,
			@RequestParam(required = false, defaultValue = "true") boolean ativo) throws ParametrosAusentesException {
		
		log.info("Iniciando criação do relatório");
		
		if(null == movimentacaoId && null == dataInicio && null == dataFim && null == clienteId && null == categoriaId) {
			throw new ParametrosAusentesException("Parametro(s) inválido(s) ou não enviado(s)!");
		}
			
		try {
			Pageable page = PageRequest.of(p.getPageNumber(), p.getPageSize());
			Page<DetalhesMovimentacao> relatorio = movimentacoesService.relatorioResumo(page, movimentacaoId, dataInicio,dataFim, clienteId, 
					moedaId, categoriaId, ativo);
			log.info("relatório gerado ");
			return ResponseEntity.ok().body(paged.toModel(relatorio));
		} catch (EntityNotFoundException e) {
			ProblemDetail forStatusAndDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), "Não foi localizado nenhuma movimentação com os dados fornecidos");
			return ResponseEntity.of(forStatusAndDetail).build();
		} catch (Exception e) {
			log.error("Erro para gerar relatorio final: " + e.getMessage());
			e.printStackTrace();
			ProblemDetail forStatusAndDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), "Erro para gerar relatório: " + e.getMessage());
			return ResponseEntity.of(forStatusAndDetail).build();
		}
			
	}
	
	@GetMapping(value = "buscar/mock")
	public ResponseEntity buscarMock() {
		
		log.info("Iniciando busca do mock" );
		
		List<Movimentacoes> buscarMovimentacaoPeloId;
		try {
			buscarMovimentacaoPeloId = movimentacoesService.buscarMovimentacaoPeloIdMock();
			log.info("O mock: " + buscarMovimentacaoPeloId + " foi localizado");
			List<DetalhesMovimentacao> detalhesList = buscarMovimentacaoPeloId.stream()
			.map(m -> new DetalhesMovimentacao(m))
			.collect(Collectors.toList());
			return ResponseEntity.ok().body(detalhesList);
		} catch (Exception e) {
			log.error("Erro ao buscar mock");
			ProblemDetail forStatusAndDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), "Erro ao buscar mock: " + e.getMessage());
			return ResponseEntity.of(forStatusAndDetail).build();
		}
		
	}
	

}
