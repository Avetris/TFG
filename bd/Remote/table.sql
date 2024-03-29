CREATE TABLE IF NOT EXISTS LOGOPEDA
(
	L_ID_LOGOPEDA INT(5) NOT NULL COMMENT 'Identificador Logopeda', 
	L_USUARIO VARCHAR(50) NOT NULL UNIQUE COMMENT 'Usuario unico de Login',
	L_NOMBRE VARCHAR(20) NOT NULL COMMENT 'Nombre Logopeda', 
	L_APELLIDOS VARCHAR(30) NOT NULL COMMENT 'Apellido Logopeda', 
	L_CONTRASENA VARCHAR(50) NOT NULL COMMENT 'Contrasena Logopeda', 
	L_IDIOMA VARCHAR(10) DEFAULT 'espanol' COMMENT 'Idioma Logopeda',

	PRIMARY KEY(L_ID_LOGOPEDA)
); 

CREATE TABLE IF NOT EXISTS NINO
(
	N_ID_NINO INT(5) NOT NULL COMMENT 'Identificador Nino', 
	N_USUARIO VARCHAR(50) NOT NULL UNIQUE COMMENT 'Usuario unico de Login',
	N_NOMBRE VARCHAR(20) NOT NULL COMMENT 'Nombre Nino', 
	N_APELLIDOS VARCHAR(30) NOT NULL COMMENT 'Apellido Nino', 
	N_CONTRASENA VARCHAR(50) NOT NULL COMMENT 'Contrasena Nino',
	N_IDIOMA VARCHAR(10) DEFAULT 'espanol' COMMENT 'Idioma de interfaz',
	N_IDIOMA_JUEGO VARCHAR(10) DEFAULT 'espanol' COMMENT 'Idioma de de contenido del juego',


	N_FECHA_NACIMIENTO DATE NOT NULL  COMMENT 'Fecha de Nacimiento del Nino', 
	N_TELEFONO_1 INT(10) NOT NULL COMMENT 'Primer telefono',
	N_TELEFONO_2 INT(10) COMMENT 'Segundo telefono',
	N_NOMBRE_PADRE VARCHAR(30) COMMENT 'Nombre del padre',
	N_NOMBRE_MADRE VARCHAR(30) COMMENT 'Nombre de la madre',
	N_PROFESION_PADRE VARCHAR(100) COMMENT 'Profesion del padre',
	N_PROFESION_MADRE VARCHAR(100) COMMENT 'Profesion de la madre',
	N_HERMANOS_EDADES VARCHAR(100) COMMENT 'Hermanos y sus edades',
	N_OTRAS_CONVIVENCIAS VARCHAR(100) COMMENT 'Otras personas que conviven en el domicilio familiar',

	N_MOTIVO_CONSULTA VARCHAR(500) COMMENT 'Motivo de la consuta',
	N_ENTREVISTADOS VARCHAR(100) COMMENT 'Personas Entrevistadas',
	

	PRIMARY KEY(N_ID_NINO)
);

