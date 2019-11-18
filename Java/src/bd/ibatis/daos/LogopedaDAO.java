package bd.ibatis.daos;

import java.util.HashMap;

import org.apache.ibatis.session.SqlSession;

import bd.ibatis.MyBatisUtil;
import bd.ibatis.mappers.LogopedaMapper;
import bd.modelo.Logopeda;
import modelo.SHA1;

public class LogopedaDAO {	
		
	public Logopeda getLogopeda(String usuario, String contrasena)  throws Exception
	{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
		try {
			LogopedaMapper mapper = sqlSession.getMapper(LogopedaMapper.class);
			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("usuario", usuario);
			parametros.put("contrasena", contrasena);	
			
			return mapper.getLogopeda(parametros); 
		} 
		finally {
			if(sqlSession != null)
			{
				sqlSession.close();
			}
		}
	}
	
	public boolean insertLogopeda(Logopeda logopeda) throws Exception
	{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();

		try {
			LogopedaMapper mapper = sqlSession.getMapper(LogopedaMapper.class);
			mapper.insertLogopeda(logopeda);
			sqlSession.commit();
			sqlSession.close();
			return true;
		} 
		finally {
			if(sqlSession != null)
			{
				sqlSession.rollback();
				sqlSession.close();
			}
		}
	}
	
	public void updateLogopeda(Logopeda logopeda)
	{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();

		try {
			LogopedaMapper mapper = sqlSession.getMapper(LogopedaMapper.class);
			mapper.updateLogopeda(logopeda);
			sqlSession.commit();
			sqlSession.close();
		} 
		finally {
			if(sqlSession != null)
			{
				sqlSession.rollback();
				sqlSession.close();
			}
		}
	}
	
	public int cambiarContrasena(String usuario) throws Exception
	{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();

		try {
			LogopedaMapper mapper = sqlSession.getMapper(LogopedaMapper.class);
			HashMap<String, Object> parametros = new HashMap<>();
			parametros.put("usuario", usuario);
			String nombre = mapper.getNombre(parametros);
			if(nombre != null && nombre.length() > 0){
				parametros.put("contrasena", SHA1.getStringMensageDigest(nombre));			
				mapper.cambiarContrasena(parametros);
			}else{
				return -1;
			}
			sqlSession.commit();
			sqlSession.close();
			return 0;
		} 
		finally {
			if(sqlSession != null)
			{
				sqlSession.rollback();
				sqlSession.close();
			}
		}
	}
}
