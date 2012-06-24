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

import smartsound.common.PropertyMap;
import smartsound.common.Tuple;
import smartsound.player.IPlayListObserver;
import smartsound.player.ItemData;
import smartsound.player.LoadingException;
import smartsound.view.gui.GUIController;

public abstract class AbstractViewController
{

    public AbstractViewController()
    {
    }

    public abstract List<UUID> getPlayListUUIDs();

    public abstract void addObserver(IPlayListObserver iplaylistobserver, UUID uuid);

    public abstract ItemData getItemData(UUID uuid, int i);

    public abstract int getSize(UUID uuid);

    public abstract void addItem(UUID uuid, int i, String s);

    public abstract void addItem(UUID uuid, String s);

    public abstract void removeItem(UUID uuid, int i, boolean flag);

    public abstract int getItemIndex(UUID uuid, UUID uuid1);

    public abstract UUID addPlayList();

    public abstract void deletePlayList(UUID uuid);

    public abstract void importItems(UUID uuid, List<UUID> list, UUID uuid1, int i, boolean flag);

    public abstract boolean itemIsActive(UUID uuid, UUID uuid1);

    public abstract String getItemName(UUID uuid, UUID uuid1);

    public abstract UUID getItemChainWith(UUID uuid, UUID uuid1);

    public abstract boolean itemIsRepeating(UUID uuid, UUID uuid1);

    public abstract Action getItemIsRepeatingAction(UUID uuid, UUID uuid1);

    public abstract boolean isRepeatList(UUID uuid);

    public abstract Action getRepeatListAction(UUID uuid);

    public abstract boolean isRandomizeList(UUID uuid);

    public abstract boolean isStopAfterEachSound(UUID uuid);

    public abstract float getRandomizeVolumeFrom(UUID uuid);

    public abstract float getRandomizeVolumeTo(UUID uuid);

    public abstract int getFadeIn(UUID uuid);

    public abstract int getFadeOut(UUID uuid);

    public abstract int getOverlap(UUID uuid);

    public abstract float getVolume(UUID uuid);

    public abstract Action getPlayAction(UUID uuid, int i);

    public abstract Action getPlayAction(UUID uuid);

    public abstract Action getRandomizeListAction(UUID uuid);

    public abstract Action getRandomizeVolumeFromAction(UUID uuid);

    public abstract Action getRandomizeVolumeToAction(UUID uuid);

    public abstract Action getStopAfterEachSoundAction(UUID uuid);

    public abstract Action getFadeInAction(UUID uuid);

    public abstract Action getFadeOutAction(UUID uuid);
    
    public abstract List<Tuple<String,Action>> getHotkeys(Action parent);

    public abstract Action getOverlapAction(UUID uuid);

    public abstract Action getVolumeAction(UUID uuid);

    public abstract Action getSaveAction();

    public abstract Action getLoadAction();

    public abstract Action getStopAction(UUID uuid);

    public abstract Action getPlayIndexAction(UUID uuid);

    public abstract Action getPlayAction(UUID uuid, UUID uuid1);

    public abstract Action getItemChainWithAction(UUID uuid, UUID uuid1);

    public abstract void addGUI(GUIController guicontroller);

    public abstract void setHotkey(String s, Action action);

    public abstract void executeHotkey(String s);

    public abstract void removePlayList(UUID uuid);

    public abstract void newPlayList(UUID uuid);

	public abstract Action getPlayItemAction(UUID playListUUID);
	
	public abstract void removeAllHotkeys();
	
	public abstract void removeHotkey(String hotkey, Action action);
	
	public abstract PropertyMap getPropertyMap();

	public abstract void loadFromPropertyMap(PropertyMap pMap) throws LoadingException;
}
