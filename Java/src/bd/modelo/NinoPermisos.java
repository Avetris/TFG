package bd.modelo;

public class NinoPermisos extends Nino {

	private Integer maximo;
	private Integer minimo;
	private Integer puntuacionMaxima;
	
	public NinoPermisos(Integer idNino, String nombre, String apellidos, Integer maximo, Integer minimo, Integer puntuacionMaxima) {
		super(idNino, nombre, apellidos);
		this.setMaximo(maximo);
		this.setMinimo(minimo);
		this.setPuntuacionMaxima(puntuacionMaxima);
	}
	
	public Integer getMaximo() {return maximo;}
	public void setMaximo(Integer maximo) {this.maximo = maximo;}

	public Integer getMinimo() {return minimo;}
	public void setMinimo(Integer minimo) {this.minimo = minimo;}

	public Integer getPuntuacionMaxima() {return puntuacionMaxima;}
	public void setPuntuacionMaxima(Integer puntuacionMaxima) {this.puntuacionMaxima = puntuacionMaxima;}
	
}
