package br.com.bfsm.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.com.bfsm.domain.cambio.TaxaCambio;
import br.com.bfsm.domain.cliente.Cliente;
import br.com.bfsm.domain.movimentacao.Moeda;
import br.com.bfsm.domain.movimentacao.MovimentacaoSpecification;
import br.com.bfsm.domain.movimentacao.Movimentacoes;
import br.com.bfsm.infra.exception.MovimentacoesException;
import br.com.bfsm.repository.ClienteRepository;
import br.com.bfsm.repository.MovimentacoesRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;

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
	
	@Value("${api.exchange.accessKey}")
	private String exchangeApiAcessKey;

	private static final Logger log = LoggerFactory.getLogger(MovimentacoesService.class);

	public String salvarMovimentacao(Movimentacoes movimentacoes) throws MovimentacoesException {

		String status = "";
		try {
			this.atualizarSaldoCliente(movimentacoes);
			movimentacoesRepo.save(movimentacoes);
			log.info("Movimentação cadastrada com sucesso!");
			status = "OK";
		} catch (Exception e) {
			log.error("Erro para cadastrar movimentação: " + e.getMessage());
			throw new MovimentacoesException("Erro para cadastrar movimentação: " + e.getMessage());
		}
		return status;
	}

	private void atualizarSaldoCliente(Movimentacoes movimentacoes) {
		
		Optional<Cliente> clientebyId = clienteRepo.findById(movimentacoes.cliente.getId());
		if(clientebyId.isEmpty()) {
			throw new EntityNotFoundException("Cliente não localizado!");
		}
		Cliente cliente = new Cliente();
		
		switch (movimentacoes.tipo) {
		case "Debito":
		case "debito":
		case "débito":
		case "Débito":
		case "Pix":
		case "pix":
		case "Depósito":
		case "depósito":
		case "Deposito":
		case "deposito":
			var saldo = (Long.parseLong(clientebyId.get().getSaldo()) - Long.parseLong(movimentacoes.valor));
			cliente.setSaldo(Long.toString(saldo));
			cliente.setId(movimentacoes.cliente.getId());
			cliente.setNome(clientebyId.get().getNome());
			cliente.setEndereco(clientebyId.get().getEndereco());
			clienteRepo.save(cliente);
			break;
		case "Credito":
		case "credito":
		case "Crédito":
		case "crédito":
		case "saque":
		case "Saque":
			var saldoCredito = (Long.parseLong(clientebyId.get().getSaldo()) + Long.parseLong(movimentacoes.valor));
			cliente.setSaldo(Long.toString(saldoCredito));
			cliente.setId(movimentacoes.cliente.getId());
			cliente.setNome(clientebyId.get().getNome());
			cliente.setEndereco(clientebyId.get().getEndereco());
			clienteRepo.save(cliente);
			break;
		default:
			break;
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

	public Movimentacoes atualizar(Movimentacoes movimentacao) throws MovimentacoesException {

		try {
			boolean existsById = movimentacoesRepo.existsById(movimentacao.getId());
			if (existsById) {
				movimentacoesRepo.save(movimentacao);
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

	public void removerPeloId(Long movimentacaoId) throws MovimentacoesException {

		try {
			boolean exists = movimentacoesRepo.existsById(movimentacaoId);
			if (exists) {
				movimentacoesRepo.deleteById(movimentacaoId);
			} else {
				log.info("Movimentacao não localizada !");
				throw new EntityNotFoundException("Movimentacao não localizada!");
			}
		} catch (Exception e) {
			log.error("Erro para remover Movimentacao: " + e.getMessage());
			throw new MovimentacoesException("Erro para atualizar movimentação: " + e.getMessage());
		}
	}

	public MockMultipartFile relatorio(Long movimentacaoId, String data, Long clienteId, String moeda) {
		
		List<Movimentacoes> movimentacao = new ArrayList<Movimentacoes>();
		
		LocalDateTime dataConvertida = null;
		
		if(!StringUtils.isEmpty(data) && clienteId != null) {
			dataConvertida = LocalDateTime.parse(data, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		}
		
		Specification<Movimentacoes> spec = Specification.where(MovimentacaoSpecification.byId(movimentacaoId))
				.and(MovimentacaoSpecification.byData(dataConvertida))
				.and(MovimentacaoSpecification.byCliente(clienteId))
				.and(MovimentacaoSpecification.byMoeda(moeda));
		
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
				
		        DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		        DateTimeFormatter outputFormatterTaxaDeCambio = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				
				for (Iterator iterator = movimentacao.iterator(); iterator.hasNext();) {
					Movimentacoes movimentacao2 = (Movimentacoes) iterator.next();

					Row linha = abaMovimentacao.createRow(linhaNum++);
					
					LocalDateTime dateTimeExcel = LocalDateTime.parse(movimentacao2.getData().toString(), inputFormatter);
					
					String dataConvertidaTaxaDeCambio = dateTimeExcel.format(outputFormatterTaxaDeCambio);
					//TODO testar o relatório
					if(!movimentacao2.getMoeda().toString().equalsIgnoreCase(moeda)) {
						TaxaCambio pegarTaxaCambioHistorico = pegarTaxaCambioHistorico(dataConvertidaTaxaDeCambio, moeda, 
								movimentacao2.getMoeda().toString());
						int cambioCabecalho = 5;
						
						for (Entry<String, Double> entry : pegarTaxaCambioHistorico.rates().entrySet()) {
							
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
							Double valorConvertido = (Double.parseDouble(movimentacao2.getValor()) * entry.getValue());
							linha.createCell(cambioCabecalho).setCellValue((String) valorConvertido.toString());
							
							
						}
						
					}
					
					
					linha.createCell(0).setCellValue((Long) movimentacao2.getId());
					
					String dataConvertidaExcel = dateTimeExcel.format(outputFormatter);
					
					linha.createCell(1).setCellValue((String) dataConvertidaExcel);
					
					linha.createCell(2).setCellValue((String) movimentacao2.getCliente().getNome());
					
					linha.createCell(3).setCellValue((String) movimentacao2.getValor());
					
					linha.createCell(4).setCellValue((String) movimentacao2.getMoeda().toString());
					
					linha.createCell(5).setCellValue((String) movimentacao2.getTipo());
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
	
	public TaxaCambio pegarTaxaCambioHistorico(String data, String moeda, String moedaBase) {
		
		RestTemplate restTemplate = new RestTemplate();
		
		if(StringUtils.isEmpty(moeda)) {
			moeda = "";
			Moeda[] moedas = Moeda.values();
			for(int m = 0; m < moedas.length; m++) {
				moeda += moedas[m].name() + ",";					
			}
		}
		
		ResponseEntity<TaxaCambio> response = restTemplate.getForEntity(URI
				.create(exchangeApi+data+ "?"+"access_key="+exchangeApiAcessKey+"&symbols="+ moeda.toUpperCase()), TaxaCambio.class);
		TaxaCambio body = response.getBody();
		
		return body;

	}

}
