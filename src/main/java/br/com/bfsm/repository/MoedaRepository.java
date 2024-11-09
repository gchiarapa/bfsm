package br.com.bfsm.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.bfsm.domain.movimentacao.Moeda;

@Repository
public interface MoedaRepository extends CrudRepository<Moeda, Long> {
	

}
