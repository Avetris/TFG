package bd.modelo;

import java.io.File;
import java.util.List;

import javax.swing.ImageIcon;

import modelo.GestorServidor;

public class Mapa{	

	private Integer idMapa;
	private String nombre;
	private String url;
	private ImageIcon imagen;
	private List<Boton> botones;
		
	public Mapa(Integer idMapa, String nombre, String url, List<Boton> botones) {
		this.setIdMapa(idMapa);
		this.setNombre(nombre);
		this.setUrl(url);	
		this.setBotones(botones);		
	}
	
	public Mapa(Integer idMapa, String nombre, String url) {
		this.setIdMapa(idMapa);
		this.setNombre(nombre);
		this.setUrl(url);	
	}
	
	public Mapa(String nombre, File imagen, List<Boton> botones){
		this.setNombre(nombre);
		this.setUrl(GestorServidor.getGestorImagenes().uploadFile("jpg", nombre, imagen));
		this.setBotones(botones);
	}

	public Integer getIdMapa() {return idMapa;}
	public void setIdMapa(Integer idMapa) {this.idMapa = idMapa;}
	
	public String getUrl() {return url;}
	public void setUrl(String url) {this.url = url;}

	public ImageIcon getImagen() {return imagen;}
	public void setImagen(ImageIcon imagen) {this.imagen = imagen;}

	public String getNombre() {return nombre;}
	public void setNombre(String nombre) {this.nombre = nombre;}

	public List<Boton> getBotones() {return botones;}
	public void setBotones(List<Boton> botones) {this.botones = botones;}
	
}
