package br.com.bfsm.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import br.com.bfsm.domain.cambio.TaxaCambio;
import br.com.bfsm.domain.cliente.Cliente;
import br.com.bfsm.domain.cliente.SaldoHistorico;
import br.com.bfsm.domain.movimentacao.DetalhesMovimentacao;
import br.com.bfsm.domain.movimentacao.Moeda;
import br.com.bfsm.domain.movimentacao.MovimentacaoSpecification;
import br.com.bfsm.domain.movimentacao.Movimentacoes;
import br.com.bfsm.domain.movimentacao.MovimentacoesCliente;
import br.com.bfsm.infra.exception.MovimentacoesException;
import br.com.bfsm.repository.ClienteRepository;
import br.com.bfsm.repository.MoedaRepository;
import br.com.bfsm.repository.MovimentacoesClienteRepository;
import br.com.bfsm.repository.MovimentacoesRepository;
import br.com.bfsm.repository.SaldoHistoricoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Transient;import jakarta.transaction.Transactional;

@Service
public class MovimentacoesService {

	@Autowired
	MovimentacoesRepository movimentacoesRepo;

	@Autowired
	ClienteRepository clienteRepo;
	
	@Autowired
	EntityManager entityManager;
	
	@Value("${api.exchange.url}")
	private String exchangeApi;
	
	
	@Value("${api.mock.movimentacoes.buscar}")
	private String mockApiBuscar;
	
	@Value("${api.exchange.accessKey}")
	private String exchangeApiAcessKey;

	private static final Logger log = LoggerFactory.getLogger(MovimentacoesService.class);
	
	@Autowired
	MoedaRepository moedaRepo;
	
	@Autowired
	SaldoHistoricoRepository saldoRepo;
	
	@Autowired
	MovimentacoesClienteRepository movimentacosClienteRepo;
	
	@Autowired
	MovimentacoesRepository movimentacaoRepo;
	
	public Movimentacoes salvarMovimentacao(Movimentacoes movimentacoes, Long clienteBId) throws MovimentacoesException {

		try {
//			movimentacoesRepo.save(movimentacoes);
			this.atualizarSaldoCliente(movimentacoes, clienteBId, null);
			log.info("Movimentação cadastrada com sucesso!");
			return movimentacoes;
		} catch (Exception e) {
			log.error("Erro para cadastrar movimentação: " + e.getMessage());
			throw new MovimentacoesException("Erro para cadastrar movimentação: " + e.getMessage());
		}
	}

