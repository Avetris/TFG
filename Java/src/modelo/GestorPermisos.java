package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import bd.ibatis.daos.GrupoDAO;
import bd.ibatis.daos.MinijuegoDAO;
import bd.ibatis.daos.NinoDAO;
import bd.modelo.Contenido;
import bd.modelo.Grupo;
import bd.modelo.Minijuego;
import bd.modelo.Nino;
import bd.modelo.NinoPermisos;

public class GestorPermisos extends Observable implements Runnable{
	
	private static GestorPermisos miGestorPermisos = new GestorPermisos();
	private GrupoDAO grupoDAO;
	private NinoDAO ninoDAO;
	private MinijuegoDAO minijuegoDAO;
	private List<Grupo> grupos = null;
	private List<Nino> ninos = null;
	private List<Nino> ninosActuales = null;
	private List<Minijuego> minijuegos = null;
	private Grupo grupoActual;
	private Minijuego minijuegoActual;

	public final int accionCargar = 1;
	public final int accionCargarError = 2;
	public final int accionDatosError = 3;
	public final int accionInsertarMinijuego = 4;
	public final int accionEliminarMinijuego = 5;
	public final int accionInsertarGrupo = 6;
	public final int accionEliminarGrupo = 7;
	public final int accionInsertarMinijuegoError = 8;
	public final int accionEliminarMinijuegoError = 9;
	public final int accionInsertarGrupoError = 10;
	public final int accionEliminarGrupoError = 11;
	
	private int accion;
	private Integer nino, maximo, minimo, puntuacion;	
	private List<Integer> parameterEliminar;	
	
	private GestorPermisos(){
		grupoDAO = new GrupoDAO();
		minijuegoDAO = new MinijuegoDAO();
		ninoDAO = new NinoDAO();
	}
	
	public static GestorPermisos getGestorPermisos(){
		if(miGestorPermisos == null){
			miGestorPermisos = new GestorPermisos();
		}
		return miGestorPermisos;
	}

	public Object[][] obtenerPermisosGrupo(){
		Object[][] valores = null;
		if(grupoActual != null){
			List<Nino> list = grupoActual.getNinos();
			valores = new Object[list == null ? 1 : list.size()+1][2];
			for(int i = 0; i < valores.length-1; i++){
				valores[i][0] = list.get(i).getNombre()+" "+list.get(i).getApellidos();
				valores[i][1] = new Boolean(false);
			}
			valores[valores.length-1][0] = "";
			valores[valores.length-1][1] = new Boolean(false);
		}		
		return valores;
	}
	
	public Object[][] obtenerPermisosMinijuegos(){
		Object[][] valores = null;
		if(minijuegoActual != null){
			List<NinoPermisos> list = minijuegoActual.getNinos();
			valores = new Object[list == null ? 1 : list.size()+1][7];
			for(int i = 0; i < valores.length-1; i++){
				valores[i][0] = list.get(i).getNombre()+" "+list.get(i).getApellidos();
				valores[i][1] = list.get(i).getMinimo();
				valores[i][2] = minijuegoActual.getMinimo();
				valores[i][3] = list.get(i).getMaximo();
				valores[i][4] = minijuegoActual.getMaximo();
				valores[i][5] = list.get(i).getPuntuacionMaxima();
				valores[i][6] = new Boolean(false);
			}
			valores[valores.length-1][0] = "";
			valores[valores.length-1][1] = minijuegoActual.getMinimo();
			valores[valores.length-1][2] = minijuegoActual.getMinimo();
			valores[valores.length-1][3] = minijuegoActual.getMaximo();
			valores[valores.length-1][4] = minijuegoActual.getMaximo();
			valores[valores.length-1][5] = 3;
			valores[valores.length-1][6] = new Boolean(false);
		}	
		return valores;
	}
	
	public String[] obtenerNinos(){
		String[] nombres = new String[ninos.size()+1];
		nombres[0] = " ";
		for(int i = 0; i < ninos.size(); i++) nombres[i+1] = ninos.get(i).getNombre()+" "+ninos.get(i).getApellidos();
		return nombres;
	}
	
	public String[] obtenerNombresGrupos(){		
		String[] nombres = new String[grupos.size()];
		for(int i = 0; i < grupos.size(); i++){
			nombres[i] = grupos.get(i).getNombre();
		}
		return nombres;
	}
	
	public String[] obtenerNombresMinijuegos(){		
		String[] nombres = new String[minijuegos.size()];
		for(int i = 0; i < minijuegos.size(); i++){
			nombres[i] = minijuegos.get(i).getNombre();
		}
		return nombres;
	}
	
	private void insertarGrupo(Integer posNino){
		List<Nino> listaNinos = grupoActual.getNinos();
		if(listaNinos == null) listaNinos = new ArrayList<>();
		Nino nino = ninos.get(posNino);
		if(nino != null) listaNinos.add(nino);
		if(grupoDAO.insertarPermisos(grupoActual)){
			try{
				grupoActual.setNinos(grupoDAO.getPermisos(grupoActual));
				setChanged();
				notifyObservers(accionInsertarGrupo);	
			}catch(Exception e){
				setChanged();
				notifyObservers(accionCargar);
			}
		}else{
			setChanged();
			notifyObservers(accionInsertarGrupoError);				
		}
	}
	
