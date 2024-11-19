ALTER TABLE saldo_historico
ADD COLUMN movimentacao_id INT,
ADD KEY idMovimentacao_saldo_cliente_idx (movimentacao_id),
ADD CONSTRAINT fk_idMovimentacao_saldo FOREIGN KEY (movimentacao_id) REFERENCES movimentacoes (id);