	@Transactional
	private void atualizarSaldoCliente(Movimentacoes movimentacoes, Long clienteBId, Movimentacoes movimentacoesOld) throws MovimentacoesException {
		
		Optional<Cliente> clientebyId = clienteRepo.findById(movimentacoes.cliente.getId());
		if(clientebyId.isEmpty()) {
			throw new EntityNotFoundException("Cliente não localizado!");
		}
		Cliente cliente = new Cliente();
		
		if(movimentacoesOld != null) {
			switch (movimentacoesOld.tipo) {
			//Fez:
			case "Debito":
			case "debito":
			case "débito":
			case "Débito":
			case "saque":
			case "Saque":
				
				cliente.setId(movimentacoesOld.cliente.getId());
				cliente.setNome(clientebyId.get().getNome());
				cliente.setEndereco(clientebyId.get().getEndereco());
				movimentacoesRepo.save(movimentacoes);
				SaldoHistorico saldoHist = new SaldoHistorico();
				saldoHist.setCliente(clientebyId.get());
				saldoHist.setValor(clientebyId.get().getSaldo());
				saldoHist.setMovimentacoes(movimentacoes);
				saldoHist.setData(LocalDateTime.now());
				saldoRepo.save(saldoHist);
				var saldo = (clientebyId.get().getSaldo().add(movimentacoesOld.valor));
				cliente.setSaldo(saldo);
				clienteRepo.save(cliente);
				break;
				//Recebeu:
			case "Credito":
			case "credito":
			case "Crédito":
			case "crédito":
			case "Depósito":
			case "depósito":
			case "Deposito":
			case "deposito":
				cliente.setId(movimentacoesOld.cliente.getId());
				cliente.setNome(clientebyId.get().getNome());
				cliente.setEndereco(clientebyId.get().getEndereco());
				movimentacoesRepo.save(movimentacoes);
				
				SaldoHistorico saldoHistCredito = new SaldoHistorico();
				saldoHistCredito.setCliente(clientebyId.get());
				saldoHistCredito.setValor(clientebyId.get().getSaldo());
				saldoHistCredito.setMovimentacoes(movimentacoes);
				saldoHistCredito.setData(LocalDateTime.now());
				saldoRepo.save(saldoHistCredito);
				
				var saldoCredito = (clientebyId.get().getSaldo().subtract(movimentacoesOld.valor));
				cliente.setSaldo(saldoCredito);
				clienteRepo.save(cliente);
				break;
			case "Transferência":
			case "transferência":
			case "Transferencia":
			case "transferencia":
			case "Pix":
			case "pix":
				Movimentacoes movimentacao = new Movimentacoes();
				MovimentacoesCliente movimentacoesCliente = new MovimentacoesCliente();
				Optional<MovimentacoesCliente> byMovimentacaoAIdOrMovimentacaoBId = movimentacosClienteRepo.findByMovimentacaoAIdOrMovimentacaoBId(movimentacoesOld.id, movimentacoesOld.id);
				
				Cliente clienteB = byMovimentacaoAIdOrMovimentacaoBId.get().getClienteB();
				Cliente clienteA = byMovimentacaoAIdOrMovimentacaoBId.get().getClienteA();
				
				if(clienteB.getId() != null) {
					Optional<Cliente> clientebyBId = clienteRepo.findById(clienteB.getId());
					if(clientebyBId.isEmpty()) {
						throw new EntityNotFoundException("Cliente B não localizado!");
					}
					
					cliente.setId(movimentacoes.cliente.getId());
					cliente.setNome(clienteA.getNome());
					cliente.setEndereco(clienteA.getEndereco());
					movimentacoesRepo.save(movimentacoes);
					
					SaldoHistorico saldoHistClienteA = new SaldoHistorico();
					saldoHistClienteA.setCliente(clienteA);
					saldoHistClienteA.setValor(clienteA.getSaldo());
					saldoHistClienteA.setMovimentacoes(movimentacoes);
					saldoHistClienteA.setData(LocalDateTime.now());
					saldoRepo.save(saldoHistClienteA);
					
					var saldoDebito = (clienteA.getSaldo().add(movimentacoes.valor));
					cliente.setSaldo(saldoDebito);
					clienteRepo.save(cliente);
					
					
					movimentacao.setCliente(clientebyBId.get());
					movimentacao.setTipo(movimentacoesOld.getTipo());
					movimentacao.setData(movimentacoesOld.getData());
					movimentacao.setValor(movimentacoesOld.getValor());
					movimentacao.setCategoria(movimentacoesOld.getCategoria());
					movimentacao.setMoeda(movimentacoesOld.getMoeda());
					clienteB.setId(movimentacao.cliente.getId());
					clienteB.setNome(clientebyBId.get().getNome());
					clienteB.setEndereco(clientebyBId.get().getEndereco());
					movimentacoesRepo.save(movimentacao);
					
					SaldoHistorico saldoHistClienteB = new SaldoHistorico();
					saldoHistClienteB.setCliente(clientebyBId.get());
					saldoHistClienteB.setValor(clientebyBId.get().getSaldo());
					saldoHistClienteB.setMovimentacoes(movimentacao);
					saldoHistClienteB.setData(LocalDateTime.now());
					saldoRepo.save(saldoHistClienteB);
					
					var saldoCreditoClienteB = (clientebyBId.get().getSaldo().add(movimentacoes.valor));				
					clienteB.setSaldo(saldoCreditoClienteB);
					clienteRepo.save(clienteB);
					
					
					movimentacoesCliente.setClienteA(cliente);
					movimentacoesCliente.setClienteB(clienteB);
					movimentacoesCliente.setMovimentacaoA(movimentacoesOld);
					movimentacoesCliente.setMovimentacaoB(movimentacao);
					movimentacosClienteRepo.save(movimentacoesCliente);
				}
			break;
			default:
				throw new MovimentacoesException("Tipo de movimentação incorreta");
			}
		}
		
		switch (movimentacoes.tipo) {
		//Fez:
		case "Debito":
		case "debito":
		case "débito":
		case "Débito":
		case "saque":
		case "Saque":
			cliente.setId(movimentacoes.cliente.getId());
			cliente.setNome(clientebyId.get().getNome());
			cliente.setEndereco(clientebyId.get().getEndereco());
			movimentacoesRepo.save(movimentacoes);
			SaldoHistorico saldoHist = new SaldoHistorico();
			saldoHist.setCliente(cliente);
			saldoHist.setValor(cliente.getSaldo());
			saldoHist.setMovimentacoes(movimentacoes);
			saldoHist.setData(LocalDateTime.now());
			saldoRepo.save(saldoHist);
			var saldo = (clientebyId.get().getSaldo().subtract(movimentacoes.valor));
			cliente.setSaldo(saldo);
			clienteRepo.save(cliente);
			break;
			//Recebeu:
		case "Credito":
		case "credito":
		case "Crédito":
		case "crédito":
		case "Depósito":
		case "depósito":
		case "Deposito":
		case "deposito":
			cliente.setId(movimentacoes.cliente.getId());
			cliente.setNome(clientebyId.get().getNome());
			cliente.setEndereco(clientebyId.get().getEndereco());
			movimentacoesRepo.save(movimentacoes);
			SaldoHistorico saldoHistCredito = new SaldoHistorico();
			saldoHistCredito.setCliente(cliente);
			saldoHistCredito.setValor(cliente.getSaldo());
			saldoHistCredito.setMovimentacoes(movimentacoes);
			saldoHistCredito.setData(LocalDateTime.now());
			saldoRepo.save(saldoHistCredito);
			var saldoCredito = (clientebyId.get().getSaldo().add(movimentacoes.valor));
			cliente.setSaldo(saldoCredito);
			clienteRepo.save(cliente);
			
			break;
		case "Transferência":
		case "transferência":
		case "Transferencia":
		case "transferencia":
		case "Pix":
		case "pix":
			Movimentacoes movimentacao = new Movimentacoes();
			Cliente clienteB = new Cliente();
			MovimentacoesCliente movimentacoesClienteB = new MovimentacoesCliente();
			
			if(clienteBId != null) {
				Optional<Cliente> clientebyBId = clienteRepo.findById(clienteBId);
				if(clientebyBId.isEmpty()) {
					throw new EntityNotFoundException("Cliente B não localizado!");
				}
				
				cliente.setId(movimentacoes.cliente.getId());
				cliente.setNome(clientebyId.get().getNome());
				cliente.setEndereco(clientebyId.get().getEndereco());
				movimentacoesRepo.save(movimentacoes);

				SaldoHistorico saldoHistClienteA = new SaldoHistorico();
				saldoHistClienteA.setCliente(clientebyId.get());
				saldoHistClienteA.setValor(clientebyId.get().getSaldo());
				saldoHistClienteA.setMovimentacoes(movimentacoes);
				saldoHistClienteA.setData(LocalDateTime.now());
				saldoRepo.save(saldoHistClienteA);
				
				var saldoDebito = (clientebyId.get().getSaldo().subtract(movimentacoes.valor));
				cliente.setSaldo(saldoDebito);
				clienteRepo.save(cliente);
				
				
				movimentacao.setCliente(clientebyBId.get());
				movimentacao.setTipo(movimentacoes.getTipo());
				movimentacao.setData(movimentacoes.getData());
				movimentacao.setValor(movimentacoes.getValor());
				movimentacao.setCategoria(movimentacoes.getCategoria());
				movimentacao.setMoeda(movimentacoes.getMoeda());
				clienteB.setId(movimentacao.cliente.getId());
				clienteB.setNome(clientebyBId.get().getNome());
				clienteB.setEndereco(clientebyBId.get().getEndereco());
				
				movimentacoesRepo.save(movimentacao);
				
				SaldoHistorico saldoHistClienteB = new SaldoHistorico();
				saldoHistClienteB.setCliente(clientebyBId.get());
				saldoHistClienteB.setValor(clientebyBId.get().getSaldo());
				saldoHistClienteB.setMovimentacoes(movimentacao);
				saldoHistClienteB.setData(LocalDateTime.now());
				saldoRepo.save(saldoHistClienteB);
				
				var saldoCreditoClienteB = (clientebyBId.get().getSaldo().add(movimentacoes.valor));				
				clienteB.setSaldo(saldoCreditoClienteB);
				clienteRepo.save(clienteB);
				
				movimentacoesClienteB.setClienteA(cliente);
				movimentacoesClienteB.setClienteB(clienteB);
				movimentacoesClienteB.setMovimentacaoA(movimentacoes);
				movimentacoesClienteB.setMovimentacaoB(movimentacao);
				movimentacosClienteRepo.save(movimentacoesClienteB);
			}
		break;
		default:
			throw new MovimentacoesException("Tipo de movimentação incorreta");
		}
		
	}
	
