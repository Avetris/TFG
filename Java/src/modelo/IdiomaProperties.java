package modelo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.prefs.Preferences;

public class IdiomaProperties {

	private static IdiomaProperties miIdiomaProperties = new IdiomaProperties();
	private Properties properties;
	private String idioma;
	
	private IdiomaProperties(){
		properties = new Properties();		
		String[] datos = obtenerUsuario();
		if(datos == null || datos.length != 2){
			idioma = "espanol";			
		}
	}
	
	public static IdiomaProperties getIdiomaProperties(){
		if(miIdiomaProperties == null){
			miIdiomaProperties = new IdiomaProperties();
		}
		return miIdiomaProperties;
	}
	
	public String leerProperty(String propiedad){
		InputStream input = null;
		String texto = "";

		try {			
			input = ClassLoader.getSystemResourceAsStream(idioma+".properties");
			properties.load(input);
			
			texto = properties.getProperty(propiedad);
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			properties.clear();
		}
		return texto;
	}
	
	public void guardarUsuario(String usuario, String idioma, boolean logopeda){
		Preferences prefs = Preferences.userNodeForPackage(IdiomaProperties.class);
		prefs.put("usuario", usuario);
		prefs.put("idioma", idioma);
		prefs.put("esLogopeda", String.valueOf(logopeda));
		this.idioma = idioma;
	}
	
	public String[] obtenerUsuario(){
		String[] texto = null;
		Preferences prefs = Preferences.userNodeForPackage(IdiomaProperties.class);
		texto = new String[]{prefs.get("usuario", null), prefs.get("esLogopeda", "false")};
		idioma = prefs.get("idioma", "espanol");
		if(texto[0] == null) texto = null;
		return texto;
	}
	
	public void setIdioma (String pIdioma){
		idioma = pIdioma;
	}
	
}
