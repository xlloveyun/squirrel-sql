package net.sourceforge.squirrel_sql.client.session.properties;
/**
 * Copyright (C) 2003-2004 Glenn Griffin
 *
 * Modifications Copyright (C) 2003-2004 Jason Height
 *
 * Adapted from SQLFilterSheet.java by Maury Hammel.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.alibaba.fastjson.JSONObject;
import com.esgyn.dataloader.impl.DataLoaderImpl;
import com.jidesoft.icons.IconSet.Text;
import com.jidesoft.utils.Base64.OutputStream;
import com.jidesoft.utils.SwingWorker;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.SessionDialogWidget;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * Edit Where Cols dialog gui. JASON: Rename to EditWhereColsInternalFrame
 */
public class ImportDataFromDBDialog extends SessionDialogWidget {

	private static final StringManager s_stringMgr = StringManagerFactory
			.getStringManager(ImportDataFromDBDialog.class);

	/**
	 * This interface defines locale specific strings. This should be replaced
	 * with a property file.
	 */
	private interface i18n {
		/** Title */
		// i18n[editWhereColsSheet.editWhereColumns=Edit 'WHERE' columns]
		String TITLE = s_stringMgr.getString("ImportDataFromDBDialog.title");
	}

	/** Logger for this class. */
	private static final ILogger s_log = LoggerController.createLogger(ImportDataFromDBDialog.class);

	/**
	 * A reference to a class containing information about the database
	 * metadata.
	 */
	private IDatabaseObjectInfo _objectInfo;

	/** Frame title. */
	private JLabel _titleLbl = new JLabel();

	private JTable table = null;

	private JComboBox comboBox;

	private String[] columnNames = { "目标表列", "源表列" };

	private final IApplication app = getSession().getApplication();

	DefaultTableModel model = new DefaultTableModel(new Object[][] {}, columnNames);

	private JTextArea tableName;

	private JTextArea insNum;
	
	JPanel contentPane ;
	
	private List<String> list;

	/**
	 * Creates a new instance of SQLFilterSheet
	 *
	 * @param session
	 *            A reference to the current SQuirreL session
	 * @param objectInfo
	 *            An instance of a class containing database metadata
	 *            information.
	 */
	public ImportDataFromDBDialog(ISession session, IDatabaseObjectInfo objectInfo) {
		super(i18n.TITLE, true, session);
		if (objectInfo == null) {
			throw new IllegalArgumentException("Null IDatabaseObjectInfo passed");
		}

		_objectInfo = objectInfo;
		createGUI();
		pack();
	}

	/**
	 * Position and display the sheet.
	 *
	 * @param show
	 *            A boolean that determines whether the sheet is shown or
	 *            hidden.
	 */
	public synchronized void setVisible(boolean show) {
		if (show) {
			if (!isVisible()) {
				final boolean isDebug = s_log.isDebugEnabled();
				long start = 0;

				pack();
				Dimension d = getSize();
				d.width += 5;
				d.height += 5;
				setSize(d);
				/*
				 * END-KLUDGE
				 */
				DialogWidget.centerWithinDesktop(this);
			}
			moveToFront();
		}
		super.setVisible(show);
	}

	/**
	 * Dispose of the sheet.
	 */

	public IDatabaseObjectInfo getDatabaseObjectInfo() {
		return _objectInfo;
	}

	/**
	 * Create the GUI elements for the sheet and pass in the setup data to the
	 * panel.
	 */
	private void createGUI() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		// This is a tool window.
		makeToolWindow(true);

		_titleLbl.setText(getTitle() + ": " + _objectInfo.getSimpleName());

		contentPane = new JPanel(new GridBagLayout());
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		setContentPane(contentPane);

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridwidth = 1;

		gbc.gridx = 0;
		gbc.gridy = 0;

		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		JLabel lblChooseAlias = new JLabel("Choose Alias:");
		lblChooseAlias.setBounds(24, 26, 87, 15);
		contentPane.add(lblChooseAlias, gbc);

		++gbc.gridx;
		comboBox = new JComboBox();
		comboBox.setBounds(124, 23, 227, 21);
		contentPane.add(comboBox, gbc);
		for (Iterator<ISQLAlias> it = app.getDataCache().aliases(); it.hasNext();) {
			ISQLAlias alias = it.next();
			comboBox.addItem(alias.getName());
		}

