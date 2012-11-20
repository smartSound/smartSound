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

package smartsound.view;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import smartsound.common.IAddObserver;
import smartsound.common.IChangeObserver;
import smartsound.common.IRemoveObserver;
import smartsound.common.PropertyMap;
import smartsound.player.LoadingException;
import smartsound.view.AbstractViewController.PositionType;

public class Layout implements IAddObserver, IRemoveObserver {

	private UUID uuid;
	private final UUID elementUUID;
	private final List<List<Layout>> columns = new ArrayList<List<Layout>>();
	private final Type type;
	private int count = 0;
	private AbstractViewController controller;
	private final Layout parent;
	private int x;
	private int y;

	private final List<IChangeObserver> changeObservers = new LinkedList<IChangeObserver>();
	private final List<IAddObserver> addObservers = new LinkedList<IAddObserver>();

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setCoordinates(final int x, final int y) {
		boolean changed = x != this.x || y != this.y;

		if (changed) {
			this.x = x;
			this.y = y;
			notifyChangeObservers();
		}
	}


	private void notifyChangeObservers() {
		Map<String, Object> valuesMap = new HashMap<String,Object>();
		valuesMap.put("X", x);
		valuesMap.put("Y", y);

		for (IChangeObserver obs : changeObservers) {
			obs.elementChanged(uuid, new HashMap<String,Object>(valuesMap));
		}
	}


	protected Set<ILayoutObserver> layoutObservers = new HashSet<ILayoutObserver>();

	private static final int NORTH = 0;
	private static final int NORTHWEST = 1;
	private static final int WEST = 2;
	private static final int SOUTHWEST = 3;
	private static final int SOUTH = 4;
	private static final int SOUTHEAST = 5;
	private static final int EAST = 6;
	private static final int NORTHEAST = 7;

	public static final int AUTOALIGN = -1;


	public Layout(final AbstractViewController controller, final Layout parent, final UUID elementUUID, final Type type) {
		this.controller = controller;
		this.type = type;
		this.elementUUID = elementUUID;
		this.parent = parent;

		uuid = UUID.randomUUID();

		controller.add(elementUUID, "ADDOBSERVER", this);
		controller.add(elementUUID, "REMOVEOBSERVER", this);
	}

	public void set(final Map<String, Object> params) {
		int x = this.x;
		int y = this.y;
		PositionType type = PositionType.LEFT;

		if (params.containsKey("X")) {
			x = (Integer) params.get("X");
		}
		if (params.containsKey("Y")) {
			y = (Integer) params.get("Y");
		}
		if (params.containsKey("ALIGNMENT") && params.get("ALIGNMENT").equals("ABOVE")) {
			type = PositionType.ABOVE;
		}

		parent.moveTo(this, x, y, type);
	}

	private void moveTo(final Layout layout, final int x, final int y, final PositionType type) {
		shiftElement(layout, x, y, type);
	}

	public Layout(final Layout other) {
		elementUUID = other.elementUUID;
		type = other.type;
		count = other.count;
		List<Layout> currentColumn;
		for (List<Layout> list : other.columns) {
			columns.add(currentColumn = new ArrayList<Layout>());
			for (Layout layout : list) {
				currentColumn.add(new Layout(layout));
			}
		}

		uuid = UUID.fromString(other.uuid.toString());
		parent = other.parent;
	}

	public Layout(final AbstractViewController controller, final Layout parent, final PropertyMap pMap) throws LoadingException {
		if (!pMap.get("type").equals(getClass().getCanonicalName())) {
			throw new LoadingException();
		}
		this.controller = controller;
		this.parent = parent;

		uuid = pMap.getMapUUID();
		elementUUID = pMap.get("element").equals("NULL") ? null : UUID.fromString(pMap.get("element"));
		type = Type.valueOf(pMap.get("layout_type"));

		String[] split;
		int x;
		int y;
		Layout subLayout;
		for (String key : pMap.getKeys()) {
			switch (key) {
			case "parent":
			case "layout_type":
			case "type":
				break;
			default:
				if (!key.contains(","))
					break;
				split = key.split(",");
				if (split.length != 2)
					break;
				x = Integer.valueOf(split[0]);
				y = Integer.valueOf(split[1]);
				//createEntry(new Layout(UUID.fromString(pMap.get(key)), x, y);
				count++;
			}
		}
		check();
	}

	private void addLayoutObserver(final IChangeObserver obs) {
		changeObservers.add(obs);
		fullUpdate(obs);
	}

