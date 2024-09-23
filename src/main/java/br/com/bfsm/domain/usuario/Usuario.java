package br.com.bfsm.domain.usuario;

import java.util.ArrayList;
import java.util.List;

import br.com.bfsm.domain.permissao.Permissao;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import lombok.Data;

@Data
@Entity
@Table(name = "api_usuario", schema = "bank")
public class Usuario {
	
	public Usuario(@Valid UsuarioCadastro cadastroUsuario) {
		this.login = cadastroUsuario.login();
		this.senha = cadastroUsuario.senha();
	}

	public Usuario(AtualizaUsuario usuarioAtualizacao) {
		this.id = usuarioAtualizacao.id();
		this.login = usuarioAtualizacao.login();
	}
	
	public Usuario(DetalhesUsuario usuarioDetalhes) {
		this.login = usuarioDetalhes.login();
	}
	
	public Usuario() {

	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	String login;
	
	String senha;
	
	@ManyToMany
	@JoinTable(name = "api_usuario_permissoes", schema = "bank",
	joinColumns = @JoinColumn(name = "usuario_id"),
	inverseJoinColumns = @JoinColumn(name = "permissao_id"))
	private List<Permissao> roles = new ArrayList<>();

}
