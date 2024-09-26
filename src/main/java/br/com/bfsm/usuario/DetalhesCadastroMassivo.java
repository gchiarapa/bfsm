package br.com.bfsm.usuario;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DetalhesCadastroMassivo {

	
	@JsonProperty("Logins")
	public List<DetalhesUsuarioMassivo> listaUsuario;


}
