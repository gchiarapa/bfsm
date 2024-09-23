package br.com.bfsm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.bfsm.domain.usuario.DadosAutenticacao;
import br.com.bfsm.domain.usuario.Usuario;
import br.com.bfsm.infra.security.TokenService;
import br.com.bfsm.infra.security.TokenUsuario;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/login")
public class AutenticacaoController {
	
	private static final Logger log = LoggerFactory.getLogger(AutenticacaoController.class);
	
	@Autowired
	private AuthenticationManager manager;
	
	@Autowired
	private TokenService tokenService;
	
	@PostMapping
	public ResponseEntity autenticar(@RequestBody @Valid DadosAutenticacao dados) {
		
		var authUsuario = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());
		
		var authentication =  manager.authenticate(authUsuario);
		
		String token = tokenService.gerarToken((Usuario) authentication.getPrincipal());
		
		return ResponseEntity.ok(new TokenUsuario(token));
		
	}

}
