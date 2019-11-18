package modelo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA1 {

	/***
	 * Convierte un arreglo de bytes a String usando valores hexadecimales
	 * @param digest arreglo de bytes a convertir
	 * @return String creado a partir de <code>digest</code>
	 */
	
	private static String aHexadecimal(byte[] digest){
		String hash = "";
		for(byte aux:digest){
			int b = aux & 0xff;
			if(Integer.toHexString(b).length() == 1) hash += "0";
			hash += Integer.toHexString(b);
		}
		return hash;
	}
	
	public static String getStringMensageDigest(String pCont){
		byte[] digest = null;
		byte[] buffer = pCont.getBytes();
		try{
			MessageDigest mensajeDigest = MessageDigest.getInstance("SHA-1");
			mensajeDigest.reset();
			mensajeDigest.update(buffer);
			digest = mensajeDigest.digest();
		}catch(NoSuchAlgorithmException ex){
			System.out.println("Error creando Digest");
		}
		return aHexadecimal(digest);
	}	
}
