CREATE TABLE IF NOT EXISTS movimentacoes_cliente (
  id int NOT NULL AUTO_INCREMENT,
  clienteA_id int NOT NULL,
  clienteB_id int ,
  PRIMARY KEY (id),
  KEY idClienteA_idx (clienteA_id),
  KEY idClienteB_idx (clienteB_id),
  CONSTRAINT fk_idClienteA FOREIGN KEY (clienteA_id) REFERENCES cliente (id),
  CONSTRAINT fk_idClienteB FOREIGN KEY (clienteB_id) REFERENCES cliente (id)
);