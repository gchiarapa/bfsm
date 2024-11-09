package br.com.bfsm.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import br.com.bfsm.domain.cambio.TaxaCambio;
import br.com.bfsm.domain.cliente.Cliente;
import br.com.bfsm.domain.movimentacao.AtualizarMovimentacao;
import br.com.bfsm.domain.movimentacao.Categoria;
import br.com.bfsm.domain.movimentacao.DadosCadastroMovimentacao;
import br.com.bfsm.domain.movimentacao.DetalhesMovimentacao;
import br.com.bfsm.domain.movimentacao.Moeda;
import br.com.bfsm.domain.movimentacao.Movimentacoes;
import br.com.bfsm.infra.exception.MovimentacoesException;
import br.com.bfsm.repository.ClienteRepository;
import br.com.bfsm.service.MovimentacoesService;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class MovimentacaoControllerTest {

    @Mock
    private MovimentacoesService movimentacoesService;

    @Mock
    private ClienteRepository clienteRepo;

    @InjectMocks
    private MovimentacaoController movimentacaoController;
    
	@Autowired
	private JacksonTester<DadosCadastroMovimentacao> dadosCadastroMovimentacao;

	@Autowired
	private JacksonTester<AtualizarMovimentacao> atualizarMovimentacao;
	@Autowired
	private JacksonTester<DetalhesMovimentacao> detalhesMovimentacao;

    @Autowired
    private MockMvc mockMvc;

    private LocalDateTime data;
    
    private Movimentacoes movimentacao;
    
    private DadosCadastroMovimentacao cadastroMovimentacao;
    
    private AtualizarMovimentacao atualizarMovimentacoes;
    
    private DetalhesMovimentacao detalhes;

    @BeforeEach
    public void setUp() {
        data = LocalDateTime.now();
        movimentacao = new Movimentacoes(1L, "Debito", data, "100", new Cliente(), new Moeda(), new Categoria(), new TaxaCambio(0, null, null));
        cadastroMovimentacao = new DadosCadastroMovimentacao("Debito", data, "1000", 1L, new Moeda(), new Categoria());
        atualizarMovimentacoes = new AtualizarMovimentacao(1L, "Debito", data, "1000", 1L, new Moeda(), new Categoria());
    }

    @Test
	@DisplayName("Testar cadastro com requisição presente HTTP 200")
	@WithMockUser
    public void testCadastrarSucesso200() throws Exception {
        try {
			when(movimentacoesService.salvarMovimentacao(any(Movimentacoes.class))).thenReturn(new Movimentacoes(cadastroMovimentacao));
		} catch (MovimentacoesException e) {
			e.printStackTrace();
		}

        var response = mockMvc
        .perform(
        		post("/movimentacoes/adicionar")
		        .contentType("application/json")
		        .content(dadosCadastroMovimentacao.write(cadastroMovimentacao).getJson())
		        )
		.andReturn().getResponse();
        
		assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
		
		var jsonEsperado = detalhesMovimentacao.write(detalhes).getJson();
		
		assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
    }

    @Test
	@DisplayName("Testar cadastro com requisição ausente HTTP 400")
	@WithMockUser
    public void testCadastrarError400() throws Exception {
        try {
			when(movimentacoesService.salvarMovimentacao(any(Movimentacoes.class))).thenThrow(new MovimentacoesException("Erro!"));
		} catch (MovimentacoesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        mockMvc.perform(post("/movimentacoes/adicionar")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isInternalServerError());
    }

    @Test
	@DisplayName("Testar buscar com requisição presente HTTP 200")
	@WithMockUser
    public void testBuscarSucesso200() throws Exception {
        try {
			when(movimentacoesService.buscarMovimentacaoPeloId(1L)).thenReturn(Optional.of(new Movimentacoes(cadastroMovimentacao)));
		} catch (MovimentacoesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        mockMvc.perform(get("/movimentacoes/buscar?movimentacaoId=1"))
                .andExpect(status().isOk());
    }

    @Test
	@DisplayName("Testar buscar com requisição ausente HTTP 200")
	@WithMockUser
    public void testBuscarErro404() throws Exception {
        try {
			when(movimentacoesService.buscarMovimentacaoPeloId(1L)).thenThrow(new EntityNotFoundException());
		} catch (MovimentacoesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        mockMvc.perform(get("/movimentacoes/buscar?movimentacaoId=1"))
                .andExpect(status().isNotFound());
    }

    @Test
	@DisplayName("Testar remoção com requisição presente HTTP 200")
	@WithMockUser
    public void testRemoverSucesso200() throws Exception {
        try {
			doNothing().when(movimentacoesService).removerPeloId(1L);
		} catch (MovimentacoesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        mockMvc.perform(delete("/movimentacoes/remover?movimentacaoId=1"))
                .andExpect(status().isNoContent());
    }

    @Test
	@DisplayName("Testar remoção com requisição incorreta HTTP 404")
	@WithMockUser
    public void testRemoverErro404() throws Exception {
        try {
			doThrow(new EntityNotFoundException()).when(movimentacoesService).removerPeloId(1L);
		} catch (MovimentacoesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        mockMvc.perform(delete("/movimentacoes/remover?movimentacaoId=1"))
                .andExpect(status().isNotFound());
    }

    @Test
	@DisplayName("Testar atualização com requisição presente HTTP 200")
	@WithMockUser
    public void testAtualizarSucesso200() throws Exception {
        try {
			when(movimentacoesService.atualizar(any(Movimentacoes.class))).thenReturn(new Movimentacoes(cadastroMovimentacao));
		} catch (MovimentacoesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        mockMvc.perform(put("/movimentacoes/atualizar")
                .contentType("application/json")
                .content(atualizarMovimentacao.write(atualizarMovimentacoes).getJson()))
                .andExpect(status().isCreated());
    }

//    @Test
//	@DisplayName("Testar atualização com requisição presente HTTP 404")
//	@WithMockUser
//    public void testAtualizarErro404() throws Exception {
//        try {
//			when(movimentacoesService.atualizar(any(Movimentacoes.class))).thenThrow(new EntityNotFoundException());
//		} catch (MovimentacoesException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//        mockMvc.perform(put("/movimentacoes/atualizar")
//                .contentType("application/json")
//                .content(atualizarMovimentacao.write(atualizarMovimentacoes).getJson()))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//	@DisplayName("Testar relatório HTTP 200")
//	@WithMockUser
//    public void testRelatorioSucesso200() throws Exception {
//        MockMultipartFile relatorio = new MockMultipartFile("relatorio", "report.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[0]);
//        when(movimentacoesService.relatorio(any(), any(), any(), any())).thenReturn(relatorio);
//
//        mockMvc.perform(get("/movimentacoes/relatorio?movimentacaoId=1"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//	@DisplayName("Testar relatorio HTTP 403")
//	@WithMockUser
//    public void testRelatorioErro403() throws Exception {
//        mockMvc.perform(get("/movimentacoes/relatorio"))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//	@DisplayName("Testar relatório HTTP 500")
//	@WithMockUser
//    public void testRelatorioErro500() throws Exception {
//        when(movimentacoesService.relatorio(any(), any(), any(), any())).thenThrow(new IOException("Erro IO"));
//
//        mockMvc.perform(get("/movimentacoes/relatorio?movimentacaoId=1"))
//                .andExpect(status().isNotFound());
//    }
}
