package br.com.bfsm.domain.movimentacao;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class MovimentacaoSpecification {
	
		public static Specification<Movimentacoes> byId(Long movimentacaoId) {
			return (root, query, criteriaBuilder) -> movimentacaoId == null ? null : criteriaBuilder.equal(root.get("id"), movimentacaoId);
		}

	    public static Specification<Movimentacoes> byDataBetween(LocalDateTime dataInicio, LocalDateTime dataFim) {
	        return (root, query, criteriaBuilder) -> /*data == null ? null : criteriaBuilder.equal(root.get("data"), data)*/ {
	        	if(!StringUtils.isEmpty(dataInicio) && !StringUtils.isEmpty(dataFim)) {
	        		return criteriaBuilder.between(root.get("data"), dataInicio, dataFim);
	        	} else if (!StringUtils.isEmpty(dataInicio)) {
	                return criteriaBuilder.greaterThanOrEqualTo(root.get("data"), dataInicio);
	            } else if (!StringUtils.isEmpty(dataFim)) {
	                return criteriaBuilder.lessThanOrEqualTo(root.get("data"), dataFim);
	            }
	        	return criteriaBuilder.conjunction();
	        };
	    }

	    public static Specification<Movimentacoes> byCliente(Long clienteId) {
	    	return (root, query, criteriaBuilder) -> clienteId == null ? null : criteriaBuilder.equal(root.get("cliente").get("id"), clienteId);
	    }
	    
	    public static Specification<Movimentacoes> byMoeda(Long moedaId) {
	    	return (root, query, criteriaBuilder) -> moedaId == null ? null : criteriaBuilder.equal(root.get("moeda").get("id"), moedaId);
	    }
	    public static Specification<Movimentacoes> byCategoria(Long categoriaId) {
	    	return (root, query, criteriaBuilder) -> categoriaId == null ? null : criteriaBuilder.equal(root.get("categoria").get("id"), categoriaId);
	    }

}
