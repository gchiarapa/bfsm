package br.com.bfsm.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import br.com.bfsm.domain.movimentacao.MovimentacoesCliente;
import jakarta.transaction.Transactional;

public interface MovimentacoesClienteRepository extends CrudRepository<MovimentacoesCliente, Long>{

	Optional<MovimentacoesCliente> findByMovimentacaoAIdOrMovimentacaoBId(Long movimentacaoAId, Long movimentacaoBId);

}
