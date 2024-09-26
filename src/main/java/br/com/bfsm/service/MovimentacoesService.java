package br.com.bfsm.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.bfsm.domain.cliente.Cliente;
import br.com.bfsm.domain.movimentacao.Movimentacao;
import br.com.bfsm.repository.ClienteRepository;
import br.com.bfsm.repository.MovimentacoesRepository;

@Service
public class MovimentacoesService {

	@Autowired
	MovimentacoesRepository movimentacoesRepo;

	@Autowired
	ClienteRepository clienteRepo;

	private static final Logger log = LoggerFactory.getLogger(MovimentacoesService.class);

	public String salvarMovimentacao(Movimentacao movimentacoes) {

		String status = "";
		try {
			this.atualizarSaldoCliente(movimentacoes);
			movimentacoesRepo.save(movimentacoes);
			log.info("Movimentação cadastrada com sucesso!");
			status = "OK";
		} catch (Exception e) {
			log.error("Erro para cadastrar movimentação: " + e.getMessage());
			status = "NOK";
		}
		return status;
	}

	private void atualizarSaldoCliente(Movimentacao movimentacoes) {
		
		Optional<Cliente> clientebyId = clienteRepo.findById(movimentacoes.cliente.getId());
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

	public Optional<Movimentacao> buscarMovimentacaoPeloId(Long movimentacaoId) {

		Optional<Movimentacao> movimentacao = java.util.Optional.empty();
		try {
			movimentacao = movimentacoesRepo.findById(movimentacaoId);
			return movimentacao;
		} catch (Exception e) {
			log.error("Erro para localizar movimentacao: " + e.getMessage());
			return movimentacao;
		}
	}

	public String atualizar(Movimentacao movimentacao) {

		String status = "";
		try {
			boolean existsById = movimentacoesRepo.existsById(movimentacao.getId());
			if (existsById) {
				movimentacoesRepo.save(movimentacao);
				log.info("Movimentacao atualizado com sucesso!");
				status = "OK";
			} else {
				log.info("movimentacao não localizada!");
				status = "404";
			}
		} catch (Exception e) {
			log.error("Erro para atualizar movimentacao: " + e.getMessage());
			status = "NOK";
		}

		return status;
	}

	public String removerPeloId(Long movimentacaoId) {
		String status = "";

		try {
			boolean exists = movimentacoesRepo.existsById(movimentacaoId);
			if (exists) {
				movimentacoesRepo.deleteById(movimentacaoId);
				return status = "OK";
			} else {
				log.info("Movimentacao não localizada !");
				return status = "404";
			}
		} catch (Exception e) {
			log.error("Erro para remover Movimentacao: " + e.getMessage());
			return status = e.getMessage();
		}
	}

}
