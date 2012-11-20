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

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import smartsound.common.IElement.NameValuePair;
import smartsound.view.gui.IconManager.IconType;

public class PlayListSetToolBar extends JToolBar implements IGUILadder{

	private final IGUILadder parent;
	private int sceneCounter = 1;

	public PlayListSetToolBar(final IGUILadder parent, final UUID playListSetUUID) {
		this.parent = parent;
		JLabel label = new ButtonLabel(IconManager.getImage(IconType.PLAY).getScaledInstance(-1, 22, Image.SCALE_DEFAULT));
		label.setCursor(new Cursor(Cursor.HAND_CURSOR));
		label.setToolTipText("Play");
		label.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				getGUIController().act(playListSetUUID, "PLAY");
			}

			@Override
			public void mouseEntered(final MouseEvent e) {

			}

			@Override
			public void mouseExited(final MouseEvent e) {
			}

			@Override
			public void mousePressed(final MouseEvent e) {
				if (e.isPopupTrigger())
					showPopup(e);
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				if (e.isPopupTrigger())
					showPopup(e);
			}

			private void showPopup(final MouseEvent e) {
				JPopupMenu menu = new JPopupMenu();
				JMenu hotkeyMenu = new JMenu("Hotkeys");
				hotkeyMenu.setIcon(new ImageIcon(IconManager.getImage(IconType.HOTKEY)));
				hotkeyMenu.add(new TitledSeparator("Add Hotkeys", false));
				menu.add(hotkeyMenu);

				NameValuePair[] pairs = getGUIController().get(playListSetUUID, "NAME");
				assert pairs.length == 1;
				assert pairs[0].value instanceof String;

				hotkeyMenu.add(new AddMenuItem(new AbstractAction("Play '" + (String) pairs[0].value + "'") {
					@Override
					public void actionPerformed(final ActionEvent e) {
						NameValuePair[] pairs = getGUIController().get(playListSetUUID, "NAME");
						assert pairs.length == 1;
						assert pairs[0].value instanceof String;

						Window wnd = SwingUtilities.getWindowAncestor(PlayListSetToolBar.this);
						HotkeyDialog dialog = new HotkeyDialog(wnd);
						KeyEvent event = dialog.getEvent();
						if (event.getKeyCode() == KeyEvent.VK_ESCAPE)
							return;
						parent.getGUIController().addActHotkey(event, playListSetUUID, "PLAY");
						wnd.toFront();
					}
				}));

				List<RemoveHotkeyMenuItem> removeList = new LinkedList<RemoveHotkeyMenuItem>();
				/*
				Action action = getGUIController().getPlayPlayListAction(playListSetUUID, "Play");
				for (Tuple<String,Action> t : getGUIController().getHotkeys(action))
					removeList.add(new RemoveHotkeyMenuItem(t.second, t.second.getDescription(), t.first, playListSetUUID, getGUIController()));

				action = getGUIController().getStopAction(playListSetUUID, "Stop");
				for (Tuple<String,Action> t : getGUIController().getHotkeys(action))
					removeList.add(new RemoveHotkeyMenuItem(t.second, t.second.getDescription(), t.first, playListSetUUID, getGUIController()));

				action = getGUIController().getGUIController().getVolumeAction(playListSetUUID, "Set Volume");
				for (Tuple<String,Action> t : getGUIController().getHotkeys(action))
					removeList.add(new RemoveHotkeyMenuItem(t.second, t.second.getDescription(), t.first, playListSetUUID, getGUIController()));
				 */

				if (!removeList.isEmpty()) {
					hotkeyMenu.add(new TitledSeparator("Remove hotkeys", false));
					for (RemoveHotkeyMenuItem item : removeList)
						hotkeyMenu.add(item);
				}

				propagatePopupMenu(menu, e);
			}

		});
		add(label);

		label = new ButtonLabel(IconManager.getImage(IconType.STOP).getScaledInstance(-1, 22, Image.SCALE_DEFAULT));
		label.setCursor(new Cursor(Cursor.HAND_CURSOR));
		label.setToolTipText("Stop");
		label.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(final MouseEvent arg0) {
				getGUIController().act(playListSetUUID, "STOP");
			}

			@Override
			public void mouseEntered(final MouseEvent arg0) {
			}

			@Override
			public void mouseExited(final MouseEvent arg0) {
			}

			@Override
			public void mousePressed(final MouseEvent e) {
				if (e.isPopupTrigger())
					showPopup(e);
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				if (e.isPopupTrigger())
					showPopup(e);
			}

			private void showPopup(final MouseEvent e) {
				JPopupMenu menu = new JPopupMenu();
				JMenu hotkeyMenu = new JMenu("Hotkeys");
				hotkeyMenu.setIcon(new ImageIcon(IconManager.getImage(IconType.HOTKEY)));
				hotkeyMenu.add(new TitledSeparator("Add Hotkeys", false));
				menu.add(hotkeyMenu);

				NameValuePair[] pairs = getGUIController().get(playListSetUUID, "NAME");
				assert pairs.length == 1;
				assert pairs[0].value instanceof String;

				hotkeyMenu.add(new AddMenuItem(new AbstractAction("Stop '" + (String) pairs[0].value + "'") {
					@Override
					public void actionPerformed(final ActionEvent e) {
						NameValuePair[] pairs = getGUIController().get(playListSetUUID, "NAME");
						assert pairs.length == 1;
						assert pairs[0].value instanceof String;

						Window wnd = SwingUtilities.getWindowAncestor(PlayListSetToolBar.this);
						HotkeyDialog dialog = new HotkeyDialog(wnd);
						KeyEvent event = dialog.getEvent();
						if (event.getKeyCode() == KeyEvent.VK_ESCAPE)
							return;
						parent.getGUIController().addActHotkey(event, playListSetUUID, "STOP");
						wnd.toFront();
					}
				}));

				List<RemoveHotkeyMenuItem> removeList = new LinkedList<RemoveHotkeyMenuItem>();
				/*
				Action action = getGUIController().getPlayPlayListAction(playListSetUUID, "Play");
				for (Tuple<String,Action> t : getGUIController().getHotkeys(action))
					removeList.add(new RemoveHotkeyMenuItem(t.second, t.second.getDescription(), t.first, playListSetUUID, getGUIController()));

				action = getGUIController().getStopAction(playListSetUUID, "Stop");
				for (Tuple<String,Action> t : getGUIController().getHotkeys(action))
					removeList.add(new RemoveHotkeyMenuItem(t.second, t.second.getDescription(), t.first, playListSetUUID, getGUIController()));

				action = getGUIController().getGUIController().getVolumeAction(playListSetUUID, "Set Volume");
				for (Tuple<String,Action> t : getGUIController().getHotkeys(action))
					removeList.add(new RemoveHotkeyMenuItem(t.second, t.second.getDescription(), t.first, playListSetUUID, getGUIController()));
				 */

				if (!removeList.isEmpty()) {
					hotkeyMenu.add(new TitledSeparator("Remove hotkeys", false));
					for (RemoveHotkeyMenuItem item : removeList)
						hotkeyMenu.add(item);
				}

				propagatePopupMenu(menu, e);
			}

		});
		add(label);

		final VolumeLabel volumeLabel = new VolumeLabel(IconManager.getImage(IconType.VOLUME).getScaledInstance(-1, 22, Image.SCALE_DEFAULT), playListSetUUID, this);
		volumeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		volumeLabel.setToolTipText("Set volume");

		final NameValuePair[] pairs = getGUIController().get(playListSetUUID, "VOLUME");
		assert pairs.length == 1;
		assert pairs[0].value instanceof Float;

		MouseAdapter mouseAdapter = new MouseAdapter() {

			private boolean draggingSpeaker = false;
			private boolean dragged = false;
			private boolean movedClockwise = (Float) pairs[0].value > 0.5f;

			@Override
			public void mouseClicked(final MouseEvent arg0) {

			}

			@Override
			public void mouseEntered(final MouseEvent arg0) {
				volumeLabel.setShowVolume(true);
			}

			@Override
			public void mouseExited(final MouseEvent arg0) {
				volumeLabel.setShowVolume(false);
			}

			@Override
			public void mousePressed(final MouseEvent e) {
				if (e.isPopupTrigger())
					showPopup(e);
				else
					draggingSpeaker = true;

				dragged = false;
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				if (e.isPopupTrigger())
					showPopup(e);
			}

			@Override
			public void mouseDragged(final MouseEvent e) {
				if (!draggingSpeaker)
					return;

				dragged = true;

				int degrees = volumeLabel.posToAngle(e.getPoint());
				float percentage = (float) (degrees / 360D);
				if (percentage < 0.25D && movedClockwise)
					percentage = 1.0F;
				else if (percentage > 0.75D && !movedClockwise)
					percentage = 0.0F;
				else
					movedClockwise = percentage > 0.5F;

					volumeLabel.setVolume(percentage);
					getGUIController().set(playListSetUUID, "VOLUME", percentage);
			}

			private void showPopup(final MouseEvent e) {
				JPopupMenu menu = new JPopupMenu();
				JMenu hotkeyMenu = new JMenu("Hotkeys");
				hotkeyMenu.setIcon(new ImageIcon(IconManager.getImage(IconType.HOTKEY)));
				hotkeyMenu.add(new TitledSeparator("Add Hotkeys", false));
				menu.add(hotkeyMenu);

				NameValuePair[] pairs = getGUIController().get(playListSetUUID, "NAME");
				assert pairs.length == 1;
				assert pairs[0].value instanceof String;

				hotkeyMenu.add(new AddMenuItem(new AbstractAction("Set volume for '" + (String) pairs[0].value + "'") {
					@Override
					public void actionPerformed(final ActionEvent arg0) {
						Window wnd = SwingUtilities.getWindowAncestor(PlayListSetToolBar.this);
						HotkeyDialog dialog = new HotkeyDialog(wnd);
						KeyEvent event = dialog.getEvent();
						if (event.getKeyCode() == KeyEvent.VK_ESCAPE)
							return;
						NameValuePair[] pairs = getGUIController().get(playListSetUUID, "VOLUME", "NAME");
						assert pairs.length == 2;
						assert pairs[0].value instanceof Float;
						assert pairs[1].value instanceof String;

						Double result = UserInput
								.getInput(PlayListSetToolBar.this, 0, 100, 1, (Float) pairs[0].value * 100);
						float volume;
						try {
							volume = result.floatValue() / 100.0f;
							volume = Math.max(volume, 0);
							volume = Math.min(volume, 100);
						} catch (NumberFormatException e) {
							return;
						}
						Map<String, Object> values = new HashMap<String, Object>();
						values.put("VOLUME", volume);
						parent.getGUIController().addSetHotkey(event, playListSetUUID, values);
						wnd.toFront();
					}
				}));

				List<RemoveHotkeyMenuItem> removeList = new LinkedList<RemoveHotkeyMenuItem>();
				/*
				Action action = getGUIController().getPlayPlayListAction(playListSetUUID, "Play");
				for (Tuple<String,Action> t : getGUIController().getHotkeys(action))
					removeList.add(new RemoveHotkeyMenuItem(t.second, t.second.getDescription(), t.first, playListSetUUID, getGUIController()));

				action = getGUIController().getStopAction(playListSetUUID, "Stop");
				for (Tuple<String,Action> t : getGUIController().getHotkeys(action))
					removeList.add(new RemoveHotkeyMenuItem(t.second, t.second.getDescription(), t.first, playListSetUUID, getGUIController()));

				action = getGUIController().getGUIController().getVolumeAction(playListSetUUID, "Set Volume");
				for (Tuple<String,Action> t : getGUIController().getHotkeys(action))
					removeList.add(new RemoveHotkeyMenuItem(t.second, t.second.getDescription(), t.first, playListSetUUID, getGUIController()));
				 */

				if (!removeList.isEmpty()) {
					hotkeyMenu.add(new TitledSeparator("Remove hotkeys", false));
					for (RemoveHotkeyMenuItem item : removeList)
						hotkeyMenu.add(item);
				}

				propagatePopupMenu(menu, e);
			}

		};

		volumeLabel.addMouseListener(mouseAdapter);
		volumeLabel.addMouseMotionListener(mouseAdapter);
		add(volumeLabel);

		label = new ButtonLabel(IconManager.getImage(IconType.ADD).getScaledInstance(-1, 22, Image.SCALE_DEFAULT));
		label.setCursor(new Cursor(Cursor.HAND_CURSOR));
		label.setToolTipText("Add new Playlist");
		label.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(final MouseEvent arg0) {
				StringInputDialog dlg = new StringInputDialog(SwingUtilities.getWindowAncestor(PlayListSetToolBar.this), "Please enter a name for the new playlist and press Enter.", "New Playlist " + sceneCounter++);
				dlg.setVisible(true);

				UUID uuid = getGUIController().add(playListSetUUID, "PLAYLIST");
				NameValuePair[] setPair = new NameValuePair[1];
				setPair[0] = NameValuePair.create("NAME", dlg.getTextInput());
				getGUIController().set(uuid, setPair);
			}

			@Override
			public void mouseEntered(final MouseEvent arg0) {
			}

			@Override
			public void mouseExited(final MouseEvent arg0) {
			}

			@Override
			public void mousePressed(final MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(final MouseEvent arg0) {
			}

		});
		add(label);
		setFloatable(false);
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder());
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
