package br.com.bfsm.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "api_usuario", schema = "bank")
public class Usuario {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;
	
	String usuario;
	
	String senha;
	
	@ManyToMany(fetch = FetchType.EAGER)
	    @JoinTable(name = "api_usuario_permissoes", schema = "bank"
//        ,joinColumns = @JoinColumn(name = "usuario_id"),
//        inverseJoinColumns = @JoinColumn(name = "permissao_id"
	    )
	private List<Permissoes> roles = new ArrayList<>();

}
