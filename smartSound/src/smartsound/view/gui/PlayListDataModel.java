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

import smartsound.common.IElement.NameValuePair;
import smartsound.common.IObserver;
import smartsound.view.LayoutManager;

public class PlayListDataModel implements ListModel<ItemData>, IObserver {

	private final GUIController controller;
	private final UUID playListUUID;
	private final List<ListDataListener> listeners;

	public PlayListDataModel(final GUIController controller,
			final UUID layoutUUID) {
		listeners = new LinkedList<ListDataListener>();
		this.playListUUID = (UUID) LayoutManager.get(layoutUUID, "ELEMENTUUID").get("ELEMENTUUID");
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
		NameValuePair[] pairs = controller.get(playListUUID, "PLAYLISTITEMS");
		assert pairs.length == 1;
		assert pairs[0].value instanceof List<?>;

		boolean allUUIDs = true;
		List<?> list = (List<?>) pairs[0].value;
		for (Object o : list) {
			if (!(o instanceof UUID)) {
				allUUIDs = false;
				break;
			}
		}

		assert allUUIDs;

		UUID uuid = (UUID) list.get(index);

		return new ItemData(uuid, controller);
	}

	@Override
	public int getSize() {
		NameValuePair[] pairs = controller.get(playListUUID,  "SIZE");
		assert pairs.length == 1;
		assert pairs[0].value instanceof Integer;

		return (Integer) pairs[0].value;
	}

	@Override
	public void removeListDataListener(final ListDataListener listener) {
		listeners.remove(listener);
	}

	public void remove(final int index, final boolean stop) {
		controller.remove(getElementAt(index).getUUID());
	}

	public void add(final int index, final String filePath) {
		controller.add(playListUUID, "PLAYLISTITEM", filePath, index);
	}

	public void add(final String filePath) {
		controller.add(playListUUID, "PLAYLISTITEM", filePath, getSize());
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
		NameValuePair[] pairs = controller.get(itemUUID, "INDEX");
		assert pairs.length == 0;
		assert pairs[0].value instanceof Integer;

		return (Integer) pairs[0].value;
	}

	public UUID getUUID() {
		return playListUUID;
	}

	public void importItems(final UUID sourcePlayListUUID,
			final List<UUID> itemUUIDs, final int targetIndex,
			final boolean copy) {

		controller.add(getUUID(), "PLAYLISTITEM", itemUUIDs, targetIndex, copy);
	}

	public boolean isRepeatList() {
		NameValuePair[] pairs = controller.get(getUUID(), "REPEAT");
		assert pairs.length == 0;
		assert pairs[0].value instanceof Boolean;

		return (Boolean) pairs[0].value;
	}

	public boolean isRandomizeList() {
		NameValuePair[] pairs = controller.get(getUUID(), "RANDOMIZE");
		assert pairs.length == 0;
		assert pairs[0].value instanceof Boolean;

		return (Boolean) pairs[0].value;
	}

	public void setChainWith(final UUID source, final UUID target) {
		controller.set(source, "CHAINEDWITH", target);
	}

	public float getRandomizeVolumeFrom() {
		NameValuePair[] pairs = controller.get(getUUID(), "RANDOMIZEVOLUMEFROM");
		assert pairs.length == 0;
		assert pairs[0].value instanceof Float;

		return (Float) pairs[0].value;
	}

	public float getRandomizeVolumeTo() {
		NameValuePair[] pairs = controller.get(getUUID(), "RANDOMIZEVOLUMETO");
		assert pairs.length == 0;
		assert pairs[0].value instanceof Float;

		return (Float) pairs[0].value;
	}

	public int getFadeIn() {
		NameValuePair[] pairs = controller.get(getUUID(), "FADEINTIME");
		assert pairs.length == 0;
		assert pairs[0].value instanceof Integer;

		return (Integer) pairs[0].value;
	}

	public int getFadeOut() {
		NameValuePair[] pairs = controller.get(getUUID(), "FADEOUTTIME");
		assert pairs.length == 0;
		assert pairs[0].value instanceof Integer;

		return (Integer) pairs[0].value;
	}

	public int getOverlap() {
		NameValuePair[] pairs = controller.get(getUUID(), "OVERLAPTIME");
		assert pairs.length == 0;
		assert pairs[0].value instanceof Integer;

		return (Integer) pairs[0].value;
	}

	public float getVolume() {
		NameValuePair[] pairs = controller.get(getUUID(), "VOLUME");
		assert pairs.length == 0;
		assert pairs[0].value instanceof Float;

		return (Float) pairs[0].value;
	}

	public String getTitle() {
		NameValuePair[] pairs = controller.get(getUUID(), "NAME");
		assert pairs.length == 1;
		assert pairs[0].value instanceof String;

		return (String) pairs[0].value;
	}
}
