package br.com.bfsm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.bfsm.domain.movimentacao.Movimentacoes;
import jakarta.transaction.Transactional;

@Repository
public interface MovimentacoesRepository extends CrudRepository<Movimentacoes, Long>, JpaSpecificationExecutor<Movimentacoes>, JpaRepository<Movimentacoes, Long>{
	
	@Modifying
    @Query("UPDATE Movimentacoes m SET m.ativo = :ativo WHERE m.id = :id")
	@Transactional
    int updateMovimentacaoAtivoById(@Param("ativo") boolean ativo, @Param("id") Long id);

}
