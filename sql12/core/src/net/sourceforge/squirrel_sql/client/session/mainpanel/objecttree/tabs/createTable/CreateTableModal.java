package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.createTable;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformator;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformatorConfigFactory;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DatabaseTypesDataSet;
import javax.swing.ScrollPaneConstants;
import javax.swing.ScrollPaneLayout;


public class CreateTableModal extends JScrollPane {

	private static final long serialVersionUID = 1L;
	
	
	private  JScrollPane scrollPane;
	private  JTable columnTable;
	private  JTable hBaseOptionTable;
	private  JTable pkTable;
	private  JTextField tnTextField;
	private  JTextField snTextField;
    private ISession session;
    private DefaultTableModel columnTableModel;
    private DefaultTableModel hBaseOptionTableModel;
    private DefaultTableModel pkTableModel;
    private String connectionName;
    private String schemaName;
    private int index=0;
    List<String> list =new ArrayList<String>();
    List<String> colList =new ArrayList<String>();
    
	public CreateTableModal(ISession session,String connectionName, String schemaName) {
		this.session=session;
		this.connectionName=connectionName;
		this.schemaName=schemaName;
		initialize();
	}

	public Component getTableComponent(){
		return scrollPane;
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private JScrollPane initialize() {
		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setLayout(null);
		try {
			tableDraw();
			hBaseOptionTableDraw();
			primaryKeyTableDraw();
			hBaseOptionTableModel.addRow(new String[]{});
			addCol();
		} catch (DataSetException e1) {
			e1.printStackTrace();
		}
		scrollPane.add(getTableInfo());
		scrollPane.add(getJTabbedPane());
		return scrollPane;
	}
	
	

	private JTabbedPane getJTabbedPane(){
		JTabbedPane jTabbedPane = new JTabbedPane();
		jTabbedPane.setBounds(10, 120,850,350);
		
		JPanel hoPan =getHbaseOptionPan();		
		JPanel columnPan= getColumnPan();
		JPanel pkPan = getPrimaryKeykPan();
		
		jTabbedPane.addTab("Columns",columnPan);
		jTabbedPane.addTab("HBase Option",hoPan);
		jTabbedPane.addTab("Primary Key",pkPan);
		
		jTabbedPane.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if(columnTable.isEditing())
					columnTable.getCellEditor().stopCellEditing();
				if(jTabbedPane.getSelectedIndex()==2){
					pkTableModel.setRowCount(0);
						for (String string : list) {
							String[] str = {string};
							pkTableModel.addRow(str);
						}
					}
				}
		});
		return jTabbedPane;
	}

	
	private JPanel getPrimaryKeykPan() {
		JPanel pkPan = new JPanel();
		JScrollPane tableScrollPane= new JScrollPane(pkTable);
		tableScrollPane.setBounds(20, 43, 296, 254);
		JButton upBtn = new JButton("Up");
		upBtn.setBounds(20, 10, 95, 23);
		upBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				int selectedRow = pkTable.getSelectedRow();
				if(selectedRow>0){
					String cacheColumnName = (String) pkTableModel.getValueAt(selectedRow-1, 0);
					pkTableModel.setValueAt(pkTableModel.getValueAt(selectedRow, 0),selectedRow-1, 0);
					pkTableModel.setValueAt(cacheColumnName,selectedRow, 0);
					list.set(selectedRow-1, list.get(selectedRow));
					list.set(selectedRow,cacheColumnName);
				}
			}
		});
		pkPan.setLayout(null);
		pkPan.add(upBtn);
		
		JButton downBtn = new JButton("DOWN");
		downBtn.setBounds(122, 10, 95, 23);
		downBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				int selectedRow = pkTable.getSelectedRow();
				if(selectedRow<pkTableModel.getRowCount()-1&&selectedRow>=0){
					String cacheColumnName = (String) pkTableModel.getValueAt(selectedRow, 0);
					pkTableModel.setValueAt(pkTableModel.getValueAt(selectedRow+1, 0),selectedRow, 0);
					pkTableModel.setValueAt(cacheColumnName,selectedRow+1, 0);
					list.set(selectedRow, list.get(selectedRow+1));
					list.set(selectedRow+1,cacheColumnName);
				}
			}
		});
		pkPan.add(downBtn);
		
		JButton downCancel = new JButton("Cancel");
		downCancel.setBounds(221, 10, 95, 23);
		downCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int selectedRow = pkTable.getSelectedRow();
				String value = (String) pkTableModel.getValueAt(selectedRow, 0);
				list.remove(selectedRow);
				pkTableModel.removeRow(selectedRow);
				for (int i = 0; i < columnTableModel.getRowCount(); i++) {
					if(value.equals(columnTableModel.getValueAt(i, 0))){
						columnTableModel.setValueAt(false,i,5);
						columnTableModel.setValueAt(false,i,6);
					}
				}
			}
		});
		pkPan.add(downCancel);
		pkPan.add(tableScrollPane);
		return pkPan;
	}

	private JPanel getHbaseOptionPan() {
		JPanel hBaseOptPan = new JPanel();
		JScrollPane tableScrollPane= new JScrollPane(hBaseOptionTable);
		tableScrollPane.setBounds(20, 43, 800, 254);
		JButton upBtn = new JButton("ADD");
		upBtn.setBounds(20, 10, 95, 23);
		upBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Object[] rowValues = {};
				hBaseOptionTableModel.addRow(rowValues); // 添加一行
			}
		});
		hBaseOptPan.setLayout(null);
		hBaseOptPan.add(upBtn);
		
		JButton downBtn = new JButton("DELETE");
		downBtn.setBounds(122, 10, 95, 23);
		downBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int selectedRow = hBaseOptionTable.getSelectedRow();// 获得选中行的索引
				if (selectedRow != -1) // 存在选中行
				{
					hBaseOptionTableModel.removeRow(selectedRow); // 删除行
				}
			}
		});
