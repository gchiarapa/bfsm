package br.com.bfsm.domain.cambio;

import java.util.Map;

public record TaxaCambio (
		boolean success,
	    long timestamp,
	    String base,
	    String date,
	    Map<String, Double> rates){

}
