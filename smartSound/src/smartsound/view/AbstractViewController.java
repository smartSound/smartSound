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

package smartsound.view;

import java.util.List;
import java.util.UUID;

import smartsound.common.IObserver;
import smartsound.common.PropertyMap;
import smartsound.common.Tuple;
import smartsound.player.ItemData;
import smartsound.player.LoadingException;
import smartsound.view.gui.GUIController;

public abstract class AbstractViewController
{

	public AbstractViewController()
	{
	}

	public abstract List<UUID> getPlayListUUIDs(UUID parentSetUUID);

	public abstract List<UUID> getPlayListSetUUIDs(UUID parentSetUUID);

	public abstract void addObserver(IObserver observer, UUID observableUUID);

	public abstract void addLayoutObserver(ILayoutObserver observer, UUID observableUUID);

	public abstract void removeLayoutObserver(ILayoutObserver observer, UUID observableUUID);

	public abstract ItemData getItemData(UUID uuid, int i);

	public abstract int getSize(UUID uuid);

	public abstract void addItem(UUID uuid, int i, String s);

	public abstract void addItem(UUID uuid, String s);

	public abstract void removeItem(UUID uuid, int i, boolean flag);

	public abstract int getItemIndex(UUID uuid, UUID uuid1);

	public abstract UUID addPlayList(UUID parentSetUUID);

	public abstract void deletePlayList(UUID uuid);

	public abstract void importItems(UUID uuid, List<UUID> list, UUID uuid1, int i, boolean flag);

	public abstract boolean itemIsActive(UUID uuid, UUID uuid1);

	public abstract String getItemName(UUID uuid, UUID uuid1);

	public abstract UUID getItemChainWith(UUID uuid, UUID uuid1);

	public abstract boolean itemIsRepeating(UUID uuid, UUID uuid1);

	public abstract Action getItemIsRepeatingAction(UUID uuid, UUID uuid1, String description);

	public abstract boolean isRepeatList(UUID uuid);

	public abstract Action getRepeatListAction(UUID uuid, String description);

	public abstract boolean isRandomizeList(UUID uuid);

	public abstract boolean isStopAfterEachSound(UUID uuid);

	public abstract float getRandomizeVolumeFrom(UUID uuid);

	public abstract float getRandomizeVolumeTo(UUID uuid);

	public abstract int getFadeIn(UUID uuid);

	public abstract int getFadeOut(UUID uuid);

	public abstract int getOverlap(UUID uuid);

	public abstract float getVolume(UUID uuid);

	public abstract Action getPlayAction(UUID uuid, int i, String description);

	public abstract Action getPlayAction(UUID uuid, String description);

	public abstract Action getRandomizeListAction(UUID uuid, String description);

	public abstract Action getRandomizeVolumeFromAction(UUID uuid, String description);

	public abstract Action getRandomizeVolumeToAction(UUID uuid, String description);

	public abstract Action getStopAfterEachSoundAction(UUID uuid, String description);

	public abstract Action getFadeInAction(UUID uuid, String description);

	public abstract Action getFadeOutAction(UUID uuid, String description);

	public abstract List<Tuple<String,Action>> getHotkeys(UUID sceneUUID, Action parent);

	public abstract Action getOverlapAction(UUID uuid, String description);

	public abstract Action getVolumeAction(UUID uuid, String description);

	public abstract Action getSaveAction();

	public abstract Action getLoadAction();

	public abstract Action getStopAction(UUID uuid, String description);

	public abstract Action getPlayIndexAction(UUID uuid, String description);

	public abstract Action getPlayAction(UUID uuid, UUID uuid1, String description);

	public abstract Action getItemChainWithAction(UUID uuid, UUID uuid1, String description);

	public abstract void addGUI(GUIController guicontroller);

	public abstract void setHotkey(UUID elementUUID, String s, Action action);

	public abstract void executeHotkey(String s);

	public abstract void removePlayList(UUID uuid);

	public abstract Action getPlayItemAction(UUID playListUUID, String description);

	public abstract void removeAllHotkeys();

	public abstract void removeHotkey(UUID elementUUID, String hotkey, Action action);

	public abstract PropertyMap getPropertyMap();

	public abstract void loadFromPropertyMap(PropertyMap pMap) throws LoadingException;

	public abstract String getHotkey(Action action);

	public abstract void removeHotkey(Action action);

	public abstract Action getSetRepeatItemAction(UUID uuid, String description);

	public abstract Action getSetItemChainWithAction(UUID uuid, String description);

	public abstract String getTitle(UUID uuid);

	public abstract UUID getParent(UUID child);

	public abstract Layout getLayout(UUID parentSetUUID);

	public abstract UUID addPlayListSet(UUID parentSetUUID);

	public abstract void shiftElement(UUID playListUUID, int x, int y,
			PositionType alignment);

	public abstract void reloadView();

	public abstract void setTitle(UUID uuid, String newTitle);

	public enum PositionType {
		ABOVE, LEFT
	}

	public abstract void remove(UUID uuid);

	public abstract boolean isActive(UUID elementUUID);

	public abstract void setAutoplay(UUID elementUUID, boolean autoplay);

	public abstract boolean getAutoplay(UUID elementUUID);

	public abstract void setActive(UUID playListSetUUID);

	public abstract List<Tuple<String, Action>> getHotkeys(UUID sceneUUID);

	public abstract List<Tuple<Action, String>> getHotkeyComments();

	public abstract void setHotkeyDescription(Action action, String description);
}
