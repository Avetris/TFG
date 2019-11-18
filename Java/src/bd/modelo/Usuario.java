package bd.modelo;

public class Usuario {	

	private Integer idUsuario;
	private String usuario;
	private String nombre;
	private String apellidos;
	private String contrasena;
	private String idioma;
	
	public Usuario(String usuario, String nombre, String apellidos, String contrasena, String idioma) {
		
		this.setUsuario(usuario);
		this.setNombre(nombre);
		this.setApellidos(apellidos);
		this.setContrasena(contrasena);
		this.setIdioma(idioma);
	}
	
	public Usuario(Integer idUsuario, String usuario, String nombre, String apellidos, String contrasena, String idioma) {
		this.setIdUsuario(idUsuario);
		this.setUsuario(usuario);
		this.setNombre(nombre);
		this.setApellidos(apellidos);
		this.setContrasena(contrasena);
		this.setIdioma(idioma);
	}
	
	public Usuario(Integer idUsuario, String nombre, String apellidos) {
		this.setIdUsuario(idUsuario);
		this.setNombre(nombre);
		this.setApellidos(apellidos);
	}

	public Integer getIdUsuario() {return idUsuario;}	
	public void setIdUsuario(Integer idUsuario) {this.idUsuario = idUsuario;}

	public String getUsuario() {return usuario;}
	public void setUsuario(String usuario) {this.usuario = usuario;}

	public String getNombre() {return nombre;}
	public void setNombre(String nombre) {this.nombre = nombre;}
	
	public String getApellidos() {return apellidos;}
	public void setApellidos(String apellidos) {this.apellidos = apellidos;}

	public String getContrasena() {return contrasena;}
	public void setContrasena(String contrasena) {this.contrasena = contrasena;}

	public String getIdioma() {return idioma;}
	public void setIdioma(String idioma) {this.idioma = idioma;}
}
