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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.SmallTabButton;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformator;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformatorConfigFactory;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DatabaseTypesDataSet;

import javax.swing.ScrollPaneConstants;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.ImageIcon;

public class CreateTableModal extends JScrollPane {

	private static final long serialVersionUID = 1L;

	private JScrollPane scrollPane;
	private JTable columnTable;
	private JTable hBaseOptionTable;
	private JTable pkTable;
	private JTextField tnTextField;
	private JTextField snTextField;
	private ISession session;
	private IApplication _app;
	private DefaultTableModel columnTableModel;
	private DefaultTableModel hBaseOptionTableModel;
	private DefaultTableModel pkTableModel;
	private String connectionName;
	private String schemaName;
	private int index = 0;
	private JRadioButton storeRadio;
	private JRadioButton pkRadio;
	private JCheckBox isPartitionCheckBox ;
	private JTextField partitionNum ;
	List<Object[]> pklist = new ArrayList<Object[]>();
	List<String> colList = new ArrayList<String>();
	List<String> storeList = new ArrayList<String>();
	final SquirrelResources rsrc;

	public CreateTableModal(IApplication app, ISession session, String connectionName, String schemaName) {
		this._app = app;
		this.session = session;
		this.connectionName = connectionName;
		this.schemaName = schemaName;
		rsrc= _app.getResources();
		initialize();
	}

