package br.com.bfsm.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.bfsm.domain.usuario.DetalhesUsuario;
import br.com.bfsm.domain.usuario.Usuario;
import br.com.bfsm.domain.usuario.UsuarioCadastro;
import br.com.bfsm.infra.exception.ClienteException;
import br.com.bfsm.repository.UsuarioRepository;
import br.com.bfsm.usuario.DetalhesCadastroMassivo;
import br.com.bfsm.usuario.DetalhesUsuarioMassivo;
import jakarta.validation.constraints.NotNull;

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

	public Usuario buscarUsuarioPeloId(Long usuarioId) throws ClienteException, Exception {
		
		Optional<Usuario> usuario = java.util.Optional.empty();
		
		try {
			usuario = usuarioRepo.findById(usuarioId);
			if(usuario.isPresent()) {
				log.info("O id: [id] " + usuarioId + " foi localizado");
				return usuario.get();
			} else {
				throw new ClienteException("Usuario não localizado!");
			}
				
		} catch (Exception e) {
			log.error("Erro para localizar usuario: " + e.getMessage());
			throw new Exception("Erro para buscar Usuario!");
		}
		
	}

	public String removerPeloId(Long usuarioId) throws ClienteException, Exception {
		
		String status = "";

		try {
			boolean exists = usuarioRepo.existsById(usuarioId);
			if (exists) {
				usuarioRepo.deleteById(usuarioId);
				log.info("remocao do id: [id] " + usuarioId + " efetuada com sucesso");
				return status = "OK";
			} else {
				log.info("Usuario não localizado !");
				throw new ClienteException("Usuario não localizado!");
			}
		} catch (Exception e) {
			log.error("Erro para remover usuario: " + e.getMessage());
			throw new Exception("Erro para remover Usuario!");
		}
		
	}

	public String atualizar(Usuario usuario) throws ClienteException, Exception {
		
		try {
			String status = "";
			boolean existsById = usuarioRepo.existsById(usuario.getId());
			if (existsById) {
				usuarioRepo.save(usuario);
				log.info("usuario atualizado com sucesso!");
				return status = "OK";				
			} else {
				log.info("usuario não localizado!");
				throw new ClienteException("Usuario não localizado!");
			}
		} catch (Exception e) {
			log.error("Erro para atualizar usuario: " + e.getMessage());
			throw new Exception("Erro para atualizar Usuario!");
		}
		
	}
	
	public ResponseEntity<DetalhesUsuario> cadastrarUsuario(UsuarioCadastro cadastroUsuario) throws ClienteException {
		
		log.info("Verificando se o login " +cadastroUsuario.login() + " existe");
		Optional<Usuario> login = usuarioRepo.findByLogin(cadastroUsuario.login());
		if(login.isEmpty()) {
			Usuario novoUsuario = new Usuario(cadastroUsuario);
			novoUsuario.setLogin(cadastroUsuario.login());
			novoUsuario.setSenha(new BCryptPasswordEncoder().encode(cadastroUsuario.senha()));
			this.salvarUsuario(novoUsuario);			
			log.info("o login " + novoUsuario.getLogin() + " foi cadastrado");
			return ResponseEntity.ok(new DetalhesUsuario(novoUsuario));
		} else {
			log.info("o login " + login.get().getLogin() + " já existe" );
			throw new ClienteException("Usuario já cadastrado!");
		}
		
	}

	public DetalhesCadastroMassivo cadastrarUsuarioMassivo(MultipartFile file) {
		
		DetalhesCadastroMassivo cadastroMassivo = new DetalhesCadastroMassivo();
		List<DetalhesUsuarioMassivo> listaDetalhesUsuario = new ArrayList<DetalhesUsuarioMassivo>();
		
		List<Usuario> listUsuarios = new ArrayList<Usuario>();
		InputStream is = null;
		
		try {
			is = file.getInputStream();
			Workbook workbook;
			
	        if (file.getOriginalFilename().endsWith(".xlsx")) {
	            workbook = new XSSFWorkbook(is);
	        } else if(file.getOriginalFilename().endsWith(".xls")) {
	            workbook = new HSSFWorkbook(is);
	        }else {
	            throw new IllegalArgumentException("Formato de arquivo inválido. Por favor, envie um arquivo do Excel.");
	        }
	        
			Sheet abaUsuario = workbook.getSheetAt(0);
			Iterator<Row> iterator = abaUsuario.iterator();
			
			while (iterator.hasNext()) {
				Row linha = iterator.next();
				
				Iterator<Cell> cellIterator = linha.cellIterator();
				
				Usuario usuario = new Usuario();
				listUsuarios.add(usuario);
				
				while (cellIterator.hasNext()) {
					Cell celula = (Cell) cellIterator.next();
					
					switch (celula.getColumnIndex()) {
					case 0:
						usuario.setLogin(celula.getStringCellValue());
						break;
					case 1:
						usuario.setSenha(celula.getStringCellValue());
						break;
					default:
						break;
					}
				}
				
			}
			if(listUsuarios.size() > 0) {
				try {
					for (int i = 0; i < listUsuarios.size(); i++) {
						UsuarioCadastro cadastroUsuario = new UsuarioCadastro(listUsuarios.get(i).getLogin(), 
								listUsuarios.get(i).getSenha(), 1);
						ResponseEntity<DetalhesUsuario> cadastrarUsuario;
						try {
							cadastrarUsuario = this.cadastrarUsuario(cadastroUsuario);
							DetalhesUsuarioMassivo detalhes = new DetalhesUsuarioMassivo();
							detalhes.setStatus("Login criado com sucesso");
							detalhes.setLogin(cadastrarUsuario.getBody().login());
							listaDetalhesUsuario.add(detalhes);
							cadastroMassivo.setListaUsuario(listaDetalhesUsuario);
						} catch (ClienteException e) {
							DetalhesUsuarioMassivo detalhes = new DetalhesUsuarioMassivo();
							detalhes.setStatus("Login já existe");
							detalhes.setLogin(cadastroUsuario.login());
							listaDetalhesUsuario.add(detalhes);
							cadastroMassivo.setListaUsuario(listaDetalhesUsuario);
							e.printStackTrace();
							
						} catch (Exception e) {
							DetalhesUsuarioMassivo detalhes = new DetalhesUsuarioMassivo();
							detalhes.setStatus("Erro para cadastrar o login");
							detalhes.setLogin(cadastroUsuario.login());
							listaDetalhesUsuario.add(detalhes);
							cadastroMassivo.setListaUsuario(listaDetalhesUsuario);
						}
					}
				} catch (Exception e) {
					log.error("Erro no cadastro massivo de usuario " + e.getMessage());
				}			
			}
			
			is.close();
			
		} catch (Exception e) {
			log.error("Erro no cadastro massivo " + e.getMessage());
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				log.error("Erro no cadastro massivo " + e.getMessage());
				e.printStackTrace();
			}
		}
		
		return cadastroMassivo;
	}

	public Boolean validaUsuarioAtivo(@NotNull String login) {
		Optional<Usuario> byLogin = usuarioRepo.findByLogin(login);
		
		Boolean ativo = false;
		
		if(byLogin.isPresent()) {
			if(byLogin.get().getAtivo() == 1) {
				ativo = true;
			} else {
				ativo = false;
			}
		}
		return ativo;
		
	}

}
