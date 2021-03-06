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
import smartsound.player.events.ITimeEventListener;
import smartsound.player.events.TimeEventHandler;

public class PlayListTransferHandler extends TransferHandler implements ITimeEventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6802446968140030296L;

	private PlayList lastPlayList = null;
	private long currentTime = -1;
	
	@Override
	public int getSourceActions(final JComponent comp) {
		assert comp instanceof PlayList;
		PlayList list = (PlayList) comp;
		if (list.getMousePosition() == null) {
			return TransferHandler.NONE;
		}
		
		if (list.insideChainRectOf(list.getMousePosition()) != -1) {
			return TransferHandler.LINK;
		} else {
			return TransferHandler.COPY_OR_MOVE;
		}
	}

	@Override
	public Transferable createTransferable(final JComponent comp) {
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
			PlayListDataModel model = list.getModel();
			int index = list.locationToIndex(list.getMousePosition());
			itemUUIDs.add(model.getElementAt(index).getUUID());
			Transferable result = new ItemDataTransferable(list.getUUID(), itemUUIDs);
			list.setDropSourceIndex(index);
			return result;
		}

		return null;
	}

	@Override
	public void exportDone(final JComponent c, final Transferable t, final int action) {
		currentTime = -1;
		c.repaint();
	}

	@Override
	public boolean canImport(final TransferHandler.TransferSupport t) {
		t.setShowDropLocation(false);
		if (t
				.isDataFlavorSupported(ItemDataTransferable.itemDataFlavor)
				|| t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			if (t.getComponent() instanceof PlayList) {
				PlayList list = (PlayList) t.getComponent();
				if (currentTime == -1) {
					lastPlayList = list;
					TimeEventHandler.add(this, null);
				}
				currentTime = System.currentTimeMillis(); 
				list.setDropLocation((JList.DropLocation) t.getDropLocation());
				list.setDropAction(t.getDropAction());
				list.repaint(20);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean importData(final TransferHandler.TransferSupport t) {
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
					if (file.isDirectory()) {
						filePathList.addAll(directoryToFilePathList(file));
					} else {
						filePathList.add(file.getAbsolutePath());
					}
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

	private List<String> directoryToFilePathList(final File folder) {
		assert folder.isDirectory();

		List<String> returnList = new LinkedList<String>();

		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				returnList.addAll(directoryToFilePathList(file));
			} else {
				returnList.add(file.getAbsolutePath());
			}
		}

		return returnList;
	}

	@Override
	public boolean receiveTimeEvent(final long currentTime, final Object obj) {
		if (this.currentTime == -1) {
			if (lastPlayList != null) {
				lastPlayList.setDropLocation(null);
				lastPlayList.repaint(250);
			}
			return false;
		}
		
		if (currentTime - this.currentTime > 1000) {
			lastPlayList.setDropLocation(null);
			lastPlayList.repaint(250);
		}
		
		return true;
			
	}
}
