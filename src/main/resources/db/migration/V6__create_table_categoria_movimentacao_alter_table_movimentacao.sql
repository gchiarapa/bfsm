CREATE TABLE if not exists categoria_movimentacao (
	id INTEGER NOT NULL AUTO_INCREMENT UNIQUE,
	nome VARCHAR(255),
	PRIMARY KEY(id)
);

alter table movimentacoes 
add column id_categoria int,
ADD CONSTRAINT fk_categoria
FOREIGN KEY (id_categoria) REFERENCES categoria_movimentacao(id);
