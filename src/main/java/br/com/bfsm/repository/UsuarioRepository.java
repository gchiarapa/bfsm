package br.com.bfsm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.bfsm.model.Usuario;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long>{
	
	@Query("select a from Usuario a where login = :login")
	Optional<Usuario> buscarUsuarioPeloLogin(@Param("login") String login);

}
