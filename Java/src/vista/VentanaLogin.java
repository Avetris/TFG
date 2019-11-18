package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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

public class VentanaLogin extends JFrame implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private IdiomaProperties idioma;
	private GestorSesion gestorSesion;

	private JPanel contentPane;
	private JPanel norte, sur, centro;

	private JTextField txtUsuario;

	private JPasswordField txtPass;

	private JCheckBox checkRecordar, checkLogopeda;

	private JProgressDialog dialog;

	private Dimension dimVentana = new Dimension(480, 300);
	private Dimension dimAreaTexto = new Dimension(200, 25);
	private Dimension dimBoton = new Dimension(150, 30);

	/**
	 * Create the frame.
	 */
	public VentanaLogin() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		idioma = IdiomaProperties.getIdiomaProperties();
		gestorSesion = GestorSesion.obtSesion();
		gestorSesion.borrarUsuario();

		setSize(dimVentana);
		setLocationRelativeTo(null);

		gestorSesion.addObserver(this);

		contentPane = new JPanel();
		setContentPane(contentPane);

		setMinimumSize(new Dimension(350, 250));
		setResizable(false);
		setLayout(new BorderLayout());

		norte = new JPanel();
		centro = new JPanel();
		sur = new JPanel();

		centro.setLayout(new GridLayout(4, 2));
		centro.setBorder(new EmptyBorder(10, 10, 10, 10));
		sur.setBorder(new EmptyBorder(10, 10, 10, 10));

		getTituloLogin();

		getUsuario();
		getPass();
		getLogopeda();
		getRecordar();
		getLblOlvido();
		getBtnLogin();
		getBtnRegistrar();

		comprobarRecordatorio();

		add(norte, BorderLayout.NORTH);
		add(centro, BorderLayout.CENTER);
		add(sur, BorderLayout.SOUTH);
		pack();
		setVisible(true);
	}

	private void getTituloLogin() {

		JLabel lblTitulo = new JLabel(idioma.leerProperty("login.ventana"));
		lblTitulo.setHorizontalAlignment(0);
		lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
		lblTitulo.setOpaque(true);
		lblTitulo.setForeground(Color.black);

		Border paddingBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		Border border = BorderFactory.createBevelBorder(0);
		lblTitulo.setBorder(BorderFactory.createCompoundBorder(border, paddingBorder));
		norte.add(lblTitulo);
	}

	private void getUsuario() {
		JLabel lblUsuario = new JLabel(idioma.leerProperty("usuario"));
		txtUsuario = new JTextField();
		txtUsuario.setFont(new Font("Serif", Font.ITALIC, 16));
		txtUsuario.setPreferredSize(dimAreaTexto);
		centro.add(lblUsuario);
		centro.add(txtUsuario);
	}

	private void getPass() {
		JLabel lblPass = new JLabel(idioma.leerProperty("contrasena"));
		txtPass = new JPasswordField(10);
		txtPass.setPreferredSize(dimAreaTexto);

		centro.add(lblPass);
		centro.add(txtPass);
	}

	private void getLogopeda() {
		checkLogopeda = new JCheckBox(idioma.leerProperty("soy_logopeda"));
		centro.add(checkLogopeda);
	}

	private void getRecordar() {
		checkRecordar = new JCheckBox(idioma.leerProperty("recordar"));
		centro.add(checkRecordar);
	}

	private void getLblOlvido() {
		JLabel lblOlvido = new JLabel(idioma.leerProperty("contrasena_olvidada"));

		lblOlvido.addMouseListener(new MouseListener() {
			@Override
			public void mousePressed(MouseEvent e) {
				String usuario = txtUsuario.getText();
				if (usuario == null || usuario.trim().length() == 0) {

				} else {
					gestorSesion.setDatosRecuperar(usuario, checkLogopeda.isSelected());
					dialog = new JProgressDialog(VentanaLogin.this,
							idioma.leerProperty("login.titulo_generar_contrasena"),
							idioma.leerProperty("login.mensaje_generar_contrasena"));
					setEnabled(false);
					new Thread(gestorSesion).start();
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}
		});
		centro.add(lblOlvido);
	}

	private void getBtnLogin() {
		JButton btnLogin = new JButton(idioma.leerProperty("entrar"));
		btnLogin.setMinimumSize(dimBoton);
		btnLogin.setPreferredSize(dimBoton);
		btnLogin.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));

		btnLogin.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String usuario = txtUsuario.getText();
				String contrasena = FiltroTexto.getPassword(txtPass.getPassword());
				if (usuario == null || usuario.equals("")) {
					JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("error.sin_usuario"));
				} else if (contrasena == null || contrasena.equals("")) {
					JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("error.sin_contrasena"));
				} else {
					gestorSesion.setDatos(usuario, contrasena, checkLogopeda.isSelected(), checkRecordar.isSelected());
					dialog = new JProgressDialog(VentanaLogin.this, idioma.leerProperty("login.titulo_conectandose"),
							idioma.leerProperty("login.mensaje_conectandose"));
					setEnabled(false);
					new Thread(gestorSesion).start();
				}
			}
		});
		sur.add(btnLogin);
	}

	private void getBtnRegistrar() {
		JButton btnRegistrar = new JButton(idioma.leerProperty("registrar"));
		btnRegistrar.setMinimumSize(dimBoton);
		btnRegistrar.setPreferredSize(dimBoton);
		btnRegistrar.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));

		btnRegistrar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new VentanaRegistro();
				gestorSesion.deleteObserver(VentanaLogin.this);
				dispose();
			}
		});

		sur.add(btnRegistrar);
	}

	private void comprobarRecordatorio() {
		String[] usuario = idioma.obtenerUsuario();
		if (usuario != null && usuario.length == 2) {
			txtUsuario.setText(usuario[0]);
			if (usuario[1].equals("true")) {
				checkLogopeda.setSelected(true);
			}
			checkRecordar.setSelected(true);
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		dialog.dispose();
		setEnabled(true);
		if ((int) arg == GestorSesion.obtSesion().accionLoginCorrecto) {
			gestorSesion.deleteObserver(VentanaLogin.this);
			new VentanaMenu();
			dispose();
		} else if ((int) arg == gestorSesion.accionLoginIncorrecto) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("error.datos_incorrectos"));
		} else if ((int) arg == gestorSesion.accionCargarError) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("error_carga"), idioma.leerProperty("error"),
					JOptionPane.ERROR_MESSAGE);
		} else if ((int) arg == gestorSesion.accionRecuperarContrasena) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("login.mensaje_contrasena"),
					idioma.leerProperty("login.titulo_contrasena"), JOptionPane.INFORMATION_MESSAGE);
		} else if ((int) arg == gestorSesion.accionRecuperarContrasenaError) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("login.error_cambio_contrasena"), idioma.leerProperty("error"),
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public static void main(String[] args){
		
		new VentanaLogin();

	}

}