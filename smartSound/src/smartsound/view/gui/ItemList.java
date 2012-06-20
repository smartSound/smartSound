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

import java.util.List;
import java.util.UUID;

public class ItemList {

	private UUID sourceUUID;
	private List<UUID> itemUUIDs;
	
	public ItemList(UUID sourceUUID, List<UUID> itemUUIDs) {
		this.sourceUUID = sourceUUID;
		this.itemUUIDs = itemUUIDs;
	}

	public UUID getSourceUUID() {
		return sourceUUID;
	}

	public List<UUID> getItemUUIDs() {
		return itemUUIDs;
	}
	
	
}
