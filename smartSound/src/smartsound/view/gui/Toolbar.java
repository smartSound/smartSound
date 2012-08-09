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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileFilter;

import smartsound.view.gui.IconManager.IconType;

public class Toolbar extends JToolBar implements IGUILadder {
	protected final IGUILadder parent;
	protected final JFileChooser fileChooser;

	public Toolbar(final IGUILadder parent) {
		super();
		this.parent = parent;
		setFloatable(false);
		setBorder(BorderFactory.createEmptyBorder());


		fileChooser = new JFileChooser();

		fileChooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(final File file) {
				return file.getPath().endsWith(".ssf");
			}

			@Override
			public String getDescription() {
				return "smartSound files (*.ssf)";
			}
		});

		JLabel lbl = new ButtonLabel(IconManager.getImage(IconType.LOAD));
		lbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				int result = fileChooser.showOpenDialog(null);
				if(result == JFileChooser.APPROVE_OPTION)
					getGUIController().getLoadAction().execute(
							fileChooser.getSelectedFile().getAbsolutePath());
			}
		});
		lbl.setToolTipText("Load");
		add(lbl);

		lbl = new ButtonLabel(IconManager.getImage(IconType.SAVE));
		lbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				int result = fileChooser.showSaveDialog(null);
				if(result == JFileChooser.APPROVE_OPTION) {
					String path = fileChooser.getSelectedFile().getAbsolutePath();
					if (!path.contains("."))
						path += ".ssf";
					getGUIController().getSaveAction().execute(path);
				}
			}
		});
		lbl.setToolTipText("Save");
		add(lbl);

		lbl = new ButtonLabel(IconManager.getImage(IconType.HOTKEY));
		lbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				new HotkeySettings(Toolbar.this, Toolbar.this);
			}
		});
		lbl.setToolTipText("Hotkeys");
		add(lbl);

		lbl = new ButtonLabel(IconManager.getImage(IconType.SETTINGS));
		lbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				new PreferencesPage(Toolbar.this);
			}
		});
		lbl.setToolTipText("Preferences");
		add(lbl);
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

}
