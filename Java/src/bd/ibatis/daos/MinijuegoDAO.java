package bd.ibatis.daos;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import bd.ibatis.MyBatisUtil;
import bd.ibatis.mappers.MinijuegoMapper;
import bd.modelo.Minijuego;
import bd.modelo.NinoPermisos;

public class MinijuegoDAO {

	public List<Minijuego> getMinijuegos() throws Exception {
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();

		try {
			MinijuegoMapper mapper = sqlSession.getMapper(MinijuegoMapper.class);
			List<Minijuego> lista = mapper.getMinijuegos();
			if (lista == null || lista.isEmpty()) {
				mapper.insercionInical();
				sqlSession.commit();
				lista = mapper.getMinijuegos();
			}
			return lista;
		} finally {
			if (sqlSession != null)
				sqlSession.close();
		}
	}

	public boolean existeNombre(Integer id, String nombre) throws Exception {

		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
		try {
			MinijuegoMapper mapper = sqlSession.getMapper(MinijuegoMapper.class);
			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("nombre", nombre);
			parametros.put("idMinijuego", id);
			return mapper.existeNombre(parametros) != 0;

		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
	}

	public boolean updateMinijuego(Minijuego minijuego) {
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();

		try {
			MinijuegoMapper mapper = sqlSession.getMapper(MinijuegoMapper.class);
			mapper.updateMinijuego(minijuego);
			sqlSession.commit();
			sqlSession.close();
			return true;
		} catch (Exception e) {
			if (sqlSession != null) {
				sqlSession.rollback();
				sqlSession.close();
			}
			return false;
		}
	}

	public boolean insertGrupoMinijuego(Integer idMinijuego, Integer idGrupo) {
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();

		try {
			MinijuegoMapper mapper = sqlSession.getMapper(MinijuegoMapper.class);
			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("idMinijuego", idMinijuego);
			parametros.put("idGrupo", idGrupo);
			mapper.insertGrupoMinijuego(parametros);
			sqlSession.commit();
			sqlSession.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			if (sqlSession != null) {
				sqlSession.rollback();
				sqlSession.close();
			}
			return false;
		}
	}

	public boolean deleteGrupoMinijuego(Integer idMinijuego, List<Integer> idGrupos) {
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();

		try {
			MinijuegoMapper mapper = sqlSession.getMapper(MinijuegoMapper.class);
			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("idMinijuego", idMinijuego);
			for (Integer id : idGrupos) {
				parametros.put("idGrupo", id);
				mapper.deleteGrupoMinijuego(parametros);
			}
			sqlSession.commit();
			sqlSession.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			if (sqlSession != null) {
				sqlSession.rollback();
				sqlSession.close();
			}
			return false;
		}
	}

	public List<NinoPermisos> getPermisos(Minijuego minijuego) throws Exception {
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
		try {
			MinijuegoMapper mapper = sqlSession.getMapper(MinijuegoMapper.class);
			return mapper.getPermisos(minijuego);
		} finally {
			if (sqlSession != null)
				sqlSession.close();
		}
	}

	public boolean insertarPermisos(Minijuego minijuego) {
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();

		try {
			MinijuegoMapper mapper = sqlSession.getMapper(MinijuegoMapper.class);
			mapper.insertPermisos(minijuego);
			sqlSession.commit();
			sqlSession.close();
			if (sqlSession != null)
				sqlSession.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			if (sqlSession != null)
				sqlSession.close();
			return false;
		}
	}

	public boolean deletePermisos(Integer idGrupo, List<Integer> ninos) {
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();

		try {
			MinijuegoMapper mapper = sqlSession.getMapper(MinijuegoMapper.class);
			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("idMinijuego", idGrupo);
			for (Integer v : ninos)
				parametros.put("idNino", v);
			mapper.deletePermisos(parametros);
			sqlSession.commit();
			sqlSession.close();
			if (sqlSession != null)
				sqlSession.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			if (sqlSession != null)
				sqlSession.close();
			return false;
		}
	}
}
