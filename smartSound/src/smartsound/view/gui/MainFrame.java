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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPopupMenu;

import smartsound.settings.Global;

public class MainFrame extends JFrame implements IGUILadder {

	private final GUIController controller;

	public MainFrame(final GUIController controller) {
		this.controller = controller;

		addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(final KeyEvent arg0) {
			}

			@Override
			public void keyReleased(final KeyEvent arg0) {
				propagateHotkey(arg0);
			}

			@Override
			public void keyTyped(final KeyEvent arg0) {

			}

		});
	}

	@Override
	public GUIController getGUIController() {
		return controller;
	}

	@Override
	public void propagateHotkey(final KeyEvent event) {
		controller.executeHotkey(event);
	}

	@Override
	public void propagatePopupMenu(final JPopupMenu menu, final MouseEvent e) {
		controller.propagatePopupMenu(menu, e);
	}

	@Override
	public void updateMinimumSize() {
		boolean resize = true;
		try {
			resize = Global.getInstance().getProperty("resize_automatically").toLowerCase().equals("true");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(resize);
		
		setMinimumSize(getComponent(0).getMinimumSize());
		if ((getExtendedState() & JFrame.MAXIMIZED_BOTH) != JFrame.MAXIMIZED_BOTH && !getSize().equals(getMinimumSize())) {
			if (getMinimumSize().width < getSize().width && getMinimumSize().height < getSize().height) {
				if (resize)
					setSize(getMinimumSize());
			} else {
				setSize(getMinimumSize());
			}
		}

	}

}
