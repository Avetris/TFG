package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import modelo.IdiomaProperties;
import modelo.GestorSesion;


public class VentanaAdministrarJuegos extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private IdiomaProperties idioma;
	private GestorSesion sesion;
	
	private JPanel contentPane;
	private JPanel norte, centro, sur;
		
	private Dimension dimVentana = new Dimension(350, 550);
	private Dimension dimBoton = new Dimension(170,40);

	/**
	 * Create the frame.
	 */
	public VentanaAdministrarJuegos() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		idioma = IdiomaProperties.getIdiomaProperties();
		sesion = GestorSesion.obtSesion();
		if(sesion.esLogopeda() != 0){	
			new VentanaLogin();
			dispose();
		}else{
			setSize(dimVentana);	
			setLocationRelativeTo(null);

			contentPane = new JPanel();
			setContentPane(contentPane);
			setLayout(new BorderLayout());
			centro = new JPanel();
			norte = new JPanel();
			sur = new JPanel();

			centro.setLayout(new BorderLayout());
			centro.setLayout(new GridLayout(0,2,30,30));
			centro.setBorder( new EmptyBorder(30,30,30,30));

			
			getTituloAdministrarJuegos();
		
			getBtnFondos();		
			getBtnMapas();
			getBtnPremios();		
			getBtnContenido();
			getBtnGrupos();
			getBtnMinijuegos();
			getBtnPermisos();
			getBtnAtras();
			
			add(norte, BorderLayout.NORTH);
			add(centro, BorderLayout.CENTER);
			add(sur, BorderLayout.SOUTH);
			
			pack();
			setVisible(true);
		}
		
		
	}

	private void getTituloAdministrarJuegos() {
	
		JLabel lblTitulo = new JLabel(idioma.leerProperty("administrar_juego.ventana"));
		lblTitulo.setHorizontalAlignment(0);
		lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
		lblTitulo.setOpaque(true);
		lblTitulo.setForeground(Color.black);
		
		Border paddingBorder = BorderFactory.createEmptyBorder(10,10,10,10);
		Border border = BorderFactory.createBevelBorder(0);
		lblTitulo.setBorder(BorderFactory.createCompoundBorder(border,paddingBorder));

		norte.add(lblTitulo);
	}

	private void getBtnFondos(){
		JButton btnConsultarSesion = new JButton(idioma.leerProperty("fondos.ventana"));
		btnConsultarSesion.setMinimumSize(dimBoton);
		btnConsultarSesion.setPreferredSize(dimBoton);
		btnConsultarSesion.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		
		btnConsultarSesion.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new VentanaFondos();
				dispose();
			}
		});		
		centro.add(btnConsultarSesion);
	}
	
	private void getBtnMapas(){
		JButton btnAdministrarJuegos = new JButton(idioma.leerProperty("mapas.ventana"));
		btnAdministrarJuegos.setMinimumSize(dimBoton);
		btnAdministrarJuegos.setPreferredSize(dimBoton);
		btnAdministrarJuegos.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		
		btnAdministrarJuegos.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new VentanaMapas();		
				dispose();
			}
		});		
		centro.add(btnAdministrarJuegos);
	}	
	
	private void getBtnPremios(){
		JButton btnAdministrarJuegos = new JButton(idioma.leerProperty("premios.ventana"));
		btnAdministrarJuegos.setMinimumSize(dimBoton);
		btnAdministrarJuegos.setPreferredSize(dimBoton);
		btnAdministrarJuegos.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		
		btnAdministrarJuegos.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new VentanaPremios();		
				dispose();
			}
		});		
		centro.add(btnAdministrarJuegos);
	}
	
	private void getBtnContenido(){
		JButton btnContenido = new JButton(idioma.leerProperty("contenido.ventana"));
		btnContenido.setMinimumSize(dimBoton);
		btnContenido.setPreferredSize(dimBoton);
		btnContenido.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		
		btnContenido.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new VentanaContenido();
				dispose();		
			}
		});		
		centro.add(btnContenido);
	}
	
	private void getBtnGrupos(){
		JButton btnGrupos = new JButton(idioma.leerProperty("grupo.ventana"));
		btnGrupos.setMinimumSize(dimBoton);
		btnGrupos.setPreferredSize(dimBoton);
		btnGrupos.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		
		btnGrupos.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new VentanaGrupos();
				dispose();			
			}
		});		
		centro.add(btnGrupos);
	}
	


	private void getBtnMinijuegos(){
		JButton btnMinijuegos= new JButton(idioma.leerProperty("minijuego.ventana"));
		btnMinijuegos.setMinimumSize(dimBoton);
		btnMinijuegos.setPreferredSize(dimBoton);
		btnMinijuegos.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		
		btnMinijuegos.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new VentanaMinijuegos();
				dispose();
		
			}
		});		
		centro.add(btnMinijuegos);
	}
	
	private void getBtnPermisos(){
		JButton btnCambiarUsuario = new JButton(idioma.leerProperty("permisos.ventana"));
		btnCambiarUsuario.setMinimumSize(dimBoton);
		btnCambiarUsuario.setPreferredSize(dimBoton);
		btnCambiarUsuario.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		
		btnCambiarUsuario.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new VentanaPermisos();
				dispose();			
			}
		});		
		centro.add(btnCambiarUsuario);
	}
	
	private void getBtnAtras(){	
		sur.setLayout(new GridBagLayout());
		JButton btnAtras = new JButton("<");
		btnAtras.setEnabled(true);		
		
		
		btnAtras.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new VentanaMenu();
				dispose();				
			}
		});		
		 GridBagConstraints c = new GridBagConstraints();

	        c.fill = GridBagConstraints.HORIZONTAL;
	        c.weightx = 2.0; 
	        c.gridwidth = GridBagConstraints.RELATIVE;  
			c.insets = new Insets(10,10,10,10);
	        c.gridy = 1; 
	        c.fill = GridBagConstraints.EAST;
	        c.anchor = GridBagConstraints.WEST;
		
		sur.add(btnAtras, c);
	}
}