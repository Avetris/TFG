package bd.ibatis.daos;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import bd.ibatis.MyBatisUtil;
import bd.ibatis.mappers.GrupoMapper;
import bd.modelo.Grupo;
import bd.modelo.Nino;

public class GrupoDAO {	
	
	public List<Grupo> getGrupos() throws Exception
	{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();

		try {
			GrupoMapper mapper = sqlSession.getMapper(GrupoMapper.class);
			return mapper.getGrupos();
		} 
		finally {
			if(sqlSession != null) sqlSession.close();
		}
	}
		
	public boolean insertGrupoNuevo(Grupo grupo, Boolean usuarios)
	{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();

		try {
			GrupoMapper mapper = sqlSession.getMapper(GrupoMapper.class);
			mapper.insertGrupoNuevo(grupo);
			if(usuarios) mapper.insertGrupoUsuarios(grupo);
			sqlSession.commit();
			sqlSession.close();
			return true;
		} catch(Exception e){
			e.printStackTrace();
			if(sqlSession != null)
			{
				sqlSession.rollback();
				sqlSession.close();
			}
			return false;
		}
	}
	
	public boolean insertGrupo(Grupo grupo, Boolean usuarios)
	{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();

		try {
			GrupoMapper mapper = sqlSession.getMapper(GrupoMapper.class);
			mapper.insertGrupo(grupo);
			if(usuarios) mapper.insertGrupoUsuarios(grupo);
			sqlSession.commit();
			sqlSession.close();
			return true;
		} catch(Exception e){
			e.printStackTrace();
			if(sqlSession != null)
			{
				sqlSession.rollback();
				sqlSession.close();
			}
			return false;
		}
	}
	

	public boolean existeGrupo(String nombre) throws Exception 
	{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
		try {
			GrupoMapper mapper = sqlSession.getMapper(GrupoMapper.class);
			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("nombre", nombre);
			return mapper.existeGrupo(parametros) != 0;
			
		} 
		finally {
			if(sqlSession != null)
			{
				sqlSession.close();
			}
		}
	}
	
	public boolean updateGrupo(List<Grupo> grupos)
	{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();

		try {
			GrupoMapper mapper = sqlSession.getMapper(GrupoMapper.class);
			for(Grupo grupo : grupos){
				mapper.updateGrupo(grupo);				
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
	
	public int esBorrable(Integer id) throws Exception{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
		try {
			GrupoMapper mapper = sqlSession.getMapper(GrupoMapper.class);
			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("idGrupo", id);
			if(mapper.esBorrableMinijuego(parametros) == 0){
				if(mapper.esBorrableUsuario(parametros) == 0){
					return 0;
				}else{
					return 2;
				}
			}else{
				return 1; 				
			}
		} 
		finally {
			if(sqlSession != null)
			{
				sqlSession.close();
			}
		}
	}
	
	public boolean deleteGrupo(Integer[][] valores) 
	{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();

		try {
			GrupoMapper mapper = sqlSession.getMapper(GrupoMapper.class);
			HashMap<String, Object> parametros = new HashMap<String, Object>();
			for(Integer[] v : valores){
				parametros.put("idGrupo", v[0]);	
				parametros.put("solucion", v[1]);
				parametros.put("opcion", v[2]);	
				mapper.deleteGrupo(parametros);			
			}	
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
	
	public List<Nino> getPermisos(Grupo grupo) throws Exception{
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();

		try {
			GrupoMapper mapper = sqlSession.getMapper(GrupoMapper.class);
			return mapper.getPermisos(grupo);
		} 
		finally {
			if(sqlSession != null) sqlSession.close();
		}
	}
	
	public boolean insertarPermisos(Grupo grupo){
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();

		try {
			GrupoMapper mapper = sqlSession.getMapper(GrupoMapper.class);
			mapper.insertPermisos(grupo);
			sqlSession.commit();
			sqlSession.close();
			if(sqlSession != null) sqlSession.close();
			return true;
		} catch (Exception e){
			e.printStackTrace();
			if(sqlSession != null) sqlSession.close();
			return false;
		}
	}
	
	public boolean deletePermisos(Integer idGrupo, List<Integer> ninos){
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();

		try {
			GrupoMapper mapper = sqlSession.getMapper(GrupoMapper.class);
			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("idGrupo", idGrupo);
			for(Integer v : ninos){
				parametros.put("idNino", v);	
				mapper.deletePermisos(parametros);
			}
			sqlSession.commit();
			sqlSession.close();
			if(sqlSession != null) sqlSession.close();
			return true;
		} catch (Exception e){
			e.printStackTrace();
			if(sqlSession != null) sqlSession.close();
			return false;
		}
	}
}
