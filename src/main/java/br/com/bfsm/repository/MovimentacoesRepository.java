package br.com.bfsm.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.bfsm.domain.movimentacao.Movimentacoes;

@Repository
public interface MovimentacoesRepository extends CrudRepository<Movimentacoes, Long>, JpaSpecificationExecutor<Movimentacoes>{
	

}