CREATE TABLE IF NOT EXISTS ANAMNESIS_NINO
(
	AN_ID_NINO INT(5) NOT NULL COMMENT 'Identificador Nino', 
	AN_EMBARAZO_PARTO VARCHAR(500) COMMENT 'Embarazo y/o incidencias y Parto',
	AN_DES_FISICO VARCHAR(500) COMMENT 'Talla y Peso',
	AN_DES_MOTOR VARCHAR(500) COMMENT 'Control de la cabeza, gateo, primeros pasos...',
	AN_DES_OROFACIAL VARCHAR(500) COMMENT 'Succion, deglucion masticacion, denticion',
	AN_DES_LENGUAJE VARCHAR(500) COMMENT 'Balbuceo, primeras palabras, frases',
	AN_ANTECEDENTES_FAMILIARES VARCHAR(500) COMMENT 'Antecedentes familiares con problemas',
	AN_DATOS_MEDICOS VARCHAR(500) COMMENT 'Enfermedades, hospitalizaciones, tratamientos...',
	AN_HISTORIA_ESCOLAR VARCHAR(500) COMMENT 'Edad inicio/centro, cambios...',
	AN_ATEN_FUERA_CENTRO VARCHAR(500) COMMENT 'Estimulacion precoz, logopedia, fisiterapia...',

	PRIMARY KEY (AN_ID_NINO),
	FOREIGN KEY(AN_ID_NINO) REFERENCES NINO(N_ID_NINO)
	ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS AUTONOMIA_NINO
(
	AN_ID_NINO INT(5) NOT NULL COMMENT 'Identificador Nino', 
	AN_TIPO_ALIMENTOS VARCHAR(500) COMMENT 'Triturados, semisolidos, solidos',
	AN_ALIMENTOS_PREFERIDOS VARCHAR(500) COMMENT 'Alimentos preferidos/rechazados',
	AN_RECONOCE_COMER VARCHAR(500) COMMENT 'Reconoce algun modo cuando va a comer',
	AN_PIDE_ALIMENTO VARCHAR(500) COMMENT 'Pide alimento o manifiesta cuando tiene sed/hambre',
	AN_BIBERON_CHUPETE VARCHAR(500) COMMENT 'Uso de biberon/chupete y hasta cuando',
	AN_MASTICA VARCHAR(500) COMMENT 'Mastica bien',
	AN_SUCCIONA_TRAGA VARCHAR(500) COMMENT 'Sabe succionar y tragar',
	AN_MUERDE_COSAS VARCHAR(500) COMMENT 'Succiona/muerde el dedo u otros objetos',
	AN_SALIVA VARCHAR(500) COMMENT 'Controla la saliva',

	AN_HORAS_SUENO VARCHAR(500) COMMENT 'Horas de sueno y descanso tras dormir',
	AN_INTENCION_DORMIR VARCHAR(500) COMMENT 'Indica que quiere ir a dormir',
	AN_RECONOCE_DORMIR VARCHAR(500) COMMENT 'Reconoce cuando va a dormir',
	AN_RONCA VARCHAR(500) COMMENT 'Ronca o mueve la boca',

	AN_CONTROL_ESFINTERES VARCHAR(500) COMMENT 'Tiene un control de esfinteres',
	AN_AVISA_SUCIO VARCHAR(500) COMMENT 'Como avisa cuando esta sucio o mojado',
	AN_PIDE_WC VARCHAR(500) COMMENT 'Muestra inquietud, se lleva la mano, realiza algún signo aprendido...',

	AN_RECONOCE_ROPA VARCHAR(500) COMMENT 'Reconoce Ropa y donde se guarda',
	AN_NECESIDAD_CAMBIO_ROPA VARCHAR(500) COMMENT 'Expresa de algún modo la necesidad de quitar o poner la ropa',

	AN_MALESTAR_SUCIO VARCHAR(500) COMMENT 'Expresa malestar por estar sucio',
	AN_SONARSE_NARIZ VARCHAR(500) COMMENT 'Sabe sonarse solo la nariz',

	PRIMARY KEY (AN_ID_NINO),
	FOREIGN KEY(AN_ID_NINO) REFERENCES NINO(N_ID_NINO)
	ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS SOCIALIZACION_NINO
(
	SN_ID_NINO INT(5) NOT NULL COMMENT 'Identificador Nino', 
	SN_A_CARGO VARCHAR(500) COMMENT 'Comania habitual y cuando esta sin padres',
	SN_MAYOR_APEGO VARCHAR(500) COMMENT 'Hacia quien tiene mas apego',
	SN_SIN_ATENDER VARCHAR(500) COMMENT 'Comportamiento cuando no se le puede atender',
	SN_RECONOCE_FAMILIARES VARCHAR(500) COMMENT 'Reconoce familiares mas cercanos',
	SN_COMPORTAMIENTO_DESCONOCIDOS VARCHAR(500) COMMENT 'Comportamiento con otros adultos desconocidos',

	SN_JUEGO_NINOS VARCHAR(500) COMMENT 'Juega con otros ninos',
	SN_RELACION_NINOS VARCHAR(500) COMMENT 'Se relaciona con otros ninos',

	SN_JUEGO_FUNCIONAL VARCHAR(500) COMMENT 'Utiliza los objetos de juego de forma funcional',
	SN_IMITA_ACCIONES VARCHAR(500) COMMENT 'Imita acciones de situaciones cotidianas',
	SN_OBJETOS_PREFERIDOS VARCHAR(500) COMMENT 'Objetos o juguetes preferidos',
	SN_RECHAZA_OBJETO VARCHAR(500) COMMENT 'Rechaza algun objeto o juguete',
	SN_JUEGA_EN_CASA VARCHAR(500) COMMENT 'En casa se juega con el nino',
	SN_RECONOCE_JUGUETE VARCHAR(500) COMMENT 'Reconoce sus juguetes y donde se guardan',

	PRIMARY KEY (SN_ID_NINO),
	FOREIGN KEY(SN_ID_NINO) REFERENCES NINO(N_ID_NINO)
	ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS LENGUAJE_NINO
(
	LN_ID_NINO INT(5) NOT NULL COMMENT 'Identificador Nino', 
	LN_INTENCIONALIDAD_COMUNICATIVA VARCHAR(500) COMMENT 'Tiene intencionalidad comunicativa',
	LN_QUIEN_COMUNICA VARCHAR(500) COMMENT 'Con quien se comunica',
	LN_COMO_COMUNICA VARCHAR(500) COMMENT 'Gestos, signos, palabras, frases',
	LN_LLAMA_ATENCION VARCHAR(500) COMMENT 'Presta atención cuando se le llama por su nombre, se le pide algo...',
	LN_INTENTA_ATENCION VARCHAR(500) COMMENT 'Como intenta obtener atencion',
	LN_PIDE_NECESITA VARCHAR(500) COMMENT 'Pide lo que necesita y como',
	LN_GESTO_RECHAZO VARCHAR(500) COMMENT 'Utiliza algún gesto, sonido o palabra para rechazar',
	LN_AGRADO VARCHAR(500) COMMENT 'Expresa y como agrado/desagrado',
	LN_ESTADOS_EMOCION VARCHAR(500) COMMENT 'Expresa estados emocionales y respecto a que',
	LN_GESTO_ADIOS VARCHAR(500) COMMENT 'Utiliza el gesto de adios',
	LN_ORDENES_SENCILLAS VARCHAR(500) COMMENT 'Comprende ordenes sencillas',
	LN_ORDENES_COMPLEJAS VARCHAR(500) COMMENT 'Comprende ordenes complejas',
	LN_SE_ENTIENDE VARCHAR(500) COMMENT 'Se le entiende lo que dice',

	PRIMARY KEY (LN_ID_NINO),
	FOREIGN KEY(LN_ID_NINO) REFERENCES NINO(N_ID_NINO)
	ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS SESION_NINO
(
	S_ID_NINO INT(5) NOT NULL COMMENT 'Identificador Nino', 
	S_ID_SESION INT(5) NOT NULL COMMENT 'Identificar Sesion',
	S_VER VARCHAR(1) NOT NULL COMMENT 'Si se puede ver por el tutor S o N',
	S_COMENTARIO VARCHAR(100) NOT NULL COMMENT 'Comentario sesion',
	S_FECHA DATETIME NOT NULL COMMENT 'Fecha de la sesion',

	PRIMARY KEY (S_ID_NINO, S_ID_SESION),
	FOREIGN KEY(S_ID_NINO) REFERENCES NINO(N_ID_NINO)
	ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS CONTENIDO
(
	C_ID_CONTENIDO INT(5) NOT NULL COMMENT 'Identificador Contenido',
	C_CASTELLANO VARCHAR(200) COMMENT 'Texto en castellano',
	C_EUSKERA VARCHAR(200) COMMENT 'Texto en euskera',
	C_NOMBRE VARCHAR(40) UNIQUE NOT NULL COMMENT 'Nombre Identicativo del Contenido',
	C_IMAGEN VARCHAR(100) COMMENT 'Url a imagen Contenido',

	PRIMARY KEY(C_ID_CONTENIDO)
);

CREATE TABLE IF NOT EXISTS GRUPO
(
	G_ID_GRUPO INT(5) NOT NULL COMMENT 'Identificador Gruppo',
	G_NOMBRE VARCHAR(40) NOT NULL COMMENT 'Nombre Identicativo del Grupo',
	G_CONTENIDO_SOLUCION INT (5) NOT NULL COMMENT 'Identificador Contenido Solucion',
	G_CONTENIDO_OPCION INT (5) NOT NULL COMMENT 'Identificador Contenido Opcion',

	PRIMARY KEY (G_ID_GRUPO, G_CONTENIDO_SOLUCION, G_CONTENIDO_OPCION),
	FOREIGN KEY (G_CONTENIDO_SOLUCION) REFERENCES CONTENIDO (C_ID_CONTENIDO)
	ON DELETE CASCADE,
	FOREIGN KEY (G_CONTENIDO_OPCION) REFERENCES CONTENIDO (C_ID_CONTENIDO)
	ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS MINIJUEGO
(
	M_ID_MINIJUEGO INT(2) NOT NULL COMMENT 'Identificador Minijuego' , 
	M_NOMBRE VARCHAR(40) NOT NULL COMMENT 'Nombre Minijuego',
	M_DESCRIPCION VARCHAR(500) NOT NULL COMMENT 'Descripcion Minijuego (Instrucciones)' ,
	M_TAMANO INT(1) NOT NULL COMMENT 'Tamaño maximo de cada grupo',
	M_MINIMO INT(1) DEFAULT 4 COMMENT 'Cantidad minima de contenidos fijados inicialmente en un juego para el usuario',
	M_MAXIMO INT(1) DEFAULT 4 COMMENT 'Cantidad maxima de contenidos fijados inicialmente en un juego para el usuario',

	PRIMARY KEY(M_ID_MINIJUEGO)
);

CREATE TABLE IF NOT EXISTS PERMISOS_MINIJUEGOS
(
	PM_ID_MINIJUEGO INT(2) NOT NULL COMMENT 'Identificador Minijuego', 
	PM_ID_NINO INT(5) NOT NULL COMMENT 'Identificador Usuario' , 
	PM_MAXIMO INT(1) DEFAULT 4 COMMENT 'Cantidad maxima de contenidos en un juego para el usuario',
	PM_MINIMO INT(1) DEFAULT 4 COMMENT 'Cantidad minima de contenidos en un juego para el usuario',
	PM_PUNTUACION_MAXIMA INT(2) NOT NULL COMMENT 'Puntuacion de partida (multiple de 3)', 

	PRIMARY KEY(PM_ID_MINIJUEGO, PM_ID_NINO),
	FOREIGN KEY (PM_ID_MINIJUEGO) REFERENCES MINIJUEGO(M_ID_MINIJUEGO)
	ON DELETE CASCADE,
	FOREIGN KEY (PM_ID_NINO) REFERENCES NINO(N_ID_NINO)
	ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS PERMISOS_GRUPOS
(
	PG_ID_GRUPO INT(5) NOT NULL COMMENT 'Identificador Gruppo',
	PG_ID_NINO INT (5) NOT NULL COMMENT 'Identificador Contenido Padre',
	
	PRIMARY KEY (PG_ID_GRUPO, PG_ID_NINO),
	FOREIGN KEY (PG_ID_NINO) REFERENCES NINO (N_ID_NINO)
	ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS GRUPO_MINIJUEGOS
(
	GM_ID_GRUPO INT(5) NOT NULL COMMENT 'Identificador Grupo',
	GM_ID_MINIJUEGO INT(2) NOT NULL COMMENT 'Identificador Minijuego',

	PRIMARY KEY(GM_ID_GRUPO, GM_ID_MINIJUEGO),
	FOREIGN KEY (GM_ID_MINIJUEGO) REFERENCES MINIJUEGO(M_ID_MINIJUEGO)
	ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS PREMIO
(
	PR_ID_PREMIO INT(5) NOT NULL COMMENT 'Identificador Premio',
	PR_NOMBRE VARCHAR(40) NOT NULL UNIQUE COMMENT 'Nombre Premio', 
	PR_IMAGEN VARCHAR(100) NOT NULL COMMENT 'Imagen Premio',

	PRIMARY KEY(PR_ID_PREMIO)
);

CREATE TABLE IF NOT EXISTS PREMIOS_USUARIOS
(
	PU_ID_PREMIO INT(5) NOT NULL COMMENT 'Identificador Premio',
	PU_ID_NINO INT(5) NOT NULL COMMENT 'Identificador Nino', 
	PU_CONSEGUIDO VARCHAR(2) NOT NULL DEFAULT 'No' COMMENT 'Conseguido el premio Si o No',
	PU_FECMOD DATETIME NOT NULL COMMENT 'Fecha creacion/modificacion',

	PRIMARY KEY(PU_ID_PREMIO, PU_ID_NINO),
	FOREIGN KEY (PU_ID_PREMIO) REFERENCES PREMIO (PR_ID_PREMIO)
	ON DELETE CASCADE,
	FOREIGN KEY (PU_ID_NINO) REFERENCES NINO (N_ID_NINO)
	ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS FONDO
(
	F_ID_FONDO INT(3) NOT NULL COMMENT 'Identificador Fondo', 
	F_NOMBRE VARCHAR(20) NOT NULL UNIQUE COMMENT 'Nombre del fondo (identificativo)', 
	F_IMAGEN VARCHAR(100) NOT NULL COMMENT 'Url a imagen Fondo',
	F_FECMOD DATETIME NOT NULL COMMENT 'Fecha de creacion', 

	PRIMARY KEY(F_ID_FONDO)
);

CREATE TABLE IF NOT EXISTS MAPA
(
	MA_ID_MAPA INT(3) NOT NULL COMMENT 'Identificador Mapa', 
	MA_NOMBRE VARCHAR(40) NOT NULL UNIQUE COMMENT 'Nombre Identificativo Mapa',
	MA_IMAGEN VARCHAR(100) NOT NULL COMMENT 'Url a imagen Mapa', 
	MA_FECMOD DATETIME NOT NULL COMMENT 'Fecha de creacion',

	PRIMARY KEY(MA_ID_MAPA)
);

CREATE TABLE IF NOT EXISTS BOTONES_MAPA
(
	BM_ID_MAPA INT(3) NOT NULL COMMENT 'Identificador Mapa', 
	BM_ID_BOTON INT(5) NOT NULL COMMENT 'Identificador Boton respecto al mapa',
	BM_POSICION_X FLOAT(7,3) NOT NULL COMMENT 'Posicion X del Boton', 
	BM_POSICION_Y FLOAT(7,3) NOT NULL COMMENT 'Posicion Y del Boton', 
	BM_WIDTH FLOAT(7,3) NOT NULL COMMENT 'Anchura del Boton',
	BM_HEIGHT FLOAT(7,3) NOT NULL COMMENT 'Altura del Boton',

	PRIMARY KEY(BM_ID_MAPA, BM_ID_BOTON),
	FOREIGN KEY(BM_ID_MAPA) REFERENCES MAPA(MA_ID_MAPA)
	ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS HISTORIAL_MAPAS
(
	HM_ID_NINO INT(5) NOT NULL COMMENT 'Identificador Usuario',
	HM_ID_MAPA INT(3) NOT NULL COMMENT 'Identificador Mapa', 
	HM_POS_MAPA INT(5) NOT NULL COMMENT 'Posicion del Mapa en el Juego',
	HM_ID_MINIJUEGO INT(2) NOT NULL COMMENT 'Identificador Minijuego', 
	HM_ID_BOTON INT(3) NOT NULL COMMENT 'Identificador boton', 
	HM_ESTADO VARCHAR(6) NOT NULL COMMENT 'ACTUAL: Posicion Jugador. FUTURO: Nivel futuro. PASADO: Nivel superado',
	HM_FECHA DATETIME NOT NULL COMMENT 'Fecha creacion/Modificacion',


	PRIMARY KEY(HM_ID_NINO, HM_ID_MAPA, HM_POS_MAPA, HM_ID_MINIJUEGO, HM_ID_BOTON),
	FOREIGN KEY(HM_ID_NINO) REFERENCES NINO(N_ID_NINO)
	ON DELETE CASCADE,
	FOREIGN KEY(HM_ID_MAPA) REFERENCES MAPA(MA_ID_MAPA)
	ON DELETE CASCADE,
	FOREIGN KEY(HM_ID_MINIJUEGO) REFERENCES MINIJUEGO(M_ID_MINIJUEGO)
	ON DELETE CASCADE,	
	FOREIGN KEY(HM_ID_MAPA, HM_ID_BOTON) REFERENCES BOTONES_MAPA(BM_ID_MAPA, BM_ID_BOTON)
	ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS HISTORIAL_JUEGO
(
	HJ_ID_NINO INT(5) NOT NULL COMMENT 'Identificador Usuario',
	HJ_ID_MINIJUEGO INT(2) NOT NULL COMMENT 'Identificador Minijuego', 
	HJ_FECHA DATETIME NOT NULL COMMENT 'Fecha en jugar',
	HJ_COMPLETO VARCHAR(1) DEFAULT 'N' COMMENT 'S: Se ha completado el nivel, N: No se ha completado el nivel',
	HJ_VIDAS INT(2) NOT NULL COMMENT 'Vidas que le han quedado',
	HJ_ERRORES INT(3) NOT NULL COMMENT 'Errores cometidos',
	HJ_TIEMPO INT(10) NOT NULL COMMENT 'Tiempo en pasarse el Minijuego', 

	PRIMARY KEY(HJ_ID_NINO, HJ_ID_MINIJUEGO, HJ_FECHA),
	FOREIGN KEY(HJ_ID_NINO) REFERENCES NINO(N_ID_NINO)
	ON DELETE CASCADE,
	FOREIGN KEY(HJ_ID_MINIJUEGO) REFERENCES MINIJUEGO(M_ID_MINIJUEGO)
	ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS TS_CONTENIDO
(
	TSC_ID_CONTENIDO INT(5) NOT NULL COMMENT 'Identificador Contenido',
	TSC_ACCION VARCHAR(1) NOT NULL COMMENT 'Ultima Accion Realizada',
	TSC_FECMOD DATETIME NOT NULL COMMENT 'Fecha Modificacion',

	PRIMARY KEY(TSC_ID_CONTENIDO)
);

CREATE TABLE IF NOT EXISTS TS_GRUPO
(
	TSG_ID_GRUPO INT(5) NOT NULL COMMENT 'Identificador Grupo',
	TSG_CONTENIDO_SOLUCION INT (5) NOT NULL COMMENT 'Identificador Contenido Solucion',
	TSG_CONTENIDO_OPCION INT (5) NOT NULL COMMENT 'Identificador Contenido Opcion',
	TSG_ID_NINO INT(5) NOT NULL COMMENT 'Identificador Usuario',
	TSG_ACCION VARCHAR(1) NOT NULL COMMENT 'Ultima Accion Realizada',
	TSG_FECMOD DATETIME NOT NULL COMMENT 'Fecha Modificacion',

	PRIMARY KEY(TSG_ID_GRUPO, TSG_ID_NINO, TSG_CONTENIDO_SOLUCION, TSG_CONTENIDO_OPCION)
);

CREATE TABLE IF NOT EXISTS TS_MINIJUEGO
(
	TSM_ID_MINIJUEGO INT(2) NOT NULL COMMENT 'Identificador Minijuego',
	TSM_ID_NINO INT(5) NOT NULL COMMENT 'Identificador Usuario',
	TSM_ACCION VARCHAR(1) NOT NULL COMMENT 'Ultima Accion Realizada',
	TSM_FECMOD DATETIME NOT NULL COMMENT 'Fecha Modificacion',

	PRIMARY KEY(TSM_ID_MINIJUEGO, TSM_ID_NINO)
);

CREATE TABLE IF NOT EXISTS TS_GRUPO_MINIJUEGOS
(
	TSGM_ID_MINIJUEGO INT(2) NOT NULL COMMENT 'Identificador Minijuego',
	TSGM_ID_GRUPO INT(5) NOT NULL COMMENT 'Identificador Grupo',
	TSGM_ACCION VARCHAR(1) NOT NULL COMMENT 'Ultima Accion Realizada',
	TSGM_FECMOD DATETIME NOT NULL COMMENT 'Fecha Modificacion',

	PRIMARY KEY(TSGM_ID_GRUPO, TSGM_ID_MINIJUEGO)
);

CREATE TABLE IF NOT EXISTS TS_BOTONES_MAPA
(
	TSBM_ID_MAPA INT(2) NOT NULL COMMENT 'Identificador Mapa',
	TSBM_ID_BOTON INT(2) NOT NULL COMMENT 'Identificador Boton',
	TSBM_ACCION VARCHAR(1) NOT NULL COMMENT 'Ultima Accion Realizada',
	TSBM_FECMOD DATETIME NOT NULL COMMENT 'Fecha Modificacion',

	PRIMARY KEY(TSBM_ID_MAPA, TSBM_ID_BOTON)
);