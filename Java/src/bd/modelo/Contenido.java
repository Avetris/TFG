package bd.modelo;

import java.io.File;

import javax.swing.ImageIcon;
import modelo.GestorServidor;

public class Contenido {
	
	private Integer idContenido;
	private String nombre;
	private String castellano;
	private String euskera;
	private String url;
	private ImageIcon imagen;
	
	public Contenido(Integer idContenido, String nombre, String castellano, String euskera, String url) {		
		this.setIdContenido(idContenido);
		this.setNombre(nombre);	
		this.setCastellano(castellano);
		this.setEuskera(euskera);
		this.setUrl(url);
		if(url != null && url.length() > 0){
			this.setImagen(GestorServidor.getGestorImagenes().downloadImage(url));
		}
	}
	
	public Contenido(Integer idContenido, String nombre, String castellano, String euskera, File imagen) {		
		this.setIdContenido(idContenido);
		this.setNombre(nombre);	
		this.setCastellano(castellano);
		this.setEuskera(euskera);
		if(imagen != null){
			this.setUrl(GestorServidor.getGestorImagenes().uploadFile(imagen.getAbsolutePath().endsWith("png") ? "png" : "jpg", nombre, imagen));	
		}
	}
	
	public Contenido(String nombre, String castellano, String euskera, File imagen) {		
		this.setNombre(nombre);	
		this.setCastellano(castellano);
		this.setEuskera(euskera);
		if(imagen != null){
			this.setUrl(GestorServidor.getGestorImagenes().uploadFile(imagen.getAbsolutePath().endsWith("png") ? "png" : "jpg", nombre, imagen));			
		}
	}
		
	public Integer getIdContenido() {return idContenido;}
	public void setIdContenido(Integer idContenido) {this.idContenido = idContenido;}
	
	
	public ImageIcon getImagen() {return imagen;}
	public void setImagen(ImageIcon imagen) {this.imagen = imagen;}
	
	public String getNombre() {return nombre;}
	public void setNombre(String nombre) {this.nombre = nombre;}
	
	public String getUrl() {return url;}
	public void setUrl(String url) {this.url = url;}

	public String getCastellano() {return castellano;}
	public void setCastellano(String castellano) {this.castellano = castellano;}

	public String getEuskera() {return euskera;}
	public void setEuskera(String euskera) {this.euskera = euskera;}
}
