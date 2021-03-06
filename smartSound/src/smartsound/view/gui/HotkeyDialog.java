/*
 *	Copyright (C) 2012 Andr� Becker
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
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class HotkeyDialog extends JDialog {

	private KeyEvent event = null;

	public HotkeyDialog(final Window window) {
		super(window);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setAlwaysOnTop(true);
		setUndecorated(true);

		addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(final KeyEvent arg0) {}

			@Override
			public void keyReleased(final KeyEvent event) {
				HotkeyDialog.this.event = event;
				HotkeyDialog.this.setVisible(false);
			}

			@Override
			public void keyTyped(final KeyEvent arg0) {}

		});

		setSize(150, 75);
		JLabel label = new JLabel("<html><p align=\"center\">Press hotkey or ESC to abort.</p></html>");
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEtchedBorder());
		panel.add(label);
		add(panel);
		setLocation(window.getX() + window.getWidth() / 2 - getWidth() / 2, window.getY() + window.getHeight() / 2 - getHeight() / 2);

		setVisible(true);
	}

	public KeyEvent getEvent() {
		return event;
	}
}
