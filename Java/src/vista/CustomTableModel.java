package vista;

import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

/**
 * {@link https://docs.oracle.com/javase/tutorial/uiswing/components/table.html}
 * @author Aitor
 *
 */
public class CustomTableModel extends AbstractTableModel  {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] columnNames;
    private Object[][] data;
    private boolean editableTodo, primeraNoInteger;    


	public CustomTableModel(Object[][] data, String[] columnNames, boolean editableTodo, boolean primeraNoInteger){
		this.columnNames = columnNames;
		this.data = data;
		this.editableTodo = editableTodo;
		this.primeraNoInteger = primeraNoInteger;
	}
    
    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    public Class getColumnClass(int c) {    
    	for(int i = 0; i < data.length; i++){
    		if(getValueAt(i, c) != null){
    	        return getValueAt(i, c).getClass();    			
    		}
    	}
        return Object.class;
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
        if (getColumnClass(col)==Integer.class || col == 0) {
            return primeraNoInteger;
        } else {
            return editableTodo || row == getRowCount() -1 || getColumnClass(col)==Boolean.class;
        }
    }

    /*
     * Don't need to implement this method unless your table's data can
     * change.
     */
    public void setValueAt(Object value, int row, int col) {
      if(getColumnClass(col) == ImagePanel.class){
    	  if(value == null){
    		  ((ImagePanel) data[row][col]).setImageFile((File) value);      		  
    	  }else if(value.getClass() == File.class){
    		  ((ImagePanel) data[row][col]).setImageFile((File) value);  
    	  }else if(value.getClass() == ImageIcon.class){
    		  ((ImagePanel) data[row][col]).setImage((ImageIcon) value);  
    	  }
      }else if(getColumnClass(col) == String.class){
        data[row][col] = value;    	  
      }else{
          data[row][col] = value;    	  
        }
      fireTableCellUpdated(row, col);
    }  
}