//		hBaseOptionTable.getModel().addTableModelListener(new TableModelListener() {
//			
//			@Override
//			public void tableChanged(TableModelEvent arg0) {
//				if(arg0.getType()==TableModelEvent.UPDATE&&arg0.getColumn()==0){
//					int row = hBaseOptionTable.getSelectedRow();
//					setValue(hBaseOptionTable, hBaseOptionTableModel.getValueAt(row, 0),row);
//				}
//			}
//		});
		
		hBaseOptPan.add(downBtn);
		hBaseOptPan.add(tableScrollPane);
		return hBaseOptPan;
	}

	private JPanel getColumnPan() {
		JScrollPane tableScrollPane= new JScrollPane(columnTable);
		JPanel columnPan =new JPanel();
		tableScrollPane.setBounds(10, 24, 825, 297);
		columnPan.setLayout(null);
		columnPan.setBounds(10, 100,100,100);
		JButton btnNewButton = new JButton("ADD");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addCol();
			}
		});
		columnPan.add(btnNewButton);
		columnPan.add(tableScrollPane);
		JButton btnDelete = new JButton("DELETE");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedRow = columnTable.getSelectedRow();// 获得选中行的索引
				if (selectedRow != -1) // 存在选中行
				{
					colList.remove(selectedRow);
					if(list.contains(columnTableModel.getValueAt(selectedRow, 0)))
						list.remove(list.indexOf(columnTableModel.getValueAt(selectedRow, 0)));
					columnTableModel.removeRow(selectedRow); // 删除行
					
				}
			}
		});
		btnNewButton.setBounds(10, 0, 95, 23);
		btnDelete.setBounds(119, 0, 95,23);
		columnPan.add(btnDelete);
		return columnPan;
	}

	private JPanel getTableInfo(){
		JPanel panel;
		JLabel dbConLabel= new JLabel("DataBase Connection :");
		JLabel snLabel= new JLabel("Schema Name");
		JLabel tbLabel= new JLabel("Table Name ");
		JTextField dbName= new JTextField();
		panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Table Info", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(10, 10, 850, 110);
		panel.setLayout(null);
		
		

		dbName.setText(connectionName);
		dbName.setEditable(false);
		dbName.setBounds(242, 17, 258, 21);
		dbName.setColumns(10);

		snLabel.setBounds(39, 46, 122, 15);
		dbConLabel.setBounds(39, 20, 144, 15);
		tbLabel.setBounds(39, 72, 122, 15);
		
		snTextField = new JTextField();
		snTextField.setText(schemaName);
		snTextField.setBounds(242, 43, 258, 21);
		snTextField.setColumns(10);
		
		tnTextField = new JTextField(); 
		tnTextField.setBounds(242, 69, 258, 21);
		tnTextField.setColumns(10);

		JButton btnExecute = new JButton("EXECUTE");
		btnExecute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toSql();
				excuteSql();
			}
		});
		btnExecute.setBounds(747, 68, 93, 23);
		
		JButton btnToSql = new JButton("VIEW SQL");
		btnToSql.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toSql();
			}
		});
		btnToSql.setBounds(640, 68, 93, 23);
		
		panel.add(snLabel);
		panel.add(dbConLabel);
		panel.add(dbName);
		panel.add(tbLabel);
		panel.add(snTextField);
		panel.add(tnTextField);
		panel.add(btnExecute);
		panel.add(btnToSql);
		
		return panel;
	}
	
	private void tableDraw() throws DataSetException {
		String[] columnNames = { "Column Name", "Data Type", "Size","Scale", "Default Value","PK","NN",
		"Column Description" };
		Object[][] tableVales = {};
		columnTableModel = new DefaultTableModel(tableVales, columnNames){
			@Override
			public void fireTableCellUpdated(int row, int column) {
				if(columnTableModel.getValueAt(row, column) == null)
				{
					return ;
				}else{
					if(column==0){
						if(list.contains(colList.get(row))){
							list.remove(colList.get(row));
//							pkTableModel.removeRow(arg0);
						}
						colList.set(row, (String) columnTableModel.getValueAt(row, column));
					}
					if(columnTableModel.getValueAt(row, 5)!=null&&(boolean)columnTableModel.getValueAt(row, 5)){
						
						if(!list.contains((String)columnTableModel.getValueAt(row, 0))){
							list.add((String) columnTableModel.getValueAt(row, 0));
						}
					}else{
						if(list.contains((String)columnTableModel.getValueAt(row, 0))){
							list.remove(((String)columnTableModel.getValueAt(row, 0)));
						}
					}
				}
				super.fireTableCellUpdated(row, column);
			}
			
			
		};
		columnTable = new JTable(columnTableModel);
		columnTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		TableColumnModel tcm =columnTable.getColumnModel();
		TableColumn tc0 = tcm.getColumn(0);
		TableColumn tc1 = tcm.getColumn(1);
		TableColumn tc2 = tcm.getColumn(2);
		TableColumn tc3 = tcm.getColumn(3);
		TableColumn tc4 = tcm.getColumn(4);
		TableColumn tc5 = tcm.getColumn(5);
		TableColumn tc6 = tcm.getColumn(6);
		TableColumn tc7 = tcm.getColumn(7);
		tc0.setMinWidth(80);
		tc1.setMinWidth(150);
		tc2.setMaxWidth(40);
		tc3.setMaxWidth(40);
		tc4.setMaxWidth(100);
		tc5.setMaxWidth(30);
		tc6.setMaxWidth(30);
		tc7.setMinWidth(240);
		tc1.setCellRenderer(new JComboBoxRenderer((DatabaseTypesDataSet) session.getSQLConnection().getSQLMetaData().getTypesDataSet()));
		tc1.setCellEditor(new DefaultCellEditor(new Combobox((DatabaseTypesDataSet) session.getSQLConnection().getSQLMetaData().getTypesDataSet())));
		tc5.setCellRenderer(new JCheckBoxRenderer());
		tc5.setCellEditor(new DefaultCellEditor(new CheckBox()));
		tc6.setCellRenderer(new JCheckBoxRenderer());
		tc6.setCellEditor(new DefaultCellEditor(new CheckBox()));
		columnTable.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		columnTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		columnTable.setRowHeight(20);
	}
	
	private void hBaseOptionTableDraw() throws DataSetException {
		String[] columnNames = { "HBase Option", "Values"};
		Object[][] tableVales = {};
		hBaseOptionTableModel= new DefaultTableModel(tableVales, columnNames);
		hBaseOptionTable = new JTable(hBaseOptionTableModel);
		hBaseOptionTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		TableColumnModel tcm =hBaseOptionTable.getColumnModel();
		TableColumn tc0 = tcm.getColumn(0);
//		TableColumn tc1 = tcm.getColumn(1);
//		tc1.setCellRenderer(new JHOComboBoxRenderer("true","false"));
//		tc1.setCellEditor(new DefaultCellEditor(new HOCombobox("true","false")));
		tc0.setCellRenderer(new JHOComboBoxRenderer());
		tc0.setCellEditor(new DefaultCellEditor(new HOCombobox()));
		hBaseOptionTable.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		hBaseOptionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		hBaseOptionTable.setRowHeight(20);
	}
	
	private void primaryKeyTableDraw() {

		String[] columnNames = { "Columns Name"};
		Object[][] tableVales = {};
		pkTableModel= new DefaultTableModel(tableVales, columnNames);
		pkTable = new JTable(pkTableModel);
		pkTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		pkTable.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		pkTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pkTable.setRowHeight(20);
	}
	
	private void toSql(){
		StringBuffer mainsb =new StringBuffer();
		StringBuffer pkSb =new StringBuffer();
		StringBuffer hoSb =new StringBuffer();
		StringBuffer commentSb =new StringBuffer();
		mainsb.append("CREATE TABLE ");
		pkSb.append("PRIMARY KEY (");
		hoSb.append("HBASE_OPTIONS(");
		mainsb.append(snTextField.getText()+"."+tnTextField.getText()+" (");
		for (int i = 0; i < columnTableModel.getRowCount(); i++) {
			mainsb.append(columnTableModel.getValueAt(i, 0)+" "+columnTableModel.getValueAt(i, 1));
			if(columnTableModel.getValueAt(i, 2)!=null&&!"".equals(columnTableModel.getValueAt(i, 2))){
				mainsb.append("("+columnTableModel.getValueAt(i, 2)+")");
			}
			if(columnTableModel.getValueAt(i, 3)!=null&&!"".equals(columnTableModel.getValueAt(i, 3))){
				mainsb.deleteCharAt(mainsb.length()-1);
				mainsb.append(","+columnTableModel.getValueAt(i, 3)+")");
			}
			if(columnTableModel.getValueAt(i, 4)!=null&&!"".equals(columnTableModel.getValueAt(i, 4))){
				mainsb.append(" DEFAULT '"+columnTableModel.getValueAt(i, 4)+"'");
			}
			if(columnTableModel.getValueAt(i, 6)!=null&&(boolean)columnTableModel.getValueAt(i, 6)){
				mainsb.append(" NOT NULL ");
			}
			if(columnTableModel.getValueAt(i, 7)!=null&&!"".equals(columnTableModel.getValueAt(i, 7))){
//				commentSb.append(" comment on column'"+columnTableModel.getValueAt(i, 7)+"'");
				commentSb.append("comment on column "+snTextField.getText()+"."+tnTextField.getText()+"."+columnTableModel.getValueAt(i, 0)+" is '" +columnTableModel.getValueAt(i, 7)+"';");
			}
			mainsb.append(",");
		}
		for (int i = 0; i < pkTableModel.getRowCount(); i++) {
			pkSb.append(pkTableModel.getValueAt(i, 0)+",");
		}
		pkSb.deleteCharAt(pkSb.length()-1);
		
		for (int i = 0; i < hBaseOptionTableModel.getRowCount(); i++) {
			if (hBaseOptionTableModel.getValueAt(i, 0) != null && hBaseOptionTableModel.getValueAt(i, 1) != null
					&& !"".equals(hBaseOptionTableModel.getValueAt(i, 0))
					&& !"".equals(hBaseOptionTableModel.getValueAt(i, 0)))
				hoSb.append(hBaseOptionTableModel.getValueAt(i, 0)+"='"+hBaseOptionTableModel.getValueAt(i, 1)+"',");
		}
		hoSb.deleteCharAt(hoSb.length()-1);
		
		if(pkSb.length()>13){
			mainsb.append(pkSb);
			mainsb.append(")");
		}
		mainsb.append(")");
		if(hoSb.length()>14){
			mainsb.append(hoSb);
			mainsb.append(")");
		}
		mainsb.append(";");
		mainsb.append(commentSb);
		session.getSQLPanelAPIOfActiveSessionWindow().getSQLEntryPanel().setText(mainsb.toString());
		formatSql();
		session.selectMainTab(1);
		try {
			session.getSQLConnection().getSQLMetaData().getTypesDataSet();
		} catch (DataSetException e) {
			e.printStackTrace();
		}
	}
	
	private void excuteSql(){
		session.getSQLPanelAPIOfActiveSessionWindow().executeCurrentSQL();	
	}

	private void formatSql() {
		ISQLEntryPanel panel = session.getSQLPanelAPIOfActiveSessionWindow().getSQLEntryPanel();
	      int[] bounds = panel.getBoundsOfSQLToBeExecuted();

	      if(bounds[0] == bounds[1]){
	         return;
	      }

	      String textToReformat = panel.getSQLToBeExecuted();

			if (null == textToReformat){
				return;
			}

			CodeReformator cr = new CodeReformator(CodeReformatorConfigFactory.createConfig(session));

			String reformatedText = cr.reformat(textToReformat);

			panel.setSelectionStart(bounds[0]);
			panel.setSelectionEnd(bounds[1]);
			panel.replaceSelection(reformatedText);
	}
	
	private void addCol() {
		String coln = "col"+index++;
		Object[] rowValues = {coln};
		colList.add(coln);
		columnTableModel.addRow(rowValues); // 添加一行
	}
	
	
