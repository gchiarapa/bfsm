package br.com.bfsm.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
		movimentacoesRepo.save(movimentacoes);
		log.debug("Movimentação cadastrada com sucesso!");
		status = "OK";
		} catch (Exception e) {
			log.error("Erro para cadastrar movimentação: " + e.getMessage());
			status = "NOK";
		}
		return status;
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
				log.debug("Movimentacao atualizado com sucesso!");
				status = "OK";				
			} else {
				log.debug("movimentacao não localizada!");
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
		if(exists) {
			movimentacoesRepo.deleteById(movimentacaoId);
			return status = "OK";
		} else {
			log.debug("Movimentacao não localizada !");			
			return status = "404";
		}
		} catch (Exception e) {
			log.error("Erro para remover Movimentacao: " + e.getMessage());
			return status = e.getMessage();
		}
	}

}