	private void fullUpdate(final IChangeObserver obs) {
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("X", x);
		values.put("Y", y);
		obs.elementChanged(uuid, values);
	}

	public void removeLayoutObserver(final IChangeObserver observer) {
		changeObservers.remove(observer);
	}

	public int getCount() {
		return count;
	}

	public int getRows() {
		int result = Integer.MIN_VALUE;
		for (List<Layout> list : columns) {
			result = Math.max(result, list.size());
		}
		return result;
	}

	public int getColumns() {
		return columns.size();
	}

	void addComponent(final Layout layout, final int x, final int y) {
		int effectiveX = x;
		int effectiveY = type == Type.GRID ? y : 0;

		if ((effectiveX != AUTOALIGN && effectiveX < 0) || (effectiveY != AUTOALIGN && effectiveY < 0)) {
			throw new IndexOutOfBoundsException();
		}
		count++;
		int dx;
		int dy;
		Layout l;

		if (effectiveX == AUTOALIGN && effectiveY == AUTOALIGN) {
			int run = 0;
			boolean found = false;
			while (true) {
				for (int i = 0; i < 2*run + 1; i++) {
					dx = run - Math.max(0,  i - run);
					dy = Math.min(i,run);

					l = getByCoordinates(dx, dy);
					if (l == null) {
						createEntry(layout, dx, dy);
						found = true;
						break;
					}
				}
				if (found)
					break;

				run++;
			}
			return;
		}

		if (effectiveX == AUTOALIGN) {
			dx = 0;
			dy = effectiveY;

			while (true) {
				if (getByCoordinates(dx,dy) == null) {
					createEntry(layout, dx, dy);
					break;
				}
				dx++;
			}
			return;
		}

		if (effectiveY == AUTOALIGN) {
			dx = effectiveX;
			dy = 0;

			while (true) {
				if (getByCoordinates(dx,dy) == null) {
					createEntry(layout, dx, dy);
					break;
				}
				dy++;
			}
			return;
		}

		if (getByCoordinates(effectiveX,effectiveY) != null) {
			columns.get(effectiveX).add(effectiveY, layout);
			return;
		}

		dx = effectiveX;
		dy = effectiveY;

		List<Layout> neighbours = getNeighbours(dx,dy);
		int i = 0;
		while ((!(dx == 0 && dy == 0)) &&
				(neighbours.get(NORTH) == null
				&& neighbours.get(WEST) == null
				&& neighbours.get(SOUTH) == null
				&& neighbours.get(EAST) == null)) {
			if (dx > 0 && i % 2 == 1) {
				dx -= 1;
			} else {
				dy -= 1;
			}
			i++;
			neighbours = getNeighbours(dx, dy);
		}
		createEntry(layout, dx, dy);
		check();
		update();
	}

	private void createEntry(final Layout layout, final int x, final int y) {
		if (uuid == null) {
			List<Layout> neighbours = getNeighbours(x, y);
			if (neighbours.get(EAST) == null && neighbours.get(SOUTH) == null && getByCoordinates(x,y) != null) {
				columns.get(x).remove(y);
			}
		}

		while (columns.size() < x + 1) {
			columns.add(new ArrayList<Layout>());
		}

		List<Layout> column = columns.get(x);

		while (column.size() < y + 1)
			column.add(null);

		column.set(y, layout);
	}

	public Layout getByCoordinates(final int x, final int y) {
		if (x < 0 || columns.size() <= x)
			return null;

		if (y < 0 || columns.get(x).size() <= y)
			return null;

		return columns.get(x).get(y);
	}

	@Override
	public String toString() {
		return "Layout [element=" + elementUUID + ", columns=" + Arrays.deepToString(columns.toArray()) + ", type="
				+ type + ", count=" + count + "]";
	}

	private List<Layout> getNeighbours(final int x, final int y) {
		List<Layout> result = new ArrayList<Layout>(8);

		result.add(getByCoordinates(x,y - 1));
		result.add(getByCoordinates(x - 1,y - 1));
		result.add(getByCoordinates(x - 1,y));
		result.add(getByCoordinates(x - 1,y + 1));
		result.add(getByCoordinates(x,y + 1));
		result.add(getByCoordinates(x + 1,y + 1));
		result.add(getByCoordinates(x + 1,y));
		result.add(getByCoordinates(x + 1,y - 1));

		return result;
	}

	public enum Type {
		GRID, ROW
	}

	public Layout copy() {
		return new Layout(this);
	}