//	private void setValue(JTable table, Object value, int row) {
//	TableColumn tc=table.getColumnModel().getColumn(1);
////	table.getCe
//	if(value==null)
//		return ;
//	switch (value.toString()) {
//	case "BLOCKCACHE":
//		tc.setCellRenderer(new JHOComboBoxRenderer("true","false"));
//		
//		tc.setCellEditor(new DefaultCellEditor(new HOCombobox("true","false")));
//		hBaseOptionTableModel.setValueAt("",row,1);
//		break;
//	case "BLOCKSIZE":
//		tc.setCellRenderer(null);
//		tc.setCellEditor(new DefaultCellEditor(new JTextField()));
//		hBaseOptionTableModel.setValueAt("",row,1);
//		break;
//	case "BLOOMFILTER":
//		tc.setCellRenderer(new JHOComboBoxRenderer("NONE","ROW","ROWCOL"));
//		tc.setCellEditor(new DefaultCellEditor(new HOCombobox("NONE","ROW","ROWCOL")));
//		hBaseOptionTableModel.setValueAt("",row,1);
//		break;
//	case "CACHE_BLOOMS_ON_WRITE":
//		tc.setCellRenderer(new JHOComboBoxRenderer("true","false"));
//		tc.setCellEditor(new DefaultCellEditor(new HOCombobox("true","false")));
//		break;
//	case "CACHE_DATA_ON_WRITE":
//		tc.setCellRenderer(new JHOComboBoxRenderer("true","false"));
//		tc.setCellEditor(new DefaultCellEditor(new HOCombobox("true","false")));
//		hBaseOptionTableModel.setValueAt("",row,1);
//		break;
//	case "CACHE_INDEXES_ON_WRITE":
//		tc.setCellRenderer(new JHOComboBoxRenderer("true","false"));
//		tc.setCellEditor(new DefaultCellEditor(new HOCombobox("true","false")));
//		hBaseOptionTableModel.setValueAt("",row,1);
//		break;
//	case "COMPACT":
//		tc.setCellRenderer(new JHOComboBoxRenderer("true","false"));
//		tc.setCellEditor(new DefaultCellEditor(new HOCombobox("true","false")));
//		hBaseOptionTableModel.setValueAt("",row,1);
//		break;
//	case "COMPACT_COMPRESSION":
//		tc.setCellRenderer(new JHOComboBoxRenderer("GZ","LZ4","LZO","NONE","SNAPPY"));
//		tc.setCellEditor(new DefaultCellEditor(new HOCombobox("GZ","LZ4","LZO","NONE","SNAPPY")));
//		hBaseOptionTableModel.setValueAt("",row,1);
//		break;
//	case "COMPRESSION":
//		tc.setCellRenderer(new JHOComboBoxRenderer("GZ","LZ4","LZO","NONE","SNAPPY"));
//		tc.setCellEditor(new DefaultCellEditor(new HOCombobox("GZ","LZ4","LZO","NONE","SNAPPY")));
//		hBaseOptionTableModel.setValueAt("",row,1);
//		break;
//	case "DATA_BLOCK_ENCODING":
//		tc.setCellRenderer(new JHOComboBoxRenderer("DIFF","FAST_DIFF","NONE","PREFIX"));
//		tc.setCellEditor(new DefaultCellEditor(new HOCombobox("DIFF","FAST_DIFF","NONE","PREFIX")));
//		hBaseOptionTableModel.setValueAt("",row,1);
//		break;
//	case "DURABILITY":
//		tc.setCellRenderer(new JHOComboBoxRenderer("USE_DEFAULT","SKIP_WAL","ASYNC_WAL","SYNC_WAL","FSYNC_WAL"));
//		tc.setCellEditor(new DefaultCellEditor(new HOCombobox("USE_DEFAULT","SKIP_WAL","ASYNC_WAL","SYNC_WAL","FSYNC_WAL")));
//		hBaseOptionTableModel.setValueAt("",row,1);
//		break;
//	case "EVICT_BLOCKS_ON_CLOSE":
//		tc.setCellRenderer(new JHOComboBoxRenderer("true","false"));
//		tc.setCellEditor(new DefaultCellEditor(new HOCombobox("true","false")));
//		hBaseOptionTableModel.setValueAt("",row,1);
//		break;
//	case "IN_MEMORY":
//		tc.setCellRenderer(new JHOComboBoxRenderer("true","false"));
//		tc.setCellEditor(new DefaultCellEditor(new HOCombobox("true","false")));
//		hBaseOptionTableModel.setValueAt("",row,1);
//		break;
//	case "KEEP_DELETED_CELLS":
//		tc.setCellRenderer(new JHOComboBoxRenderer("true","false"));
//		tc.setCellEditor(new DefaultCellEditor(new HOCombobox("true","false")));
//		hBaseOptionTableModel.setValueAt("",row,1);
//		break;
//	case "MAX_FILESIZE":
//		tc.setCellRenderer(null);
//		tc.setCellEditor(new DefaultCellEditor(new JTextField()));
//		hBaseOptionTableModel.setValueAt("",row,1);
//		break;
//	case "MAX_VERSIONS":
//		tc.setCellRenderer(null);
//		tc.setCellEditor(new DefaultCellEditor(new JTextField()));
//		hBaseOptionTableModel.setValueAt("",row,1);
//		break;
//
//	case "MEMSTORE_FLUSH_SIZE":
//		tc.setCellRenderer(null);
//		tc.setCellEditor(new DefaultCellEditor(new JTextField()));
//		hBaseOptionTableModel.setValueAt("",row,1);
//		break;
//
//	case "MIN_VERSIONS":
//		tc.setCellRenderer(null);
//		tc.setCellEditor(new DefaultCellEditor(new JTextField()));
//		hBaseOptionTableModel.setValueAt("",row,1);
//		break;
//
//	case "PREFIX_LENGTH_KEY":
//		tc.setCellRenderer(null);
//		tc.setCellEditor(new DefaultCellEditor(new JTextField()));
//		hBaseOptionTableModel.setValueAt("",row,1);
//		break;
//	case "REPLICATION_SCOPE":
//		tc.setCellRenderer(new JHOComboBoxRenderer("0","1"));
//		tc.setCellEditor(new DefaultCellEditor(new HOCombobox("0","1")));
//		hBaseOptionTableModel.setValueAt("",row,1);
//		break;
//	case "SPLIT_POLICY":
//		tc.setCellRenderer(new JHOComboBoxRenderer("org.apache.hadoop.hbase.regionserver.ConstantSizeRegionSplitPolicy",
//				"org.apache.hadoop.hbase.regionserver.IncreasingToUpperBoundRegionSplitPolicy",
//				"org.apache.hadoop.hbase.regionserver.KeyPrefixRegionSplitPolicy"));
//		tc.setCellEditor(new DefaultCellEditor(new HOCombobox("org.apache.hadoop.hbase.regionserver.ConstantSizeRegionSplitPolicy",
//				"org.apache.hadoop.hbase.regionserver.IncreasingToUpperBoundRegionSplitPolicy",
//				"org.apache.hadoop.hbase.regionserver.KeyPrefixRegionSplitPolicy")));
//		hBaseOptionTableModel.setValueAt("",row,1);
//		break;
//	case "TTL":
//		tc.setCellRenderer(null);
//		tc.setCellEditor(new DefaultCellEditor(new JTextField()));
//		hBaseOptionTableModel.setValueAt("",row,1);
//		break;
//
//	default:
//		tc.setCellRenderer(null);
//		tc.setCellEditor(new DefaultCellEditor(new JTextField()));
//		hBaseOptionTableModel.setValueAt("",row,1);
//		break;
//	}
//} 
}
