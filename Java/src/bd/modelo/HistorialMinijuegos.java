package bd.modelo;

import java.util.Date;

public class HistorialMinijuegos {
	
	private String nombre;
	private Date fecha;
	private String completo;
	private Integer vidas;
	private Integer errores;
	private Integer tiempo;
	
	public HistorialMinijuegos(String nombre, Date fecha, String completo, Integer vidas, Integer errores, Integer tiempo) {	
		this.setNombre(nombre);
		this.setFecha(fecha);
		this.setCompleto(completo);
		this.setVidas(vidas);
		this.setErrores(errores);
		this.setTiempo(tiempo);
	}
	
	public String getNombre() {return nombre;}
	public void setNombre(String nombre) {this.nombre = nombre;}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getCompleto() {
		return completo;
	}

	public void setCompleto(String completo) {
		this.completo = completo;
	}

	public Integer getVidas() {
		return vidas;
	}

	public void setVidas(Integer vidas) {
		this.vidas = vidas;
	}

	public Integer getErrores() {
		return errores;
	}

	public void setErrores(Integer errores) {
		this.errores = errores;
	}

	public Integer getTiempo() {
		return tiempo;
	}

	public void setTiempo(Integer tiempo) {
		this.tiempo = tiempo;
	}
}
