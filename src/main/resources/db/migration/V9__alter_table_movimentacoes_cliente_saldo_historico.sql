alter table movimentacoes 
modify COLUMN valor DECIMAL(10, 2); 

alter table cliente 
modify COLUMN saldo DECIMAL(10, 2); 

alter table saldo_historico 
modify COLUMN valor DECIMAL(10, 2); 