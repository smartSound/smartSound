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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import smartsound.common.Tuple;
import smartsound.view.Action;


public class SettingsPanel extends javax.swing.JPanel implements ListDataListener, IGUILadder, MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6840626218594923611L;

	protected boolean editing = false;

	private final IGUILadder parent;
	private final UUID playListUUID;
	private final Action randomizeListAction;
	private final Action randomizeVolumeFromAction;
	private final Action randomizeVolumeToAction;
	private final Action stopAfterEachSoundAction;
	private final Action fadeInAction;
	private final Action fadeOutAction;
	private final Action overlapAction;


	private final JCheckBox randomizeCheckBox = new JCheckBox();
	private final JLabel randomizeLabel = new JLabel("Randomize playlist");

	private final JCheckBox stopAfterEachSoundCheckBox = new JCheckBox();
	private final JLabel stopAfterEachSoundLabel = new JLabel("Stop after each sound");

	private final JLabel playAtRandomVolumesLabel = new JLabel("Play at random volumes");
	protected JSlider playAtRandomVolumesSliderFrom = new JSlider(0,100);
	protected JLabel playAtRandomVolumesFromLabel = new JLabel(playAtRandomVolumesSliderFrom.getValue() + " %");
	protected JSlider playAtRandomVolumesSliderTo = new JSlider(0,100);
	protected JLabel playAtRandomVolumesToLabel = new JLabel(playAtRandomVolumesSliderTo.getValue() + " %");

	private final JLabel fadeInLabel = new JLabel("Fade In");
	private final JSpinner fadeInSpinner = new JSpinner(new SpinnerNumberModel(0,0,9.9,0.1));

	private final JLabel fadeOutLabel = new JLabel("Fade Out");
	private final JSpinner fadeOutSpinner = new JSpinner(new SpinnerNumberModel(0,0,9.9,0.1));

	private final JLabel overlapLabel = new JLabel("Overlap");
	private final JSpinner overlapSpinner = new JSpinner(new SpinnerNumberModel(0,0,9.9,0.1));

	public SettingsPanel(final IGUILadder parent, final PlayListDataModel playListDataModel) {
		super(new GridBagLayout());

		this.parent = parent;
		playListUUID = playListDataModel.getUUID();
		playListDataModel.addListDataListener(this);
		addMouseListener(this);


		randomizeListAction = parent.getGUIController().getRandomizeListAction(playListUUID, "Randomize for " + playListUUID);
		randomizeVolumeFromAction = parent.getGUIController().getRandomizeVolumeFromAction(playListUUID, "Randomize volume minimum for " + playListUUID);
		randomizeVolumeToAction = parent.getGUIController().getRandomizeVolumeToAction(playListUUID, "Randomize volume maximum for " + playListUUID);
		stopAfterEachSoundAction = parent.getGUIController().getStopAfterEachSoundAction(playListUUID, "Stop after each sound for " + playListUUID);
		fadeInAction = parent.getGUIController().getFadeInAction(playListUUID, "Set fade in for " + playListUUID);
		fadeOutAction = parent.getGUIController().getFadeOutAction(playListUUID, "Set fade out for " + playListUUID);
		overlapAction = parent.getGUIController().getOverlapAction(playListUUID, "Set overlapping for " + playListUUID);


		randomizeCheckBox.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent e) {
				JCheckBox source = (JCheckBox) e.getSource();

				if (!editing) randomizeListAction.execute(source.isSelected());
			}
		});

		randomizeCheckBox.setFocusable(false);
		addMouseListenerToComponent(randomizeCheckBox);

		stopAfterEachSoundCheckBox.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent e) {
				JCheckBox source = (JCheckBox) e.getSource();

				if (!editing) stopAfterEachSoundAction.execute(source.isSelected());
			}
		});

		stopAfterEachSoundCheckBox.setFocusable(false);
		addMouseListenerToComponent(stopAfterEachSoundCheckBox);

		playAtRandomVolumesSliderFrom.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				playAtRandomVolumesFromLabel.setText(source.getValue() + " %");

				if (source.getValue() > playAtRandomVolumesSliderTo.getValue()) {
					playAtRandomVolumesSliderTo.setValue(source.getValue());
				}
				if (!source.getValueIsAdjusting() && !editing) {
					randomizeVolumeFromAction.execute(source.getValue() / 100.0f);
				}
			}
		});

		playAtRandomVolumesSliderFrom.setFocusable(false);
		addMouseListenerToComponent(playAtRandomVolumesSliderFrom);

		playAtRandomVolumesSliderTo.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				playAtRandomVolumesToLabel.setText(source.getValue() + " %");

				if (source.getValue() < playAtRandomVolumesSliderFrom.getValue()) {
					playAtRandomVolumesSliderFrom.setValue(source.getValue());
				}
				if (!source.getValueIsAdjusting() && !editing) {
					randomizeVolumeToAction.execute(source.getValue() / 100.0f);
				}
			}
		});

		playAtRandomVolumesSliderTo.setFocusable(false);
		addMouseListenerToComponent(playAtRandomVolumesSliderTo);


		fadeInSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent e) {
				JSpinner source = (JSpinner) e.getSource();
				if (!editing) fadeInAction.execute((int) (((Number) source.getValue()).doubleValue()*1000));
			}
		});
		fadeInSpinner.setEditor(new JSpinner.NumberEditor(fadeInSpinner, "0.0 s"));
		fadeInSpinner.setPreferredSize(new Dimension(70,fadeInSpinner.getPreferredSize().height));
		fadeInSpinner.setFocusable(false);
		addMouseListenerToComponent(fadeInSpinner);


		fadeOutSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent e) {
				JSpinner source = (JSpinner) e.getSource();
				if (!editing) fadeOutAction.execute((int) (((Number) source.getValue()).doubleValue()*1000));
			}
		});
		fadeOutSpinner.setEditor(new JSpinner.NumberEditor(fadeOutSpinner, "0.0 s"));
		fadeOutSpinner.setPreferredSize(new Dimension(70,fadeOutSpinner.getPreferredSize().height));
		fadeOutSpinner.setFocusable(false);
		addMouseListenerToComponent(fadeOutSpinner);


		overlapSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent e) {
				JSpinner source = (JSpinner) e.getSource();
				if (!editing) overlapAction.execute((int) (((Number) source.getValue()).doubleValue()*1000));
			}
		});
		overlapSpinner.setEditor(new JSpinner.NumberEditor(overlapSpinner, "0.0 s"));
		overlapSpinner.setPreferredSize(new Dimension(70, overlapSpinner.getPreferredSize().height));
		overlapSpinner.setFocusable(false);
		addMouseListenerToComponent(overlapSpinner);


		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(0,5,0,0);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		add(randomizeCheckBox, constraints);

		constraints = new GridBagConstraints();
		constraints.insets = new Insets(0,5,0,0);
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.gridwidth = 2;
		add(randomizeLabel, constraints);

		constraints = new GridBagConstraints();
		constraints.insets = new Insets(15,5,0,0);
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.gridwidth = 2;
		add(playAtRandomVolumesLabel, constraints);

		constraints = new GridBagConstraints();
		constraints.insets = new Insets(0,15,0,0);
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.gridwidth = 1;
		playAtRandomVolumesSliderFrom.setMajorTickSpacing(20);
		playAtRandomVolumesSliderFrom.setMinorTickSpacing(5);
		playAtRandomVolumesSliderFrom.setPaintTicks(true);
		playAtRandomVolumesSliderFrom.setPreferredSize(new Dimension(130, playAtRandomVolumesSliderFrom.getPreferredSize().height));
		add(playAtRandomVolumesSliderFrom, constraints);

		constraints = new GridBagConstraints();
		constraints.insets = new Insets(0,10,0,0);
		constraints.gridx = 2;
		constraints.gridy = 2;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.gridwidth = 1;
		add(playAtRandomVolumesFromLabel, constraints);

		constraints = new GridBagConstraints();
		constraints.insets = new Insets(0,15,0,0);
		constraints.gridx = 1;
		constraints.gridy = 3;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.gridwidth = 1;
		playAtRandomVolumesSliderTo.setMajorTickSpacing(20);
		playAtRandomVolumesSliderTo.setMinorTickSpacing(5);
		playAtRandomVolumesSliderTo.setPaintTicks(true);
		playAtRandomVolumesSliderTo.setPreferredSize(new Dimension(130, playAtRandomVolumesSliderTo.getPreferredSize().height));
		add(playAtRandomVolumesSliderTo, constraints);

		constraints = new GridBagConstraints();
		constraints.insets = new Insets(0,10,0,0);
		constraints.gridx = 2;
		constraints.gridy = 3;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.gridwidth = 1;
		add(playAtRandomVolumesToLabel, constraints);

		constraints = new GridBagConstraints();
		constraints.insets = new Insets(15,5,0,0);
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.anchor = GridBagConstraints.LINE_START;
		add(stopAfterEachSoundCheckBox, constraints);

		constraints = new GridBagConstraints();
		constraints.insets = new Insets(15,5,0,0);
		constraints.gridx = 1;
		constraints.gridy = 4;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		add(stopAfterEachSoundLabel, constraints);

		constraints = new GridBagConstraints();
		constraints.insets = new Insets(15,5,0,0);
		constraints.gridx = 1;
		constraints.gridy = 5;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.gridwidth = 1;
		add(fadeInLabel, constraints);

		constraints = new GridBagConstraints();
		constraints.insets = new Insets(15,5,0,0);
		constraints.gridx = 2;
		constraints.gridy = 5;
		constraints.anchor = GridBagConstraints.LINE_START;
		add(fadeInSpinner, constraints);

		constraints = new GridBagConstraints();
		constraints.insets = new Insets(0,5,0,0);
		constraints.gridx = 1;
		constraints.gridy = 6;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.gridwidth = 1;
		add(fadeOutLabel, constraints);

		constraints = new GridBagConstraints();
		constraints.insets = new Insets(0,5,0,0);
		constraints.gridx = 2;
		constraints.gridy = 6;
		constraints.anchor = GridBagConstraints.LINE_START;
		add(fadeOutSpinner, constraints);

		constraints = new GridBagConstraints();
		constraints.insets = new Insets(0,5,0,0);
		constraints.gridx = 1;
		constraints.gridy = 7;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.gridwidth = 1;
		add(overlapLabel, constraints);

		constraints = new GridBagConstraints();
		constraints.insets = new Insets(0,5,0,0);
		constraints.gridx = 2;
		constraints.gridy = 7;
		constraints.anchor = GridBagConstraints.LINE_START;
		add(overlapSpinner, constraints);

		refreshValues();
	}

	private void refreshValues() {
		editing = true;
		randomizeCheckBox.setSelected(parent.getGUIController().isRandomizeList(playListUUID));
		stopAfterEachSoundCheckBox.setSelected(parent.getGUIController().isStopAfterEachSound(playListUUID));
		playAtRandomVolumesSliderFrom.setValue((int) (parent.getGUIController().getRandomizeVolumeFrom(playListUUID)*100));
		playAtRandomVolumesSliderTo.setValue((int) (parent.getGUIController().getRandomizeVolumeTo(playListUUID)*100));
		fadeInSpinner.setValue(parent.getGUIController().getFadeIn(playListUUID)/1000.0);
		fadeOutSpinner.setValue(parent.getGUIController().getFadeOut(playListUUID)/1000.0);
		overlapSpinner.setValue(parent.getGUIController().getOverlap(playListUUID)/1000.0);
		editing = false;
	}

	private void addMouseListenerToComponent(final Component comp) {
		comp.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(final MouseEvent arg0) {}

			@Override
			public void mouseEntered(final MouseEvent arg0) {}

			@Override
			public void mouseExited(final MouseEvent arg0) {}

			@Override
			public void mousePressed(final MouseEvent arg0) {
				if (arg0.isPopupTrigger()) {
					showPopup(arg0);
				}
			}

			@Override
			public void mouseReleased(final MouseEvent arg0) {
				if (arg0.isPopupTrigger()) {
					showPopup(arg0);
				}
			}

		});
	}

	@Override
	public void contentsChanged(final ListDataEvent arg0) {
		refreshValues();
	}

	@Override
	public void intervalAdded(final ListDataEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void intervalRemoved(final ListDataEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public GUIController getGUIController() {
		return parent.getGUIController();
	}

	@Override
	public void propagateHotkey(final KeyEvent event) {
		parent.propagateHotkey(event);
	}

	@Override
	public void propagatePopupMenu(final JPopupMenu menu, final MouseEvent e) {
		parent.propagatePopupMenu(menu,e);
	}

	@Override
	public void mouseClicked(final MouseEvent arg0) {}

	@Override
	public void mouseEntered(final MouseEvent arg0) {

	}

	@Override
	public void mouseExited(final MouseEvent arg0) {}

	@Override
	public void mousePressed(final MouseEvent arg0) {
		if (arg0.isPopupTrigger())
			showPopup(arg0);
	}

	@Override
	public void mouseReleased(final MouseEvent arg0) {
		SwingUtilities.getWindowAncestor(this).requestFocus();
		if (arg0.isPopupTrigger())
			showPopup(arg0);
	}

	private void showPopup(final MouseEvent e) {
		JPopupMenu popup = new JPopupMenu();
		JMenu hotkeyMenu = new JMenu("Hotkeys");
		popup.add(hotkeyMenu);
		final String title = getGUIController().getTitle(playListUUID);
		List<JMenuItem> itemList = new LinkedList<JMenuItem>();

		hotkeyMenu.add(new TitledSeparator("Add hotkeys", false));

		hotkeyMenu.add(new AddMenuItem(
				new AbstractAction("Set randomizing") {
					@Override
					public void actionPerformed(final ActionEvent arg0) {
						KeyEvent e = new HotkeyDialog(SwingUtilities.getWindowAncestor(SettingsPanel.this)).getEvent(); if (e.getKeyCode() == KeyEvent.VK_ESCAPE) return;
						Object obj = UserInput.getInput(SettingsPanel.this, "Turn on", "Turn off");
						parent.getGUIController().setHotkey(playListUUID, e, randomizeListAction.specialize(obj + " randomizing for '" + title + "'","Turn on".equals(obj)));
					}

				}));

		for (Tuple<String,Action> tuple : getGUIController().getHotkeys(randomizeListAction)) {
			itemList.add(new RemoveHotkeyMenuItem(tuple.second, tuple.second.getDescription(), getGUIController()));
		}

		hotkeyMenu.add(new AddMenuItem(
				new AbstractAction("Set randomize volume (minimum)") {
					@Override
					public void actionPerformed(final ActionEvent arg0) {
						KeyEvent e = new HotkeyDialog(SwingUtilities.getWindowAncestor(SettingsPanel.this)).getEvent(); if (e.getKeyCode() == KeyEvent.VK_ESCAPE) return;
						Double value = UserInput.getInput(SettingsPanel.this, 0.0d, 100.0d, 1.0d, (int) (getGUIController().getRandomizeVolumeFrom(playListUUID) * 100.0));
						if (value != null)
							parent.getGUIController().setHotkey(playListUUID, e, randomizeVolumeFromAction.specialize("Set minimum of randomize volume for '" + title + "' to " + value + "%", (float) (value / 100.0d)));
					}
				}));

		for (Tuple<String,Action> tuple : getGUIController().getHotkeys(randomizeVolumeFromAction)) {
			itemList.add(new RemoveHotkeyMenuItem(tuple.second, tuple.second.getDescription(), getGUIController()));
		}


		hotkeyMenu.add(new AddMenuItem(
				new AbstractAction("Set randomize volume (maximum)") {
					@Override
					public void actionPerformed(final ActionEvent arg0) {
						KeyEvent e = new HotkeyDialog(SwingUtilities.getWindowAncestor(SettingsPanel.this)).getEvent(); if (e.getKeyCode() == KeyEvent.VK_ESCAPE) return;
						Double value = UserInput.getInput(SettingsPanel.this, 0.0d, 100.0d, 1.0d, (int) (getGUIController().getRandomizeVolumeTo(playListUUID) * 100.0));
						if (value != null)
							parent.getGUIController().setHotkey(playListUUID, e, randomizeVolumeToAction.specialize("Set maximum of randomize volume for '" + title + "' to " + value + "%", (float) (value / 100.0d)));
					}
				}));

		for (Tuple<String,Action> tuple : getGUIController().getHotkeys(randomizeVolumeToAction)) {
			itemList.add(new RemoveHotkeyMenuItem(tuple.second, tuple.second.getDescription(), getGUIController()));
		}

		hotkeyMenu.add(new AddMenuItem(
				new AbstractAction("Set stop after each sound") {
					@Override
					public void actionPerformed(final ActionEvent arg0) {
						KeyEvent e = new HotkeyDialog(SwingUtilities.getWindowAncestor(SettingsPanel.this)).getEvent(); if (e.getKeyCode() == KeyEvent.VK_ESCAPE) return;
						Object obj = UserInput.getInput(SettingsPanel.this, "Turn on", "Turn off");
						parent.getGUIController().setHotkey(playListUUID, e, stopAfterEachSoundAction.specialize(obj + " stopping after each sound for '" + title + "'", "Turn on".equals(obj)));
					}
				}));

		for (Tuple<String,Action> tuple : getGUIController().getHotkeys(stopAfterEachSoundAction)) {
			itemList.add(new RemoveHotkeyMenuItem(tuple.second, tuple.second.getDescription(), getGUIController()));
		}

		hotkeyMenu.add(new AddMenuItem(
				new AbstractAction("Set fade in") {
					@Override
					public void actionPerformed(final ActionEvent arg0) {
						KeyEvent e = new HotkeyDialog(SwingUtilities.getWindowAncestor(SettingsPanel.this)).getEvent(); if (e.getKeyCode() == KeyEvent.VK_ESCAPE) return;
						Double value = UserInput.getInput(SettingsPanel.this, 0.0d, 9.9d, 0.1d, (int) (getGUIController().getFadeIn(playListUUID) / 1000.0));
						if (value != null)
							parent.getGUIController().setHotkey(playListUUID, e, fadeInAction.specialize("Set fade in time for '" + title + "' to " + value + "s", (int) (value * 1000)));
					}
				}));

		for (Tuple<String,Action> tuple : getGUIController().getHotkeys(fadeInAction)) {
			itemList.add(new RemoveHotkeyMenuItem(tuple.second, tuple.second.getDescription(), getGUIController()));
		}

		hotkeyMenu.add(new AddMenuItem(
				new AbstractAction("Set fade out") {
					@Override
					public void actionPerformed(final ActionEvent arg0) {
						KeyEvent e = new HotkeyDialog(SwingUtilities.getWindowAncestor(SettingsPanel.this)).getEvent(); if (e.getKeyCode() == KeyEvent.VK_ESCAPE) return;
						Double value = UserInput.getInput(SettingsPanel.this, 0.0d, 9.9d, 0.1d, (int) (getGUIController().getFadeOut(playListUUID) / 1000.0));
						if (value != null)
							parent.getGUIController().setHotkey(playListUUID, e, fadeOutAction.specialize("Set fade out time for '" + title + "' to " + value  + "s",(int) (value * 1000)));
					}
				}));

		for (Tuple<String,Action> tuple : getGUIController().getHotkeys(fadeOutAction)) {
			itemList.add(new RemoveHotkeyMenuItem(tuple.second, tuple.second.getDescription(), getGUIController()));
		}

		hotkeyMenu.add(new AddMenuItem(
				new AbstractAction("Set overlap") {
					@Override
					public void actionPerformed(final ActionEvent arg0) {
						KeyEvent e = new HotkeyDialog(SwingUtilities.getWindowAncestor(SettingsPanel.this)).getEvent(); if (e.getKeyCode() == KeyEvent.VK_ESCAPE) return;
						Double value = UserInput.getInput(SettingsPanel.this, 0.0d, 9.9d, 0.1d, (int) (getGUIController().getOverlap(playListUUID) / 1000.0));
						if (value != null)
							parent.getGUIController().setHotkey(playListUUID, e, overlapAction.specialize("Set overlap time for '" + title + "' to " + value  + "s", (int) (value * 1000)));
					}
				}));

		for (Tuple<String,Action> tuple : getGUIController().getHotkeys(overlapAction)) {
			itemList.add(new RemoveHotkeyMenuItem(tuple.second, tuple.second.getDescription(), getGUIController()));
		}

		if (!itemList.isEmpty()) {
			hotkeyMenu.add(new TitledSeparator("Remove hotkeys", true));
		}

		for (JMenuItem item : itemList) {
			hotkeyMenu.add(item);
		}

		parent.propagatePopupMenu(popup, e);
	}

	@Override
	public void updateMinimumSize() {
		parent.updateMinimumSize();
	}
}
