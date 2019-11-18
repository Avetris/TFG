package bd.modelo;

import java.util.List;

public class Minijuego {
	
	private Integer idMinijuego;
	private String nombre;
	private String descripcion;
	private Integer tamano;
	private List<Grupo> grupos;
	private List<NinoPermisos> ninos;
	private Integer maximo;
	private Integer minimo;
	
	public Minijuego(Integer idMinijuego, String nombre, String descripcion, Integer tamano, Integer maximo, Integer minimo) {	
		this.setIdMinijuego(idMinijuego);
		this.setNombre(nombre);
		this.setDescripcion(descripcion);
		this.setTamano(tamano);
		this.setMaximo(maximo);
		this.setMinimo(minimo);
	}
	
	public Integer getIdMinijuego() {return idMinijuego;}
	public void setIdMinijuego(Integer idMinijuego) {this.idMinijuego = idMinijuego;}
	
	public String getNombre() {return nombre;}
	public void setNombre(String nombre) {this.nombre = nombre;}
	
	public String getDescripcion() {return descripcion;}
	public void setDescripcion(String descripcion) {this.descripcion = descripcion;}

	public Integer getTamano() {return tamano;}
	public void setTamano(Integer tamano) {this.tamano = tamano;}
	
	public List<Grupo> getGrupos() {return grupos;}
	public void setGrupos(List<Grupo> grupos) {this.grupos = grupos;}

	public List<NinoPermisos> getNinos() {return ninos;}
	public void setNinos(List<NinoPermisos> ninos) {this.ninos = ninos;}

	public Integer getMaximo() {return maximo;}
	public void setMaximo(Integer maximo) {this.maximo = maximo;}

	public Integer getMinimo() {return minimo;}
	public void setMinimo(Integer minimo) {this.minimo = minimo;}
}
