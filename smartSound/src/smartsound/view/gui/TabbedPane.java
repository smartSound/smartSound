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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import smartsound.common.IObserver;
import smartsound.common.Tuple;
import smartsound.view.AbstractViewController.PositionType;
import smartsound.view.Action;
import smartsound.view.ILayoutObserver;
import smartsound.view.Layout;
import smartsound.view.gui.IconManager.IconType;

public class TabbedPane extends JTabbedPane implements IGUILadder, ChangeListener, MouseListener, MouseMotionListener, IObserver, ILayoutObserver {

	private final IGUILadder parent;
	private int selectedIndex = getSelectedIndex();
	protected final Map<Integer, PlayListSetPanel> indexMap = new HashMap<Integer, PlayListSetPanel>();
	protected final Map<PlayListSetPanel, Integer> invertedIndexMap = new HashMap<PlayListSetPanel, Integer>();
	protected final Map<UUID, PlayListSetPanel> uuidMap = new HashMap<UUID, PlayListSetPanel>();
	private boolean locked = false;
	protected Point currentClickLocation;
	private boolean dragging;
	private int sceneCounter = 1;

	public TabbedPane(final IGUILadder parent) {
		super();
		this.parent = parent;
		addTab("", new ImageIcon(IconManager.getImage(IconType.ADD)), null);
		setSelectedIndex(-1);
		setUI(new CustomTabbedPaneUI());

		for (MouseListener listener : getMouseListeners()) {
			removeMouseListener(listener);
		}

		addChangeListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		getGUIController().addObserver(this, null);
		getGUIController().addLayoutObserver(this, null);
		locked = true;
	}

