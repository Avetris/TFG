package bd.ibatis.mappers;

import java.util.List;
import java.util.Map;

import bd.modelo.HistorialMinijuegos;
import bd.modelo.Nino;
import bd.modelo.Sesion;

public interface NinoMapper
{
	public List<Nino> getNinos(Map<String, Object> parametros);
	public List<Sesion> getSesiones(Nino nino);
	public List<HistorialMinijuegos> getHistorialMinijuegos(Nino nino);
	
	public int existeIdNino(Map<String, Object> parametros);
	
	public void insertNino(Nino nino);
	public void insertNinoAnamnesis(Nino nino);
	public void insertNinoAutonomia(Nino nino);
	public void insertNinoSocializacion(Nino nino);
	public void insertNinoLenguaje(Nino nino);
	
	public void updateNino(Nino nino);
	public void updateNinoAnamnesis(Nino nino);
	public void updateNinoAutonomia(Nino nino);
	public void updateNinoSocializacion(Nino nino);
	public void updateNinoLenguaje(Nino nino);
	
	public void insertSesion(Sesion sesion);
	public void updateSesion(Sesion sesion);	

	public void cambiarContrasena(Map<String, Object> parametros);
	public String getNombre(Map<String, Object> parametros);
}