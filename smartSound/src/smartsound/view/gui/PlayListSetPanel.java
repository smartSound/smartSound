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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import smartsound.common.IAddObserver;
import smartsound.common.IChangeObserver;
import smartsound.common.IObserver;
import smartsound.common.IRemoveObserver;
import smartsound.common.Tuple;
import smartsound.view.AbstractViewController.PositionType;
import smartsound.view.LayoutManager;

public class PlayListSetPanel extends JPanel implements IGUILadder, IObserver, IChangeObserver, IRemoveObserver, IAddObserver {

	private final IGUILadder parent;
	private final UUID playListSetUUID;
	private final UUID layoutUUID;
	private final Map<UUID, JPanel> panelMap = new HashMap<UUID, JPanel>();
	private Point dragMousePosition = null;
	private int dragDeltaX = -1;
	private int dragDeltaY = -1;
	private String title;

	public PlayListSetPanel(final IGUILadder parent, final UUID playListSetUUID, final UUID layoutUUID) {
		super(new GridBagLayout());
		setBorder(BorderFactory.createEmptyBorder(2,2,2,2));

		this.playListSetUUID = playListSetUUID;
		this.layoutUUID = layoutUUID;
		this.parent = parent;

		setMinimumSize(new Dimension(400,400));
		getGUIController().addObserver(this, playListSetUUID);
		LayoutManager.add(layoutUUID, "ADDOBSERVER", this);
		LayoutManager.add(layoutUUID, "CHANGEOBSERVER", this);
		getGUIController().add(playListSetUUID, "CHANGEOBSERVER", this);

		refresh();
	}

	private void refresh() {
		/*panelMap.clear();

		//TODO: Extend for embedded PlayListSets
		NameValuePair[] pairs = getGUIController().get(playListSetUUID, "PLAYLISTS");
		assert pairs.length == 1;
		assert pairs[0].value instanceof List<?>;

		List<UUID> childElements = (List<UUID>) pairs[0].value;

		if (childElements != null)
			for (UUID uuid : childElements) {
				pairs = getGUIController().get(uuid, "NAME");
				assert pairs.length == 1;
				assert pairs[0].value instanceof String;

				PlayListPanelBorder border = new PlayListPanelBorder(this,
						new PlayListDataModel(getGUIController(), uuid), (String) pairs[0].value);

				add(border.getPanel(), uuid, false);
			}*/
	}

