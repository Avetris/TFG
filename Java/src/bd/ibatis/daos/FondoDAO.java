package bd.ibatis.daos;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import bd.ibatis.MyBatisUtil;
import bd.ibatis.mappers.FondoMapper;
import bd.modelo.Fondo;

public class FondoDAO {	
		
	public List<Fondo> getFondos() throws Exception	{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
		try {
			FondoMapper mapper = sqlSession.getMapper(FondoMapper.class);
			return mapper.getFondos(); 
		} 
		finally {
			if(sqlSession != null)
			{
				sqlSession.close();
			}
		}
	}
	
	public boolean existeFondo(String nombre) throws Exception
	{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
		try {
			FondoMapper mapper = sqlSession.getMapper(FondoMapper.class);
			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("nombre", nombre);
			if(mapper.existeFondo(parametros) == 0){
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
	
	public boolean insertFondo(Fondo fondo)
	{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
		try {
			FondoMapper mapper = sqlSession.getMapper(FondoMapper.class);
			mapper.insertFondo(fondo);
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
