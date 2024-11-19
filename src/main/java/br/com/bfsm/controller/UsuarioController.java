package br.com.bfsm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.bfsm.domain.usuario.AtualizaUsuario;
import br.com.bfsm.domain.usuario.DetalhesUsuario;
import br.com.bfsm.domain.usuario.Usuario;
import br.com.bfsm.domain.usuario.UsuarioCadastro;
import br.com.bfsm.infra.exception.ClienteException;
import br.com.bfsm.service.UsuarioService;
import br.com.bfsm.usuario.DetalhesCadastroMassivo;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;


@RestController
@RequestMapping(path = "/usuario")
@SecurityRequirement(name = "bearer-key") 
public class UsuarioController {
	
	@Autowired
	UsuarioService usuarioService;
	
	private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);
	
	@PostMapping("/cadastro")
	public ResponseEntity adicionar(@RequestBody @Valid UsuarioCadastro cadastroUsuario, UriComponentsBuilder uriBuilder) {
			
			ResponseEntity<DetalhesUsuario> cadastrarUsuario;
			try {
				cadastrarUsuario = usuarioService.cadastrarUsuario(cadastroUsuario);
				var uri = uriBuilder.path("/usuario/{id}").buildAndExpand(cadastrarUsuario.getBody().id()).toUri();
				return ResponseEntity.created(uri).body(new DetalhesUsuario(cadastrarUsuario));				
			} catch (ClienteException e) {
				ProblemDetail forStatusAndDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(409), "Login já cadastrado");
				return ResponseEntity.of(forStatusAndDetail).build();
			} catch (Exception e) {
				return ResponseEntity.internalServerError().build();
			}
	}
	
	@GetMapping()
	public ResponseEntity buscar(@RequestParam Long usuarioId, UriComponentsBuilder uriBuilder) {
		
		log.info("Iniciando busca do id: [id] " + usuarioId);
		
		try {
			Usuario buscarUsuarioPeloId = usuarioService.buscarUsuarioPeloId(usuarioId);
			var uri = uriBuilder.path("/usuario/{id}").buildAndExpand(buscarUsuarioPeloId.getId()).toUri();
			return ResponseEntity.created(uri).body(new DetalhesUsuario(buscarUsuarioPeloId));
		} catch (ClienteException e) {
			ProblemDetail forStatusAndDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), "Login não localizado");
			return ResponseEntity.of(forStatusAndDetail).build();
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
				
	}
	
	@DeleteMapping()
	public ResponseEntity remover(@RequestParam Long usuarioId) {
		
		log.info("Iniciando remocao do id: [id] " + usuarioId);
		
		String removerPeloId;
		try {
			removerPeloId = usuarioService.removerPeloId(usuarioId);
			log.info("remocao do id: [id] " + usuarioId + " efetuada com sucesso");
			return ResponseEntity.noContent().build();
		} catch (ClienteException e) {
			log.info("O id: [id] " + usuarioId + " não foi localizado");
			ProblemDetail forStatusAndDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), "Login não localizado");
			return ResponseEntity.of(forStatusAndDetail).build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
		
	}
	
	@PutMapping("/atualizar")
	public ResponseEntity atualizar(@RequestBody AtualizaUsuario usuarioAtualizacao, UriComponentsBuilder uriBuilder) {
//		log.info("Tentativa de adicionar cliente - Sessão: {}", SecurityContextHolder.getContext().getAuthentication());
		log.info("Valores recebidos: [Login] " + usuarioAtualizacao.login());
		
		Usuario usuario = new Usuario(usuarioAtualizacao);
		
		String status;
		try {
			status = usuarioService.atualizar(usuario);
			var uri = uriBuilder.path("/usuario/{id}").buildAndExpand(usuario.getId()).toUri();
			return ResponseEntity.created(uri).body(new DetalhesUsuario(usuario));
		} catch (ClienteException e) {
			ProblemDetail forStatusAndDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), "Login não localizado");
			return ResponseEntity.of(forStatusAndDetail).build();
		} catch (Exception e) {
			ProblemDetail forStatusAndDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), "Erro para atualizar: " + e.getMessage());
			return ResponseEntity.of(forStatusAndDetail).build();	
		}
		
	}
	
	@PostMapping("/cadastro/massivo")
	public ResponseEntity cadastroMassivo(@RequestParam("file") MultipartFile file) {
		
		DetalhesCadastroMassivo cadastrarUsuarioMassivo = usuarioService.cadastrarUsuarioMassivo(file);
		
		return ResponseEntity.ok(cadastrarUsuarioMassivo);
	}
	
	

}
