package net.sourceforge.squirrel_sql.client.session.objectstree;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.tree.MutableTreeNode;

import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.ISession;

public class DatabaseNode extends BaseNode
{
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(DatabaseNode.class);

	private interface ISessionKeys
	{
		String DETAIL_PANEL_KEY = DatabaseNode.class.getName() + "_DETAIL_PANEL_KEY";
	}

	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n
	{
		String DATABASE = "Database";
	}

	DatabaseNode(ISession session, ObjectsTreeModel treeModel)
	{
		super(session, treeModel, i18n.DATABASE);
	}

	public JComponent getDetailsPanel()
	{
		final ISession session = getSession();
		final IPlugin plugin = session.getApplication().getDummyAppPlugin();
		DatabasePanel pnl =
			(DatabasePanel) session.getPluginObject(
				plugin,
				ISession.ISessionKeys.DATABASE_DETAIL_PANEL_KEY);
		return pnl;
	}

	public boolean isLeaf()
	{
		return false;
	}

	public boolean equals(Object obj)
	{
		return obj instanceof DatabaseNode;
	}

	public void expand() throws SQLException
	{
		if (getChildCount() == 0)
		{
			getSession().getApplication().getThreadPool().addTask(
				new ObjectsTreeLoader(addLoadingNode()));
		}
		else
		{
			fireExpanded();
		}
	}

	protected TreeNodesLoader getTreeNodesLoader()
	{
		return new ObjectsTreeLoader(null);
	}

	protected class ObjectsTreeLoader extends BaseNode.TreeNodesLoader
	{
		/**
		 * Constructor for ObjectsTreeLoader.
		 * @param loading
		 */
		ObjectsTreeLoader(MutableTreeNode loading)
		{
			super(loading);
		}

		/*
		 * @see TreeNodesLoader#getNodeList(ISession, SQLConnection, ObjectsTreeModel)
		 */
		public List getNodeList(
			final ISession session,
			final SQLConnection conn,
			final ObjectsTreeModel model)
			throws SQLException
		{
			final ArrayList tableTypeList = new ArrayList();
			if (conn != null)
			{
				boolean supportsCatalogs = false;
				try
				{
					supportsCatalogs = conn.getSQLMetaData().supportsCatalogs();
				}
				catch (SQLException ex)
				{
				}

				boolean supportsSchemas = false;
//				try
//				{
//					supportsSchemas = conn.supportsSchemas();
//				}
//				catch (SQLException ex)
//				{
//				}
				if (supportsCatalogs)
				{
					final String[] catalogs = conn.getSQLMetaData().getCatalogs();
					for (int i = 0; i < catalogs.length; ++i)
					{
						final String catalogName = catalogs[i];
						tableTypeList.add(
							new TableTypesGroupNode(session, model, catalogName, catalogName, null, null));
					}
				}
				else if (supportsSchemas)
				{
//					final String[] schemas = conn.getSchemas();
//					for (int i = 0; i < schemas.length; ++i)
//					{
//						final String schemaName = schemas[i];
//						tableTypeList.add(
//							new TableTypesGroupNode(session, model, null, null, schemaName, schemaName));
//					}
				}
				else
				{
					tableTypeList.add(new TableTypesGroupNode(session, model, null, null, null, null));
				}
			}
			return tableTypeList;
		}
	}
}