package modelo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Observable;

import bd.ibatis.daos.NinoDAO;
import bd.modelo.HistorialMinijuegos;
import bd.modelo.Nino;
import bd.modelo.Sesion;

public class GestorNinos extends Observable implements Runnable {

	private static GestorNinos miGestorNinos;
	private NinoDAO ninoDAO;
	private List<Nino> ninos;
	private Nino ninoActual;

	public final Integer accionCargar = 1;
	public final int accionCargarError = 2;
	public final Integer accionInsertar = 3;
	public final Integer accionModificar = 4;
	public final Integer accionInsertarSesion = 5;
	public final Integer accionModificarSesion = 6;
	public final Integer accionInsertarError = 7;
	public final Integer accionModificarError = 8;
	public final Integer accionInsertarSesionError = 9;
	public final Integer accionModificarSesionError = 10;

	private Integer accionActual = null;
	private String[] datos = null;;

	private GestorNinos() {
		ninoDAO = new NinoDAO();
	}

	public static GestorNinos getGestorNinos() {
		if (miGestorNinos == null) {
			miGestorNinos = new GestorNinos();
		}
		return miGestorNinos;
	}

	public String[] obtenerListaNombreApellidos() {
		String[] nombreApellidos;
		if (ninos != null) {
			nombreApellidos = new String[ninos.size() + 1];
			nombreApellidos[0] = " ";
			for (int i = 0; i < ninos.size(); i++) {
				nombreApellidos[i + 1] = ninos.get(i).getNombre() + " " + ninos.get(i).getApellidos();
			}
		} else {
			nombreApellidos = new String[] { " " };
		}

		return nombreApellidos;
	}

	public String getNombre() {
		return ninoActual.getNombre() + " " + ninoActual.getApellidos();
	}
	
	public void setNino(int pos){
		if(pos >= 0){
			ninoActual = ninos.get(pos);
		}else{
			ninoActual = null;			
		}
	}

