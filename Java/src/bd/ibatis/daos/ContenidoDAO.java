package bd.ibatis.daos;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import bd.ibatis.MyBatisUtil;
import bd.ibatis.mappers.ContenidoMapper;
import bd.modelo.Contenido;

public class ContenidoDAO {	
	
	public List<Contenido> getContenidos() throws Exception
	{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();

		try {
			ContenidoMapper mapper = sqlSession.getMapper(ContenidoMapper.class);
			return mapper.getContenidos();
		} 
		finally {
			if(sqlSession != null) sqlSession.close();
		}
	}
	
	public boolean insertContenido(Contenido contenido)
	{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();

		try {
			ContenidoMapper mapper = sqlSession.getMapper(ContenidoMapper.class);
			mapper.insertContenido(contenido);
			sqlSession.commit();
			sqlSession.close();
			return true;
		} catch(Exception e){
			if(sqlSession != null)
			{
				sqlSession.rollback();
				sqlSession.close();
			}
			return false;
		}
	}
	

	public boolean existeContenido(String nombre) throws Exception 
	{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
		try {
			ContenidoMapper mapper = sqlSession.getMapper(ContenidoMapper.class);
			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("nombre", nombre);
			if(mapper.existeContenido(parametros) == 0){
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
	
	public boolean updateContenido(List<Contenido> contenidos)
	{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();

		try {
			ContenidoMapper mapper = sqlSession.getMapper(ContenidoMapper.class);
			for(Contenido contenido : contenidos){
				mapper.updateContenido(contenido);				
			}
			sqlSession.commit();
			sqlSession.close();
			return true;
		} 
		catch(Exception e){
			if(sqlSession != null)
			{
				sqlSession.rollback();
				sqlSession.close();
			}
			return false;			
		}
	}
	
	public boolean deleteContenido(List<Integer> contenido) 
	{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();

		try {
			ContenidoMapper mapper = sqlSession.getMapper(ContenidoMapper.class);
			for(int i : contenido)
				mapper.deleteContenido(i);
			sqlSession.commit();
			sqlSession.close();
			return true;
		} catch(Exception e){

			if(sqlSession != null)
			{
				sqlSession.rollback();
				sqlSession.close();
			}
			return false;			
		}
	}
}
