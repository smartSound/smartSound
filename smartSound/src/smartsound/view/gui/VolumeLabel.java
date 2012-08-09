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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.UUID;

import javax.swing.JPopupMenu;

public class VolumeLabel extends ButtonLabel implements IGUILadder {

	private boolean showVolume = false;
	private float volume;
	private final UUID uuid;
	private final IGUILadder parent;

	public VolumeLabel(final Image defaultImage, final UUID uuid, final IGUILadder parent) {
		super(defaultImage);

		this.uuid = uuid;
		this.parent = parent;

		volume = getGUIController().getVolume(uuid);
	}

	public boolean isShowVolume() {
		return showVolume;
	}

	public void setShowVolume(final boolean showVolume) {
		this.showVolume = showVolume;
	}

	public float getVolume() {
		return volume;
	}

	public void setVolume(final float volume) {
		this.volume = volume;
	}

	@Override
	public GUIController getGUIController() {
		return parent.getGUIController();
	}
	@Override
	public void propagateHotkey(final KeyEvent event) {
		parent.propagateHotkey(event);
	}
	@Override
	public void propagatePopupMenu(final JPopupMenu menu, final MouseEvent e) {
		parent.propagatePopupMenu(menu, e);
	}
	@Override
	public void updateMinimumSize() {
		parent.updateMinimumSize();
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;

		if (showVolume) {
			int min = Math.min(getWidth(), getHeight());
			min = (min / 2) * 2 - 1;
			g2d.setColor(Color.LIGHT_GRAY);
			g2d.fillOval(0, 0, min, min);
			g2d.setColor(Color.BLUE);
			g2d.fillArc(0, 0, min, min, 90,
					(int) (-volume * 360D));
			g2d.setColor(Color.BLACK);
			g2d.drawOval(0, 0, min, min);
			g2d.setColor(Color.WHITE);
			String percentage = String.valueOf((int) (volume * 100D));
			Font font = g2d.getFont();
			g2d.setFont(new Font(font.getName(), font.getStyle(), 10));
			FontMetrics fm = g2d.getFontMetrics();
			int stringWidth = fm.stringWidth(percentage);
			int textY = (((min + 1) / 2) - (fm
					.getAscent() + fm.getDescent()) / 2) + fm.getAscent();
			g2d.drawString(percentage, (min - stringWidth) / 2,
					textY);
		}
	}

	public int posToAngle(final Point point) {
		int imageCenterX = getWidth() / 2;
		int imageCenterY = getHeight() / 2;
		double theta = Math.atan2(point.y - imageCenterY, point.x
				- imageCenterX);
		for (theta += 1.5707963267948966D; theta < 0.0D; theta += 6.2831853071795862D);
		return (int) Math.toDegrees(theta);
	}
}
