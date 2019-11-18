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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import com.sun.javafx.geom.Rectangle;

import jdk.nashorn.internal.runtime.regexp.joni.Config;
import modelo.FiltroTexto;
import modelo.GestorNinos;
import modelo.IdiomaProperties;
import sun.java2d.SunGraphicsEnvironment;
import modelo.GestorSesion;

public class VentanaNino extends JFrame implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private IdiomaProperties idioma;
	private GestorNinos gestorNinos;

	private boolean esLogopeda = false;

	private JPanel contentPane;
	private JPanel norte, centro, sur;

	private JPanel centroSur;

	JProgressDialog dialog;

	private JComboBox<String> spNinos, spVer;
	private Dimension dimTextFieldPequeno = new Dimension(150, 30);
	private Dimension dimTextField = new Dimension(300, 30);
	private Dimension dimTextField2 = new Dimension(1100, 30);
	private Dimension dimTextArea = new Dimension(80, 70);
	private Dimension dimTextArea2 = new Dimension(80, 40);
	private Dimension dimTextAreaGrande = new Dimension(80, 200);
	
	private JComponent[] txtInformacion = new JComponent[70];

	String[] datos;

	/**
	 * Create the frame.
	 */
	public VentanaNino() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		idioma = IdiomaProperties.getIdiomaProperties();
		gestorNinos = GestorNinos.getGestorNinos();

		int log = GestorSesion.obtSesion().esLogopeda();
		if (log == 0 || log == 1) {
			esLogopeda = log == 0;

			gestorNinos.addObserver(this);
			dialog = new JProgressDialog(this, idioma.leerProperty("nino.titulo_cargando"),
					idioma.leerProperty("nino.mensaje_cargando"));
			gestorNinos.setDatos(gestorNinos.accionCargar, null);
			new Thread(gestorNinos).start();			
		} else {
			new VentanaLogin();
			dispose();
		}
	}

	private void getTituloSesion() {

		JLabel lblTitulo = new JLabel(idioma.leerProperty("sesion.ventana"));
		lblTitulo.setHorizontalAlignment(0);
		lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
		lblTitulo.setOpaque(true);
		lblTitulo.setForeground(Color.black);

		Border paddingBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		Border border = BorderFactory.createBevelBorder(0);
		lblTitulo.setBorder(BorderFactory.createCompoundBorder(border, paddingBorder));

		norte.add(lblTitulo);
	}

	private void getSeleccionNino(int posNino, int posVer) {
		if(((BorderLayout) centro.getLayout()).getLayoutComponent(BorderLayout.NORTH) != null)
			centro.remove(((BorderLayout) centro.getLayout()).getLayoutComponent(BorderLayout.NORTH));
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		JLabel lblNino = new JLabel(idioma.leerProperty("nino.nino"));
		JLabel lblVer = new JLabel(idioma.leerProperty("nino.ver"));
		if (esLogopeda) {
			spNinos = new JComboBox<>(gestorNinos.obtenerListaNombreApellidos());
			panel.add(lblNino);
			panel.add(spNinos);
			spVer = new JComboBox<>(new String[] { idioma.leerProperty("nino.informacion"),
					idioma.leerProperty("nino.sesiones"), idioma.leerProperty("nino.minijuegos") });
			if(spNinos.getSelectedIndex() == 0){
				spVer.setSelectedIndex(0);
				spVer.setEnabled(false);
			}
			spNinos.addItemListener(new ItemListener() {				
				@Override
				public void itemStateChanged(ItemEvent arg0) {

					if (arg0.getStateChange() == ItemEvent.SELECTED) {
						if(spNinos.getSelectedIndex() == 0){
							spVer.setSelectedIndex(0);
							spVer.setEnabled(false);
							gestorNinos.setNino(spNinos.getSelectedIndex()-1);
							getInformacion();							
						}else{
							spVer.setEnabled(true);
							switch (spVer.getSelectedIndex()){
								case 0:
									gestorNinos.setNino(spNinos.getSelectedIndex()-1);
									getInformacion();
									break;
								case 1:
									getSesiones();	
									break;
								case 2:
									getHistorial();
								break;
							default:
								
							}
						}
					}
				}
			});
			spVer.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent arg0) {

					if (arg0.getStateChange() == ItemEvent.SELECTED) {
						switch (spVer.getSelectedIndex()){
							case 0:
								gestorNinos.setNino(spNinos.getSelectedIndex()-1);
								getInformacion();
								getBtnAtras();
								break;
							case 1:
								getSesiones();
								break;
							case 2:
								getHistorial();
							break;
							default:
							
						}					
					}
				}
			});
			panel.add(lblVer);
			panel.add(spVer);
		} else {
			spNinos = new JComboBox<>();
			spNinos.addItem(gestorNinos.getNombre());
			spNinos.setEnabled(false);			
			panel.add(lblNino);
			panel.add(spNinos);
			spVer = new JComboBox<>(
					new String[] { idioma.leerProperty("nino.sesiones")});
			spVer.setEnabled(false);				
			panel.add(lblVer);
			panel.add(spVer);
		}
		centro.add(panel, BorderLayout.NORTH);
	}
	
	private void getInformacion(){
		if(((BorderLayout) centro.getLayout()).getLayoutComponent(BorderLayout.CENTER) != null)
			centro.remove(((BorderLayout) centro.getLayout()).getLayoutComponent(BorderLayout.CENTER));

		if(((BorderLayout) centro.getLayout()).getLayoutComponent(BorderLayout.SOUTH) != null)
			centro.remove(((BorderLayout) centro.getLayout()).getLayoutComponent(BorderLayout.SOUTH));
		centroSur.removeAll();
		datos = gestorNinos.obtenerInformacion();
		getInfoNino();
		getAnamnesis();
		getAutonomia();
		getSocializacion();
		getLenguaje();
		getBtnNino();		

		txtInformacion[0].addKeyListener(FiltroTexto.textInputLimiter(txtInformacion[0], 5));
		txtInformacion[1].addKeyListener(FiltroTexto.textInputLimiter(txtInformacion[1], 20));
		txtInformacion[2].addKeyListener(FiltroTexto.textInputLimiter(txtInformacion[2], 30));
		txtInformacion[5].addKeyListener(FiltroTexto.textInputLimiter(txtInformacion[5], 9));
		txtInformacion[6].addKeyListener(FiltroTexto.textInputLimiter(txtInformacion[6], 9));
		txtInformacion[7].addKeyListener(FiltroTexto.textInputLimiter(txtInformacion[7], 30));
		txtInformacion[8].addKeyListener(FiltroTexto.textInputLimiter(txtInformacion[8], 30));
		txtInformacion[9].addKeyListener(FiltroTexto.textInputLimiter(txtInformacion[9], 100));
		txtInformacion[10].addKeyListener(FiltroTexto.textInputLimiter(txtInformacion[10], 100));
		txtInformacion[11].addKeyListener(FiltroTexto.textInputLimiter(txtInformacion[11], 100));
		txtInformacion[12].addKeyListener(FiltroTexto.textInputLimiter(txtInformacion[12], 100));
		for(JComponent component : txtInformacion) component.addKeyListener(FiltroTexto.textInputLimiter(component, 500));
		
		JScrollPane scroll = new JScrollPane(centroSur, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		centro.add(scroll, BorderLayout.CENTER);

		centroSur.updateUI();
		centro.updateUI();
	}
	
	private void getSesiones(){
		if(((BorderLayout) centro.getLayout()).getLayoutComponent(BorderLayout.CENTER) != null)
			centro.remove(((BorderLayout) centro.getLayout()).getLayoutComponent(BorderLayout.CENTER));

		if(((BorderLayout) centro.getLayout()).getLayoutComponent(BorderLayout.SOUTH) != null)
			centro.remove(((BorderLayout) centro.getLayout()).getLayoutComponent(BorderLayout.SOUTH));
		centroSur.removeAll();
		String[][] datosSesion = gestorNinos.obtenerSesiones();
		if(esLogopeda){
			JPanel panel = crearCampo(new String[]{"","","",""});				
			GridBagConstraints c = new GridBagConstraints();
			c.gridy = 0;
			c.gridx = 0;
			c.gridwidth = 2;
			c.weightx = 1;
			c.insets = new Insets(10, 10, 10, 10);
			c.fill= GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.CENTER;
			centroSur.add(panel,c);
		}
			for(int i = 0; i<datosSesion.length; i++){
			if(esLogopeda || datosSesion[i][2].equals("S")){
				JPanel panel = crearCampo(datosSesion[i]);	
				if(panel != null){
					GridBagConstraints c = new GridBagConstraints();
					c.gridy = esLogopeda ? i+1 : i;
					c.gridx = 0;
					c.gridwidth = 2;
					c.weightx = 1;
					c.insets = new Insets(10, 10, 10, 10);
					c.fill= GridBagConstraints.HORIZONTAL;
					c.anchor = GridBagConstraints.CENTER;
					centroSur.add(panel,c);
				}
			}			
		}	
		if(datosSesion == null || datosSesion.length == 0 && !esLogopeda){
			centroSur.add(new JLabel(idioma.leerProperty("nino.sin_sesiones")));
		}
		JScrollPane scroll = new JScrollPane(centroSur, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		centro.add(scroll, BorderLayout.CENTER);

		centroSur.updateUI();
		centro.updateUI();
	}
	
	private void getHistorial(){
		if(((BorderLayout) centro.getLayout()).getLayoutComponent(BorderLayout.CENTER) != null)
			centro.remove(((BorderLayout) centro.getLayout()).getLayoutComponent(BorderLayout.CENTER));

		if(((BorderLayout) centro.getLayout()).getLayoutComponent(BorderLayout.SOUTH) != null)
			centro.remove(((BorderLayout) centro.getLayout()).getLayoutComponent(BorderLayout.SOUTH));
		centroSur.removeAll();
		String[][] datosHistorial = gestorNinos.obtenerHistorial();		
		String[] textos = new String[]{idioma.leerProperty("nino.minijuego"),
										idioma.leerProperty("nino.fecha_jugado"),
										idioma.leerProperty("nino.completado"),
										idioma.leerProperty("nino.vidas"),
										idioma.leerProperty("nino.numero_errores"),
										idioma.leerProperty("nino.tiempo")};
			for(int i = 0; i<datosHistorial.length; i++){
				JPanel panel = new JPanel();
				panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
				panel.setBorder(new LineBorder(Color.BLACK));
				
				for(int j = 0; j < datosHistorial[i].length; j++){				
					JPanel aux = new JPanel();
					aux.setLayout(new BoxLayout(aux, BoxLayout.LINE_AXIS));
					JLabel lbl = new JLabel(textos[j]);
					lbl.setBorder(new EmptyBorder(10, 10, 10, 10));
					lbl.setFont(new Font("Arial", Font.BOLD, 15));
					JLabel txt = new JLabel(datosHistorial[i][j]);
					aux.add(lbl);
					aux.add(txt);
					panel.add(aux);
				}				
				
				GridBagConstraints c = new GridBagConstraints();
				c.gridy = esLogopeda ? i+1 : i;
				c.gridx = 0;
				c.gridwidth = 2;
				c.weightx = 1;
				c.insets = new Insets(10, 10, 10, 10);
				c.fill= GridBagConstraints.HORIZONTAL;
				c.anchor = GridBagConstraints.CENTER;
				centroSur.add(panel,c);
		}	
		if(datosHistorial == null || datosHistorial.length == 0){
			centroSur.add(new JLabel(idioma.leerProperty("nino.sin_historial")));
		}
		JScrollPane scroll = new JScrollPane(centroSur, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		centro.add(scroll, BorderLayout.CENTER);

		centroSur.updateUI();
		centro.updateUI();
	}
	
	private JPanel crearCampo(final String[] datos){
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(new LineBorder(Color.BLACK));
		
		JPanel aux = new JPanel();
		aux.setLayout(new BoxLayout(aux, BoxLayout.LINE_AXIS));
		
		final JLabel lblFecha = new JLabel(idioma.leerProperty("nino.sesion_fecha") +"*");
		lblFecha.setBorder(new EmptyBorder(10, 10, 10, 10));
		lblFecha.setFont(new Font("Arial", Font.BOLD, 15));
		aux.add(lblFecha);
		if(esLogopeda){
			UtilDateModel model = new UtilDateModel();
			Properties p = new Properties();
			p.put("text.today", idioma.leerProperty("fecha.hoy"));
			p.put("text.month", idioma.leerProperty("fecha.mes"));
			p.put("text.year", idioma.leerProperty("fecha.ano"));
			JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
			JDatePickerImpl datePicker= new JDatePickerImpl(datePanel, new DateLabelFormatter());	
			datePicker.getJFormattedTextField().setText(datos[1]);
			datePicker.setFont(new Font("Serif", Font.ITALIC, 15));
			datePicker.setMaximumSize(dimTextFieldPequeno);
			lblFecha.setLabelFor(datePicker);
			aux.add(datePicker);
		}else{
			JTextField datePicker= new JTextField(datos[1]);
			datePicker.setFont(new Font("Serif", Font.ITALIC, 15));
			datePicker.setBackground(getBackground());
			datePicker.setDisabledTextColor(Color.BLACK);
			datePicker.setEnabled(false);
			datePicker.setMaximumSize(dimTextFieldPequeno);
			lblFecha.setLabelFor(datePicker);
			aux.add(datePicker);
		}
		panel.add(aux);		
		
		
		JLabel comentario = new JLabel(idioma.leerProperty("nino.sesion_comentario"));	
		comentario.setBorder(new EmptyBorder(10, 10, 10, 10));
		final JTextArea txtComentario = new JTextArea(datos[3]);
		comentario.setLabelFor(comentario);
		txtComentario.setWrapStyleWord(true);
		txtComentario.setLineWrap(true);
		txtComentario.setCaretPosition(0);
		txtComentario.setPreferredSize(dimTextAreaGrande);
		txtComentario.setFont(new Font("Serif", Font.ITALIC, 15));
		comentario.setFont(new Font("Arial", Font.BOLD, 15));
		if(esLogopeda){
			final JCheckBox ver = new JCheckBox(idioma.leerProperty("nino.ver_sesion"));
			ver.setFont(new Font("Arial", Font.BOLD, 15));
			ver.setBorder(new EmptyBorder(10, 10, 10, 10));
			ver.setSelected(datos[2].equals("S"));
			panel.add(ver);			

			panel.add(comentario);
			panel.add(new JScrollPane(txtComentario, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
			
			final JButton anadir = new JButton(datos[0].equals("") ? idioma.leerProperty("nino.sesion_anadir") :idioma.leerProperty("nino.sesion_modificar"));
			anadir.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String fecha = ((JDatePickerImpl) lblFecha.getLabelFor()).getJFormattedTextField().getText();
					if(fecha.length() == 0){
						JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("error.campos_obligatorios"), idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
					}else{
						String[] sesion = new String[4];
						sesion[1] = fecha;
						sesion[2] = ver.isSelected() ? "S" : "N";
						sesion[3] = txtComentario.getText();
						if(anadir.getText().equals(idioma.leerProperty("nino.sesion_anadir"))){
							gestorNinos.setDatos(gestorNinos.accionInsertarSesion, sesion);
							dialog = new JProgressDialog(VentanaNino.this, idioma.leerProperty("nino.titulo_creando_sesion"),
									idioma.leerProperty("nino.mensaje_creando_sesion"));
							new Thread(gestorNinos).start();						
						}else{
							sesion[0] = datos[0];
							gestorNinos.setDatos(gestorNinos.accionModificarSesion, sesion);
							dialog = new JProgressDialog(VentanaNino.this, idioma.leerProperty("nino.titulo_modificando_sesion"),
									idioma.leerProperty("nino.mensaje_modificando_sesion"));
							new Thread(gestorNinos).start();
						}
						
					}
					
				}
			});
	
			aux = new JPanel();
			aux.add(anadir);
			aux.setBorder(new EmptyBorder(10, 10, 10, 10));
			panel.add(aux);
		}else{
			txtComentario.setBackground(getBackground());
			txtComentario.setDisabledTextColor(Color.BLACK);
			txtComentario.setEnabled(false);

			panel.add(comentario);
			panel.add(new JScrollPane(txtComentario, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		}
		return panel;
	}

	private void getInfoNino() {
		JPanel panel = new JPanel();
		JLabel[] jblInformacion = new JLabel[15];
		final JTextField edad = new JTextField();
		JLabel lblEdad = new JLabel(idioma.leerProperty("nino.edad")); 
		
		TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),
				idioma.leerProperty("nino.datos_nino"));
		border.setTitleJustification(TitledBorder.CENTER);
		border.setTitleFont(new Font("Arial", Font.BOLD, 20));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(border);

		jblInformacion[0] = new JLabel(idioma.leerProperty("nino.idNino"));
		jblInformacion[1] = new JLabel(idioma.leerProperty("nombre")+"*");
		jblInformacion[2] = new JLabel(idioma.leerProperty("apellidos")+"*");
		jblInformacion[3] = new JLabel(idioma.leerProperty("fecha_nacimiento")+"*");
		jblInformacion[4] = new JLabel(idioma.leerProperty("nino.idioma_juego"));
		jblInformacion[5] = new JLabel(idioma.leerProperty("nino.telefono1")+"*");
		jblInformacion[6] = new JLabel(idioma.leerProperty("nino.telefono2"));
		jblInformacion[7] = new JLabel(idioma.leerProperty("nino.nombre_padre"));
		jblInformacion[8] = new JLabel(idioma.leerProperty("nino.profesion"));
		jblInformacion[9] = new JLabel(idioma.leerProperty("nino.nombre_madre"));
		jblInformacion[10] = new JLabel(idioma.leerProperty("nino.profesion"));
		jblInformacion[11] = new JLabel(idioma.leerProperty("nino.hermanos_edades"));
		jblInformacion[12] = new JLabel(idioma.leerProperty("nino.otras_convivencias"));
		jblInformacion[13] = new JLabel(idioma.leerProperty("nino.entrevistados"));
		jblInformacion[14] = new JLabel(idioma.leerProperty("nino.motivo_consulta"));
		
		if (datos == null || datos.length == 0) {
			JPanel aux = new JPanel();
			aux.setLayout(new BoxLayout(aux, BoxLayout.LINE_AXIS));
			for (int i = 0; i < jblInformacion.length; i++) {
				jblInformacion[i].setBorder(new EmptyBorder(10, 10, 10, 10));
				if (i == 14) {
					JTextArea textArea = new JTextArea();
					textArea.setWrapStyleWord(true);
					textArea.setLineWrap(true);
					textArea.setCaretPosition(0);
					txtInformacion[i] = textArea;
					txtInformacion[i].setPreferredSize(dimTextArea);
				} else if( i >= 11){
					txtInformacion[i] = new JTextField();
					txtInformacion[i].setPreferredSize(dimTextField2);
				}else if(i == 4){
					txtInformacion[i] = new JComboBox<String>(new String[]{idioma.leerProperty("idioma.espanol"), idioma.leerProperty("idioma.euskera")});
					((JComboBox<String>) txtInformacion[i]).setSelectedIndex(0);
					txtInformacion[i].setMaximumSize(dimTextFieldPequeno);					
				}else if(i == 3){
					UtilDateModel model = new UtilDateModel();
					Properties p = new Properties();
					p.put("text.today", idioma.leerProperty("fecha.hoy"));
					p.put("text.month", idioma.leerProperty("fecha.mes"));
					p.put("text.year", idioma.leerProperty("fecha.ano"));
					JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
					final JDatePickerImpl datePicker= new JDatePickerImpl(datePanel, new DateLabelFormatter());
					datePanel.addPropertyChangeListener(new PropertyChangeListener() {
						
						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							String date = datePicker.getJFormattedTextField().getText();
							if(date.length() > 0){
								String[] d = date.split("-");
								Calendar cal = Calendar.getInstance();
								Calendar calAux = Calendar.getInstance();
								calAux.set(Integer.valueOf(d[0]), Integer.valueOf(d[1])-1, Integer.valueOf(d[2]));
								int anos = Math.max(0, cal.get(Calendar.YEAR)-calAux.get(Calendar.YEAR));
								int meses = cal.get(Calendar.MONTH)-calAux.get(Calendar.MONTH);
								int dias = cal.get(Calendar.DAY_OF_MONTH)-calAux.get(Calendar.DAY_OF_MONTH);
								if(anos != 0 && meses < 0 || meses == 0 && dias < 0){
									anos --;
								}
								edad.setText(String.valueOf(anos));
							}							
						}
					});
					txtInformacion[i] = datePicker;
					txtInformacion[i].setMaximumSize(dimTextFieldPequeno);
					
				}else{
					txtInformacion[i] = new JTextField();
					txtInformacion[i].setMaximumSize(dimTextField);
				}
				jblInformacion[i].setFont(new Font("Arial", Font.BOLD, 15));
				txtInformacion[i].setFont(new Font("Serif", Font.ITALIC, 15));
				jblInformacion[i].setLabelFor(txtInformacion[i]);
				if (txtInformacion[i] instanceof JTextArea) {
					panel.add(jblInformacion[i]);
					panel.add(new JScrollPane(txtInformacion[i], JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
							JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
				} else{
					aux.add(jblInformacion[i]);
					aux.add(txtInformacion[i]);
				}	
				if(i == 3){						
					lblEdad.setBorder(new EmptyBorder(10, 10, 10, 10));
					lblEdad.setFont(new Font("Arial", Font.BOLD, 15));
					edad.setFont(new Font("Serif", Font.ITALIC, 15));
					lblEdad.setLabelFor(edad);
					edad.setMaximumSize(dimTextFieldPequeno);
					aux.add(lblEdad);
					aux.add(edad);	
					edad.setEnabled(false);
					edad.setBackground(getBackground());
					edad.setDisabledTextColor(Color.BLACK);					
				}
				if (aux.getComponentCount() >= 4 || i == 0 || i==4 || i == 11 || i == 12 || i == 13) {
					panel.add(aux);
					aux = new JPanel();
					aux.setLayout(new BoxLayout(aux, BoxLayout.LINE_AXIS));
				}
			}
		} else {
			JPanel aux = new JPanel();
			aux.setLayout(new BoxLayout(aux, BoxLayout.LINE_AXIS));
			for (int i = 0; i < jblInformacion.length; i++) {
				jblInformacion[i].setBorder(new EmptyBorder(10, 10, 10, 10));
				if (i == 14) {
					JTextArea textArea = new JTextArea(datos[i]);
					textArea.setWrapStyleWord(true);
					textArea.setLineWrap(true);
					textArea.setCaretPosition(0);
					txtInformacion[i] = textArea;
					txtInformacion[i].setPreferredSize(dimTextArea);
				} else if( i >= 11){
					txtInformacion[i] = new JTextField(datos[i]);
					txtInformacion[i].setPreferredSize(dimTextField2);
				}else if(i == 4){
					txtInformacion[i] = new JComboBox<String>(new String[]{idioma.leerProperty("idioma.espanol"), idioma.leerProperty("idioma.euskera")});
					if(datos[i].equals("euskera")){
						((JComboBox<String>) txtInformacion[i]).setSelectedIndex(1);						
					}else{
						((JComboBox<String>) txtInformacion[i]).setSelectedIndex(0);						
					}
					txtInformacion[i].setMaximumSize(dimTextFieldPequeno);					
				}else if(i == 3){
					UtilDateModel model = new UtilDateModel();
					Properties p = new Properties();
					p.put("text.today", idioma.leerProperty("fecha.hoy"));
					p.put("text.month", idioma.leerProperty("fecha.mes"));
					p.put("text.year", idioma.leerProperty("fecha.ano"));
					JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
					final JDatePickerImpl datePicker= new JDatePickerImpl(datePanel, new DateLabelFormatter());
					datePanel.addPropertyChangeListener(new PropertyChangeListener() {
						
						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							String date = datePicker.getJFormattedTextField().getText();
							if(date.length() > 0){
								String[] d = date.split("-");
								Calendar cal = Calendar.getInstance();
								Calendar calAux = Calendar.getInstance();
								calAux.set(Integer.valueOf(d[0]), Integer.valueOf(d[1])-1, Integer.valueOf(d[2]));
								int anos = Math.max(0, cal.get(Calendar.YEAR)-calAux.get(Calendar.YEAR));
								int meses = cal.get(Calendar.MONTH)-calAux.get(Calendar.MONTH);
								int dias = cal.get(Calendar.DAY_OF_MONTH)-calAux.get(Calendar.DAY_OF_MONTH);
								if(anos != 0 && meses < 0 || meses == 0 && dias < 0){
									anos --;
								}
								edad.setText(String.valueOf(anos));
							}							
						}
					});
					datePicker.getJFormattedTextField().setText(datos[i]);
					txtInformacion[i] = datePicker;
					txtInformacion[i].setMaximumSize(dimTextFieldPequeno);
					
				}else{
					txtInformacion[i] = new JTextField(datos[i]);
					txtInformacion[i].setMaximumSize(dimTextField);
				}
				jblInformacion[i].setFont(new Font("Arial", Font.BOLD, 15));
				txtInformacion[i].setFont(new Font("Serif", Font.ITALIC, 15));
				jblInformacion[i].setLabelFor(txtInformacion[i]);
				if (txtInformacion[i] instanceof JTextArea) {
					panel.add(jblInformacion[i]);
					panel.add(new JScrollPane(txtInformacion[i], JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
							JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
				} else{
					aux.add(jblInformacion[i]);
					aux.add(txtInformacion[i]);
				}	
				if(i == 3){						
					lblEdad.setBorder(new EmptyBorder(10, 10, 10, 10));
					lblEdad.setFont(new Font("Arial", Font.BOLD, 15));
					edad.setFont(new Font("Serif", Font.ITALIC, 15));
					lblEdad.setLabelFor(edad);
					edad.setMaximumSize(dimTextFieldPequeno);
					aux.add(lblEdad);
					aux.add(edad);	
					edad.setEnabled(false);
					edad.setBackground(getBackground());
					edad.setForeground(Color.BLACK);
					String[] d = ((JDatePickerImpl)txtInformacion[i]).getJFormattedTextField().getText().split("-");
					Calendar cal = Calendar.getInstance();
					Calendar calAux = Calendar.getInstance();
					calAux.set(Integer.valueOf(d[0]), Integer.valueOf(d[1])-1, Integer.valueOf(d[2]));
					int anos = Math.max(0, cal.get(Calendar.YEAR)-calAux.get(Calendar.YEAR));
					int meses = cal.get(Calendar.MONTH)-calAux.get(Calendar.MONTH);
					int dias = cal.get(Calendar.DAY_OF_MONTH)-calAux.get(Calendar.DAY_OF_MONTH);
					if(anos != 0 && meses < 0 || meses == 0 && dias < 0){
						anos --;
					}
					edad.setText(String.valueOf(anos));
					edad.setBackground(getBackground());
					edad.setDisabledTextColor(Color.BLACK);	
				}else if(i <= 2){
					txtInformacion[i].setEnabled(false);
					txtInformacion[i].setBackground(getBackground());
					((JTextField) txtInformacion[i]).setDisabledTextColor(Color.BLACK);
				}
				if (aux.getComponentCount() >= 4 || i == 0 || i==4 || i == 11 || i == 12 || i == 13) {
					panel.add(aux);
					aux = new JPanel();
					aux.setLayout(new BoxLayout(aux, BoxLayout.LINE_AXIS));
				}
			}
		}
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.gridx = 0;
		c.gridwidth = 2;
		c.weightx = 1;
		c.insets = new Insets(10, 10, 10, 10);
		c.fill= GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		centroSur.add(panel,c);
	}

	private void getAnamnesis() {
		JPanel panel = new JPanel();
		JLabel[] jblInformacion = new JLabel[9];
		TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),
				idioma.leerProperty("nino.anammnesis"));
		border.setTitleJustification(TitledBorder.CENTER);
		border.setTitleFont(new Font("Arial", Font.BOLD, 20));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(border);

		jblInformacion[0] = new JLabel(idioma.leerProperty("nino.embarazoParto"));
		jblInformacion[1] = new JLabel(idioma.leerProperty("nino.desFisico"));
		jblInformacion[2] = new JLabel(idioma.leerProperty("nino.desMotor"));
		jblInformacion[3] = new JLabel(idioma.leerProperty("nino.desOrofacial"));
		jblInformacion[4] = new JLabel(idioma.leerProperty("nino.desLenguaje"));
		jblInformacion[5] = new JLabel(idioma.leerProperty("nino.antecedentesFamiliares"));
		jblInformacion[6] = new JLabel(idioma.leerProperty("nino.datosMedicos"));
		jblInformacion[7] = new JLabel(idioma.leerProperty("nino.historialEscolar"));
		jblInformacion[8] = new JLabel(idioma.leerProperty("nino.atenFueraCentro"));
		if (datos == null || datos.length == 0) {
			for (int i = 0; i < jblInformacion.length; i++) {
				jblInformacion[i].setBorder(new EmptyBorder(10, 10, 10, 10));
				if (jblInformacion[i].getText().length() > 20)
					jblInformacion[i].setPreferredSize(new Dimension(100, 50));
				JTextArea textArea = new JTextArea();
				textArea.setWrapStyleWord(true);
				textArea.setLineWrap(true);
				textArea.setCaretPosition(0);
				txtInformacion[i+15] = textArea;
				jblInformacion[i].setFont(new Font("Arial", Font.BOLD, 15));
				txtInformacion[i+15].setFont(new Font("Serif", Font.ITALIC, 15));
				jblInformacion[i].setLabelFor(txtInformacion[i+15]);
				txtInformacion[i+15].setPreferredSize(dimTextArea2);
				panel.add(jblInformacion[i]);
				panel.add(new JScrollPane(txtInformacion[i+15], JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
						JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
			}
		} else {
			for (int i = 0; i < jblInformacion.length; i++) {
				jblInformacion[i].setBorder(new EmptyBorder(10, 10, 10, 10));
				if (jblInformacion[i].getText().length() > 20)
					jblInformacion[i].setPreferredSize(new Dimension(100, 50));
				JTextArea textArea = new JTextArea(datos[i+15]);
				textArea.setWrapStyleWord(true);
				textArea.setLineWrap(true);
				textArea.setCaretPosition(0);
				txtInformacion[i+15] = textArea;
				jblInformacion[i].setFont(new Font("Arial", Font.BOLD, 15));
				txtInformacion[i+15].setFont(new Font("Serif", Font.ITALIC, 15));
				jblInformacion[i].setLabelFor(txtInformacion[i+15]);
				txtInformacion[i+15].setPreferredSize(dimTextArea2);
				panel.add(jblInformacion[i]);
				panel.add(new JScrollPane(txtInformacion[i+15], JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
						JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
			}
		}		
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 1;
		c.gridx = 0;
		c.gridwidth = 1;
		c.weightx = 1;
		c.insets = new Insets(10, 10, 10, 10);
		c.fill= GridBagConstraints.BOTH;
		centroSur.add(panel,c);
	}

	private void getAutonomia() {
		JPanel panel = new JPanel();
		JLabel[] jblInformacion = new JLabel[20];
		TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),
				idioma.leerProperty("nino.autonomia"));
		border.setTitleJustification(TitledBorder.CENTER);
		border.setTitleFont(new Font("Arial", Font.BOLD, 20));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(border);

		jblInformacion[0] = new JLabel(idioma.leerProperty("nino.tipoAlimentos"));
		jblInformacion[1] = new JLabel(idioma.leerProperty("nino.alimentosPreferidos"));
		jblInformacion[2] = new JLabel(idioma.leerProperty("nino.reconoceComer"));
		jblInformacion[3] = new JLabel(idioma.leerProperty("nino.pideAlimento"));
		jblInformacion[4] = new JLabel(idioma.leerProperty("nino.biberonChupete"));
		jblInformacion[5] = new JLabel(idioma.leerProperty("nino.mastica"));
		jblInformacion[6] = new JLabel(idioma.leerProperty("nino.succionaTraga"));
		jblInformacion[7] = new JLabel(idioma.leerProperty("nino.muerdeCosas"));
		jblInformacion[8] = new JLabel(idioma.leerProperty("nino.saliva"));
		jblInformacion[9] = new JLabel(idioma.leerProperty("nino.horasSueno"));
		jblInformacion[10] = new JLabel(idioma.leerProperty("nino.intencionDormir"));
		jblInformacion[11] = new JLabel(idioma.leerProperty("nino.reconoceDormir"));
		jblInformacion[12] = new JLabel(idioma.leerProperty("nino.ronca"));
		jblInformacion[13] = new JLabel(idioma.leerProperty("nino.controlEsfinteres"));
		jblInformacion[14] = new JLabel(idioma.leerProperty("nino.avisaSucio"));
		jblInformacion[15] = new JLabel(idioma.leerProperty("nino.pideWC"));
		jblInformacion[16] = new JLabel(idioma.leerProperty("nino.reconoceRopa"));
		jblInformacion[17] = new JLabel(idioma.leerProperty("nino.necesidadCambioRopa"));
		jblInformacion[18] = new JLabel(idioma.leerProperty("nino.malestarSucio"));
		jblInformacion[19] = new JLabel(idioma.leerProperty("nino.sonarseNariz"));

		if (datos == null || datos.length == 0) {
			for (int i = 0; i < jblInformacion.length; i++) {
				jblInformacion[i].setBorder(new EmptyBorder(10, 10, 10, 10));
				if (jblInformacion[i].getText().length() > 20)
					jblInformacion[i].setPreferredSize(new Dimension(100, 50));
				JTextArea textArea = new JTextArea();
				textArea.setWrapStyleWord(true);
				textArea.setLineWrap(true);
				textArea.setCaretPosition(0);
				txtInformacion[i+24] = textArea;
				jblInformacion[i].setFont(new Font("Arial", Font.BOLD, 15));
				txtInformacion[i+24].setFont(new Font("Serif", Font.ITALIC, 15));
				jblInformacion[i].setLabelFor(txtInformacion[i+24]);
				txtInformacion[i+24].setPreferredSize(dimTextArea2);
				panel.add(jblInformacion[i]);
				panel.add(new JScrollPane(txtInformacion[i+24], JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
						JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
				panel.setAlignmentX(LEFT_ALIGNMENT);
			}
		} else {
			for (int i = 0; i < jblInformacion.length; i++) {
				jblInformacion[i].setBorder(new EmptyBorder(10, 10, 10, 10));
				if (jblInformacion[i].getText().length() > 20)
					jblInformacion[i].setPreferredSize(new Dimension(100, 50));
				JTextArea textArea = new JTextArea(datos[i+24]);
				textArea.setWrapStyleWord(true);
				textArea.setLineWrap(true);
				textArea.setCaretPosition(0);
				txtInformacion[i+24] = textArea;
				jblInformacion[i].setFont(new Font("Arial", Font.BOLD, 15));
				txtInformacion[i+24].setFont(new Font("Serif", Font.ITALIC, 15));
				jblInformacion[i].setLabelFor(txtInformacion[i+24]);
				txtInformacion[i+24].setPreferredSize(dimTextArea2);
				panel.add(jblInformacion[i]);
				panel.add(new JScrollPane(txtInformacion[i+24], JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
						JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
			}
		}
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 1;
		c.gridx = 1;
		c.gridwidth = 1;
		c.weightx = 1;
		c.insets = new Insets(10, 10, 10, 10);
		c.fill= GridBagConstraints.BOTH;
		centroSur.add(panel,c);
	}

	private void getSocializacion() {
		JPanel panel = new JPanel();
		JLabel[] jblInformacion = new JLabel[13];
		TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),
				idioma.leerProperty("nino.socializacion"));
		border.setTitleJustification(TitledBorder.CENTER);
		border.setTitleFont(new Font("Arial", Font.BOLD, 20));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(border);

		jblInformacion[0] = new JLabel(idioma.leerProperty("nino.aCargo"));
		jblInformacion[1] = new JLabel(idioma.leerProperty("nino.mayorApego"));
		jblInformacion[2] = new JLabel(idioma.leerProperty("nino.sinAtender"));
		jblInformacion[3] = new JLabel(idioma.leerProperty("nino.reconoceFamiliares"));
		jblInformacion[4] = new JLabel(idioma.leerProperty("nino.comportamientoDesconocidos"));
		jblInformacion[5] = new JLabel(idioma.leerProperty("nino.juegaNinos"));
		jblInformacion[6] = new JLabel(idioma.leerProperty("nino.relacionNinos"));
		jblInformacion[7] = new JLabel(idioma.leerProperty("nino.juegoFunional"));
		jblInformacion[8] = new JLabel(idioma.leerProperty("nino.imitaAcciones"));
		jblInformacion[9] = new JLabel(idioma.leerProperty("nino.objetosPreferidos"));
		jblInformacion[10] = new JLabel(idioma.leerProperty("nino.rechazaObjetos"));
		jblInformacion[11] = new JLabel(idioma.leerProperty("nino.juegaEnCasa"));
		jblInformacion[12] = new JLabel(idioma.leerProperty("nino.reconoceJuguetes"));

		if (datos == null || datos.length == 0) {
			for (int i = 0; i < jblInformacion.length; i++) {
				jblInformacion[i].setBorder(new EmptyBorder(10, 10, 10, 10));
				if (jblInformacion[i].getText().length() > 20)
					jblInformacion[i].setPreferredSize(new Dimension(100, 50));
				JTextArea textArea = new JTextArea();
				textArea.setWrapStyleWord(true);
				textArea.setLineWrap(true);
				textArea.setCaretPosition(0);
				txtInformacion[i+44] = textArea;
				jblInformacion[i].setFont(new Font("Arial", Font.BOLD, 15));
				txtInformacion[i+44].setFont(new Font("Serif", Font.ITALIC, 15));
				jblInformacion[i].setLabelFor(txtInformacion[i+44]);
				txtInformacion[i+44].setPreferredSize(dimTextArea2);
				panel.add(jblInformacion[i]);
				panel.add(new JScrollPane(txtInformacion[i+44], JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
						JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
			}
		} else {
			for (int i = 0; i < jblInformacion.length; i++) {
				jblInformacion[i].setBorder(new EmptyBorder(10, 10, 10, 10));
				if (jblInformacion[i].getText().length() > 20)
					jblInformacion[i].setPreferredSize(new Dimension(100, 50));
				JTextArea textArea = new JTextArea(datos[i+44]);
				textArea.setWrapStyleWord(true);
				textArea.setLineWrap(true);
				textArea.setCaretPosition(0);
				txtInformacion[i+44] = textArea;
				jblInformacion[i].setFont(new Font("Arial", Font.BOLD, 15));
				txtInformacion[i+44].setFont(new Font("Serif", Font.ITALIC, 15));
				jblInformacion[i].setLabelFor(txtInformacion[i+44]);
				txtInformacion[i+44].setPreferredSize(dimTextArea2);
				panel.add(jblInformacion[i]);
				panel.add(new JScrollPane(txtInformacion[i+44], JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
						JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
			}
		}
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 2;
		c.gridx = 0;
		c.gridwidth = 1;
		c.weightx = 1;
		c.insets = new Insets(10, 10, 10, 10);
		c.fill= GridBagConstraints.BOTH;
		centroSur.add(panel,c);
	}

	private void getLenguaje() {
		JPanel panel = new JPanel();
		JLabel[] jblInformacion = new JLabel[13];
		TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),
				idioma.leerProperty("nino.lenguaje"));
		border.setTitleJustification(TitledBorder.CENTER);
		border.setTitleFont(new Font("Arial", Font.BOLD, 20));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(border);

		jblInformacion[0] = new JLabel(idioma.leerProperty("nino.intencionalidadComunicativa"));
		jblInformacion[1] = new JLabel(idioma.leerProperty("nino.quienComunica"));
		jblInformacion[2] = new JLabel(idioma.leerProperty("nino.comoComunica"));
		jblInformacion[3] = new JLabel(idioma.leerProperty("nino.llamaAtencion"));
		jblInformacion[4] = new JLabel(idioma.leerProperty("nino.intentaAtencion"));
		jblInformacion[5] = new JLabel(idioma.leerProperty("nino.pideNecesita"));
		jblInformacion[6] = new JLabel(idioma.leerProperty("nino.gestoRechazo"));
		jblInformacion[7] = new JLabel(idioma.leerProperty("nino.agrado"));
		jblInformacion[8] = new JLabel(idioma.leerProperty("nino.estadosEmocion"));
		jblInformacion[9] = new JLabel(idioma.leerProperty("nino.gestoAdios"));
		jblInformacion[10] = new JLabel(idioma.leerProperty("nino.ordenesSencillas"));
		jblInformacion[11] = new JLabel(idioma.leerProperty("nino.ordenesComplejas"));
		jblInformacion[12] = new JLabel(idioma.leerProperty("nino.seEntiende"));

		if (datos == null || datos.length == 0) {
			for (int i = 0; i < jblInformacion.length; i++) {
				jblInformacion[i].setBorder(new EmptyBorder(10, 10, 10, 10));
				if (jblInformacion[i].getText().length() > 20)
					jblInformacion[i].setPreferredSize(new Dimension(100, 50));
				JTextArea textArea = new JTextArea();
				textArea.setWrapStyleWord(true);
				textArea.setLineWrap(true);
				textArea.setCaretPosition(0);
				txtInformacion[i+57] = textArea;
				jblInformacion[i].setFont(new Font("Arial", Font.BOLD, 15));
				txtInformacion[i+57].setFont(new Font("Serif", Font.ITALIC, 15));
				jblInformacion[i].setLabelFor(txtInformacion[i+57]);
				txtInformacion[i+57].setPreferredSize(dimTextArea2);
				panel.add(jblInformacion[i]);
				panel.add(new JScrollPane(txtInformacion[i+57], JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
						JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
				panel.setAlignmentX(LEFT_ALIGNMENT);
			}
		} else {
			for (int i = 0; i < jblInformacion.length; i++) {
				jblInformacion[i].setBorder(new EmptyBorder(10, 10, 10, 10));
				if (jblInformacion[i].getText().length() > 20)
					jblInformacion[i].setPreferredSize(new Dimension(100, 50));
				JTextArea textArea = new JTextArea(datos[i+57]);
				textArea.setWrapStyleWord(true);
				textArea.setLineWrap(true);
				textArea.setCaretPosition(0);
				txtInformacion[i+57] = textArea;
				jblInformacion[i].setFont(new Font("Arial", Font.BOLD, 15));
				txtInformacion[i+57].setFont(new Font("Serif", Font.ITALIC, 15));
				jblInformacion[i].setLabelFor(txtInformacion[i+57]);
				txtInformacion[i+57].setPreferredSize(dimTextArea2);
				panel.add(jblInformacion[i]);
				panel.add(new JScrollPane(txtInformacion[i+57], JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
						JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
			}
		}
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 2;
		c.gridx = 1;
		c.gridwidth = 1;
		c.weightx = 1;
		c.insets = new Insets(10, 10, 10, 10);
		c.fill= GridBagConstraints.BOTH;
		centroSur.add(panel,c);
	}

	private void getBtnNino() {
		JButton btnAnadir = new JButton(spNinos.getSelectedIndex() == 0 ? idioma.leerProperty("nino.anadir") : idioma.leerProperty("nino.modificar"));
		btnAnadir.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String[] datos = new String[txtInformacion.length];
				for(int i = 0; i < txtInformacion.length; i++){
					if(txtInformacion[i] instanceof JTextField){
						datos[i] = ((JTextField) txtInformacion[i]).getText();
					}else if(txtInformacion[i] instanceof JTextArea){
						datos[i] =((JTextArea) txtInformacion[i]).getText();
					}else if(txtInformacion[i] instanceof JComboBox<?>){
						switch(((JComboBox<String>) txtInformacion[i]).getSelectedIndex()){
						case 0:
							datos[i] = "espanol";
							break;
						case 1:
							datos[i] = "euskera";
							break;
						default:
							datos[i] = "espanol";
						}						
					}else if(txtInformacion[i] instanceof JDatePickerImpl){
						datos[i] = ((JDatePickerImpl) txtInformacion[i]).getJFormattedTextField().getText();						
					}
				}
				datos[0] = datos[0].trim();
				datos[5] = datos[5].trim();
				datos[6] = datos[6].trim();
				if(datos[1].trim().length() == 0 || datos[2].trim().length() == 0 || datos[3].trim().length() == 0 || datos[5].trim().length() == 0){
					JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("error.campos_obligatorios"), idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
				}else if(!FiltroTexto.soloTexto(datos[1])){
					JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("error.formato_nombre"), idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
				}else if(!FiltroTexto.soloTexto(datos[2])){
					JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("error.formato_apellidos"), idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
				}else if(!FiltroTexto.soloNumero(datos[5]) || !FiltroTexto.soloNumero(datos[6]) && datos[6].length()>0){
					JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("error.formato_telefono"), idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);				
				}else if(datos[0].trim().length() == 0){
					int seleccionado = JOptionPane.showOptionDialog(contentPane,
							idioma.leerProperty("nino.mensaje_sin_id"),
							idioma.leerProperty("nino.titulo_sin_id"), JOptionPane.YES_NO_OPTION,
							JOptionPane.DEFAULT_OPTION, null,
							new String[] { idioma.leerProperty("aceptar"), idioma.leerProperty("cancelar") },
							idioma.leerProperty("aceptar"));
					if(seleccionado == 0){
						if(spNinos.getSelectedIndex() == 0){
							gestorNinos.setDatos(gestorNinos.accionInsertar, datos);
							dialog = new JProgressDialog(VentanaNino.this, idioma.leerProperty("nino.titulo_creando"),
									idioma.leerProperty("nino.mensaje_creando"));
							new Thread(gestorNinos).start();						
						}else{
							gestorNinos.setDatos(gestorNinos.accionModificar, datos);
							dialog = new JProgressDialog(VentanaNino.this, idioma.leerProperty("nino.titulo_creando"),
									idioma.leerProperty("nino.mensaje_creando"));
							new Thread(gestorNinos).start();								
						}
					}					
				}else{
					if(!FiltroTexto.soloNumero(datos[0])){
						JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("error.formato_numero_casos"), idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);				
					}else{
						if(spNinos.getSelectedIndex() == 0){
							Boolean b = gestorNinos.existeNino(datos[0]);
							if(b == null){return;
							}else if(b){
								JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("nino.error_id_existe"), idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
							}else{
								gestorNinos.setDatos(gestorNinos.accionInsertar, datos);
								dialog = new JProgressDialog(VentanaNino.this, idioma.leerProperty("nino.titulo_modificando"),
										idioma.leerProperty("nino.mensaje_modificando"));
								new Thread(gestorNinos).start();	
							}
						}else{
							gestorNinos.setDatos(gestorNinos.accionModificar, datos);
							dialog = new JProgressDialog(VentanaNino.this, idioma.leerProperty("nino.titulo_modificando"),
							idioma.leerProperty("nino.mensaje_modificando"));
							new Thread(gestorNinos).start();			
						}
					}
				}
			}
		});
		JPanel aux = new JPanel();
		aux.add(btnAnadir);
		centro.add(aux, BorderLayout.SOUTH);		
	}
	
	private void getBtnAtras(){
		sur.removeAll();
		sur.setLayout(new GridBagLayout());
		JButton btnAtras = new JButton("<");
		btnAtras.setAlignmentX(Component.LEFT_ALIGNMENT);

		btnAtras.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new VentanaMenu();
				gestorNinos.deleteObserver(VentanaNino.this);
				gestorNinos.salirPantalla();
				dispose();
			}
		});
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 2.0;
		c.insets = new Insets(10, 10, 10, 10);
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.gridy = 1;
		c.fill = GridBagConstraints.EAST;
		c.anchor = GridBagConstraints.WEST;
		sur.add(btnAtras, c);
	}
	
	

	@Override
	public void update(Observable o, Object arg) {
		dialog.dispose();
		setEnabled(true);
		if ((int) arg == gestorNinos.accionCargar) {
			contentPane = new JPanel();
			setContentPane(contentPane);
			norte = new JPanel();
			centro = new JPanel();
			centroSur = new JPanel();
			sur = new JPanel();

			setLayout(new BorderLayout());
			centro.setLayout(new BorderLayout());
			centroSur.setLayout(new GridBagLayout());
			getTituloSesion();
			getSeleccionNino(0,0);
			if (esLogopeda) {
				getInformacion();
			}else{
				getSesiones();
			}
			getBtnAtras();

			add(norte, BorderLayout.NORTH);
			
			add(centro, BorderLayout.CENTER);
			add(sur, BorderLayout.SOUTH);
			setAlwaysOnTop(true);
			setExtendedState(MAXIMIZED_BOTH);
			pack();
			//setResizable(false);
			setLocationRelativeTo(null);
			setVisible(true);
		} else if ((int) arg == gestorNinos.accionInsertar) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("nino.mensaje_creado") + " "+((JTextField) txtInformacion[1]).getText()+" "+((JTextField) txtInformacion[2]).getText(),
					idioma.leerProperty("nino.titulo_creado"), JOptionPane.INFORMATION_MESSAGE);
			getSeleccionNino(0, 0);
			getInformacion();
		} else if ((int) arg == gestorNinos.accionCargarError) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("error_carga"),
					idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
			gestorNinos.deleteObserver(this);
			new VentanaLogin();
			dispose();
		}else if ((int) arg == gestorNinos.accionModificar) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("nino.mensaje_modificado"),
					idioma.leerProperty("nino.titulo_modificado"), JOptionPane.INFORMATION_MESSAGE);
		} else if ((int) arg == gestorNinos.accionInsertarSesion) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("nino.mensaje_sesion_agregada"),
					idioma.leerProperty("nino.titulo_sesion_agregada"), JOptionPane.INFORMATION_MESSAGE);
			getSesiones();
		} else if ((int) arg == gestorNinos.accionModificarSesion) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("nino.mensaje_sesion_modificada"),
					idioma.leerProperty("nino.titulo_sesion_modificada"), JOptionPane.INFORMATION_MESSAGE);
			getSesiones();
		} else if ((int) arg == gestorNinos.accionInsertarError) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("nino.error_anadir"),
					idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
		} else if ((int) arg == gestorNinos.accionModificarError) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("nino.error_modificar"),
					idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
		} else if ((int) arg == gestorNinos.accionInsertarSesionError) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("nino.error_anadir_sesion"),
					idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
		}
		 else if ((int) arg == gestorNinos.accionInsertarSesionError) {
				JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("nino.error_modificar_sesion"),
						idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
			}
	}

}