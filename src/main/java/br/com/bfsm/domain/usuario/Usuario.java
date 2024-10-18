package br.com.bfsm.domain.usuario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.bfsm.domain.permissao.Permissao;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "api_usuario", schema = "bank")
public class Usuario implements UserDetails {
	
	public Usuario(@Valid UsuarioCadastro cadastroUsuario) {
		this.login = cadastroUsuario.login();
		this.senha = cadastroUsuario.senha();
		this.ativo = 1;
	}

	public Usuario(AtualizaUsuario usuarioAtualizacao) {
		this.id = usuarioAtualizacao.id();
		this.login = usuarioAtualizacao.login();
		this.senha = usuarioAtualizacao.senha();
		this.ativo = usuarioAtualizacao.ativo();
	}
	
	public Usuario(DetalhesUsuario usuarioDetalhes) {
		this.login = usuarioDetalhes.login();
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	
	String login;
	
	String senha;
	
	int ativo;
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "api_usuario_permissoes", schema = "bank",
	joinColumns = @JoinColumn(name = "usuario_id"),
	inverseJoinColumns = @JoinColumn(name = "permissao_id"))
	private List<Permissao> permissao = new ArrayList<>();

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("API_ROLE"));
	}

	@Override
	public String getPassword() {
		return this.senha;
	}

	@Override
	public String getUsername() {
		return this.login;
	}

	public Usuario(String login2, String senha2) {
		this.login = login2;
		this.senha = senha2;
	}

}