	private void atualizarSaldoClienteDelete(Long movimentacaoId) throws MovimentacoesException {
		
		Optional<MovimentacoesCliente> byMovimentacao = movimentacosClienteRepo.findByMovimentacaoAIdOrMovimentacaoBId(movimentacaoId, movimentacaoId);
		
		MovimentacoesCliente byMovimentacaoAIdOrMovimentacaoBId = byMovimentacao.get();
		
		Optional<Cliente> clienteBbyId = java.util.Optional.empty();
		Optional<Movimentacoes> movimentacaoBbyId = java.util.Optional.empty();
		
		Optional<Cliente> clienteAbyId = clienteRepo.findById(byMovimentacaoAIdOrMovimentacaoBId.getClienteA().getId());
		Optional<Movimentacoes> movimentacaoAbyId = movimentacaoRepo.findById(byMovimentacaoAIdOrMovimentacaoBId.getMovimentacaoA().getId());
		
		if(clienteAbyId.isEmpty()) {
			throw new EntityNotFoundException("Cliente A não localizado!");
		}
		
		if(movimentacaoAbyId.isEmpty()) {
			throw new EntityNotFoundException("Movimentacao A não localizado!");
		}
		
		if(null != byMovimentacaoAIdOrMovimentacaoBId.getClienteB()) {
			clienteBbyId = clienteRepo.findById(byMovimentacaoAIdOrMovimentacaoBId.getClienteB().getId());
			if(clienteBbyId.isEmpty()) {
				throw new EntityNotFoundException("Cliente B não localizado!");
			}
			movimentacaoBbyId = movimentacaoRepo.findById(byMovimentacaoAIdOrMovimentacaoBId.getMovimentacaoB().getId());
			if(movimentacaoBbyId.isEmpty()) {
				throw new EntityNotFoundException("Movimentacao B não localizado!");
			}
		}
		
		Cliente cliente = new Cliente();
		
		switch (movimentacaoAbyId.get().tipo) {
		//Fez:
		case "Debito":
		case "debito":
		case "débito":
		case "Débito":
		case "saque":
		case "Saque":
			cliente.setId(movimentacaoAbyId.get().cliente.getId());
			cliente.setNome(clienteAbyId.get().getNome());
			cliente.setEndereco(clienteAbyId.get().getEndereco());
			SaldoHistorico saldoHistDebito = new SaldoHistorico(0L, LocalDateTime.now(), clienteAbyId.get().getSaldo(), clienteAbyId.get(), movimentacaoAbyId.get());
			saldoRepo.save(saldoHistDebito);
			var saldo = clienteAbyId.get().getSaldo().add(movimentacaoAbyId.get().valor);
			cliente.setSaldo(saldo);
			clienteRepo.save(cliente);
			movimentacoesRepo.updateMovimentacaoAtivoById(false, movimentacaoAbyId.get().id);
			break;
			//Recebeu:
		case "Credito":
		case "credito":
		case "Crédito":
		case "crédito":
		case "Depósito":
		case "depósito":
		case "Deposito":
		case "deposito":
			cliente.setId(movimentacaoAbyId.get().cliente.getId());
			cliente.setNome(clienteAbyId.get().getNome());
			cliente.setEndereco(clienteAbyId.get().getEndereco());
			SaldoHistorico saldoHistCredito = new SaldoHistorico(0L, LocalDateTime.now(), clienteAbyId.get().getSaldo(), clienteAbyId.get(),
					movimentacaoAbyId.get());
			saldoRepo.save(saldoHistCredito);
			var saldoCredito = clienteAbyId.get().getSaldo().subtract(movimentacaoAbyId.get().valor);
			cliente.setSaldo(saldoCredito);
			clienteRepo.save(cliente);
			movimentacoesRepo.updateMovimentacaoAtivoById(false, movimentacaoAbyId.get().id);
			break;
		case "Transferência":
		case "transferência":
		case "Transferencia":
		case "transferencia":
		case "Pix":
		case "pix":
			MovimentacoesCliente movimentacoesCliente = new MovimentacoesCliente();
			SaldoHistorico saldoHistClienteA = new SaldoHistorico(clienteAbyId.get().getId(), LocalDateTime.now(), 
					clienteAbyId.get().getSaldo(), clienteAbyId.get(), movimentacaoAbyId.get());
			saldoRepo.save(saldoHistClienteA);
			SaldoHistorico saldoHistClienteB = new SaldoHistorico(clienteBbyId.get().getId(), LocalDateTime.now(), clienteBbyId.get().getSaldo(), 
					clienteBbyId.get(), movimentacaoBbyId.get());
			saldoRepo.save(saldoHistClienteB);
			
			var saldoCreditoA = clienteAbyId.get().getSaldo().add(movimentacaoAbyId.get().valor);
			var saldoDebitoB = (clienteBbyId.get().getSaldo().subtract(movimentacaoBbyId.get().valor));
			clienteAbyId.get().setSaldo(saldoCreditoA);
			clienteBbyId.get().setSaldo(saldoDebitoB);
			clienteRepo.save(clienteAbyId.get());
			clienteRepo.save(clienteBbyId.get());
			movimentacoesRepo.save(movimentacaoAbyId.get());
			movimentacoesRepo.save(movimentacaoBbyId.get());
			
			
			
			movimentacoesCliente.setClienteA(clienteAbyId.get());
			movimentacoesCliente.setClienteB(clienteBbyId.get());
			movimentacoesCliente.setMovimentacaoA(movimentacaoAbyId.get());
			movimentacoesCliente.setMovimentacaoB(movimentacaoBbyId.get());
			movimentacosClienteRepo.save(movimentacoesCliente);
			movimentacoesRepo.updateMovimentacaoAtivoById(false, movimentacaoAbyId.get().id);
			movimentacoesRepo.updateMovimentacaoAtivoById(false, movimentacaoBbyId.get().id);
			return;
		default:
			throw new MovimentacoesException("Tipo de movimentação incorreta");
		}
		
	}

