package br.com.bfsm.repository;

import org.springframework.data.repository.CrudRepository;

import br.com.bfsm.model.Cliente;

public interface ClienteRepository extends CrudRepository<Cliente, Integer>{
	

}
