ALTER TABLE movimentacoes_cliente
ADD COLUMN movimentacaoA_id INT NOT NULL,
ADD COLUMN movimentacaoB_id INT,
ADD KEY idMovimentacaoA_idx (movimentacaoA_id),
ADD KEY idMovimentacaoB_idx (movimentacaoB_id),
ADD CONSTRAINT fk_idMovimentacaoA FOREIGN KEY (movimentacaoA_id) REFERENCES movimentacoes (id),
ADD CONSTRAINT fk_idMovimentacaoB FOREIGN KEY (movimentacaoB_id) REFERENCES movimentacoes (id);