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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import smartsound.view.Action;

public class RemoveHotkeyMenuItem extends JMenuItem {
	
	protected Action action;
	protected GUIController controller;
	
	public RemoveHotkeyMenuItem(final Action action, String description, final GUIController con) {
		super(new AbstractAction(description) {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				con.removeHotkey(action);
			}
		});
		this.controller = con;
		this.action = action;
		Image img = new ImageIcon("images/delete.png").getImage();
		int border = getBorder().getBorderInsets(this).top + getBorder().getBorderInsets(this).bottom;
		Image newimg = img.getScaledInstance(-1, getPreferredSize().height - border,  java.awt.Image.SCALE_SMOOTH);
		setIcon(new ImageIcon(newimg));
		setAccelerator(getKeyStroke());
	}

	private KeyStroke getKeyStroke() {
		String[] split = controller.getHotkey(action).split("\\|");
		return KeyStroke.getKeyStroke(Integer.parseInt(split[1]),Integer.parseInt(split[0]));
	}


}
