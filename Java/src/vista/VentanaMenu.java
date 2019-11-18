package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
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


public class VentanaMenu extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private IdiomaProperties idioma;
	private GestorSesion sesion;
	
	private JPanel contentPane;
	private JPanel norte, centro;
			
	private Dimension dimVentana = new Dimension(350, 550);
	private Dimension dimVentanaReducida = new Dimension(350, 450);
	private Dimension dimBoton = new Dimension(170,50);

	/**
	 * Create the frame.
	 */
	public VentanaMenu() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		idioma = IdiomaProperties.getIdiomaProperties();
		sesion = GestorSesion.obtSesion();
		int esLogopeda = sesion.esLogopeda();
		if(esLogopeda == 0){
			setSize(dimVentana);			
		}else if(esLogopeda == 1){
			setSize(dimVentanaReducida);			
		}else{
			new VentanaLogin();
			dispose();
		}
		
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		setContentPane(contentPane);
		norte = new JPanel();
		centro = new JPanel();

		setLayout(new BorderLayout());
		centro.setLayout(new GridLayout(0,1,30,30));
		centro.setBorder( new EmptyBorder(30,30,30,30));
		
		getTituloMenu();
	
		getBtnConsultarSesion();
		if(esLogopeda == 0){
			getBtnAdministrarJuegos();
		}
		getBtnModificarDatos();
		getBtnCambiarUsuario();
	
		add(norte, BorderLayout.NORTH);
		add(centro, BorderLayout.CENTER);
		
		pack();
		setVisible(true);
	}

	private void getTituloMenu() {
	
		JLabel lblTitulo = new JLabel(idioma.leerProperty("menu_principal.ventana"));
		lblTitulo.setHorizontalAlignment(0);
		lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
		lblTitulo.setOpaque(true);
		lblTitulo.setForeground(Color.black);
		
		Border paddingBorder = BorderFactory.createEmptyBorder(10,10,10,10);
		Border border = BorderFactory.createBevelBorder(0);
		lblTitulo.setBorder(BorderFactory.createCompoundBorder(border,paddingBorder));

		norte.add(lblTitulo);
	}

	private void getBtnConsultarSesion(){
		JButton btnConsultarSesion = new JButton(idioma.leerProperty("consultar_sesion.ventana"));
		btnConsultarSesion.setMinimumSize(dimBoton);
		btnConsultarSesion.setPreferredSize(dimBoton);
		btnConsultarSesion.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		
		btnConsultarSesion.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new VentanaNino();
				dispose();		
			}
		});		
		centro.add(btnConsultarSesion);
	}
	
	private void getBtnAdministrarJuegos(){
		JButton btnAdministrarJuegos = new JButton(idioma.leerProperty("administrar_juego.ventana"));
		btnAdministrarJuegos.setMinimumSize(dimBoton);
		btnAdministrarJuegos.setPreferredSize(dimBoton);
		btnAdministrarJuegos.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		
		btnAdministrarJuegos.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new VentanaAdministrarJuegos();
				dispose();		
			}
		});		
		centro.add(btnAdministrarJuegos);
	}

	private void getBtnModificarDatos(){
		JButton btnModificarDatos = new JButton(idioma.leerProperty("modificar_datos.ventana"));
		btnModificarDatos.setMinimumSize(dimBoton);
		btnModificarDatos.setPreferredSize(dimBoton);
		btnModificarDatos.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		
		btnModificarDatos.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new VentanaModificarDatos();
				dispose();
		
			}
		});		
		centro.add(btnModificarDatos);
	}
	
	private void getBtnCambiarUsuario(){
		JButton btnCambiarUsuario = new JButton(idioma.leerProperty("cambiar_usuario.ventana"));
		btnCambiarUsuario.setMinimumSize(dimBoton);
		btnCambiarUsuario.setPreferredSize(dimBoton);
		btnCambiarUsuario.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		
		btnCambiarUsuario.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new VentanaLogin();
				dispose();			
			}
		});
		centro.add(btnCambiarUsuario);
	}
}