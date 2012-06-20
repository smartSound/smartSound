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

import java.lang.reflect.Method;
import java.util.*;

import smartsound.controller.AbstractController;
import smartsound.player.IPlayListObserver;
import smartsound.player.ItemData;
import smartsound.view.gui.GUIController;



public class ViewController extends AbstractViewController
{

    public ViewController(AbstractController controller)
    {
        guis = new LinkedList<GUIController>();
        hotkeyMap = new HashMap<String, Action>();
        this.controller = controller;
    }

    public void addGUI(GUIController gui)
    {
        guis.add(gui);
    }

    public void addObserver(IPlayListObserver observer, UUID playListUUID)
    {
        controller.addObserver(observer, playListUUID);
    }

    public ItemData getItemData(UUID playListUUID, int index)
    {
        return controller.getItemData(playListUUID, index);
    }

    public int getSize(UUID playListUUID)
    {
        return controller.getSize(playListUUID);
    }

    public void addItem(UUID playListUUID, int index, String filePath)
    {
        controller.addItem(playListUUID, index, filePath);
    }

    public void addItem(UUID playListUUID, String filePath)
    {
        controller.addItem(playListUUID, filePath);
    }

    public void removeItem(UUID playListUUID, int index, boolean stop)
    {
        controller.removeItem(playListUUID, index, stop);
    }

    public int getItemIndex(UUID playListUUID, UUID itemUUID)
    {
        return controller.getItemIndex(playListUUID, itemUUID);
    }

    public UUID addPlayList()
    {
        UUID uuid = controller.addPlayList();
        hotkeyMap.put(" A", getPlayAction(uuid));
        return uuid;
    }

    public void deletePlayList(UUID playListUUID)
    {
        controller.deletePlayList(playListUUID);
    }

    public void importItems(UUID sourcePlayListUUID, List<UUID> itemUUIDs, UUID playListUUID, int targetIndex, boolean copy)
    {
        controller.importItems(sourcePlayListUUID, itemUUIDs, playListUUID, targetIndex, copy);
    }

    public boolean itemIsActive(UUID playListUUID, UUID itemUUID)
    {
        return controller.itemIsActive(playListUUID, itemUUID);
    }

    public String getItemName(UUID playListUUID, UUID itemUUID)
    {
        return controller.getItemName(playListUUID, itemUUID);
    }

    public UUID getItemChainWith(UUID playListUUID, UUID itemUUID)
    {
        return controller.getItemChainWith(playListUUID, itemUUID);
    }