	public String[] obtenerInformacion() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		if (ninoActual != null) {
			String[] datos = new String[70];
			datos[0] = String.valueOf(ninoActual.getIdUsuario());
			datos[1] = ninoActual.getNombre();
			datos[2] = ninoActual.getApellidos();
			datos[3] = format.format(ninoActual.getFechaNacimiento());
			datos[4] = ninoActual.getIdiomaJuego();
			datos[5] = String.valueOf(ninoActual.getTelefono1());
			datos[6] = String.valueOf(ninoActual.getTelefono1());
			datos[7] = ninoActual.getNombrePadre();
			datos[8] = ninoActual.getProfesionPadre();
			datos[9] = ninoActual.getNombreMadre();
			datos[10] = ninoActual.getProfesionMadre();
			datos[11] = ninoActual.getHermanosEdades();
			datos[12] = ninoActual.getOtrasConvivencias();
			datos[13] = ninoActual.getEntrevistados();
			datos[14] = ninoActual.getMotivoConsulta();
			datos[15] = ninoActual.getEmbarazoParto();
			datos[16] = ninoActual.getDesFisico();
			datos[17] = ninoActual.getDesMotor();
			datos[18] = ninoActual.getDesOrofacial();
			datos[19] = ninoActual.getDesLenguaje();
			datos[20] = ninoActual.getAntecedentesFamiliares();
			datos[21] = ninoActual.getDatosMedicos();
			datos[22] = ninoActual.getHistorialEscolar();
			datos[23] = ninoActual.getAtenFueraCentro();
			datos[24] = ninoActual.getTipoAlimentos();
			datos[25] = ninoActual.getAlimentosPreferidos();
			datos[26] = ninoActual.getReconoceComer();
			datos[27] = ninoActual.getPideAlimento();
			datos[28] = ninoActual.getBiberonChupete();
			datos[29] = ninoActual.getMastica();
			datos[30] = ninoActual.getSuccionaTraga();
			datos[31] = ninoActual.getMuerdeCosas();
			datos[32] = ninoActual.getSaliva();
			datos[33] = ninoActual.getHorasSueno();
			datos[34] = ninoActual.getIntencionDormir();
			datos[35] = ninoActual.getReconoceDormir();
			datos[36] = ninoActual.getRonca();
			datos[37] = ninoActual.getControlEsfinteres();
			datos[38] = ninoActual.getAvisaSucio();
			datos[39] = ninoActual.getPideWC();
			datos[40] = ninoActual.getReconoceRopa();
			datos[41] = ninoActual.getNecesidadCambioRopa();
			datos[42] = ninoActual.getMalestarSucio();
			datos[43] = ninoActual.getSonarseNariz();
			datos[44] = ninoActual.getACargo();
			datos[45] = ninoActual.getMayorApego();
			datos[46] = ninoActual.getSinAtender();
			datos[47] = ninoActual.getReconoceFamiliares();
			datos[48] = ninoActual.getComportamientoDesconocidos();
			datos[49] = ninoActual.getJuegaNinos();
			datos[50] = ninoActual.getRelacionNinos();
			datos[51] = ninoActual.getJuegoFunional();
			datos[52] = ninoActual.getImitaAcciones();
			datos[53] = ninoActual.getObjetosPreferidos();
			datos[54] = ninoActual.getRechazaObjetos();
			datos[55] = ninoActual.getJuegaEnCasa();
			datos[56] = ninoActual.getReconoceJuguetes();
			datos[57] = ninoActual.getIntencionalidadComunicativa();
			datos[58] = ninoActual.getQuienComunica();
			datos[59] = ninoActual.getComoComunica();
			datos[60] = ninoActual.getLlamaAtencion();
			datos[61] = ninoActual.getIntenaAtencion();
			datos[62] = ninoActual.getPideNecesita();
			datos[63] = ninoActual.getGestoRechazo();
			datos[64] = ninoActual.getAgrado();
			datos[65] = ninoActual.getEstadosEmocion();
			datos[66] = ninoActual.getGestoAdios();
			datos[67] = ninoActual.getOrdenesSencillas();
			datos[68] = ninoActual.getOrdenesComplejas();
			datos[69] = ninoActual.getSeEntiende();
			return datos;
		} else {
			return null;
		}
	}

	private void insertar(String[] datosNino) {
		try{
			String[] d = datosNino[3].split("-");
			Calendar cal = Calendar.getInstance();
			cal.set(Integer.valueOf(d[0]), Integer.valueOf(d[1])-1, Integer.valueOf(d[2]));
			Nino nino = new Nino(datosNino[0].length()>0 ? new Integer(datosNino[0]) : null, datosNino[1] + " " + datosNino[2], datosNino[1], datosNino[2],
					SHA1.getStringMensageDigest(datosNino[1] + " " + datosNino[2]), "espanol", datosNino[4], cal.getTime(), new Integer(datosNino[5]),
					datosNino[6] != null && datosNino[6].length() > 0 ? new Integer(datosNino[6]) : null);
			nino.setNombrePadre(datos[7]);
			nino.setProfesionPadre(datos[8]);
			nino.setNombreMadre(datos[9]);
			nino.setProfesionMadre(datos[10]);
			nino.setHermanosEdades(datos[11]);
			nino.setOtrasConvivencias(datos[12]);
			nino.setEntrevistados(datos[13]);
			nino.setMotivoConsulta(datos[14]);
			nino.setEmbarazoParto(datos[15]);
			nino.setDesFisico(datos[16]);
			nino.setDesMotor(datos[17]);
			nino.setDesOrofacial(datos[18]);
			nino.setDesLenguaje(datos[19]);
			nino.setAntecedentesFamiliares(datos[20]);
			nino.setDatosMedicos(datos[21]);
			nino.setHistorialEscolar(datos[22]);
			nino.setAtenFueraCentro(datos[23]);
			nino.setTipoAlimentos(datos[24]);
			nino.setAlimentosPreferidos(datos[25]);
			nino.setReconoceComer(datos[26]);
			nino.setPideAlimento(datos[27]);
			nino.setBiberonChupete(datos[28]);
			nino.setMastica(datos[29]);
			nino.setSuccionaTraga(datos[30]);
			nino.setMuerdeCosas(datos[31]);
			nino.setSaliva(datos[32]);
			nino.setHorasSueno(datos[33]);
			nino.setIntencionDormir(datos[34]);
			nino.setReconoceDormir(datos[35]);
			nino.setRonca(datos[36]);
			nino.setControlEsfinteres(datos[37]);
			nino.setAvisaSucio(datos[38]);
			nino.setPideWC(datos[39]);
			nino.setReconoceRopa(datos[40]);
			nino.setNecesidadCambioRopa(datos[41]);
			nino.setMalestarSucio(datos[42]);
			nino.setSonarseNariz(datos[43]);
			nino.setACargo(datos[44]);
			nino.setMayorApego(datos[45]);
			nino.setSinAtender(datos[46]);
			nino.setReconoceFamiliares(datos[47]);
			nino.setComportamientoDesconocidos(datos[48]);
			nino.setJuegaNinos(datos[49]);
			nino.setRelacionNinos(datos[50]);
			nino.setJuegoFunional(datos[52]);
			nino.setImitaAcciones(datos[52]);
			nino.setObjetosPreferidos(datos[53]);
			nino.setRechazaObjetos(datos[54]);
			nino.setJuegaEnCasa(datos[55]);
			nino.setReconoceJuguetes(datos[56]);
			nino.setIntencionalidadComunicativa(datos[57]);
			nino.setQuienComunica(datos[58]);
			nino.setComoComunica(datos[59]);
			nino.setLlamaAtencion(datos[60]);
			nino.setIntenaAtencion(datos[61]);
			nino.setPideNecesita(datos[62]);
			nino.setGestoRechazo(datos[63]);
			nino.setAgrado(datos[64]);
			nino.setEstadosEmocion(datos[65]);
			nino.setGestoAdios(datos[66]);
			nino.setOrdenesSencillas(datos[67]);
			nino.setOrdenesComplejas(datos[68]);
			nino.setSeEntiende(datos[69]);
			if(ninoDAO.insertNino(nino)){
				ninos = ninoDAO.getNinos();
				for (int i = 0; i < ninos.size(); i++) {
					ninos.get(i).setSesiones(ninoDAO.getSesiones(ninos.get(i)));
					ninos.get(i).setHistorialMinijuego(ninoDAO.getHistorial(ninos.get(i)));
				}
				setChanged();
				notifyObservers(accionInsertar);
			}else{
				setChanged();
				notifyObservers(accionInsertarError);			
			}
		}catch (Exception e){
			e.printStackTrace();
			setChanged();
			notifyObservers(accionInsertarError);
		}
	}
	
	private void modificar(String[] datosNino) {
		try{
			String[] d = datosNino[3].split("-");
			Calendar cal = Calendar.getInstance();
			cal.set(Integer.valueOf(d[0]), Integer.valueOf(d[1])-1, Integer.valueOf(d[2]));
			Nino nino = new Nino(datosNino[0].length()>0 ? new Integer(datosNino[0]) : null, null, datosNino[1], datosNino[2],
					null, "espanol", datosNino[4], cal.getTime(), new Integer(datosNino[5]),
					new Integer(datosNino[6]));
			nino.setNombrePadre(datos[7]);
			nino.setProfesionPadre(datos[8]);
			nino.setNombreMadre(datos[9]);
			nino.setProfesionMadre(datos[10]);
			nino.setHermanosEdades(datos[11]);
			nino.setOtrasConvivencias(datos[12]);
			nino.setEntrevistados(datos[13]);
			nino.setMotivoConsulta(datos[14]);
			nino.setEmbarazoParto(datos[15]);
			nino.setDesFisico(datos[16]);
			nino.setDesMotor(datos[17]);
			nino.setDesOrofacial(datos[18]);
			nino.setDesLenguaje(datos[19]);
			nino.setAntecedentesFamiliares(datos[20]);
			nino.setDatosMedicos(datos[21]);
			nino.setHistorialEscolar(datos[22]);
			nino.setAtenFueraCentro(datos[23]);
			nino.setTipoAlimentos(datos[24]);
			nino.setAlimentosPreferidos(datos[25]);
			nino.setReconoceComer(datos[26]);
			nino.setPideAlimento(datos[27]);
			nino.setBiberonChupete(datos[28]);
			nino.setMastica(datos[29]);
			nino.setSuccionaTraga(datos[30]);
			nino.setMuerdeCosas(datos[31]);
			nino.setSaliva(datos[32]);
			nino.setHorasSueno(datos[33]);
			nino.setIntencionDormir(datos[34]);
			nino.setReconoceDormir(datos[35]);
			nino.setRonca(datos[36]);
			nino.setControlEsfinteres(datos[37]);
			nino.setAvisaSucio(datos[38]);
			nino.setPideWC(datos[39]);
			nino.setReconoceRopa(datos[40]);
			nino.setNecesidadCambioRopa(datos[41]);
			nino.setMalestarSucio(datos[42]);
			nino.setSonarseNariz(datos[43]);
			nino.setACargo(datos[44]);
			nino.setMayorApego(datos[45]);
			nino.setSinAtender(datos[46]);
			nino.setReconoceFamiliares(datos[47]);
			nino.setComportamientoDesconocidos(datos[48]);
			nino.setJuegaNinos(datos[49]);
			nino.setRelacionNinos(datos[50]);
			nino.setJuegoFunional(datos[52]);
			nino.setImitaAcciones(datos[52]);
			nino.setObjetosPreferidos(datos[53]);
			nino.setRechazaObjetos(datos[54]);
			nino.setJuegaEnCasa(datos[55]);
			nino.setReconoceJuguetes(datos[56]);
			nino.setIntencionalidadComunicativa(datos[57]);
			nino.setQuienComunica(datos[58]);
			nino.setComoComunica(datos[59]);
			nino.setLlamaAtencion(datos[60]);
			nino.setIntenaAtencion(datos[61]);
			nino.setPideNecesita(datos[62]);
			nino.setGestoRechazo(datos[63]);
			nino.setAgrado(datos[64]);
			nino.setEstadosEmocion(datos[65]);
			nino.setGestoAdios(datos[66]);
			nino.setOrdenesSencillas(datos[67]);
			nino.setOrdenesComplejas(datos[68]);
			nino.setSeEntiende(datos[69]);
			nino.setSesiones(new ArrayList<Sesion>());
			if(ninoDAO.updateNino(nino)){
				ninos = ninoDAO.getNinos();
				for (int i = 0; i < ninos.size(); i++) {
					ninos.get(i).setSesiones(ninoDAO.getSesiones(ninos.get(i)));
				}
				setChanged();
				notifyObservers(accionModificar);
			}else{
				setChanged();
				notifyObservers(accionModificarError);			
			}
		}catch (Exception e){
			e.printStackTrace();
			setChanged();
			notifyObservers(accionModificarError);
		}
	}
	
	public Boolean existeNino(String idNino){
		try{
			return ninoDAO.existeIdNino(idNino);
		}catch (Exception e){
			setChanged();
			notifyObservers(accionCargarError);
			return null;
		}
	}
	
	public String[][] obtenerSesiones(){
		if(ninoActual == null){
			return null;
		}else{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String[][] sesiones = new String[ninoActual.getSesiones().size()][4];
			int i = 0;
			for(Sesion s : ninoActual.getSesiones()){
				sesiones[i][0] = String.valueOf(s.getIdSesion());
				sesiones[i][1] = format.format(s.getFecha());
				sesiones[i][2] = s.getVer();
				sesiones[i][3] = s.getComentario();				
				i++;
			}
			return sesiones;
		}		
	}
	
	public String[][] obtenerHistorial(){
		if(ninoActual == null){
			return null;
		}else{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String[][] historial = new String[ninoActual.getHistorialMinijuego().size()][6];
			int i = 0;
			for(HistorialMinijuegos h : ninoActual.getHistorialMinijuego()){
				historial[i][0] = h.getNombre();
				historial[i][1] = format.format(h.getFecha());
				historial[i][2] = h.getCompleto().equals("S") ? "Si" : "No";
				historial[i][3] = String.valueOf(h.getVidas());
				historial[i][4] = String.valueOf(h.getErrores());		
				historial[i][5] = String.valueOf(h.getTiempo());			
				i++;
			}
			return historial;
		}		
	}
	
	private void insertarSesion(String[] datosSesion){
		try{
			String[] d = datosSesion[1].split("-");
			Calendar cal = Calendar.getInstance();
			cal.set(Integer.valueOf(d[0]), Integer.valueOf(d[1])-1, Integer.valueOf(d[2]));
			Sesion sesion = new Sesion(ninoActual.getIdUsuario(), Integer.valueOf(datosSesion[0]), datosSesion[2], datosSesion[3], cal.getTime());
			if(ninoDAO.insertSesion(sesion)){
				ninoActual.setSesiones(ninoDAO.getSesiones(ninoActual));
				setChanged();
				notifyObservers(accionInsertarSesion);
			}else{
				setChanged();
				notifyObservers(accionInsertarSesionError);			
			}
		}catch (Exception e){
			e.printStackTrace();
			setChanged();
			notifyObservers(accionInsertarSesionError);
		}
	}
	
	private void modificarSesion(String[] datosSesion){
		try{
			String[] d = datosSesion[1].split("-");
			Calendar cal = Calendar.getInstance();
			cal.set(Integer.valueOf(d[0]), Integer.valueOf(d[1])-1, Integer.valueOf(d[2]));
			Sesion sesion = new Sesion(ninoActual.getIdUsuario(), Integer.valueOf(datosSesion[0]), datosSesion[2], datosSesion[3], cal.getTime());
			if(ninoDAO.updateSesion(sesion)){
				ninoActual.setSesiones(ninoDAO.getSesiones(ninoActual));
				setChanged();
				notifyObservers(accionModificarSesion);
			}else{
				setChanged();
				notifyObservers(accionModificarSesionError);			
			}
		}catch (Exception e){
			e.printStackTrace();
			setChanged();
			notifyObservers(accionModificarSesionError);
		}		
	}

	public void setDatos(Integer accion, String[] datos) {
		this.accionActual = accion;
		this.datos = datos;
	}
	
	public void salirPantalla(){
		ninoActual = null;
	}
	
	

	@Override
	public void run() {
		if (accionActual == accionCargar) {
			try{
				if (GestorSesion.obtSesion().esLogopeda() == 0) {
					ninos = ninoDAO.getNinos();
					for (int i = 0; i < ninos.size(); i++) {
						ninos.get(i).setSesiones(ninoDAO.getSesiones(ninos.get(i)));
						ninos.get(i).setHistorialMinijuego(ninoDAO.getHistorial(ninos.get(i)));
					}
				} else {
					ninos = new ArrayList<>();
					ninoActual = ninoDAO.getNino(GestorSesion.obtSesion().getIdNino());
					ninoActual.setSesiones(ninoDAO.getSesiones(ninoActual));
					ninos.add(ninoActual);
				}
				setChanged();
				notifyObservers(accionCargar);
			}catch(Exception e){
				e.printStackTrace();
				setChanged();
				notifyObservers(accionCargarError);
			}
		} else if (accionActual == accionInsertar) {
			insertar(datos);
		} else if (accionActual == accionModificar) {
			modificar(datos);
		} else if (accionActual == accionInsertarSesion) {
			insertarSesion(datos);
		} else if (accionActual == accionModificarSesion) {
			modificarSesion(datos);
		}
	}
}