		++gbc.gridy;
		contentPane.add(new JLabel(" "), gbc);

		gbc.fill = GridBagConstraints.BOTH;
		--gbc.gridx;
		gbc.gridy++;
		JLabel lblChooseTable = new JLabel("Input Table:");
		lblChooseTable.setBounds(24, 73, 87, 15);
		contentPane.add(lblChooseTable, gbc);

		++gbc.gridx;
		gbc.gridwidth = 3;
		tableName = new JTextArea();
		tableName.setBounds(124, 69, 227, 24);
		contentPane.add(tableName, gbc);

		++gbc.gridy;
		contentPane.add(new JLabel(" "), gbc);

		gbc.fill = GridBagConstraints.BOTH;
		--gbc.gridx;
		gbc.gridy++;
		JLabel subNumAlias = new JLabel("One commit inserts number:");
		subNumAlias.setBounds(24, 26, 87, 15);
		contentPane.add(subNumAlias, gbc);

		++gbc.gridx;
		insNum = new JTextArea();
		insNum.setBounds(124, 69, 227, 24);
		insNum.setText("1000");
		contentPane.add(insNum, gbc);

		++gbc.gridy;
		contentPane.add(new JLabel(" "), gbc);
		gbc.fill = GridBagConstraints.BOTH;
		--gbc.gridx;
		gbc.gridy++;
		initTable();
		JScrollPane scrollPane = new JScrollPane(table);
		contentPane.add(scrollPane, gbc);

