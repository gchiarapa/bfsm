package br.com.bfsm.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import br.com.bfsm.domain.usuario.Usuario;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long>{
	
	@Query("select a from Usuario a where login = :login")
	UserDetails findByLogin(@Param("login") String login);

}
