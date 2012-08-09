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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import smartsound.common.Tuple;
import smartsound.view.Action;

public class HotkeySettings extends JDialog implements ItemListener {

	private final IGUILadder parent;
	private final HotkeyTableModel tableModel;
	private final SceneComboBoxModel comboBoxModel;

	public HotkeySettings(final IGUILadder parent, final JComponent parentComponent) {
		super(SwingUtilities.getWindowAncestor(parentComponent), "Manage Hotkeys");
		Window wnd = SwingUtilities.getWindowAncestor(parentComponent);
		setModal(true);

		JPanel panel = new JPanel(new GridBagLayout());
		add(panel);
		this.parent = parent;

		setResizable(false);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 0.1f;
		gbc.weighty = 0.0f;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_START;

		JPanel borderPanel = new JPanel(new BorderLayout());
		borderPanel.setBorder(BorderFactory.createTitledBorder("Scene"));

		comboBoxModel = new SceneComboBoxModel();
		JComboBox<String> comboBox = new JComboBox<String>(comboBoxModel);
		borderPanel.add(comboBox);

		panel.add(borderPanel, gbc);

		borderPanel = new JPanel(new GridLayout(0,1));
		borderPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

		tableModel = new HotkeyTableModel();
		final JTable table = new JTable(tableModel);
		table.getTableHeader().setReorderingAllowed(false);
		table.setFillsViewportHeight(true);

		JButton removeButton = new JButton(new AbstractAction("Remove") {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				Action action;
				if (table.getSelectedRow() > -1) {
					action = tableModel.getActionAtRow(table.getSelectedRow());
					getGUIController().removeHotkey(action);
					tableModel.update();
					repaint();
				}
			}
		});
		borderPanel.add(removeButton, gbc);
		borderPanel.add(new JButton("Export as PDF"), gbc);
		borderPanel.add(new JButton("Print"), gbc);

		gbc.gridy = 1;
		panel.add(borderPanel, gbc);

		JPanel helpPanel = new JPanel(new BorderLayout());
		helpPanel.setMinimumSize(new Dimension(150, 300));
		helpPanel.setBorder(BorderFactory.createTitledBorder("Help"));
		gbc.gridy = 2;
		gbc.fill =  GridBagConstraints.BOTH;

		JLabel helpText = new JLabel("<html><body>Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum</body></html>");
		helpPanel.add(helpText);
		panel.add(helpPanel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 0.0;
		gbc.gridheight = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.BOTH;


		JPanel tablePanel = new JPanel(new BorderLayout());
		JScrollPane tableScrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		tableScrollPane.setMinimumSize(new Dimension(500,100));

		tablePanel.setBorder(BorderFactory.createTitledBorder("Hotkeys"));
		tablePanel.add(tableScrollPane);
		panel.add(tablePanel, gbc);

		comboBox.addItemListener(this);
		comboBox.setSelectedIndex(0);

		setSize(getMinimumSize().width, getMinimumSize().height + 30);
		setResizable(false);
		setLocation(wnd.getX() + wnd.getWidth() / 2 - getWidth() / 2, wnd.getY() + wnd.getHeight() / 2 - getHeight() / 2);
		setVisible(true);
	}

	private GUIController getGUIController() {
		return parent.getGUIController();
	}

	private KeyStroke getKeyStroke(final String hotkeyString) {
		String[] split = hotkeyString.split("\\|");
		return KeyStroke.getKeyStroke(Integer.parseInt(split[1]),Integer.parseInt(split[0]));
	}

	protected class SceneComboBoxModel extends DefaultComboBoxModel<String> {

		List<UUID> sceneList = new LinkedList<UUID>();
		Map<UUID, String> titleMap = new HashMap<UUID, String>();
		Map<String, UUID> invertedTitleMap = new HashMap<String, UUID>();

		public SceneComboBoxModel() {
			sceneList.add(null);
			sceneList.addAll(getGUIController().getPlayListSetUUIDs(null));

			for (UUID uuid : sceneList) {
				titleMap.put(uuid, uuid == null ? "GLOBAL" : getGUIController().getTitle(uuid));
				invertedTitleMap.put(uuid == null ? "GLOBAL" : getGUIController().getTitle(uuid), uuid);
			}
		}

		@Override
		public String getElementAt(final int index) {
			return titleMap.get(sceneList.get(index));
		}

		@Override
		public int getSize() {
			return sceneList.size();
		}

		public UUID getCurrentUUID() {
			return invertedTitleMap.get(getSelectedItem());
		}

	}

	protected class HotkeyTableModel extends AbstractTableModel {

		private final Map<UUID, List<Tuple<String, Action>>> sceneMap =
				new HashMap<UUID, List<Tuple<String,Action>>>();
		private final Map<Action, String> commentMap = new HashMap<Action, String>();

		private UUID currentScene = null;

		public HotkeyTableModel() {
			update();
		}

		public void update() {
			GUIController c = parent.getGUIController();

			List<UUID> sceneList = c.getPlayListSetUUIDs(null);
			sceneMap.clear();
			sceneMap.put(null, c.getHotkeys((UUID) null));
			for (UUID uuid : sceneList) {
				sceneMap.put(uuid, c.getHotkeys(uuid));
			}

			commentMap.clear();
			for (Tuple<Action, String> tuple : getGUIController().getHotkeyComments()) {
				commentMap.put(tuple.first, tuple.second);
			}
			repaint();
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public String getColumnName(final int index) {
			switch(index) {
			case 0:
				return "Hotkey";
			case 1:
				return "Action";
			case 2:
				return "Comment";
			}
			return null;
		}

		@Override
		public int getRowCount() {
			return sceneMap.get(currentScene).size();
		}

		@Override
		public Object getValueAt(final int rowIndex, final int columnIndex) {
			Tuple<String, Action> tuple = sceneMap.get(currentScene).get(rowIndex);

			switch (columnIndex) {
			case 0:
				return getKeyStroke(tuple.first).toString();
			case 1:
				return tuple.second.getDescription();
			case 2:
				return commentMap.get(tuple.second);
			}
			return "";
		}

		@Override
		public void setValueAt(final Object obj, final int rowIndex, final int columnIndex) {
			System.out.println("Value set");
			if (columnIndex != 2)
				return;

			Action action = getActionAtRow(rowIndex);
			getGUIController().setHotkeyComment(action, obj.toString());
			update();
		}

		public Action getActionAtRow(final int rowIndex) {
			return sceneMap.get(currentScene).get(rowIndex).second;
		}

		public void setCurrentScene(final UUID uuid) {
			currentScene = uuid;
			repaint();
		}

		@Override
		public boolean isCellEditable(final int row, final int col) {
			return col == 2;
		}

	}

	@Override
	public void itemStateChanged(final ItemEvent event) {
		tableModel.setCurrentScene(comboBoxModel.getCurrentUUID());
	}
}
