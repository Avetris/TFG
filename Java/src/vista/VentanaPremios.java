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
import java.io.File;
import java.util.Observable;
import java.util.Observer;

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
import modelo.GestorPremios;
import modelo.IdiomaProperties;
import modelo.GestorSesion;


public class VentanaPremios extends JFrame implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private IdiomaProperties idioma;
	private GestorPremios gestorPremios;
		
	private JPanel contentPane;
	private JPanel norte, centro, sur;
	
	private ImagePanel panelPremio;
		
	JComboBox<String> cbPremios;
	
	JProgressDialog dialog;
	
	JFileChooser fileChooser;
		
	private Dimension dimVentana = new Dimension(500, 500);
	private Dimension dimAreaTexto = new Dimension(150, 25);

	/**
	 * Create the frame.
	 */
	public VentanaPremios() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		idioma = IdiomaProperties.getIdiomaProperties();
		gestorPremios = GestorPremios.getGestorPremios();
		
		int log = GestorSesion.obtSesion().esLogopeda();
		if(log == 0){
			gestorPremios.addObserver(this);			

			dialog = new JProgressDialog(this, idioma.leerProperty("premios.titulo_cargando"), idioma.leerProperty("premios.mensaje_cargando"));
			gestorPremios.setParametrosYAccion(gestorPremios.accionCargar, null);
			new Thread(gestorPremios).start();	
			setEnabled(false);
		}else{
			new VentanaLogin();
			dispose();
		}	
	}

	private void getTituloSesion() {
	
		JLabel lblTitulo = new JLabel(idioma.leerProperty("premios.ventana"));
		lblTitulo.setHorizontalAlignment(0);
		lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
		lblTitulo.setOpaque(true);
		lblTitulo.setForeground(Color.black);
		
		Border paddingBorder = BorderFactory.createEmptyBorder(10,10,10,10);
		Border border = BorderFactory.createBevelBorder(0);
		lblTitulo.setBorder(BorderFactory.createCompoundBorder(border,paddingBorder));

		norte.add(lblTitulo);
	}
	
	private void getSeleccionPremios(){
		centro.setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		JLabel lblPremio = new JLabel(idioma.leerProperty("premio"));
		cbPremios = new JComboBox<>(gestorPremios.obtenerNombrePremios());
		cbPremios.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				
                if(arg0.getStateChange() == ItemEvent.SELECTED){
                	if(cbPremios.getSelectedIndex() != 0){
    					ImageIcon img = gestorPremios.getImagen(cbPremios.getSelectedIndex()-1);
    					if(img != null){
    						panelPremio.setImage(img);
    						
    					}else{
    						panelPremio.setImage(null);
    					}					
    				}else{
						panelPremio.setImage(null);
    				}
    				centro.updateUI();	
    				sur.updateUI();
                }				
			}
		});
		
		panel.add(lblPremio);
		panel.add(cbPremios);
		panelPremio = new ImagePanel(null);
		
		centro.add(panel, BorderLayout.NORTH);		
		centro.add(panelPremio, BorderLayout.CENTER);		
	}
	
	private void getBtnAnadirPremio(){
		sur.removeAll();
		sur.setLayout(new GridBagLayout());
		JLabel lblNombre = new JLabel(idioma.leerProperty("premios.nombre")+"*");
		lblNombre.setFont(new Font("Arial", Font.BOLD, 15));
		final JTextField txtNombre = new JTextField();
		txtNombre.setFont(new Font("Serif", Font.ITALIC, 12));
		txtNombre.setPreferredSize(dimAreaTexto);
		txtNombre.addKeyListener(FiltroTexto.textInputLimiter(txtNombre, 20));
		JButton btnAnadirPremio = new JButton(idioma.leerProperty("premios.anadir"));
		btnAnadirPremio.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String nombre = txtNombre.getText();
				if(nombre != null && nombre.trim().length() > 0){
					Boolean b = gestorPremios.existeNombre(nombre);
					if(b == null){return;
					}else if(b){
						JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("premios.error_existe"), idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
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
										 if(name.endsWith("png") || name.endsWith("jpg")){
											 return true;										 
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
				        int returnVal = fileChooser.showDialog(VentanaPremios.this,idioma.leerProperty("premios.seleccionar"));
				        if (returnVal == JFileChooser.APPROVE_OPTION) {    
							gestorPremios.setParametrosYAccion(gestorPremios.accionInsertar, new Object[]{nombre, fileChooser.getSelectedFile()});
							dialog = new JProgressDialog(VentanaPremios.this, idioma.leerProperty("premios.titulo_creando"), idioma.leerProperty("premios.mensaje_creando"));
							new Thread(gestorPremios).start();
							setEnabled(false);
				        }
				        fileChooser.setSelectedFile(null);
					}
				}else{
					JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("premios.error_nombre"), idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
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
				gestorPremios.deleteObserver(VentanaPremios.this);
				dispose();				
			}
		});

		JPanel flowPanel = new JPanel();
		flowPanel.setLayout(new FlowLayout());
		flowPanel.add(lblNombre);
		flowPanel.add(txtNombre);
		flowPanel.add(btnAnadirPremio);
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
		if((int) arg1 == gestorPremios.accionCargar){
			setSize(dimVentana);
			setLocationRelativeTo(null);

			contentPane = new JPanel();
			setContentPane(contentPane);
			norte = new JPanel();
			centro = new JPanel();
			sur = new JPanel();

			setLayout(new BorderLayout());

			getTituloSesion();
			getSeleccionPremios();
			getBtnAnadirPremio();

			add(norte, BorderLayout.NORTH);
			add(centro, BorderLayout.CENTER);
			add(sur, BorderLayout.SOUTH);
			setVisible(true);	
		}else if ((int) arg1 == gestorPremios.accionCargarError) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("error_carga"),
					idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
			gestorPremios.deleteObserver(this);
			new VentanaLogin();
			dispose();
		}else if((int) arg1 == gestorPremios.accionInsertar){
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("premios.mensaje_correcto"), idioma.leerProperty("premios.titulo_correcto"), JOptionPane.INFORMATION_MESSAGE);			            	
			cbPremios.removeAllItems();
			centro.removeAll();
			getSeleccionPremios();
			getBtnAnadirPremio();
			centro.updateUI();	
			sur.updateUI();		
		}else if((int) arg1 == gestorPremios.accionInsertarError){
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("premios.error_crear"), idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
		}
	}
}