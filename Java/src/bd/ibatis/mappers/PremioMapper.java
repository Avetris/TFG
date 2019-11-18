package bd.ibatis.mappers;

import java.util.List;
import java.util.Map;

import bd.modelo.Premio;

public interface PremioMapper
{
	public List<Premio> getPremios();

	public int existePremio(Map<String, Object> parametros);
	public void insertPremio(Premio premio);
}