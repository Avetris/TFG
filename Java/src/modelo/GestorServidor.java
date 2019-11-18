
package modelo;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class GestorServidor {

	private static GestorServidor miGestorImagenes = new GestorServidor();
	
	private GestorServidor(){}
	
	public static GestorServidor getGestorImagenes(){
		if(miGestorImagenes == null){
			miGestorImagenes = new GestorServidor();
		}
		return miGestorImagenes;
	}
	
	public String uploadFile(String tipo, String nombre, File archivo){
		nombre = nombre.replace(".", "").replace(" ", "_");
		nombre = Normalizer.normalize(nombre, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
		String[] usu = GestorSesion.obtSesion().obtUsuarioYContrasena();
		String urlImagen = null;
		HttpURLConnection urlConnection = null;
		if(usu != null){
			try {
				URL url = new URL("http://galan.ehu.eus/avelez012/WEB/TFG/uploadImage.php");
				
				StringBuffer param = new StringBuffer("usuario=").append(URLEncoder.encode(usu[0], "UTF-8"));
				param.append("&contrasena=").append(URLEncoder.encode(usu[1], "UTF-8"));
				param.append("&tipo=").append(URLEncoder.encode(tipo, "UTF-8"));			
				param.append("&nombre=").append(URLEncoder.encode(nombre, "UTF-8"));	
				param.append("&imagen=").append(URLEncoder.encode(Base64.encode(readFile(archivo)), "UTF-8"));

				InputStream inputStream;
	            urlConnection = (HttpURLConnection) url.openConnection();
	            urlConnection.setRequestMethod("POST");
	            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	            urlConnection.setRequestProperty("Accept", "application/json");
	            urlConnection.setRequestProperty("Accept-Language", Locale.getDefault().getLanguage() + "-" + Locale.getDefault().getCountry());
	            urlConnection.setConnectTimeout(5000);
	            urlConnection.setReadTimeout(5000);
	            urlConnection.setDoOutput(true);

	            OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
	            wr.write(param.toString());
	            wr.close();

	            if (urlConnection.getResponseCode() == 200) {
	                inputStream = new BufferedInputStream(urlConnection.getInputStream());
	                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
	                String line;
	                String result = "";
	                while((line = bufferedReader.readLine()) != null){
	                    result += line;
	                }
	                // Cerra stream:
	                inputStream.close();
	                String response = result;
	                if(response != null && response.charAt(0) != '{'){
		                return response;	                	
	                }else{
	                	return null;
	                }
	            } else{
	                return urlImagen;
	            }
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();				 
	        } finally {
	            if(urlConnection != null)
	                urlConnection.disconnect();
	        }
		}		
		return urlImagen;		
	}
	
	public ImageIcon downloadImage(String direccion){
		ImageIcon imagen = null;
		URL url;
		try {
			url = new URL(direccion);			
			BufferedImage img = ImageIO.read(url);
			imagen = new ImageIcon(img);			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return imagen;
	}
	
	private static byte[] readFile(File file) {
		ByteArrayOutputStream bos = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			bos = new ByteArrayOutputStream();
			for (int len; (len = fis.read(buffer)) != -1;) {
				bos.write(buffer, 0, len);
			}
			fis.close();
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (IOException e2) {
			System.err.println(e2.getMessage());
		}
		return bos != null ? bos.toByteArray() : null;
	}	
	
	public boolean deleteFile(String urlImagen){
		String[] usu = GestorSesion.obtSesion().obtUsuarioYContrasena();
		if(urlImagen != null){
			urlImagen = urlImagen.substring(urlImagen.lastIndexOf('/')+1);
			HttpURLConnection urlConnection = null;
			if(usu != null){
				try {
					URL url = new URL("http://galan.ehu.eus/avelez012/WEB/TFG/deleteImage.php");
					
					StringBuffer param = new StringBuffer("usuario=").append(URLEncoder.encode(usu[0], "UTF-8"));
					param.append("&contrasena=").append(URLEncoder.encode(usu[1], "UTF-8"));	
					param.append("&url=").append(URLEncoder.encode(urlImagen, "UTF-8"));				
					
					InputStream inputStream;
		            urlConnection = (HttpURLConnection) url.openConnection();
		            urlConnection.setRequestMethod("POST");
		            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		            urlConnection.setRequestProperty("Accept", "application/json");
		            urlConnection.setRequestProperty("Accept-Language", Locale.getDefault().getLanguage() + "-" + Locale.getDefault().getCountry());
		            urlConnection.setConnectTimeout(5000);
		            urlConnection.setReadTimeout(5000);
		            urlConnection.setDoOutput(true);

		            OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
		            wr.write(param.toString());
		            wr.close();
		            
		            if (urlConnection.getResponseCode() == 200) {
		                inputStream = new BufferedInputStream(urlConnection.getInputStream());
		                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		                String line;
		                String result = "";
		                while((line = bufferedReader.readLine()) != null){
		                    result += line;
		                }
		                // Cerra stream:
		                inputStream.close();
		                String response = result;
		                if(response != null && response.charAt(0) != '{'){
			                return true;	                	
		                }else{
		                	return false;
		                }
		            }else{
		            	return false;
		            }
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
		    		return false;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();	
		    		return false;			 
		        } finally {
		            if(urlConnection != null)
		                urlConnection.disconnect();
		        }
			}else{
				return false;
			}	                
		}else{
    		return false;
		}
	}
}
