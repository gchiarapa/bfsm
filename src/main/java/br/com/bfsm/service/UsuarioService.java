package br.com.bfsm.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.bfsm.model.Cliente;
import br.com.bfsm.model.Usuario;
import br.com.bfsm.repository.UsuarioRepository;

@Service
public class UsuarioService {
	
	private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);
	
	@Autowired
	UsuarioRepository usuarioRepo;
	
	public String salvarUsuario(Usuario usuario) {
		
		String status = "";
		try {
			log.debug("Criando usuário: " + usuario.getLogin());
			usuarioRepo.save(usuario);
			status = "OK";
			log.debug("usuario cadastrado com sucesso!");
		} catch (Exception e) {
			log.error("Erro para cadastrar usuario: " + e.getMessage());
			status = "NOK";
		}
		
		return status;
		
	}
	
	public Optional<Usuario> buscarUsuarioPeloLogin(String login) {
		
		Optional<Usuario> loginEncontrado = java.util.Optional.empty();
		try {
			loginEncontrado = usuarioRepo.buscarUsuarioPeloLogin(login);
			log.debug("usuario cadastrado com sucesso!");
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
				log.debug("cliente não localizado !");
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
				log.debug("usuario atualizado com sucesso!");
				status = "OK";				
			} else {
				log.debug("usuario não localizado!");
				status = "404";
			}
		} catch (Exception e) {
			log.error("Erro para atualizar usuario: " + e.getMessage());
			status = "NOK";
		}

		return status;
	}

}
