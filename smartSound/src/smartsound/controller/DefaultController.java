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

package smartsound.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import smartsound.common.IObserver;
import smartsound.common.Observable;
import smartsound.common.PropertyMap;
import smartsound.player.ItemData;
import smartsound.player.LoadingException;
import smartsound.player.PlayList;
import smartsound.player.PlayListItem;
import smartsound.player.PlayListSet;
import smartsound.player.PlayListSetElement;
import smartsound.view.AbstractViewController;
import smartsound.view.ViewController;
import smartsound.view.gui.GUIController;

/**
 * Default implementation of <c>AbstractController</c>. Creates a view for
 * user input.
 * @author André Becker
 *
 */
public class DefaultController extends AbstractController {

	private final UUID controllerUUID = UUID.randomUUID();
	private final AbstractViewController viewController;
	private final Map<UUID, PlayListSet> rootPlayListSets = new HashMap<UUID, PlayListSet>();
	private UUID activeSet = null;

	/**
	 * 
	 * @param parent The <c>JFrame</c> to which the controller adds its view
	 * 	elements.
	 */
	public DefaultController() {
		viewController = new ViewController(this);
		viewController.addGUI(new GUIController(viewController));
		UUID set = addPlayListSet(null);
		setActive(set);
	}

	@Override
	public void addObserver(final IObserver observer, final UUID uuid) {
		if (uuid == null) {
			addObserver(observer);
			return;
		}
		PlayListSetElement element = getElement(uuid);
		if (element instanceof Observable) {
			((Observable) element).addObserver(observer);
		}
	}

	@Override
	public ItemData getItemData(final UUID playListUUID, final int index) {
		PlayList playList = getPlayList(playListUUID);

		if (playList == null) {
			//TODO: Exception
			return null;
		}
		return new ItemData(this, playListUUID, playList.UUIDfromIndex(index));
	}

	private PlayList getPlayList(final UUID playListUUID) {
		PlayList playList = null;

		for (PlayListSet set : rootPlayListSets.values()) {
			playList = set.getPlayList(playListUUID);
			if (playList != null)
				break;
		}

		return playList;
	}

	private PlayListSet getPlayListSet(final UUID playListSetUUID) {
		PlayListSet set = rootPlayListSets.get(playListSetUUID);

		if (set == null)
			for (PlayListSet s : rootPlayListSets.values()) {
				set = s.getPlayListSet(playListSetUUID);
				if (set != null)
					break;
			}

		return set;
	}

	@Override
	public int getSize(final UUID playListUUID) {
		PlayList playList = getPlayList(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return -1;
		}
		return playList.getSize();
	}

	@Override
	public void addItem(final UUID playListUUID, final int index, final String filePath) {
		if (index == -1) {
			addItem(playListUUID, filePath);
			return;
		}

		PlayList playList = getPlayList(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return;
		}
		playList.add(index, filePath);
	}

	@Override
	public void addItem(final UUID playListUUID, final String filePath) {
		PlayList playList = getPlayList(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return;
		}
		playList.add(filePath);
	}

	@Override
	public void removeItem(final UUID playListUUID, final int index, final boolean stop) {
		PlayList playList = getPlayList(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return;
		}
		playList.remove(index, stop);
	}

	@Override
	public void play(final UUID playListUUID, final int index) {
		PlayList playList = getPlayList(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return;
		}
		playList.play(index);

		setActive(getRootParent(playListUUID));
		stopOthers(playListUUID);
	}

	@Override
	public void play(final UUID playListUUID) {
		PlayListSetElement element = getPlayList(playListUUID);
		element = (element != null) ? element : getPlayListSet(playListUUID);
		element.play();

		stopOthers(playListUUID);
		setActive(getRootParent(playListUUID));
	}

	@Override
	public int getItemIndex(final UUID playListUUID, final UUID itemUUID) {
		PlayList playList = getPlayList(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return -1;
		}
		return playList.getEntryIndex(itemUUID);
	}

	private void stopOthers(final UUID uuid) {
		UUID rootParent = getRootParent(uuid);

		for (PlayListSet set : rootPlayListSets.values()) {
			if (!set.getUUID().equals(rootParent))
				set.stop();
		}
	}

	@Override
	public UUID getRootParent(final UUID uuid) {
		UUID current = uuid;
		UUID parent = getParent(uuid);
		while (parent != null) {
			current = parent;
			parent = getParent(current);
		}
		return current;
	}

