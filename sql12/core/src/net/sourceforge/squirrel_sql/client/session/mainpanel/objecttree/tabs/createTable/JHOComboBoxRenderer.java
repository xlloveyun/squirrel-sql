package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.createTable;

import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.util.EventObject;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
 
public class JHOComboBoxRenderer extends JPanel implements TableCellRenderer {
        /**
	 * 
	 */
	private static final long serialVersionUID = 6482235776690619189L;
	
	HOCombobox combobox;
        public JHOComboBoxRenderer(){ 
              super();
              combobox = new HOCombobox();
              combobox.setBackground(Color.white);
              add(combobox);
       } 
        
        public JHOComboBoxRenderer(String... args){ 
              super();
              combobox = new HOCombobox(args);
              combobox.setBackground(Color.white);
              add(combobox);
       }
 
     public Component getTableCellRendererComponent(JTable table, Object value, 
          boolean isSelected, boolean hasFocus, int row, int column) { 
          if(isSelected){ 
              setForeground(table.getForeground()); 
               super.setBackground(table.getBackground()); 
          }else{ 
               setForeground(table.getForeground()); 
               setBackground(table.getBackground()); 
         } 
          combobox.setSelectedItem(value);
             return combobox; 
        }
     
   


}
