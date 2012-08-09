package smartsound.view.gui;
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



import java.awt.Component;
import java.awt.Graphics;

import javax.swing.border.TitledBorder;

public class NonTitledBorder extends TitledBorder {

	public NonTitledBorder() {
		super("");
	}

	@Override
	public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int width,
			final int height) {
		getBorder().paintBorder(c, g, x, y, width, height);
	}

}
