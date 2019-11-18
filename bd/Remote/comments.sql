--------------------------------------------USUARIO------------------------------------

COMMENT ON COLUMN USUARIO.ID_USUARIO IS 'Identificador Usuario';
COMMENT ON COLUMN USUARIO.NOMBRE IS 'Nombre Usuario';
COMMENT ON COLUMN USUARIO.APELLIDOS IS 'Apellido Usuario';
COMMENT ON COLUMN USUARIO.EDAD IS 'Edad Usuario';
COMMENT ON COLUMN USUARIO.CONTRASENA IS 'Contrasena Usuario';
COMMENT ON COLUMN USUARIO.LOGOPEDA IS 'Si el usuario es logopeda';

--------------------------------------------IDIOMAS_USUARIOS------------------------------------

COMMENT ON COLUMN IDIOMAS_USUARIOS.ID_USUARIO IS 'Identificador Usuario';
COMMENT ON COLUMN IDIOMAS_USUARIOS.IDIOMA IS 'Idioma habilitado para Usuario';

--------------------------------------------CONTENIDO------------------------------------

COMMENT ON COLUMN CONTENIDO.ID_USUARIO IS 'Identificador Contenido';
COMMENT ON COLUMN CONTENIDO.ID_TEXTO IS 'Texto Contenido';
COMMENT ON COLUMN CONTENIDO.IMAGEN IS 'Imagen Contenido';
COMMENT ON COLUMN CONTENIDO.SONIDO IS 'Sonido Contenido';

--------------------------------------------CATEGORIA------------------------------------

COMMENT ON COLUMN CATEGORIA.ID_CATEGORIA IS 'Identificador Categoria';
COMMENT ON COLUMN CATEGORIA.NOMBRE IS 'Nombre Categoria';

--------------------------------------------CATEGORIA_CONTENIDO------------------------------------

COMMENT ON COLUMN CATEGORIA_CONTENIDO.ID_CATEGORIA IS 'Identificador Categoria';
COMMENT ON COLUMN CATEGORIA_CONTENIDO.ID_CONTENIDO IS 'Identificador Contenido';

--------------------------------------------MINIJUEGO------------------------------------

COMMENT ON COLUMN MINIJUEGO.ID_MINIJUEGO IS 'Identificador Minijuego';
COMMENT ON COLUMN MINIJUEGO.NOMBRE IS 'Nombre Minijuego';
COMMENT ON COLUMN MINIJUEGO.DESCRIPCION IS 'Descripcion Minijuego';

--------------------------------------------PERMISOS_MINIJUEGOS------------------------------------

COMMENT ON COLUMN PERMISOS_MINIJUEGOS.ID_MINIJUEGO IS 'Identificador Minijuego';
COMMENT ON COLUMN PERMISOS_MINIJUEGOS.ID_USUARIO IS 'Identificador Usuario';

--------------------------------------------PERMISOS_CONTENIDO------------------------------------

COMMENT ON COLUMN PERMISOS_CONTENIDO.ID_USUARIO IS 'Identificador Usuario';
COMMENT ON COLUMN PERMISOS_CONTENIDO.ID_CONTENIDO IS 'Identificador Contenido';

--------------------------------------------PERMISOS_CATEGORIA------------------------------------

COMMENT ON COLUMN PERMISOS_CATEGORIA.ID_CATEGORIA IS 'Identificador Categoria';
COMMENT ON COLUMN PERMISOS_CATEGORIA.ID_USUARIO IS 'Identificador Usuario';

--------------------------------------------CONTENIDO_MINIJUEGOS------------------------------------

COMMENT ON COLUMN CONTENIDO_MINIJUEGOS.ID_MINIJUEGO IS 'Identificador Minijuego';
COMMENT ON COLUMN CONTENIDO_MINIJUEGOS.ID_CONTENIDO IS 'Identificador Contenido';

--------------------------------------------PREMIO------------------------------------

COMMENT ON COLUMN PREMIO.ID_PREMIO IS 'Identificador Premio';
COMMENT ON COLUMN PREMIO.NOMBRE IS 'Nombre Premio';
COMMENT ON COLUMN PREMIO.IMAGEN IS 'Imagen Premio';

--------------------------------------------PERMISOS_PREMIOS------------------------------------

COMMENT ON COLUMN PERMISOS_PREMIOS.ID_USUARIO IS 'Identificador Usuario';
COMMENT ON COLUMN PERMISOS_PREMIOS.ID_PREMIO IS 'Identificador Premio';

--------------------------------------------MAPAS------------------------------------

COMMENT ON COLUMN MAPAS.ID_MAPA IS 'Identificador Mapa';
COMMENT ON COLUMN MAPAS.IMAGEN IS 'Imagen Mapa';

--------------------------------------------BOTONES_MAPA------------------------------------

COMMENT ON COLUMN BOTONES_MAPA.ID_MAPA IS 'Identificador Mapa';
COMMENT ON COLUMN BOTONES_MAPA.ID_BOTON IS 'Identificador Boton respecto al mapa';
COMMENT ON COLUMN BOTONES_MAPA.POSICION_X IS 'Posicion X del Boton';
COMMENT ON COLUMN BOTONES_MAPA.POSICION_Y IS 'Posicion Y del Boton';

--------------------------------------------HISTORIAL_MINIJUEGOS------------------------------------

COMMENT ON COLUMN HISTORIAL_MINIJUEGOS.ID_USUARIO IS 'Identificador Usuario';
COMMENT ON COLUMN HISTORIAL_MINIJUEGOS.ID_MAPA IS 'Identificador Mapa';
COMMENT ON COLUMN HISTORIAL_MINIJUEGOS.ID_MINIJUEGO IS 'Identificador Minijuego';
COMMENT ON COLUMN HISTORIAL_MINIJUEGOS.CONT_BOTON IS 'Contador de boton en el mapa';
COMMENT ON COLUMN HISTORIAL_MINIJUEGOS.INTENTO IS 'Numero de intento en el nivel seleccionado';
COMMENT ON COLUMN HISTORIAL_MINIJUEGOS.PUNTUACION IS 'Puntuacion Obtenida en el nivel (Gane o pierda)';
COMMENT ON COLUMN HISTORIAL_MINIJUEGOS.TIEMPO IS 'Tiempo en pasarse el Minijuego';
COMMENT ON COLUMN HISTORIAL_MINIJUEGOS.FECHA IS 'Fecha en jugar';

--------------------------------------------HISTORIAL_MAPA------------------------------------

COMMENT ON COLUMN HISTORIAL_MAPA.ID_USUARIO IS 'Identificador Usuario';
COMMENT ON COLUMN HISTORIAL_MAPA.ID_MAPA IS 'Identificador Mapa';
COMMENT ON COLUMN HISTORIAL_MAPA.NIVELES_COMPLETOS IS 'Numero de niveles completos en el mapa';

