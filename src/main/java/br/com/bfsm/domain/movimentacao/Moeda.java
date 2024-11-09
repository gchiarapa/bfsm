package br.com.bfsm.domain.movimentacao;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "moeda", schema = "bank")
public class Moeda {
	
//    DOLAR(1.0, "USD"),
//    EURO(0.85, "EUR"),
//    REAL(5.25, "BRL"),
//    LIBRA(0.75, "GBP"),
//    IENE(110.0, "JPY");
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public
	Long id;
	
	public String nome;
	
//	Moeda(double txCambio, String moeda) {
//		this.moeda = moeda;
//		this.txCambio = txCambio;
//	}
//
//	public String moeda;
//	public Double txCambio;
//	
//	public String getMoeda() {
//		return moeda;
//	}
//	
//	public Double getTxCambio() {
//		return txCambio;
//	}
//	
//	public void setMoeda(String moeda) {
//		this.moeda = moeda;
//	}
//	
//	public void setTxCambio(Double txCambio) {
//		this.txCambio = txCambio;
//	}

}
