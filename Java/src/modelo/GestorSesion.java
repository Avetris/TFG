package modelo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Observable;

import org.apache.ibatis.exceptions.PersistenceException;

import bd.ibatis.daos.LogopedaDAO;
import bd.ibatis.daos.NinoDAO;
import bd.modelo.Logopeda;
import bd.modelo.Nino;
import bd.modelo.Usuario;

public class GestorSesion extends Observable implements Runnable {

	private static GestorSesion mSesion;
	private LogopedaDAO logopedaDAO;
	private NinoDAO ninoDAO;
	private Usuario usuario;

	public final int accionLogin = 1;
	public final int accionCargarError = 2;
	public final int accionRegistro = 3;
	public final int accionLoginCorrecto = 4;
	public final int accionLoginIncorrecto = 5;
	public final int accionRegistroUsuario = 6;
	public final int accionRecuperarContrasena = 7;
	public final int accionRegistroError = 8;
	public final int accionRecuperarContrasenaError = 9;
	public final int accionModificacion = 10;
	public final int accionModificacionError = 11;

	private Object[] datos;
	private int accionActual;

	private GestorSesion() {
		logopedaDAO = new LogopedaDAO();
		ninoDAO = new NinoDAO();
	}

	public static GestorSesion obtSesion() {
		if (mSesion == null) {
			mSesion = new GestorSesion();
		}
		return mSesion;
	}

	public String obtNombreUsuario() {
		if (usuario != null) {
			return usuario.getUsuario();
		} else {
			return null;
		}
	}
	
	public void borrarUsuario(){
		usuario = null;
	}

	private void registrarse(String pNombreUsuario, String pNombre, String pApellidos, String pContrasena,
			String pIdioma) {

		Logopeda logopedaAux = new Logopeda(pNombreUsuario, pNombre, pApellidos,
				SHA1.getStringMensageDigest(pContrasena), pIdioma);
		try {
			boolean resultado = logopedaDAO.insertLogopeda(logopedaAux);
			if (resultado) {
				IdiomaProperties.getIdiomaProperties().setIdioma(logopedaAux.getIdioma());
			}
			setChanged();
			notifyObservers(accionRegistro);
		} catch (PersistenceException e) {
			setChanged();
			notifyObservers(accionRegistroUsuario);
		} catch (Exception e) {
			setChanged();
			notifyObservers(accionRegistroError);
		}
	}

	private void identificarse(String pNombreUsuario, String pContrasena, boolean pLogopeda, boolean pRecordar) {
		boolean bien = false;
		try {
			if ((boolean) datos[2]) {
				Logopeda logopeda = logopedaDAO.getLogopeda(pNombreUsuario, SHA1.getStringMensageDigest(pContrasena));
				if (logopeda == null) {
					bien = false;
				} else {
					usuario = logopeda;
					bien = true;
				}
			} else {
				Nino nino = ninoDAO.getNino(pNombreUsuario, SHA1.getStringMensageDigest(pContrasena));
				if (nino == null) {
					bien = false;
				} else {
					usuario = nino;
					bien = true;
				}
			}
			if (bien) {
				IdiomaProperties.getIdiomaProperties().setIdioma(usuario.getIdioma());
				if ((boolean) datos[3]) {
					IdiomaProperties.getIdiomaProperties().guardarUsuario(usuario.getUsuario(), usuario.getIdioma(),
							(boolean) datos[2]);
				}
				setChanged();
				notifyObservers(accionLoginCorrecto);
			} else {
				setChanged();
				notifyObservers(accionLoginIncorrecto);
			}
		} catch (Exception e) {
			setChanged();
			notifyObservers(accionCargarError);
		}
	}

	public void setDatos(String pNombreUsuario, String pContrasena, Boolean pLogopeda, Boolean pRecordar) {
		this.accionActual = accionLogin;
		datos = new Object[] { pNombreUsuario, pContrasena, pLogopeda, pRecordar };
	}

	public void setDatosRegistro(String pNombreUsuario, String pNombre, String pApellidos, String pContrasena,
			String pIdioma) {
		this.accionActual = accionRegistro;
		datos = new Object[] { pNombreUsuario, pNombre, pApellidos, pContrasena, pIdioma };
	}

	public void setDatosRecuperar(String pNombreUsuario, Boolean pLogopeda) {
		this.accionActual = accionRecuperarContrasena;
		datos = new Object[] { pNombreUsuario, pLogopeda };
	}

	public void setDatosActualizar(String pNombreUsuario, String pNombre, String pApellidos, String pContrasena,
			String pIdioma, String pFechaNacimiento, String pTelefono1, String pTelefono2) {
		this.accionActual = accionModificacion;
		datos = new Object[] { pNombreUsuario, pNombre, pApellidos, pContrasena, pIdioma, pFechaNacimiento, pTelefono1,
				pTelefono2 };
	}

	public int esLogopeda() {
		if (usuario == null) {
			return -1;
		} else if (usuario instanceof Logopeda) {
			return 0;
		} else if (usuario instanceof Nino) {
			return 1;
		} else {
			return -2;
		}
	}

