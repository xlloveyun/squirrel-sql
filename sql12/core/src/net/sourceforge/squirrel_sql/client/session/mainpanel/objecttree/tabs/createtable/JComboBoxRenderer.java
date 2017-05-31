package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.createtable;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DatabaseTypesDataSet;
 
public class JComboBoxRenderer extends JPanel implements TableCellRenderer {
        /**
	 * 
	 */
	private static final long serialVersionUID = 6482235776690619189L;
	
		Combobox combobox;
        public JComboBoxRenderer(DatabaseTypesDataSet dataset){ 
              super();
              combobox = new Combobox(dataset);
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
