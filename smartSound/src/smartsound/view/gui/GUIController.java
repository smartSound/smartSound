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

import smartsound.common.IElement.NameValuePair;
import smartsound.common.IObserver;
import smartsound.common.Tuple;
import smartsound.settings.Global;
import smartsound.view.AbstractViewController;
import smartsound.view.Action;

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

		tabs = new TabbedPane(frame);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;

		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.5;
		c.weighty = 0.5;

		mainPanel.add(tabs, c);

		mainPanel.revalidate();
	}

	public void act(final UUID element, final String... actionTypes) {
		viewController.act(element, actionTypes);
	}

	public UUID add(final UUID parent, final String elementType, final Object... params) {
		return viewController.add(parent, elementType, params);
	}

	public void remove(final UUID uuid) {
		viewController.remove(uuid);
	}

	public NameValuePair[] get(final UUID uuid, final String... propertyNames) {
		return viewController.get(uuid, propertyNames);
	}

	public void set(final UUID uuid, final String name, final Object value) {
		NameValuePair[] pair = {NameValuePair.create(name, value)};
		set(uuid, pair);
	}

	public void set(final UUID uuid, final NameValuePair... params) {
		viewController.set(uuid, params);
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

		mainPanel.revalidate();
	}

	public void addObserver(final IObserver observer, final UUID observableUUID) {
		viewController.addObserver(observer, observableUUID);
	}

	public Action getSaveAction() {
		return viewController.getSaveAction();
	}

	public Action getLoadAction() {
		return viewController.getLoadAction();
	}

	public void executeHotkey(final KeyEvent event) {
		KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(event);
		String hotkey = keyStroke.getModifiers() + "|" + keyStroke.getKeyCode();
		viewController.executeHotkey(hotkey);
	}

	public void setHotkey(final UUID playListSet, final KeyEvent keyEvent, final Action action) {
		KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(keyEvent);
		String hotkey = keyStroke.getModifiers() + "|" + keyStroke.getKeyCode();
		//viewController.setHotkey(playListSet, hotkey, action);
	}

	public void addSetHotkey(final KeyEvent event, final UUID elementUUID, final Map<String, Object> values) {
		KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(event);
		String hotkey = keyStroke.getModifiers() + "|" + keyStroke.getKeyCode();
		viewController.addSetHotkey(hotkey, elementUUID, values);
	}

	public void addActHotkey(final KeyEvent event, final UUID elementUUID, final String... actions) {
		KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(event);
		String hotkey = keyStroke.getModifiers() + "|" + keyStroke.getKeyCode();
		viewController.addActHotkey(hotkey, elementUUID, actions);
	}

	public void removePlayList(final UUID playListUUID) {
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

	//	public void removeHotkey(final Action action) {
	//		viewController.removeHotkey(action);
	//	}

	public void removeHotkey(final UUID elementUUID, final String hotkey, final Action action) {
		viewController.removeHotkey(elementUUID, hotkey, action);
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

	public void reload() {
		componentMap.clear();
		resetTabs();
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
				add(playListUUID, "PLAYLISTITEM", file.getAbsolutePath(), -1);
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
			add(playListUUID, "PLAYLISTITEM", path, -1);
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