	@Override
	public void stop(final UUID playListUUID) {
		PlayListSetElement element = getElement(playListUUID);
		if (element != null)
			element.stop();
	}

	private PlayListSetElement getElement(final UUID uuid) {
		PlayListSetElement element = getPlayList(uuid);
		return element != null ? element : getPlayListSet(uuid);
	}
	@Override
	public UUID addPlayList(final UUID parentSet) {
		if (getPlayListSet(parentSet) == null)
			return null;

		PlayList playList = new PlayList();
		addPlayList(parentSet, playList);

		return playList.getUUID();
	}

	private void addPlayList(final UUID parentSet, final PlayList playList) {
		PlayListSet set = getPlayListSet(parentSet);
		if (set == null)
			return;

		set.addPlayList(playList);

		playList.addObserver(this);
		//newPlayList(playList.getUUID());
	}

	@Override
	public void deletePlayList(final UUID playListUUID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void importItems(final UUID sourcePlayListUUID, final List<UUID> itemUUIDs,
			final UUID targetPlayListUUID, final int targetIndex, final boolean copy) {
		Map<UUID, UUID> uuidMap = new HashMap<UUID, UUID>();
		PlayList sourcePlayList = getPlayList(sourcePlayListUUID);
		PlayList targetPlayList = getPlayList(targetPlayListUUID);
		if (sourcePlayList == null || targetPlayList == null) {
			//TODO: Exception
			return;
		}

		int i;
		if (targetIndex > -1) {
			i = targetIndex;
		} else {
			i = targetPlayList.getSize();
		}

		int shift = 0;
		List<PlayListItem> playListItemList = new LinkedList<PlayListItem>();
		for (UUID uuid: itemUUIDs) {
			playListItemList.add(sourcePlayList.getPlayListItem(uuid));
		}

		if (sourcePlayList == targetPlayList) {
			for (UUID uuid: itemUUIDs) {
				if (sourcePlayList.getEntryIndex(uuid) <= i) {
					shift += 1;
				}
			}
		}

		PlayListItem playListItem;

		boolean removeChaining = sourcePlayList != targetPlayList;
		if (!copy) {

			for (UUID itemUUID : itemUUIDs) {
				sourcePlayList.remove(itemUUID, false, removeChaining);
			}
			if (sourcePlayList == targetPlayList) {
				for (int j = 0; j < sourcePlayList.getSize(); j++) {
					playListItem = sourcePlayList.getEntryAt(j);
					if (playListItem.getChainWith() != null && uuidMap.containsKey(playListItem.getChainWith())) {
						playListItem.setChainWith(uuidMap.get(playListItem.getChainWith()));
					}
				}
			}

		}

		PlayListItem newPlayListItem;
		for (PlayListItem item : playListItemList) {
			newPlayListItem = new PlayListItem(item, !copy);
			targetPlayList.add(i++ - shift, newPlayListItem);
			if (!copy) {
				uuidMap.put(item.getUUID(), newPlayListItem.getUUID());
				item.dispose();
			}
		}
	}

	@Override
	public boolean itemIsActive(final UUID playListUUID, final UUID itemUUID) {
		PlayList playList = getPlayList(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return false;
		}
		return playList.itemIsActive(itemUUID);
	}

	@Override
	public String getItemName(final UUID playListUUID, final UUID itemUUID) {
		PlayList playList = getPlayList(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return null;
		}
		return playList.getItemName(itemUUID);
	}

	@Override
	public UUID getItemChainWith(final UUID playListUUID, final UUID itemUUID) {
		PlayList playList = getPlayList(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return null;
		}
		return playList.getItemChainWith(itemUUID);
	}

	@Override
	public void setItemChainWith(final UUID playListUUID, final UUID itemUUID,
			final UUID chainWith) {
		PlayList playList = getPlayList(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return;
		}
		playList.setItemChainWith(itemUUID, chainWith);
	}

	@Override
	public boolean itemIsRepeating(final UUID playListUUID, final UUID itemUUID) {
		PlayList playList = getPlayList(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return false;
		}
		return playList.itemIsRepeating(itemUUID);
	}

	@Override
	public void setItemIsRepeating(final UUID playListUUID, final UUID itemUUID,
			final boolean isRepeating) {
		PlayList playList = getPlayList(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return;
		}
		playList.setItemIsRepeating(itemUUID, isRepeating);
	}

	@Override
	public boolean isRepeatList(final UUID playListUUID) {
		PlayList playList = getPlayList(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return false;
		}
		return playList.isRepeatList();
	}

	@Override
	public void setRepeatList(final UUID playListUUID, final boolean repeat) {
		PlayList playList = getPlayList(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return;
		}
		playList.setRepeatList(repeat);
	}

	@Override
	public boolean isRandomizeList(final UUID playListUUID) {
		PlayList playList = getPlayList(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return false;
		}
		return playList.isRandomizeList();
	}

	@Override
	public void setRandomizeList(final UUID playListUUID, final boolean randomize) {
		PlayList playList = getPlayList(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return;
		}
		playList.setRandomizeList(randomize);

	}

	@Override
	public void setRandomizeVolumeFrom(final UUID playListUUID, final float from) {
		PlayList playList = getPlayList(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return;
		}
		playList.setRandomizeVolumeFrom(from);
	}

	@Override
	public void setRandomizeVolumeTo(final UUID playListUUID, final float to) {
		PlayList playList = getPlayList(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return;
		}
		playList.setRandomizeVolumeTo(to);
	}

	@Override
	public void setStopAfterEachSound(final UUID playListUUID,
			final boolean stopAfterEachSound) {
		PlayList playList = getPlayList(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return;
		}
		playList.setStopAfterEachSound(stopAfterEachSound);
	}

	@Override
	public void setFadeIn(final UUID playListUUID, final int fadeIn) {
		PlayList playList = getPlayList(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return;
		}
		playList.setFadeInLength(fadeIn);
	}

	@Override
	public void setFadeOut(final UUID playListUUID, final int fadeOut) {
		PlayList playList = getPlayList(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return;
		}
		playList.setFadeOutLength(fadeOut);
	}

	@Override
	public void setOverlap(final UUID playListUUID, final int overlap) {
		PlayList playList = getPlayList(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return;
		}
		playList.setOverlapTime(overlap);
	}

	@Override
	public boolean isStopAfterEachSound(final UUID playListUUID) {
		PlayList playList = getPlayList(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return false;
		}
		return playList.isStopAfterEachSound();
	}

	@Override
	public float getRandomizeVolumeFrom(final UUID playListUUID) {
		PlayList playList = getPlayList(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return -1;
		}
		return playList.getRandomizeVolumeFrom();
	}

	@Override
	public float getRandomizeVolumeTo(final UUID playListUUID) {
		PlayList playList = getPlayList(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return -1;
		}
		return playList.getRandomizeVolumeTo();
	}

	@Override
	public int getFadeIn(final UUID playListUUID) {
		PlayList playList = getPlayList(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return -1;
		}
		return playList.getFadeInLength();
	}

	@Override
	public int getFadeOut(final UUID playListUUID) {
		PlayList playList = getPlayList(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return -1;
		}
		return playList.getFadeOutLength();
	}

	@Override
	public int getOverlap(final UUID playListUUID) {
		PlayList playList = getPlayList(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return -1;
		}
		return playList.getOverlapTime();
	}

	@Override
	public void setVolume(final UUID playListUUID, final float volume) {
		PlayListSetElement element = getElement(playListUUID);
		if (element == null) {
			//TODO: Exception
			return;
		}
		element.setVolume(volume);
	}

	@Override
	public float getVolume(final UUID playListUUID) {
		PlayListSetElement element = getElement(playListUUID);
		if (element == null) {
			//TODO: Exception
			return -1;
		}
		return element.getVolume();
	}

	@Override
	public void save(final String savePath) {
		PropertyMap map = new PropertyMap(controllerUUID);

		for (PlayListSet set : rootPlayListSets.values()) {
			map.addPropertyMap(set.getPropertyMap());
		}

		map.addPropertyMap(viewController.getPropertyMap());

		map.saveToIni(savePath);
	}

	@Override
	public void load(final String loadPath) {

		for (PlayListSet set : rootPlayListSets.values()) {
			set.stop();
			set.dispose();
		}
		rootPlayListSets.clear();


		PropertyMap map = new PropertyMap(loadPath);
		PlayListSet set;
		boolean firstSet = true;
		try {
			for (PropertyMap pMap : map.getNestedMaps()) {
				if (pMap.get("type").equals(PlayListSet.class.getCanonicalName())) {
					set = new PlayListSet(pMap);
					addPlayListSet(null, set);
					if (firstSet) {
						firstSet = false;
						activeSet = set.getUUID();
					}
				} else if (pMap.get("type").equals(ViewController.class.getCanonicalName())) {
					viewController.loadFromPropertyMap(pMap);
				}
			}
		} catch (LoadingException e) {
			e.printStackTrace();
		}

		viewController.reloadView();
	}

	@Override
	public List<UUID> getPlayListUUIDs(final UUID parentSetUUID) {
		List<UUID> result = new LinkedList<UUID>();
		PlayListSet set = getPlayListSet(parentSetUUID);

		if (set == null)
			return null;

		for (PlayList pList : set.getNestedPlayLists()) {
			result.add(pList.getUUID());
		}

		return result;
	}

	@Override
	public List<UUID> getPlayListSetUUIDs(final UUID parentSetUUID) {
		if (parentSetUUID == null) {
			return new LinkedList<UUID>(rootPlayListSets.keySet());
		}
		PlayListSet set = rootPlayListSets.get(parentSetUUID);
		List<PlayListSet> currentList = new LinkedList<PlayListSet>(rootPlayListSets.values());
		List<PlayListSet> nextList = new LinkedList<PlayListSet>();
		while (set == null) {
			if (currentList.isEmpty()) {
				break;
			}
			for (PlayListSet currentSet : currentList) {
				if (currentSet.getUUID().equals(parentSetUUID)) {
					set = currentSet;
					break;
				}
				nextList.add(currentSet);
			}
			currentList = new LinkedList<PlayListSet>(nextList);
		}

		if (set == null)
			return null;

		List<UUID> result = new LinkedList<UUID>();
		for (PlayListSet list : set.getNestedSets()) {
			result.add(list.getUUID());
		}
		return result;
	}

	@Override
	public String getTitle(final UUID uuid) {
		PlayListSetElement elem = getPlayListSet(uuid);
		elem = elem == null ? getPlayList(uuid) : elem;

		return elem == null ? null : elem.getName();
	}

	@Override
	public UUID addPlayListSet(final UUID parentSetUUID) {
		PlayListSet newSet = new PlayListSet();
		addPlayListSet(parentSetUUID, newSet);
		addPlayList(newSet.getUUID());

		return newSet.getUUID();
	}

	private void addPlayListSet(final UUID parentSetUUID, final PlayListSet playListSet) {
		if (parentSetUUID == null) {
			rootPlayListSets.put(playListSet.getUUID(), playListSet);
			update();
		} else {
			for (PlayListSet set : rootPlayListSets.values()) {
				if (set.addPlayList(parentSetUUID, playListSet))
					break;
			}
		}
	}

	@Override
	public UUID getParent(final UUID child) {
		if (rootPlayListSets.containsKey(child))
			return null;

		UUID result = null;

		for (PlayListSetElement element : rootPlayListSets.values()) {
			result = element.getParent(child);
			if (result != null)
				return result;
		}

		return null;
	}

	@Override
	public void setTitle(final UUID uuid, final String newTitle) {
		PlayListSetElement element = getPlayList(uuid);
		element = (element != null) ? element : getPlayListSet(uuid);
		if (element == null)
			return;

		element.setName(newTitle);
	}

	@Override
	public void remove(final UUID uuid) {
		UUID parentUUID = getParent(uuid);
		if (parentUUID == null) {
			rootPlayListSets.remove(uuid);
			update();
		} else {
			PlayListSetElement parent = getElement(parentUUID);
			parent.remove(uuid);
			parent.update();
		}

	}

	@Override
	public void update(final UUID arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isActive(final UUID elementUUID) {
		return elementUUID != null ? elementUUID.equals(activeSet) : false;
	}

	@Override
	public void setAutoplay(final UUID elementUUID, final boolean autoplay) {
		PlayListSetElement element = getElement(elementUUID);
		if (element != null) {
			element.setAutoPlay(autoplay);
		}
	}

	@Override
	public boolean getAutoplay(final UUID elementUUID) {
		PlayListSetElement element = getElement(elementUUID);
		if (element != null) {
			return element.getAutoPlay();
		}
		return false;
	}

	@Override
	public void setActive(final UUID playListSetUUID) {
		activeSet = playListSetUUID;
	}

}
