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
	
    public PlayListDataModel(GUIController controller, UUID playListUUID)
    {
        listeners = new LinkedList<ListDataListener>();
        this.playListUUID = playListUUID;
        this.controller = controller;
        controller.addObserver(this, playListUUID);
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

    public boolean isRandomizeList()
    {
        return controller.isRandomizeList(playListUUID);
    }

    public void setChainWith(UUID source, UUID target)
    {
        controller.getItemChainWithAction(playListUUID, source).execute(target);
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

    public float getVolume()
    {
        return controller.getVolume(playListUUID);
    }
}
