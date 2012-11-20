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

import java.util.UUID;

import smartsound.common.IElement.NameValuePair;

public class ItemData {

	private final UUID uuid;
	private final String name;
	private final boolean repeat;
	private final UUID chainedWith;
	private final int index;
	private final boolean active;

	public ItemData(final UUID uuid, final GUIController controller) {
		this.uuid = uuid;
		NameValuePair[] pairs = controller.get(uuid, "NAME", "REPEAT", "INDEX",
				"CHAINEDWITH", "ACTIVE");
		assert pairs.length == 5;
		assert pairs[0].value instanceof String;
		assert pairs[1].value instanceof Boolean;
		assert pairs[2].value instanceof Integer;
		assert pairs[3].value == null || pairs[3].value instanceof UUID;
		assert pairs[4].value instanceof Boolean;

		name = (String) pairs[0].value;
		repeat = (Boolean) pairs[1].value;
		index = (Integer) pairs[2].value;
		chainedWith = (UUID) pairs[3].value;
		active = (Boolean) pairs[4].value;
	}

	public UUID getUUID() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public boolean isRepeating() {
		return repeat;
	}

	public UUID getChainedWith() {
		return chainedWith;
	}

	public int getIndex() {
		return index;
	}

	public boolean isActive() {
		return active;
	}

	@Override
	public String toString() {
		return getName();
	}

}