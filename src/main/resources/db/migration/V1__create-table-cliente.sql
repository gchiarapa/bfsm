CREATE TABLE IF NOT EXISTS cliente (
  id int NOT NULL AUTO_INCREMENT,
  nome varchar(50) DEFAULT NULL,
  endereco varchar(200) DEFAULT NULL,
  saldo varchar(100) DEFAULT NULL,
  ativo int NOT NULL,
  PRIMARY KEY (id)
);