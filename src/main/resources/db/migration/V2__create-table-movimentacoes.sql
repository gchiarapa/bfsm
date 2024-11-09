CREATE TABLE IF NOT EXISTS movimentacoes (
  id int NOT NULL AUTO_INCREMENT,
  tipo varchar(50) DEFAULT NULL,
  data datetime DEFAULT NULL,
  valor varchar(200) DEFAULT NULL,
  cliente_id int NOT NULL,
  moeda varchar(50) NOT NULL,
  PRIMARY KEY (id),
  KEY idCliente_idx (cliente_id),
  CONSTRAINT idCliente FOREIGN KEY (cliente_id) REFERENCES cliente (id)
);