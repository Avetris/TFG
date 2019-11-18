package bd.ibatis.mappers;

import java.util.List;
import java.util.Map;

import bd.modelo.Grupo;
import bd.modelo.Nino;

public interface GrupoMapper
{
	public List<Grupo> getGrupos();

	public int existeGrupo(Map<String, Object> parametros);
	
	public int esBorrableMinijuego(Map<String, Object> parametros);
	public int esBorrableUsuario(Map<String, Object> parametros);
	public void insertGrupo(Grupo grupo);
	public void insertGrupoUsuarios(Grupo grupo);
	public void insertGrupoNuevo(Grupo grupo);
	public void updateGrupo(Grupo grupo);
	public void deleteGrupo(Map<String, Object> parametros);
	

	public List<Nino> getPermisos(Grupo grupo);
	public void insertPermisos(Grupo grupo);
	public void deletePermisos(Map<String, Object> parametros);
	
}