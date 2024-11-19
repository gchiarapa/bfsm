package br.com.bfsm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.bfsm.domain.usuario.Usuario;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long>{

	Optional<Usuario> findByLogin(@NotNull String login);

//	@Query("""
//			select a from Usuario a 
//			where login = :login
//			""")
//	UserDetails findByLogin(@Param("login") String login);
	
	@Modifying
    @Query("UPDATE Usuario u SET u.ativo = :ativo WHERE u.id = :id")
	@Transactional
    int updateUsuarioAtivoById(@Param("ativo") boolean ativo, @Param("id") Long id);

}
