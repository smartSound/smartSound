/* 
 *	Copyright (C) 2012 André Becker
 *	
 *	This program is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	any later version.
 *	
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *	
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package smartsound.controller;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListModel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.html.HTMLEditorKit;

import smartsound.settings.Global;


public class PluginChooser extends JPanel implements ListSelectionListener,
		ActionListener, WindowListener, ClipboardOwner, HyperlinkListener {
	protected class PluginListModel implements ListModel<PluginDescription> {
		
		List<ListDataListener> listeners;
		List<PluginDescription> items;

		public void addListDataListener(ListDataListener l) {
			if (!listeners.contains(l))
				listeners.add(l);
		}

		public PluginDescription getElementAt(int index) {
			if (index >= 0 && index < getSize())
				return (PluginDescription) items.get(index);
			else
				return null;
		}

		public int getSize() {
			return items.size();
		}

		public void removeListDataListener(ListDataListener l) {
			listeners.remove(l);
		}

		public PluginListModel(List<PluginDescription> descriptionList) {
			listeners = new LinkedList<ListDataListener>();
			items = new LinkedList<PluginDescription>(descriptionList);
		}
	}
	
	protected List<PluginDescription> items;
	protected JList<PluginDescription> list;
	protected JTextPane textPane;
	private Thread thread;
	private PluginDescription result;
	private JDialog parent;
	private boolean okPressed;
	private JCheckBox remember;
	protected JButton btn;
	protected JScrollPane scrollPane;
	protected JPanel panel;

	public JCheckBox getRemember() {
		return remember;
	}

	public PluginDescription getResult() {
		return result;
	}

	private PluginChooser(List<PluginDescription> descriptions, Thread thisThread) {
		super(new GridBagLayout());
		items = new LinkedList<PluginDescription>();
		result = null;
		okPressed = false;
		thread = thisThread;
		parent = new JDialog();
		parent.setSize(800, 400);
		parent.setLocation(200, 200);
		parent.setContentPane(this);
		parent.setVisible(true);
		parent.setResizable(false);
		parent.addWindowListener(this);
		parent.setTitle("Choose a plugin");
		list = new JList<PluginDescription>(new PluginListModel(descriptions));
		list.setSelectionMode(0);
		list.addListSelectionListener(this);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.20000000000000001D;
		c.weighty = 1.0D;
		c.fill = 1;
		panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Plugins"));
		panel.add(list);
		add(panel, c);
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1.0D;
		c.weighty = 0.69999999999999996D;
		c.fill = 1;
		panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Description"));
		textPane = new JTextPane();
		HTMLEditorKit editorKit = new HTMLEditorKit();
		textPane.setEditorKit(editorKit);
		textPane.setEditable(false);
		textPane.addHyperlinkListener(this);
		textPane.setText("<h1>Welcome to smartSound!</h1><p align=\"justify\">Choose a plugin for the sound engine to get started. We recommend the NativeBASS plugin. If you set the \"Remember my decision\" option this dialog will not be shown again.</p><p>For news and updates please visit <a href=\"http://smartrpgsound.blogspot.com\">http://smartrpgsound.blogspot.com</a>.</p>");
		scrollPane = new JScrollPane(textPane);
		scrollPane.setHorizontalScrollBarPolicy(31);
		panel.add(scrollPane);
		add(panel, c);
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 2;
		c.fill = 3;
		c.anchor = 22;
		btn = new JButton("OK");
		btn.addActionListener(this);
		btn.setEnabled(false);
		add(btn, c);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.anchor = 22;
		remember = new JCheckBox("Remember my decision");
		add(remember, c);
		parent.revalidate();
	}

	public static PluginDescription getPlugin(List<PluginDescription> pluginList) {
		Thread thisThread = Thread.currentThread();
		PluginChooser chooser = new PluginChooser(pluginList, thisThread);
		chooser.setVisible(true);
		while (chooser.getResult() == null)
			try {
				synchronized (thisThread) {
					thisThread.wait();
				}
			} catch (InterruptedException interruptedexception) {
			}
		try {
			if (chooser.getRemember().isSelected()) {
				Global.getInstance().setProperty("plugin",
						chooser.getResult().getName());
			}
		} catch (IOException ioexception) {
			ioexception.printStackTrace();
		}
		return chooser.getResult();
	}

	public void valueChanged(ListSelectionEvent e) {
		btn.setEnabled(true);
		PluginDescription elem = (PluginDescription) list.getSelectedValue();
		textPane.setText(elem.getDescription().replace('\n', ' ')
				.replace('\r', ' '));
		textPane.setCaretPosition(0);
		textPane.setSize(scrollPane.getWidth(), textPane.getHeight());
	}

	public void actionPerformed(ActionEvent e) {
		okPressed = true;
		result = (PluginDescription) list.getSelectedValue();
		thread.interrupt();
		parent.setVisible(false);
		parent.dispose();
	}

	public void windowActivated(WindowEvent windowevent) {
	}

	public void windowClosed(WindowEvent arg0) {
		if (!okPressed)
			System.exit(0);
	}

	public void windowClosing(WindowEvent arg0) {
		if (!okPressed)
			System.exit(0);
	}

	public void windowDeactivated(WindowEvent windowevent) {
	}

	public void windowDeiconified(WindowEvent windowevent) {
	}

	public void windowIconified(WindowEvent windowevent) {
	}

	public void windowOpened(WindowEvent windowevent) {
	}

	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == javax.swing.event.HyperlinkEvent.EventType.ACTIVATED) {
			Toolkit.getDefaultToolkit()
					.getSystemClipboard()
					.setContents(new StringSelection(e.getURL().toString()),
							this);
			JOptionPane.showMessageDialog(null,
					"The URL has been copied to the clipboard.");
		}
	}

	public void lostOwnership(Clipboard clipboard1, Transferable transferable) {
	}
}
