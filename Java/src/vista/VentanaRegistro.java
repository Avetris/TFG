package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import modelo.FiltroTexto;
import modelo.IdiomaProperties;
import modelo.GestorSesion;


public class VentanaRegistro extends JFrame implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private IdiomaProperties idioma;
	private JPanel contentPane;
	private JPanel norte, centro, sur;

	private JTextField txtUsuario, txtNombre, txtApellidos;
	
	private JComboBox<String> cbIdioma;
	
	private JPasswordField txtPass;	
	
	private JProgressDialog dialog;
		
	private Dimension dimVentana = new Dimension(400, 550);
	private Dimension dimBoton = new Dimension(150,30);

	/**
	 * Create the frame.
	 */
	public VentanaRegistro() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		idioma = IdiomaProperties.getIdiomaProperties();
		setSize(dimVentana);
		setLocationRelativeTo(null);
		
		GestorSesion.obtSesion().addObserver(this);

		contentPane = new JPanel();
		setContentPane(contentPane);
		setLayout(new BorderLayout());
		setMinimumSize(new Dimension(350, 450));
		setResizable(false);
		
		norte = new JPanel();
		centro = new JPanel();
		sur = new JPanel();

		centro.setLayout(new GridLayout(0,2,10,10));
		sur.setLayout(new GridBagLayout ());
		centro.setBorder(new EmptyBorder(10,10,10,10));
		sur.setBorder(new EmptyBorder(10,10,10,10));		

		getTituloRegistro();
		
		getUsuario();	
		getNombre();	
		getApellidos();		
		getPass();		
		getIdioma();	
		
		getBtns();		

		add(norte, BorderLayout.NORTH);
		add(centro, BorderLayout.CENTER);
		add(sur, BorderLayout.SOUTH);
		pack();
		
		setVisible(true);
	}

	private void getTituloRegistro() {
	
		JLabel lblTitulo = new JLabel(idioma.leerProperty("registro.ventana"));
		lblTitulo.setHorizontalAlignment(0);
		lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
		lblTitulo.setOpaque(true);
		lblTitulo.setForeground(Color.black);
		
		Border paddingBorder = BorderFactory.createEmptyBorder(10,10,10,10);
		Border border = BorderFactory.createBevelBorder(0);
		lblTitulo.setBorder(BorderFactory.createCompoundBorder(border,paddingBorder));

		norte.add(lblTitulo, BorderLayout.NORTH);
	}


	private void getUsuario() {
		JLabel lblUsuario = new JLabel(idioma.leerProperty("usuario")+"*");
		lblUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);
		txtUsuario = new JTextField();
		txtUsuario.setFont(new Font("Serif", Font.ITALIC, 16));
		txtUsuario.addKeyListener(FiltroTexto.textInputLimiter(txtUsuario, 50));
		
		centro.add(lblUsuario);
		centro.add(txtUsuario);
	}
	
	private void getNombre() {
		JLabel lblNombre = new JLabel(idioma.leerProperty("nombre")+"*");
		lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
		txtNombre = new JTextField();
		txtNombre.setFont(new Font("Serif", Font.ITALIC, 16));
		txtNombre.addKeyListener(FiltroTexto.textInputLimiter(txtNombre, 20));
	
		centro.add(lblNombre);
		centro.add(txtNombre);
	}
	
	private void getApellidos() {
		JLabel lblApellidos = new JLabel(idioma.leerProperty("apellidos")+"*");
		lblApellidos.setAlignmentX(Component.CENTER_ALIGNMENT);
		txtApellidos = new JTextField();
		txtApellidos.setFont(new Font("Serif", Font.ITALIC, 16));
		txtApellidos.addKeyListener(FiltroTexto.textInputLimiter(txtApellidos, 30));

		centro.add(lblApellidos);
		centro.add(txtApellidos);
	}
	
	private void getPass() {
		JLabel lblPass = new JLabel(idioma.leerProperty("contrasena")+"*");
		lblPass.setAlignmentX(Component.RIGHT_ALIGNMENT);
		txtPass = new JPasswordField();
		txtPass.addKeyListener(FiltroTexto.textInputLimiter(txtPass, 50));

		centro.add(lblPass);
		centro.add(txtPass);
	}
	
	private void getIdioma() {
		JLabel lblIdioma = new JLabel(idioma.leerProperty("idioma")+"*");
		lblIdioma.setAlignmentX(Component.CENTER_ALIGNMENT);
		cbIdioma = new JComboBox<String>(new String[]{idioma.leerProperty("idioma.espanol"), idioma.leerProperty("idioma.euskera")});
		cbIdioma.setSelectedItem(0);	

		centro.add(lblIdioma);
		centro.add(cbIdioma);
	}
	
	private void getBtns(){
		JButton btnRegistrar = new JButton(idioma.leerProperty("registrar"));
		btnRegistrar.setAlignmentX(CENTER_ALIGNMENT);
		btnRegistrar.setMinimumSize(dimBoton);
		btnRegistrar.setPreferredSize(dimBoton);
		btnRegistrar.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		
		btnRegistrar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String usuario = txtUsuario.getText();
				String nombre = txtNombre.getText();
				String apellidos = txtApellidos.getText();
				String idiomaSeleccionado = null;
				switch(cbIdioma.getSelectedIndex()){
				case 0:
					idiomaSeleccionado = "espanol";
					break;
				case 1:
					idiomaSeleccionado = "euskera";
					break;
				default:
					idiomaSeleccionado = "espanol";
				}
				String contrasena = FiltroTexto.getPassword(txtPass.getPassword());
				if(usuario.length() == 0 || nombre.length() == 0 || apellidos.length() == 0 || contrasena.length() == 0){
					JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("error.campos_obligatorios"), idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
				}else{
					if(!FiltroTexto.soloTexto(nombre)){						
						JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("error.formato_nombre"), idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
					}else if(!FiltroTexto.soloTexto(apellidos)){
						JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("error.formato_apellidos"), idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);					
					}else{
						GestorSesion.obtSesion().setDatosRegistro(usuario,nombre,apellidos,contrasena,idiomaSeleccionado);
						dialog = new JProgressDialog(VentanaRegistro.this, idioma.leerProperty("registro.titulo_creando"), idioma.leerProperty("registro.mensaje_creando"));
						setEnabled(false);
						new Thread(GestorSesion.obtSesion()).start();	
					}						
				}				
			}
		});
		
		JButton btnAtras = new JButton("<");
		btnAtras.setEnabled(true);
		btnAtras.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		
		btnAtras.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new VentanaLogin();
				GestorSesion.obtSesion().deleteObserver(VentanaRegistro.this);
				dispose();				
			}
		});

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 2.0; 
        c.gridwidth = 2;    
		sur.add(btnRegistrar, c);	
		c.insets = new Insets(10,0,0,0);
        c.gridy = 1; 
        c.fill = GridBagConstraints.EAST;
        c.anchor = GridBagConstraints.WEST;
		sur.add(btnAtras, c);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		dialog.dispose();
		setEnabled(true);	
		if((int) arg == GestorSesion.obtSesion().accionRegistro){
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("registro.mensaje_registro_correcto"), idioma.leerProperty("registro.titulo_registro_correcto"), JOptionPane.INFORMATION_MESSAGE);
			GestorSesion.obtSesion().deleteObserver(VentanaRegistro.this);
			new VentanaLogin();
			dispose();
		}else if((int) arg == GestorSesion.obtSesion().accionRegistroUsuario){
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("error.usuario_existente"), idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
		}else if((int) arg == GestorSesion.obtSesion().accionRegistroError){
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("error.registro"), idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
		}
	}
}