package vista;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * {@link http://stackoverflow.com/questions/19125707/simplest-way-to-set-image-as-jpanel-background}
 * @author Aitor
 *
 */

public class ImagePanel extends JPanel{
	
	private static final long serialVersionUID = 1L;
	private File archivo = null;
	private ImageIcon image = null;

	public ImagePanel(Object imagen)
	{
		if(imagen != null){
			if(imagen.getClass() == ImageIcon.class){
				this.image = (ImageIcon) imagen;
			}else{
				this.archivo = (File) imagen;
				try {
					this.image = new ImageIcon(ImageIO.read(this.archivo));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
			}				
		}else{
			this.image = null;	
			this.archivo = null;		
		}
	}
	
	public void paintComponent(Graphics g)
	{
	    super.paintComponent(g);
	    if (image != null)
	    {
	        g.drawImage(image.getImage(),0, 0, getWidth(),getHeight(),this);
	    }
	}
	
	public void setImage(ImageIcon imagen){
		if(imagen != null){
			this.image = imagen;			
		}else{
			this.archivo = null;
			this.image = null;			
		}
	}
	
	public void setImageFile(File imagen){
		this.archivo = imagen;
		if(imagen != null){
			BufferedImage buf;
			try {
				buf = ImageIO.read(imagen);
				this.image = new ImageIcon(buf);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			this.image = null;
		}
	}
	
	public File getImageFile(){
		return this.archivo;
	}
	
	public ImageIcon getImage(){
		return this.image;
	}	
}