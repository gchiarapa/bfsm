package br.com.bfsm.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.bfsm.domain.movimentacao.Movimentacao;

@Repository
public interface MovimentacoesRepository extends CrudRepository<Movimentacao, Long>{

}
