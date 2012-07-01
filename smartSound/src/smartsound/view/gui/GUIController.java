/* 
 *	Copyright (C) 2012 Andr� Becker
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

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import smartsound.common.Tuple;
import smartsound.player.IPlayListObserver;
import smartsound.player.ItemData;
import smartsound.settings.Global;
import smartsound.view.AbstractViewController;
import smartsound.view.Action;

public class GUIController implements IGUILadder {

	private AbstractViewController viewController;
	private Map<UUID, PlayListPanel> playListPanelMap;
	private JPanel mainPanel;

	public GUIController(AbstractViewController controller) {
		playListPanelMap = new HashMap<UUID, PlayListPanel>();
		viewController = controller;
		final JFrame frame = new MainFrame(this);
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File("images/Note.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		frame.setIconImage(image);
		frame.setDefaultCloseOperation(3);
		frame.setTitle(" smartSound v0.3");
		frame.setSize(300, 400);
		frame.setLocation(397, 47);
		frame.setVisible(true);
		frame.setBackground(new Color(0xEE, 0xE8, 0xAA));
		
		mainPanel = new JPanel(new GridLayout());
		frame.getContentPane().add(mainPanel);

		UUID uuid;
		for (Iterator<UUID> iterator = viewController.getPlayListUUIDs()
				.iterator(); iterator.hasNext(); newPlayList(uuid))
			uuid = (UUID) iterator.next();
	}

	public void addObserver(IPlayListObserver observer, UUID playListUUID) {
		viewController.addObserver(observer, playListUUID);
	}

	public ItemData getItemData(UUID playListUUID, int index) {
		return viewController.getItemData(playListUUID, index);
	}

	public int getSize(UUID playListUUID) {
		return viewController.getSize(playListUUID);
	}

	public void removeItem(UUID playListUUID, int index, boolean stop) {
		viewController.removeItem(playListUUID, index, stop);
	}

	public void addItem(UUID playListUUID, int index, String filePath) {
		viewController.addItem(playListUUID, index, filePath);
	}

	public int getItemIndex(UUID playListUUID, UUID itemUUID) {
		return viewController.getItemIndex(playListUUID, itemUUID);
	}

	public void importItems(UUID sourcePlayListUUID, List<UUID> itemUUIDs,
			UUID playListUUID, int targetIndex, boolean copy) {
		viewController.importItems(sourcePlayListUUID, itemUUIDs, playListUUID,
				targetIndex, copy);
	}

	public boolean isRepeatList(UUID playListUUID) {
		return viewController.isRepeatList(playListUUID);
	}

	public boolean isRandomizeList(UUID playListUUID) {
		return viewController.isRandomizeList(playListUUID);
	}

	public boolean isStopAfterEachSound(UUID playListUUID) {
		return viewController.isStopAfterEachSound(playListUUID);
	}

	public float getRandomizeVolumeFrom(UUID playListUUID) {
		return viewController.getRandomizeVolumeFrom(playListUUID);
	}

	public int getFadeIn(UUID playListUUID) {
		return viewController.getFadeIn(playListUUID);
	}

	public int getOverlap(UUID playListUUID) {
		return viewController.getOverlap(playListUUID);
	}

	public float getVolume(UUID playListUUID) {
		return viewController.getVolume(playListUUID);
	}

	public Action getPlayAction(UUID playListUUID, UUID itemUUID) {
		return viewController.getPlayAction(playListUUID, itemUUID);
	}

	public Action getPlayIndexAction(UUID playListUUID) {
		return viewController.getPlayIndexAction(playListUUID);
	}

	public Action getItemChainWithAction(UUID playListUUID, UUID source) {
		return viewController.getItemChainWithAction(playListUUID, source);
	}

	public Action getPlayAction(UUID playListUUID, int index) {
		return viewController.getPlayAction(playListUUID, index);
	}

	public Action getPlayPlayListAction(UUID playListUUID) {
		return viewController.getPlayAction(playListUUID);
	}
	
	public Action getPlayItemAction(UUID playListUUID) {
		return viewController.getPlayItemAction(playListUUID);
	}

	public Action getRandomizeListAction(UUID playListUUID) {
		return viewController.getRandomizeListAction(playListUUID);
	}

	public Action getRandomizeVolumeFromAction(UUID playListUUID) {
		return viewController.getRandomizeVolumeFromAction(playListUUID);
	}

	public Action getRandomizeVolumeToAction(UUID playListUUID) {
		return viewController.getRandomizeVolumeToAction(playListUUID);
	}

	public Action getStopAfterEachSoundAction(UUID playListUUID) {
		return viewController.getStopAfterEachSoundAction(playListUUID);
	}

	public Action getFadeInAction(UUID playListUUID) {
		return viewController.getFadeInAction(playListUUID);
	}

	public Action getFadeOutAction(UUID playListUUID) {
		return viewController.getFadeOutAction(playListUUID);
	}

	public Action getOverlapAction(UUID playListUUID) {
		return viewController.getOverlapAction(playListUUID);
	}

	public Action getVolumeAction(UUID playListUUID) {
		return viewController.getVolumeAction(playListUUID);
	}

	public Action getSaveAction() {
		return viewController.getSaveAction();
	}

	public Action getLoadAction() {
		return viewController.getLoadAction();
	}

	public Action getStopAction(UUID playListUUID) {
		return viewController.getStopAction(playListUUID);
	}
	
	

	public Action getRepeatListAction(UUID playListUUID) {
		return viewController.getRepeatListAction(playListUUID);
	}

	public float getRandomizeVolumeTo(UUID playListUUID) {
		return viewController.getRandomizeVolumeTo(playListUUID);
	}

	public int getFadeOut(UUID playListUUID) {
		return viewController.getFadeOut(playListUUID);
	}

	public void executeHotkey(KeyEvent event) {
		String hotkey = event.getModifiers() + "|" + event.getKeyCode();
		viewController.executeHotkey(hotkey);
	}

	public void setHotkey(KeyEvent keyEvent, Action action) {
		KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(keyEvent);
		String hotkey = keyStroke.getModifiers() + "|" + keyStroke.getKeyCode();
		viewController.setHotkey(hotkey, action);
	}

	public void removePlayList(UUID playListUUID) {
		mainPanel.remove((Component) playListPanelMap.get(playListUUID));
		mainPanel.revalidate();
	}

	public void newPlayList(UUID playListUUID) {
		PlayListPanelBorder border = new PlayListPanelBorder(this,
				new PlayListDataModel(this, playListUUID), "Playlist");
		playListPanelMap.put(playListUUID, border.getPanel());
		mainPanel.add(border.getPanel());
		mainPanel.revalidate();
	}

	@Override
	public GUIController getGUIController() {
		return this;
	}

	@Override
	public void propagateHotkey(KeyEvent event) {
		executeHotkey(event);
	}

	@Override
	public void propagatePopupMenu(JPopupMenu menu, MouseEvent e) {
		if (menu.getSubElements().length != 0) {
			menu.addSeparator();
		}
		menu.add(new SaveAction());
		menu.add(new LoadAction());
		menu.addSeparator();
		menu.add(new ResetPluginAction());
		menu.show(e.getComponent(), e.getX(), e.getY());
	}
	
	public List<Tuple<String,Action>> getHotkeys(Action parent) {
		return viewController.getHotkeys(parent);
	}
	
	public String getHotkey(Action action) {
		return viewController.getHotkey(action);
	}
	
	public void removeHotkey(Action action) {
		viewController.removeHotkey(action);
	}
	
	protected class SaveAction extends AbstractAction
    {

        public void actionPerformed(ActionEvent e)
        {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showSaveDialog(null);
            if(result == 0)
                getSaveAction().execute(chooser.getSelectedFile().getAbsolutePath());
        }
        
        public SaveAction()
        {
            super("Save");
        }
    }
	
	protected class LoadAction extends AbstractAction
    {

        public void actionPerformed(ActionEvent e)
        {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(null);
            if(result == 0)
                getLoadAction().execute(chooser.getSelectedFile().getAbsolutePath());
        }
        
        public LoadAction()
        {
            super("Load");
        }
    }
	
	protected class ResetPluginAction extends AbstractAction
    {

        public void actionPerformed(ActionEvent arg0)
        {
            try
            {
                Global.getInstance().removeProperty("plugin");
                JOptionPane.showMessageDialog(null, "Plugin successfully resetted. The next time you start smartSound you can choose a different plugin.");
            }
            catch(IOException e)
            {
                JOptionPane.showMessageDialog(null, "Error while resetting the plugin");
            }
        }

        public ResetPluginAction()
        {
            super("Reset plugin");
        }
    }

	public Action getSetRepeatItemAction(UUID uuid) {
		return viewController.getSetRepeatItemAction(uuid);
	}

	public Action getSetItemChainWithAction(UUID uuid) {
		return viewController.getSetItemChainWithAction(uuid);
	}
}
