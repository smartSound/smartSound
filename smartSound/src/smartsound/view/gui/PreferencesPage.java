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

package smartsound.view.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import smartsound.player.SoundEngineSingleton;
import smartsound.settings.Global;

public class PreferencesPage extends JDialog {
	public PreferencesPage(final JComponent parent) {
		super(SwingUtilities.getWindowAncestor(parent), "Preferences");
		Window wnd = SwingUtilities.getWindowAncestor(parent);
		setModal(true);

		JPanel panel = new JPanel(new GridBagLayout());
		add(panel);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.5f;
		c.weighty = 0.5f;
		c.fill = GridBagConstraints.BOTH;

		JPanel pluginPanel = new JPanel(new GridLayout(2,1));

		pluginPanel.setBorder(BorderFactory.createTitledBorder("Sound plugin"));
		pluginPanel.add(new JButton(new AbstractAction("Reset Plugin") {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				try {
					Global.getInstance().removeProperty("plugin");
					JOptionPane.showMessageDialog((Component) null, "The plugin has successfully been reset.\n You can choose a different one when starting smartSound the next time.");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}));
		pluginPanel.add(new JButton(new AbstractAction("Configure Plugin") {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				try {
					SoundEngineSingleton.getInstance().configure();
				} catch (UnsupportedOperationException e) {
					JOptionPane.showMessageDialog(PreferencesPage.this, "Plugin '" + SoundEngineSingleton.getInstance().getName() + "' does not support configuration.");
				}
			}
		}));
		panel.add(pluginPanel, c);

		JPanel languagePanel = new JPanel(new BorderLayout());
		languagePanel.setBorder(BorderFactory.createTitledBorder("Languages"));
		JComboBox cBox = new JComboBox();
		cBox.addItem("English");
		cBox.setSelectedIndex(0);
		languagePanel.add(cBox);
		c.gridy++;

		panel.add(languagePanel, c);

		JPanel miscPanel = new JPanel(new BorderLayout());
		miscPanel.setBorder(BorderFactory.createTitledBorder("Miscellaneous"));
		final JCheckBox resizeAutomatically = new JCheckBox("Resize automatically");
		boolean resize;
		try {
			resize = Global.getInstance().getProperty("resize_automatically").toLowerCase().equals("true");
		} catch (IOException e) {
			resize = true;
		}
		resizeAutomatically.setSelected(resize);
		resizeAutomatically.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(final ChangeEvent arg0) {
				try {
					Global.getInstance().setProperty("resize_automatically", String.valueOf(resizeAutomatically.isSelected()));
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "Sorry, there was an error saving your changes. Please report this bug.");
				}
			}
		});
		miscPanel.add(resizeAutomatically);
		c.gridy++;

		panel.add(miscPanel, c);

		pack();
		setResizable(false);
		setLocation(wnd.getX() + wnd.getWidth() / 2 - getWidth() / 2, wnd.getY() + wnd.getHeight() / 2 - getHeight() / 2);
		setVisible(true);
	}
}
