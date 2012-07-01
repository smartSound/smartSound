/* 
 *	Copyright (C) 2012 Andr� Becker
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.swing.AbstractAction;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import smartsound.common.Tuple;
import smartsound.player.ItemData;
import smartsound.view.Action;

public class PlayList extends JList<ItemData> implements ListDataListener,
		IGUILadder {
	class ChainWithTuple implements Comparable<ChainWithTuple> {

		public int getStartIndex() {
			return startIndex;
		}

		public int getEndIndex() {
			return endIndex;
		}

		public int compareTo(ChainWithTuple cwt) {
			return Math.abs(startIndex - endIndex)
					- Math.abs(cwt.startIndex - cwt.endIndex);
		}

		private int startIndex;
		private int endIndex;

		public ChainWithTuple(int startIndex, int endIndex) {
			this.startIndex = startIndex;
			this.endIndex = endIndex;
		}
	}

	private static final long serialVersionUID = 0x1d435aeadf9beb73L;
	private static final Color activeColor = Color.BLUE;
	private static final int RIGHTBORDER = 40;
	private javax.swing.JList.DropLocation dropLocation;
	private int dropAction;
	private int dropSourceIndex;
	private PlayListPanel panel;
	protected Point lastMouseClickPosition;
	private Action playIndexAction;
	private Action playUUIDAction;
	private Action setRepeatItemAction;
	private Action setChainWithAction;
	protected IGUILadder parent;

	public PlayList(IGUILadder parent, final PlayListDataModel model) {
		super(model);
		this.parent = parent;
		playIndexAction = parent.getGUIController().getPlayIndexAction(model.getUUID());
		playUUIDAction = parent.getGUIController().getPlayItemAction(model.getUUID());
		setRepeatItemAction = parent.getGUIController().getSetRepeatItemAction(model.getUUID());
		setChainWithAction = parent.getGUIController().getSetItemChainWithAction(model.getUUID());
		
		lastMouseClickPosition = null;
		model.addListDataListener(this);
		getInputMap()
				.put(KeyStroke.getKeyStroke("released DELETE"), "released");
		getActionMap().put("released", new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				((PlayList) e.getSource()).removeSelectedEntries();
			}
		});
		setAutoscrolls(true);
		addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				PlayList playList = (PlayList) e.getSource();
				int index = playList.locationToIndex(e.getPoint());
				if (index < 0)
					return;
				if (e.getClickCount() == 2)
					playList.play(index);
				else if (playList.insideRepeatRectOf(e.getPoint()) == index) {
					ItemData data = model.getElementAt(index);
					setRepeatItemAction.execute(data.getUUID(), !data.isRepeating());
				}
			}
		});
		addMouseMotionListener(new MouseAdapter() {

			public void mouseMoved(MouseEvent e) {
				PlayList playList = (PlayList) e.getSource();
				if (playList.insideChainRectOf(e.getPoint()) != -1
						|| playList.insideRepeatRectOf(e.getPoint()) != -1)
					playList.setCursor(Cursor.getPredefinedCursor(12));
				else
					playList.setCursor(Cursor.getDefaultCursor());
				panel.setShowVolume(false);
			}
		});
		addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger())
					showMenu(e);
				lastMouseClickPosition = e.getPoint();
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger())
					showMenu(e);
				lastMouseClickPosition = null;
			}

			private void showMenu(final MouseEvent e) {
				PlayListContextMenu menu = new PlayListContextMenu(getGUIController(),
						(PlayList) e.getSource());
				JMenu hotkeyMenu = new JMenu("Hotkeys");
				menu.add(hotkeyMenu);
				
				int index = locationToIndex(e.getPoint());
				if (index != -1) {
					final ItemData item = getElementAt(locationToIndex(e.getPoint()));
					hotkeyMenu.add(new TitledSeparator("Add hotkeys", false));
					hotkeyMenu.add(
							new AddMenuItem(
									new AbstractAction("Play '"
							+ item.toString() + "')") {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							Window wnd = SwingUtilities
									.getWindowAncestor(PlayList.this);
							HotkeyDialog dialog = new HotkeyDialog(wnd); KeyEvent event = dialog.getEvent(); if (event.getKeyCode() == KeyEvent.VK_ESCAPE) return;
							PlayList.this.parent.getGUIController().setHotkey(
									event,
									playUUIDAction.specialize(item.getUUID()));
							wnd.toFront();
						}

					}));
					hotkeyMenu.add(new AddMenuItem(
							new AbstractAction("Set 'repeat' for '"
							+ item.toString() + "')") {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							Window wnd = SwingUtilities
									.getWindowAncestor(PlayList.this);
							HotkeyDialog dialog = new HotkeyDialog(wnd); KeyEvent event = dialog.getEvent(); if (event.getKeyCode() == KeyEvent.VK_ESCAPE) return;
							String result = (String) UserInput.getInput(panel, "Turn on", "Turn off");
							PlayList.this.parent.getGUIController().setHotkey(
									event,
									setRepeatItemAction.specialize(item.getUUID(), result.equals("Turn on")));
							wnd.toFront();
						}
					}));
					hotkeyMenu.add(new AddMenuItem(
							new AbstractAction("Set chaining for '"
							+ item.toString() + "')") {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							Window wnd = SwingUtilities
									.getWindowAncestor(PlayList.this);
							HotkeyDialog dialog = new HotkeyDialog(wnd); KeyEvent event = dialog.getEvent(); if (event.getKeyCode() == KeyEvent.VK_ESCAPE) return;
							Object[] possibilities = new Object[model.getSize()];
							possibilities[0] = "None";
							int j = 1;
							ItemData data;
							for (int i = 0; i < model.getSize(); i++) {
								data = model.getElementAt(i);
								if (!data.getUUID().equals(item.getUUID())) {
									possibilities[j++] = data;
								}
							}
							Object result = UserInput.getInput(panel, possibilities);
							PlayList.this.parent.getGUIController().setHotkey(
									event,
									setChainWithAction.specialize(item.getUUID(), result.equals("None") ? null : ((ItemData) result).getUUID()));
							wnd.toFront();
						}
					}));
				} else {
					JMenuItem addHotkeyItem = new JMenuItem();
					addHotkeyItem.setText("Click on an entry to add hotkeys");
					addHotkeyItem.setEnabled(false);
					hotkeyMenu.add(addHotkeyItem);
				}
				
				String itemTitle;
				UUID itemUUID;
				List<JMenuItem> itemList = new LinkedList<JMenuItem>();
				
				List<Tuple<String,Action>> hotkeyList = PlayList.this.parent.getGUIController().getHotkeys(playUUIDAction);
				for (Tuple<String,Action> tuple : hotkeyList) {
					itemTitle = "Play '";
					itemUUID = (UUID) tuple.second.getDefaultParam(tuple.second.getNoOfDefaultParams() - 1);
					itemTitle += getElement(itemUUID).toString();
					itemTitle += "'";
					
					itemList.add(new RemoveHotkeyMenuItem(tuple.second, itemTitle, getGUIController()));
				}
				
				hotkeyList = PlayList.this.parent.getGUIController().getHotkeys(setRepeatItemAction);
				for (Tuple<String,Action> tuple : hotkeyList) {
					itemTitle = (boolean) tuple.second.getLastParam() ? "Turn on" : "Turn off";
					itemTitle += " 'repeat' of '";
					itemUUID = (UUID) tuple.second.getDefaultParam(tuple.second.getNoOfDefaultParams() - 2);
					itemTitle += getElement(itemUUID).toString();
					itemTitle += "'";
					
					itemList.add(new RemoveHotkeyMenuItem(tuple.second, itemTitle, getGUIController()));
				}
				
				hotkeyList = PlayList.this.parent.getGUIController().getHotkeys(setChainWithAction);
				for (Tuple<String,Action> tuple : hotkeyList) {
					itemTitle = "Set chaining of '";
					itemUUID = (UUID) tuple.second.getDefaultParam(tuple.second.getNoOfDefaultParams() - 2);
					itemTitle += itemUUID == null ? "None" : getElement(itemUUID).toString();
					itemTitle += "' to '";
					itemUUID = (UUID) tuple.second.getLastParam();
					itemTitle += getElement(itemUUID).toString();
					itemTitle += "'";
					
					itemList.add(new RemoveHotkeyMenuItem(tuple.second, itemTitle, getGUIController()));
				}
				
				if (!itemList.isEmpty()) {
					hotkeyMenu.add(new TitledSeparator("Remove hotkeys", true));
				}
				
				for (JMenuItem item : itemList) {
					hotkeyMenu.add(item);
				}
				
				propagatePopupMenu(menu, e);
			}
		});
		addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent keyevent) {
			}

			public void keyReleased(KeyEvent arg0) {
				propagateHotkey(arg0);
			}

			public void keyTyped(KeyEvent keyevent) {
			}
		});
		setFixedCellHeight(30);
		model.addListDataListener(this);
		setCellRenderer(new PainterLabel());
		setTransferHandler(new PlayListTransferHandler());
		setDragEnabled(true);
		setSelectionBackground(new Color(225, 225, 225));
	}

	protected void removeSelectedEntries() {
		int indices[] = getSelectedIndices();
		for (int i = indices.length - 1; i >= 0; i--)
			getModel().remove(indices[i], true);

		clearSelection();
	}

	protected void play(int index) {
		playIndexAction.execute(index);
	}

	public void setDropLocation(javax.swing.JList.DropLocation loc) {
		dropLocation = loc;
	}

	public void setDropAction(int action) {
		dropAction = action;
	}

	public void setDropSourceIndex(int index) {
		dropSourceIndex = index;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		Color color = new Color(0, 0, 0, 128);
		g2d.setColor(color);
		Stroke s = new BasicStroke(2.0F, 1, 1);
		g2d.setStroke(s);
		drawChaining(g2d);
		if (dropLocation != null) {
			int cellIndex = dropLocation.getIndex();
			if (dropAction == 1 || dropAction == 2) {
				Rectangle bounds = getCellBounds(cellIndex, cellIndex);
				if (bounds == null) {
					cellIndex = getModel().getSize() - 1;
					if (cellIndex == -1)
						return;
					bounds = getCellBounds(cellIndex, cellIndex);
					bounds.translate(0, bounds.height);
				}
				int x1 = bounds.x + 5;
				int y1 = bounds.y + 1;
				int x2 = (bounds.x + bounds.width) - 5;
				int y2 = y1;
				g2d.drawLine(x1, y1, x2, y2);
			} else if (dropAction == 0x40000000 && dropSourceIndex != cellIndex) {
				Rectangle bounds = getCellBounds(cellIndex, cellIndex);
				if (bounds != null) {
					g2d.setColor(activeColor);
					g2d.drawRect(bounds.x + 3, bounds.y + 3,
							bounds.width - 6 - 40, bounds.height - 6);
					drawArrow(g2d, dropSourceIndex, cellIndex, 20);
				}
			}
		}
	}

	private void drawArrow(Graphics2D g2d, int startIndex, int targetIndex,
			int offset) {
		Rectangle bounds = getCellBounds(startIndex, startIndex);
		int x1 = (bounds.x + bounds.width) - 40;
		int y1 = bounds.y + bounds.height / 2;
		int x2 = x1 + offset;
		int y2 = y1;
		g2d.drawLine(x1, y1, x2, y2);
		x1 = x2;
		y1 = y2;
		bounds = getCellBounds(targetIndex, targetIndex);
		if (startIndex < targetIndex)
			y2 = bounds.y + bounds.height / 4;
		else
			y2 = bounds.y + (3 * bounds.height) / 4;
		g2d.drawLine(x1, y1, x2, y2);
		x1 = x2;
		y1 = y2;
		x2 = x1 - offset;
		y2 = y1;
		g2d.drawLine(x1, y1, x2, y2);
		Polygon poly = new Polygon();
		poly.addPoint(x2, y2);
		poly.addPoint(x2 + 6, y2 - 6);
		poly.addPoint(x2 + 6, y2 + 6);
		g2d.fillPolygon(poly);
	}

	private void drawChaining(Graphics2D g2d) {
		PlayListDataModel model = getModel();
		ItemData entry;
		ChainWithTuple tuple;
		List<ChainWithTuple> tuples = new LinkedList<ChainWithTuple>();
		for (int i = 0; i < model.getSize(); i++) {
			entry = model.getElementAt(i);
			if (entry.getChainWith() != null) {
				tuple = new ChainWithTuple(i, model.getIndexFromUuid(entry
						.getChainWith()));
				tuples.add(tuple);
			}
		}

		if (tuples.isEmpty()) {
			return;
		}

		Collections.sort(tuples);

		int[] markers = new int[model.getSize()];

		int i = 0;
		int start;
		int end;
		boolean marked;
		List<ChainWithTuple> leftTuples = new LinkedList<ChainWithTuple>(tuples);
		List<ChainWithTuple> tempTuples;
		List<List<ChainWithTuple>> tupleLists = new LinkedList<List<ChainWithTuple>>();
		List<ChainWithTuple> currentList;
		while (!leftTuples.isEmpty()) {
			tempTuples = new LinkedList<ChainWithTuple>();
			currentList = new LinkedList<ChainWithTuple>();

			for (ChainWithTuple cwt : leftTuples) {
				start = Math.min(cwt.getStartIndex(), cwt.getEndIndex());
				end = Math.max(cwt.getStartIndex(), cwt.getEndIndex());

				marked = false;
				for (int j = start; j < end; j++) {
					if (markers[j] > i) {
						marked = true;
					}
				}

				if (!marked) {
					for (int j = start; j < end; j++) {
						markers[j] = i + 1;
					}
					currentList.add(cwt);
				} else {
					tempTuples.add(cwt);
				}
			}

			leftTuples = tempTuples;
			tupleLists.add(currentList);
			i++;
		}

		int gap = (RIGHTBORDER - 10) / tupleLists.size();
		i = 0;

		for (List<ChainWithTuple> list : tupleLists) {
			for (ChainWithTuple cwt : list) {
				drawArrow(g2d, cwt.getStartIndex(), cwt.getEndIndex(), 10 + i
						* gap);
			}
			i++;
		}
	}

	public PlayListDataModel getModel() {
		if (!(super.getModel() instanceof PlayListDataModel))
			return null;
		else
			return (PlayListDataModel) super.getModel();
	}

	public Rectangle getCellRect() {
		int height = getFixedCellHeight() - 6;
		int width = getWidth() - 40 - 6;
		return new Rectangle(3, 3, width, height);
	}

	public Rectangle getTitleRect() {
		Rectangle result = getCellRect();
		int width = result.width - 32 - 20;
		int height = result.height;
		int x = result.x + 5;
		int y = result.y;
		result.setSize(width, height);
		result.setLocation(x, y);
		return result;
	}

	public Rectangle getRepeatRect() {
		Rectangle result = getTitleRect();
		int width = 16;
		int height = 16;
		int x = result.x + result.width + 5;
		int y = (result.y + result.height / 2) - 8;
		result.setSize(width, height);
		result.setLocation(x, y);
		return result;
	}

	public Rectangle getChainRect() {
		Rectangle result = getTitleRect();
		int width = 16;
		int height = 16;
		int x = result.x + result.width + 10 + 16;
		int y = (result.y + result.height / 2) - 8;
		result.setSize(width, height);
		result.setLocation(x, y);
		return result;
	}

	public int insideChainRectOf(Point pos) {
		int index = locationToIndex(pos);
		if (index < 0)
			return -1;
		Rectangle bounds = getCellBounds(index, index);
		Rectangle chainRect = getChainRect();
		chainRect.setLocation(chainRect.x + bounds.x, chainRect.y + bounds.y);
		if (chainRect.contains(pos))
			return index;
		else
			return -1;
	}

	public int insideRepeatRectOf(Point pos) {
		int index = locationToIndex(pos);
		if (index < 0)
			return -1;
		Rectangle bounds = getCellBounds(index, index);
		Rectangle repeatRect = getRepeatRect();
		repeatRect
				.setLocation(repeatRect.x + bounds.x, repeatRect.y + bounds.y);
		if (repeatRect.contains(pos))
			return index;
		else
			return -1;
	}

	public void contentsChanged(ListDataEvent e) {
		repaint();
	}

	public void intervalAdded(ListDataEvent listdataevent) {
	}

	public void intervalRemoved(ListDataEvent listdataevent) {
	}

	public UUID getUUID() {
		return getModel().getUUID();
	}

	public void setChainWith(UUID source, UUID target) {
		getModel().setChainWith(source, target);
	}

	public void importItems(UUID playListUUID, List<UUID> itemUUIDs,
			int targetIndex, boolean copy) {
		getModel().importItems(playListUUID, itemUUIDs, targetIndex, copy);
		if (getParent() != null)
			getParent().revalidate();
	}

	public ItemData getElementAt(int index) {
		return getModel().getElementAt(index);
	}
	
	public ItemData getElement(UUID uuid) {
		return getElementAt(getIndexFromUuid(uuid));
	}

	public int getIndexFromUuid(UUID itemUUID) {
		return getModel().getIndexFromUuid(itemUUID);
	}

	public int getNumberOfItems() {
		return getModel().getSize();
	}

	public void addAll(int index, List<String> filePathList) {
		getModel().addAll(index, filePathList);
		if (getParent() != null)
			getParent().revalidate();
	}

	public void addAll(List<String> filePathList) {
		getModel().addAll(filePathList);
		if (getParent() != null)
			getParent().revalidate();
	}

	public void setPanel(PlayListPanel playListPanel) {
		panel = playListPanel;
	}

	public Point getLastMouseClickPosition() {
		return lastMouseClickPosition;
	}

	@Override
	public GUIController getGUIController() {
		return panel.getGUIController();
	}

	@Override
	public void propagateHotkey(KeyEvent event) {
		panel.propagateHotkey(event);
	}

	@Override
	public void propagatePopupMenu(JPopupMenu menu, MouseEvent e) {
		panel.propagatePopupMenu(menu, e);
	}

}
