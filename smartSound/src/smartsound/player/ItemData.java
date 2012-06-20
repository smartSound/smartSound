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

package smartsound.player;

import java.util.UUID;

import smartsound.controller.AbstractController;


/**
 * This class is a wrapper object around all data belonging to a single
 * <c>PlayList</c> entry. It does not cache any information but simply
 * delegates the calls.
 * @author André Becker
 *
 */
public class ItemData {

	private AbstractController controller;
	private UUID playListUUID;
	private UUID itemUUID;

	/**
	 * Creates a new <c>ItemData</c> object.
	 * @param controller The <c>AbstractController</c> object to which the
	 * 	calls are delegated.
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 * @param itemUUID The <c>UUID</c> identifying the <c>PlayList</c> entry.
	 */
	public ItemData(AbstractController controller, UUID playListUUID,
			UUID itemUUID) {
		this.controller = controller;
		this.playListUUID = playListUUID;
		this.itemUUID = itemUUID;
	}
	
	/**
	 * @return <c>true</c> if the entry is playing or paused.
	 */
	public boolean isActive() {
		return controller.itemIsActive(playListUUID, itemUUID);
	}
	
	@Override
	public String toString() {
		return controller.getItemName(playListUUID, itemUUID);
	}

	/**
	 * @return The <c>UUID</c> of the entry this entry is chained with or
	 * 	<c>null</c> if no such chaining exists.
	 */
	public UUID getChainWith() {
		return controller.getItemChainWith(playListUUID, itemUUID);
	}

	/**
	 * Sets the chaining to another <c>PlayList</c> entry.
	 * @param chainWith The <c>UUID</c> of the <c>PlayList</c> entry to be
	 * 	chained with.
	 */
	public void setChainWith(UUID chainWith) {
		controller.setItemChainWith(playListUUID, itemUUID, chainWith);
	}

	/**
	 * @return The <c>UUID</c> identifying the <c>PlayList</c> entry.
	 */
	public UUID getUUID() {
		return itemUUID;
	}

	/**
	 * @return <c>true</c> if the single <c>PlayList</c> entry is repeated. 
	 */
	public boolean isRepeating() {
		return controller.itemIsRepeating(playListUUID, itemUUID);
	}

	/**
	 * Toggles repeating, i.e. sets it to !isRepeating().
	 */
	public void toggleRepeating() {
		controller.setItemIsRepeating(playListUUID, itemUUID, !isRepeating());
	}

}
