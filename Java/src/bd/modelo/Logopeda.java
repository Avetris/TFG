package bd.modelo;

public class Logopeda extends Usuario{	

	public Logopeda(String usuario, String nombre, String apellidos, String contrasena, String idioma) {
		super(usuario,nombre,apellidos,contrasena,idioma);
	}
	
	public Logopeda(Integer idLogopeda, String usuario, String nombre, String apellidos, String contrasena, String idioma) {
		super(idLogopeda, usuario, nombre, apellidos, contrasena, idioma);
	}
}
