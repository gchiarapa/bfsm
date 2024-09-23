package br.com.bfsm.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.bfsm.domain.cliente.Cliente;

@Repository
public interface ClienteRepository extends CrudRepository<Cliente, Long>{
	

}
