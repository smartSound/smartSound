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

import java.util.*;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import smartsound.player.IPlayListObserver;
import smartsound.player.ItemData;
import smartsound.view.Action;


public class PlayListDataModel
    implements ListModel<ItemData>, IPlayListObserver
{

	private GUIController controller;
    private UUID playListUUID;
    private List<ListDataListener> listeners;
    private Action playAction;
    private Action playIndexAction;
    private Action stopAction;
    private Action repeatListAction;
    private Action randomizeListAction;
    private Action randomizeVolumeFromAction;
    private Action randomizeVolumeToAction;
    private Action stopAfterEachSoundAction;
    private Action fadeInAction;
    private Action fadeOutAction;
    private Action overlapAction;
    private Action volumeAction;
	
    public PlayListDataModel(GUIController controller, UUID playListUUID)
    {
        listeners = new LinkedList<ListDataListener>();
        this.playListUUID = playListUUID;
        this.controller = controller;
        controller.addObserver(this, playListUUID);
        playAction = controller.getPlayAction(playListUUID);
        playIndexAction = controller.getPlayIndexAction(playListUUID);
        stopAction = controller.getStopAction(playListUUID);
        repeatListAction = controller.getRepeatListAction(playListUUID);
        randomizeListAction = controller.getRandomizeListAction(playListUUID);
        randomizeVolumeFromAction = controller.getRandomizeVolumeFromAction(playListUUID);
        randomizeVolumeToAction = controller.getRandomizeVolumeToAction(playListUUID);
        stopAfterEachSoundAction = controller.getStopAfterEachSoundAction(playListUUID);
        fadeInAction = controller.getFadeInAction(playListUUID);
        fadeOutAction = controller.getFadeOutAction(playListUUID);
        overlapAction = controller.getOverlapAction(playListUUID);
        volumeAction = controller.getVolumeAction(playListUUID);
        controller.setHotkey("1", controller.getPlayAction(playListUUID, 0));
        controller.setHotkey("2", controller.getPlayAction(playListUUID, 1));
        controller.setHotkey("3", controller.getPlayAction(playListUUID, 2));
        controller.setHotkey("4", controller.getPlayAction(playListUUID, 3));
        controller.setHotkey("5", controller.getPlayAction(playListUUID, 4));
        controller.setHotkey("6", controller.getPlayAction(playListUUID, 5));
        controller.setHotkey("7", controller.getPlayAction(playListUUID, 6));
        controller.setHotkey("8", controller.getPlayAction(playListUUID, 7));
        controller.setHotkey("9", controller.getPlayAction(playListUUID, 8));
        controller.setHotkey("0", controller.getPlayAction(playListUUID, 9));
    }

    public void addListDataListener(ListDataListener listener)
    {
        if(!listeners.contains(listener))
            listeners.add(listener);
    }

    public ItemData getElementAt(int index)
    {
        return controller.getItemData(playListUUID, index);
    }

    public int getSize()
    {
        return controller.getSize(playListUUID);
    }

    public void removeListDataListener(ListDataListener listener)
    {
        listeners.remove(listener);
    }

    public void remove(int index, boolean stop)
    {
        controller.removeItem(playListUUID, index, stop);
    }

    public void add(int index, String filePath)
    {
        controller.addItem(playListUUID, index, filePath);
    }

    public void add(String filePath)
    {
        controller.addItem(playListUUID, getSize(), filePath);
    }

    public void addAll(int index, List<String> filePathList)
    {
        int i = 0;
        for(Iterator<String> iterator = filePathList.iterator(); iterator.hasNext();)
        {
            String filePath = (String)iterator.next();
            add(index + i, filePath);
            i++;
        }

    }

    public void addAll(List<String> filePathList)
    {
        String filePath;
        for(Iterator<String> iterator = filePathList.iterator(); iterator.hasNext(); add(filePath))
            filePath = (String)iterator.next();

    }

    public void play(int index)
    {
        playIndexAction.execute(Integer.valueOf(index));
    }

    public void play()
    {
        playAction.execute(null);
    }

    private void notifyListeners()
    {
        ListDataListener listener;
        for(Iterator<ListDataListener> iterator = listeners.iterator(); iterator.hasNext(); listener.contentsChanged(null))
            listener = (ListDataListener)iterator.next();

    }

    public void playListChanged(UUID playListUUID)
    {
        if(playListUUID.equals(this.playListUUID))
            notifyListeners();
    }

    public int getIndexFromUuid(UUID itemUUID)
    {
        return controller.getItemIndex(playListUUID, itemUUID);
    }

    public void stop()
    {
        stopAction.execute(null);
    }

    public UUID getUUID()
    {
        return playListUUID;
    }

    public void importItems(UUID sourcePlayListUUID, List<UUID> itemUUIDs, int targetIndex, boolean copy)
    {
        controller.importItems(sourcePlayListUUID, itemUUIDs, playListUUID, targetIndex, copy);
    }

    public boolean isRepeatList()
    {
        return controller.isRepeatList(playListUUID);
    }

    public void setRepeatList(boolean repeat)
    {
        repeatListAction.execute(Boolean.valueOf(repeat));
    }

    public boolean isRandomizeList()
    {
        return controller.isRandomizeList(playListUUID);
    }

    public void setRandomizeList(boolean randomize)
    {
        randomizeListAction.execute(Boolean.valueOf(randomize));
    }

    public void setChainWith(UUID source, UUID target)
    {
        controller.getItemChainWithAction(playListUUID, source).execute(target);
    }

    public void setRandomizeVolumeFrom(float value)
    {
        randomizeVolumeFromAction.execute(Float.valueOf(value));
    }

    public void setRandomizeVolumeTo(float value)
    {
        randomizeVolumeToAction.execute(Float.valueOf(value));
    }

    public void setStopAfterEachSound(boolean selected)
    {
        stopAfterEachSoundAction.execute(Boolean.valueOf(selected));
    }

    public void setFadeIn(int value)
    {
        fadeInAction.execute(Integer.valueOf(value));
    }

    public void setFadeOut(int value)
    {
        fadeOutAction.execute(Integer.valueOf(value));
    }

    public void setOverlap(int value)
    {
        overlapAction.execute(Integer.valueOf(value));
    }

    public boolean isStopAfterEachSound()
    {
        return controller.isStopAfterEachSound(playListUUID);
    }

    public float getRandomizeVolumeFrom()
    {
        return controller.getRandomizeVolumeFrom(playListUUID);
    }

    public float getRandomizeVolumeTo()
    {
        return controller.getRandomizeVolumeTo(playListUUID);
    }

    public int getFadeIn()
    {
        return controller.getFadeIn(playListUUID);
    }

    public int getFadeOut()
    {
        return controller.getFadeOut(playListUUID);
    }

    public int getOverlap()
    {
        return controller.getOverlap(playListUUID);
    }

    public void setVolume(float volume)
    {
        volumeAction.execute(Float.valueOf(volume));
    }

    public float getVolume()
    {
        return controller.getVolume(playListUUID);
    }
}