	public Optional<Movimentacoes> buscarMovimentacaoPeloId(Long movimentacaoId) throws MovimentacoesException {

		Optional<Movimentacoes> movimentacao = java.util.Optional.empty();
		try {
			movimentacao = movimentacoesRepo.findById(movimentacaoId);
			if(movimentacao.isEmpty()) {
				throw new EntityNotFoundException("Movimentacao não localizada!");
			}
			return movimentacao;
		} catch (Exception e) {
			log.error("Erro para localizar movimentacao: " + e.getMessage());
			throw new MovimentacoesException("Erro para localizar movimentação: " + e.getMessage());
		}
	}

	public Movimentacoes atualizar(Movimentacoes movimentacao, Long clienteBId) throws MovimentacoesException {

		try {
			Optional<Movimentacoes> movimentacaobyIdOld = movimentacoesRepo.findById(movimentacao.getId());
			if (movimentacaobyIdOld.isPresent()) {
				this.atualizarSaldoCliente(movimentacao, clienteBId, movimentacaobyIdOld.get());
//				movimentacoesRepo.save(movimentacao);
				log.info("Movimentacao atualizado com sucesso!");
				return movimentacao;
			} else {
				log.info("movimentacao não localizada!");
				throw new EntityNotFoundException("Movimentacao não localizada!");
			}
		} catch (Exception e) {
			log.error("Erro para atualizar movimentacao: " + e.getMessage());
			throw new MovimentacoesException("Erro para atualizar movimentação: " + e.getMessage());
		}

	}

