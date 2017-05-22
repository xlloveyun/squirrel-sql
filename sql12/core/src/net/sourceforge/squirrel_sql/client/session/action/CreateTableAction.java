package net.sourceforge.squirrel_sql.client.session.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.CreateTableTab;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;

public class CreateTableAction extends SquirrelAction implements ISessionAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ISession session;
	public CreateTableAction(IApplication app) {
		super(app);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		session.getObjectTreeAPIOfActiveSessionWindow().addDetailTab(DatabaseObjectType.SCHEMA, new CreateTableTab());
	}

	@Override
	public void setSession(ISession session) {
		this.session=session;
	}

}
