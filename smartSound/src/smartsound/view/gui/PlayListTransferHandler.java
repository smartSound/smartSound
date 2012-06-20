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
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

import smartsound.player.ItemData;

public class PlayListTransferHandler extends TransferHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6802446968140030296L;

	@Override
	public int getSourceActions(JComponent comp) {
		assert comp instanceof PlayList;
		PlayList list = (PlayList) comp;
		if (list.insideChainRectOf(list.getMousePosition()) != -1) {
			return TransferHandler.LINK;
		} else {
			return TransferHandler.COPY_OR_MOVE;
		}
	}

	@Override
	public Transferable createTransferable(JComponent comp) {
		assert comp instanceof PlayList;
		PlayList list = (PlayList) comp;
		assert list.getModel() instanceof PlayListDataModel;

		int action = getSourceActions(comp);
		
		List<UUID> itemUUIDs = new LinkedList<UUID>();
		
		if (action == TransferHandler.COPY_OR_MOVE) {
			for (ItemData itemData : list.getSelectedValuesList()) {
				itemUUIDs.add(itemData.getUUID());
			}
			return new ItemDataTransferable(list.getUUID(), itemUUIDs);
		} else if (action == TransferHandler.LINK) {
			PlayListDataModel model = (PlayListDataModel) list.getModel();
			int index = list.locationToIndex(list.getMousePosition());
			itemUUIDs.add(model.getElementAt(index).getUUID());
			Transferable result = new ItemDataTransferable(list.getUUID(), itemUUIDs);
			list.setDropSourceIndex(index);
			return result;
		}
		
		return null;
	}

	@Override
	public void exportDone(JComponent c, Transferable t, int action) {
		c.repaint();
	}

	@Override
	public boolean canImport(TransferHandler.TransferSupport t) {
		t.setShowDropLocation(false);
		if (t
				.isDataFlavorSupported(ItemDataTransferable.itemDataFlavor)
				|| t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			if (t.getComponent() instanceof PlayList) {
				PlayList list = (PlayList) t.getComponent();
				list.setDropLocation((JList.DropLocation) t.getDropLocation());
				list.setDropAction(t.getDropAction());
				list.repaint(20);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean importData(TransferHandler.TransferSupport t) {
		JList.DropLocation loc = (JList.DropLocation) t.getDropLocation();
		
		if (!canImport(t)) { return false; }
		
		try {
			PlayList playList =  ((PlayList) t.getComponent());
			List<UUID> itemUUIDs = null;
			
			if (t.isDataFlavorSupported(ItemDataTransferable.itemDataFlavor)) {
				ItemList itemList = (ItemList) t.getTransferable().getTransferData(ItemDataTransferable.itemDataFlavor);
				itemUUIDs = itemList.getItemUUIDs();
				if (t.getDropAction() == TransferHandler.LINK) {
					UUID itemUUID = itemUUIDs.get(0);
					if (loc.getIndex() == -1 || loc.getIndex() == playList.getIndexFromUuid(itemUUID)) {
						playList.setChainWith(itemUUID,null);
					} else {
						playList.setChainWith(itemUUID, playList.getElementAt(loc.getIndex()).getUUID());
					}
				} else {
					playList.importItems(itemList.getSourceUUID(), itemUUIDs, loc.getIndex(), false);
				}
			} else if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				List<File> fileList = (List<File>) t.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
				List<String> filePathList = new LinkedList<String>();

				for (File file : fileList) {
					filePathList.add(file.getAbsolutePath());
				}
				
				if (loc.getIndex() < playList.getNumberOfItems() && loc.getIndex() >= 0) {
					playList.addAll(loc.getIndex(), filePathList);
				} else {
					playList.addAll(filePathList);
				}
				
			}

			if (t.getComponent() instanceof PlayList) {
				playList.setDropLocation(null);
			}
			
			playList.repaint();
		} catch (UnsupportedFlavorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
}
