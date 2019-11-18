package bd.modelo;

public class Boton{	

	private Integer idMapa;
	private Integer idBoton;
	private Float posicionX;
	private Float posicionY;
	private Float width;
	private Float height;
		
	public Boton(Integer idMapa, Integer idBoton, Float posicionX, Float posicionY, Float width, Float height) {
		this.setIdBoton(idBoton);
		this.setPosicionX(posicionX);
		this.setPosicionY(posicionY);
		this.setWidth(width);
		this.setHeight(height);
	}
	
	public Boton(Float posicionX, Float posicionY, Float width, Float height){
		this.setPosicionX(posicionX);
		this.setPosicionY(posicionY);
		this.setWidth(width);
		this.setHeight(height);
	}

	public Integer getIdMapa() {return idMapa;}
	public void setIdMapa(Integer idMapa) {this.idMapa = idMapa;}
	
	public Integer getIdBoton() {return idBoton;}
	public void setIdBoton(Integer idBoton) {this.idBoton = idBoton;}

	public Float getPosicionX() {return posicionX;}
	public void setPosicionX(Float posicionX) {this.posicionX = posicionX;}

	public Float getPosicionY() {return posicionY;}
	public void setPosicionY(Float posicionY) {this.posicionY = posicionY;}

	public Float getWidth() {return width;}
	public void setWidth(Float width) {this.width = width;}

	public Float getHeight() {return height;}
	public void setHeight(Float height) {this.height = height;}	
}
