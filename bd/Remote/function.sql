DELIMITER $$
CREATE FUNCTION LOGIN
(
	p_usuario VARCHAR(50),
	p_contrasena VARCHAR(50)
) 
RETURNS INT

BEGIN
	DECLARE	p_existe INT DEFAULT -2;

	IF(p_usuario IS NULL OR p_contrasena IS NULL) THEN
		RETURN -2;
	END IF;

	SELECT COUNT(N_ID_NINO)
       INTO p_existe
	FROM NINO
	WHERE N_USUARIO = p_usuario
		AND N_CONTRASENA = p_contrasena;

	IF(p_existe <> 0) THEN
		SELECT N_ID_NINO 
			INTO p_existe
		FROM NINO
		WHERE N_USUARIO = p_usuario
			AND N_CONTRASENA = p_contrasena;
	ELSE
		SET p_existe = -2;
	END IF;
	RETURN p_existe;
END$$

DELIMITER ;