	@Override
	public void addMouseListener(final MouseListener listener) {
		if (!locked)
			super.addMouseListener(listener);
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
	public void stateChanged(final ChangeEvent e) {
		if (getTabCount() > 1 && getSelectedIndex() == getTabCount() - 1) {
			setSelectedIndex(Math.max(0,selectedIndex));

			StringInputDialog dlg = new StringInputDialog(SwingUtilities.getWindowAncestor(this), "Enter a name for the new scene and press enter.", "New scene " + sceneCounter ++);
			dlg.setVisible(true);
			UUID uuid = getGUIController().addPlayListSet(null);
			getGUIController().setTitle(uuid, dlg.getTextInput());
		}

		selectedIndex = getSelectedIndex();
		updateMinimumSize();
	}

	public void newTab(final String title, final PlayListSetPanel panel,
			final String string) {
		int newIndex = getTabCount() - 1;

		JToolBar bar = new PlayListSetToolBar(this, panel.getPlayListSetUUID());
		super.insertTab(title,null, new PanelWrapper(panel, bar), null, newIndex);
		super.setSelectedIndex(newIndex);

		getGUIController().addObserver(this, panel.getPlayListSetUUID());
		indexMap.put(newIndex, panel);
		invertedIndexMap.put(panel, newIndex);
		uuidMap.put(panel.getPlayListSetUUID(), panel);
	}

	@Override
	public void updateMinimumSize() {
		parent.updateMinimumSize();
	}

	@Override
	public Dimension getMinimumSize() {
		Insets insets = new Insets(45, 10, 10, 10); //FIXME: calculate from UI
		int width = insets.left + insets.right;
		int height = insets.top + insets.bottom;

		int selectedIndex = getSelectedIndex();
		Component c = selectedIndex > -1 ? getComponentAt(getSelectedIndex()) : null;
		if (c != null) {
			width += c.getMinimumSize().width;
			height += c.getMinimumSize().height;
		} else {
		}

		return new Dimension(width, height);
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		Point p = e.getPoint();
		int index = getTabIndex(p);
		if (index > -1) {
			if (e.getClickCount() == 2)
				getGUIController().getPlayPlayListAction(
						indexMap.get(index).getPlayListSetUUID(), "Play").execute();

			setSelectedIndex(index);
		}
	}

	@Override
	public void mouseEntered(final MouseEvent e) {

	}

	@Override
	public void mouseExited(final MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(final MouseEvent e) {
		if (e.isPopupTrigger()) {
			showPopup(e);
			return;
		}
		currentClickLocation = e.getPoint();
	}

	@Override
	public void mouseDragged(final MouseEvent e) {
		if (currentClickLocation != null) {
			dragging = true;

			int currentIndex = getTabIndex(e.getPoint());
			boolean changed = currentIndex != ((CustomTabbedPaneUI) getUI()).dropTargetIndex;
			((CustomTabbedPaneUI) getUI()).dropTargetIndex = currentIndex;
			if (changed)
				repaint();
		}
	}

	@Override
	public void mouseMoved(final MouseEvent e) {

	}

	@Override
	public void mouseReleased(final MouseEvent e) {
		if (e.isPopupTrigger())
			showPopup(e);

		if (dragging) {
			int sourceIndex = getTabIndex(currentClickLocation);
			int targetIndex = getTabIndex(e.getPoint());

			if (sourceIndex > -1 && sourceIndex < getTabCount() - 1) {
				UUID sourceUUID = indexMap.get(sourceIndex).getPlayListSetUUID();
				getGUIController().shiftElement(sourceUUID, targetIndex, 0, PositionType.LEFT);
				setSelectedIndex(sourceIndex > targetIndex ? targetIndex : targetIndex - 1);
			}
		}

		currentClickLocation = null;
		dragging = false;
		((CustomTabbedPaneUI) getUI()).dropTargetIndex = -1;
		repaint();
	}

	private int getTabIndex(final Point p) {
		Rectangle rect;
		int index = -1;
		for (int i = 0; i < getTabCount(); i++) {
			rect = getUI().getTabBounds(TabbedPane.this, i);
			if (rect.contains(p)) {
				index = i;
				break;
			}
		}
		return index;
	}

	private void showPopup(final MouseEvent e) {
		final int index = getTabIndex(e.getPoint());

		JPopupMenu menu = new JPopupMenu();
		if (index != -1 && index < getTabCount() - 1) {
			JMenu hotkeyMenu = new JMenu("Hotkeys");
			hotkeyMenu.add(new TitledSeparator("Add Hotkeys", false));
			hotkeyMenu.setIcon(new ImageIcon(IconManager.getImage(IconType.HOTKEY)));
			menu.add(hotkeyMenu);

			hotkeyMenu.add(new AddMenuItem(new AbstractAction("Play '" + getTitleAt(index) + "'") {
				@Override
				public void actionPerformed(final ActionEvent arg0) {
					Window wnd = SwingUtilities.getWindowAncestor(TabbedPane.this);
					HotkeyDialog dialog = new HotkeyDialog(wnd);
					KeyEvent event = dialog.getEvent();
					if (event.getKeyCode() == KeyEvent.VK_ESCAPE)
						return;
					parent.getGUIController().setHotkey(
							null,
							event,
							getGUIController().getPlayPlayListAction(
									indexMap.get(index).getPlayListSetUUID(),
									"Play '" + getTitleAt(index) + "'"));
					wnd.toFront();
				}

			}));

			menu.add(new AbstractAction("Remove Scene", new ImageIcon(IconManager.getImage(IconType.REMOVE))) {
				@Override
				public void actionPerformed(final ActionEvent arg0) {
					getGUIController().remove(indexMap.get(index).getPlayListSetUUID());
				}
			});

			menu.addSeparator();

			menu.add(new AbstractAction("Rename Scene") {
				@Override
				public void actionPerformed(final ActionEvent e) {
					StringInputDialog dialog = new StringInputDialog(SwingUtilities.getWindowAncestor(TabbedPane.this), "Choose a new name for the scene and press Enter", getTitleAt(index));
					dialog.setVisible(true);
					String input = dialog.getTextInput();
					getGUIController().setTitle(indexMap.get(index).getPlayListSetUUID(), input);
				}
			});

			List<RemoveHotkeyMenuItem> removeList = new LinkedList<RemoveHotkeyMenuItem>();
			Action action = getGUIController().getPlayPlayListAction(indexMap.get(index).getPlayListSetUUID(), "Play");
			for (Tuple<String,Action> t : getGUIController().getHotkeys(action))
				removeList.add(new RemoveHotkeyMenuItem(t.second, t.second.getDescription(), getGUIController()));

			action = getGUIController().getStopAction(indexMap.get(index).getPlayListSetUUID(), "Stop");
			for (Tuple<String,Action> t : getGUIController().getHotkeys(action))
				removeList.add(new RemoveHotkeyMenuItem(t.second, t.second.getDescription(), getGUIController()));

			action = getGUIController().getGUIController().getVolumeAction(indexMap.get(index).getPlayListSetUUID(), "Set Volume");
			for (Tuple<String,Action> t : getGUIController().getHotkeys(action))
				removeList.add(new RemoveHotkeyMenuItem(t.second, t.second.getDescription(), getGUIController()));

			if (!removeList.isEmpty()) {
				hotkeyMenu.add(new TitledSeparator("Remove hotkeys", false));
				for (RemoveHotkeyMenuItem item : removeList)
					hotkeyMenu.add(item);
			}
		}

		propagatePopupMenu(menu, e);
	}

	@Override
	public void update(final UUID uuid) {
		if (uuid == null) {
			removeChangeListener(this);
			Layout layout = getGUIController().getLayout(null);
			removeAll();
			setSelectedIndex(-1);
			addTab("", new ImageIcon(IconManager.getImage(IconType.ADD)), null);

			PlayListSetPanel panel;
			for (int i = 0; i < layout.getCount(); i++) {
				panel = new PlayListSetPanel(this,layout.getByCoordinates(i, 0));
				newTab(getGUIController().getTitle(layout.getByCoordinates(i, 0)), panel, "");
				panel.updateLayout();
			}
			addChangeListener(this);
			return;
		}
		PlayListSetPanel panel = uuidMap.get(uuid);
		assert panel != null;
		int index = invertedIndexMap.get(panel);

		setTitleAt(index, getGUIController().getTitle(uuid));
	}

	@Override
	public void updateLayout(final Layout layout) {
		update((UUID) null);
	}

	public UUID getUUIDAt(final int index) {
		PlayListSetPanel panel = indexMap.get(index);
		return panel != null ? panel.getPlayListSetUUID() : null;
	}
}
