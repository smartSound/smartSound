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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import smartsound.common.IObserver;
import smartsound.common.PropertyMap;
import smartsound.player.LoadingException;
import smartsound.view.AbstractViewController.PositionType;

public class Layout implements IObserver {

	private final UUID uuid;
	private final UUID parent;
	private final List<List<UUID>> columns = new ArrayList<List<UUID>>();
	private final Type type;
	private int count = 0;
	private AbstractViewController controller;
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


	public Layout(final AbstractViewController controller, final UUID parent, final Type type) {
		this.controller = controller;
		this.type = type;
		this.parent = parent;

		uuid = UUID.randomUUID();
	}

	public Layout(final Layout other) {
		parent = other.parent;
		type = other.type;
		count = other.count;
		for (List<UUID> list : other.columns) {
			columns.add(new ArrayList<UUID>(list));
		}

		uuid = UUID.fromString(other.uuid.toString());
	}

	public Layout(final AbstractViewController controller, final PropertyMap pMap) throws LoadingException {
		if (!pMap.get("type").equals(getClass().getCanonicalName())) {
			throw new LoadingException();
		}
		this.controller = controller;

		uuid = pMap.getMapUUID();
		parent = pMap.get("parent").equals("NULL") ? null : UUID.fromString(pMap.get("parent"));
		type = Type.valueOf(pMap.get("layout_type"));

		String[] split;
		int x;
		int y;
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
				createEntry(UUID.fromString(pMap.get(key)), x, y);
				count++;
			}
		}
		check();
	}

	public void addLayoutObserver(final ILayoutObserver observer) {
		layoutObservers.add(observer);
	}

	public void removeLayoutObserver(final ILayoutObserver observer) {
		layoutObservers.remove(observer);
	}

	private void updateObservers() {
		Layout layout = copy();
		for (ILayoutObserver observer : layoutObservers) {
			observer.updateLayout(layout);
		}
	}

	public int getCount() {
		return count;
	}

	public int getRows() {
		int result = Integer.MIN_VALUE;
		for (List<UUID> list : columns) {
			result = Math.max(result, list.size());
		}
		return result;
	}

	public int getColumns() {
		return columns.size();
	}

	void addComponent(final UUID uuid, final int x, final int y) {
		int effectiveX = x;
		int effectiveY = type == Type.GRID ? y : 0;

		if ((effectiveX != AUTOALIGN && effectiveX < 0) || (effectiveY != AUTOALIGN && effectiveY < 0)) {
			throw new IndexOutOfBoundsException();
		}
		count++;
		int dx;
		int dy;
		UUID id;

		if (effectiveX == AUTOALIGN && effectiveY == AUTOALIGN) {
			int run = 0;
			boolean found = false;
			while (true) {
				for (int i = 0; i < 2*run + 1; i++) {
					dx = run - Math.max(0,  i - run);
					dy = Math.min(i,run);

					id = getByCoordinates(dx, dy);
					if (id == null) {
						createEntry(uuid, dx, dy);
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
					createEntry(uuid, dx, dy);
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
					createEntry(uuid, dx, dy);
					break;
				}
				dy++;
			}
			return;
		}

		if (getByCoordinates(effectiveX,effectiveY) != null) {
			columns.get(effectiveX).add(effectiveY, uuid);
			return;
		}

		dx = effectiveX;
		dy = effectiveY;

		List<UUID> neighbours = getNeighbours(dx,dy);
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
		createEntry(uuid, dx, dy);
		check();
		updateObservers();
	}

	private void createEntry(final UUID uuid, final int x, final int y) {
		if (uuid == null) {
			List<UUID> neighbours = getNeighbours(x, y);
			if (neighbours.get(EAST) == null && neighbours.get(SOUTH) == null && getByCoordinates(x,y) != null) {
				columns.get(x).remove(y);
			}
		}

		while (columns.size() < x + 1) {
			columns.add(new ArrayList<UUID>());
		}

		List<UUID> column = columns.get(x);

		while (column.size() < y + 1)
			column.add(null);

		column.set(y, uuid);
	}

	public UUID getByCoordinates(final int x, final int y) {
		if (x < 0 || columns.size() <= x)
			return null;

		if (y < 0 || columns.get(x).size() <= y)
			return null;

		return columns.get(x).get(y);
	}

	@Override
	public String toString() {
		return "Layout [parent=" + parent + ", columns=" + Arrays.deepToString(columns.toArray()) + ", type="
				+ type + ", count=" + count + "]";
	}

	private List<UUID> getNeighbours(final int x, final int y) {
		List<UUID> result = new ArrayList<UUID>(8);

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

	private Point getGridPosition(final UUID uuid) {
		for (int x = 0; x < columns.size(); x++) {
			for (int y = 0; y < columns.get(x).size(); y++) {
				if (uuid.equals(columns.get(x).get(y))) {
					return new Point(x,y);
				}
			}
		}
		return null;
	}

	void shiftElement(final UUID uuid, final int x, final int y,
			final PositionType alignment) {
		Point currentPos = getGridPosition(uuid);
		if (currentPos == null)
			return;

		columns.get(currentPos.x).set(currentPos.y, null);

		boolean collapseHorizontal = !(y == currentPos.y && alignment == PositionType.ABOVE);

		if (getByCoordinates(x,y) == null) {
			createEntry(uuid, x, y);
		} else {
			if (alignment == PositionType.ABOVE) {
				columns.get(x).add(y, uuid);
			} else {
				insertHorizontal(x, y, uuid);
			}
		}

		if (!collapseHorizontal) {
			for (List<UUID> row : columns)
				row.remove(null);
		}
		check();
		updateObservers();
	}

	private void insertHorizontal(final int x, final int y, final UUID uuid) {
		UUID currentField = getByCoordinates(x,y);
		if (currentField != null) {
			insertHorizontal(x + 1, y, currentField);
		}
		createEntry(uuid, x, y);
		check();
	}

	private void check() {
		int count;
		for (List<UUID> list : columns) {
			count = 0;
			for (UUID uuid : list) {
				if (uuid == null)
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
		result.put("parent", parent != null ? parent.toString() : "NULL");
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

	public UUID getParentUUID() {
		return parent;
	}

	public Set<UUID> getUUIDSet() {
		Set<UUID> result = new HashSet<UUID>();
		for (List<UUID> column : columns) {
			for (UUID uuid : column) {
				if (uuid != null)
					result.add(uuid);
			}
		}
		return result;
	}

	public void removeUUID(final UUID uuid) {
		for (List<UUID> column : columns) {
			if (column.remove(uuid))
				count--;
		}
		check();
		updateObservers();
	}

	@Override
	public void update(final UUID uuid) {
		assert (uuid == null && parent == null) || uuid.equals(parent);
		//TODO: extend for PlayListSets in sub layouts
		List<UUID> uuidList;
		if (uuid == null) { // is root layout
			uuidList = controller.getPlayListSetUUIDs(null);
		} else {
			uuidList = controller.getPlayListUUIDs(uuid);
		}

		Set<UUID> obsoleteUUIDs = getUUIDSet();
		obsoleteUUIDs.removeAll(uuidList);

		for (UUID obsolete : obsoleteUUIDs) {
			removeUUID(obsolete);
		}

		Set<UUID> newUUIDs = new HashSet<UUID>();
		for (UUID u : uuidList) {
			newUUIDs.add(u);
		}

		newUUIDs.removeAll(getUUIDSet());
		for (UUID u : newUUIDs) {
			addComponent(u, Layout.AUTOALIGN, Layout.AUTOALIGN);
		}
	}

	public UUID getUUID() {
		return uuid;
	}
}
