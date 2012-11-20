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
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

public class UserInput extends JDialog {

	static Object getInput(final Component comp, final Object... possibilities) {
		return getInput(comp, false, possibilities);
	}

	static Object getInput(final Component comp, final boolean multipleSelect, final Object... possibilities) {
		JPanel panel = new JPanel(new GridBagLayout());
		ButtonGroup btnGroup = new ButtonGroup();

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;

		if (multipleSelect)
			panel.add(new JLabel("Choose some options: "));
		else
			panel.add(new JLabel("Choose an option: "));

		c.gridy = 1;
		panel.add(new JLabel(" "));

		JToggleButton button;
		List<JToggleButton> buttons = new LinkedList<JToggleButton>();
		Map<String, Object> objectMap = new HashMap<String,Object>();
		boolean set = false;
		for (Object obj : possibilities) {
			c.gridy = c.gridy + 1;
			objectMap.put(obj.toString(), obj);
			if (multipleSelect)
				button = new JCheckBox(obj.toString());
			else
				button = new JRadioButton(obj.toString());

			buttons.add(button);
			if (!set) {
				button.setSelected(true);
				set = true;
			}
			panel.add(button,c);
			if (!multipleSelect)
				btnGroup.add(button);
		}

		c.gridy = c.gridy + 1;
		c.anchor = GridBagConstraints.SOUTH;

		UserInput input = new UserInput(comp,panel);

		List<Object> resultList = new LinkedList<Object>();
		for (AbstractButton aBtn : buttons) {
			if (aBtn.isSelected()) {
				if (multipleSelect)
					resultList.add(objectMap.get(aBtn.getText()));
				else
					return objectMap.get(aBtn.getText());
			}
		}
		return multipleSelect ? resultList : null;
	}

	static Double getInput(final Component comp, final double from, final double to, final double stepSize, final double defaultValue) {
		JPanel panel = new JPanel(new GridBagLayout());
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(defaultValue, from, to, stepSize));

		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;

		panel.add(new JLabel("Choose a value: "), c);
		c.gridy = 1;
		panel.add(spinner,c);

		c.gridy = 2;
		UserInput input = new UserInput(comp, panel);

		return (Double) spinner.getValue();
	}


	private UserInput(final Component comp, final JPanel panel) {
		super(SwingUtilities.getWindowAncestor(comp));

		setModalityType(ModalityType.APPLICATION_MODAL);
		setAlwaysOnTop(true);
		setUndecorated(true);
		panel.setBorder(BorderFactory.createEtchedBorder());
		add(panel);

		GridBagLayout layout = (GridBagLayout) panel.getLayout();

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(new JButton(new AbstractAction("OK") {
			@Override
			public void actionPerformed(final ActionEvent e) {
				UserInput.this.setVisible(false);
			}
		}), c);

		setMinimumSize(new Dimension(100,-1));
		pack();
		setLocation((int) (comp.getLocationOnScreen().getX() + comp.getWidth() / 2 - getWidth() / 2), (int) (comp.getLocationOnScreen().getY() + comp.getHeight() / 2 - getHeight() / 2));
		setVisible(true);
	}
}
