package vista;

import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

public class JProgressDialog extends JDialog{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5964907389746403627L;

	public JProgressDialog(JFrame frame, String titulo, String mensaje){
		super(frame, titulo);
		
		JProgressBar progressBar = new JProgressBar(0, 500);
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 1));
		panel.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));	    
	    panel.add(new JLabel(mensaje));
	    panel.add(progressBar);
	    setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	    progressBar.setIndeterminate(true); 
	    setSize(300, 100);
	    setLocationRelativeTo(frame);
		
	    add(panel);
		
		setVisible(true);
	}
	
}
