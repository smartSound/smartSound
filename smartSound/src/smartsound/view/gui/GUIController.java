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

import java.awt.Component;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import smartsound.player.IPlayListObserver;
import smartsound.player.ItemData;
import smartsound.view.AbstractViewController;
import smartsound.view.Action;


public class GUIController
{

    public GUIController(AbstractViewController controller)
    {
        playListPanelMap = new HashMap<UUID, PlayListPanel>();
        viewController = controller;
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(3);
        frame.setTitle(" smartSound v0.2");
        frame.setSize(300, 400);
        frame.setLocation(397, 47);
        frame.setVisible(true);
        mainPanel = new JPanel(new GridLayout());
        frame.getContentPane().add(mainPanel);
        try
        {
            javax.swing.UIManager.LookAndFeelInfo alookandfeelinfo[];
            int j = (alookandfeelinfo = UIManager.getInstalledLookAndFeels()).length;
            for(int i = 0; i < j; i++)
            {
                javax.swing.UIManager.LookAndFeelInfo info = alookandfeelinfo[i];
                if(!"Nimbus".equals(info.getName()))
                    continue;
                UIManager.setLookAndFeel(info.getClassName());
                break;
            }

        }
        catch(Exception exception) { }
        UUID uuid;
        for(Iterator<UUID> iterator = viewController.getPlayListUUIDs().iterator(); iterator.hasNext(); newPlayList(uuid))
            uuid = (UUID)iterator.next();

    }

    public void addObserver(IPlayListObserver observer, UUID playListUUID)
    {
        viewController.addObserver(observer, playListUUID);
    }

    public ItemData getItemData(UUID playListUUID, int index)
    {
        return viewController.getItemData(playListUUID, index);
    }

    public int getSize(UUID playListUUID)
    {
        return viewController.getSize(playListUUID);
    }

    public void removeItem(UUID playListUUID, int index, boolean stop)
    {
        viewController.removeItem(playListUUID, index, stop);
    }

    public void addItem(UUID playListUUID, int index, String filePath)
    {
        viewController.addItem(playListUUID, index, filePath);
    }

    public int getItemIndex(UUID playListUUID, UUID itemUUID)
    {
        return viewController.getItemIndex(playListUUID, itemUUID);
    }

    public void importItems(UUID sourcePlayListUUID, List<UUID> itemUUIDs, UUID playListUUID, int targetIndex, boolean copy)
    {
        viewController.importItems(sourcePlayListUUID, itemUUIDs, playListUUID, targetIndex, copy);
    }

    public boolean isRepeatList(UUID playListUUID)
    {
        return viewController.isRepeatList(playListUUID);
    }

    public boolean isRandomizeList(UUID playListUUID)
    {
        return viewController.isRandomizeList(playListUUID);
    }

    public boolean isStopAfterEachSound(UUID playListUUID)
    {
        return viewController.isStopAfterEachSound(playListUUID);
    }

    public float getRandomizeVolumeFrom(UUID playListUUID)
    {
        return viewController.getRandomizeVolumeFrom(playListUUID);
    }

    public int getFadeIn(UUID playListUUID)
    {
        return viewController.getFadeIn(playListUUID);
    }

    public int getOverlap(UUID playListUUID)
    {
        return viewController.getOverlap(playListUUID);
    }

    public float getVolume(UUID playListUUID)
    {
        return viewController.getVolume(playListUUID);
    }

    public Action getPlayAction(UUID playListUUID, UUID itemUUID)
    {
        return viewController.getPlayAction(playListUUID, itemUUID);
    }

    public Action getPlayIndexAction(UUID playListUUID)
    {
        return viewController.getPlayIndexAction(playListUUID);
    }

    public Action getItemChainWithAction(UUID playListUUID, UUID source)
    {
        return viewController.getItemChainWithAction(playListUUID, source);
    }

    public Action getPlayAction(UUID playListUUID, int index)
    {
        return viewController.getPlayAction(playListUUID, index);
    }

    public Action getPlayAction(UUID playListUUID)
    {
        return viewController.getPlayAction(playListUUID);
    }

    public Action getRandomizeListAction(UUID playListUUID)
    {
        return viewController.getRandomizeListAction(playListUUID);
    }

    public Action getRandomizeVolumeFromAction(UUID playListUUID)
    {
        return viewController.getRandomizeVolumeFromAction(playListUUID);
    }

    public Action getRandomizeVolumeToAction(UUID playListUUID)
    {
        return viewController.getRandomizeVolumeToAction(playListUUID);
    }

    public Action getStopAfterEachSoundAction(UUID playListUUID)
    {
        return viewController.getStopAfterEachSoundAction(playListUUID);
    }

    public Action getFadeInAction(UUID playListUUID)
    {
        return viewController.getFadeInAction(playListUUID);
    }

    public Action getFadeOutAction(UUID playListUUID)
    {
        return viewController.getFadeOutAction(playListUUID);
    }

    public Action getOverlapAction(UUID playListUUID)
    {
        return viewController.getOverlapAction(playListUUID);
    }

    public Action getVolumeAction(UUID playListUUID)
    {
        return viewController.getVolumeAction(playListUUID);
    }

    public Action getSaveAction()
    {
        return viewController.getSaveAction();
    }

    public Action getLoadAction()
    {
        return viewController.getLoadAction();
    }

    public Action getStopAction(UUID playListUUID)
    {
        return viewController.getStopAction(playListUUID);
    }

    public Action getRepeatListAction(UUID playListUUID)
    {
        return viewController.getRepeatListAction(playListUUID);
    }

    public float getRandomizeVolumeTo(UUID playListUUID)
    {
        return viewController.getRandomizeVolumeTo(playListUUID);
    }

    public int getFadeOut(UUID playListUUID)
    {
        return viewController.getFadeOut(playListUUID);
    }

    public void executeHotkey(String hotkey)
    {
        viewController.executeHotkey(hotkey);
    }

    public void setHotkey(String hotkey, Action action)
    {
        viewController.setHotkey(hotkey, action);
    }

    public void removePlayList(UUID playListUUID)
    {
        mainPanel.remove((Component)playListPanelMap.get(playListUUID));
        mainPanel.revalidate();
    }

    public void newPlayList(UUID playListUUID)
    {
        PlayListPanel panel = new PlayListPanel(this, new PlayListDataModel(this, playListUUID));
        playListPanelMap.put(playListUUID, panel);
        mainPanel.add(panel);
        panel.setVisible(true);
        mainPanel.revalidate();
    }

    private AbstractViewController viewController;
    private Map<UUID, PlayListPanel> playListPanelMap;
    private JPanel mainPanel;
}
