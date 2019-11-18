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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableRowSorter;
import modelo.GestorMinijuegos;
import modelo.IdiomaProperties;
import modelo.GestorSesion;

public class VentanaMinijuegos extends JFrame implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private IdiomaProperties idioma;
	private GestorMinijuegos gestorMinijuegos;

	private JPanel contentPane;
	private JPanel norte, sur, centro;

	JProgressDialog dialog;

	TableRowSorter<CustomTableModel> sorter;

	private CustomJTable table;

	JComboBox<String> cbMinijuego;
	JComboBox<String> cbGrupo;

	String[] nombreColumnas = null;

	private Dimension dimBoton = new Dimension(150, 30);

	/**
	 * Create the frame.
	 */
	public VentanaMinijuegos() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		idioma = IdiomaProperties.getIdiomaProperties();
		gestorMinijuegos = GestorMinijuegos.getGestorMinijuegos();

		if (GestorSesion.obtSesion().esLogopeda() == 0) {
			nombreColumnas = new String[] { idioma.leerProperty("grupo.id"), idioma.leerProperty("grupo.nombre"),
					idioma.leerProperty("borrar") };

			gestorMinijuegos.addObserver(this);

			dialog = new JProgressDialog(this, idioma.leerProperty("minijuego.titulo_cargando"),
					idioma.leerProperty("minijuego.mensaje_cargando"));
			gestorMinijuegos.setParametrosYAccion(gestorMinijuegos.accionCargar, null, null, null);
			new Thread(gestorMinijuegos).start();
		} else {
			new VentanaLogin();
			dispose();
		}
	}

	private void getTituloMinijuego() {

		JLabel lblTitulo = new JLabel(idioma.leerProperty("minijuego.ventana"));
		lblTitulo.setHorizontalAlignment(0);
		lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
		lblTitulo.setOpaque(true);
		lblTitulo.setForeground(Color.black);

		Border paddingBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		Border border = BorderFactory.createBevelBorder(0);
		lblTitulo.setBorder(BorderFactory.createCompoundBorder(border, paddingBorder));

		norte.add(lblTitulo);

	}

	private void getCombo(int pos) {
		JPanel form = new JPanel(new FlowLayout());
		JLabel lblNombre = new JLabel(idioma.leerProperty("minijuego.seleccionar"));
		lblNombre.setFont(new Font("Arial", Font.BOLD, 15));
		cbMinijuego = new JComboBox<>(gestorMinijuegos.obtenerNombres());
		if (pos < cbMinijuego.getItemCount() && pos > -1)
			cbMinijuego.setSelectedIndex(pos);
		
		cbMinijuego.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {

				if (arg0.getStateChange() == ItemEvent.SELECTED) {
					centro.removeAll();
					getCombo(cbMinijuego.getSelectedIndex());
					getTable();
					getModificar();
					centro.updateUI();
				}
			}
		});

		lblNombre.setLabelFor(cbMinijuego);
		form.add(lblNombre);
		form.add(cbMinijuego);
		centro.add(form, BorderLayout.NORTH);
	}

	private void getTable() {
		gestorMinijuegos.setMinijuego(cbMinijuego.getSelectedIndex());
		String[] grupos = gestorMinijuegos.obtenerNombresGrupos();
		Object[][] datos = gestorMinijuegos.obtenerGruposMinijuego(cbMinijuego.getSelectedIndex());
		if (grupos == null) {
			centro.add(new JLabel(idioma.leerProperty("minijuego.sin_grupo")));
		} else if (grupos.length == 1 && datos.length == 1) {
			centro.add(new JLabel(idioma.leerProperty("minijuego.sin_grupos_disponibles")));
		}else if (grupos.length > 1) {
			cbGrupo = new JComboBox<>(grupos);
			table = new CustomJTable(new CustomTableModel(datos, nombreColumnas, false, false));
			table.getColumnModel().getColumn(1).setMinWidth(80);
			table.todosNoSorteables();
			table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(cbGrupo));
			table.setRowHeight(20);
	        table.primerNumero();
			centro.add(new JScrollPane(table), BorderLayout.CENTER);
		}else{
			cbGrupo = new JComboBox<>(new String[0]);
			table = new CustomJTable(new CustomTableModel(datos, nombreColumnas, false, false));
			table.getColumnModel().getColumn(1).setMinWidth(80);
			table.todosNoSorteables();
			table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(cbGrupo));
			table.setRowHeight(20);
	        table.primerNumero();
			centro.add(new JScrollPane(table), BorderLayout.CENTER);
		}
	}

	private void getModificar() {
		String[] datos = gestorMinijuegos.obtenerDatos();
		JPanel form = new JPanel(new GridBagLayout());
		JLabel lblNombre = new JLabel(idioma.leerProperty("minijuego.nombre"));
		JLabel lblDescripcion = new JLabel(idioma.leerProperty("minijuego.descripcion"));
		JLabel lblTamano = new JLabel(idioma.leerProperty("minijuego.tamano"));
		JLabel txtTamano = new JLabel(datos[2]);
		lblNombre.setFont(new Font("Arial", Font.BOLD, 15));
		lblDescripcion.setFont(new Font("Arial", Font.BOLD, 15));
		lblTamano.setFont(new Font("Arial", Font.BOLD, 15));
		txtTamano.setFont(new Font("Serif", Font.ITALIC, 12));
		final JTextField txtNombre = new JTextField((datos == null ? "" : datos[0]));
		final JTextArea txtDescripcion = new JTextArea((datos == null ? "" : datos[1]));
		txtNombre.setFont(new Font("Serif", Font.ITALIC, 12));
		txtDescripcion.setFont(new Font("Serif", Font.ITALIC, 12));
		txtNombre.setMinimumSize(new Dimension(150, 25));
		txtNombre.setPreferredSize(new Dimension(200, 25));
		txtDescripcion.setPreferredSize(new Dimension(200, 60));
		txtDescripcion.setWrapStyleWord(true);
		txtDescripcion.setCaretPosition(0);
		txtDescripcion.setLineWrap(true);

		JScrollPane scroll = new JScrollPane(txtDescripcion, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setPreferredSize(new Dimension(200, 60));

		JButton btnAnadir = new JButton(idioma.leerProperty("minijuego.modificar"));
		btnAnadir.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (txtNombre.getText() == null || txtNombre.getText().trim().length() == 0) {
					JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("minijuego.error_nombre"),
							idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
				} else if (txtDescripcion.getText() == null || txtDescripcion.getText().trim().length() == 0) {
					JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("minijuego.error_descripcion"),
							idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
				} else {
					Boolean b = gestorMinijuegos.existeNombre(txtNombre.getText());
					if(b == null){return;
					}else if(b){
						JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("minijuego.error_nombre_existe"),
								idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
					}else{
						gestorMinijuegos.setParametrosYAccion(gestorMinijuegos.accionModificar, txtNombre.getText(),
								txtDescripcion.getText(), null);
						dialog = new JProgressDialog(VentanaMinijuegos.this,
								idioma.leerProperty("minijuego.titulo_modificando"),
								idioma.leerProperty("minijuego.mensaje_modificando"));
						new Thread(gestorMinijuegos).start();
					}
				}
			}
		});
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(10, 10, 10, 10);
		lblNombre.setLabelFor(txtNombre);
		lblDescripcion.setLabelFor(scroll);
		lblTamano.setLabelFor(txtTamano);
		form.add(lblNombre, c);
		form.add(txtNombre, c);
		form.add(lblTamano, c);
		form.add(txtTamano, c);
		form.add(lblDescripcion, c);
		form.add(scroll, c);
		form.add(btnAnadir, c);

		centro.add(form, BorderLayout.SOUTH);
	}

	private void getBtnAnadir() {
		JButton btnAnadir = new JButton(idioma.leerProperty("minijuego.anadir"));
		btnAnadir.setAlignmentX(CENTER_ALIGNMENT);
		btnAnadir.setMinimumSize(dimBoton);
		btnAnadir.setPreferredSize(dimBoton);
		btnAnadir.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));

		btnAnadir.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				List<Integer> grupos = new ArrayList<>();
				int nombre = getSelected(
						(table.getValueAt(table.getRowCount()-1, 1) == null) ? "" : String.valueOf(table.getValueAt(table.getRowCount()-1, 1)));
				if (nombre == -1) {
					JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("minijuego.error_seleccionar"),
							idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
					return;
				} else {
					grupos.add(nombre);
				}
				gestorMinijuegos.setParametrosYAccion(gestorMinijuegos.accionInsertar, null, null, grupos);
				dialog = new JProgressDialog(VentanaMinijuegos.this, idioma.leerProperty("minijuego.titulo_creando"),
						idioma.leerProperty("minijuego.mensaje_creando"));
				new Thread(gestorMinijuegos).start();
			}
		});

		JButton btnEliminar = new JButton(idioma.leerProperty("minijuego.eliminar"));
		btnEliminar.setAlignmentX(CENTER_ALIGNMENT);
		btnEliminar.setMinimumSize(dimBoton);
		btnEliminar.setPreferredSize(dimBoton);
		btnEliminar.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));

		btnEliminar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int seleccionado = JOptionPane.showOptionDialog(contentPane,
						idioma.leerProperty("minijuego.mensaje_eliminar"),
						idioma.leerProperty("minijuego.titulo_eliminar"), JOptionPane.YES_NO_OPTION,
						JOptionPane.DEFAULT_OPTION, null,
						new String[] { idioma.leerProperty("aceptar"), idioma.leerProperty("cancelar") },
						idioma.leerProperty("aceptar"));
				if (seleccionado == 0) {
					List<Integer> valores = new ArrayList<>();
					for (int i = 0; i < table.getRowCount() - 1; i++) {
						if ((Boolean) table.getValueAt(i, 2)) {
							valores.add(Integer.parseInt(String.valueOf(table.getValueAt(i, 0))));
						}
					}
					if (valores.size() == 0) {
						JOptionPane.showMessageDialog(contentPane,
								idioma.leerProperty("minijuego.error_elementos_eliminar"), idioma.leerProperty("error"),
								JOptionPane.ERROR_MESSAGE);
					} else {
						gestorMinijuegos.setParametrosYAccion(gestorMinijuegos.accionEliminar, null, null, valores);
						dialog = new JProgressDialog(VentanaMinijuegos.this,
								idioma.leerProperty("minijuego.titulo_eliminando"),
								idioma.leerProperty("minijuego.mensaje_eliminando"));
						new Thread(gestorMinijuegos).start();
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
				new VentanaAdministrarJuegos();
				gestorMinijuegos.deleteObserver(VentanaMinijuegos.this);
				dispose();
			}
		});

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 2.0;
		c.gridwidth = 1;
		c.insets = new Insets(10, 10, 10, 10);
		sur.add(btnAnadir, c);
		sur.add(btnEliminar, c);
		c.gridy = 1;
		c.fill = GridBagConstraints.EAST;
		c.anchor = GridBagConstraints.WEST;
		sur.add(btnAtras, c);
	}

	private int getSelected(String value) {
		for (int i = 0; i < cbGrupo.getItemCount(); i++) {
			if (cbGrupo.getItemAt(i).equals(value)) {
				return i - 1;
			}
		}
		return -1;
	}

	@Override
	public void update(Observable o, Object arg) {
		dialog.dispose();
		setEnabled(true);
		int pos = (cbMinijuego == null) ? 0 : cbMinijuego.getSelectedIndex();
		if ((int) arg == gestorMinijuegos.accionCargar) {
			setSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width / 2,
					Toolkit.getDefaultToolkit().getScreenSize().height / 2));
			setLocationRelativeTo(null);

			contentPane = new JPanel();
			setContentPane(contentPane);

			setMinimumSize(new Dimension(800, 615));
			setLayout(new BorderLayout());

			norte = new JPanel();
			centro = new JPanel();
			sur = new JPanel();

			sur.setLayout(new GridBagLayout());
			centro.setLayout(new BorderLayout());
			centro.setBorder(new EmptyBorder(10, 10, 10, 10));
			sur.setBorder(new EmptyBorder(10, 10, 10, 10));

			getTituloMinijuego();
			getCombo(pos);
			getTable();
			getModificar();
			getBtnAnadir();

			add(norte, BorderLayout.NORTH);
			add(centro, BorderLayout.CENTER);
			add(sur, BorderLayout.SOUTH);
			setVisible(true);
			pack();
		} else if ((int) arg == gestorMinijuegos.accionCargarError) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("error_carga"),
					idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
			gestorMinijuegos.deleteObserver(this);
			new VentanaLogin();
			dispose();
		}else if ((int) arg == gestorMinijuegos.accionInsertar) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("minijuego.mensaje_anadido"),
					idioma.leerProperty("minijuego.titulo_anadido"), JOptionPane.INFORMATION_MESSAGE);
			centro.removeAll();
			getCombo(pos);
			getTable();
			getModificar();
			centro.updateUI();
		} else if ((int) arg == gestorMinijuegos.accionModificar) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("minijuego.mensaje_modificado"),
					idioma.leerProperty("minijuego.titulo_modificado"), JOptionPane.INFORMATION_MESSAGE);
			centro.removeAll();
			getCombo(pos);
			getTable();
			getModificar();
			centro.updateUI();
		} else if ((int) arg == gestorMinijuegos.accionEliminar) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("minijuego.mensaje_eliminado"),
					idioma.leerProperty("minijuego.titulo_eliminado"), JOptionPane.INFORMATION_MESSAGE);
			centro.removeAll();
			getCombo(pos);
			getTable();
			getModificar();
			centro.updateUI();
		} else if ((int) arg == gestorMinijuegos.accionInsertarError) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("minijuego.error_anadir"),
					idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
		} else if ((int) arg == gestorMinijuegos.accionModificarError) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("minijuego.error_modificar"),
					idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
		} else if ((int) arg == gestorMinijuegos.accionEliminarError) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("minijuego.error_eliminar"),
					idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
		}
	}
}