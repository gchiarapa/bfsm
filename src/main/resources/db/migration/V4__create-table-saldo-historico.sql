CREATE TABLE IF NOT EXISTS saldo_historico (
  id int NOT NULL AUTO_INCREMENT,
  data datetime NOT NULL,
  valor varchar(100) NOT NULL,
  id_cliente int DEFAULT NULL,
  PRIMARY KEY (id),
  KEY saldo_historico_cliente_FK (id_cliente),
  CONSTRAINT saldo_historico_cliente_FK FOREIGN KEY (id_cliente) REFERENCES cliente (id)
);