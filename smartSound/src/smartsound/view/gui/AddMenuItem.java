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

import java.awt.Image;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import smartsound.view.gui.IconManager.IconType;

public class AddMenuItem extends JMenuItem {

	public AddMenuItem(final AbstractAction action) {
		super(action);
		Image img = IconManager.getImage(IconType.ADD);
		setHorizontalAlignment(SwingConstants.LEFT);
		int border = getBorder().getBorderInsets(this).top + getBorder().getBorderInsets(this).bottom;
		setIcon(new ImageIcon(img.getScaledInstance(-1, getPreferredSize().height - border,  java.awt.Image.SCALE_SMOOTH)));
		setBackground(UIManager.getColor("Menu.background"));
		setOpaque(false);
	}
}
