package bd.ibatis.mappers;

import java.util.List;
import java.util.Map;

import bd.modelo.Fondo;

public interface FondoMapper
{
	public List<Fondo> getFondos();

	public int existeFondo(Map<String, Object> parametros);
	public void insertFondo(Fondo fondo);
}