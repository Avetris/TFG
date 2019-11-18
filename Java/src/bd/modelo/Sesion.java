package bd.modelo;

import java.util.Date;

public class Sesion{

	private Integer idNino;
	private Integer idSesion;
	private String ver;
	private String comentario;
	private Date fecha;

	public Sesion(Integer idNino, Integer idSesion, String ver, String comentario, Date fecha) {
		this.setIdNino(idNino);
		this.setIdSesion(idSesion);
		this.setVer(ver);
		this.setComentario(comentario);
		this.setFecha(fecha);
	}
	
	public Sesion(Integer idNino, String ver, String comentario, Date fecha) {
		this.setIdNino(idNino);
		this.setComentario(comentario);
		this.setVer(ver);
		this.setFecha(fecha);
	}

	public Integer getIdNino() {return idNino;}
	public void setIdNino(Integer idNino) {this.idNino = idNino;}	
	
	public Integer getIdSesion() {return idSesion;}
	public void setIdSesion(Integer idSesion) {this.idSesion = idSesion;}

	public String getVer() {return ver;}
	public void setVer(String ver) {this.ver = ver;}
	
	public String getComentario() {return comentario;}
	public void setComentario(String comentario) {this.comentario = comentario;}

	public Date getFecha() {return fecha;}
	public void setFecha(Date fecha) {this.fecha = fecha;}
}
