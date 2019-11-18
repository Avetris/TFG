package modelo;

import java.io.File;
import java.util.List;
import java.util.Observable;

import javax.swing.ImageIcon;

import bd.ibatis.daos.PremioDAO;
import bd.modelo.Premio;

public class GestorPremios extends Observable implements Runnable{
	
	private static GestorPremios miGestorPremios = new GestorPremios();
	private PremioDAO premioDAO;
	private List<Premio> premios = null;

	public final int accionCargar = 1;
	public final int accionCargarError = 2;
	public final int accionInsertar = 3;
	public final int accionInsertarError = 4;
	
	private int accion;
	private Object[] parametros;
	
	private GestorPremios(){
		premioDAO = new PremioDAO();
		
	}
	
	public static GestorPremios getGestorPremios(){
		if(miGestorPremios == null){
			miGestorPremios = new GestorPremios();
		}
		return miGestorPremios;		
	}

	public String[] obtenerNombrePremios(){
		String[] nombres = null;
		nombres = new String[premios.size()+1];
		nombres[0] = " ";
		for(int i = 0; i < premios.size(); i++){
			nombres[i+1] = premios.get(i).getNombre();
		}
		return nombres;
	}
	
	public Boolean existeNombre(String nombre){
		try{
			return premioDAO.existePremio(nombre);
		}catch(Exception e){
			setChanged();
			notifyObservers(accionCargarError);
			return null;
		}
	}
	
	private void insertarPremio(String nombrePremio, File imagen){
		Premio f = new Premio(nombrePremio, imagen);
		if(premioDAO.insertPremio(f)){
			obtenerDatos();
			setChanged();
			notifyObservers(accionInsertar);
		}else{
			setChanged();
			notifyObservers(accionInsertarError);
		}	
	}
	
	public ImageIcon getImagen(int pos){
		if(pos >= 0 && pos <= premios.size()){
			return premios.get(pos).getImagen();			
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
			premios = premioDAO.getPremios();	
			for(int i = 0; i < premios.size(); i++){
				premios.get(i).setImagen(GestorServidor.getGestorImagenes().downloadImage(premios.get(i).getUrl()));
			}
			if(premios.size() == 0){
				Premio premioInicial = new Premio("Caja Sorpresa", new File(ClassLoader.getSystemResource("/imagenes/caja_sorpresa.png").getFile()));
				premioDAO.insertPremio(premioInicial);
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
			String nombrePremio = (parametros[0] == null) ? null : String.valueOf(parametros[0]);
			File imagen = (parametros[1] == null) ? null : (File) parametros[1];
			insertarPremio(nombrePremio, imagen);
		}	
	}
}
