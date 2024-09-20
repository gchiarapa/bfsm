package br.com.bfsm.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.bfsm.model.Movimentacoes;

@Repository
public interface MovimentacoesRepository extends CrudRepository<Movimentacoes, Long>{

}
