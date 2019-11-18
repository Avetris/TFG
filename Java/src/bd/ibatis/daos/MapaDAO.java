package bd.ibatis.daos;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import bd.ibatis.MyBatisUtil;
import bd.ibatis.mappers.MapaMapper;
import bd.modelo.Boton;
import bd.modelo.Mapa;

public class MapaDAO {

	public List<Mapa> getMapas() throws Exception {
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
		try {
			MapaMapper mapper = sqlSession.getMapper(MapaMapper.class);
			return mapper.getMapas();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
	}

	public boolean existeMapa(String nombre) throws Exception {
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
		try {
			MapaMapper mapper = sqlSession.getMapper(MapaMapper.class);
			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("nombre", nombre);
			if (mapper.existeMapa(parametros) == 0) {
				return false;
			} else {
				return true;
			}
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
	}

	public boolean insertMapa(Mapa mapa) {
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
		try {
			MapaMapper mapper = sqlSession.getMapper(MapaMapper.class);
			mapper.insertMapa(mapa);
			System.out.println(mapa.getIdMapa());
			for (Boton btn : mapa.getBotones()) {
				btn.setIdMapa(mapa.getIdMapa());
				mapper.insertBoton(btn);
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

	public boolean updateBotones(Mapa mapa) {
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();

		try {
			MapaMapper mapper = sqlSession.getMapper(MapaMapper.class);
			for (Boton btn : mapa.getBotones()) {
				btn.setIdMapa(mapa.getIdMapa());
				if (btn.getIdBoton() == null || btn.getIdBoton() == 0) {
					mapper.insertBoton(btn);
				} else if (btn.getPosicionX() == null || btn.getPosicionY() == null || btn.getHeight() == null
						|| btn.getWidth() == null) {
					mapper.deleteBoton(btn);
				} else {
					mapper.updateBoton(btn);
				}
			}
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
}