	private void eliminarGrupo(List<Integer> listaPos){
		List<Integer> listaIndices = new ArrayList<>();
		for(int i = 0; i < listaPos.size(); i++){
			listaIndices.add(grupoActual.getNinos().get(listaPos.get(i)).getIdUsuario());
		}
		if(listaIndices.size() > 0){
			if(grupoDAO.deletePermisos(grupoActual.getIdGrupo(), listaIndices)){
				try{
					grupoActual.setNinos(grupoDAO.getPermisos(grupoActual));
					setChanged();
					notifyObservers(accionEliminarGrupo);
				}catch(Exception e){
					setChanged();
					notifyObservers(accionCargar);
				}
			}else{			
				setChanged();
				notifyObservers(accionEliminarGrupoError);
			}
		}else{
			setChanged();
			notifyObservers(accionEliminarGrupoError);
			
		}		
	}
	
	public void setGrupoActual(int pos){
		if(pos >= 0){
			grupoActual = grupos.get(pos);
			minijuegoActual = null;
			ninosActuales = new ArrayList<>();
			for(Nino nino : ninos){
				boolean bien = false;
				for(int i = 0; i < grupoActual.getNinos().size() && !bien; i++){
					bien = grupoActual.getNinos().get(i).getIdUsuario() != nino.getIdUsuario();
				}
				if(bien){
					switch (nino.getIdiomaJuego()) {
					case "espanol":
						if(grupoActual.getContenidoSolucion().getCastellano() != null  && grupoActual.getContenidoSolucion().getCastellano().trim().length() > 0){
							for(Contenido contenido : grupoActual.getContenidoOpciones()){
								if(contenido.getCastellano() == null || contenido.getCastellano().trim().length() == 0){
									bien = false;
								}
							}
							if(bien) ninosActuales.add(nino);
						}
						break;
					case "euskera":
						if(grupoActual.getContenidoSolucion().getEuskera() != null && grupoActual.getContenidoSolucion().getEuskera().trim().length() > 0){
							for(Contenido contenido : grupoActual.getContenidoOpciones()){
								if(contenido.getEuskera() == null || contenido.getEuskera().trim().length() == 0){
									bien = false;
								}
							}
							if(bien) ninosActuales.add(nino);
						}
						break;
					}
				}
			}
		}
	}
	public void setMinijuegoActual(int pos){
		if(pos >= 0){
			minijuegoActual = minijuegos.get(pos);
			grupoActual = null;
			ninosActuales = ninos;
		}
	}
	
	private void insertarMinijuego(Integer posNino, Integer maximo, Integer minimo, Integer puntuacionMaxima){
		List<NinoPermisos> listaNinos = minijuegoActual.getNinos();
		if(listaNinos == null) listaNinos = new ArrayList<>();
		NinoPermisos nino = new NinoPermisos(ninos.get(posNino).getIdUsuario(), ninos.get(posNino).getNombre(), ninos.get(posNino).getApellidos(), maximo, minimo, puntuacionMaxima);
		if(nino != null) listaNinos.add(nino);
		if(minijuegoDAO.insertarPermisos(minijuegoActual)){
			try{
				minijuegoActual.setNinos(minijuegoDAO.getPermisos(minijuegoActual));
				setChanged();
				notifyObservers(accionInsertarMinijuego);
			}catch(Exception e){
				setChanged();
				notifyObservers(accionCargar);
			}
		}else{
			setChanged();
			notifyObservers(accionInsertarMinijuegoError);				
		}
	}
	
	private void eliminarMinijuego(List<Integer> listaPos){
		List<Integer> listaIndices = new ArrayList<>();
		for(int i = 0; i < listaPos.size(); i++){
			listaIndices.add(minijuegoActual.getNinos().get(listaPos.get(i)).getIdUsuario());
		}
		if(listaIndices.size() > 0){
			if(minijuegoDAO.deletePermisos(minijuegoActual.getIdMinijuego(), listaIndices)){
				try{
					minijuegoActual.setNinos(minijuegoDAO.getPermisos(minijuegoActual));
					setChanged();
					notifyObservers(accionEliminarMinijuego);
				}catch(Exception e){
					setChanged();
					notifyObservers(accionCargar);
				}				
			}else{			
				setChanged();
				notifyObservers(accionEliminarMinijuegoError);
			}
		}else{
			setChanged();
			notifyObservers(accionEliminarMinijuegoError);
			
		}		
	}
	
	public void setParametrosYAccion(Integer accion, Integer nino, Integer maximo, Integer minimo, Integer puntuacionMaxima, List<Integer> paramEliminar){
		this.accion = accion;
		this.nino = nino;
		this.maximo= maximo;
		this.minimo = minimo;
		this.puntuacion = puntuacionMaxima;
		this.parameterEliminar = paramEliminar;
	}

	@Override
	public void run() {
		if(accion == accionCargar){
			try{
				grupos = grupoDAO.getGrupos();
				ninos = ninoDAO.getNinos();
				minijuegos = minijuegoDAO.getMinijuegos();
				for(Grupo grupo : grupos) grupo.setNinos(grupoDAO.getPermisos(grupo));
				for(Minijuego minijuego : minijuegos) minijuego.setNinos(minijuegoDAO.getPermisos(minijuego));
				if(grupos.size() == 0 || ninos.size() == 0 || minijuegos.size() == 0){
					setChanged();
					notifyObservers(accionDatosError);						
				}
				setChanged();
				notifyObservers(accionCargar);
			}catch(Exception e){
				e.printStackTrace();
				setChanged();
				notifyObservers(accionCargarError);				
			}
		}else if(accion == accionInsertarGrupo){					
			insertarGrupo(nino);		
		}else if(accion == accionEliminarGrupo){
			eliminarGrupo(parameterEliminar);	
		}else if(accion == accionInsertarMinijuego){					
			insertarMinijuego(nino, maximo, minimo, puntuacion);		
		}else if(accion == accionEliminarMinijuego){
			eliminarMinijuego(parameterEliminar);	
		}
	}
}
