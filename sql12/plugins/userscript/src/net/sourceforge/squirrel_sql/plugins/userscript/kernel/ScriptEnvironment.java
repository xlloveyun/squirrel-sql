package net.sourceforge.squirrel_sql.plugins.userscript.kernel;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import javax.swing.*;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.util.Vector;
import java.awt.*;

public class ScriptEnvironment
{
	private ISQLPanelAPI m_sqlPanelApi;
	private JFrame m_ownerFrame;

	private JDialog m_dlg;
	private JTabbedPane m_tabbedPane;
	private JLabel m_lblStatus;

	private Vector m_printStreams = new Vector();
	private int createdPrintStreamsCount = 0;

	ScriptEnvironment(ISQLPanelAPI sqlPanelApi, JFrame ownerFrame)
	{
		m_sqlPanelApi = sqlPanelApi;
		m_ownerFrame = ownerFrame;

		m_dlg = new JDialog(m_ownerFrame, "Script execution output", false);
		m_dlg.getContentPane().setLayout(new BorderLayout());
		m_tabbedPane = new JTabbedPane();
		m_lblStatus = new JLabel("Executing Script...");

		m_dlg.getContentPane().add(m_tabbedPane, BorderLayout.CENTER);
		m_dlg.getContentPane().add(m_lblStatus, BorderLayout.SOUTH);

		GUIUtils.centerWithinParent(m_dlg);

		m_dlg.setSize(400, 400);
	}

	public PrintStream createPrintStream()
	{
		return createPrintStream(null);
	}

	public PrintStream createPrintStream(String tabTitle)
	{
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final JTextArea txtOut = new JTextArea();

		if(null == tabTitle)
		{
			m_tabbedPane.addTab("<" + (++createdPrintStreamsCount) + ">", txtOut);
		}
		else
		{
			m_tabbedPane.addTab(tabTitle, new JScrollPane(txtOut));
		}

		PrintStream ret =
			new PrintStream(bos)
			{
				public void flush()
				{
					super.flush();
					onFlush(bos, txtOut);
				}
			};


		// Dialog is shown only when it is written to.
		m_dlg.setVisible(true);

		m_printStreams.add(ret);

		return ret;
	}

	private void onFlush(ByteArrayOutputStream bos, JTextArea txtOut)
	{
		txtOut.append(bos.toString());
		bos.reset();
	}

	public PrintStream getSQLAreaPrintStream()
	{
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintStream ret =
			new PrintStream(bos)
			{
				public void flush()
				{
					super.flush();
					onFlushToSqlArea(bos);
				}
			};
		m_printStreams.add(ret);
		return ret;
	}

	private void onFlushToSqlArea(ByteArrayOutputStream bos)
	{
		m_sqlPanelApi.appendSQLScript(bos.toString());
		bos.reset();
	}


	void flushAll()
	{
		for (int i = 0; i < m_printStreams.size(); i++)
		{
			PrintStream printStream = (PrintStream) m_printStreams.elementAt(i);
			printStream.flush();
		}
	}

	void setExecutionFinished(boolean successful)
	{
		if(successful)
		{
			m_lblStatus.setText("Script completed");
		}
		else
		{
			m_lblStatus.setText("Script completed with errors");
		}
	}
}
