package net.sourceforge.squirrel_sql.plugins.oracle;
/*
 * Copyright (C) 2002 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode.IObjectTreeNodeType;
/**
 * Oracle plugin class.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class OraclePlugin extends DefaultSessionPlugin
{
	/** Logger for this class. */
	private final static ILogger s_log = LoggerController.createLogger(OraclePlugin.class);

	/** Folder to store user settings in. */
	private File _userSettingsFolder;

	/** API for the Obejct Tree. */
	private IObjectTreeAPI _treeAPI;

	/**
	 * Collection of <TT>SessionInfo</TT> objects keyed by
	 * ISession.getIdentifier().
	 */
	private Map _sessions = new HashMap();

	/**
	 * Return the internal name of this plugin.
	 *
	 * @return  the internal name of this plugin.
	 */
	public String getInternalName()
	{
		return "oracle";
	}

	/**
	 * Return the descriptive name of this plugin.
	 *
	 * @return  the descriptive name of this plugin.
	 */
	public String getDescriptiveName()
	{
		return "Oracle Plugin";
	}

	/**
	 * Returns the current version of this plugin.
	 *
	 * @return  the current version of this plugin.
	 */
	public String getVersion()
	{
		return "0.10";
	}

	/**
	 * Returns the authors name.
	 *
	 * @return  the authors name.
	 */
	public String getAuthor()
	{
		return "Colin Bell";
	}

	/**
	 * Load this plugin.
	 *
	 * @param   app	 Application API.
	 */
	public synchronized void load(IApplication app) throws PluginException
	{
		super.load(app);

		// Folder within plugins folder that belongs to this
		// plugin.
		File pluginAppFolder = null;
		try
		{
			pluginAppFolder = getPluginAppSettingsFolder();
		}
		catch (IOException ex)
		{
			throw new PluginException(ex);
		}

		// Folder to store user settings.
		try
		{
			_userSettingsFolder = getPluginUserSettingsFolder();
		}
		catch (IOException ex)
		{
			throw new PluginException(ex);
		}
	}

	/**
	 * Application is shutting down so save preferences.
	 */
	public void unload()
	{
		super.unload();
	}

	/**
	 * Session has been started. If this is an Oracle session then
	 * register an extra expander for the Schema nodes to show
	 * Oracle Packages.
	 * 
	 * @param	session		Session that has started.
	 * 
	 * @return	<TT>true</TT> if session is Oracle in which case this plugin
	 * 							is interested in it.
	 */
	public boolean sessionStarted(ISession session)
	{
		boolean isOracle = false;
		if( super.sessionStarted(session))
		{
			isOracle = isOracle(session);
			if (isOracle)
			{
				SessionInfo si = new SessionInfo(session, this);
				_sessions.put(session.getIdentifier(), si);
				_treeAPI = session.getObjectTreeAPI(this);
				_treeAPI.registerExpander(IObjectTreeNodeType.SCHEMA, new SchemaExpander(this));
				_treeAPI.registerExpander(si._packageNodeType, new PackageExpander());
				_treeAPI.registerDetailTab(IObjectTreeNodeType.PROCEDURE, new ProcedureSourceTab());
			}
		}
		return isOracle;
	}

	/**
	 * Return the session information object for the passed session.
	 * 
	 * @param	session		Session to retrieve info object for.
	 * 
	 * @return	The <TT>SessionInfo</TT> object for the passed session.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>ISession</TT> passed.
	 */
	SessionInfo getSessionInfo(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		return (SessionInfo)_sessions.get(session.getIdentifier());
	}

	private boolean isOracle(ISession session)
	{
		final String ORACLE = "oracle";
		String dbms = null;
		try
		{
			dbms = session.getSQLConnection().getMetaData().getDatabaseProductName();
		}
		catch (SQLException ex)
		{
			s_log.debug("Error in getDatabaseProductName()", ex);
		}
		return dbms != null && dbms.toLowerCase().startsWith(ORACLE);
	}
}