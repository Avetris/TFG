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
import modelo.GestorPermisos;
import modelo.IdiomaProperties;
import modelo.GestorSesion;

public class VentanaPermisos extends JFrame implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private IdiomaProperties idioma;
	private GestorPermisos gestorPermisos;

	private JPanel contentPane;
	private JPanel norte, sur, centro;

	JProgressDialog dialog;

	TableRowSorter<CustomTableModel> sorter;

	private CustomJTable table;

	JComboBox<String> cbNombres, cbSeleccion, cbNinos;

	JComboBox<Integer> cbMaximo,cbMinimo,cbPuntuacion;

	String[] nombreColumnas = null;

	private Dimension dimBoton = new Dimension(150, 30);

	/**
	 * Create the frame.
	 */
	public VentanaPermisos() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		idioma = IdiomaProperties.getIdiomaProperties();
		gestorPermisos = GestorPermisos.getGestorPermisos();

		if (GestorSesion.obtSesion().esLogopeda() == 0) {

			gestorPermisos.addObserver(this);

			dialog = new JProgressDialog(this, idioma.leerProperty("permisos.titulo_cargando"),
					idioma.leerProperty("permisos.mensaje_cargando"));
			gestorPermisos.setParametrosYAccion(gestorPermisos.accionCargar, null, null, null, null, null);
			new Thread(gestorPermisos).start();
		} else {
			new VentanaLogin();
			dispose();
		}
	}

	private void getTituloGrupo() {

		JLabel lblTitulo = new JLabel(idioma.leerProperty("permisos.ventana"));
		lblTitulo.setHorizontalAlignment(0);
		lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
		lblTitulo.setOpaque(true);
		lblTitulo.setForeground(Color.black);

		Border paddingBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		Border border = BorderFactory.createBevelBorder(0);
		lblTitulo.setBorder(BorderFactory.createCompoundBorder(border, paddingBorder));

		norte.add(lblTitulo);

	}

	public void getCombo(final int seleccion, final int pos) {
		JPanel form = new JPanel(new FlowLayout());
		JLabel lblSeleccion = new JLabel(idioma.leerProperty("permisos.seleccionar"));	
		lblSeleccion.setFont(new Font("Arial", Font.BOLD, 15));
		cbSeleccion = new JComboBox<>(new String[]{idioma.leerProperty("permisos.grupos"), idioma.leerProperty("permisos.minijuegos")});
		if(seleccion >= 0) cbSeleccion.setSelectedIndex(seleccion);
		JLabel lblNombre = new JLabel(idioma.leerProperty("permisos.nombres"));
		lblNombre.setFont(new Font("Arial", Font.BOLD, 15));
		cbNombres = new JComboBox<>(cbSeleccion.getSelectedIndex() == 1 ? gestorPermisos.obtenerNombresMinijuegos() : gestorPermisos.obtenerNombresGrupos());
		if (pos < cbNombres.getItemCount() && pos > -1) cbNombres.setSelectedIndex(pos);
		
		cbSeleccion.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {

				if (arg0.getStateChange() == ItemEvent.SELECTED) {
					centro.removeAll();
					getCombo(cbSeleccion.getSelectedIndex(), 0);
					if(cbSeleccion.getSelectedIndex() == 0)
						getTableGrupos();
					else
						getTableMinijuegos();
					centro.updateUI();
				}
			}
		});
		
		cbNombres.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {

				if (arg0.getStateChange() == ItemEvent.SELECTED) {
					centro.removeAll();
					getCombo(cbSeleccion.getSelectedIndex(), cbNombres.getSelectedIndex());
					if(cbSeleccion.getSelectedIndex() == 0)
						getTableGrupos();
					else
						getTableMinijuegos();					
					centro.updateUI();
				}
			}
		});

		lblNombre.setLabelFor(cbNombres);
		lblSeleccion.setLabelFor(cbSeleccion);
		form.add(lblSeleccion);
		form.add(cbSeleccion);
		form.add(lblNombre);
		form.add(cbNombres);
		centro.add(form, BorderLayout.NORTH);
	}

	public void getTableMinijuegos() {
		gestorPermisos.setMinijuegoActual(cbNombres.getSelectedIndex());
		nombreColumnas = new String[] { idioma.leerProperty("permisos.nino"), idioma.leerProperty("permisos.minimo"),
				idioma.leerProperty("permisos.maximo"), idioma.leerProperty("permisos.puntuacion_maxima"), idioma.leerProperty("borrar") };
		cbNinos = new JComboBox<>(gestorPermisos.obtenerNinos());
		Object[][] datos = gestorPermisos.obtenerPermisosMinijuegos();
		Object[][] aux = new Object[datos.length][5];
		Integer[] min = new Integer[(int)datos[0][4]-(int)datos[0][2]];
		for(int i = 0; i < min.length; i++) min[i] = (int)datos[0][2]+i;
		Integer[] max = new Integer[(int)datos[0][4]-(int)datos[0][2]];
		for(int i = 0; i < max.length; i++) max[i] = (int)datos[0][4]-i;
		if(min.length == 0) min = new Integer[]{0};
		if(max.length == 0) max = new Integer[]{0};
		cbMaximo = new JComboBox<>(max);
		cbMinimo = new JComboBox<>(min);
		cbPuntuacion = new JComboBox<>(new Integer[]{3,6,9,12,15,18,21});
		for(int i = 0; i < datos.length; i++){
			aux[i][0] = datos[i][0];
			aux[i][1] = datos[i][1];
			aux[i][2] = datos[i][3];
			aux[i][3] = datos[i][5];
			aux[i][4] = datos[i][6];
		}
		table = new CustomJTable(
				new CustomTableModel(aux, nombreColumnas, false, true));
		table.getColumnModel().getColumn(0).setMinWidth(80);
		table.todosNoSorteables();
		table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(cbNinos));
		table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(cbMinimo));
		table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(cbMaximo));
		table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(cbPuntuacion));
		table.setRowHeight(20);
		centro.add(new JScrollPane(table), BorderLayout.CENTER);
	}
	
	public void getTableGrupos() {
		gestorPermisos.setGrupoActual(cbNombres.getSelectedIndex());
		nombreColumnas = new String[] { idioma.leerProperty("permisos.nino"), idioma.leerProperty("borrar")};
		
		cbNinos = new JComboBox<>(gestorPermisos.obtenerNinos());
		table = new CustomJTable(
				new CustomTableModel(gestorPermisos.obtenerPermisosGrupo(), nombreColumnas, false, true));
		table.getColumnModel().getColumn(0).setMinWidth(80);
		table.todosNoSorteables();
		table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(cbNinos));
		table.setRowHeight(20);
		centro.add(new JScrollPane(table), BorderLayout.CENTER);
	}

	private void getBtnAnadir() {
		JButton btnAnadir = new JButton(idioma.leerProperty("permisos.anadir"));
		btnAnadir.setAlignmentX(CENTER_ALIGNMENT);
		btnAnadir.setMinimumSize(dimBoton);
		btnAnadir.setPreferredSize(dimBoton);
		btnAnadir.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));

		btnAnadir.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int nino = getSelected((table.getValueAt(table.getRowCount()-1, 0) == null) ? null : String.valueOf(table.getValueAt(table.getRowCount()-1, 0)));
				if(nino == -1){
					JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("permisos.error_nino"),
							idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
					return;
				}
				for(int i = 0; i < table.getRowCount()-1; i++){
					if(getSelected((table.getValueAt(i, 0) == null) ? null : String.valueOf(table.getValueAt(i, 0))) == nino){
						JOptionPane.showMessageDialog(contentPane,
								idioma.leerProperty("premisos.error_mismo_nino"), idioma.leerProperty("error"),
								JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				if(cbSeleccion.getSelectedIndex() == 1){
					int minimo= (int) table.getValueAt(table.getRowCount()-1, 1);
					int maximo = (int) table.getValueAt(table.getRowCount()-1, 2);
					int puntuacionMaxima = (int) table.getValueAt(table.getRowCount()-1, 3);
					gestorPermisos.setParametrosYAccion(gestorPermisos.accionInsertarMinijuego, nino, maximo, minimo, puntuacionMaxima, null);
					dialog = new JProgressDialog(VentanaPermisos.this, idioma.leerProperty("permisos.titulo_asignando_minijuego"),
							idioma.leerProperty("permisos.mensaje_asignando_minijuego"));
					new Thread(gestorPermisos).start();
				}else{
					gestorPermisos.setParametrosYAccion(gestorPermisos.accionInsertarGrupo, nino, null, null, null, null);
					dialog = new JProgressDialog(VentanaPermisos.this, idioma.leerProperty("permisos.titulo_asignando_grupo"),
							idioma.leerProperty("permisos.mensaje_asignando_grupo"));
					new Thread(gestorPermisos).start();
				}
			}
		});

		JButton btnEliminar = new JButton(idioma.leerProperty("permisos.eliminar"));
		btnEliminar.setAlignmentX(CENTER_ALIGNMENT);
		btnEliminar.setMinimumSize(dimBoton);
		btnEliminar.setPreferredSize(dimBoton);
		btnEliminar.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));

		btnEliminar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int seleccionado = JOptionPane.showOptionDialog(contentPane,
						idioma.leerProperty(cbSeleccion.getSelectedIndex() == 1 ? "permisos.mensaje_eliminar_minijuego" : "permisos.mensaje_eliminar_grupo"), idioma.leerProperty("permisos.titulo_eliminar"),
						JOptionPane.YES_NO_OPTION, JOptionPane.DEFAULT_OPTION, null,
						new String[] { idioma.leerProperty("aceptar"), idioma.leerProperty("cancelar") },
						idioma.leerProperty("aceptar"));
				if (seleccionado == 0) {
					List<Integer> valores = new ArrayList<>();
					for (int i = 0; i < table.getRowCount() - 1; i++) {
						if ((Boolean) table.getValueAt(i, table.getColumnCount()-1)) {
							valores.add(i);
						}
					}
					if (valores.size() == 0) {
						JOptionPane.showMessageDialog(contentPane,
								idioma.leerProperty("permisos.error_elementos_eliminar"), idioma.leerProperty("error"),
								JOptionPane.ERROR_MESSAGE);
					} else {
						if(cbSeleccion.getSelectedIndex() == 1){
							gestorPermisos.setParametrosYAccion(gestorPermisos.accionEliminarMinijuego, null, null, null, null, valores);
							dialog = new JProgressDialog(VentanaPermisos.this, idioma.leerProperty("permisos.titulo_eliminando_minijuego"),
									idioma.leerProperty("permisos.mensaje_eliminando_minijuego"));
							new Thread(gestorPermisos).start();
						}else{
							gestorPermisos.setParametrosYAccion(gestorPermisos.accionEliminarGrupo, null, null, null, null, valores);
							dialog = new JProgressDialog(VentanaPermisos.this, idioma.leerProperty("permisos.titulo_eliminando_grupo"),
									idioma.leerProperty("permisos.mensaje_eliminando_grupo"));
							new Thread(gestorPermisos).start();
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
				new VentanaAdministrarJuegos();
				gestorPermisos.deleteObserver(VentanaPermisos.this);
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
		if(value != null){
			for (int i = 0; i < cbNinos.getItemCount(); i++) {
				if (cbNinos.getItemAt(i).equals(value)) {
					return i - 1;
				}
			}
		}
		return -1;
	}

	@Override
	public void update(Observable o, Object arg) {
		dialog.dispose();
		setEnabled(true);
		int pos = (cbNombres == null) ? 0 : cbNombres.getSelectedIndex();
		if ((int) arg == gestorPermisos.accionCargar) {
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
			getCombo(0, pos);
			getTableGrupos();
			getBtnAnadir();

			add(norte, BorderLayout.NORTH);
			add(centro, BorderLayout.CENTER);
			add(sur, BorderLayout.SOUTH);
			setVisible(true);
			pack();
		}else if ((int) arg == gestorPermisos.accionCargarError) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("error_carga"),
					idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
			gestorPermisos.deleteObserver(this);
			new VentanaLogin();
		}else if ((int) arg == gestorPermisos.accionDatosError) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("permisos.error_datos"),
					idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
			gestorPermisos.deleteObserver(this);
			new VentanaAdministrarJuegos();		
		} else if ((int) arg == gestorPermisos.accionInsertarGrupo) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("permisos.mensaje_anadido_grupo"),
					idioma.leerProperty("permisos.titulo_anadido_grupo"), JOptionPane.INFORMATION_MESSAGE);
			centro.removeAll();
			getCombo(0, pos);
			getTableGrupos();
			centro.updateUI();			
		} else if ((int) arg == gestorPermisos.accionInsertarMinijuego) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("permisos.mensaje_anadido_minijuego"),
					idioma.leerProperty("permisos.titulo_anadido_minijuego"), JOptionPane.INFORMATION_MESSAGE);
			centro.removeAll();
			getCombo(1, pos);
			getTableMinijuegos();
			centro.updateUI();
		}else if ((int) arg == gestorPermisos.accionEliminarGrupo) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("permisos.mensaje_eliminado_grupo"),
					idioma.leerProperty("permisos.titulo_eliminado_grupo"), JOptionPane.INFORMATION_MESSAGE);
			centro.removeAll();
			getCombo(0, pos);
			getTableGrupos();
			centro.updateUI();
		}else if ((int) arg == gestorPermisos.accionEliminarMinijuego) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("permisos.mensaje_eliminado_minijuego"),
					idioma.leerProperty("permisos.titulo_eliminado_minijuego"), JOptionPane.INFORMATION_MESSAGE);
			centro.removeAll();
			getCombo(1, pos);
			getTableMinijuegos();
			centro.updateUI();
		} else if ((int) arg == gestorPermisos.accionInsertarGrupoError) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("permisos.error_insertar_grupo"),
					idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
		} else if ((int) arg == gestorPermisos.accionEliminarGrupoError) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("permisos.error_eliminar_grupo"),
					idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
		} else if ((int) arg == gestorPermisos.accionInsertarMinijuegoError) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("permisos.error_insertar_minijuego"),
					idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
		}else if ((int) arg == gestorPermisos.accionEliminarMinijuegoError) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("permisos.error_eliminar_minijuego"),
					idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
		}
	}
}