package net.sourceforge.squirrel_sql.client.mainframe;
/*
 * Copyright (C) 2001-2002 Colin Bell
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
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.swing.Action;
import javax.swing.DefaultDesktopManager;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.fw.gui.BaseMDIParentFrame;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.ScrollableDesktopPane;
import net.sourceforge.squirrel_sql.fw.gui.StatusBar;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Version;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.db.DataCache;
import net.sourceforge.squirrel_sql.client.gui.BaseSheet;
import net.sourceforge.squirrel_sql.client.mainframe.action.ViewAliasesAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ViewDriversAction;
import net.sourceforge.squirrel_sql.client.plugin.PluginManager;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.session.SessionSheet;
import net.sourceforge.squirrel_sql.client.session.action.CommitAction;
import net.sourceforge.squirrel_sql.client.session.action.ExecuteSqlAction;
import net.sourceforge.squirrel_sql.client.session.action.RefreshTreeAction;
import net.sourceforge.squirrel_sql.client.session.action.RollbackAction;

public class MainFrame extends BaseMDIParentFrame {
	public interface IMenuIDs extends MainFrameMenuBar.IMenuIDs {
	}

	private ILogger s_log = LoggerController.createLogger(MainFrame.class);

	private IApplication _app;

	private AliasesToolWindow _aliasesToolWindow;
	private DriversToolWindow _driversToolWindow;

	/** Status bar at bottom of window. */
	private MainFrameStatusBar _statusBar;

	private JInternalFrame _activeInternalFrame;

	/**
	 * Ctor.
	 *
	 * @param   app	 Application API.
	 *
	 * @throws  IllegalArgumentException
	 *			  Thrown if <TT>null</TT> <TT>IApplication</TT>
	 *			  passed.
	 */
	public MainFrame(IApplication app)
			throws IllegalArgumentException {
		super(Version.getVersion(), new ScrollableDesktopPane());
		if (app == null) {
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_app = app;
		createUserInterface();
		preferencesHaveChanged(null);   // Initial load of prefs.
		_app.getSquirrelPreferences().addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				preferencesHaveChanged(evt);
			}
		});
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	public void dispose() {
		if (closeAllNonToolWindows()) {
			closeAllToolWindows();
			_app.shutdown();
			super.dispose();
			System.exit(0);
		}
	}

	//??Why
	public void pack() {
	}

	public void addInternalFrame(JInternalFrame child, boolean addToWindowMenu, Action action) {
		super.addInternalFrame(child, addToWindowMenu, action);
		s_log.debug("Adding " + child.getClass().getName() + " to Main Frame");
		JInternalFrame[] frames = GUIUtils.getOpenNonToolWindows(getDesktopPane().getAllFrames());
		_app.getActionCollection().internalFrameOpenedOrClosed(frames.length);

		// Size non-tool child window.
		if (!GUIUtils.isToolWindow(child)) {
			getSessionMenu().setEnabled(true);
			Dimension cs = child.getParent().getSize();
			// Cast to int required as Dimension::setSize(double,double)
			// doesn't appear to do anything in JDK1.2.2.
			cs.setSize((int)(cs.width * 0.8d), (int)(cs.height * 0.8d));
			child.setSize(cs);
		}
	}

	public void internalFrameClosed(JInternalFrame child) {
		super.internalFrameClosed(child);
		s_log.debug("Removing " + child.getClass().getName() + " from Main Frame");
		JInternalFrame[] frames = GUIUtils.getOpenNonToolWindows(getDesktopPane().getAllFrames());
		_app.getActionCollection().internalFrameOpenedOrClosed(frames.length);
		if (frames.length == 0) {
			getSessionMenu().setEnabled(false);
		}
	}

	/**
	 * Return the Drivers tool window.
	 */
	public DriversToolWindow getDriversToolWindow() {
		return _driversToolWindow;
	}

	/**
	 * Return the Aliases tool window.
	 */
	public AliasesToolWindow getAliasesToolWindow() {
		return _aliasesToolWindow;
	}

	Point getAliasesWindowLocation() {
		return _aliasesToolWindow.getLocation();
	}

	Point getDriversWindowLocation() {
		return _driversToolWindow.getLocation();
	}

	public JMenu getSessionMenu() {
		return ((MainFrameMenuBar)getJMenuBar()).getSessionMenu();
	}

	public JMenu getWindowsMenu() {
		return ((MainFrameMenuBar)getJMenuBar()).getWindowsMenu();
	}

	public JInternalFrame getActiveInternalFrame() {
		return _activeInternalFrame;
	}

	/**
	 * Return the statusbar.
	 * 
	 * @return	the statusbar.
	 */
	public MainFrameStatusBar getStatusBar() {
		return _statusBar;
	}

	public void addToMenu(int menuId, JMenu menu) {
		if (menu == null) {
			throw new IllegalArgumentException("Null JMenu passed");
		}
		((MainFrameMenuBar)getJMenuBar()).addToMenu(menuId, menu);
	}

	public void addToMenu(int menuId, Action action) {
		if (action == null) {
			throw new IllegalArgumentException("Null Action passed");
		}
		((MainFrameMenuBar)getJMenuBar()).addToMenu(menuId, action);
	}

	private void preferencesHaveChanged(PropertyChangeEvent evt) {
		String propName = evt != null ? evt.getPropertyName() : null;

		final SquirrelPreferences prefs = _app.getSquirrelPreferences();

		if (propName == null || propName.equals(SquirrelPreferences.IPropertyNames.SHOW_CONTENTS_WHEN_DRAGGING)) {
			if (prefs.getShowContentsWhenDragging()) {
				getDesktopPane().putClientProperty("JDesktopPane.dragMode",  null);
			} else {
				getDesktopPane().putClientProperty("JDesktopPane.dragMode",  "outline");
			}
		}
	}

	synchronized public boolean closeAllNonToolWindows() {
		JInternalFrame[] frames = GUIUtils.getOpenNonToolWindows(getDesktopPane().getAllFrames());
		for (int i = 0; i < frames.length; ++i) {
			frames[i].dispose();
		}
		return true;
	}

	private void closeAllToolWindows() {
		JInternalFrame[] frames = GUIUtils.getOpenToolWindows(getDesktopPane().getAllFrames());
		for (int i = 0; i < frames.length; ++i) {
			frames[i].dispose();
		}
	}

	private void createUserInterface() {
		setVisible(false);

		getDesktopPane().setDesktopManager(new MyDesktopManager());

		final Container content = getContentPane();

		_aliasesToolWindow = new AliasesToolWindow(_app);
		_driversToolWindow = new DriversToolWindow(_app);

		preLoadActions();
		content.setLayout(new BorderLayout());
		content.add(new MainFrameToolBar(_app, this), BorderLayout.NORTH);
		JScrollPane sp = new JScrollPane(getDesktopPane(),
								ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
								ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		content.add(sp, BorderLayout.CENTER);

		_statusBar = new MainFrameStatusBar(true);
		Font fn = _app.getFontInfoStore().getStatusBarFontInfo().createFont();
		_statusBar.setFont(fn);
		content.add(_statusBar, BorderLayout.SOUTH);

		setJMenuBar(new MainFrameMenuBar(_app, getDesktopPane(), _app.getActionCollection()));

		setupFromPreferences();

		validate();
	}

	private void preLoadActions() {
		ActionCollection actions = _app.getActionCollection();

		if (actions == null) {
			throw new IllegalStateException("ActionCollection hasn't been created.");
		}
		if (_aliasesToolWindow == null) {
			throw new IllegalStateException("AliasesToolWindow hasn't been created.");
		}
		if (_driversToolWindow == null) {
			throw new IllegalStateException("DriversToolWindow hasn't been created.");
		}

		actions.add(new ViewAliasesAction(_app, _aliasesToolWindow));
		actions.add(new ViewDriversAction(_app, _driversToolWindow));
	}

	private void setupFromPreferences() {
		final SquirrelPreferences prefs = _app.getSquirrelPreferences();
		MainFrameWindowState ws = prefs.getMainFrameWindowState();

		// Position window to where it was when last closed. If this is not
		// on the screen, move it back on to the screen.
		setBounds(ws.getBounds().createRectangle());
		if (!GUIUtils.isWithinParent(this)) {
			setLocation(new Point(10, 10));
		}

		addInternalFrame(_driversToolWindow, false, null);
		Point pt = ws.getDriversWindowLocation().createPoint();
		_driversToolWindow.setBounds(pt.x, pt.y, 200, 200);
		_driversToolWindow.setVisible(true);
		try {
			_driversToolWindow.setSelected(true);
		} catch (PropertyVetoException ignore) {
		}

		addInternalFrame(_aliasesToolWindow, false, null);
		pt = ws.getAliasesWindowLocation().createPoint();
		_aliasesToolWindow.setBounds(pt.x, pt.y, 200, 200);
		_aliasesToolWindow.setVisible(true);
		try {
			_aliasesToolWindow.setSelected(true);
		} catch (PropertyVetoException ignore) {
		}

		prefs.setMainFrameWindowState(new MainFrameWindowState(this));
	}

	private class MyDesktopManager extends DefaultDesktopManager {
		public void activateFrame(JInternalFrame f) {
			super.activateFrame(f);
			_activeInternalFrame = f;
			_app.getActionCollection().internalFrameActivated(f);
			if (f instanceof SessionSheet) {
				getSessionMenu().setEnabled(true);
				((SessionSheet)f).updateState();
			}
		}
		public void deactivateFrame(JInternalFrame f) {
			super.deactivateFrame(f);
			_activeInternalFrame = null;
			_app.getActionCollection().internalFrameDeactivated(f);
			if (f instanceof SessionSheet) {
				((SessionSheet)f).updateState();
				getSessionMenu().setEnabled(false);
			}
		}
	}
}
