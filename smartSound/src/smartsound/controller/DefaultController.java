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

import smartsound.common.PropertyMap;
import smartsound.player.IPlayListObserver;
import smartsound.player.ItemData;
import smartsound.player.LoadingException;
import smartsound.player.PlayList;
import smartsound.player.PlayListItem;
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

	private Map<UUID, List<IPlayListObserver>> observerMap = new HashMap<UUID, List<IPlayListObserver>>();
	private Map<UUID, PlayList> playListMap = new HashMap<UUID, PlayList>();
	private UUID controllerUUID = UUID.randomUUID();
	private List<UUID> playListUUIDs = new LinkedList<UUID>();
	private AbstractViewController viewController;
	
	/**
	 * 
	 * @param parent The <c>JFrame</c> to which the controller adds its view
	 * 	elements.
	 */
	public DefaultController() {
		viewController = new ViewController(this);
		viewController.addGUI(new GUIController(viewController));
		addPlayList();
	}
	
	@Override
	public void addObserver(IPlayListObserver observer, UUID playListUUID) {
		if (observerMap.get(playListUUID) == null) {
			observerMap.put(playListUUID, new LinkedList<IPlayListObserver>());
		}
		observerMap.get(playListUUID).add(observer);
	}

	@Override
	public ItemData getItemData(UUID playListUUID, int index) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return null;
		}
		return new ItemData(this, playListUUID, playList.UUIDfromIndex(index));
	}

	@Override
	public int getSize(UUID playListUUID) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return -1;
		}
		return playList.getSize();
	}

	@Override
	public void addItem(UUID playListUUID, int index, String filePath) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return;
		}
		playList.add(index, filePath);
	}

	@Override
	public void addItem(UUID playListUUID, String filePath) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return;
		}
		playList.add(filePath);
	}

	@Override
	public void removeItem(UUID playListUUID, int index, boolean stop) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return;
		}
		playList.remove(index, stop);
	}

	@Override
	public void play(UUID playListUUID, int index) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return;
		}
		playList.play(index);
	}

	@Override
	public void play(UUID playListUUID) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return;
		}
		playList.play();
	}

	@Override
	public int getItemIndex(UUID playListUUID, UUID itemUUID) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return -1;
		}
		return playList.getEntryIndex(itemUUID);
	}

	@Override
	public void stop(UUID playListUUID) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return;
		}
		
		playList.stop();
	}

	@Override
	public UUID addPlayList() {
		PlayList playList = new PlayList();
		addPlayList(playList);
		
		return playList.getUUID();
	}
	
	private void addPlayList(PlayList playList) {
		this.playListMap.put(playList.getUUID(), playList);
		playListUUIDs.add(playList.getUUID());
		playList.addObserver(this);
		newPlayList(playList.getUUID());
	}

	@Override
	public void deletePlayList(UUID playListUUID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void importItems(UUID sourcePlayListUUID, List<UUID> itemUUIDs,
			UUID targetPlayListUUID, int targetIndex, boolean copy) {
		Map<UUID, UUID> uuidMap = new HashMap<UUID, UUID>();
		PlayList sourcePlayList = playListMap.get(sourcePlayListUUID);
		PlayList targetPlayList = playListMap.get(targetPlayListUUID);
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
			newPlayListItem = new  PlayListItem(item, !copy);
			targetPlayList.add(i++ - shift, newPlayListItem);
			if (!copy) {
				uuidMap.put(item.getUUID(), newPlayListItem.getUUID());
			}
		}
		
	}

	@Override
	public void playListChanged(UUID playListUUID) {
		List<IPlayListObserver> observers = observerMap.get(playListUUID);
		if (observers == null) {
			return;
		}
		for (IPlayListObserver observer : observers) {
			observer.playListChanged(playListUUID);
		}
	}

	@Override
	public boolean itemIsActive(UUID playListUUID, UUID itemUUID) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return false;
		}
		return playList.itemIsActive(itemUUID);
	}

	@Override
	public String getItemName(UUID playListUUID, UUID itemUUID) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return null;
		}
		return playList.getItemName(itemUUID);
	}

	@Override
	public UUID getItemChainWith(UUID playListUUID, UUID itemUUID) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return null;
		}
		return playList.getItemChainWith(itemUUID);
	}

	@Override
	public void setItemChainWith(UUID playListUUID, UUID itemUUID,
			UUID chainWith) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return;
		}
		playList.setItemChainWith(itemUUID, chainWith);
	}

	@Override
	public boolean itemIsRepeating(UUID playListUUID, UUID itemUUID) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return false;
		}
		return playList.itemIsRepeating(itemUUID);
	}

	@Override
	public void setItemIsRepeating(UUID playListUUID, UUID itemUUID,
			boolean isRepeating) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return;
		}
		playList.setItemIsRepeating(itemUUID, isRepeating);
	}

	@Override
	public boolean isRepeatList(UUID playListUUID) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return false;
		}
		return playList.isRepeatList();
	}

	@Override
	public void setRepeatList(UUID playListUUID, boolean repeat) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return;
		}
		playList.setRepeatList(repeat);
	}

	@Override
	public boolean isRandomizeList(UUID playListUUID) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return false;
		}
		return playList.isRandomizeList();
	}

	@Override
	public void setRandomizeList(UUID playListUUID, boolean randomize) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return;
		}
		playList.setRandomizeList(randomize);
		
	}

	@Override
	public void setRandomizeVolumeFrom(UUID playListUUID, float from) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return;
		}
		playList.setRandomizeVolumeFrom(from);
	}

	@Override
	public void setRandomizeVolumeTo(UUID playListUUID, float to) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return;
		}
		playList.setRandomizeVolumeTo(to);
	}

	@Override
	public void setStopAfterEachSound(UUID playListUUID,
			boolean stopAfterEachSound) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return;
		}
		playList.setStopAfterEachSound(stopAfterEachSound);
	}

	@Override
	public void setFadeIn(UUID playListUUID, int fadeIn) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return;
		}
		playList.setFadeInLength(fadeIn);
	}

	@Override
	public void setFadeOut(UUID playListUUID, int fadeOut) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return;
		}
		playList.setFadeOutLength(fadeOut);
	}

	@Override
	public void setOverlap(UUID playListUUID, int overlap) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return;
		}
		playList.setOverlapTime(overlap);
	}

	@Override
	public boolean isStopAfterEachSound(UUID playListUUID) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return false;
		}
		return playList.isStopAfterEachSound();
	}

	@Override
	public float getRandomizeVolumeFrom(UUID playListUUID) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return -1;
		}
		return playList.getRandomizeVolumeFrom();
	}

	@Override
	public float getRandomizeVolumeTo(UUID playListUUID) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return -1;
		}
		return playList.getRandomizeVolumeTo();
	}

	@Override
	public int getFadeIn(UUID playListUUID) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return -1;
		}
		return playList.getFadeInLength();
	}

	@Override
	public int getFadeOut(UUID playListUUID) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return -1;
		}
		return playList.getFadeOutLength();
	}

	@Override
	public int getOverlap(UUID playListUUID) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return -1;
		}
		return playList.getOverlapTime();
	}

	@Override
	public void setVolume(UUID playListUUID, float volume) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return;
		}
		playList.setVolume(volume);
	}

	@Override
	public float getVolume(UUID playListUUID) {
		PlayList playList = playListMap.get(playListUUID);
		if (playList == null) {
			//TODO: Exception
			return -1;
		}
		return playList.getVolume();
	}

	@Override
	public void save(String savePath) {
		PropertyMap map = new PropertyMap(controllerUUID);
		
		for (PlayList list : playListMap.values()) {
			map.addPropertyMap(list.getPropertyMap());
		}
		
		map.saveToIni(savePath);
	}

	@Override
	public void load(String loadPath) {
		PropertyMap map = new PropertyMap(loadPath);
		
		for (PlayList list : playListMap.values()) {
			remove(list.getUUID());
			list.stop();
		}
		playListMap.clear();
		
		
		PlayList newList = null;
		try {
			newList = new PlayList(map.getNestedMaps().get(0));
		} catch (LoadingException e) {
			e.printStackTrace();
		}
		addPlayList(newList);
	}

	@Override
	public List<UUID> getPlayListUUIDs() {
		return new LinkedList<UUID>(playListUUIDs);
	}
	
	private void remove(UUID playListUUID) {
		viewController.removePlayList(playListUUID);
	}
	
	private void newPlayList(UUID playListUUID) {
		viewController.newPlayList(playListUUID);
	}

}
