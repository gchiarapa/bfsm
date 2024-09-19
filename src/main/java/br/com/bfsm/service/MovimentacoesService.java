package br.com.bfsm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.bfsm.model.Movimentacoes;
import br.com.bfsm.repository.ClienteRepository;
import br.com.bfsm.repository.MovimentacoesRepository;

@Service
public class MovimentacoesService {
	
	@Autowired
	MovimentacoesRepository movimentacoesRepo;
	
	@Autowired
	ClienteRepository clienteRepo;
	
	private static final Logger log = LoggerFactory.getLogger(MovimentacoesService.class);
	
	public String salvarMovimentacao(Movimentacoes movimentacoes) {
		
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

}
