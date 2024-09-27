package br.com.bfsm.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ClienteControllerTest {
	
	@Autowired
	MockMvc mvc;

	@Test
	@DisplayName("Testar cadastro com requisição ausente HTTP 400")
	@WithMockUser
	void testCadatrarErro400() throws Exception {
		
		var response = mvc.perform(post("/cliente/adicionar"))
		.andReturn().getResponse();
		
		assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		
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
