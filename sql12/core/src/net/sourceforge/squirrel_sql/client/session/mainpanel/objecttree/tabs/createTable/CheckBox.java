package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.createTable;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

public class CheckBox extends JCheckBox implements TableCellEditor{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CheckBox(){
		super();
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
		return this;
	}

}
