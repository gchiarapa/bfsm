package br.com.bfsm.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.bfsm.domain.movimentacao.Movimentacoes;

@Repository
public interface MovimentacoesRepository extends CrudRepository<Movimentacoes, Long>{
	
//	@Query("""
//			select m from Movimentacoes m
//			JOIN m.cliente c
//			where (m.id = :id
//			OR m.data = :data)
//			AND c.id = :clienteId
//			""")
	List<Movimentacoes> findByIdOrDataOrClienteId(@Param("id") Long id, 
			@Param("data") LocalDateTime data, 
			@Param("clienteId") Long clienteId);

}
