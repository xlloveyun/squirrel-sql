package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.createtable;

import java.awt.Component;
import java.util.EventObject;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DatabaseTypesDataSet;

public class Combobox extends JComboBox<String> implements TableCellEditor{
	
	private static final long serialVersionUID = 1L;

	public Combobox(DatabaseTypesDataSet dataSet) {
		super();
		 List<Object[]> _allData =dataSet._allData;
		 for (Object[] objects : _allData) {
			 if("INTERVAL".equals(objects[0])){
				 String str = (String) objects[4];
				 str=str.substring(2,str.length()-1);
				 addItem("INTERVAL "+str);
				 continue;
			 }
			 addItem((String) objects[0]);
		}
	}

	@Override
	public void addCellEditorListener(CellEditorListener arg0) {
		
	}

	@Override
	public void cancelCellEditing() {
		
		
	}

	@Override
	public Object getCellEditorValue() {
		
		return null;
	}

	@Override
	public boolean isCellEditable(EventObject arg0) {
		
		return false;
	}

	@Override
	public void removeCellEditorListener(CellEditorListener arg0) {
		
		
	}

	@Override
	public boolean shouldSelectCell(EventObject arg0) {
		
		return false;
	}

	@Override
	public boolean stopCellEditing() {
		
		return false;
	}

	@Override
	public Component getTableCellEditorComponent(JTable arg0, Object arg1, boolean arg2, int arg3, int arg4) {
		
		return null;
	}
	
}
