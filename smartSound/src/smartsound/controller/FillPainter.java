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

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.Painter;

class FillPainter implements Painter<JComponent> {

	private final Color color;

	FillPainter(final Color c) {
		color = c;
	}

	@Override
	public void paint(final Graphics2D g, final JComponent object, final int width, final int height) {
		g.setColor(color);
		g.fillRect(0, 0, width - 1, height - 1);
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, width - 1, height - 1);
	}
}