package modelo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;

import javax.swing.ImageIcon;

import bd.ibatis.daos.MapaDAO;
import bd.modelo.Boton;
import bd.modelo.Mapa;

public class GestorMapas extends Observable implements Runnable{
	
	private static GestorMapas miGestorMapas = new GestorMapas();
	private MapaDAO mapaDAO;
	private List<Mapa> mapas = null;	

	public final int accionCargar = 1;
	public final int accionCargarError = 2;
	public final int accionInsertar = 3;
	public final int accionModificar = 4;
	public final int accionEliminar = 5;
	public final int accionInsertarError = 6;
	public final int accionModificarError = 7;
	public final int accionEliminarError = 8;
	
	private int accion;
	private HashMap<String, Object[][]> parametros;
	
	private GestorMapas(){
		mapaDAO = new MapaDAO();
		
	}
	
	public static GestorMapas getGestorMapas(){
		if(miGestorMapas == null){
			miGestorMapas = new GestorMapas();
		}
		return miGestorMapas;		
	}

	public String[] obtenerNombreMapas(){
		String[] nombres = null;		
		nombres = new String[mapas.size()+1];
		nombres[0] = " ";
		for(int i = 0; i < mapas.size(); i++){
			nombres[i+1] = mapas.get(i).getNombre();
		}
		return nombres;
	}
	
	public Boolean existeNombre(String nombre){
		try{
			return mapaDAO.existeMapa(nombre);
		}catch(Exception e){
			setChanged();
			notifyObservers(accionCargarError);
			return null;
		}
	}
	
	private void insertarMapa(String nombreMapa, File imagen, Float[][] medidas){
		List<Boton> botones = new ArrayList<>();
		for(int i = 0; i < medidas.length; i++){
			botones.add(new Boton(medidas[i][0], medidas[i][1], medidas[i][2], medidas[i][3]));
		}
		Mapa mapaNuevo = new Mapa(nombreMapa, imagen, botones);
		
		if(mapaDAO.insertMapa(mapaNuevo)){
			getMapas();
			setChanged();
			notifyObservers(accionInsertar);
		}else{
			setChanged();
			notifyObservers(accionInsertarError);
		}
	}
	
	private void modificarMapa(int posicion, Float[][] medidas){		
		List<Boton> botones = mapas.get(posicion).getBotones();
		for(int i = 0; i < botones.size(); i++){
			if(i < medidas.length){
				botones.get(i).setPosicionX(medidas[i][0]);
				botones.get(i).setPosicionY(medidas[i][1]);
				botones.get(i).setWidth(medidas[i][2]);
				botones.get(i).setHeight(medidas[i][3]);				
			}else{
				botones.get(i).setPosicionX(null);
				botones.get(i).setPosicionY(null);
				botones.get(i).setWidth(null);
				botones.get(i).setHeight(null);	
			}
		}
		if(botones.size() < medidas.length){
			for(int i = botones.size(); i < medidas.length; i++){
				botones.add(new Boton(medidas[i][0], medidas[i][1], medidas[i][2], medidas[i][3]));
			}
		}
		mapas.get(posicion).setBotones(botones);			
		
		if(mapaDAO.updateBotones(mapas.get(posicion))){
			getMapas();
			setChanged();
			notifyObservers(accionModificar);
		}else{
			setChanged();
			notifyObservers(accionModificarError);
		}
	}
	
	public ImageIcon getImagen(int pos){
		if(pos >= 0 && pos <= mapas.size()){
			return mapas.get(pos).getImagen();			
		}else{
			return null;
		}
	}
	
	public List<Float[]> getBotones(int pos){
		if(pos >= 0 && pos <= mapas.size()){
			List<Float[]> medidas = new ArrayList<>();
			for (Boton btn : mapas.get(pos).getBotones()) {
				Float[] f = new Float[4];
				f[0] = btn.getPosicionX();
				f[1] = btn.getPosicionY();
				f[2] = btn.getWidth();
				f[3] = btn.getHeight();
				medidas.add(f);
			}		
			return medidas;
		}else{
			return null;
		}		
	}
	
	public void setParametrosYAccion(int accion, HashMap<String, Object[][]> params){
		this.accion = accion;
		this.parametros = params;
	}
	
	private void getMapas(){
		try{
			mapas = mapaDAO.getMapas();	
			for(int i = 0; i < mapas.size(); i++){
				mapas.get(i).setImagen(GestorServidor.getGestorImagenes().downloadImage(mapas.get(i).getUrl()));
			}
			if(mapas.size() == 0){
				List<Boton> botones = new ArrayList<>();				
				botones.add(new Boton(65.674f, 586.527f, 103.018f, 75.196f));
				botones.add(new Boton(995.413f, 506.632f, 115.895f, 84.595f));
				botones.add(new Boton(159.678f, 266.005f, 115.895f, 84.595f));
				botones.add(new Boton(526.680f, 113.734f, 103.018f, 75.196f));
				botones.add(new Boton(92.716f, 62.037f, 115.895f, 84.595f));
				botones.add(new Boton(507.364f, 391.958f, 128.773f, 93.995f));
				botones.add(new Boton(996.700f, 102.454f, 141.650f, 103.394f));		
				Mapa mapaInicial = new Mapa("Mapa Inicial", new File(ClassLoader.getSystemResource("/imagenes/mapaInicial.jpg").getFile()), botones);
				mapaDAO.insertMapa(mapaInicial);
				getMapas();
			}
		}catch(Exception e){
			setChanged();
			notifyObservers(accionCargarError);
		}
	}

	@Override
	public void run() {
		if(accion == accionCargar){
			getMapas();
			setChanged();
			notifyObservers(accionCargar);			
		}else if(accion == accionInsertar){		
			String nombreMapa = (parametros.get("Nombre")[0][0] == null) ? null : String.valueOf(parametros.get("Nombre")[0][0]);
			File imagen = (parametros.get("Imagen")[0][0] == null) ? null : (File) parametros.get("Imagen")[0][0];
			Float[][] medidas = (Float[][]) parametros.get("Medidas");
			insertarMapa(nombreMapa, imagen, medidas);
		}else if(accion == accionModificar){
			int posicion = (int) parametros.get("Posicion")[0][0];
			Float[][] medidas = (Float[][]) parametros.get("Medidas");
			modificarMapa(posicion, medidas);		
		}
	}
}
