package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.createtable;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

 
public class JCheckBoxRenderer extends JPanel implements TableCellRenderer {
        /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
		CheckBox check_box;
        public JCheckBoxRenderer(){ 
              super();
              check_box = new CheckBox();
              check_box.setBackground(Color.white);
              add(check_box);
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
    	 if(value!=null&&"true".equals(value.toString())){
    		 check_box.setSelected(true);
    	 }else{
    		 check_box.setSelected(false);
    	 }
             return check_box; 
        }
     
}
