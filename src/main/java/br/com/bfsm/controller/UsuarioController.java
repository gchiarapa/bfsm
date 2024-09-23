package br.com.bfsm.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.bfsm.domain.usuario.AtualizaUsuario;
import br.com.bfsm.domain.usuario.DetalhesUsuario;
import br.com.bfsm.domain.usuario.Usuario;
import br.com.bfsm.domain.usuario.UsuarioCadastro;
import br.com.bfsm.service.UsuarioService;
import jakarta.validation.Valid;


@RestController
@RequestMapping(path = "/usuario")
public class UsuarioController {
	
	@Autowired
	UsuarioService usuarioService;
	
	private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);
	
	@PostMapping("/adicionar")
	public ResponseEntity adicionar(@RequestBody @Valid UsuarioCadastro cadastroUsuario, UriComponentsBuilder uriBuilder) {
			
		try {
			log.debug("Verificando se o login " +cadastroUsuario.login() + " existe" );
			UserDetails login = usuarioService.buscarUsuarioPeloLogin(cadastroUsuario.login());
			if(login == null) {
				Usuario novoUsuario = new Usuario(cadastroUsuario);
				novoUsuario.setLogin(cadastroUsuario.login());
				novoUsuario.setSenha(new BCryptPasswordEncoder().encode(cadastroUsuario.senha()));
				usuarioService.salvarUsuario(novoUsuario);
				var uri = uriBuilder.path("/usuario/{id}").buildAndExpand(novoUsuario.getId()).toUri();
				log.debug("o login " + novoUsuario.getLogin() + " foi cadastrado");
				return ResponseEntity.created(uri).body(new DetalhesUsuario(novoUsuario));
			} else {
				log.debug("o login " + cadastroUsuario.login() + " já existe" );
				return ResponseEntity.noContent().build();
			}
		} catch (Exception e) {
			log.error("Erro para cadastrar o login " + e.getMessage());
			return ResponseEntity.internalServerError().build();
		}
		
	}
	
	@GetMapping("{id}")
	public ResponseEntity buscar(@RequestParam Long usuarioId) {
		
		log.info("Iniciando busca do id: [id] " + usuarioId);
		
		Optional<Usuario> buscarUsuarioPeloId = usuarioService.buscarUsuarioPeloId(usuarioId);
		
		if(buscarUsuarioPeloId.isPresent()) {
			log.info("O id: [id] " + usuarioId + " foi localizado");
			return ResponseEntity.ok().body(new DetalhesUsuario(buscarUsuarioPeloId.get()));
		} else {
			return ResponseEntity.notFound().build();
		}
		
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity remover(@RequestParam Long usuarioId) {
		
		log.debug("Iniciando remocao do id: [id] " + usuarioId);
		
		String removerPeloId = usuarioService.removerPeloId(usuarioId);
		
		if(removerPeloId == "OK") {
			log.debug("remocao do id: [id] " + usuarioId + " efetuada com sucesso");
			return ResponseEntity.noContent().build();
		} else if(removerPeloId == "404") {
			log.debug("O id: [id] " + usuarioId + " não foi localizado");
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.internalServerError().build();
		}
		
	}
	
	@PutMapping("/atualizar")
	public ResponseEntity putMethodName(@RequestBody AtualizaUsuario usuarioAtualizacao, UriComponentsBuilder uriBuilder) {
//		log.info("Tentativa de adicionar cliente - Sessão: {}", SecurityContextHolder.getContext().getAuthentication());
		log.debug("Valores recebidos: [Login] " + usuarioAtualizacao.login());
		
		Usuario usuario = new Usuario(usuarioAtualizacao);
		
		String status = usuarioService.atualizar(usuario);
		
		if(status == "OK") {
			var uri = uriBuilder.path("/usuario/{id}").buildAndExpand(usuario.getId()).toUri();
			return ResponseEntity.created(uri).body(new DetalhesUsuario(usuario));
		} else if(status == "404") {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.internalServerError().build();			
		}
	}
	

}
