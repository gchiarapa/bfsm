package br.com.bfsm.domain.movimentacao;

import br.com.bfsm.domain.cliente.Cliente;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "movimentacoes_cliente", schema = "bank")
public class MovimentacoesCliente {
	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;
	
    @ManyToOne
    @JoinColumn(name = "clienteA_id", nullable = false)
    private Cliente clienteA;

    @ManyToOne
    @JoinColumn(name = "clienteB_id", nullable = false)
    private Cliente clienteB;
    
    @ManyToOne
    @JoinColumn(name = "movimentacaoA_id", nullable = false)
    private Movimentacoes movimentacaoA;

    @ManyToOne
    @JoinColumn(name = "movimentacaoB_id", nullable = false)
    private Movimentacoes movimentacaoB;
	

}
