package net.sourceforge.squirrel_sql.plugins.jedit;
/*
 * Copyright (C) 2001 Colin Bell
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory;
import net.sourceforge.squirrel_sql.client.session.properties.ISessionPropertiesPanel;

import net.sourceforge.squirrel_sql.plugins.jedit.textarea.TextAreaDefaults;

/**
 * The jEdit plugin class. This plugin replaces the standard SQL entry text area
 * with the jEdit edia area.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class JeditPlugin extends DefaultSessionPlugin {
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(JeditPlugin.class);

	/** Preferences for new sessions. */
	private JeditPreferences _newSessionPrefs;

	/** Folder to store user settings in. */
	private File _userSettingsFolder;

	/** Factory that creates jEdit text controls. */
	private ISQLEntryPanelFactory _jeditFactory;

	/**
	 * Return the internal name of this plugin.
	 *
	 * @return  the internal name of this plugin.
	 */
	public String getInternalName() {
		return "jedit";
	}

	/**
	 * Return the descriptive name of this plugin.
	 *
	 * @return  the descriptive name of this plugin.
	 */
	public String getDescriptiveName() {
		return "jEdit Text Area Plugin";
	}

	/**
	 * Returns the current version of this plugin.
	 *
	 * @return  the current version of this plugin.
	 */
	public String getVersion() {
		return "0.1";
	}

	/**
	 * Returns the authors name.
	 *
	 * @return  the authors name.
	 */
	public String getAuthor() {
		return "Colin Bell";
	}

	/**
	 * Initialize this plugin.
	 */
	public synchronized void initialize() throws PluginException {
		super.initialize();

		// Folder to store user settings.
		try {
			_userSettingsFolder = getPluginUserSettingsFolder();
		} catch (IOException ex) {
			throw new PluginException(ex);
		}

		// Load plugin preferences.
		loadPrefs();
		TextAreaDefaults dfts = TextAreaDefaults.getDefaults();
		dfts.inputHandler = new JeditInputHandler();

		// Install the jEdit factory for creating SQL entry text controls.
		ISQLEntryPanelFactory originalFactory = getApplication().getSQLEntryPanelFactory();
		_jeditFactory = new JeditSQLEntryPanelFactory(this, _newSessionPrefs, originalFactory);
		getApplication().setSQLEntryPanelFactory(_jeditFactory);
	}

	/**
	 * Application is shutting down so save preferences.
	 */
	public void unload() {
		savePrefs();
		super.unload();
	}

	/**
	 * Create preferences panel for the New Session Properties dialog.
	 *
	 * @return  preferences panel.
	 */
	public INewSessionPropertiesPanel[] getNewSessionPropertiesPanels() {
		return new INewSessionPropertiesPanel[] {
			new JeditPreferencesPanel(_newSessionPrefs)
		};
	}

	/**
	 * Override this to create panels for the Session Properties dialog.
	 *
	 * @return  <TT>null</TT> to indicate that this plugin doesn't use session property panels.
	 */
	public ISessionPropertiesPanel[] getSessionPropertiesPanels(ISession session) {
		JeditPreferences sessionPrefs = (JeditPreferences)session.getPluginObject(this, JeditConstants.ISessionKeys.PREFS);
		return new ISessionPropertiesPanel[] {
			 new JeditPreferencesPanel(sessionPrefs)
		};
	}

	ISQLEntryPanelFactory getJeditFactory() {
		return _jeditFactory;
	}

	/**
	 * Load from preferences file.
	 */
	private void loadPrefs() {
		try {
			XMLBeanReader doc = new XMLBeanReader();
			doc.load(
				new File(_userSettingsFolder, JeditConstants.USER_PREFS_FILE_NAME),
				getClass().getClassLoader());
			Iterator it = doc.iterator();
			if (it.hasNext()) {
				_newSessionPrefs = (JeditPreferences)it.next();
			}
		} catch (FileNotFoundException ignore) {
			// property file not found for user - first time user ran pgm.
		} catch (Exception ex) {
			s_log.error("Error occured reading from preferences file: "
							+ JeditConstants.USER_PREFS_FILE_NAME, ex);
		}
		if (_newSessionPrefs == null) {
			_newSessionPrefs = new JeditPreferences();
		}
	}

	/**
	 * Save preferences to disk.
	 */
	private void savePrefs() {
		try {
			XMLBeanWriter wtr = new XMLBeanWriter(_newSessionPrefs);
			wtr.save(new File(_userSettingsFolder, JeditConstants.USER_PREFS_FILE_NAME));
		} catch (Exception ex) {
			s_log.error("Error occured writing to preferences file: "
					+ JeditConstants.USER_PREFS_FILE_NAME, ex);
		}
	}

}