    public Action getItemChainWithAction(UUID playListUUID, UUID itemUUID)
    {
        Method method;
        try
        {
            method = ViewController.class.getMethod("setItemChainWith", new Class[] {
                UUID.class, UUID.class, UUID.class
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
        Object params[] = {
            playListUUID, itemUUID
        };
        return new Action(method, this, params);
    }

    public void setItemChainWith(UUID playListUUID, UUID source, UUID target)
    {
        controller.setItemChainWith(playListUUID, source, target);
    }

    public boolean itemIsRepeating(UUID playListUUID, UUID itemUUID)
    {
        return controller.itemIsRepeating(playListUUID, itemUUID);
    }

    public Action getItemIsRepeatingAction(UUID playListUUID, UUID itemUUID)
    {
        Method method;
        try
        {
            method = ViewController.class.getMethod("setItemIsRepeating", new Class[] {
                UUID.class, UUID.class, boolean.class
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
        Object params[] = {
            playListUUID, itemUUID
        };
        return new Action(method, this, params);
    }

    public void setItemIsRepeating(UUID playListUUID, UUID itemUUID, boolean repeating)
    {
        controller.setItemIsRepeating(playListUUID, itemUUID, repeating);
    }

    public boolean isRepeatList(UUID playListUUID)
    {
        return controller.isRepeatList(playListUUID);
    }

    public Action getRepeatListAction(UUID playListUUID)
    {
        Method method;
        try
        {
            method =ViewController.class.getMethod("setRepeatList", new Class[] {
                UUID.class, boolean.class
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
        Object params[] = {
            playListUUID
        };
        return new Action(method, this, params);
    }

    public void setRepeatList(UUID playListUUID, boolean repeatList)
    {
        controller.setRepeatList(playListUUID, repeatList);
    }

    public boolean isRandomizeList(UUID playListUUID)
    {
        return controller.isRandomizeList(playListUUID);
    }

    public boolean isStopAfterEachSound(UUID playListUUID)
    {
        return controller.isStopAfterEachSound(playListUUID);
    }

    public float getRandomizeVolumeFrom(UUID playListUUID)
    {
        return controller.getRandomizeVolumeFrom(playListUUID);
    }

    public float getRandomizeVolumeTo(UUID playListUUID)
    {
        return controller.getRandomizeVolumeTo(playListUUID);
    }

    public int getFadeIn(UUID playListUUID)
    {
        return controller.getFadeIn(playListUUID);
    }

    public int getFadeOut(UUID playListUUID)
    {
        return controller.getFadeOut(playListUUID);
    }

    public int getOverlap(UUID playListUUID)
    {
        return controller.getOverlap(playListUUID);
    }

    public float getVolume(UUID playListUUID)
    {
        return controller.getVolume(playListUUID);
    }

    public Action getPlayAction(UUID playListUUID, int index)
    {
        Method method;
        try
        {
            method = ViewController.class.getMethod("play", new Class[] {
                UUID.class, int.class
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
        Object params[] = {
            playListUUID, Integer.valueOf(index)
        };
        return new Action(method, this, params);
    }

    public void play(UUID playListUUID, int index)
    {
        controller.play(playListUUID, index);
    }

    public Action getPlayAction(UUID playListUUID)
    {
        Method method;
        try
        {
            method = ViewController.class.getMethod("play", new Class[] {
                UUID.class
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
        Object params[] = {
            playListUUID
        };
        return new Action(method, this, params);
    }

    public void play(UUID playListUUID)
    {
        controller.play(playListUUID);
    }

    public Action getRandomizeListAction(UUID playListUUID)
    {
        Method method;
        try
        {
            method = ViewController.class.getMethod("setRandomizeList", new Class[] {
                UUID.class, boolean.class
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
        Object params[] = {
            playListUUID
        };
        return new Action(method, this, params);
    }

    public void setRandomizeList(UUID playListUUID, boolean randomize)
    {
        controller.setRandomizeList(playListUUID, randomize);
    }

    public Action getRandomizeVolumeFromAction(UUID playListUUID)
    {
        Method method;
        try
        {
            method = ViewController.class.getMethod("setRandomizeVolumeFrom", new Class[] {
                UUID.class, float.class
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
        Object params[] = {
            playListUUID
        };
        return new Action(method, this, params);
    }

    public void setRandomizeVolumeFrom(UUID playListUUID, float value)
    {
        controller.setRandomizeVolumeFrom(playListUUID, value);
    }

    public Action getRandomizeVolumeToAction(UUID playListUUID)
    {
        Method method;
        try
        {
            method = ViewController.class.getMethod("setRandomizeVolumeTo", new Class[] {
                UUID.class, float.class
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
        Object params[] = {
            playListUUID
        };
        return new Action(method, this, params);
    }

    public void setRandomizeVolumeTo(UUID playListUUID, float value)
    {
        controller.setRandomizeVolumeTo(playListUUID, value);
    }

    public Action getStopAfterEachSoundAction(UUID playListUUID)
    {
        Method method;
        try
        {
            method = ViewController.class.getMethod("setStopAfterEachSound", new Class[] {
                UUID.class, boolean.class
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
        Object params[] = {
            playListUUID
        };
        return new Action(method, this, params);
    }

    public void setStopAfterEachSound(UUID playListUUID, boolean stopAfterEachSound)
    {
        controller.setStopAfterEachSound(playListUUID, stopAfterEachSound);
    }

    public Action getFadeInAction(UUID playListUUID)
    {
        Method method;
        try
        {
            method = ViewController.class.getMethod("setFadeIn", new Class[] {
                UUID.class, int.class
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
        Object params[] = {
            playListUUID
        };
        return new Action(method, this, params);
    }

    public void setFadeIn(UUID playListUUID, int value)
    {
        controller.setFadeIn(playListUUID, value);
    }

    public Action getFadeOutAction(UUID playListUUID)
    {
        Method method;
        try
        {
            method = ViewController.class.getMethod("setFadeOut", new Class[] {
                UUID.class, int.class
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
        Object params[] = {
            playListUUID
        };
        return new Action(method, this, params);
    }

    public void setFadeOut(UUID playListUUID, int value)
    {
        controller.setFadeOut(playListUUID, value);
    }

    public Action getOverlapAction(UUID playListUUID)
    {
        Method method;
        try
        {
            method = ViewController.class.getMethod("setOverlap", new Class[] {
                UUID.class, int.class
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
        Object params[] = {
            playListUUID
        };
        return new Action(method, this, params);
    }

    public void setOverlap(UUID playListUUID, int value)
    {
        controller.setOverlap(playListUUID, value);
    }

    public Action getVolumeAction(UUID playListUUID)
    {
        Method method;
        try
        {
            method = ViewController.class.getMethod("setVolume", new Class[] {
                UUID.class, float.class
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
        Object params[] = {
            playListUUID
        };
        return new Action(method, this, params);
    }

    public void setVolume(UUID playListUUID, float value)
    {
        controller.setVolume(playListUUID, value);
    }

    public Action getSaveAction()
    {
        Method method;
        try
        {
            method = ViewController.class.getMethod("save", new Class[] {
                String.class
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
        Object params[] = new Object[0];
        return new Action(method, this, params);
    }

    public void save(String filePath)
    {
        controller.save(filePath);
    }

    public Action getLoadAction()
    {
        Method method;
        try
        {
            method = ViewController.class.getMethod("load", new Class[] {
                String.class
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
        Object params[] = new Object[0];
        return new Action(method, this, params);
    }

    public void load(String filePath)
    {
        controller.load(filePath);
    }

    public Action getStopAction(UUID playListUUID)
    {
        Method method;
        try
        {
            method = ViewController.class.getMethod("stop", new Class[] {
                UUID.class
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
        Object params[] = {
            playListUUID
        };
        return new Action(method, this, params);
    }

    public void stop(UUID playListUUID)
    {
        controller.stop(playListUUID);
    }

    public Action getPlayIndexAction(UUID playListUUID)
    {
        Method method;
        try
        {
            method = ViewController.class.getMethod("play", new Class[] {
                UUID.class, int.class
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
        Object params[] = {
            playListUUID
        };
        return new Action(method, this, params);
    }

    public Action getPlayAction(UUID playListUUID, UUID itemUUID)
    {
        Method method;
        try
        {
            method = ViewController.class.getMethod("play", new Class[] {
                UUID.class, UUID.class
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
        Object params[] = {
            playListUUID, itemUUID
        };
        return new Action(method, this, params);
    }

    public void play(UUID playListUUID, UUID itemUUID)
    {
        controller.play(playListUUID, controller.getItemIndex(playListUUID, itemUUID));
    }

    public List<UUID> getPlayListUUIDs()
    {
        return controller.getPlayListUUIDs();
    }

    public void setHotkey(String hotkey, Action action)
    {
        hotkeyMap.put(hotkey, action);
    }

    public void executeHotkey(String hotkey)
    {
        if(hotkeyMap.containsKey(hotkey))
            ((Action)hotkeyMap.get(hotkey)).execute(null);
    }

    public void removePlayList(UUID playListUUID)
    {
        GUIController gui;
        for(Iterator<GUIController> iterator = guis.iterator(); iterator.hasNext(); gui.removePlayList(playListUUID))
            gui = (GUIController)iterator.next();

    }

    public void newPlayList(UUID playListUUID)
    {
        GUIController gui;
        for(Iterator<GUIController> iterator = guis.iterator(); iterator.hasNext(); gui.newPlayList(playListUUID))
            gui = (GUIController)iterator.next();

    }

    private AbstractController controller;
    private List<GUIController> guis;
    private Map<String, Action> hotkeyMap;
}