	public Component getTableComponent() {
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
			addCol();
		} catch (DataSetException e1) {
			e1.printStackTrace();
		}
		scrollPane.add(getTableInfo());
		scrollPane.add(getJTabbedPane());
		return scrollPane;
	}

	private JTabbedPane getJTabbedPane() {
		JTabbedPane jTabbedPane = new JTabbedPane();
		jTabbedPane.setBounds(10, 120, 900, 350);

		JPanel hoPan = getHbaseOptionPan();
		JPanel columnPan = getColumnPan();
		JPanel pkPan = getPrimaryKeykPan();

		jTabbedPane.addTab("Columns", columnPan);
		jTabbedPane.addTab("HBase Option", hoPan);
		jTabbedPane.addTab("PK/STORE", pkPan);

		jTabbedPane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (columnTable.isEditing())
					columnTable.getCellEditor().stopCellEditing();
				if (jTabbedPane.getSelectedIndex() == 2) {
					pkTableModel.setRowCount(0);
					for (Object[] string : pklist) {
						pkTableModel.addRow(string);
					}
				}
			}
		});
		return jTabbedPane;
	}

	private JPanel getPrimaryKeykPan() {
		JPanel pkPan = new JPanel();
		JScrollPane tableScrollPane = new JScrollPane(pkTable);
		tableScrollPane.setBounds(0, 35, 553, 286);
		final ImageIcon upIcon = rsrc.getIcon(SquirrelResources.IImageNames.ARROW_UP);
		SmallTabButton upBtn = new SmallTabButton("Move the column up", upIcon);
		upBtn.setBounds(0, 10, 18, 18);
		upBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				int selectedRow = pkTable.getSelectedRow();
				if (selectedRow > 0) {
					Object[] cacheColumnName =new Object[]{pkTableModel.getValueAt(selectedRow - 1, 0),pkTableModel.getValueAt(selectedRow - 1, 1)};
					pkTableModel.setValueAt(pkTableModel.getValueAt(selectedRow, 0), selectedRow - 1, 0);
					pkTableModel.setValueAt(pkTableModel.getValueAt(selectedRow, 1), selectedRow - 1, 1);
					pkTableModel.setValueAt(cacheColumnName[0], selectedRow, 0);
					pkTableModel.setValueAt(cacheColumnName[1], selectedRow, 1);
					pklist.set(selectedRow - 1,pklist.get(selectedRow));
					pklist.set(selectedRow,cacheColumnName);
				}
			}
		});
		pkPan.setLayout(null);
		pkPan.add(upBtn);

		final ImageIcon downIcon = rsrc.getIcon(SquirrelResources.IImageNames.ARROW_DOWN);
		SmallTabButton downBtn = new SmallTabButton("Move the column down", downIcon);
		downBtn.setBounds(61, 10, 18, 18);
		downBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				int selectedRow = pkTable.getSelectedRow();
				if (selectedRow < pkTableModel.getRowCount() - 1 && selectedRow >= 0) {
					Object[] cacheColumnName = new Object[]{pkTableModel.getValueAt(selectedRow, 0),pkTableModel.getValueAt(selectedRow, 1)};
					pkTableModel.setValueAt(pkTableModel.getValueAt(selectedRow + 1, 0), selectedRow, 0);
					pkTableModel.setValueAt(pkTableModel.getValueAt(selectedRow + 1, 1), selectedRow, 1);
					pkTableModel.setValueAt(cacheColumnName[0], selectedRow + 1, 0);
					pkTableModel.setValueAt(cacheColumnName[1], selectedRow + 1, 1);
					pklist.set(selectedRow, pklist.get(selectedRow + 1));
					pklist.set(selectedRow + 1,cacheColumnName);
				}
			}
		});
		pkPan.add(downBtn);
		final ImageIcon deleteIcon = rsrc.getIcon(SquirrelResources.IImageNames.CLOSE);
		SmallTabButton deleteBtn = new SmallTabButton("delete this pk/store", deleteIcon);
		deleteBtn.setBounds(120, 10, 18, 18);
		deleteBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int selectedRow = pkTable.getSelectedRow();
				if(selectedRow<0)
					return ;
				String value = (String) pkTableModel.getValueAt(selectedRow, 0);
				pklist.remove(selectedRow);
				pkTableModel.removeRow(selectedRow);
				for (int i = 0; i < columnTableModel.getRowCount(); i++) {
					if (value.equals(columnTableModel.getValueAt(i, 0))) {
						columnTableModel.setValueAt(false, i, 5);
						columnTableModel.setValueAt(false, i, 6);
					}
				}
			}
		});
		pkPan.add(deleteBtn);
		pkPan.add(tableScrollPane);

		partitionNum = new JTextField();
		partitionNum.setColumns(10);
		partitionNum.setBounds(377, 10, 176, 21);
		pkPan.add(partitionNum);

		JLabel lblPartitionNum = new JLabel("Partition Num :");
		lblPartitionNum.setBounds(270, 10, 97, 15);
		pkPan.add(lblPartitionNum);

		isPartitionCheckBox = new JCheckBox("Is Partition");
		isPartitionCheckBox.setBounds(156, 10, 97, 23);
		pkPan.add(isPartitionCheckBox);
		return pkPan;
	}

	private JPanel getHbaseOptionPan() {
		JPanel hBaseOptPan = new JPanel();
		JScrollPane tableScrollPane = new JScrollPane(hBaseOptionTable);
		tableScrollPane.setBounds(0, 29, 696, 292);
		final ImageIcon addIcon = rsrc.getIcon(SquirrelResources.IImageNames.ADD);
		SmallTabButton addBtn = new SmallTabButton("Add new column", addIcon);
		addBtn.setBounds(0, 10, 19, 19);
		addBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Object[] rowValues = {};
				hBaseOptionTableModel.addRow(rowValues); // 添加一行
			}
		});
		hBaseOptPan.setLayout(null);
		hBaseOptPan.add(addBtn);

		final ImageIcon delIcon = rsrc.getIcon(SquirrelResources.IImageNames.DELETE);
		SmallTabButton delBtn = new SmallTabButton("Del selected column", delIcon);
		delBtn.setBounds(60, 10, 19, 19);
		delBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int selectedRow = hBaseOptionTable.getSelectedRow();// 获得选中行的索引
				if (selectedRow != -1) // 存在选中行
				{
					hBaseOptionTableModel.removeRow(selectedRow); // 删除行
				}
			}
		});
		hBaseOptPan.add(delBtn);
		hBaseOptPan.add(tableScrollPane);
		return hBaseOptPan;
	}

	private JPanel getColumnPan() {
		JScrollPane tableScrollPane = new JScrollPane(columnTable);
		JPanel columnPan = new JPanel();
		tableScrollPane.setBounds(0, 32, 895, 289);
		columnPan.setLayout(null);
		columnPan.setBounds(10, 100, 100, 100);
		final ImageIcon addIcon = rsrc.getIcon(SquirrelResources.IImageNames.ADD);
		SmallTabButton addBtn = new SmallTabButton("Add new column", addIcon);
		addBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addCol();
			}
		});
		columnPan.add(addBtn);
		columnPan.add(tableScrollPane);
		final ImageIcon delIcon = rsrc.getIcon(SquirrelResources.IImageNames.DELETE);
		SmallTabButton delBtn = new SmallTabButton("Del selected column", delIcon);
		delBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedRow = columnTable.getSelectedRow();// 获得选中行的索引
				if (selectedRow != -1) // 存在选中行
				{
					colList.remove(selectedRow);
					if (pklist.contains(new Object[]{columnTableModel.getValueAt(selectedRow, 0),columnTableModel.getValueAt(selectedRow, 1)}))
						pklist.remove(new Object[]{columnTableModel.getValueAt(selectedRow, 0),columnTableModel.getValueAt(selectedRow, 1)});
					columnTableModel.removeRow(selectedRow); // 删除行

				}
			}
		});
		addBtn.setBounds(0, 10, 19, 19);
		delBtn.setBounds(60, 10, 19, 19);
		columnPan.add(delBtn);

		pkRadio = new JRadioButton("PK");
		pkRadio.setBounds(570, 10, 44, 23);
		pkRadio.setSelected(true);
		columnPan.add(pkRadio);

		storeRadio = new JRadioButton("Store");
		storeRadio.setBounds(634, 10, 65, 23);
		columnPan.add(storeRadio);
		JLabel tbLabel = new JLabel("please select :");
		tbLabel.setBounds(456, 10, 108, 15);
		columnPan.add(tbLabel);

		pkRadio.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				pkRadio.setSelected(true);
				storeRadio.setSelected(false);
			}
		});
		storeRadio.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				pkRadio.setSelected(false);
				storeRadio.setSelected(true);
			}
		});

		return columnPan;
	}

	private JPanel getTableInfo() {
		JPanel panel;
		JLabel dbConLabel = new JLabel("DataBase Connection :");
		JLabel snLabel = new JLabel("Schema Name :");
		JTextField dbName = new JTextField();
		panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Table Info", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(10, 10, 900, 110);
		panel.setLayout(null);

		dbName.setText(connectionName);
		dbName.setEditable(false);
		dbName.setBounds(242, 17, 258, 21);
		dbName.setColumns(10);

		snLabel.setBounds(39, 46, 122, 15);
		dbConLabel.setBounds(39, 20, 144, 15);

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
		btnExecute.setBounds(797, 68, 93, 23);

		JButton btnToSql = new JButton("VIEW SQL");
		btnToSql.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toSql();
			}
		});
		btnToSql.setBounds(690, 68, 93, 23);

		panel.add(snLabel);
		panel.add(dbConLabel);
		panel.add(dbName);
		panel.add(snTextField);
		panel.add(tnTextField);
		panel.add(btnExecute);
		panel.add(btnToSql);

		JLabel lblTableName = new JLabel("Table Name :");
		lblTableName.setBounds(39, 71, 122, 15);
		panel.add(lblTableName);

		return panel;
	}

	private void tableDraw() throws DataSetException {
		String[] columnNames = { "Column Name", "Data Type", "Size", "Scale", "Default Value", "PK/Store", "NN" };
		Object[][] tableVales = {};
		columnTableModel = new DefaultTableModel(tableVales, columnNames) {
			@Override
			public void fireTableCellUpdated(int row, int column) {
				if (columnTableModel.getValueAt(row, column) == null) {
					return;
				} else {
					if (column == 0) {
						for (int i = 0; i < pklist.size(); i++) {
							if((pklist.get(i)[0]).equals(colList.get(row))){
								pklist.remove(i);
							}
						}
						colList.set(row, (String) columnTableModel.getValueAt(row, column));
					}
					if (columnTableModel.getValueAt(row, 5) != null && (boolean) columnTableModel.getValueAt(row, 5)) {
						boolean boo =true;
						for (int i = 0; i < pklist.size(); i++) {
							if((pklist.get(i)[0]).equals(columnTableModel.getValueAt(row, 0))){
								boo= false;
							}
						}
						if(boo)
							pklist.add(new Object[]{columnTableModel.getValueAt(row, 0),true});
					} else {
						for (int i = 0; i < pklist.size(); i++) {
							if((pklist.get(i)[0]).equals(columnTableModel.getValueAt(row, 0))){
								pklist.remove(i);
							}
						}
					}
					if (column == 2 || column == 3) {
						if (!columnTableModel.getValueAt(row, column).toString().matches("[0-9]+")) {
							columnTableModel.setValueAt(null, row, column);
						}
					}
				}
				super.fireTableCellUpdated(row, column);
			}

		};
		columnTable = new JTable(columnTableModel);
		columnTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		TableColumnModel tcm = columnTable.getColumnModel();
		TableColumn tc0 = tcm.getColumn(0);
		TableColumn tc1 = tcm.getColumn(1);
		TableColumn tc2 = tcm.getColumn(2);
		TableColumn tc3 = tcm.getColumn(3);
		TableColumn tc4 = tcm.getColumn(4);
		TableColumn tc5 = tcm.getColumn(5);
		TableColumn tc6 = tcm.getColumn(6);
		tc0.setMinWidth(150);
		tc1.setMinWidth(160);
		tc2.setMaxWidth(60);
		tc3.setMaxWidth(60);
		tc4.setMinWidth(120);
		tc5.setMaxWidth(80);
		tc6.setMaxWidth(30);
		tc1.setCellRenderer(new JComboBoxRenderer(
				(DatabaseTypesDataSet) session.getSQLConnection().getSQLMetaData().getTypesDataSet()));
		tc1.setCellEditor(new DefaultCellEditor(
				new Combobox((DatabaseTypesDataSet) session.getSQLConnection().getSQLMetaData().getTypesDataSet())));
		tc5.setCellRenderer(new JCheckBoxRenderer());
		tc5.setCellEditor(new DefaultCellEditor(new CheckBox()));
		tc6.setCellRenderer(new JCheckBoxRenderer());
		tc6.setCellEditor(new DefaultCellEditor(new CheckBox()));
		columnTable.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		columnTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		columnTable.setRowHeight(20);
	}

	private void hBaseOptionTableDraw() throws DataSetException {
		String[] columnNames = { "HBase Option", "Values" };
		Object[][] tableVales = {};
		hBaseOptionTableModel = new DefaultTableModel(tableVales, columnNames);
		hBaseOptionTable = new JTable(hBaseOptionTableModel);
		hBaseOptionTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		TableColumnModel tcm = hBaseOptionTable.getColumnModel();
		TableColumn tc0 = tcm.getColumn(0);
		// TableColumn tc1 = tcm.getColumn(1);
		// tc1.setCellRenderer(new JHOComboBoxRenderer("true","false"));
		// tc1.setCellEditor(new DefaultCellEditor(new
		// HOCombobox("true","false")));
		tc0.setCellRenderer(new JHOComboBoxRenderer());
		tc0.setCellEditor(new DefaultCellEditor(new HOCombobox()));
		hBaseOptionTable.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		hBaseOptionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		hBaseOptionTable.setRowHeight(20);
		hBaseOptionTableModel.addRow(new String[] { "DATA_BLOCK_ENCODING", "FAST_DIFF" });
		hBaseOptionTableModel.addRow(new String[] { "COMPRESSION", "SNAPPY" });
		hBaseOptionTableModel.addRow(new String[] { "MEMSTORE_FLUSH_SIZE", "1073741824" });
	}

	private void primaryKeyTableDraw() {

		String[] columnNames = { "Columns Name","partition"};
		Object[][] tableVales = {};
		pkTableModel = new DefaultTableModel(tableVales, columnNames){
			@Override
			public void fireTableCellUpdated(int row, int column) {
				if(column==1){
					pklist.get(row)[1]=pkTableModel.getValueAt(row, column);
				}
				super.fireTableCellUpdated(row, column);
			}
		};
		pkTable = new JTable(pkTableModel);
		TableColumnModel tcm = pkTable.getColumnModel();
		TableColumn tc1 = tcm.getColumn(1);
		tc1.setCellEditor(new DefaultCellEditor(new CheckBox()));
		tc1.setCellRenderer(new JCheckBoxRenderer());
		pkTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		pkTable.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		pkTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pkTable.setRowHeight(20);
	}


	private void toSql() {
		StringBuffer mainsb = new StringBuffer();
		StringBuffer pkSb = new StringBuffer();
		StringBuffer hoSb = new StringBuffer();
		StringBuffer partitionSb = new StringBuffer();
		partitionSb.append(" SALT USING ");
		partitionSb.append(partitionNum.getText()+" PARTITIONS ON");
		mainsb.append("CREATE TABLE ");
		if(pkRadio.isSelected()){
			pkSb.append("PRIMARY KEY (");			
		}else{
			pkSb.append("STORE BY (");
		}
		hoSb.append("HBASE_OPTIONS(");
		mainsb.append(snTextField.getText() + "." + tnTextField.getText() + " (");
		for (int i = 0; i < columnTableModel.getRowCount(); i++) {
			mainsb.append(columnTableModel.getValueAt(i, 0) + " " + columnTableModel.getValueAt(i, 1));
			if (columnTableModel.getValueAt(i, 2) != null && !"".equals(columnTableModel.getValueAt(i, 2))) {
				mainsb.append("(" + columnTableModel.getValueAt(i, 2) + ")");
			}
			if (columnTableModel.getValueAt(i, 3) != null && !"".equals(columnTableModel.getValueAt(i, 3))) {
				mainsb.deleteCharAt(mainsb.length() - 1);
				mainsb.append("," + columnTableModel.getValueAt(i, 3) + ")");
			}
			if (columnTableModel.getValueAt(i, 4) != null && !"".equals(columnTableModel.getValueAt(i, 4))) {
				if ("CHAR".equals(columnTableModel.getValueAt(i, 1).toString())
						|| "VARCHAR".equals(columnTableModel.getValueAt(i, 1).toString())) {
					mainsb.append(" DEFAULT '" + columnTableModel.getValueAt(i, 4) + "'");
				} else if (columnTableModel.getValueAt(i, 1).toString().startsWith("INTERVAL")) {
					mainsb.append(" DEFAULT INTERVAL'" + columnTableModel.getValueAt(i, 4) + "'"
							+ columnTableModel.getValueAt(i, 1).toString().substring(7,
									columnTableModel.getValueAt(i, 1).toString().length()));
				} else if ("DATE".equals(columnTableModel.getValueAt(i, 1).toString())) {
					if ("CURRENT_DATE".equals(columnTableModel.getValueAt(i, 1).toString().toUpperCase())) {
						mainsb.append(" DEFAULT CURRENT_DATE");
					} else {
						mainsb.append(" DEFAULT '" + columnTableModel.getValueAt(i, 4) + "'");
					}
				} else if ("TIME".equals(columnTableModel.getValueAt(i, 1).toString())) {
					if ("CURRENT_TIME".equals(columnTableModel.getValueAt(i, 1).toString().toUpperCase())) {
						mainsb.append(" DEFAULT CURRENT_TIME");
					} else {
						mainsb.append(" DEFAULT '" + columnTableModel.getValueAt(i, 4) + "'");
					}
				} else if ("TIMESTAMP".equals(columnTableModel.getValueAt(i, 1).toString())) {
					if ("CURRENT_TIMESTAMP".equals(columnTableModel.getValueAt(i, 1).toString().toUpperCase())) {
						mainsb.append(" DEFAULT CURRENT_TIMESTAMP");
					} else {
						mainsb.append(" DEFAULT '" + columnTableModel.getValueAt(i, 4) + "'");
					}
				} else {
					mainsb.append(" DEFAULT " + columnTableModel.getValueAt(i, 4));
				}
			}
			if (columnTableModel.getValueAt(i, 5) != null && (boolean) columnTableModel.getValueAt(i, 5)) {
				mainsb.append(" NOT NULL ");
			}
			if (columnTableModel.getValueAt(i, 6) != null && (boolean) columnTableModel.getValueAt(i, 6)) {
				mainsb.append(" NOT NULL ");
			}
			mainsb.append(",");
		}
		for (int i = 0; i < pklist.size(); i++) {
			pkSb.append(pklist.get(i)[0] + ",");
			if(pklist.get(i)[1].toString()=="true")
				partitionSb.append(pklist.get(i)[0]+",");
		}
		pkSb.deleteCharAt(pkSb.length() - 1);
		pkSb.append(")");
		for (int i = 0; i < hBaseOptionTableModel.getRowCount(); i++) {
			if (hBaseOptionTableModel.getValueAt(i, 0) != null && hBaseOptionTableModel.getValueAt(i, 1) != null
					&& !"".equals(hBaseOptionTableModel.getValueAt(i, 0))
					&& !"".equals(hBaseOptionTableModel.getValueAt(i, 0)))
				hoSb.append(
						hBaseOptionTableModel.getValueAt(i, 0) + "='" + hBaseOptionTableModel.getValueAt(i, 1) + "',");
		}
		hoSb.deleteCharAt(hoSb.length() - 1);
		mainsb.deleteCharAt(mainsb.length() - 1);
		mainsb.append(")");

		if(isPartitionCheckBox.isSelected()&&partitionNum.getText()!=null&&!"".equals(partitionNum.getText())){
			partitionSb.deleteCharAt(partitionSb.length()-1);
			partitionSb.append(")");
			mainsb.append(partitionSb);
		}

		if (pkSb.length() > 13) {
			mainsb.append(pkSb);
		}
		if (hoSb.length() > 14) {
			mainsb.append(hoSb);
			mainsb.append(")");
		}
		mainsb.append(";");
		session.getSQLPanelAPIOfActiveSessionWindow().getSQLEntryPanel().setText(mainsb.toString());
		formatSql();
		session.selectMainTab(1);
		try {
			session.getSQLConnection().getSQLMetaData().getTypesDataSet();
		} catch (DataSetException e) {
			e.printStackTrace();
		}
	}

	private void excuteSql() {
		session.getSQLPanelAPIOfActiveSessionWindow().executeCurrentSQL();
	}

	private void formatSql() {
		ISQLEntryPanel panel = session.getSQLPanelAPIOfActiveSessionWindow().getSQLEntryPanel();
		int[] bounds = panel.getBoundsOfSQLToBeExecuted();

		if (bounds[0] == bounds[1]) {
			return;
		}

		String textToReformat = panel.getSQLToBeExecuted();

		if (null == textToReformat) {
			return;
		}

		CodeReformator cr = new CodeReformator(CodeReformatorConfigFactory.createConfig(session));

		String reformatedText = cr.reformat(textToReformat);

		panel.setSelectionStart(bounds[0]);
		panel.setSelectionEnd(bounds[1]);
		panel.replaceSelection(reformatedText);
	}

	private void addCol() {
		String coln = "col" + index++;
		Object[] rowValues = { coln };
		colList.add(coln);
		columnTableModel.addRow(rowValues); // 添加一行
	}
}
