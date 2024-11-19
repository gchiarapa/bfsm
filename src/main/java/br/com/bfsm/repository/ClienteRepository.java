package br.com.bfsm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.bfsm.domain.cliente.Cliente;
import jakarta.transaction.Transactional;

@Repository
public interface ClienteRepository extends CrudRepository<Cliente, Long>{
	
	@Modifying
    @Query("UPDATE Cliente c SET c.ativo = :ativo WHERE c.id = :id")
	@Transactional
    int updateClienteAtivoById(@Param("ativo") boolean ativo, @Param("id") Long id);

	Optional<Cliente> findClienteByIdAndAtivo(Long clienteId, Boolean ativo);
	

}
