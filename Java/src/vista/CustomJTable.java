package vista;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import javax.xml.transform.Source;

import modelo.IdiomaProperties;

public class CustomJTable extends JTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	TableRowSorter<CustomTableModel> sorter;
	
	public CustomJTable(CustomTableModel model){
		super(model);
		
		sorter = new TableRowSorter<CustomTableModel>(model);
	    setRowSorter(sorter);
	    modificarYBorrado();
        setPreferredScrollableViewportSize(new Dimension(500, 300));        
        setFillsViewportHeight(true);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN );
        
        for(int i = 0; i < getColumnCount(); i++){
        	if(getColumnClass(i) == String.class){
                getColumnModel().getColumn(i).setCellRenderer( new TextAreaRenderer());
                getColumnModel().getColumn(i).setCellEditor(new TextAreaEditor());     
        	}
        }
        
        getTableHeader().setReorderingAllowed(false);

	    setRowSorter(sorter);
        setPreferredScrollableViewportSize(new Dimension(500, 300));        
        setFillsViewportHeight(true);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN );		
	}
	
	public void primerNumero(){
        getColumnModel().getColumn(0).setMaxWidth(90);
        getColumnModel().getColumn(0).setMinWidth(90); 		
	}
	
	public void modificarYBorrado(){
		for(int i = 0; i < getColumnCount(); i++){
			if(getColumnName(i).equals(IdiomaProperties.getIdiomaProperties().leerProperty("borrar")) || getColumnName(i).equals(IdiomaProperties.getIdiomaProperties().leerProperty("modificar"))){
				sorter.setSortable(i, false);
		        getColumnModel().getColumn(i).setMaxWidth(65);
		        getColumnModel().getColumn(i).setMinWidth(65);				
			}
		}	
	}
	
	public void setNoSorteable(int column){
		sorter.setSortable(column, false);		
	}
	
	public void todosNoSorteables(){
		for(int i = 0; i < getColumnCount(); i++) sorter.setSortable(i, false);		
	}
	
	 /** 
     * Update the row filter regular expression from the expression in
     * the text box.
     */
    public void newFilter(String text, int[] columnas) {   	
        RowFilter<CustomTableModel, Object> rf = null;
        //If current expression doesn't parse, don't update.
        try {
            rf = RowFilter.regexFilter("(?i)"+text, columnas);
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        sorter.setRowFilter(rf);
    }
    
    public void limitarCaracteres(int columna, final int limite){
		 JTextField areaText = new JTextField();
         PlainDocument document = (PlainDocument) areaText.getDocument();

         document.setDocumentFilter(new DocumentFilter() {

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset,
                    int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                String string = fb.getDocument().getText(0,
                        fb.getDocument().getLength())
                        + text;

                if (string.length() <= limite)
                    super.replace(fb, offset, length, text, attrs); 
            }

        });
        getColumnModel().getColumn(columna).setCellEditor(new DefaultCellEditor(areaText));    	
    }
    
    /**
     * {@link http://esus.com/embedding-a-jtextarea-in-a-jtable-cell/}
     * @author Pc-Aitor
     *
     */
    class TextAreaRenderer extends JScrollPane implements TableCellRenderer
    {
    	   /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		JTextArea textarea;
    	  
    	   public TextAreaRenderer() {
    	      textarea = new JTextArea();
    	      textarea.setLineWrap(true);
    	      textarea.setWrapStyleWord(true);
    	      getViewport().add(textarea);
    	   }
    	  
    	   public Component getTableCellRendererComponent(JTable table, Object value,
    	                                  boolean isSelected, boolean hasFocus,
    	                                  int row, int column)
    	   {
    	      if (isSelected) {
    	         setForeground(table.getSelectionForeground());
    	         setBackground(table.getSelectionBackground());
    	         textarea.setForeground(table.getSelectionForeground());
    	         textarea.setBackground(table.getSelectionBackground());
    	      } else {
    	         setForeground(table.getForeground());
    	         setBackground(table.getBackground());
    	         textarea.setForeground(table.getForeground());
    	         textarea.setBackground(table.getBackground());
    	      }
    	  
    	      textarea.setText((String) value);
    	      textarea.setCaretPosition(0);
    	      return this;
    	   }
    	}
    	  
    	class TextAreaEditor extends DefaultCellEditor {
    	   /**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			protected JScrollPane scrollpane;
    	    protected JTextArea textarea;
    	  
    	   public TextAreaEditor() {
    	      super(new JCheckBox());
    	      scrollpane = new JScrollPane();
    	      textarea = new JTextArea(); 
    	      textarea.setWrapStyleWord(true);
    	      textarea.setLineWrap(true);
    	      textarea.setWrapStyleWord(true);
    	      scrollpane.getViewport().add(textarea);
    	   }
    	  
    	   public Component getTableCellEditorComponent(JTable table, Object value,
    	                                   boolean isSelected, int row, int column) {
    	      textarea.setText((String) value);
    	  
    	      return scrollpane;
    	   }
    	  
    	   public Object getCellEditorValue() {
    	      return textarea.getText();
    	   }
    	}
    
}
