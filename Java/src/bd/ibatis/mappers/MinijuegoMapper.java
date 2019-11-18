package bd.ibatis.mappers;

import java.util.List;
import java.util.Map;

import bd.modelo.Minijuego;
import bd.modelo.NinoPermisos;

public interface MinijuegoMapper
{
	public List<Minijuego> getMinijuegos();

	public int existeNombre(Map<String, Object> parametros);

	public void insercionInical();
	public void updateMinijuego(Minijuego minijuego);
	public void insertGrupoMinijuego(Map<String, Object> parametros);
	public void deleteGrupoMinijuego(Map<String, Object> parametros);	

	public List<NinoPermisos> getPermisos(Minijuego minijuego);
	public void insertPermisos(Minijuego minijuego);
	public void deletePermisos(Map<String, Object> parametros);
}