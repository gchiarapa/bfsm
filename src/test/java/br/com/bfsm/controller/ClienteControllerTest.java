package br.com.bfsm.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import br.com.bfsm.domain.cliente.CadastroCliente;
import br.com.bfsm.domain.cliente.Cliente;
import br.com.bfsm.domain.cliente.DetalhesCliente;
import br.com.bfsm.repository.ClienteRepository;
import br.com.bfsm.service.ClienteService;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class ClienteControllerTest {
	
	@Autowired
	MockMvc mvc;
	
	@Autowired
	private JacksonTester<CadastroCliente> dadosCadastro;

	@Autowired
	private JacksonTester<DetalhesCliente> detalhesCliente;
	
	@Autowired
	ClienteService clienteService;
	
	@MockBean
	ClienteRepository clienteRepo;
	
	@Test
	@DisplayName("Testar cadastro com requisição ausente HTTP 400")
	@WithMockUser
	void testCadatrarErro400() throws Exception {
		
		var response = mvc.perform(post("/cliente/adicionar"))
		.andReturn().getResponse();
		
		assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		
	}
	
	@Test
	@DisplayName("Testar cadastro com requisição presente HTTP 200")
	@WithMockUser
	void testCadatrarSucesso200() throws Exception {
		
		var dadosDetalhamentosCliente = new DetalhesCliente(null, "Gustavo", "Rua 2, numero 1", "1000");
		var cadastro = new CadastroCliente("Gustavo", "Rua 2, numero 1", "1000");
		when(clienteService.salvar(any(Cliente.class))).thenReturn(new Cliente(cadastro));
		
		var response = mvc
				.perform(
						post("/cliente/adicionar")
						.contentType(MediaType.APPLICATION_JSON)
						.content(dadosCadastro.write(cadastro).getJson())
						)
				.andReturn().getResponse();
		
		assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
		
		var jsonEsperado = detalhesCliente.write(dadosDetalhamentosCliente).getJson();
		
		assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
		
	}

//	@Test
//	void testBuscar() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	void testRemover() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	void testAtualizar() {
//		fail("Not yet implemented"); // TODO
//	}

}
