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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import smartsound.common.IObserver;
import smartsound.player.ItemData;

public class PlayListDataModel implements ListModel<ItemData>, IObserver {

	private final GUIController controller;
	private final UUID playListUUID;
	private final List<ListDataListener> listeners;

	public PlayListDataModel(final GUIController controller,
			final UUID playListUUID) {
		listeners = new LinkedList<ListDataListener>();
		this.playListUUID = playListUUID;
		this.controller = controller;
		controller.addObserver(this, playListUUID);
	}

	@Override
	public void addListDataListener(final ListDataListener listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
	}

	@Override
	public ItemData getElementAt(final int index) {
		return controller.getItemData(playListUUID, index);
	}

	@Override
	public int getSize() {
		return controller.getSize(playListUUID);
	}

	@Override
	public void removeListDataListener(final ListDataListener listener) {
		listeners.remove(listener);
	}

	public void remove(final int index, final boolean stop) {
		controller.removeItem(playListUUID, index, stop);
	}

	public void add(final int index, final String filePath) {
		controller.addItem(playListUUID, index, filePath);
	}

	public void add(final String filePath) {
		controller.addItem(playListUUID, getSize(), filePath);
	}

	public void addAll(final int index, final List<String> filePathList) {
		int i = 0;
		for (String string : filePathList) {
			String filePath = string;
			add(index + i, filePath);
			i++;
		}

	}

	public void addAll(final List<String> filePathList) {
		String filePath;
		for (Iterator<String> iterator = filePathList.iterator(); iterator
				.hasNext(); add(filePath))
			filePath = iterator.next();

	}

	private void notifyListeners() {
		ListDataListener listener;
		for (Iterator<ListDataListener> iterator = listeners.iterator(); iterator
				.hasNext(); listener.contentsChanged(null))
			listener = iterator.next();

	}

	@Override
	public void update(final UUID observableUUID) {
		if (observableUUID.equals(playListUUID))
			notifyListeners();
	}

	public int getIndexFromUuid(final UUID itemUUID) {
		return controller.getItemIndex(playListUUID, itemUUID);
	}

	public UUID getUUID() {
		return playListUUID;
	}

	public void importItems(final UUID sourcePlayListUUID,
			final List<UUID> itemUUIDs, final int targetIndex,
			final boolean copy) {
		controller.importItems(sourcePlayListUUID, itemUUIDs, playListUUID,
				targetIndex, copy);
	}

	public boolean isRepeatList() {
		return controller.isRepeatList(playListUUID);
	}

	public boolean isRandomizeList() {
		return controller.isRandomizeList(playListUUID);
	}

	public void setChainWith(final UUID source, final UUID target) {
		controller.getItemChainWithAction(playListUUID, source, "Chain with item action").execute(target);
	}

	public float getRandomizeVolumeFrom() {
		return controller.getRandomizeVolumeFrom(playListUUID);
	}

	public float getRandomizeVolumeTo() {
		return controller.getRandomizeVolumeTo(playListUUID);
	}

	public int getFadeIn() {
		return controller.getFadeIn(playListUUID);
	}

	public int getFadeOut() {
		return controller.getFadeOut(playListUUID);
	}

	public int getOverlap() {
		return controller.getOverlap(playListUUID);
	}

	public float getVolume() {
		return controller.getVolume(playListUUID);
	}
}