	public void add(final PlayListPanel panel, final UUID uuid, final boolean updateLayout) {
		panelMap.put(uuid, panel);
		panel.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(final MouseEvent e) {
				if (panel.getUsesDrag())
					return;
				Point pt = e.getLocationOnScreen();
				SwingUtilities.convertPointFromScreen(pt, PlayListSetPanel.this);
				if (dragDeltaX == -1 && dragDeltaY == -1) {
					Point ptPanel = e.getLocationOnScreen();
					SwingUtilities.convertPointFromScreen(ptPanel, panel);
					dragDeltaX = ptPanel.x;
					dragDeltaY = ptPanel.y;
				}

				dragMousePosition = pt;

				setComponentZOrder(panel, 0);
				panel.setLocation(pt.x - dragDeltaX, pt.y - dragDeltaY);

				repaint();
			}

			@Override
			public void mouseMoved(final MouseEvent e) {

			}
		});

		panel.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(final MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(final MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(final MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(final MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(final MouseEvent arg0) {
				if (dragMousePosition == null)
					return;
				Tuple<Integer,Integer> cell = getGridPosition(dragMousePosition);
				PositionType alignment = getAlignment(dragMousePosition);

				dragMousePosition = null;
				dragDeltaX = -1;
				dragDeltaY = -1;

				Map<String, Object> values = new HashMap<String, Object>();
				values.put("X", cell.first);
				values.put("Y", cell.second);
				values.put("ALIGNMENT", alignment.toString());
				LayoutManager.set(layoutUUID, values);

			}

		});
	}

	private Tuple<Integer, Integer> getGridPosition(final Point pos) {
		GridBagLayout layout = (GridBagLayout) getLayout();
		int[] widths = layout.getLayoutDimensions()[0];
		int[] heights = layout.getLayoutDimensions()[1];

		int aggregate = 0;
		int widthIndex = widths.length;
		for (int i = 0; i < widths.length; i++) {
			aggregate += widths[i];
			if (aggregate > dragMousePosition.x) {
				widthIndex = i;
				break;
			}
		}

		int heightIndex = heights.length;
		aggregate = 0;
		for (int i = 0; i < heights.length; i++) {
			aggregate += heights[i];
			if (aggregate > dragMousePosition.y) {
				heightIndex = i;
				break;
			}
		}

		return new Tuple<Integer, Integer>(widthIndex, heightIndex);
	}

	private Tuple<Integer, Integer> getCellBegin(final int cellX, final int cellY) {
		GridBagLayout layout = (GridBagLayout) getLayout();
		int[] widths = layout.getLayoutDimensions()[0];
		int[] heights = layout.getLayoutDimensions()[1];

		int dx = 0;
		int dy = 0;
		for (int i = 0; i < cellX; i++) {
			dx += widths[i];
		}
		for (int i = 0; i < cellY; i++) {
			dy += heights[i];
		}

		return new Tuple<Integer,Integer>(dx,dy);
	}

	private PositionType getAlignment(final Point pos) {
		Tuple<Integer, Integer> cell = getGridPosition(pos);
		Tuple<Integer,Integer> cellBorders = getCellBegin(cell.first, cell.second);

		if (pos.x - cellBorders.first < pos.y - cellBorders.second)
			return PositionType.LEFT;

		return PositionType.ABOVE;
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		if (dragMousePosition == null)
			return;

		GridBagLayout layout = (GridBagLayout) getLayout();
		int[] widths = layout.getLayoutDimensions()[0];
		int[] heights = layout.getLayoutDimensions()[1];

		Tuple<Integer,Integer> cell = getGridPosition(dragMousePosition);
		int widthIndex = cell.first;
		int heightIndex = cell.second;

		Tuple<Integer,Integer> cellBorder = getCellBegin(cell.first, cell.second);
		int dx = cellBorder.first;
		int dy = cellBorder.second;

		if (dragMousePosition.x - dx < dragMousePosition.y - dy) {
			//Line is left of cell
			g2d.drawLine(dx + 1, dy, dx + 1, dy + heights[Math.min(heightIndex, heights.length - 1)]);
		} else { //Line is above the cell
			g2d.drawLine(dx, Math.max(0,dy - 1), dx + widths[Math.min(widthIndex, widths.length - 1)], Math.max(0,dy - 1));
		}

	}

	private void put(final JPanel panel, final int x, final int y) {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.5f;
		c.weighty = 0.5f;

		c.gridx = x;
		c.gridy = y;

		add(panel, c);
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

	public UUID getPlayListSetUUID() {
		return playListSetUUID;
	}

	@Override
	public void updateMinimumSize() {
		GridBagLayout layout = (GridBagLayout) getLayout();
		GridBagConstraints c;
		List<Integer> widths = new LinkedList<Integer>();
		List<Integer> heights = new LinkedList<Integer>();
		Component comp;
		for (int i = 0; i < getComponentCount(); i++) {
			comp = getComponent(i);
			c = layout.getConstraints(comp);

			while (widths.size() <= c.gridy)
				widths.add(0);

			while (heights.size() <= c.gridx)
				heights.add(0);

			widths.set(c.gridy, widths.get(c.gridy) + comp.getMinimumSize().width);
			heights.set(c.gridx, heights.get(c.gridx) + comp.getMinimumSize().height);
		}

		int maxWidth = 0;
		for (int w : widths) {
			maxWidth = Math.max(maxWidth, w);
		}

		int maxHeight = 0;
		for (int h : heights) {
			maxHeight = Math.max(maxHeight, h);
		}

		setMinimumSize(new Dimension(maxWidth, maxHeight));
		parent.updateMinimumSize();
	}

	@Override
	public void update(final UUID uuid) {
		refresh();
	}

	public void moveTo(final int x, final int y, final PositionType alignment) {
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("X", x);
		values.put("Y", y);
		values.put("ALIGNMENT", alignment.toString());

		LayoutManager.set(layoutUUID, values);
	}

	@Override
	public void elementRemoved(final UUID elementUUID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void elementChanged(final UUID elementUUID, final Map<String, Object> values) {
		if (elementUUID.equals(layoutUUID)) {
			layoutChanged(values);
		}

		if (elementUUID.equals(playListSetUUID)) {
			propertiesChanged(values);
		}


	}

	private void layoutChanged(final Map<String, Object> values) {
		int x = (Integer) values.get("X");
		int y = (Integer) values.get("Y");

		if (parent instanceof TabbedPane) {
			((TabbedPane) parent).movedTo(this, x);
		} else if (parent instanceof PlayListSetPanel) {
			((PlayListSetPanel) parent).movedTo(this, x, y);
		}
	}

	public void movedTo(final JPanel panel, final int x, final int y) {
		remove(panel);
		put(panel, x, y);
	}

	private void propertiesChanged(final Map<String, Object> values) {
		for (Entry<String, Object> entry : values.entrySet()) {
			switch (entry.getKey()) {
			case "NAME":
				setTitle((String) entry.getValue());
				break;
			}
		}
	}

	private void setTitle(final String newTitle) {
		title = newTitle;

		if (parent instanceof TabbedPane) {
			((TabbedPane) parent).updateTitle(this);
		} else if (parent instanceof PlayListSetPanel) {
			((PlayListSetPanel) parent).updateTitle(this);
		}
	}

	private void updateTitle(final PlayListSetPanel playListSetPanel) {
		// TODO Auto-generated method stub

	}

	public String getTitle() {
		return title;
	}

	@Override
	public void elementAdded(final UUID parentUUID, final UUID newElementUUID) {
		//TODO: Extend for nested sets
		PlayListPanelBorder border = new PlayListPanelBorder(this, newElementUUID);
	}
}