		++gbc.gridy;
		gbc.gridwidth = 2;
		gbc.weighty = 0;
		contentPane.add(createButtonsPanel(), gbc);

	}

	private void initTable() {
		table = new JTable(model) {
			@Override
			public boolean isCellEditable(int row, int column) {
				if (column == 0)
					return false;
				return super.isCellEditable(row, column);
			}
		};
		table.setRowHeight(21);
	}

	private void performClose() {
		dispose();
	}

	private JPanel createButtonsPanel() {
		JPanel pnl = new JPanel();
		JButton checkBtn = new JButton(s_stringMgr.getString("ImportDataFromDBDialog.check"));
		checkBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if(checkTable())
					match();
			}

		});

		JButton okBtn = new JButton(s_stringMgr.getString("ImportDataFromDBDialog.ok"));
		okBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if(!checkTable())
					return;
				if(work()){
					performClose();
//					JOptionPane.showMessageDialog(contentPane , "import Data workload has been created", "提示",JOptionPane.YES_OPTION);
				}
			}
		});
		JButton closeBtn = new JButton(s_stringMgr.getString("ImportDataFromDBDialog.close"));
		closeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				performClose();
			}
		});

		pnl.add(checkBtn);
		pnl.add(okBtn);
		pnl.add(closeBtn);

		GUIUtils.setJButtonSizesTheSame(new JButton[] { checkBtn, okBtn, closeBtn });
		getRootPane().setDefaultButton(okBtn);

		return pnl;
	}

	private List<String> getConnection(ISQLAlias alias) throws Exception{
		if(tableName.getText().indexOf(".")<=0){
			throw new Exception("请输入正确的表名(schema.tableName)");
		}
		List<String> list = new ArrayList<String>();
		ResultSet rs = null;
		DatabaseMetaData metaData = null;
		Connection conn = null;
		ISQLDriver sqlDriver = app.getDataCache().getDriver(alias.getDriverIdentifier());
		File file = new File(sqlDriver.getJarFileNames()[0]);
		URLClassLoader loader;
		try {
			loader = new URLClassLoader(new URL[] { file.toURI().toURL() });
//			Class.forName(sqlDriver.getDriverClassName());
			Object clazz = loader.loadClass(sqlDriver.getDriverClassName()).newInstance();
			Driver myDriver = (Driver) clazz;
			Properties Obj = new Properties();
			Obj.setProperty("user", alias.getUserName());
			Obj.setProperty("password", alias.getPassword());
			conn = myDriver.connect(sqlDriver.getUrl(), Obj);
			metaData = conn.getMetaData();
			String catalog ="org.trafodion.jdbc.t4.T4Driver".equals(sqlDriver.getDriverClassName())?"TRAFODION":null;
			rs = metaData.getColumns(catalog,tableName.getText().substring(0,tableName.getText().indexOf(".")).toUpperCase(),tableName.getText().substring(tableName.getText().indexOf(".")+1).toUpperCase(), null);
			while (rs.next()) {
				list.add(rs.getString("COLUMN_NAME"));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				rs = null;
				conn = null;
			}
		}
		return list;
	}

	private void match() {
		model.setRowCount(0);
		TableColumnModel tcm = table.getColumnModel();
		TableColumn tc = tcm.getColumn(1);
		tc.setCellRenderer(new SourceComboboxRenderer(list));
		tc.setCellEditor(new DefaultCellEditor(new SourceCombobox(list)));

		TableColumnInfo[] dataSet;
		try {
			dataSet = getSession().getMetaData().getColumnInfo(_objectInfo.getCatalogName(),
					_objectInfo.getSchemaName(), _objectInfo.getSimpleName());
			for (TableColumnInfo tableColumnInfo : dataSet) {
				model.addRow(new Object[] { tableColumnInfo.getColumnName(), "" });
			}
		} catch (SQLException e) {
			s_log.warn("cant get MetaData from selecetd tables", e);
			JOptionPane.showMessageDialog(contentPane , "获取元数据失败", "警告",JOptionPane.WARNING_MESSAGE);
		}
		for (int i = 0; i < model.getRowCount(); i++) {
			model.setValueAt("跳过", i, 1);
			for (int j = 0; j < list.size(); j++) {
				if (list.get(j).equals(model.getValueAt(i, 0))) {
					model.setValueAt(list.get(j), i, 1);
				}
			}
		}
	}

	private boolean checkTable() {
		list = new ArrayList<String>();


		// second to find input tablename is ok
		if (tableName.getText() == null || "".equals(tableName.getText()))
			return false;
		
		ISQLAlias alias =getInputAlias();
		try {
			list.addAll(getConnection(alias));
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(contentPane , e1.getMessage(), "警告",JOptionPane.WARNING_MESSAGE);
			return false;
		}
		
		
		//if can't find column list ,then we can ensure this table is not exist
		if (list.size() == 0) {
			JOptionPane.showMessageDialog(contentPane , "该表不存在", "警告",JOptionPane.WARNING_MESSAGE);
			s_log.warn("can't find this table from DB");
			return false;
		}
		
		return true;
	}
	
	private boolean work(){
		try {
			Map map =new HashMap<String,String>();
			ISQLAlias alias =getInputAlias();
			Properties prop =new Properties();
			for (int i = 0; i < model.getRowCount(); i++) {
				if("跳过".equals(model.getValueAt(i, 1)))
					continue;
				map.put(model.getValueAt(i, 0),model.getValueAt(i, 1));
			}
			if(map.size()<=0)
			{
				JOptionPane.showMessageDialog(contentPane ,"请匹配两表字段", "警告",JOptionPane.WARNING_MESSAGE);
				return false;
			}
			JSONObject json =new JSONObject();
			prop.put("mapping",json.toJSONString(map));
			prop.put("jdbc.select.url",alias.getUrl());
			prop.put("jdbc.insert.url",getSession().getAlias().getUrl());
			prop.put("select.user",alias.getUserName());
			prop.put("select.pwd",alias.getPassword());
			prop.put("insert.user",getSession().getAlias().getUserName());
			prop.put("insert.pwd",getSession().getAlias().getPassword());
			prop.put("batch.size",insNum.getText());
			prop.put("select.jdbc.driver",app.getDataCache().getDriver(alias.getDriverIdentifier()).getDriverClassName());
			prop.put("insert.jdbc.driver",getSession().getDriver().getDriverClassName());
			prop.put("select.driver.path",app.getDataCache().getDriver(alias.getDriverIdentifier()).getJarFileNames()[0]);
			prop.put("insert.driver.path",getSession().getDriver().getJarFileNames()[0]);
			prop.put("select.table",tableName.getText());
			prop.put("insert.table",_objectInfo.getQualifiedName().replaceAll("`", ""));
//		    SwingUtilities.invokeLater(new DataLoaderImpl(prop));
		    app.getThreadPool().addTask(new DataLoaderImpl(prop));
		    prop.store(new FileOutputStream(new File("C:\\file1.properties")), "userinfo");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private ISQLAlias getInputAlias(){
		for (Iterator<ISQLAlias> it = app.getDataCache().aliases(); it.hasNext();) {
			ISQLAlias alias = it.next();
			if (alias.getName().equals(comboBox.getSelectedItem())) {
				return alias;
			}
		}
		return null;
	}
}
