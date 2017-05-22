package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.createTable;

import java.awt.Component;
import java.util.EventObject;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DatabaseTypesDataSet;

public class HOCombobox extends JComboBox<String> implements TableCellEditor{
	
	private static final long serialVersionUID = 1L;

	public HOCombobox() {
		super();
		 addItem("BLOCKCACHE");
		 addItem("BLOCKSIZE");
		 addItem("BLOOMFILTER");
		 addItem("CACHE_BLOOMS_ON_WRITE");
		 addItem("CACHE_DATA_ON_WRITE");
		 addItem("CACHE_INDEXES_ON_WRITE");
		 addItem("COMPACT");
		 addItem("COMPACT_COMPRESSION");
		 addItem("COMPRESSION");
		 addItem("DATA_BLOCK_ENCODING");
		 addItem("DURABILITY");
		 addItem("EVICT_BLOCKS_ON_CLOSE");
		 addItem("IN_MEMORY");
		 addItem("KEEP_DELETED_CELLS");
		 addItem("MAX_FILESIZE");
		 addItem("MAX_VERSIONS");
		 addItem("MEMSTORE_FLUSH_SIZE");
		 addItem("MIN_VERSIONS");
		 addItem("MEMSTORE_FLUSH_SIZE");
		 addItem("MIN_VERSIONS");
		 addItem("PREFIX_LENGTH_KEY");
		 addItem("REPLICATION_SCOPE");
		 addItem("SPLIT_POLICY");
		 addItem("TTL");
	}
	
	public HOCombobox(String... args) {
		super();
		for (String string : args) {
			addItem(string);
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