	@Transient
	public void removerPeloId(Long movimentacaoId) throws MovimentacoesException {

		try {
			boolean exists = movimentacoesRepo.existsById(movimentacaoId);
			if (exists) {
				this.atualizarSaldoClienteDelete(movimentacaoId);
			} else {
				log.info("Movimentacao não localizada !");
				throw new EntityNotFoundException("Movimentacao não localizada!");
			}
		} catch (Exception e) {
			log.error("Erro para remover Movimentacao: " + e.getMessage());
			throw new MovimentacoesException("Erro para atualizar movimentação: " + e.getMessage());
		}
	}

	public MockMultipartFile relatorio(Long movimentacaoId, String dataInicio, String dataFim, 
			Long clienteId, Long moedaId, Long categoriaId, boolean ativo) {
		
		List<Movimentacoes> movimentacao = new ArrayList<Movimentacoes>();
		
		LocalDateTime dataInicioConvertida = null;
		LocalDateTime dataFimConvertida = null;
		
		   if (!StringUtils.isEmpty(dataInicio) && !StringUtils.isEmpty(dataFim)) {
		        try {
		            dataInicioConvertida = LocalDateTime.parse(dataInicio, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		            dataFimConvertida = LocalDateTime.parse(dataFim, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		        } catch (DateTimeParseException e) {
		            throw new IllegalArgumentException("Data inválida. O formato deve ser 'yyyy-MM-dd HH:mm:ss'.");
		        }
		    }else if (!StringUtils.isEmpty(dataInicio)) {
		    	try {
		            dataInicioConvertida = LocalDateTime.parse(dataInicio, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		        } catch (DateTimeParseException e) {
		            throw new IllegalArgumentException("Data inválida. O formato deve ser 'yyyy-MM-dd HH:mm:ss'.");
		        }
           } else if (!StringUtils.isEmpty(dataFim)) {
           	try {
		            dataFimConvertida = LocalDateTime.parse(dataFim, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		        } catch (DateTimeParseException e) {
		            throw new IllegalArgumentException("Data inválida. O formato deve ser 'yyyy-MM-dd HH:mm:ss'.");
		        }
           }
		
			Specification<Movimentacoes> spec = 
					Specification.where(MovimentacaoSpecification.byId(movimentacaoId))
					.and(MovimentacaoSpecification.byDataBetween(dataInicioConvertida, dataFimConvertida))
					.and(MovimentacaoSpecification.byCliente(clienteId))
					.and(MovimentacaoSpecification.byMoeda(moedaId))
					.and(MovimentacaoSpecification.byCategoria(categoriaId)
					.and(MovimentacaoSpecification.byAtivo(ativo)));
		
		movimentacao = movimentacoesRepo.findAll(spec);
		
		FileOutputStream arquivo = null;
		File arquivoExcel = null;
		byte[] bytes = null;
		MockMultipartFile mockMultipartFile = null;
		if(!movimentacao.isEmpty()) {
			try {
				Workbook planilha;
				
				planilha = new XSSFWorkbook();
				
				Sheet abaMovimentacao = planilha.createSheet("movimentacoes");
				
				int linhaNum = 1;
				int celulaNum = 0;
				
				Row linhaCabeçalho = abaMovimentacao.createRow(0);
				
				Cell celulaA = linhaCabeçalho.createCell(0);
				celulaA.setCellValue((String)"ID");
				
				Cell celulaB = linhaCabeçalho.createCell(1);
				celulaB.setCellValue((String)"Data");
				
				Cell celulaC = linhaCabeçalho.createCell(2);
				celulaC.setCellValue((String)"Nome do cliente");
				
				Cell celulaD = linhaCabeçalho.createCell(3);
				celulaD.setCellValue((String)"Valor");
				
				Cell celulaE = linhaCabeçalho.createCell(4);
				celulaE.setCellValue((String)"Moeda");
				
				Cell celulaF = linhaCabeçalho.createCell(5);
				celulaF.setCellValue((String)"Tipo");
				
		        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		        DateTimeFormatter outputFormatterTaxaDeCambio = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				
				for (Iterator iterator = movimentacao.iterator(); iterator.hasNext();) {
					Movimentacoes movimentacao2 = (Movimentacoes) iterator.next();
					DetalhesMovimentacao detalhes = new DetalhesMovimentacao(movimentacao2);

					Row linha = abaMovimentacao.createRow(linhaNum++);
					
					String dataConvertidaTaxaDeCambio = detalhes.data().format(outputFormatterTaxaDeCambio);
					
					if(moedaId != null && moedaId != 0) {
						if(!detalhes.moeda().nome.equalsIgnoreCase(Long.toString(moedaId))) {
							TaxaCambio pegarTaxaCambioHistorico = pegarTaxaCambioHistorico(dataConvertidaTaxaDeCambio, 
									detalhes.moeda().id, 
									detalhes.moeda().nome
									);
							int cambioCabecalho = 5;
							
							for (Entry<String, BigDecimal> entry : pegarTaxaCambioHistorico.rates().entrySet()) {
								
								cambioCabecalho++;
								Cell celulaG = linhaCabeçalho.createCell(cambioCabecalho);
								celulaG.setCellValue((String)"Cambio");
								linha.createCell(cambioCabecalho).setCellValue((String) entry.getKey());
								
								cambioCabecalho++;
								Cell celulaH = linhaCabeçalho.createCell(cambioCabecalho);
								celulaH.setCellValue((String)"Taxa");
								linha.createCell(cambioCabecalho).setCellValue((String) entry.getValue().toString());
								
								cambioCabecalho++;
								Cell celulaI = linhaCabeçalho.createCell(cambioCabecalho);
								celulaI.setCellValue((String)"Valor convertido");
								BigDecimal valorConvertido = (detalhes.valor().multiply(entry.getValue()));
								linha.createCell(cambioCabecalho).setCellValue((String) valorConvertido.toString());
								
								
							}
							
						}
					} else {
						TaxaCambio pegarTaxaCambioHistorico = pegarTaxaCambioHistorico(dataConvertidaTaxaDeCambio, detalhes.moeda().id, 
								detalhes.moeda().nome);
						int cambioCabecalho = 5;
						
						for (Entry<String, BigDecimal> entry : pegarTaxaCambioHistorico.rates().entrySet()) {
							
							cambioCabecalho++;
							Cell celulaG = linhaCabeçalho.createCell(cambioCabecalho);
							celulaG.setCellValue((String)"Categoria");
							linha.createCell(cambioCabecalho).setCellValue((String) entry.getKey());
							
							cambioCabecalho++;
							Cell celulaH = linhaCabeçalho.createCell(cambioCabecalho);
							celulaH.setCellValue((String)"Cambio");
							linha.createCell(cambioCabecalho).setCellValue((String) entry.getKey());
							
							cambioCabecalho++;
							Cell celulaI = linhaCabeçalho.createCell(cambioCabecalho);
							celulaI.setCellValue((String)"Taxa");
							linha.createCell(cambioCabecalho).setCellValue((String) entry.getValue().toString());
							
							cambioCabecalho++;
							Cell celulaJ = linhaCabeçalho.createCell(cambioCabecalho);
							celulaJ.setCellValue((String)"Valor convertido");
							BigDecimal valorConvertido = (detalhes.valor().multiply(entry.getValue()));
							linha.createCell(cambioCabecalho).setCellValue((String) valorConvertido.toString());
							
							
						}
					}
					
					
					linha.createCell(0).setCellValue((Long) detalhes.id());
					
					String dataConvertidaExcel = detalhes.data().format(outputFormatter);
					
					linha.createCell(1).setCellValue((String) dataConvertidaExcel);
					
					linha.createCell(2).setCellValue((String) detalhes.cliente().getNome());
					
					linha.createCell(3).setCellValue((String) detalhes.valor().toString());
					
					linha.createCell(4).setCellValue((String) detalhes.moeda().getNome());
					
					linha.createCell(5).setCellValue((String) detalhes.tipo());
					
					linha.createCell(6).setCellValue((String) detalhes.categoria().nome);
				}
				
				abaMovimentacao.createFreezePane(0, 1);
				
				arquivoExcel = new File("movimentacoes_" + 
				LocalDateTime.now().getYear() +
				LocalDateTime.now().getMonthValue() +
				LocalDateTime.now().getDayOfMonth() +
				LocalDateTime.now().getHour() +
				LocalDateTime.now().getMinute() 			
				+".xlsx");
				arquivo = new FileOutputStream(arquivoExcel);
				planilha.write(arquivo);
				
				FileInputStream fis = new FileInputStream(arquivoExcel);
		        bytes = new byte[(int) arquivoExcel.length()];
		        
		        mockMultipartFile = new MockMultipartFile(arquivoExcel.getName(), arquivoExcel.getName(), 
		        		"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", bytes);
		        
		        fis.read(bytes);
		        fis.close();
				
				arquivo.close();
				planilha.close();
				
			} catch (Exception e) {
				log.error("Erro na geração do relatório de movimentacoes " + e.getMessage());
			} finally {
				try {
					arquivo.close();
				} catch (IOException e) {
					log.error("Erro na geração do relatório de movimentacoes " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		
        return mockMultipartFile;
	}
	
	public TaxaCambio pegarTaxaCambio() {
		
		RestTemplate restTemplate = new RestTemplate();
		
		ResponseEntity<TaxaCambio> response = restTemplate.getForEntity(URI
				.create(exchangeApi+"latest"+ "?"+"access_key="+exchangeApiAcessKey), TaxaCambio.class);
		TaxaCambio body = response.getBody();
		
		return body;

	}
	
	public TaxaCambio pegarTaxaCambioHistorico(String data, Long moeda, String moedaBase) {
		
		RestTemplate restTemplate = new RestTemplate();
		
		Optional<Moeda> byId = moedaRepo.findById(moeda);
		String moedaNome = "";
		
		if(byId.isEmpty()) {
			List<Moeda> moedas = new ArrayList<Moeda>();
			for(int m = 0; m < moedas.size(); m++) {
				moedaNome += moedas.get(m) + ",";					
			}
		}
		
		ResponseEntity<TaxaCambio> response = restTemplate.getForEntity(URI
				.create(exchangeApi+"historical/"+data+ ".json?"+"app_id="+exchangeApiAcessKey+"&base=USD"/*+ moedaNome.toUpperCase()*/), TaxaCambio.class);
		TaxaCambio body = response.getBody();
		
		return body;

	}

	public Page<DetalhesMovimentacao> relatorioResumo(Pageable p, Long movimentacaoId, String dataInicio,String dataFim, Long clienteId, Long moedaId,
			Long categoriaId, boolean ativo) {
		
		LocalDateTime dataInicioConvertida = null;
		LocalDateTime dataFimConvertida = null;
		
		   if (!StringUtils.isEmpty(dataInicio) && !StringUtils.isEmpty(dataFim)) {
		        try {
		            dataInicioConvertida = LocalDateTime.parse(dataInicio, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		            dataFimConvertida = LocalDateTime.parse(dataFim, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		        } catch (DateTimeParseException e) {
		            throw new IllegalArgumentException("Data inválida. O formato deve ser 'yyyy-MM-dd HH:mm:ss'.");
		        }
		    }else if (!StringUtils.isEmpty(dataInicio)) {
		    	try {
		            dataInicioConvertida = LocalDateTime.parse(dataInicio, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		        } catch (DateTimeParseException e) {
		            throw new IllegalArgumentException("Data inválida. O formato deve ser 'yyyy-MM-dd HH:mm:ss'.");
		        }
            } else if (!StringUtils.isEmpty(dataFim)) {
            	try {
		            dataFimConvertida = LocalDateTime.parse(dataFim, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		        } catch (DateTimeParseException e) {
		            throw new IllegalArgumentException("Data inválida. O formato deve ser 'yyyy-MM-dd HH:mm:ss'.");
		        }
            }
		
		Specification<Movimentacoes> spec = 
				Specification.where(MovimentacaoSpecification
						.byId(movimentacaoId))
				.and(MovimentacaoSpecification.byDataBetween(dataInicioConvertida, dataFimConvertida))
				.and(MovimentacaoSpecification.byCliente(clienteId))
				.and(MovimentacaoSpecification.byMoeda(moedaId))
				.and(MovimentacaoSpecification.byCategoria(categoriaId)
				.and(MovimentacaoSpecification.byAtivo(ativo)));
		
		Page<Movimentacoes> movimentacoes = movimentacoesRepo.findAll(spec, p);			
		
		if(movimentacoes.isEmpty()) {
			throw new EntityNotFoundException("Nenhuma movimentação encontrada com os dados fornecidos");
		} else {
			for (Movimentacoes movimentacao : movimentacoes) {
				String dataConvertidaTaxaDeCambio = movimentacao.getData().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				TaxaCambio pegarTaxaCambioHistorico = pegarTaxaCambioHistorico(dataConvertidaTaxaDeCambio, movimentacao.getMoeda().id, 
						movimentacao.getMoeda().nome);
				
				movimentacao.setCambio(pegarTaxaCambioHistorico);
					
			}
			Page<DetalhesMovimentacao> detalhes = movimentacoes.map(m -> new DetalhesMovimentacao(m));
			return detalhes;
		}
		
	}

	public List<Movimentacoes> buscarMovimentacaoPeloIdMock() {
		RestTemplate restTemplate = new RestTemplate();
		
//		ResponseEntity<JsonArray> response = restTemplate.getForEntity(URI
//				.create(mockApiBuscar), JsonArray.class);
//		JsonArray body = response.getBody();
//		List<Movimentacoes> movimentacoesList = restTemplate.getForObject(mockApiBuscar, String.class);
		String forObject = restTemplate.getForObject(mockApiBuscar, String.class);
		ObjectMapper obj = new ObjectMapper();
		List<Movimentacoes> movimentacoesList = null;
		try {
			JavaTimeModule module = new JavaTimeModule();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	        LocalDateTimeDeserializer deserializer = new LocalDateTimeDeserializer(formatter);
	        module.addDeserializer(LocalDateTime.class, deserializer);
	        obj.registerModule(module);
	        
			movimentacoesList = obj.readValue(forObject, new TypeReference<List<Movimentacoes>>() {});
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return movimentacoesList;
	}

}
