package bd.ibatis.mappers;

import java.util.List;
import java.util.Map;

import bd.modelo.Boton;
import bd.modelo.Mapa;

public interface MapaMapper
{
	public List<Mapa> getMapas();
	public int existeMapa(Map<String, Object> parametros);
	
	public int insertMapa(Mapa mapa);
	public int insertBoton(Boton boton);
	public void updateBoton(Boton boton);
	public void deleteBoton(Boton boton);
}