CREATE TABLE IF NOT EXISTS movimentacoes (
  id int NOT NULL AUTO_INCREMENT,
  tipo varchar(50) DEFAULT NULL,
  data datetime DEFAULT NULL,
  valor varchar(200) DEFAULT NULL,
  id_cliente int NOT NULL,
  PRIMARY KEY (id),
  KEY idCliente_idx (id_cliente),
  CONSTRAINT idCliente FOREIGN KEY (id_cliente) REFERENCES cliente (id)
);