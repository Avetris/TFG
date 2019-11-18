package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import bd.ibatis.daos.ContenidoDAO;
import bd.ibatis.daos.GrupoDAO;
import bd.modelo.Contenido;
import bd.modelo.Grupo;

public class GestorGrupos extends Observable implements Runnable{
	
	private static GestorGrupos miGestorGrupos = new GestorGrupos();
	private GrupoDAO grupoDAO;
	private List<Grupo> grupos = null;
	private ContenidoDAO contenidoDAO;
	private List<Contenido> contenidos = null;
	private Grupo grupoActual;

	public final int accionCargar = 1;
	public final int accionCargarError = 2;
	public final int accionInsertar = 3;
	public final int accionEliminar = 4;
	public final int accionInsertarError = 5;
	public final int accionEliminarError = 6;
	public final int accionEliminarErrorMinijuego = 7;
	public final int accionEliminarErrorUsuario = 8;
	
	private int accion;
	private String parametroNombre;
	private List<Integer> parametroContenido;
	private Boolean parametroUsuarios;	
	
	private GestorGrupos(){
		grupoDAO = new GrupoDAO();
		contenidoDAO = new ContenidoDAO();
	}
	
	public static GestorGrupos getGestorGrupos(){
		if(miGestorGrupos == null){
			miGestorGrupos = new GestorGrupos();
		}
		return miGestorGrupos;
	}

	public Object[][] obtenerGrupo(int pos){
		Object[][] valores = null;
		List<Object[]> cont = new ArrayList<>();		
		if(pos > 0){
			grupoActual = grupos.get(pos-1);
			for(int i = 0; i < grupoActual.getContenidoOpciones().size(); i++){
				Contenido contOpc = grupoActual.getContenidoOpciones().get(i);
				Object[] ob = new Object[5];				
				ob[0] = grupoActual.getIdGrupo();
				ob[1] = grupoActual.getNombre();
				ob[2] = grupoActual.getContenidoSolucion().getNombre();
				ob[3] = contOpc.getNombre();
				ob[4] = new Boolean(false);
				cont.add(ob);
			}
			Object[] ob = new Object[5];				
			ob[0] = grupoActual.getIdGrupo();
			ob[1] = grupoActual.getNombre();
			ob[2] = null;
			ob[3] = null;
			ob[4] = new Boolean(false);
			cont.add(ob);
			valores = new Object[cont.size()][];
			for(int i = 0; i < cont.size(); i++) valores[i] = cont.get(i); 
		}else{
			grupoActual = null;
			valores = new Object[1][5];			
			valores[valores.length-1][0] = null;
			valores[valores.length-1][1] = "";
			valores[valores.length-1][2] = null;
			valores[valores.length-1][3] = null;
			valores[valores.length-1][4] = new Boolean(false);			
		}
		return valores;
	}
	
	public String[] obtenerContenido(){
		String[] nombres = new String[contenidos.size()+1];
		nombres[0] = " ";
		for(int i = 0; i < contenidos.size(); i++) nombres[i+1] = contenidos.get(i).getNombre();
		return nombres;
	}
	
	public String[] obtenerNombres(){
		String[] nombres = new String[grupos.size()+1];
		nombres[0] = " ";
		for(int i = 0; i < grupos.size(); i++){
			nombres[i+1] = grupos.get(i).getNombre();
		}
		return nombres;
	}
	
	public Boolean existeNombre(String nombre){
		try{
			return grupoDAO.existeGrupo(nombre);
		}catch(Exception e){
			setChanged();
			notifyObservers(accionCargarError);
			return null;
		}
	}
	
	private void insertarGrupo(String nombreGrupo, List<Integer> contenidoTotal, Boolean usuarios){
		Contenido opcion = contenidos.get(contenidoTotal.get(1));
		List<Contenido> opciones = null;
		boolean crear = false;
		if(grupoActual == null){
			Contenido solucion = contenidos.get(contenidoTotal.get(0));
			opciones = new ArrayList<>();
			opciones.add(opcion);
			crear = true;
			grupoActual = new Grupo(nombreGrupo, solucion, opciones);			
		}else{
			opciones = grupoActual.getContenidoOpciones();
			opciones.add(opcion);
			crear = false;
			grupoActual.setNombre(nombreGrupo);
			grupoActual.setContenidoOpciones(opciones);
		}
		if(!crear ? grupoDAO.insertGrupo(grupoActual, usuarios) : grupoDAO.insertGrupoNuevo(grupoActual, usuarios)){
			try{
				grupos = grupoDAO.getGrupos();
				contenidos = contenidoDAO.getContenidos();
				setChanged();
				notifyObservers(accionInsertar);		
			}catch(Exception e){
				setChanged();
				notifyObservers(accionCargar);
			}	
		}else{
	    	setChanged();
			notifyObservers(accionInsertarError);
		}
	}
	
	private void eliminarGrupo(List<Integer> listaIndices){
		try{
			int borrable = grupoDAO.esBorrable(grupoActual.getIdGrupo());
			if(borrable == 0){
				Integer[][] lista= new Integer[listaIndices.size()][];
				List<Integer[]> t = new ArrayList<>();
					for(int i = 0; i < grupoActual.getContenidoOpciones().size(); i++){
						Integer[] ob = new Integer[3];				
						ob[0] = grupoActual.getIdGrupo();
						ob[1] = grupoActual.getContenidoSolucion().getIdContenido();
						ob[2] = grupoActual.getContenidoOpciones().get(i).getIdContenido();
						t.add(ob);
					}
				for(int i = 0; i < listaIndices.size(); i++){
					lista[i] = t.get(listaIndices.get(i));
				}
				
				if(grupoDAO.deleteGrupo(lista)){
					grupos = grupoDAO.getGrupos();
					contenidos = contenidoDAO.getContenidos();
					setChanged();
					notifyObservers(accionEliminar);
				}else{			
					setChanged();
					notifyObservers(accionEliminarError);
				}
			}else{
				setChanged();
				notifyObservers(borrable == 1 ? accionEliminarErrorMinijuego : accionEliminarErrorUsuario);			
			}
		}catch(Exception e){
			setChanged();
			notifyObservers(accionCargar);
		}
	}
	
	public void setParametrosYAccion(int accion, String paramNombre, List<Integer> paramSolucion, Boolean todosUsuario){
		this.accion = accion;
		this.parametroNombre = paramNombre;
		this.parametroContenido = paramSolucion;
		this.parametroUsuarios = todosUsuario;
	}

	@Override
	public void run() {
		if(accion == accionCargar){
			try{
				grupos = grupoDAO.getGrupos();
				contenidos = contenidoDAO.getContenidos();
				setChanged();
				notifyObservers(accionCargar);
			}catch(Exception e){
				setChanged();
				notifyObservers(accionCargar);
			}			
		}else if(accion == accionInsertar){					
			insertarGrupo(parametroNombre, parametroContenido, parametroUsuarios);		
		}else if(accion == accionEliminar){
			eliminarGrupo(parametroContenido);	
		}
	}
}
