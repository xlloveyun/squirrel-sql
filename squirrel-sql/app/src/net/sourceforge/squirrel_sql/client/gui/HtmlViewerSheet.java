package net.sourceforge.squirrel_sql.client.gui;
/*
 * Copyright (C) 2002 Colin Bell
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.TextPopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
/**
 * This sheet shows the contents of a HTML file.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class HtmlViewerSheet extends BaseSheet
{
	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(HtmlViewerSheet.class);
	
	/** Text area containing the HTML. */
	private JEditorPane _contentsTxt = new JEditorPane();

  	public HtmlViewerSheet(String title) throws IOException
	{
		this(title, null);
	}

  	public HtmlViewerSheet(String title, URL url) throws IOException
	{
		super(title, true, true, true, true);
		createUserInterface();
		if (url != null)
		{
			read(url);
		}
	}

	public synchronized void read(URL url) throws IOException
	{
		if (url == null)
		{
			throw new IllegalArgumentException("URL == null");
		}

		CursorChanger cursorChg = new CursorChanger(this);
		cursorChg.show();
		try
		{
			_contentsTxt.setText("");
			try
			{
				_contentsTxt.setPage(url);
			}
			catch (IOException ex)
			{
				final String msg = "Error occured reading from reader";
				s_log.error(msg, ex);
				throw(ex);
			}
		} finally {
			cursorChg.restore();
		}
	}

	/**
	 * Create user interface.
	 */
	private void createUserInterface()
	{
		GUIUtils.makeToolWindow(this, true);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(createToolBar(), BorderLayout.NORTH);
		contentPane.add(createMainPanel(), BorderLayout.CENTER);
		pack();
	}

	private ToolBar createToolBar()
	{
		final ToolBar tb = new ToolBar();
		tb.setBorder(BorderFactory.createEtchedBorder());
		tb.setUseRolloverButtons(true);
		tb.setFloatable(false);
		return tb;
	}

	/**
	 * Create the main panel.
	 */
	private JPanel createMainPanel()
	{
		_contentsTxt.setEditable(false);
		_contentsTxt.setContentType("text/html");
		final TextPopupMenu pop = new TextPopupMenu();
		pop.setTextComponent(_contentsTxt);
		_contentsTxt.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				if (evt.isPopupTrigger()) {
					pop.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}
			public void mouseReleased(MouseEvent evt) {
				if (evt.isPopupTrigger()) {
					pop.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}
		});

		final JPanel pnl = new JPanel(new BorderLayout());
		_contentsTxt.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));
		pnl.add(new JScrollPane(_contentsTxt), BorderLayout.CENTER);

		return pnl;
	}
}