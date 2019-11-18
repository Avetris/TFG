package bd.ibatis.mappers;

import java.util.Map;

import bd.modelo.Logopeda;

public interface LogopedaMapper
{
	public Logopeda getLogopeda(Map<String, Object> parametros);
	
	public void insertLogopeda(Logopeda logopeda);
	public void updateLogopeda(Logopeda logopeda);
	
	public void cambiarContrasena(Map<String, Object> parametros);
	public String getNombre(Map<String, Object> parametros);
}