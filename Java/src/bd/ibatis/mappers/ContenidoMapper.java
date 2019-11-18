package bd.ibatis.mappers;

import java.util.List;
import java.util.Map;

import bd.modelo.Contenido;

public interface ContenidoMapper
{
	public List<Contenido> getContenidos();

	public int existeContenido(Map<String, Object> parametros);
	public void insertContenido(Contenido contenido);
	public void updateContenido(Contenido contenido);
	public void deleteContenido(int i);
}