package br.com.bfsm.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.bfsm.model.Cliente;
import br.com.bfsm.model.Movimentacoes;
import br.com.bfsm.repository.ClienteRepository;
import br.com.bfsm.repository.MovimentacoesRepository;
import br.com.bfsm.service.MovimentacoesService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping(path = "/movimentacoes")
public class MovimentacoesController {
	
	@Autowired
	MovimentacoesService movimentacoesService;
	
	@Autowired
	ClienteRepository clienteRepo;
	
	private static final Logger log = LoggerFactory.getLogger(MovimentacoesController.class);
	
	@PostMapping(path = "/adicionar")
	public @ResponseBody String adicionarMovimentacoes(@RequestParam String tipo, 
			@RequestParam Date data, @RequestParam String valor, @RequestParam Integer idCliente) {
		
		log.debug("Valores recebidos: [Tipo] [Data] [Valor] [IdCliente]" + tipo, data, valor, idCliente);
		
		Movimentacoes movimentacoes = new Movimentacoes();
		Cliente cliente = new Cliente();
		cliente.setId(idCliente);
		
		movimentacoes.setTipo(tipo);
		movimentacoes.setData(data);
		movimentacoes.setValor(valor);
		movimentacoes.setCliente(cliente);
		
		String status = movimentacoesService.salvarMovimentacao(movimentacoes);
		
		return status;
		
		
	}

}
