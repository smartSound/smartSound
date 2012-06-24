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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.UUID;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import smartsound.view.Action;


public class SettingsPanel extends javax.swing.JPanel implements ListDataListener, IGUILadder {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6840626218594923611L;
	
	protected boolean editing = false;
	
	private IGUILadder parent;
	private UUID playListUUID;
	private Action randomizeListAction;
    private Action randomizeVolumeFromAction;
    private Action randomizeVolumeToAction;
    private Action stopAfterEachSoundAction;
    private Action fadeInAction;
    private Action fadeOutAction;
    private Action overlapAction;
	
	
	private JCheckBox randomizeCheckBox = new JCheckBox();
	private JLabel randomizeLabel = new JLabel("Randomize playlist");

	private JCheckBox stopAfterEachSoundCheckBox = new JCheckBox();
	private JLabel stopAfterEachSoundLabel = new JLabel("Stop after each sound");

	private JLabel playAtRandomVolumesLabel = new JLabel("Play at random volumes");
	protected JSlider playAtRandomVolumesSliderFrom = new JSlider(0,100);
	protected JLabel playAtRandomVolumesFromLabel = new JLabel(playAtRandomVolumesSliderFrom.getValue() + " %");
	protected JSlider playAtRandomVolumesSliderTo = new JSlider(0,100);
	protected JLabel playAtRandomVolumesToLabel = new JLabel(playAtRandomVolumesSliderTo.getValue() + " %");
	
	private JLabel fadeInLabel = new JLabel("Fade In");
	private JSpinner fadeInSpinner = new JSpinner(new SpinnerNumberModel(0,0,9.9,0.1));
	
	private JLabel fadeOutLabel = new JLabel("Fade Out");
	private JSpinner fadeOutSpinner = new JSpinner(new SpinnerNumberModel(0,0,9.9,0.1));
	
	private JLabel overlapLabel = new JLabel("Overlap");
	private JSpinner overlapSpinner = new JSpinner(new SpinnerNumberModel(0,0,9.9,0.1));
	
	public SettingsPanel(IGUILadder parent, PlayListDataModel playListDataModel) {
		super(new GridBagLayout());
		
		this.parent = parent;
		playListUUID = playListDataModel.getUUID();
		
		randomizeListAction = parent.getGUIController().getRandomizeListAction(playListUUID);
        randomizeVolumeFromAction = parent.getGUIController().getRandomizeVolumeFromAction(playListUUID);
        randomizeVolumeToAction = parent.getGUIController().getRandomizeVolumeToAction(playListUUID);
        stopAfterEachSoundAction = parent.getGUIController().getStopAfterEachSoundAction(playListUUID);
        fadeInAction = parent.getGUIController().getFadeInAction(playListUUID);
        fadeOutAction = parent.getGUIController().getFadeOutAction(playListUUID);
        overlapAction = parent.getGUIController().getOverlapAction(playListUUID);
		
		
		randomizeCheckBox.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JCheckBox source = (JCheckBox) e.getSource();
				
				if (!editing) randomizeListAction.execute(source.isSelected());
			}	
		});
		
		stopAfterEachSoundCheckBox.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JCheckBox source = (JCheckBox) e.getSource();
				
				if (!editing) stopAfterEachSoundAction.execute(source.isSelected());
			}	
		});
		
		playAtRandomVolumesSliderFrom.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
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
		playAtRandomVolumesSliderTo.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
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
		
		fadeInSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner source = (JSpinner) e.getSource();
				if (!editing) fadeInAction.execute((int) (((Number) source.getValue()).doubleValue()*1000));
			}
		});
		fadeInSpinner.setEditor(new JSpinner.NumberEditor(fadeInSpinner, "0.0 s"));
		fadeInSpinner.setPreferredSize(new Dimension(70,fadeInSpinner.getPreferredSize().height));
		
		fadeOutSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner source = (JSpinner) e.getSource();
				if (!editing) fadeOutAction.execute((int) (((Number) source.getValue()).doubleValue()*1000));
			}
		});
		fadeOutSpinner.setEditor(new JSpinner.NumberEditor(fadeOutSpinner, "0.0 s"));
		fadeOutSpinner.setPreferredSize(new Dimension(70,fadeOutSpinner.getPreferredSize().height));
		
		overlapSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner source = (JSpinner) e.getSource();
				if (!editing) overlapAction.execute((int) (((Number) source.getValue()).doubleValue()*1000));
			}
		});
		overlapSpinner.setEditor(new JSpinner.NumberEditor(overlapSpinner, "0.0 s"));
		overlapSpinner.setPreferredSize(new Dimension(70, overlapSpinner.getPreferredSize().height));
		
		
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
		fadeOutSpinner.setValue((int) parent.getGUIController().getFadeOut(playListUUID)/1000.0);
		overlapSpinner.setValue((int) parent.getGUIController().getOverlap(playListUUID)/1000.0);
		editing = false;
	}

	@Override
	public void contentsChanged(ListDataEvent arg0) {
		refreshValues();
	}

	@Override
	public void intervalAdded(ListDataEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void intervalRemoved(ListDataEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GUIController getGUIController() {
		return parent.getGUIController();
	}

	@Override
	public void propagateHotkey(KeyEvent event) {
		parent.propagateHotkey(event);
	}

	@Override
	public void propagatePopupMenu(JPopupMenu menu, MouseEvent e) {
		parent.propagatePopupMenu(menu,e);
	}
}
