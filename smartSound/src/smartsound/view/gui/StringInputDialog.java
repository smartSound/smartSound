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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class StringInputDialog extends JDialog {

	private String result = null;

	public StringInputDialog(final Window window, final String displayText, final String defaultText) {
		super(window);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setAlwaysOnTop(true);
		setUndecorated(true);

		final JTextField textField = new JTextField(defaultText);

		textField.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(final KeyEvent arg0) {}

			@Override
			public void keyReleased(final KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.VK_ENTER) {
					result = textField.getText();
					setVisible(false);
				}
			}

			@Override
			public void keyTyped(final KeyEvent arg0) {}

		});

		setSize(250, 75);
		JLabel label = new JLabel("<html><p align=\"center\">" + displayText + "</p></html>");
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.5;
		c.weighty = 0.5;

		panel.setBorder(BorderFactory.createEtchedBorder());
		panel.add(label, c);
		c.gridy = 1;
		panel.add(textField, c);
		add(panel);
		textField.selectAll();
		setLocation(window.getX() + window.getWidth() / 2 - getWidth() / 2, window.getY() + window.getHeight() / 2 - getHeight() / 2);
	}

	public String getTextInput() {
		return result;
	}
}
