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

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JSeparator;

public class TitledSeparator extends JSeparator {
	
	private String title;
	private boolean showBar;
	
	TitledSeparator(String title, boolean showBar) {
		super();
		this.showBar = showBar;
		this.setPreferredSize(new Dimension(150, 20));
		
		this.title = title;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		if (showBar)
			super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		
		FontMetrics metrics = g2d.getFontMetrics();
		
		int x = (int) (getWidth() / 2 - metrics.getStringBounds(title, g2d).getWidth() / 2);
		int y = getHeight() / 2 + metrics.getAscent() / 2 - metrics.getDescent() / 2;
		
		g2d.drawString(title, x, y);
		
	}
}
