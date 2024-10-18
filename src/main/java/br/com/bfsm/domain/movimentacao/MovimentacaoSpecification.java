package br.com.bfsm.domain.movimentacao;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

public class MovimentacaoSpecification {
	
		public static Specification<Movimentacoes> byId(Long movimentacaoId) {
			return (root, query, criteriaBuilder) -> movimentacaoId == null ? null : criteriaBuilder.equal(root.get("id"), movimentacaoId);
		}

	    public static Specification<Movimentacoes> byData(LocalDateTime data) {
	        return (root, query, criteriaBuilder) -> data == null ? null : criteriaBuilder.equal(root.get("data"), data);
	    }

	    public static Specification<Movimentacoes> byCliente(Long clienteId) {
	    	return (root, query, criteriaBuilder) -> clienteId == null ? null : criteriaBuilder.equal(root.get("cliente").get("id"), clienteId);
	    }
	    
	    public static Specification<Movimentacoes> byMoeda(String moeda) {
	    	return (root, query, criteriaBuilder) -> moeda == null ? null : criteriaBuilder.equal(root.get("moeda"), moeda);
	    }

}
