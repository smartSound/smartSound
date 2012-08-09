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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JToolBar;

public class PanelWrapper extends JPanel {

	private final JPanel panel;

	public PanelWrapper(final JPanel panel, final JToolBar toolBar) {
		super(new GridBagLayout());

		this.panel = panel;

		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 0.5f;
		c.weighty = 0.0f;
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		add(toolBar, c);

		//		c.gridy = 1;
		//		add(new JSeparator(), c);

		c.gridy = 2;
		c.weighty = 1;
		add(panel, c);
	}
}
