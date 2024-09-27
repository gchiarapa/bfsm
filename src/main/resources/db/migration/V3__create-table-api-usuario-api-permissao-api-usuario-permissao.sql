CREATE TABLE IF NOT EXISTS api_usuario (
  id int NOT NULL AUTO_INCREMENT,
  login varchar(50) NOT NULL,
  senha varchar(2000) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS api_permissoes (
  id int NOT NULL AUTO_INCREMENT,
  permissao varchar(50) DEFAULT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS api_usuario_permissoes (
  usuario_id int NOT NULL,
  permissao_id int NOT NULL,
  PRIMARY KEY (usuario_id,permissao_id),
  KEY permissao_id_idx (permissao_id),
  CONSTRAINT permissao_id FOREIGN KEY (permissao_id) REFERENCES api_permissoes (id),
  CONSTRAINT usuario_id FOREIGN KEY (usuario_id) REFERENCES api_usuario (id)
);