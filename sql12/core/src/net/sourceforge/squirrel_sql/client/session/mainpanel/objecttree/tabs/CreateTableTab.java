package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs;


import java.awt.Component;

import org.apache.axis.session.Session;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.createtable.CreateTableModal;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class CreateTableTab extends BaseObjectTab{

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
		String connectionName = _app.getSessionManager().getActiveSession().getTitle();
        String  schemaName=  _app.getSessionManager().getActiveSession().getObjectTreeAPIOfActiveSessionWindow().getSelectedDatabaseObjects()[0].getSimpleName();
        CreateTableModal panel =new CreateTableModal(_app,getSession(),connectionName,schemaName);
		return panel.getTableComponent();
	}

	
	@Override
	public void clear() {
		
	}

	@Override
	protected void refreshComponent() throws DataSetException {
		
	}



}
