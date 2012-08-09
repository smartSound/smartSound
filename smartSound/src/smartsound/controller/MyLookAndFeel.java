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


public class MyLookAndFeel extends javax.swing.plaf.nimbus.NimbusLookAndFeel {

	@Override
	public Color getDerivedColor(final String uiDefaultParentName,
			final float hOffset, final float sOffset, final float bOffset, final int aOffset,
			final boolean uiResource) {
		Color result = super.getDerivedColor(uiDefaultParentName, hOffset, sOffset, bOffset, aOffset, uiResource);

		return result;
	}


}
