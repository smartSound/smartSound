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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class ButtonLabel extends JLabel implements MouseListener {
	private final Icon defaultIcon;
	private final Icon mouseOverIcon;
	private final Icon activeIcon;
	private boolean active = false;

	private final Color mouseOverColor = new Color(0xFF, 0xFF, 0xFF, 0xB0);
	private final Color activeColor = new Color(0xDAA520);

	public ButtonLabel(final Image defaultImage) {
		super(new ImageIcon(defaultImage));
		this.defaultIcon = new ImageIcon(defaultImage);

		BufferedImage tmp = new BufferedImage(defaultImage.getWidth(null),
				defaultImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		tmp.createGraphics().drawImage(defaultImage, 0, 0, null);

		mouseOverIcon = new ImageIcon(GlowUtils.addShadow(tmp, mouseOverColor, 1, 3, true));
		activeIcon = new ImageIcon(GlowUtils.addGlow(tmp, activeColor, 8, 128, true));

		addMouseListener(this);
		setCursor(new Cursor(Cursor.HAND_CURSOR));
	}

	public void setActive(final boolean active) {
		this.active = active;
		if (active)
			setIcon(activeIcon);
		else
			setIcon(defaultIcon);
	}

	@Override
	public void mouseClicked(final MouseEvent arg0) {}

	@Override
	public void mouseEntered(final MouseEvent arg0) {
		if (!active)
			setIcon(mouseOverIcon);
		repaint();
	}

	@Override
	public void mouseExited(final MouseEvent arg0) {
		if (!active)
			setIcon(defaultIcon);
		repaint();
	}

	@Override
	public void mousePressed(final MouseEvent arg0) {}

	@Override
	public void mouseReleased(final MouseEvent arg0) {}
}