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
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import modelo.GestorContenido;
import modelo.IdiomaProperties;
import modelo.GestorSesion;

public class VentanaContenido extends JFrame implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private IdiomaProperties idioma;
	private GestorContenido gestorContenido;

	private JPanel contentPane;
	private JPanel norte, sur, centro;

	JTextField filterText;

	JProgressDialog dialog;

	TableRowSorter<CustomTableModel> sorter;

	private CustomJTable table;

	String[] nombreColumnas = null;

	private Dimension dimAreaTexto = new Dimension(200, 25);
	private Dimension dimBoton = new Dimension(150, 30);

	/**
	 * Create the frame.
	 */
	public VentanaContenido() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		idioma = IdiomaProperties.getIdiomaProperties();
		gestorContenido = GestorContenido.getGestorContenido();

		if (GestorSesion.obtSesion().esLogopeda() == 0) {
			nombreColumnas = new String[] { idioma.leerProperty("contenido.id"),
					idioma.leerProperty("contenido.nombre"), idioma.leerProperty("contenido.castellano"),
					idioma.leerProperty("contenido.euskera"), idioma.leerProperty("contenido.imagen"),
					idioma.leerProperty("modificar"), idioma.leerProperty("borrar") };

			gestorContenido.addObserver(this);

			dialog = new JProgressDialog(this, idioma.leerProperty("contenido.titulo_cargando"),
					idioma.leerProperty("contenido.mensaje_cargando"));
			gestorContenido.setParametrosYAccion(gestorContenido.accionCargar, null);
			new Thread(gestorContenido).start();
		} else {
			new VentanaLogin();
			dispose();
		}

	}

	private void getTituloContenido() {

		JLabel lblTitulo = new JLabel(idioma.leerProperty("contenido.ventana"));
		lblTitulo.setHorizontalAlignment(0);
		lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
		lblTitulo.setOpaque(true);
		lblTitulo.setForeground(Color.black);

		Border paddingBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		Border border = BorderFactory.createBevelBorder(0);
		lblTitulo.setBorder(BorderFactory.createCompoundBorder(border, paddingBorder));

		norte.add(lblTitulo);

	}

	public void getSearch() {

		JPanel form = new JPanel(new FlowLayout());
		JLabel lblNombre = new JLabel(idioma.leerProperty("buscar"));
		lblNombre.setFont(new Font("Arial", Font.BOLD, 15));
		filterText = new JTextField();
		filterText.setFont(new Font("Serif", Font.ITALIC, 12));
		filterText.setPreferredSize(dimAreaTexto);
		// Whenever filterText changes, invoke newFilter.
		filterText.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				table.newFilter(filterText.getText(), new int[] { 1, 2, 3 });
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				table.newFilter(filterText.getText(), new int[] { 1, 2, 3 });
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				table.newFilter(filterText.getText(), new int[] { 1, 2, 3 });
			}
		});
		lblNombre.setLabelFor(filterText);
		form.add(lblNombre);
		form.add(filterText);
		form.add(new JLabel(""));
		centro.add(form, BorderLayout.NORTH);
	}

	public void getTable() {
		table = new CustomJTable(new CustomTableModel(gestorContenido.obtenerContenido(), nombreColumnas, true, false));
		table.getColumnModel().getColumn(1).setMinWidth(125);
		table.modificarYBorrado();
		table.setNoSorteable(4);
		table.getColumnModel().getColumn(4).setCellEditor(new FileChooserCellEditor());
		table.getColumnModel().getColumn(4).setCellRenderer(new FileChooserCellEditor());
		table.getColumnModel().getColumn(4).setMaxWidth(55);
		table.getColumnModel().getColumn(4).setMinWidth(55);
		table.getColumnModel().getColumn(1).setMinWidth(130);
		table.getColumnModel().getColumn(2).setMinWidth(90);
		table.getColumnModel().getColumn(3).setMinWidth(70);
		table.limitarCaracteres(1, 40);
		table.limitarCaracteres(2, 200);
		table.limitarCaracteres(3, 200);
		table.setRowHeight(60);
		table.primerNumero();
		centro.add(new JScrollPane(table), BorderLayout.CENTER);
	}

	private void getBtnAnadir() {
		JButton btnAnadir = new JButton(idioma.leerProperty("contenido.anadir"));
		btnAnadir.setAlignmentX(CENTER_ALIGNMENT);
		btnAnadir.setMinimumSize(dimBoton);
		btnAnadir.setPreferredSize(dimBoton);
		btnAnadir.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));

		btnAnadir.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String nombre = (table.getValueAt(table.getRowCount() - 1, 1) == null) ? null
						: String.valueOf(table.getValueAt(table.getRowCount() - 1, 1));
				String cast = (table.getValueAt(table.getRowCount() - 1, 2) == null) ? null
						: String.valueOf(table.getValueAt(table.getRowCount() - 1, 2));
				String eusk = (table.getValueAt(table.getRowCount() - 1, 3) == null) ? null
						: String.valueOf(table.getValueAt(table.getRowCount() - 1, 3));
				File img = (table.getValueAt(table.getRowCount() - 1, 4) == null) ? null
						: ((ImagePanel) table.getValueAt(table.getRowCount() - 1, 4)).getImageFile();
				if (nombre == null || nombre.trim().length() == 0) {
					JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("contenido.error_sin_nombre"),
							idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
				} else if ((cast == null || cast.trim().length() == 0) && (eusk == null || eusk.trim().length() == 0)) {
					JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("contenido.error_algun_campo"),
							idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
				} else{
					Boolean b = gestorContenido.existeNombre(nombre);
					if(b == null) return;
					if (b) {
					JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("contenido.error_nombre_existe"),
							idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
					} else {
						List<Object[]> l = new ArrayList<>();
						l.add(new Object[] { nombre, cast, eusk, img });
						gestorContenido.setParametrosYAccion(gestorContenido.accionInsertar, l);
						dialog = new JProgressDialog(VentanaContenido.this, idioma.leerProperty("contenido.titulo_creando"),
								idioma.leerProperty("contenido.mensaje_creando"));
						new Thread(gestorContenido).start();
					}
				}
			}
		});

		JButton btnModificar = new JButton(idioma.leerProperty("contenido.modificar"));
		btnModificar.setAlignmentX(CENTER_ALIGNMENT);
		btnModificar.setMinimumSize(dimBoton);
		btnModificar.setPreferredSize(dimBoton);
		btnModificar.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));

		btnModificar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				List<Object[]> valores = new ArrayList<Object[]>();
				for (int i = 0; i < table.getRowCount() - 1; i++) {
					if ((Boolean) table.getValueAt(i, 5)) {
						Object[] ob = new Object[5];
						ob[0] = table.getValueAt(i, 0);
						for (int j = 1; j < 4; j++){
							ob[j] = (table.getValueAt(i, j) == null
									|| String.valueOf(table.getValueAt(i, j)).trim().length() == 0) ? null
											: String.valueOf(table.getValueAt(i, j));
						}
						ob[4] = ((ImagePanel) table.getValueAt(i, 4)).getImageFile();
						valores.add(ob);						
					}
				}
				if (valores.size() == 0) {
					JOptionPane.showMessageDialog(contentPane,
							idioma.leerProperty("contenido.error_elementos_modificar"), idioma.leerProperty("error"),
							JOptionPane.ERROR_MESSAGE);
				} else {
					for(int i = 0; i < valores.size(); i++){
						String cast = valores.get(i)[2] == null ? null : String.valueOf(valores.get(i)[2]);
						String eusk = valores.get(i)[3] == null ? null : String.valueOf(valores.get(i)[3]);
						if ((cast == null || cast.trim().length() == 0) && (eusk == null || eusk.trim().length() == 0)) {
							JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("contenido.error_algun_campo"),
									idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
					gestorContenido.setParametrosYAccion(gestorContenido.accionModificar, valores);
					dialog = new JProgressDialog(VentanaContenido.this,
							idioma.leerProperty("contenido.titulo_modificando"),
							idioma.leerProperty("contenido.mensaje_modificando"));
					new Thread(gestorContenido).start();
				}
			}
		});

		JButton btnEliminar = new JButton(idioma.leerProperty("contenido.eliminar"));
		btnEliminar.setAlignmentX(CENTER_ALIGNMENT);
		btnEliminar.setMinimumSize(dimBoton);
		btnEliminar.setPreferredSize(dimBoton);
		btnEliminar.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));

		btnEliminar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int seleccionado = JOptionPane.showOptionDialog(contentPane,
						idioma.leerProperty("contenido.mensaje_eliminar"),
						idioma.leerProperty("contenido.titulo_eliminar"), JOptionPane.YES_NO_OPTION,
						JOptionPane.DEFAULT_OPTION, null,
						new String[] { idioma.leerProperty("aceptar"), idioma.leerProperty("cancelar") },
						idioma.leerProperty("aceptar"));
				if (seleccionado == 0) {
					List<Object[]> valores = new ArrayList<>();
					for (int i = 0; i < table.getRowCount() - 1; i++) {
						if ((Boolean) table.getValueAt(i, 6)) {
							valores.add(new Object[] { table.getValueAt(i, 0) });
						}
					}
					if (valores.size() == 0) {
						JOptionPane.showMessageDialog(contentPane,
								idioma.leerProperty("contenido.error_elementos_eliminar"), idioma.leerProperty("error"),
								JOptionPane.ERROR_MESSAGE);
					} else {
						gestorContenido.setParametrosYAccion(gestorContenido.accionEliminar, valores);
						dialog = new JProgressDialog(VentanaContenido.this,
								idioma.leerProperty("contenido.titulo_eliminando"),
								idioma.leerProperty("contenido.mensaje_eliminando"));
						new Thread(gestorContenido).start();

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
				gestorContenido.deleteObserver(VentanaContenido.this);
				dispose();
			}
		});

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 2.0;
		c.gridwidth = 1;
		c.insets = new Insets(10, 10, 10, 10);
		sur.add(btnAnadir, c);
		sur.add(btnModificar, c);
		sur.add(btnEliminar, c);
		c.gridy = 1;
		c.fill = GridBagConstraints.EAST;
		c.anchor = GridBagConstraints.WEST;
		sur.add(btnAtras, c);
	}

	@Override
	public void update(Observable o, Object arg) {
		dialog.dispose();
		setEnabled(true);
		if ((int) arg == gestorContenido.accionCargar) {
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

			getTituloContenido();
			getTable();
			getSearch();
			getBtnAnadir();

			add(norte, BorderLayout.NORTH);
			add(centro, BorderLayout.CENTER);
			add(sur, BorderLayout.SOUTH);
			setVisible(true);
			pack();
		} else if ((int) arg == gestorContenido.accionCargarError) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("error_carga"), idioma.leerProperty("error"),
					JOptionPane.ERROR_MESSAGE);
			gestorContenido.deleteObserver(this);
			new VentanaLogin();
			dispose();
		} else if ((int) arg == gestorContenido.accionInsertar) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("contenido.mensaje_anadido"),
					idioma.leerProperty("contenido.titulo_anadido"), JOptionPane.INFORMATION_MESSAGE);
			centro.removeAll();
			getTable();
			getSearch();
			centro.updateUI();
		} else if ((int) arg == gestorContenido.accionModificar) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("contenido.mensaje_modificado"),
					idioma.leerProperty("contenido.titulo_modificado"), JOptionPane.INFORMATION_MESSAGE);
			centro.removeAll();
			getTable();
			getSearch();
			centro.updateUI();
		} else if ((int) arg == gestorContenido.accionEliminar) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("contenido.mensaje_eliminado"),
					idioma.leerProperty("contenido.titulo_eliminado"), JOptionPane.INFORMATION_MESSAGE);
			centro.removeAll();
			getTable();
			getSearch();
			centro.updateUI();
		} else if ((int) arg == gestorContenido.accionInsertarError) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("contenido.error_anadir"),
					idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
		} else if ((int) arg == gestorContenido.accionModificarError) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("contenido.error_modificar"),
					idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
		} else if ((int) arg == gestorContenido.accionEliminarError) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("contenido.error_eliminar"),
					idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
		}
	}

	class FileChooserCellEditor extends DefaultCellEditor implements TableCellRenderer, TableCellEditor {

		private static final long serialVersionUID = 1L;
		/** Number of clicks to start editing */
		private static final int CLICK_COUNT_TO_START = 2;
		/** Editor component */
		private ImagePanel panel;
		/** File chooser */
		private JFileChooser fileChooser;
		/** Selected file */
		private File file = null;

		ImageIcon imagen;

		/**
		 * Constructor.
		 */
		public FileChooserCellEditor() {
			super(new JTextField());
			setClickCountToStart(CLICK_COUNT_TO_START);

			// Using a JButton as the editor component
			panel = new ImagePanel(null);

			// Dialog which will do the actual editing
			fileChooser = new JFileChooser();
			fileChooser.addChoosableFileFilter(new FileFilter() {
				@Override
				public String getDescription() {
					return null;
				}

				@Override
				public boolean accept(File f) {
					if (f.isDirectory())
						return true;
					String name = f.getName().toLowerCase();
					if (name != null && name.length() > 2) {
						if (name.endsWith("jpg") || name.endsWith("png")) {
							return true;
						} else {
							return false;
						}

					} else {
						return false;
					}
				}
			});
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.setAccessory(new ImagePreview(fileChooser));
		}

		@Override
		public Object getCellEditorValue() {
			return (file == null) ? panel.getImage() : file;
		}

		@Override
		public Component getTableCellEditorComponent(final JTable table, Object value, boolean isSelected,
				final int row, final int column) {
			ImageIcon img = ((ImagePanel) table.getValueAt(row, column)).getImage();
			if (fileChooser.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION) {
				String f = fileChooser.getSelectedFile().getAbsolutePath();
				file = new File(f);
				panel.setImageFile(file);
				table.setValueAt(file, row, column);
				fireEditingStopped();
			} else {
				panel.setImage(img);
				table.setValueAt(img, row, column);
			}
			return panel;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean arg2, boolean isSelected,
				int row, int column) {
			if (value == null) {
				panel.setImageFile(file);
			} else {
				if (value.getClass() == File.class) {
					panel.setImageFile((File) value);
				} else {
					panel = (ImagePanel) value;
				}
			}
			return panel;
		}
	}
}