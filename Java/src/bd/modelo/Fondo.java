package bd.modelo;

import java.io.File;
import javax.swing.ImageIcon;

import modelo.GestorServidor;

public class Fondo{	

	private Integer idFondo;
	private String nombre;
	private String url;
	private ImageIcon imagen;
		
	public Fondo(Integer idFondo, String nombre, String url) {
		this.setIdFondo(idFondo);
		this.setNombre(nombre);
		this.setUrl(url);		
	}
	
	public Fondo(String nombre, File imagen){
		this.setNombre(nombre);
		this.setUrl(GestorServidor.getGestorImagenes().uploadFile("jpg", nombre, imagen));
		
	}

	public Integer getIdFondo() {return idFondo;}
	public void setIdFondo(Integer idFondo) {this.idFondo = idFondo;}
	
	public String getUrl() {return url;}
	public void setUrl(String url) {this.url = url;}

	public ImageIcon getImagen() {return imagen;}
	public void setImagen(ImageIcon imagen) {this.imagen = imagen;}

	public String getNombre() {return nombre;}
	public void setNombre(String nombre) {this.nombre = nombre;}
	
}
