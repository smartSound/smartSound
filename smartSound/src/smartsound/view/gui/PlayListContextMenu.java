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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;


public class PlayListContextMenu extends JPopupMenu
{
	private JMenuItem saveItem;
	private JMenuItem loadItem;
	private final PlayList list;
	protected GUIController controller;

	protected class DeleteAction extends AbstractAction
	{

		@Override
		public void actionPerformed(final ActionEvent arg0)
		{
			list.removeSelectedEntries();
		}
		public DeleteAction()
		{
			super("Delete selected");
		}
	}

	public PlayListContextMenu(final GUIController controller, final PlayList list)
	{
		this.list = list;
		this.controller = controller;
		JMenuItem item = new JMenuItem(new DeleteAction());
		add(item);

		setBackground(UIManager.getColor("Menu.background"));
	}

}
