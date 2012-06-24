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

import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JDialog;

public class HotkeyDialog extends JDialog {
	
	private KeyEvent event = null;
	
	public HotkeyDialog(Window window) {
		super(window);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setAlwaysOnTop(true);
		setUndecorated(true);
		
		this.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {}

			@Override
			public void keyReleased(KeyEvent event) {
				HotkeyDialog.this.event = event;
				HotkeyDialog.this.setVisible(false);
			}

			@Override
			public void keyTyped(KeyEvent arg0) {}
			
		});
		
		setSize(100, 50);
		setLocation(window.getX() + window.getWidth() / 2 - 50, window.getY() + window.getHeight() / 2 - 25);
		
		setVisible(true);
	}
	
	public KeyEvent getEvent() {
		return event;
	}
}
