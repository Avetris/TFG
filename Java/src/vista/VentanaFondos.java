package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;

import modelo.FiltroTexto;
import modelo.GestorFondos;
import modelo.IdiomaProperties;
import modelo.GestorSesion;


public class VentanaFondos extends JFrame implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private IdiomaProperties idioma;
	private GestorFondos gestorFondos;
		
	private JPanel contentPane;
	private JPanel norte, centro, sur;
	
	private ImagePanel panelFondo;
		
	JComboBox<String> cbFondos;
	
	JProgressDialog dialog;
	
	JFileChooser fileChooser;
		
	private Dimension dimVentana = new Dimension(1000, 1000);
	private Dimension dimAreaTexto = new Dimension(150, 25);

	/**
	 * Create the frame.
	 */
	public VentanaFondos() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		idioma = IdiomaProperties.getIdiomaProperties();
		gestorFondos = GestorFondos.getGestorFondos();
		
		int log = GestorSesion.obtSesion().esLogopeda();
		if(log == 0){
			gestorFondos.addObserver(this);			

			dialog = new JProgressDialog(this, idioma.leerProperty("fondos.titulo_cargando"), idioma.leerProperty("fondos.mensaje_cargando"));
			gestorFondos.setParametrosYAccion(gestorFondos.accionCargar, null);
			new Thread(gestorFondos).start();	
			setEnabled(false);
		}else{
			new VentanaLogin();
			dispose();
		}	
	}

	private void getTituloSesion() {
	
		JLabel lblTitulo = new JLabel(idioma.leerProperty("fondos.ventana"));
		lblTitulo.setHorizontalAlignment(0);
		lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
		lblTitulo.setOpaque(true);
		lblTitulo.setForeground(Color.black);
		
		Border paddingBorder = BorderFactory.createEmptyBorder(10,10,10,10);
		Border border = BorderFactory.createBevelBorder(0);
		lblTitulo.setBorder(BorderFactory.createCompoundBorder(border,paddingBorder));

		norte.add(lblTitulo);
	}
	
	private void getSeleccionFondos(){
		centro.setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		JLabel lblFondo = new JLabel(idioma.leerProperty("fondo"));
		cbFondos = new JComboBox<>(gestorFondos.obtenerNombreFondos());
		cbFondos.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				
                if(arg0.getStateChange() == ItemEvent.SELECTED){
                	if(cbFondos.getSelectedIndex() != 0){
    					ImageIcon img = gestorFondos.getImagen(cbFondos.getSelectedIndex()-1);
    					if(img != null){
    						panelFondo.setImage(img);
    						
    					}else{
    						panelFondo.setImage(null);
    					}					
    				}else{
						panelFondo.setImage(null);
    				}
    				centro.updateUI();	
    				sur.updateUI();
                }				
			}
		});
		
		panel.add(lblFondo);
		panel.add(cbFondos);		
		panelFondo = new ImagePanel(null);
		
		centro.add(panel, BorderLayout.NORTH);		
		centro.add(panelFondo, BorderLayout.CENTER);		
	}
	
	private void getBtnAnadirFondo(){
		sur.removeAll();
		sur.setLayout(new GridBagLayout());
		JLabel lblNombre = new JLabel(idioma.leerProperty("fondos.nombre")+"*");
		lblNombre.setFont(new Font("Arial", Font.BOLD, 15));
		final JTextField txtNombre = new JTextField();
		txtNombre.setFont(new Font("Serif", Font.ITALIC, 12));
		txtNombre.setPreferredSize(dimAreaTexto);
		txtNombre.addKeyListener(FiltroTexto.textInputLimiter(txtNombre, 20));
		JButton btnAnadirFondo = new JButton(idioma.leerProperty("fondos.anadir"));
		btnAnadirFondo.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String nombre = txtNombre.getText();
				if(nombre != null && nombre.trim().length() > 0){
					Boolean b = gestorFondos.existeNombre(nombre);
					if(b == null){return;
					}else if(b){
						JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("fondos.error_existe"), idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);	
					}else{
						if (fileChooser == null) {
							fileChooser = new JFileChooser();
							
							fileChooser.addChoosableFileFilter(new FileFilter() {							
								@Override
								public String getDescription() {return null;}
								
								@Override
								public boolean accept(File f) {
									 if (f.isDirectory()) return true;
									 String name = f.getName().toLowerCase();								 								 
									 if (name != null && name.length() > 2) {
										 if(name.endsWith("jpg")){
											 BufferedImage bimg = null;
											try {
												bimg = ImageIO.read(f);
												if(bimg.getWidth() >= 640 && bimg.getHeight() >= 360){
													return true;
												}else{
													return false;
												}
											} catch (IOException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
												return false;
											}										 
										 }else{
											 return false;
										 }							
							            
							        }else{
							        	return false;
							        }
								}
							});
							fileChooser.setAcceptAllFileFilterUsed(false);
							fileChooser.setAccessory(new ImagePreview(fileChooser));
				        }					
				        int returnVal = fileChooser.showDialog(VentanaFondos.this,idioma.leerProperty("fondos.seleccionar"));
				        if (returnVal == JFileChooser.APPROVE_OPTION) {    
							gestorFondos.setParametrosYAccion(gestorFondos.accionInsertar, new Object[]{nombre, fileChooser.getSelectedFile()});
							dialog = new JProgressDialog(VentanaFondos.this, idioma.leerProperty("fondos.titulo_creando"), idioma.leerProperty("fondos.mensaje_creando"));
							new Thread(gestorFondos).start();
							setEnabled(false);
				        }
				        fileChooser.setSelectedFile(null);
					}
				}else{
					JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("fondos.error_nombre"), idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		JButton btnAtras = new JButton("<");
		btnAtras.setEnabled(true);
		btnAtras.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		
		btnAtras.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new VentanaAdministrarJuegos();
				gestorFondos.deleteObserver(VentanaFondos.this);
				dispose();				
			}
		});

		JPanel flowPanel = new JPanel();
		flowPanel.setLayout(new FlowLayout());
		flowPanel.add(lblNombre);
		flowPanel.add(txtNombre);
		flowPanel.add(btnAnadirFondo);
		GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 2.0; 
        c.gridwidth = 2;    
		c.insets = new Insets(10,0,0,0);  //top padding
        sur.add(flowPanel, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 2.0; 
        c.gridwidth = GridBagConstraints.RELATIVE;  
		c.insets = new Insets(10,10,10,10);  //top padding
        c.gridy = 1; 
        c.fill = GridBagConstraints.EAST;
        c.anchor = GridBagConstraints.WEST;
		sur.add(btnAtras, c);	
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		dialog.dispose();
		setEnabled(true);
		if((int) arg1 == gestorFondos.accionCargar){
			setSize(dimVentana);
			setLocationRelativeTo(null);

			contentPane = new JPanel();
			setContentPane(contentPane);
			norte = new JPanel();
			centro = new JPanel();
			sur = new JPanel();

			setLayout(new BorderLayout());

			getTituloSesion();
			getSeleccionFondos();
			getBtnAnadirFondo();

			add(norte, BorderLayout.NORTH);
			add(centro, BorderLayout.CENTER);
			add(sur, BorderLayout.SOUTH);
			setVisible(true);	
		}else if ((int) arg1 == gestorFondos.accionCargarError) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("error_carga"),
					idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
			gestorFondos.deleteObserver(this);
			new VentanaLogin();
			dispose();
		}else if((int) arg1 == gestorFondos.accionInsertar){
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("fondos.mensaje_correcto"), idioma.leerProperty("fondos.titulo_correcto"), JOptionPane.INFORMATION_MESSAGE);			            	
			cbFondos.removeAllItems();
			centro.removeAll();
			getSeleccionFondos();
			getBtnAnadirFondo();
			centro.updateUI();	
			sur.updateUI();		
		}else if((int) arg1 == gestorFondos.accionInsertarError){
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("fondos.error_crear"), idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);            	
		}
	}
}