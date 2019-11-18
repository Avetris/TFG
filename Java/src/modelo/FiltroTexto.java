package modelo;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class FiltroTexto {
	
	
	public static boolean soloTexto(String str){
		if(str != null){
			Pattern pattern = Pattern.compile("[A-Za-z áéíóúÁÉÍÓÚñÑ]{1,}");
			Matcher matcher = pattern.matcher(str);
		    return matcher.matches() && str.trim().length()>0;	
		}else{
			return false;
		}			
	}
	
	public static boolean soloNumero(String str){
		
		Pattern pattern = Pattern.compile("[0-9]{1,}");
		Matcher matcher = pattern.matcher(str);
	    return matcher.matches() && str.trim().length()>0;		
	}
	
	public static String getPassword(char[] pass){
		StringBuffer contrasena = new StringBuffer();
		contrasena.append(pass);
		return contrasena.toString();	
	}
	
	public static KeyListener textInputLimiter(final JComponent component, final int limit){
		return new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				if(component instanceof JPasswordField){
					if(((JPasswordField) component).getPassword().length >= limit){
						e.consume();
					}
				}else if(component instanceof JTextField){
					if(((JTextField) component).getText().length() >= limit){
						e.consume();
					}
				}else if(component instanceof JTextArea){
					if(((JTextArea) component).getText().length() >= limit){
						e.consume();
					}
				}					
			}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {}
		};
		 
	};
}