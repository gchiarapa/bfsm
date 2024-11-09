CREATE TABLE if not exists moeda (
	id INTEGER NOT NULL AUTO_INCREMENT UNIQUE,
	nome VARCHAR(255),
	PRIMARY KEY(id)
);

alter table movimentacoes 
add column id_moeda int,
ADD CONSTRAINT fk_moeda
FOREIGN KEY (id_moeda) REFERENCES moeda(id);
