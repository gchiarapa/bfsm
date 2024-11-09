package br.com.bfsm.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.bfsm.domain.usuario.Usuario;
import jakarta.validation.constraints.NotNull;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long>{

	Optional<Usuario> findByLogin(@NotNull String login);

//	@Query("""
//			select a from Usuario a 
//			where login = :login
//			""")
//	UserDetails findByLogin(@Param("login") String login);

}
