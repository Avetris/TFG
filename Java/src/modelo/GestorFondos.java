package modelo;

import java.io.File;
import java.util.List;
import java.util.Observable;

import javax.swing.ImageIcon;

import bd.ibatis.daos.FondoDAO;
import bd.modelo.Fondo;

public class GestorFondos extends Observable implements Runnable{
	
	private static GestorFondos miGestorFondos = new GestorFondos();
	private FondoDAO fondoDAO;
	private List<Fondo> fondos = null;

	public final int accionCargar = 1;
	public final int accionCargarError = 2;
	public final int accionInsertar = 3;
	public final int accionInsertarError = 4;
	
	private int accion;
	private Object[] parametros;
	
	private GestorFondos(){
		fondoDAO = new FondoDAO();
		
	}
	
	public static GestorFondos getGestorFondos(){
		if(miGestorFondos == null){
			miGestorFondos = new GestorFondos();
		}
		return miGestorFondos;		
	}

	public String[] obtenerNombreFondos(){
		String[] nombres = null;
		nombres = new String[fondos.size()+1];
		nombres[0] = " ";
		for(int i = 0; i < fondos.size(); i++){
			nombres[i+1] = fondos.get(i).getNombre();
		}
		return nombres;
	}
	
	public Boolean existeNombre(String nombre){
		try{
			return fondoDAO.existeFondo(nombre);
		}catch(Exception e){
			setChanged();
			notifyObservers(accionCargarError);
			return null;
		}
	}
	
	private void insertarFondo(String nombreFondo, File imagen){
		Fondo f = new Fondo(nombreFondo, imagen);
		if(fondoDAO.insertFondo(f)){
			obtenerDatos();
			setChanged();
			notifyObservers(accionInsertar);
		}else{
			setChanged();
			notifyObservers(accionInsertarError);
		}	
	}	
	
	public ImageIcon getImagen(int pos){
		if(pos >= 0 && pos <= fondos.size()){
			return fondos.get(pos).getImagen();			
		}else{
			return null;
		}
	}
	

	public void setParametrosYAccion(int accion, Object[] params){
		this.accion = accion;
		this.parametros = params;
	}
	
	private void obtenerDatos(){
		try{
			fondos = fondoDAO.getFondos();	
			for(int i = 0; i < fondos.size(); i++){
				fondos.get(i).setImagen(GestorServidor.getGestorImagenes().downloadImage(fondos.get(i).getUrl()));
			}
			if(fondos.size() == 0){
				Fondo fondoInicial = new Fondo("Fondo Inicial", new File(ClassLoader.getSystemResource("/imagenes/fondoInicial.jpg").getFile()));
				fondoDAO.insertFondo(fondoInicial);
				obtenerDatos();
			}
		}catch(Exception e){
			setChanged();
			notifyObservers(accionCargarError);
		}
	}

	@Override
	public void run() {
		if(accion == accionCargar){
			obtenerDatos();
			setChanged();
			notifyObservers(accionCargar);			
		}else if(accion == accionInsertar){		
			String nombreFondo = (parametros[0] == null) ? null : String.valueOf(parametros[0]);
			File imagen = (parametros[1] == null) ? null : (File) parametros[1];
			insertarFondo(nombreFondo, imagen);
		}		
	}
}
