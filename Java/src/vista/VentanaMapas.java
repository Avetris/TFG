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
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;

import modelo.FiltroTexto;
import modelo.GestorMapas;
import modelo.IdiomaProperties;
import modelo.GestorSesion;


public class VentanaMapas extends JFrame implements Observer {

	/**
	 * 
	 */
	static final long serialVersionUID = 1L;
	
	IdiomaProperties idioma;
	GestorMapas gestorMapas;
		
	JPanel contentPane;
	JPanel norte, centro, sur;

	ImagePanel panelMapa;
	
	List<JButton> botones;
	
	JTextField txtNombre;
	JButton btnAnadirBotones, btnAnadirMapa;
			
	JComboBox<String> cbMapas;
	
	JProgressDialog dialog;
	
	JFileChooser fileChooser;
		
	Dimension dimVentana = new Dimension(1000, 1000);
	Dimension dimAreaTexto = new Dimension(150, 25);

	/**
	 * Create the frame.
	 */
	public VentanaMapas() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		idioma = IdiomaProperties.getIdiomaProperties();
		gestorMapas = GestorMapas.getGestorMapas();
		
		int log = GestorSesion.obtSesion().esLogopeda();
		if(log == 0){
			gestorMapas.addObserver(this);
			
			dialog = new JProgressDialog(this, idioma.leerProperty("mapas.titulo_cargando"), idioma.leerProperty("mapas.mensaje_cargando"));
			gestorMapas.setParametrosYAccion(gestorMapas.accionCargar, null);
			new Thread(gestorMapas).start();	
			setEnabled(false);			
		}else{
			new VentanaLogin();
			dispose();
		}	
	}

	private void getTituloSesion() {
	
		JLabel lblTitulo = new JLabel(idioma.leerProperty("mapas.ventana"));
		lblTitulo.setHorizontalAlignment(0);
		lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
		lblTitulo.setOpaque(true);
		lblTitulo.setForeground(Color.black);
		
		Border paddingBorder = BorderFactory.createEmptyBorder(10,10,10,10);
		Border border = BorderFactory.createBevelBorder(0);
		lblTitulo.setBorder(BorderFactory.createCompoundBorder(border,paddingBorder));

		norte.add(lblTitulo);
	}
	
	private void getSeleccionMapas(){
		centro.setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		JLabel lblMapa = new JLabel(idioma.leerProperty("mapa"));
		cbMapas = new JComboBox<>(gestorMapas.obtenerNombreMapas());
		cbMapas.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				
                if(arg0.getStateChange() == ItemEvent.SELECTED){
                	if(cbMapas.getSelectedIndex() != 0){
    					ImageIcon img = gestorMapas.getImagen(cbMapas.getSelectedIndex()-1);
    					panelMapa.removeAll();
    					if(img != null){
    						ponerImagen(img);
    					}else{
    						panelMapa.setImage(null);
    					}		
    					List<Float[]> btns = gestorMapas.getBotones(cbMapas.getSelectedIndex()-1);
    					float w = panelMapa.getWidth() / 1280f;
    					float h = panelMapa.getHeight() / 720f;
    					botones.clear();
    					for(Float[] f : btns){
    						anadirBotonMapa(Math.round(f[0]*w), Math.round(f[1]*h),Math.round(f[2]*w),Math.round(f[3]*h));
    					}
                		txtNombre.setText(cbMapas.getItemAt(cbMapas.getSelectedIndex()));
    					txtNombre.setEnabled(false);
    					btnAnadirBotones.setVisible(true);
    					btnAnadirMapa.setText(idioma.leerProperty("mapas.modificar"));
    				}else{
    					panelMapa.setImage(null);
    					panelMapa.removeAll();
    					botones.clear();
    					txtNombre.setText("");
    					txtNombre.setEnabled(true);
    					btnAnadirBotones.setVisible(false);
    					btnAnadirMapa.setText(idioma.leerProperty("mapas.seleccionar"));
    					panelMapa.repaint();
    				}
                }				
			}
		});
		panel.add(lblMapa);
		panel.add(cbMapas);
		panelMapa = new ImagePanel(null);		
		panelMapa.setLayout(null);
		centro.add(panel, BorderLayout.NORTH);
		centro.add(panelMapa, BorderLayout.CENTER);
		 
	}
	private void getBtnAnadirMapa(){
		sur.removeAll();
		sur.setLayout(new GridBagLayout());
		JLabel lblNombre = new JLabel(idioma.leerProperty("mapas.nombre")+"*");
		lblNombre.setFont(new Font("Arial", Font.BOLD, 15));
		txtNombre = new JTextField();
		txtNombre.setFont(new Font("Serif", Font.ITALIC, 12));
		txtNombre.setPreferredSize(dimAreaTexto);
		txtNombre.addKeyListener(FiltroTexto.textInputLimiter(txtNombre, 20));
		btnAnadirBotones = new JButton(idioma.leerProperty("mapas.anadir_botones"));
		btnAnadirBotones.setVisible(false);
		btnAnadirMapa = new JButton(idioma.leerProperty("mapas.seleccionar"));
		btnAnadirMapa.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(btnAnadirMapa.getText().equals(idioma.leerProperty("mapas.seleccionar"))){
					if(buscarImagen(txtNombre.getText())){				
						JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("mapas.mensaje_seleccionar_botones"), idioma.leerProperty("mapas.titulo_seleccionar_botones"), JOptionPane.INFORMATION_MESSAGE);	
						btnAnadirMapa.setText(idioma.leerProperty("mapas.anadir"));	
						txtNombre.setEnabled(false);
						btnAnadirBotones.setVisible(true);
					}					
				}else if(btnAnadirMapa.getText().equals(idioma.leerProperty("mapas.anadir"))){
					crearMapa(txtNombre.getText());
				}else if(btnAnadirMapa.getText().equals(idioma.leerProperty("mapas.modificar"))){
					modificarMapa();
				}
					
			}
		});
		btnAnadirBotones.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				anadirBotonMapa(null, null, null, null);				
			}
		});
		
		JButton btnAtras = new JButton("<");
		btnAtras.setEnabled(true);
		btnAtras.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		
		btnAtras.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new VentanaAdministrarJuegos();
				gestorMapas.deleteObserver(VentanaMapas.this);
				dispose();				
			}
		});

		JPanel flowPanel = new JPanel();
		flowPanel.setLayout(new FlowLayout());
		flowPanel.add(lblNombre);
		flowPanel.add(txtNombre);
		flowPanel.add(btnAnadirMapa);
		flowPanel.add(btnAnadirBotones);
		GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 2.0; 
        c.gridwidth = 2;    
		c.insets = new Insets(10,10,10,10);
        sur.add(flowPanel, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 2.0; 
        c.gridwidth = GridBagConstraints.RELATIVE;  
		c.insets = new Insets(10,10,10,10);
        c.gridy = 1; 
        c.fill = GridBagConstraints.EAST;
        c.anchor = GridBagConstraints.WEST;
		sur.add(btnAtras, c);	
	}
	
	private void anadirBotonMapa(Integer x, Integer y, Integer width, Integer height){
		botones.add(new JButton(idioma.leerProperty("mapas.boton")+" "+(botones.size()+1)));
		if(x == null) x = botones.get(botones.size()-1).getWidth();
		if(y == null) y = botones.get(botones.size()-1).getHeight();	
		if(width == null) width = 50;	
		if(height == null) height = 50;		
		botones.get(botones.size()-1).setBounds(x, y,width,height);
		botones.get(botones.size()-1).addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent arg0) {}
			
			@Override
			public void mouseDragged(MouseEvent arg0) {
				JButton btn = (JButton) arg0.getComponent();
				if(arg0.getModifiersEx() == InputEvent.BUTTON1_DOWN_MASK){							
					int X=arg0.getX()+btn.getX();
		            int Y=arg0.getY()+btn.getY();
		            int mapaX = (int) panelMapa.getLocation().getX();
		            int mapaY = (int) panelMapa.getLocation().getY()-35;
		            if(X <= mapaX){
		            	X=(int) mapaX;
		            }else if(X >= mapaX+panelMapa.getWidth()-btn.getWidth()){
		            	X=(int) mapaX+panelMapa.getWidth()-btn.getWidth();
		            }
		            if(Y <= mapaY){
		            	Y=mapaY;
		            }else if(Y >= mapaY+panelMapa.getHeight()-btn.getHeight()){
		            	Y=(int) mapaY+panelMapa.getHeight()-btn.getHeight();
		            }
		            btn.setBounds(X,Y,btn.getWidth(),btn.getHeight());	
				}					
			}
		});	
		botones.get(botones.size()-1).addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				JButton btn = (JButton) arg0.getComponent();
				if(arg0.getButton() == MouseEvent.BUTTON3){
					int seleccionado = JOptionPane.showOptionDialog(contentPane, 
							idioma.leerProperty("mapas.mensaje_eliminar_boton"), 
							idioma.leerProperty("mapas.titulo_eliminar_boton"), 
							JOptionPane.YES_NO_OPTION, 
							JOptionPane.DEFAULT_OPTION, 
							null, new String[]{idioma.leerProperty("aceptar"), idioma.leerProperty("cancelar")}, idioma.leerProperty("aceptar"));
					if(seleccionado == 0){
						panelMapa.remove(btn);
						botones.remove(btn);
						for(int i = 0; i < botones.size(); i++){
							botones.get(i).setText(idioma.leerProperty("mapas.boton")+" "+(i+1));
						}
						panelMapa.repaint();
					}
				}						
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				JButton btn = (JButton) arg0.getComponent();
				if(arg0.getButton() == MouseEvent.BUTTON1 && arg0.isControlDown()){							
					double tamano = Math.max(btn.getWidth(), btn.getHeight());
					if(tamano > 150){
						tamano = 50;
					}else{
						tamano += 10;
					}
		            btn.setBounds(btn.getX(),btn.getY(),(int) tamano, (int) tamano);							
				}				
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {}					
			@Override
			public void mouseEntered(MouseEvent arg0) {}					
			@Override
			public void mouseClicked(MouseEvent arg0) {}
		});
		panelMapa.add(botones.get(botones.size()-1));
		panelMapa.repaint();
	}
	
	private boolean buscarImagen(String nombre){
		if(nombre != null && nombre.trim().length() > 0){
			Boolean b = gestorMapas.existeNombre(nombre);
			if(b == null){return false;
			}else if(b){
				JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("mapas.error_existe"),
						idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
				return false;
			}else{
				if (fileChooser == null) {
					fileChooser = new JFileChooser();
					
					fileChooser.addChoosableFileFilter(new FileFilter() {							
						@Override
						public String getDescription() {return null;}
						
						@Override
						public boolean accept(File f) {
							 if (f.isDirectory()) return true;
							 String name = f.getName().toLowerCase();								 								 
							 if (name != null && name.length() > 2) {
								 if(name.endsWith("jpg")){
									 BufferedImage bimg = null;
									try {
										bimg = ImageIO.read(f);
										if(bimg.getWidth() >= 640 && bimg.getHeight() >= 360){
											return true;
										}else{
											return false;
										}
									} catch (IOException e) {
										e.printStackTrace();
										return false;
									}										 
								 }else{
									 return false;
								 }							
					            
					        }else{
					        	return false;
					        }
						}
					});
					fileChooser.setAcceptAllFileFilterUsed(false);
					fileChooser.setAccessory(new ImagePreview(fileChooser));
		        }					
		        int returnVal = fileChooser.showDialog(VentanaMapas.this,idioma.leerProperty("mapas.seleccionar"));
		        if (returnVal == JFileChooser.APPROVE_OPTION) {				        	
		        	ponerImagen(fileChooser.getSelectedFile());
			        fileChooser.setSelectedFile(null);
		        	return true;
		        }else{
			        fileChooser.setSelectedFile(null);
			        return false;		        	
		        }
			}
		}else{
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("mapas.error_nombre"), idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	
	private void crearMapa(String nombre){
		boolean bien = true;
		if(botones.size() > 0){
			for(int i = 0; i < botones.size() && bien; i++){
				for(int j = 0; j < botones.size() && bien; j++){
					if(i != j){
						JButton btn1 = botones.get(i);
						JButton btn2 = botones.get(j);
						if(btn1.getWidth() > btn2.getWidth()){
							bien = !(btn1.getBounds().contains(btn2.getX(), btn2.getY()) ||
										btn1.getBounds().contains(btn2.getX(), btn2.getY()+btn2.getHeight()) ||
										btn1.getBounds().contains(btn2.getX()+btn2.getWidth(), btn2.getY()) ||
										btn1.getBounds().contains(btn2.getX()+btn2.getWidth(), btn2.getY()+btn2.getHeight()));
									
						}else{
							bien = !(btn2.getBounds().contains(btn1.getX(), btn1.getY()) ||
									btn2.getBounds().contains(btn1.getX(), btn1.getY()+btn1.getHeight()) ||
									btn2.getBounds().contains(btn1.getX()+btn1.getWidth(), btn1.getY()) ||
									btn2.getBounds().contains(btn1.getX()+btn1.getWidth(), btn1.getY()+btn1.getHeight()));
						}
					}
				}
			}
			if(bien){
				float width = 1280f / panelMapa.getWidth();
				float height = 720f / panelMapa.getHeight();
				
				Float[][] medidas = new Float[botones.size()][4];
				for(int i = 0; i < botones.size(); i++){
					medidas[i][0] = botones.get(i).getX()*width;
					medidas[i][1] = botones.get(i).getY()*height;
					medidas[i][2] = botones.get(i).getWidth()*width;
					medidas[i][3] = botones.get(i).getHeight()*height;
				}
				HashMap<String, Object[][]> valores = new HashMap<>();
				valores.put("Nombre", new Object[][]{{nombre}});
				valores.put("Imagen", new Object[][]{{panelMapa.getImageFile()}});
				valores.put("Medidas", medidas);
				gestorMapas.setParametrosYAccion(gestorMapas.accionInsertar, valores);
				dialog = new JProgressDialog(VentanaMapas.this, idioma.leerProperty("mapas.titulo_creando"), idioma.leerProperty("mapas.mensaje_creando"));
				new Thread(gestorMapas).start();
				setEnabled(false);
				botones.clear();		
			}else{
				JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("mapas.error_botones_colapsados"), idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
			}
		}else{
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("mapas.error_botones_minimos"), idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void modificarMapa(){
		boolean bien = true;
		if(botones.size() > 0){
			for(int i = 0; i < botones.size() && bien; i++){
				for(int j = 0; j < botones.size() && bien; j++){
					if(i != j){
						JButton btn1 = botones.get(i);
						JButton btn2 = botones.get(j);
						if(btn1.getWidth() > btn2.getWidth()){
							bien = !(btn1.getBounds().contains(btn2.getX(), btn2.getY()) ||
										btn1.getBounds().contains(btn2.getX(), btn2.getY()+btn2.getHeight()) ||
										btn1.getBounds().contains(btn2.getX()+btn2.getWidth(), btn2.getY()) ||
										btn1.getBounds().contains(btn2.getX()+btn2.getWidth(), btn2.getY()+btn2.getHeight()));
									
						}else{
							bien = !(btn2.getBounds().contains(btn1.getX(), btn1.getY()) ||
									btn2.getBounds().contains(btn1.getX(), btn1.getY()+btn1.getHeight()) ||
									btn2.getBounds().contains(btn1.getX()+btn1.getWidth(), btn1.getY()) ||
									btn2.getBounds().contains(btn1.getX()+btn1.getWidth(), btn1.getY()+btn1.getHeight()));
						}
					}
				}
			}
			if(bien){
				float width = 1280f / panelMapa.getWidth();
				float height = 720f / panelMapa.getHeight();
				
				Float[][] medidas = new Float[botones.size()][4];
				for(int i = 0; i < botones.size(); i++){
					medidas[i][0] = botones.get(i).getX()*width;
					medidas[i][1] = botones.get(i).getY()*height;
					medidas[i][2] = botones.get(i).getWidth()*width;
					medidas[i][3] = botones.get(i).getHeight()*height;
				}
				HashMap<String, Object[][]> valores = new HashMap<>();
				valores.put("Posicion", new Object[][]{{cbMapas.getSelectedIndex() - 1}});
				valores.put("Medidas", medidas);
				gestorMapas.setParametrosYAccion(gestorMapas.accionModificar, valores);
				dialog = new JProgressDialog(VentanaMapas.this, idioma.leerProperty("mapas.titulo_modificando"), idioma.leerProperty("mapas.mensaje_modificando"));
				new Thread(gestorMapas).start();
				setEnabled(false);
				botones.clear();
			}else{
				JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("mapas.error_botones_colapsados"), idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
			}
		}else{
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("mapas.error_botones_minimos"), idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void ponerImagen(File img){
		panelMapa.setImageFile(img);		
		panelMapa.repaint();
		centro.updateUI();
	}
	
	private void ponerImagen(ImageIcon img){
		panelMapa.setImage(img);		
		panelMapa.repaint();
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		dialog.dispose();
		setEnabled(true);
		if((int) arg1 == gestorMapas.accionCargar){
			setSize(dimVentana);
			setLocationRelativeTo(null);

			contentPane = new JPanel();
			setContentPane(contentPane);
			norte = new JPanel();
			centro = new JPanel();
			sur = new JPanel();

			setLayout(new BorderLayout());

			getTituloSesion();
			getSeleccionMapas();
			getBtnAnadirMapa();

			add(norte, BorderLayout.NORTH);
			add(centro, BorderLayout.CENTER);
			add(sur, BorderLayout.SOUTH);
			
			setVisible(true);
			botones = new ArrayList<JButton>();
    		setResizable(false);
		}else if ((int) arg1 == gestorMapas.accionCargarError) {
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("error_carga"),
					idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
			gestorMapas.deleteObserver(this);
			new VentanaLogin();
			dispose();
		}else if((int) arg1 == gestorMapas.accionInsertar){
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("mapas.mensaje_correcto"), idioma.leerProperty("mapas.titulo_correcto"), JOptionPane.INFORMATION_MESSAGE);			            	
			cbMapas.removeAllItems();
			centro.removeAll();
			getSeleccionMapas();
			getBtnAnadirMapa();
			centro.updateUI();	
			sur.updateUI();		
		}else if((int) arg1 == gestorMapas.accionModificar){
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("mapas.mensaje_modificado"), idioma.leerProperty("mapas.titulo_modificado"), JOptionPane.INFORMATION_MESSAGE);			            	
			cbMapas.removeAllItems();
			centro.removeAll();
			getSeleccionMapas();
			getBtnAnadirMapa();
			centro.updateUI();	
			sur.updateUI();		
		}else if((int) arg1 == gestorMapas.accionInsertarError){
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("mapas.error_crear"), idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
		}else if((int) arg1 == gestorMapas.accionModificarError){
			JOptionPane.showMessageDialog(contentPane, idioma.leerProperty("mapas.error_modificar"), idioma.leerProperty("error"), JOptionPane.ERROR_MESSAGE);
		}
	}
}