package bd.modelo;

import java.util.List;

public class Grupo {
	
	private Integer idGrupo;
	private String nombre;
	private Contenido contenidoSolucion;
	private List<Contenido>  contenidoOpciones;
	private List<Nino> ninos;
	
	
	public Grupo(Integer idGrupo, String nombre) {	
		this.setIdGrupo(idGrupo);
		this.setNombre(nombre);
	}	
	
	public Grupo(String nombre, Contenido contenidoSolucion, List<Contenido> contenidoOpciones) {	
		this.setNombre(nombre);
		this.setContenidoSolucion(contenidoSolucion);
		this.setContenidoOpciones(contenidoOpciones);
	}
	
	public Integer getIdGrupo() {return idGrupo;}
	public void setIdGrupo(Integer idGrupo) {this.idGrupo = idGrupo;}
	
	public String getNombre() {return nombre;}
	public void setNombre(String nombre) {this.nombre = nombre;}
	
	public Contenido getContenidoSolucion() {return contenidoSolucion;}
	public void setContenidoSolucion(Contenido contenidoSolucion) {this.contenidoSolucion = contenidoSolucion;}

	public List<Nino> getNinos() {return ninos;}
	public void setNinos(List<Nino> ninos) {this.ninos = ninos;}

	public List<Contenido> getContenidoOpciones() {return contenidoOpciones;}
	public void setContenidoOpciones(List<Contenido> contenidoOpciones) {this.contenidoOpciones = contenidoOpciones;}
}
