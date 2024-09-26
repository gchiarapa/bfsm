package br.com.bfsm.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.bfsm.domain.usuario.DetalhesUsuario;
import br.com.bfsm.domain.usuario.Usuario;
import br.com.bfsm.domain.usuario.UsuarioCadastro;
import br.com.bfsm.repository.UsuarioRepository;
import br.com.bfsm.usuario.DetalhesCadastroMassivo;
import br.com.bfsm.usuario.DetalhesUsuarioMassivo;

@Service
public class UsuarioService {
	
	private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);
	
	@Autowired
	UsuarioRepository usuarioRepo;
	
	public String salvarUsuario(Usuario usuario) {
		
		String status = "";
		try {
			log.info("Criando usuário: " + usuario.getLogin());
			usuarioRepo.save(usuario);
			status = "OK";
			log.info("usuario cadastrado com sucesso!");
		} catch (Exception e) {
			log.error("Erro para cadastrar usuario: " + e.getMessage());
			status = "NOK";
		}
		
		return status;
		
	}
	
	public UserDetails buscarUsuarioPeloLogin(String login) {
		
		UserDetails loginEncontrado = null;
		try {
			loginEncontrado = usuarioRepo.findByLogin(login);
			log.info("usuario cadastrado com sucesso!");
		} catch (Exception e) {
			log.error("Erro para procurar usuario: " + e.getMessage());
		}
		return loginEncontrado;
	}

	public Optional<Usuario> buscarUsuarioPeloId(Long usuarioId) {
		
		Optional<Usuario> usuario = java.util.Optional.empty();
		try {
			usuario = usuarioRepo.findById(usuarioId);
			return usuario;
		} catch (Exception e) {
			log.error("Erro para localizar usuario: " + e.getMessage());
			return usuario;
		}
	}

	public String removerPeloId(Long usuarioId) {
		
		String status = "";

		try {
			boolean exists = usuarioRepo.existsById(usuarioId);
			if (exists) {
				usuarioRepo.deleteById(usuarioId);
				return status = "OK";
			} else {
				log.info("cliente não localizado !");
				return status = "404";
			}
		} catch (Exception e) {
			log.error("Erro para remover cliente: " + e.getMessage());
			return status = e.getMessage();
		}
	}

	public String atualizar(Usuario usuario) {
		
		String status = "";
		try {
			boolean existsById = usuarioRepo.existsById(usuario.getId());
			if (existsById) {
				usuarioRepo.save(usuario);
				log.info("usuario atualizado com sucesso!");
				status = "OK";				
			} else {
				log.info("usuario não localizado!");
				status = "404";
			}
		} catch (Exception e) {
			log.error("Erro para atualizar usuario: " + e.getMessage());
			status = "NOK";
		}

		return status;
	}
	
	public ResponseEntity<DetalhesUsuario> cadastrarUsuario(UsuarioCadastro cadastroUsuario) {
		
		try {
			log.info("Verificando se o login " +cadastroUsuario.login() + " existe");
			UserDetails login = this.buscarUsuarioPeloLogin(cadastroUsuario.login());
			if(login == null) {
				Usuario novoUsuario = new Usuario(cadastroUsuario);
				novoUsuario.setLogin(cadastroUsuario.login());
				novoUsuario.setSenha(new BCryptPasswordEncoder().encode(cadastroUsuario.senha()));
				this.salvarUsuario(novoUsuario);			
				log.info("o login " + novoUsuario.getLogin() + " foi cadastrado");
				return ResponseEntity.ok(new DetalhesUsuario(novoUsuario));
			} else {
				log.info("o login " + cadastroUsuario.login() + " já existe" );
				return ResponseEntity.noContent().build();
			}
			
		} catch (Exception e) {
			log.error("Erro para cadastrar o login " + e.getMessage());
			return ResponseEntity.internalServerError().build();
		}
	}

	public DetalhesCadastroMassivo cadastrarUsuarioMassivo(List<Usuario> listUsuarios) {
		
		DetalhesCadastroMassivo cadastroMassivo = new DetalhesCadastroMassivo();
		List<DetalhesUsuarioMassivo> listaUsuario = new ArrayList<DetalhesUsuarioMassivo>();
		try {
			for (int i = 0; i < listUsuarios.size(); i++) {
				UsuarioCadastro cadastroUsuario = new UsuarioCadastro(listUsuarios.get(i).getLogin(), 
						listUsuarios.get(i).getSenha());
				ResponseEntity<DetalhesUsuario> cadastrarUsuario = this.cadastrarUsuario(cadastroUsuario);
				if(cadastrarUsuario.getStatusCodeValue() == 200) {
					DetalhesUsuarioMassivo detalhes = new DetalhesUsuarioMassivo();
					detalhes.setStatus("Login criado com sucesso");
					detalhes.setLogin(cadastrarUsuario.getBody().login());
					listaUsuario.add(detalhes);
					cadastroMassivo.setListaUsuario(listaUsuario);
				} else if(cadastrarUsuario.getStatusCodeValue() == 204) {
					DetalhesUsuarioMassivo detalhes = new DetalhesUsuarioMassivo();
					detalhes.setStatus("Login já existe");
					detalhes.setLogin(cadastroUsuario.login());
					listaUsuario.add(detalhes);
					cadastroMassivo.setListaUsuario(listaUsuario);
				}
			}
			ResponseEntity.ok().body(cadastroMassivo);
		} catch (Exception e) {
			log.error("Erro no cadastro massivo de usuario " + e.getMessage());
		}
		
		return cadastroMassivo;
		
	}

}
