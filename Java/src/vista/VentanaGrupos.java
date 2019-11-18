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
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableRowSorter;
import modelo.GestorGrupos;
import modelo.IdiomaProperties;
import modelo.GestorSesion;

public class VentanaGrupos extends JFrame implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private IdiomaProperties idioma;
	private GestorGrupos gestorGrupos;

	private JPanel contentPane;
	private JPanel norte, sur, centro;

	JProgressDialog dialog;

	TableRowSorter<CustomTableModel> sorter;

	private CustomJTable table;

	JComboBox<String> cbGrupo;
	JComboBox<String> cbContenido;

	String[] nombreColumnas = null;

	private Dimension dimBoton = new Dimension(150, 30);

	/**
	 * Create the frame.
	 */
	public VentanaGrupos() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		idioma = IdiomaProperties.getIdiomaProperties();
		gestorGrupos = GestorGrupos.getGestorGrupos();

		if (GestorSesion.obtSesion().esLogopeda() == 0) {
			nombreColumnas = new String[] { idioma.leerProperty("grupo.id"), idioma.leerProperty("grupo.nombre"),
					idioma.leerProperty("grupo.contenidoSolucion"), idioma.leerProperty("grupo.contenidoOpcion"),
					idioma.leerProperty("borrar") };

			gestorGrupos.addObserver(this);

			dialog = new JProgressDialog(this, idioma.leerProperty("grupo.titulo_cargando"),
					idioma.leerProperty("grupo.mensaje_cargando"));
			gestorGrupos.setParametrosYAccion(gestorGrupos.accionCargar, null, null, null);
			new Thread(gestorGrupos).start();
		} else {
			new VentanaLogin();
			dispose();
		}
	}

	private void getTituloGrupo() {

		JLabel lblTitulo = new JLabel(idioma.leerProperty("grupo.ventana"));
		lblTitulo.setHorizontalAlignment(0);
		lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
		lblTitulo.setOpaque(true);
		lblTitulo.setForeground(Color.black);

		Border paddingBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		Border border = BorderFactory.createBevelBorder(0);
		lblTitulo.setBorder(BorderFactory.createCompoundBorder(border, paddingBorder));

		norte.add(lblTitulo);

	}

	public void getCombo(final int pos) {

		JPanel form = new JPanel(new FlowLayout());
		JLabel lblNombre = new JLabel(idioma.leerProperty("grupo.seleccionar"));
		lblNombre.setFont(new Font("Arial", Font.BOLD, 15));
		cbGrupo = new JComboBox<>(gestorGrupos.obtenerNombres());
		if (pos < cbGrupo.getItemCount()) {
			cbGrupo.setSelectedIndex(pos);
		}
		cbGrupo.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {

				if (arg0.getStateChange() == ItemEvent.SELECTED) {
					centro.removeAll();
					getCombo(cbGrupo.getSelectedIndex());
					getTable();
					centro.updateUI();
				}
			}
		});

		lblNombre.setLabelFor(cbGrupo);
		form.add(lblNombre);
		form.add(cbGrupo);
		form.add(new JLabel(""));
		centro.add(form, BorderLayout.NORTH);
	}

	public void getTable() {
		cbContenido = new JComboBox<>(gestorGrupos.obtenerContenido());
		table = new CustomJTable(
				new CustomTableModel(gestorGrupos.obtenerGrupo(cbGrupo.getSelectedIndex()), nombreColumnas, false, false));
		table.getColumnModel().getColumn(1).setMinWidth(80);
		table.todosNoSorteables();
		table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(cbContenido));
		table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(cbContenido));
		table.limitarCaracteres(1, 40);
		table.setRowHeight(20);
        table.primerNumero();
		centro.add(new JScrollPane(table), BorderLayout.CENTER);
	}

	private void getBtnAnadir() {
		JButton btnAnadir = new JButton(idioma.leerProperty("grupo.anadir"));
		btnAnadir.setAlignmentX(CENTER_ALIGNMENT);
		btnAnadir.setMinimumSize(dimBoton);
		btnAnadir.setPreferredSize(dimBoton);
		btnAnadir.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));

		btnAnadir.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				List<Integer> total = new ArrayList<Integer>();
				String nombre  = (table.getValueAt(table.getRowCount()-1, 1) == null) ? null : String.valueOf(table.getValueAt(table.getRowCount()-1, 1));
				total.add(getSelected((table.getValueAt(table.getRowCount()-1, 2) == null) ? " ": String.valueOf(table.getValueAt(table.getRowCount()-1, 2))));
				total.add(getSelected((table.getValueAt(table.getRowCount()-1, 3) == null) ? " ": String.valueOf(table.getValueAt(table.getRowCount()-1, 3))));
				if(table.getRowCount() == 1){
					if (nombre == null || nombre.trim().length() == 0) {
						JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("grupo.error_nombre"),
								idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
						return;
					}else if (total.get(0) == -1) {
						JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("grupo.error_solucion"),
								idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
						return;
					}else{
						Boolean b = gestorGrupos.existeNombre(nombre);
						if(b == null){return;
						}else if(b){
							JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("grupo.error_nombre_existe"),
									idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
							return;		
						}
					}
				}
				if (total.get(1) == -1) {
					JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("grupo.error_opcion"),
							idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
					return;
				} else{
					
					for(int i = 0; i < table.getRowCount()-1; i++){
						int row = getSelected((table.getValueAt(i, 3) == null) ? " ": String.valueOf(table.getValueAt(i, 3)));
						if(row == total.get(1)){
							JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("grupo.error_repetir"),
									idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
							return;							
						}					
					}
					int seleccionado = JOptionPane.showOptionDialog(contentPane,
							idioma.leerProperty("grupo.mensaje_anadir_todos"),
							idioma.leerProperty("grupo.titulo_anadir_todos"), JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.DEFAULT_OPTION, null,
							new String[] { idioma.leerProperty("aceptar"), idioma.leerProperty("cancelar") },
							idioma.leerProperty("cancelar"));
					gestorGrupos.setParametrosYAccion(gestorGrupos.accionInsertar, nombre, total, seleccionado == 0);
					dialog = new JProgressDialog(VentanaGrupos.this, idioma.leerProperty("grupo.titulo_creando"),
							idioma.leerProperty("grupo.mensaje_creando"));
					new Thread(gestorGrupos).start();

				}
			}
		});

		JButton btnEliminar = new JButton(idioma.leerProperty("grupo.eliminar"));
		btnEliminar.setAlignmentX(CENTER_ALIGNMENT);
		btnEliminar.setMinimumSize(dimBoton);
		btnEliminar.setPreferredSize(dimBoton);
		btnEliminar.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));

		btnEliminar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int seleccionado = JOptionPane.showOptionDialog(contentPane,
						idioma.leerProperty("grupo.mensaje_eliminar"), idioma.leerProperty("grupo.titulo_eliminar"),
						JOptionPane.YES_NO_OPTION, JOptionPane.DEFAULT_OPTION, null,
						new String[] { idioma.leerProperty("aceptar"), idioma.leerProperty("cancelar") },
						idioma.leerProperty("aceptar"));
				if (seleccionado == 0) {
					List<Integer> valores = new ArrayList<>();
					for (int i = 0; i < table.getRowCount() - 1; i++) {
						if ((Boolean) table.getValueAt(i, 4)) {
							valores.add(i);
						}
					}
					if (valores.size() == 0) {
						JOptionPane.showMessageDialog(contentPane,
								idioma.leerProperty("grupo.error_elementos_eliminar"), idioma.leerProperty("error"),
								JOptionPane.ERROR_MESSAGE);
					} else {
						gestorGrupos.setParametrosYAccion(gestorGrupos.accionEliminar, null, valores, null);
						dialog = new JProgressDialog(VentanaGrupos.this, idioma.leerProperty("grupo.titulo_eliminando"),
								idioma.leerProperty("grupo.mensaje_eliminando"));
						new Thread(gestorGrupos).start();
						setEnabled(false);
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
				gestorGrupos.deleteObserver(VentanaGrupos.this);
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

	public int getSelected(String value) {
		for (int i = 0; i < cbContenido.getItemCount(); i++) {
			if (cbContenido.getItemAt(i).equals(value)) {
				return i - 1;
			}
		}
		return -1;
	}

	@Override
	public void update(Observable o, Object arg) {
		dialog.dispose();
		setEnabled(true);
		int pos = (cbGrupo == null) ? 0 : cbGrupo.getSelectedIndex();
		if ((int) arg == gestorGrupos.accionCargar) {
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

			getTituloGrupo();
			getCombo(pos);
			getTable();
			getBtnAnadir();

			add(norte, BorderLayout.NORTH);
			add(centro, BorderLayout.CENTER);
			add(sur, BorderLayout.SOUTH);
			setVisible(true);
			pack();
		} else if ((int) arg == gestorGrupos.accionCargarError) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("error_carga"),
					idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
			gestorGrupos.deleteObserver(this);
			new VentanaLogin();
			dispose();
		} else if ((int) arg == gestorGrupos.accionInsertar) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("grupo.mensaje_anadido"),
					idioma.leerProperty("grupo.titulo_anadido"), JOptionPane.INFORMATION_MESSAGE);
			centro.removeAll();
			getCombo(pos);
			getTable();
			centro.updateUI();
		} else if ((int) arg == gestorGrupos.accionEliminar) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("grupo.mensaje_eliminado"),
					idioma.leerProperty("grupo.titulo_eliminado"), JOptionPane.INFORMATION_MESSAGE);
			centro.removeAll();
			getCombo(pos);
			getTable();
			centro.updateUI();
		} else if ((int) arg == gestorGrupos.accionInsertarError) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("grupo.error_anadir"),
					idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
		} else if ((int) arg == gestorGrupos.accionEliminarError) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("grupo.error_eliminar"),
					idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
		} else if ((int) arg == gestorGrupos.accionEliminarErrorMinijuego) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("grupo.error_eliminar_minijuego"),
					idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
		} else if ((int) arg == gestorGrupos.accionEliminarErrorUsuario) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("grupo.error_eliminar_usuario"),
					idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
		}
	}
}