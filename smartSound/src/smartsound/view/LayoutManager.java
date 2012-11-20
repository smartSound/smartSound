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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import smartsound.view.Layout.Type;

public class LayoutManager {

	private static AbstractViewController viewController = null;
	private static LayoutManager inst;
	private final Map<UUID, Layout> layoutMap = new HashMap<UUID, Layout>();

	private LayoutManager() {
		Layout root = new Layout(viewController, null, null, Type.ROW);
		root.setUUID(null);
		add_(root);
	}

	public static void setViewController(final AbstractViewController avc) {
		viewController = avc;
	}

	private static LayoutManager getInst() {
		if (inst == null)
			inst = new LayoutManager();
		return inst;
	}

	public static void add(final Layout layout) {
		getInst().add_(layout);
	}

	public static void set(final UUID uuid, final Map<String, Object> values) {
		getInst().set_(uuid, values);
	}

	private void set_(final UUID uuid, final Map<String, Object> values) {
		if (layoutMap.containsKey(uuid))
			layoutMap.get(uuid).set(values);
	}

	private void add_(final Layout layout) {
		layoutMap.put(layout.getUUID(), layout);
	}

	public static void add(final UUID uuid, final String elementType, final Object... params) {
		getInst().add_(uuid, elementType, params);
	}

	private void add_(final UUID uuid, final String elementType, final Object... params) {
		if (layoutMap.containsKey(uuid))
			layoutMap.get(uuid).add(elementType, params);
	}

	public static void remove(final UUID uuid) {
		getInst().remove_(uuid);
	}

	private void remove_(final UUID uuid) {
		layoutMap.remove(uuid);
	}

	public static Map<String, Object> get(final UUID layoutUUID, final String... propertyNames) {
		return getInst().get_(layoutUUID, propertyNames);

	}

	private Map<String, Object> get_(final UUID layoutUUID, final String... propertyNames) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (layoutMap.containsKey(layoutUUID)) {
			for (String s : propertyNames) {
				result.put(s, layoutMap.get(layoutUUID).get(s));
			}
		}

		return result;
	}

}
