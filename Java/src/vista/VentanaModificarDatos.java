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
import java.util.Properties;

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

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import modelo.FiltroTexto;
import modelo.IdiomaProperties;
import modelo.GestorSesion;


public class VentanaModificarDatos extends JFrame implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private IdiomaProperties idioma;
	private GestorSesion gestorSesion;
	private JPanel contentPane;
	private JPanel norte, centro, sur;
	
	private JTextField txtUsuario, txtNombre, txtApellidos, txtTelefono1, txtTelefono2;
	
	private JComboBox<String> cbIdioma;
	
	private JPasswordField txtPass, txtPassNueva;	

	JProgressDialog dialog;
	
	private JDatePickerImpl datePicker;
	
	private Dimension dimVentana = new Dimension(350, 550);
	private Dimension dimVentanaAumentada = new Dimension(400, 550);
	private Dimension dimBoton = new Dimension(150,30);

	/**
	 * Create the frame.
	 */
	public VentanaModificarDatos() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		idioma = IdiomaProperties.getIdiomaProperties();
		gestorSesion = GestorSesion.obtSesion();
		
		int esLogopeda = gestorSesion.esLogopeda();
		if(esLogopeda != 0 && esLogopeda != 1){
			new VentanaLogin();
			dispose();			
		}else{
			gestorSesion.addObserver(this);
			if(esLogopeda == 0){
				setSize(dimVentana);			
			}else if(esLogopeda == 0){
				setSize(dimVentanaAumentada);			
			}
			setLocationRelativeTo(null);

			contentPane = new JPanel();
			setContentPane(contentPane);
			norte = new JPanel();
			centro = new JPanel();
			sur = new JPanel();

			setLayout(new BorderLayout());	
			centro.setLayout(new GridLayout(0,2,10,10));
			
			centro.setBorder(new EmptyBorder(10,10,10,10));

			getTituloModificarDatos();		
			getUsuario();	
			getNombre();	
			getApellidos();		
			getPass();
			getPassNueva();
			getIdioma();	
			if(esLogopeda == 1){
				getFechaNacimiento();	
				getTelefono1();
				getTelefono2();
			}
			getBtns();		
			
			rellenar();
			
			add(norte, BorderLayout.NORTH);
			add(centro, BorderLayout.CENTER);
			add(sur, BorderLayout.SOUTH);
			
			pack();
			setVisible(true);
		}		
	}

	private void getTituloModificarDatos() {
	
		JLabel lblTitulo = new JLabel(idioma.leerProperty("modificar_datos.ventana"));
		lblTitulo.setHorizontalAlignment(0);
		lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
		lblTitulo.setOpaque(true);
		lblTitulo.setForeground(Color.black);
		
		Border paddingBorder = BorderFactory.createEmptyBorder(10,10,10,10);
		Border border = BorderFactory.createBevelBorder(0);
		lblTitulo.setBorder(BorderFactory.createCompoundBorder(border,paddingBorder));

		norte.add(lblTitulo);
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
	
	private void getPassNueva() {
		JLabel lblPassNueva = new JLabel(idioma.leerProperty("contrasena_nueva"));
		lblPassNueva.setAlignmentX(Component.RIGHT_ALIGNMENT);
		txtPassNueva = new JPasswordField();
		txtPassNueva.addKeyListener(FiltroTexto.textInputLimiter(txtPass, 50));

		centro.add(lblPassNueva);
		centro.add(txtPassNueva);
	}
	
	private void getIdioma() {
		JLabel lblIdioma = new JLabel(idioma.leerProperty("idioma")+"*");
		lblIdioma.setAlignmentX(Component.CENTER_ALIGNMENT);
		cbIdioma = new JComboBox<String>();
		cbIdioma.addItem(idioma.leerProperty("idioma.espanol"));
		cbIdioma.addItem(idioma.leerProperty("idioma.euskera"));
		cbIdioma.setSelectedItem(0);	
		centro.add(lblIdioma);
		centro.add(cbIdioma);
	}
	
	private void getFechaNacimiento() {
		JLabel lblFechaNacimiento = new JLabel(idioma.leerProperty("fecha_nacimiento")+"*");
		lblFechaNacimiento.setAlignmentX(Component.CENTER_ALIGNMENT);

		UtilDateModel model = new UtilDateModel();
		Properties p = new Properties();
		p.put("text.today", idioma.leerProperty("fecha.hoy"));
		p.put("text.month", idioma.leerProperty("fecha.mes"));
		p.put("text.year", idioma.leerProperty("fecha.ano"));
		JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
		datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
		
		centro.add(lblFechaNacimiento);
		centro.add(datePicker);
	}
	
	private void getTelefono1() {
		JLabel lblTelefono1 = new JLabel(idioma.leerProperty("nino.telefono1")+"*");
		lblTelefono1.setAlignmentX(Component.CENTER_ALIGNMENT);

		txtTelefono1 = new JTextField();
		txtTelefono1.setFont(new Font("Serif", Font.ITALIC, 16));
		txtTelefono1.addKeyListener(FiltroTexto.textInputLimiter(txtTelefono1, 9));
		
		centro.add(lblTelefono1);
		centro.add(txtTelefono1);
	}
	
	private void getTelefono2() {
		JLabel lblTelefono2 = new JLabel(idioma.leerProperty("nino.telefono2"));
		lblTelefono2.setAlignmentX(Component.CENTER_ALIGNMENT);

		txtTelefono2 = new JTextField();
		txtTelefono2.setFont(new Font("Serif", Font.ITALIC, 16));
		txtTelefono2.addKeyListener(FiltroTexto.textInputLimiter(txtTelefono2, 9));
		
		centro.add(lblTelefono2);
		centro.add(txtTelefono2);
	}
	
	private void getBtns(){
		sur.setLayout(new GridBagLayout());
		JButton btnRegistrar = new JButton(idioma.leerProperty("guardar"));
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
				System.out.println(apellidos.length());
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
				String contrasenaNueva = FiltroTexto.getPassword(txtPassNueva.getPassword());
				if(usuario.length() == 0 || nombre.length() == 0 || apellidos.length() == 0 || contrasena.length() == 0){
					JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("error.campos_obligatorios"), idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
				}else{
					if(!GestorSesion.obtSesion().contrasenaCorrecta(contrasena)){
						JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("error.contrasena_incorrecta"), idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);						
					}else if(!FiltroTexto.soloTexto(nombre)){						
						JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("error.formato_nombre"), idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
					}else if(!FiltroTexto.soloTexto(apellidos)){
						JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("error.formato_apellidos"), idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);					
					}else{
						if(gestorSesion.esLogopeda() == 1){
							String telefono1 = txtTelefono1.getText().trim();
							String telefono2 = txtTelefono2.getText().trim();
							String fecha = datePicker.getJFormattedTextField().getText();
							if(telefono1 == null || telefono1.length() == 0 || fecha == null || fecha.trim().length()==0){
								JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("error.campos_obligatorios"), idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
							}else if(!FiltroTexto.soloNumero(telefono1) || !FiltroTexto.soloNumero(telefono2) && telefono2.length() > 0){
								JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("error.formato_telefono"), idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);						
							}else{
								dialog = new JProgressDialog(VentanaModificarDatos.this, idioma.leerProperty("modificarDatos.titulo_modificando"),
										idioma.leerProperty("modificarDatos.mensaje_modificando"));
								gestorSesion.setDatosActualizar(usuario,nombre,apellidos,contrasenaNueva,idiomaSeleccionado, fecha, telefono1, telefono2);
								new Thread(gestorSesion).start();
							}
						}else{
							dialog = new JProgressDialog(VentanaModificarDatos.this, idioma.leerProperty("modificarDatos.titulo_modificando"),
									idioma.leerProperty("modificarDatos.mensaje_modificando"));
							gestorSesion.setDatosActualizar(usuario,nombre,apellidos,contrasenaNueva,idiomaSeleccionado, null, null, null);
							new Thread(gestorSesion).start();
							
						}
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
				gestorSesion.deleteObserver(VentanaModificarDatos.this);
				new VentanaMenu();
				dispose();				
			}
		});

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 2.0; 
        c.gridwidth = 2;    
		c.insets = new Insets(10,10,10,10);
        sur.add(btnRegistrar, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 2.0; 
        c.gridwidth = GridBagConstraints.RELATIVE;  
		c.insets = new Insets(10,10,10,10);
        c.gridy = 1; 
        c.fill = GridBagConstraints.EAST;
        c.anchor = GridBagConstraints.WEST;
		
		sur.add(btnAtras, c);
	}
	
	private void rellenar(){
		Object[] datos = gestorSesion.obtenerDatos();
		if(datos != null){
			txtUsuario.setText(datos[0].toString());
			txtNombre.setText(datos[1].toString());
			txtApellidos.setText(datos[2].toString());
			if(datos[3].equals("espanol")){
				cbIdioma.setSelectedIndex(0);
			}else if(datos[3].equals("euskera")){
				cbIdioma.setSelectedIndex(1);				
			}else{
				cbIdioma.setSelectedIndex(0);				
			}
			if(datos.length == 7){
				datePicker.getJFormattedTextField().setText(String.valueOf(datos[4]));
				txtTelefono1.setText(datos[5].toString()); 
				txtTelefono2.setText(datos[6] != null ? datos[6].toString() : "");
			}
		}
	}
	
	@Override
	public void update(Observable o, Object arg) {
		dialog.dispose();
		setEnabled(true);
		if ((int) arg == gestorSesion.accionCargarError) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("error_carga"),
					idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
			gestorSesion.deleteObserver(this);
			new VentanaLogin();
			dispose();
		}else if((int) arg == gestorSesion.accionModificacion){
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("modificarDatos.mensaje_correcta"), idioma.leerProperty("modificarDatos.titulo_correcta"), JOptionPane.INFORMATION_MESSAGE);
			gestorSesion.deleteObserver(VentanaModificarDatos.this);
			new VentanaMenu();
			dispose();			
		}else if((int) arg == gestorSesion.accionModificacionError){
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("modificarDatos.error_actualizar_datos"));
		}
	}
}