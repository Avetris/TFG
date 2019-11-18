package bd.ibatis.daos;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import bd.ibatis.MyBatisUtil;
import bd.ibatis.mappers.PremioMapper;
import bd.modelo.Premio;

public class PremioDAO {	
		
	public List<Premio> getPremios() throws Exception
	{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
		try {
			PremioMapper mapper = sqlSession.getMapper(PremioMapper.class);
			return mapper.getPremios(); 
		} 
		finally {
			if(sqlSession != null)
			{
				sqlSession.close();
			}
		}
	}
	
	public boolean existePremio(String nombre)  throws Exception
	{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
		try {
			PremioMapper mapper = sqlSession.getMapper(PremioMapper.class);
			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("nombre", nombre);
			if(mapper.existePremio(parametros) == 0){
				return false;
			}else{
				return true; 				
			}
		} 
		finally {
			if(sqlSession != null)
			{
				sqlSession.close();
			}
		}
	}
	
	public boolean insertPremio(Premio premio)
	{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
		try {
			PremioMapper mapper = sqlSession.getMapper(PremioMapper.class);
			mapper.insertPremio(premio);
			sqlSession.commit();
			return true;
		} catch (Exception e){
			e.printStackTrace();
			if(sqlSession != null)
			{
				sqlSession.rollback();
				sqlSession.close();
			}
			return false;
		}
	}
}
