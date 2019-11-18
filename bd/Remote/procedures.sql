DELIMITER $$
CREATE PROCEDURE SINCRONIZAR_HISTORIAL_MAPA
(
	p_id_usuario INT(5),
	p_id_mapa INT(3),
	p_pos_mapa INT(5),
	p_id_minijuego INT(2),
	p_id_boton INT(3),
	p_estado VARCHAR(6),

	OUT po_agregado INT
)
BEGIN
	DECLARE	p_existe INT DEFAULT -1;
	DECLARE p_estado_actual VARCHAR(10) DEFAULT "";
	SELECT COUNT(1) 
			INTO p_existe
	FROM HISTORIAL_MAPAS
	WHERE HM_ID_NINO = p_id_usuario
	  AND HM_ID_MAPA = p_id_mapa
	  AND HM_POS_MAPA = p_pos_mapa
	  AND HM_ID_BOTON = p_id_boton;
	IF(p_existe > 0) THEN
		SELECT HM_ESTADO 
				INTO p_estado_actual
		FROM HISTORIAL_MAPAS
		WHERE HM_ID_NINO = p_id_usuario
		  AND HM_ID_MAPA = p_id_mapa
		  AND HM_POS_MAPA = p_pos_mapa
		  AND HM_ID_BOTON = p_id_boton;
		IF(p_estado_actual <> p_estado AND p_estado_actual <> 'Pasado') THEN
			IF((p_estado = 'Pasado' AND p_estado_actual = 'Actual') OR (p_estado = 'Actual' AND p_estado_actual = 'Futuro')) THEN
				UPDATE HISTORIAL_MAPAS
				SET HM_FECHA = SYSDATE(),
					HM_ESTADO = p_estado,
					HM_ID_MINIJUEGO = p_id_minijuego
				WHERE HM_ID_NINO = p_id_usuario
				  AND HM_ID_MAPA = p_id_mapa
				  AND HM_POS_MAPA = p_pos_mapa
				  AND HM_ID_BOTON = p_id_boton;
		  	END IF;
		END IF;

	ELSE
	 	INSERT INTO HISTORIAL_MAPAS
	 	(HM_ID_NINO, HM_ID_MAPA, HM_POS_MAPA, HM_ID_MINIJUEGO, HM_ID_BOTON, HM_ESTADO, HM_FECHA)
	 	VALUES
	 	(p_id_usuario, p_id_mapa, p_pos_mapa, p_id_minijuego, p_id_boton, p_estado, SYSDATE());
	END IF;
 	SET po_agregado = 1;
END $$

DELIMITER ;
