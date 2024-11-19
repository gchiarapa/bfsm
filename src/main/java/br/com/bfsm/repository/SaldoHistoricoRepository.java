package br.com.bfsm.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.bfsm.domain.cliente.SaldoHistorico;

@Repository
public interface SaldoHistoricoRepository extends CrudRepository<SaldoHistorico, Long>{
	
	

}
