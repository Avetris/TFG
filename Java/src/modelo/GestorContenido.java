package modelo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import bd.ibatis.daos.ContenidoDAO;
import bd.modelo.Contenido;
import vista.ImagePanel;

public class GestorContenido extends Observable implements Runnable{
	
	private static GestorContenido miGestorContenido = new GestorContenido();
	private ContenidoDAO contenidoDAO;
	private List<Contenido> contenidos = null;

	public final int accionCargar = 1;
	public final int accionCargarError = 2;
	public final int accionInsertar = 3;
	public final int accionModificar = 4;
	public final int accionEliminar = 5;
	public final int accionInsertarError = 6;
	public final int accionModificarError = 7;
	public final int accionEliminarError = 8;
	
	private int accion;
	private List<Object[]> parametros;
	
	private GestorContenido(){
		contenidoDAO = new ContenidoDAO();		
	}
	
	public static GestorContenido getGestorContenido(){
		if(miGestorContenido == null){
			miGestorContenido = new GestorContenido();
		}
		return miGestorContenido;		
	}

	public Object[][] obtenerContenido(){	
		Object[][] cont = new Object[contenidos.size()+1][7];
		for(int i = 0; i < contenidos.size(); i++){
			cont[i][0] = contenidos.get(i).getIdContenido();
			cont[i][1] = contenidos.get(i).getNombre();
			cont[i][2] = contenidos.get(i).getCastellano();
			cont[i][3] = contenidos.get(i).getEuskera();
			cont[i][4] = new ImagePanel(contenidos.get(i).getImagen());
			cont[i][5] = new Boolean(false);
			cont[i][6] = new Boolean(false);
		}
		cont[cont.length-1][0] = null;
		cont[cont.length-1][1] = null;
		cont[cont.length-1][2] = null;
		cont[cont.length-1][3] = null;
		cont[cont.length-1][4] = new ImagePanel(null);
		cont[cont.length-1][5] = new Boolean(false);
		cont[cont.length-1][6] = new Boolean(false);
		return cont;
	}
	
	public Boolean existeNombre(String nombre){
		try{
			return contenidoDAO.existeContenido(nombre);
		}catch(Exception e){
			setChanged();
			notifyObservers(accionCargarError);
			return null;
		}
	}
	
	private void insertarContenido(String nombreContenido, String castellano, String euskera, File imagen){
		Contenido cont = new Contenido(nombreContenido, castellano, euskera, imagen);
		if(contenidoDAO.insertContenido(cont)){
			try{
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
	
	private void modificarContenido(List<Object[]> cambios){
		List<Contenido> cambiosContenido = new ArrayList<Contenido>();
		for(int i = 0; i < cambios.size(); i++){
			Object[] ob = cambios.get(i);	
			String nombreContenido = (parametros.get(i)[1] == null) ? null : String.valueOf(parametros.get(i)[1]);
			String castellano = (parametros.get(i)[2] == null) ? null : String.valueOf(parametros.get(i)[2]);
			String euskera = (parametros.get(i)[3] == null) ? null : String.valueOf(parametros.get(i)[3]);
			File imagen = (parametros.get(i)[4] == null) ? null : (File) parametros.get(i)[4];
			cambiosContenido.add(new Contenido((int) ob[0], nombreContenido, castellano, euskera, imagen));
		}
		if(contenidoDAO.updateContenido(cambiosContenido)){
			for(int i = 0; i < cambiosContenido.size(); i++){
			if(cambiosContenido.get(i).getUrl() != null && cambiosContenido.get(i).getUrl().length() > 0){
					Contenido cont = getContenido((int) cambiosContenido.get(i).getIdContenido());
					if(cont != null){
						GestorServidor.getGestorImagenes().deleteFile(cont.getUrl());
					}
				}
			}
			try{
				contenidos = contenidoDAO.getContenidos();		
		    	setChanged();
				notifyObservers(accionModificar);		
			}catch(Exception e){
				setChanged();
				notifyObservers(accionCargar);
			}	
		}else{
	    	setChanged();
			notifyObservers(accionModificar);		
		}
	}
	
	private Contenido getContenido(int idContenido){
		for(Contenido cont : contenidos){
			if(cont.getIdContenido() == idContenido){
				return cont;
			}
		}
		return null;
	}
	
	private void eliminarContenido(List<Integer> lista){
		if(contenidoDAO.deleteContenido(lista)){
			for(int i : lista){
				Contenido cont = getContenido(i);
				if(cont != null){
					GestorServidor.getGestorImagenes().deleteFile(cont.getUrl());
				}
			}
			try{
				contenidos = contenidoDAO.getContenidos();				
				setChanged();
				notifyObservers(accionEliminar);
			}catch(Exception e){
				setChanged();
				notifyObservers(accionCargar);
			}
		}else{			
			setChanged();
			notifyObservers(accionEliminarError);
		}
	}
	
	public void setParametrosYAccion(int accion, List<Object[]> params){
		this.accion = accion;
		this.parametros = params;
	}

	@Override
	public void run() {
		if(accion == accionCargar){
			try{
				contenidos = contenidoDAO.getContenidos();
				setChanged();
				notifyObservers(accionCargar);			
			}catch(Exception e){
				setChanged();
				notifyObservers(accionCargar);
			}
		}else if(accion == accionInsertar){		
			String nombreContenido = (parametros.get(0)[0] == null) ? null : String.valueOf(parametros.get(0)[0]);
			String castellano = (parametros.get(0)[1] == null) ? null : String.valueOf(parametros.get(0)[1]);
			String euskera = (parametros.get(0)[2] == null) ? null : String.valueOf(parametros.get(0)[2]);
			File imagen = (parametros.get(0)[3] == null) ? null : (File) parametros.get(0)[3];
			insertarContenido(nombreContenido, castellano, euskera, imagen);
		}else if(accion == accionModificar){
			modificarContenido(parametros);			
		}else if(accion == accionEliminar){
			List<Integer> lista = new ArrayList<Integer>();
			for(Object[] ob : parametros)
				lista.add((int) ob[0]);
			eliminarContenido(lista);			
		}
	}
}
