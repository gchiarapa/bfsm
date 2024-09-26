package br.com.bfsm.controller;

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
import br.com.bfsm.service.UsuarioService;
import br.com.bfsm.usuario.DetalhesCadastroMassivo;
import jakarta.validation.Valid;


@RestController
@RequestMapping(path = "/usuario")
public class UsuarioController {
	
	@Autowired
	UsuarioService usuarioService;
	
	private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);
	
	@PostMapping("/cadastro")
	public ResponseEntity adicionar(@RequestBody @Valid UsuarioCadastro cadastroUsuario, UriComponentsBuilder uriBuilder) {
			
//		try {
			ResponseEntity<DetalhesUsuario> cadastrarUsuario = usuarioService.cadastrarUsuario(cadastroUsuario);
//			if(status == "OK") {
//				Usuario novoUsuario = new Usuario(cadastroUsuario);
//				novoUsuario.setLogin(cadastroUsuario.login());
//				novoUsuario.setSenha(new BCryptPasswordEncoder().encode(cadastroUsuario.senha()));
//				usuarioService.salvarUsuario(novoUsuario);
				var uri = uriBuilder.path("/usuario/{id}").buildAndExpand(cadastrarUsuario.getBody().id()).toUri();
//				log.info("o login " + novoUsuario.getLogin() + " foi cadastrado");
				if(cadastrarUsuario.getStatusCodeValue() == 200) {
					return ResponseEntity.created(uri).body(new DetalhesUsuario(cadastrarUsuario));					
				} else if (cadastrarUsuario.getStatusCodeValue() == 204){
					return ResponseEntity.noContent().build();
				} else {
					return ResponseEntity.internalServerError().build();
				}
//			} else {
//				log.info("o login " + cadastroUsuario.login() + " já existe" );
//				return ResponseEntity.noContent().build();
//			}
//		} catch (Exception e) {
//			log.error("Erro para cadastrar o login " + e.getMessage());
//			return ResponseEntity.internalServerError().build();
//		}
		
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
		
		log.info("Iniciando remocao do id: [id] " + usuarioId);
		
		String removerPeloId = usuarioService.removerPeloId(usuarioId);
		
		if(removerPeloId == "OK") {
			log.info("remocao do id: [id] " + usuarioId + " efetuada com sucesso");
			return ResponseEntity.noContent().build();
		} else if(removerPeloId == "404") {
			log.info("O id: [id] " + usuarioId + " não foi localizado");
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.internalServerError().build();
		}
		
	}
	
	@PutMapping("/atualizar")
	public ResponseEntity putMethodName(@RequestBody AtualizaUsuario usuarioAtualizacao, UriComponentsBuilder uriBuilder) {
//		log.info("Tentativa de adicionar cliente - Sessão: {}", SecurityContextHolder.getContext().getAuthentication());
		log.info("Valores recebidos: [Login] " + usuarioAtualizacao.login());
		
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
	
	@PostMapping("/cadastro/massivo")
	public ResponseEntity cadastroMassivo(@RequestParam("file") MultipartFile file) {
		
		List<Usuario> listUsuarios = new ArrayList<Usuario>();
		InputStream is = null;
		DetalhesCadastroMassivo cadastrarUsuarioMassivo = new DetalhesCadastroMassivo();
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
	        
			Sheet sheetUsuario = workbook.getSheetAt(0);
			Iterator<Row> iterator = sheetUsuario.iterator();
			
			while (iterator.hasNext()) {
				Row linha = iterator.next();
				
				Iterator<Cell> cellIterator = linha.cellIterator();
				
				Usuario usuario = new Usuario();
				listUsuarios.add(usuario);
				
				while (cellIterator.hasNext()) {
					Cell cell = (Cell) cellIterator.next();
					
					switch (cell.getColumnIndex()) {
					case 0:
						usuario.setLogin(cell.getStringCellValue());
						break;
					case 1:
						usuario.setSenha(cell.getStringCellValue());
						break;
					default:
						break;
					}
				}
				
			}
			if(listUsuarios.size() > 0) {
				cadastrarUsuarioMassivo = usuarioService.cadastrarUsuarioMassivo(listUsuarios);					
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
		
		return ResponseEntity.ok(cadastrarUsuarioMassivo);
	}
	
	

}