	private Point getGridPosition(final Layout layout) {
		for (int x = 0; x < columns.size(); x++) {
			for (int y = 0; y < columns.get(x).size(); y++) {
				if (layout.equals(columns.get(x).get(y))) {
					return new Point(x,y);
				}
			}
		}
		return null;
	}

	void shiftElement(final Layout layout, final int x, final int y,
			final PositionType alignment) {
		Point currentPos = getGridPosition(layout);
		if (currentPos == null)
			return;

		columns.get(currentPos.x).set(currentPos.y, null);

		boolean collapseHorizontal = !(y == currentPos.y && alignment == PositionType.ABOVE);

		if (getByCoordinates(x,y) == null) {
			createEntry(layout, x, y);
		} else {
			if (alignment == PositionType.ABOVE) {
				columns.get(x).add(y, layout);
			} else {
				insertHorizontal(x, y, layout);
			}
		}

		if (!collapseHorizontal) {
			for (List<Layout> row : columns)
				row.remove(null);
		}
		check();
		update();
	}

	private void insertHorizontal(final int x, final int y, final Layout layout) {
		Layout currentField = getByCoordinates(x,y);
		if (currentField != null) {
			insertHorizontal(x + 1, y, currentField);
		}
		createEntry(layout, x, y);
		check();
	}

	private void check() {
		int count;
		for (List<Layout> list : columns) {
			count = 0;
			for (Layout l : list) {
				if (l == null)
					count++;
			}
			if (count == list.size()) {
				columns.remove(list);
				check();
				break;
			}
		}
	}

	public PropertyMap getPropertyMap() {
		PropertyMap result = new PropertyMap(uuid);
		result.put("element", elementUUID != null ? elementUUID.toString() : "NULL");
		result.put("type", getClass().getCanonicalName());
		for (int x = 0; x < columns.size(); x++) {
			for (int y = 0; y < columns.get(x).size(); y++) {
				if (columns.get(x).get(y) != null)
					result.put(x + "," + y, columns.get(x).get(y).toString());
			}
		}
		result.put("layout_type", type.toString());
		return result;
	}

	public UUID getElementUUID() {
		return elementUUID;
	}

	@SuppressWarnings("unchecked") //is indeed checked
	public void update() {
		int i = 0;
		int j = 0;
		for (List<Layout> list : columns) {
			for (Layout l : list) {
				l.setCoordinates(i, j++);
			}
			i++;
		}
	}

	public UUID getUUID() {
		return uuid;
	}

	public void setUUID(final UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public void elementAdded(final UUID parentUUID, final UUID elementUUID) {
		System.out.println("Element '" + elementUUID + "' added to Layout with parent '" + parentUUID + "'.");
		assert (parentUUID == null && this.elementUUID == null) || parentUUID.equals(this.elementUUID);
		Layout newLayout;
		LayoutManager.add(newLayout = new Layout(controller, this, elementUUID, Type.GRID));
		addComponent(newLayout, Layout.AUTOALIGN, Layout.AUTOALIGN);
		notifyAddObservers(newLayout.getUUID());
	}

	private void notifyAddObservers(final UUID newElementUUID) {
		for (IAddObserver obs : addObservers) {
			obs.elementAdded(uuid, newElementUUID);
		}
	}

	@Override
	public void elementRemoved(final UUID elementUUID) {
		assert elementUUID.equals(this.elementUUID);
		parent.remove(this);
		LayoutManager.remove(getUUID());
	}

	private void remove(final Layout child) {
		for (List<Layout> list : columns) {
			for (Layout layout : list) {
				if (layout == child) {
					list.set(list.indexOf(child), null);
					break;
				}
			}
		}
		check();
		update();
	}

	public void add(final String elementType, final Object... params) {
		switch (elementType) {
		case "ADDOBSERVER":
			assert params.length == 1;
			assert params[0] instanceof IAddObserver;
			addLayoutObserver((IAddObserver) params[0]);
			break;
		case "CHANGEOBSERVER":
			assert params.length == 1;
			assert params[0] instanceof IChangeObserver;
			addLayoutObserver((IChangeObserver) params[0]);
			break;
		}
	}

	private void addLayoutObserver(final IAddObserver iAddObserver) {
		addObservers.add(iAddObserver);
		fullUpdate(iAddObserver);
	}

	private void fullUpdate(final IAddObserver iAddObserver) {
		// TODO Auto-generated method stub

	}

	private void removeLayoutObserver(final IAddObserver iAddObserver) {
		addObservers.remove(iAddObserver);
	}

	public Object get(final String propertyName) {
		switch (propertyName) {
		case "ELEMENTUUID":
			return elementUUID;
		}
		return null;
	}
}
