package bd.ibatis.daos;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import bd.ibatis.MyBatisUtil;
import bd.ibatis.mappers.NinoMapper;
import bd.modelo.HistorialMinijuegos;
import bd.modelo.Nino;
import bd.modelo.Sesion;
import modelo.SHA1;

public class NinoDAO {	
		
	public Nino getNino(String usuario, String contrasena) throws Exception
	{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
		try {
			NinoMapper mapper = sqlSession.getMapper(NinoMapper.class);
			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("usuario", usuario);
			parametros.put("contrasena", contrasena);	
			
			List<Nino> l = mapper.getNinos(parametros);
			if(l != null && l.size() > 0)			
				return l.get(0);
			else
				return null;
		} 
		finally {
			if(sqlSession != null)
			{
				sqlSession.rollback();
				sqlSession.close();
			}
		}
	}	
	
	public Nino getNino(Integer idNino) throws Exception
	{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
		try {
			NinoMapper mapper = sqlSession.getMapper(NinoMapper.class);
			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("idNino", idNino);	
			
			List<Nino> l = mapper.getNinos(parametros);
			if(l != null && l.size() > 0)			
				return l.get(0);
			else
				return null;
		} 
		finally {
			if(sqlSession != null)
			{
				sqlSession.rollback();
				sqlSession.close();
			}
		}
	}
	
	public List<Sesion> getSesiones(Nino nino) throws Exception
	{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
		try {
			NinoMapper mapper = sqlSession.getMapper(NinoMapper.class);			
			return mapper.getSesiones(nino);
		} 
		finally {
			if(sqlSession != null)
			{
				sqlSession.rollback();
				sqlSession.close();
			}
		}
	}	
	
	public List<HistorialMinijuegos> getHistorial(Nino nino) throws Exception
	{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
		try {
			NinoMapper mapper = sqlSession.getMapper(NinoMapper.class);			
			return mapper.getHistorialMinijuegos(nino);
		} 
		finally {
			if(sqlSession != null)
			{
				sqlSession.rollback();
				sqlSession.close();
			}
		}
	}	
	
	public List<Nino> getNinos() throws Exception
	{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
		try {
			NinoMapper mapper = sqlSession.getMapper(NinoMapper.class);			
			return mapper.getNinos(new HashMap<String, Object>()); 
		} 
		finally {
			if(sqlSession != null)
			{
				sqlSession.rollback();
				sqlSession.close();
			}
		}
	}
	
	public boolean existeIdNino(String idNino) throws Exception
	{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
		try {
			NinoMapper mapper = sqlSession.getMapper(NinoMapper.class);
			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("idNino", idNino);
			return mapper.existeIdNino(parametros) != 0;
			
		} 
		finally {
			if(sqlSession != null)
			{
				sqlSession.close();
			}
		}
	}
	
	public boolean insertNino(Nino nino)
	{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
		try {
			NinoMapper mapper = sqlSession.getMapper(NinoMapper.class);
			mapper.insertNino(nino);
			mapper.insertNinoAnamnesis(nino);
			mapper.insertNinoAutonomia(nino);
			mapper.insertNinoSocializacion(nino);
			mapper.insertNinoLenguaje(nino);
			sqlSession.commit();
			sqlSession.close();
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
	
	public boolean updateNino(Nino nino)
	{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();

		try {
			NinoMapper mapper = sqlSession.getMapper(NinoMapper.class);
			mapper.updateNino(nino);
			mapper.updateNinoAnamnesis(nino);
			mapper.updateNinoAutonomia(nino);
			mapper.updateNinoSocializacion(nino);
			mapper.updateNinoLenguaje(nino);
			sqlSession.commit();
			sqlSession.close();
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
	
	public boolean insertSesion(Sesion sesion)
	{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();

		try {
			NinoMapper mapper = sqlSession.getMapper(NinoMapper.class);
			mapper.insertSesion(sesion);
			sqlSession.commit();
			sqlSession.close();
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
	
	public boolean updateSesion(Sesion sesion)
	{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();

		try {
			NinoMapper mapper = sqlSession.getMapper(NinoMapper.class);
			mapper.updateSesion(sesion);
			sqlSession.commit();
			sqlSession.close();
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
	
	public int cambiarContrasena(String usuario) throws Exception
	{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();

		try {
			NinoMapper mapper = sqlSession.getMapper(NinoMapper.class);
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
