package br.com.bfsm.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
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
import br.com.bfsm.infra.exception.ClienteException;
import br.com.bfsm.repository.ClienteRepository;
import br.com.bfsm.service.ClienteService;
import jakarta.persistence.EntityNotFoundException;

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
	
	@MockBean
	ClienteService clienteService;
	
	@MockBean
	ClienteRepository clienteRepo;
	
	private DetalhesCliente dadosDetalhamentosCliente;
	
	private CadastroCliente cadastro;
	
	private Cliente cliente;
	
	private Long clienteId;
	
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
		
		var dadosDetalhamentosCliente = new DetalhesCliente(null, "Gustavo", "Rua 2, numero 1", new BigDecimal("1000"), true);
		var cadastro = new CadastroCliente("Gustavo", "Rua 2, numero 1", new BigDecimal("1000"), 1);
				
		try {
			when(clienteService.salvar(any(CadastroCliente.class))).thenReturn(new Cliente(cadastro));
		} catch (ClienteException e) {
			e.printStackTrace();
		}
		
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

	@Test
	@DisplayName("Testar busca de cliente que não existe HTTP 404")
	@WithMockUser
	void testBuscarErro404() throws Exception {
		
		clienteId = 11111L;
		
		try {
			when(clienteService.buscarClientePeloId(clienteId, true)).thenThrow(new EntityNotFoundException());
		} catch (ClienteException e) {
			e.printStackTrace();
		}
		
		var response = mvc
				.perform(
						get("/cliente/buscar?clienteId="+clienteId))
				.andReturn().getResponse();
		
		assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
		
	}
	
	@Test
	@DisplayName("Testar busca de cliente que existe HTTP 200")
	@WithMockUser
	void testBuscarSucesso200() throws Exception {
		
		clienteId = 1L;
		Cliente cliente = new Cliente(clienteId, "Gustavo", "Rua Abc", new BigDecimal("1000"), null, true, null);
		
		try {
			when(clienteService.buscarClientePeloId(clienteId, true)).thenReturn(cliente);
		} catch (ClienteException e) {
			e.printStackTrace();
		}
		
		var response = mvc
				.perform(
						get("/cliente/buscar?clienteId="+clienteId))
				.andReturn().getResponse();
		
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		
	}

	@Test
	@DisplayName("Testar remoção de cliente que existe HTTP 200")
	@WithMockUser
	void testRemoverSucesso200() throws Exception {
		clienteId = 1L;
		String status = "OK";
		
		try {
			when(clienteService.removerPeloId(clienteId)).thenReturn(status);
		} catch (ClienteException e) {
			e.printStackTrace();
		}
		
		var response = mvc
				.perform(
						delete("/cliente/remover?clienteId="+clienteId))
				.andReturn().getResponse();
		
		assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}
	
	@Test
	@DisplayName("Testar remoção de cliente que não existe HTTP 404")
	@WithMockUser
	void testRemoverErro404() throws Exception {
		
		clienteId = 11111L;
		
		try {
			when(clienteService.removerPeloId(clienteId)).thenThrow(new EntityNotFoundException());
		} catch (ClienteException e) {
			e.printStackTrace();
		}
		
		var response = mvc
				.perform(
						delete("/cliente/remover?clienteId="+clienteId))
				.andReturn().getResponse();
		
		assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
	}


}