	public boolean contrasenaCorrecta(String contrasena) {
		boolean correcto = false;
		int logo = esLogopeda();
		try {
			if (logo == 0) {
				Logopeda logopeda = logopedaDAO.getLogopeda(usuario.getUsuario(),
						SHA1.getStringMensageDigest(contrasena));
				if (logopeda != null) {
					correcto = true;
				}
			} else if (logo == 1) {
				Nino nino = ninoDAO.getNino(usuario.getUsuario(), SHA1.getStringMensageDigest(contrasena));
				if (nino != null) {
					correcto = true;
				}
			}
		} catch (Exception e) {

		}
		return correcto;
	}

	private void actualizarDatos(String pNombreUsuario, String pNombre, String pApellidos, String pContrasena,
			String pIdioma, String pFechaNacimiento, Integer pTelefono1, Integer pTelefono2) {
		boolean bien = false;
		int logo = esLogopeda();
		if (logo == 0) {
			Logopeda aux = new Logopeda(usuario.getIdUsuario(), pNombreUsuario, pNombre, pApellidos, SHA1.getStringMensageDigest(pContrasena),
					pIdioma);
			if (pContrasena == null || pContrasena.trim().length() == 0) {
				aux.setContrasena(null);
			}
			logopedaDAO.updateLogopeda(aux);
			bien = true;
			String[] us = IdiomaProperties.getIdiomaProperties().obtenerUsuario();
			if (us != null && us.length == 2) {
				if (us[0].equals(usuario.getUsuario())) {
					IdiomaProperties.getIdiomaProperties().guardarUsuario(aux.getUsuario(), aux.getIdioma(), true);
				}
			}
			usuario = aux;
		} else if (logo == 1) {
			String[] d = pFechaNacimiento.split("-");
			Calendar cal = Calendar.getInstance();
			cal.set(Integer.valueOf(d[0]), Integer.valueOf(d[1])-1, Integer.valueOf(d[2]));
			Nino aux = new Nino(usuario.getIdUsuario(), pNombreUsuario, pNombre, pApellidos,
					SHA1.getStringMensageDigest(pContrasena), pIdioma, ((Nino) usuario).getIdiomaJuego(),
					cal.getTime(), pTelefono1, pTelefono2);
			System.out.println(aux.getContrasena()+"; "+aux.getUsuario());
			if (pContrasena == null || pContrasena.trim().length() == 0) {
				aux.setContrasena(null);
			}
			ninoDAO.updateNino(aux);
			bien = true;
			String[] us = IdiomaProperties.getIdiomaProperties().obtenerUsuario();
			if (us != null && us.length == 2) {
				if (us[0].equals(usuario.getUsuario())) {
					IdiomaProperties.getIdiomaProperties().guardarUsuario(aux.getUsuario(), aux.getIdioma(), false);
				}
			}
			usuario = aux;
		}
		setChanged();
		if(bien){
			notifyObservers(accionModificacion);
		}else{
			notifyObservers(accionModificacionError);			
		}
	}

	public Object[] obtenerDatos() {
		Object[] datos = null;
		int logo = esLogopeda();
		if (logo == 0) {
			Logopeda log = (Logopeda) usuario;
			datos = new Object[] { log.getUsuario(), log.getNombre(), log.getApellidos(), log.getIdioma() };
		} else if (logo == 1) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Nino nino = (Nino) usuario;
			datos = new Object[] { nino.getUsuario(), nino.getNombre(), nino.getApellidos(), nino.getIdioma(),
					format.format(nino.getFechaNacimiento()), nino.getTelefono1(), nino.getTelefono2()};
		}
		return datos;
	}

	public String[] obtUsuarioYContrasena() {
		if (usuario != null) {
			return new String[] { usuario.getUsuario(), usuario.getContrasena() };
		} else {
			return null;
		}
	}

	public void finSesion(boolean pIniciado) {
		usuario = null;
	}

	private void recuperarCotrasena(String pUsuario, Boolean esLogopeda) {
		try {
			int res = (esLogopeda ? logopedaDAO.cambiarContrasena(pUsuario) : ninoDAO.cambiarContrasena(pUsuario));
			if (res == 0) {
				setChanged();
				notifyObservers(accionRecuperarContrasena);
			} else {
				setChanged();
				notifyObservers(accionRecuperarContrasenaError);
			}
		} catch (Exception e) {
			setChanged();
			notifyObservers(accionCargarError);
		}

	}

	public Integer getIdNino() {
		return usuario.getIdUsuario();
	}

	@Override
	public void run() {
		if (accionActual == accionLogin) {
			identificarse(String.valueOf(datos[0]), String.valueOf(datos[1]), (boolean) datos[2], (boolean) datos[3]);
		} else if (accionActual == accionRegistro) {
			registrarse(String.valueOf(datos[0]), String.valueOf(datos[1]), String.valueOf(datos[2]),
					String.valueOf(datos[3]), String.valueOf(datos[4]));
		} else if (accionActual == accionRecuperarContrasena) {
			recuperarCotrasena(String.valueOf(datos[0]), (boolean) datos[1]);
		} else if (accionActual == accionModificacion) {
			actualizarDatos(String.valueOf(datos[0]), String.valueOf(datos[1]), String.valueOf(datos[2]),
					datos[3] == null ? null : String.valueOf(datos[3]),
					datos[4] == null ? null : String.valueOf(datos[4]),
					datos[5] == null ? null : String.valueOf(datos[5]),
					datos[6] == null ? null : Integer.valueOf(String.valueOf(datos[6])),
					datos[7] == null || datos[7].equals("") ? null : Integer.valueOf(String.valueOf(datos[7])));
		}
	}
}