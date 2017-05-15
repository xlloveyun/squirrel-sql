package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs;


import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.ButtonTabComponent;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.SmallTabButton;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.JTableDemo;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class CreateTableTab extends BaseObjectTab{

	CloseTabButton _btnClose;
	 private static final StringManager s_stringMgr =
		        StringManagerFactory.getStringManager(CreateTableTab.class);
	 
	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return s_stringMgr.getString("CreateTableTab.title");
	}

	@Override
	public String getHint() {
		// TODO Auto-generated method stub
		return s_stringMgr.getString("CreateTableTab.hint");
	}

	@Override
	public Component getComponent() {
		JPanel panel =new JPanel();
		JPanel panel1 =new JPanel();
		JPanel panel2 =new JPanel();
		JPanel panel3 =new JPanel();
		JPanel panel4 =new JPanel();
		panel.setLayout(new GridLayout(4,10));
		panel1.add(new JButton("123"));
		panel1.add(new JButton("123"));
		panel1.add(new JButton("123"));
		panel.add(panel1,new GridLayout(1,1));
		Object[][] cellData = {{"row1-col1", "row1-col2"},{"row2-col1", "row2-col2"}};
		String[] columnNames = {"col1", "col2"};
		   
		JTable table = new JTable(cellData, columnNames);
		panel2.add(new JTableDemo());
		panel.add(panel2,new GridLayout(2,1));
		panel3.add(new JButton("123"));
		panel.add(panel3,new GridLayout(3,1));
		panel4.add(new JButton("123"));
		panel.add(panel4,new GridLayout(4,1));
		return panel;
	}

	@Override
	public void clear() {
		
	}

	@Override
	protected void refreshComponent() throws DataSetException {
		
	}
	
	
	
	
	 private static class CloseTabButton extends SmallTabButton
	   {
	      private ButtonTabComponent tabComponent = null;

	      private CloseTabButton(ButtonTabComponent tabComponent)
	      {
	         super(s_stringMgr.getString("docktabdesktop.ButtonTabComponent.toolTip"), null);
	        
	         this.tabComponent = tabComponent;
	      }
	   }



}
