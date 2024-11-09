DELIMITER //

CREATE TRIGGER after_cliente_update
AFTER UPDATE ON cliente
FOR EACH ROW
BEGIN
    INSERT INTO saldo_historico (data, valor, id_cliente)
    VALUES (NOW(), OLD.saldo, OLD.id);
END;

//

DELIMITER ;
