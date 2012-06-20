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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class ItemDataTransferable implements Transferable {

	private UUID playListUUID;
	private List<UUID> itemUUIDs;
	public static final DataFlavor itemDataFlavor = getDataFlavor();
	private static DataFlavor[] dataFlavors = {itemDataFlavor, DataFlavor.stringFlavor};
	
	private static DataFlavor getDataFlavor() {
		DataFlavor result = null;
		try {
			result = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType +
			        ";class=smartsound.player.ItemData");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public ItemDataTransferable(UUID playListUUID, List<UUID> itemUUIDs) {
		this.playListUUID = playListUUID;
		this.itemUUIDs = new LinkedList<UUID>(itemUUIDs);
	}
	
	@Override
	public Object getTransferData(DataFlavor dataFlavor)
			throws UnsupportedFlavorException, IOException {
		if (dataFlavor.equals(itemDataFlavor)) {
			return new ItemList(playListUUID, itemUUIDs);
		} else if (dataFlavor.equals(DataFlavor.stringFlavor)) {
			return "String";
		}
		throw new UnsupportedOperationException("Unsupported DataFlavor '" + dataFlavor + "'");
	}
	
	public UUID getPlayListUUID() {
		return this.playListUUID;
	}
 
	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return dataFlavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor dataFlavor) {
		return (dataFlavor.equals(dataFlavors[0])
				|| dataFlavor.equals(dataFlavors[1]));
	}

}
