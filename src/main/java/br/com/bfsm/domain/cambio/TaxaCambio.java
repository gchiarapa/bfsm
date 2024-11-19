package br.com.bfsm.domain.cambio;

import java.math.BigDecimal;
import java.util.Map;

public record TaxaCambio (
//		boolean success,
	    long timestamp,
	    String base,
//	    String date,
	    Map<String, BigDecimal> rates
	    )
{

}
