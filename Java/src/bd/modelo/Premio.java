package bd.modelo;

import java.io.File;
import javax.swing.ImageIcon;

import modelo.GestorServidor;

public class Premio{	

	private Integer idPremio;
	private String nombre;
	private String url;
	private ImageIcon imagen;
		
	public Premio(Integer idPremio, String nombre, String url) {
		this.setIdPremio(idPremio);
		this.setNombre(nombre);
		this.setUrl(url);		
	}
	
	public Premio(String nombre, File imagen){
		this.setNombre(nombre);
		this.setUrl(GestorServidor.getGestorImagenes().uploadFile(imagen.getAbsolutePath().endsWith("png") ? "png" : "jpg", nombre, imagen));		
	}

	public Integer getIdPremio() {return idPremio;}
	public void setIdPremio(Integer idPremio) {this.idPremio = idPremio;}
	
	public String getUrl() {return url;}
	public void setUrl(String url) {this.url = url;}

	public ImageIcon getImagen() {return imagen;}
	public void setImagen(ImageIcon imagen) {this.imagen = imagen;}

	public String getNombre() {return nombre;}
	public void setNombre(String nombre) {this.nombre = nombre;}
	
}
