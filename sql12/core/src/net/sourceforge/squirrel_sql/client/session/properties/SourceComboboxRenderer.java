package net.sourceforge.squirrel_sql.client.session.properties;

import java.awt.Color;
import java.awt.Component;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.createtable.Combobox;

public class SourceComboboxRenderer extends JPanel implements TableCellRenderer {
	
	private static final long serialVersionUID = 6482235776690619189L;

	SourceCombobox combobox ;
	
	public SourceComboboxRenderer(List<String> list){
		super();
        combobox = new SourceCombobox(list);
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
