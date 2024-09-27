package br.com.bfsm.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import br.com.bfsm.domain.usuario.Usuario;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UsuarioRepositoryTest {
	
	@Autowired
	UsuarioRepository usuarioRepo;
	
	@Autowired
	TestEntityManager em;

	@Test
	@DisplayName("Faz o filtro por login existente no banco")
	void testFindByLoginOk() {
		
		var login = "Gustavo";
		var senha = "teste";
		cadastraLogin(login, senha);
		UserDetails byLogin = usuarioRepo.findByLogin("Gustavo");
		assertThat(byLogin).isNotNull();
	}
	

	@Test
	@DisplayName("Faz o filtro por login inexistente no banco")
	void testFindByLoginNok() {
		UserDetails byLogin = usuarioRepo.findByLogin("aaa");
		assertThat(byLogin).isNull();
	}
	
	@Test
	@DisplayName("Verifica se o usuario cadastro é igual ao usuário buscado")
	void testFindByLogin() {
		
		//given or arrange
		var login = "Gustavo";
		var senha = "teste";
		

		Usuario cadastraLogin = cadastraLogin(login, senha);
		
		//when or act
		UserDetails byLogin = usuarioRepo.findByLogin("Gustavo");
		
		//assert or then
		assertThat(byLogin).isEqualTo(cadastraLogin);
	}

	private Usuario cadastraLogin(String login, String senha) {
		
		var usuario = new Usuario(login, senha);
		
		usuario.setLogin(login);
		usuario.setSenha(senha);
		
		em.persist(usuario);
		
		return usuario;
		
	}
}
