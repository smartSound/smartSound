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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import smartsound.common.IObserver;
import smartsound.common.Tuple;
import smartsound.player.ItemData;
import smartsound.settings.Global;
import smartsound.view.AbstractViewController;
import smartsound.view.AbstractViewController.PositionType;
import smartsound.view.Action;
import smartsound.view.ILayoutObserver;
import smartsound.view.Layout;

public class GUIController implements IGUILadder {

	private final AbstractViewController viewController;
	private final JPanel mainPanel;
	private TabbedPane tabs;
	private final Map<UUID, JComponent> componentMap = new HashMap<UUID, JComponent>();
	protected final JFileChooser fileChooser;

	private final MainFrame frame;

	public GUIController(final AbstractViewController controller) {
		viewController = controller;
		frame = new MainFrame(this);
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File("images/Note.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		frame.setIconImage(image);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle(" smartSound v0.4");
		frame.setLocation(397, 47);
		frame.setSize(1024, 768);
		frame.setVisible(true);
		frame.setBackground(new Color(0xEE, 0xE8, 0xAA));

		mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;

		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0f;
		c.weighty = 0.0f;
		mainPanel.add(new Toolbar(this), c);
		frame.getContentPane().add(mainPanel);

		fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(true);

		resetTabs();
	}

	public void addLayoutObserver(final ILayoutObserver observer, final UUID observableUUID) {
		viewController.addLayoutObserver(observer, observableUUID);
	}

	public void removeLayoutObserver(final ILayoutObserver observer, final UUID observableUUID) {
		viewController.removeLayoutObserver(observer, observableUUID);
	}

	private void resetTabs() {
		if (tabs != null)
			mainPanel.remove(tabs);
		tabs = new TabbedPane(frame);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;

		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.5;
		c.weighty = 0.5;

		mainPanel.add(tabs, c);

		tabs.update((UUID) null);
		mainPanel.revalidate();
	}

	public void addObserver(final IObserver observer, final UUID observableUUID) {
		viewController.addObserver(observer, observableUUID);
	}

	public ItemData getItemData(final UUID playListUUID, final int index) {
		return viewController.getItemData(playListUUID, index);
	}

	public int getSize(final UUID playListUUID) {
		return viewController.getSize(playListUUID);
	}

	public void removeItem(final UUID playListUUID, final int index, final boolean stop) {
		viewController.removeItem(playListUUID, index, stop);
	}

	public void remove(final UUID uuid) {
		viewController.remove(uuid);
	}

	public void addItem(final UUID playListUUID, final int index, final String filePath) {
		viewController.addItem(playListUUID, index, filePath);
	}

	public int getItemIndex(final UUID playListUUID, final UUID itemUUID) {
		return viewController.getItemIndex(playListUUID, itemUUID);
	}

	public void importItems(final UUID sourcePlayListUUID, final List<UUID> itemUUIDs,
			final UUID playListUUID, final int targetIndex, final boolean copy) {
		viewController.importItems(sourcePlayListUUID, itemUUIDs, playListUUID,
				targetIndex, copy);
	}

	public boolean isRepeatList(final UUID playListUUID) {
		return viewController.isRepeatList(playListUUID);
	}

	public boolean isRandomizeList(final UUID playListUUID) {
		return viewController.isRandomizeList(playListUUID);
	}

	public boolean isStopAfterEachSound(final UUID playListUUID) {
		return viewController.isStopAfterEachSound(playListUUID);
	}

	public float getRandomizeVolumeFrom(final UUID playListUUID) {
		return viewController.getRandomizeVolumeFrom(playListUUID);
	}

	public int getFadeIn(final UUID playListUUID) {
		return viewController.getFadeIn(playListUUID);
	}

	public int getOverlap(final UUID playListUUID) {
		return viewController.getOverlap(playListUUID);
	}

	public float getVolume(final UUID playListUUID) {
		return viewController.getVolume(playListUUID);
	}

	public Action getPlayAction(final UUID playListUUID, final UUID itemUUID, final String description) {
		return viewController.getPlayAction(playListUUID, itemUUID, description);
	}

	public Action getPlayIndexAction(final UUID playListUUID, final String description) {
		return viewController.getPlayIndexAction(playListUUID, description);
	}

	public Action getItemChainWithAction(final UUID playListUUID, final UUID source, final String description) {
		return viewController.getItemChainWithAction(playListUUID, source, description);
	}

	public Action getPlayAction(final UUID playListUUID, final int index, final String description) {
		return viewController.getPlayAction(playListUUID, index, description);
	}

	public Action getPlayPlayListAction(final UUID playListUUID, final String description) {
		return viewController.getPlayAction(playListUUID, description);
	}

	public Action getPlayItemAction(final UUID playListUUID, final String description) {
		return viewController.getPlayItemAction(playListUUID, description);
	}

	public Action getRandomizeListAction(final UUID playListUUID, final String description) {
		return viewController.getRandomizeListAction(playListUUID, description);
	}

	public Action getRandomizeVolumeFromAction(final UUID playListUUID, final String description) {
		return viewController.getRandomizeVolumeFromAction(playListUUID, description);
	}

	public Action getRandomizeVolumeToAction(final UUID playListUUID, final String description) {
		return viewController.getRandomizeVolumeToAction(playListUUID, description);
	}

	public Action getStopAfterEachSoundAction(final UUID playListUUID, final String description) {
		return viewController.getStopAfterEachSoundAction(playListUUID, description);
	}

	public Action getFadeInAction(final UUID playListUUID, final String description) {
		return viewController.getFadeInAction(playListUUID, description);
	}

	public Action getFadeOutAction(final UUID playListUUID, final String description) {
		return viewController.getFadeOutAction(playListUUID, description);
	}

	public Action getOverlapAction(final UUID playListUUID, final String description) {
		return viewController.getOverlapAction(playListUUID, description);
	}

	public Action getVolumeAction(final UUID playListUUID, final String description) {
		return viewController.getVolumeAction(playListUUID, description);
	}

	public Action getSaveAction() {
		return viewController.getSaveAction();
	}

	public Action getLoadAction() {
		return viewController.getLoadAction();
	}

	public Action getStopAction(final UUID playListUUID, final String description) {
		return viewController.getStopAction(playListUUID, description);
	}

	public Action getRepeatListAction(final UUID playListUUID, final String description) {
		return viewController.getRepeatListAction(playListUUID, description);
	}

	public float getRandomizeVolumeTo(final UUID playListUUID) {
		return viewController.getRandomizeVolumeTo(playListUUID);
	}

	public int getFadeOut(final UUID playListUUID) {
		return viewController.getFadeOut(playListUUID);
	}

	public void executeHotkey(final KeyEvent event) {
		KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(event);
		String hotkey = keyStroke.getModifiers() + "|" + keyStroke.getKeyCode();
		viewController.executeHotkey(hotkey);
	}

	public void setHotkey(final UUID playListSet, final KeyEvent keyEvent, final Action action) {
		KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(keyEvent);
		String hotkey = keyStroke.getModifiers() + "|" + keyStroke.getKeyCode();
		viewController.setHotkey(playListSet, hotkey, action);
	}

	public void removePlayList(final UUID playListUUID) {
	}

	public List<UUID> getPlayListUUIDs(final UUID parent) {
		return viewController.getPlayListUUIDs(parent);
	}

	public List<UUID> getPlayListSetUUIDs(final UUID parent) {
		return viewController.getPlayListSetUUIDs(parent);
	}

	public void setActive(final UUID playListSetUUID) {
		viewController.setActive(playListSetUUID);
	}

	public void shiftElement(final UUID uuid, final int x, final int y,
			final PositionType alignment) {
		viewController.shiftElement(uuid, x, y, alignment);
	}

	@Override
	public GUIController getGUIController() {
		return this;
	}

	@Override
	public void propagateHotkey(final KeyEvent event) {
		executeHotkey(event);
	}

	@Override
	public void propagatePopupMenu(final JPopupMenu menu, final MouseEvent e) {
		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	public List<Tuple<String,Action>> getHotkeys() {
		return viewController.getHotkeys(null, null);
	}

	public List<Tuple<String,Action>> getHotkeys(final Action parent) {
		return viewController.getHotkeys(null, parent);
	}

	public List<Tuple<String,Action>> getHotkeys(final UUID sceneUUID, final Action parent) {
		return viewController.getHotkeys(sceneUUID, parent);
	}

	public List<Tuple<String,Action>> getHotkeys(final UUID sceneUUID) {
		return viewController.getHotkeys(sceneUUID);
	}

	public String getHotkey(final Action action) {
		return viewController.getHotkey(action);
	}

	public void removeHotkey(final Action action) {
		viewController.removeHotkey(action);
	}

	protected class ResetPluginAction extends AbstractAction
	{

		@Override
		public void actionPerformed(final ActionEvent arg0)
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

	public Action getSetRepeatItemAction(final UUID uuid, final String description) {
		return viewController.getSetRepeatItemAction(uuid, description);
	}

	public Action getSetItemChainWithAction(final UUID uuid, final String description) {
		return viewController.getSetItemChainWithAction(uuid, description);
	}

	public Layout getLayout(final UUID playListSetUUID) {
		return viewController.getLayout(playListSetUUID);
	}

	public UUID addPlayListSet(final UUID parentSetUUID) {
		return viewController.addPlayListSet(parentSetUUID);
	}

	public UUID addPlayList(final UUID parentSetUUID) {
		return viewController.addPlayList(parentSetUUID);
	}


	public void reload() {
		componentMap.clear();
		resetTabs();
	}

	public void setTitle(final UUID uuid, final String newTitle) {
		viewController.setTitle(uuid, newTitle);
	}

	public String getTitle(final UUID uuid) {
		return viewController.getTitle(uuid);
	}

	public boolean isActive(final UUID elementUUID) {
		return viewController.isActive(elementUUID);
	}

	public boolean getAutoplay(final UUID elementUUID) {
		return viewController.getAutoplay(elementUUID);
	}

	public void setAutoplay(final UUID elementUUID, final boolean autoplay) {
		viewController.setAutoplay(elementUUID, autoplay);
	}

	public List<Tuple<Action, String>> getHotkeyComments() {
		return viewController.getHotkeyComments();
	}

	public void setHotkeyComment(final Action action, final String comment) {
		viewController.setHotkeyDescription(action, comment);
	}

	@Override
	public void updateMinimumSize() {
		// TODO Auto-generated method stub
	}

	public void addFiles(final UUID playListUUID) {
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int result = fileChooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION)
			for (File file : fileChooser.getSelectedFiles()) {
				addItem(playListUUID, -1, file.getAbsolutePath());
			}

	}

	public void addDirectory(final UUID playListUUID) {
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = fileChooser.showOpenDialog(null);
		List<String> filePaths = new LinkedList<String>();
		if (result == JFileChooser.APPROVE_OPTION)
			for (File file : fileChooser.getSelectedFiles())
				filePaths.addAll(directoryToFilePathList(file));
		for (String path : filePaths)
			addItem(playListUUID, -1, path);
	}

	private List<String> directoryToFilePathList(final File folder) {
		assert folder.isDirectory();

		List<String> returnList = new LinkedList<String>();

		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				returnList.addAll(directoryToFilePathList(file));
			} else {
				returnList.add(file.getAbsolutePath());
			}
		}

		return returnList;
	}
}
