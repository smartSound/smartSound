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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

public class UserInput extends JDialog {
	static Object getInput(Component comp, Object... possibilities) {
		JPanel panel = new JPanel(new GridBagLayout());
		ButtonGroup btnGroup = new ButtonGroup();
		
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		
		panel.add(new JLabel("Choose an option: "));
		c.gridy = 1;
		panel.add(new JLabel(" "));
		
		JRadioButton radioButton;
		Map<String, Object> objectMap = new HashMap<String,Object>();
		for (Object obj : possibilities) {
			c.gridy = c.gridy + 1;
			objectMap.put(obj.toString(), obj);
			radioButton = new JRadioButton(obj.toString());
			panel.add(radioButton,c);
			btnGroup.add(radioButton);
		}
		
		c.gridy = c.gridy + 1;
		c.anchor = GridBagConstraints.SOUTH;
		
		UserInput input = new UserInput(comp,panel);

		for (AbstractButton aBtn : Collections.list(btnGroup.getElements())) {
			if (aBtn.isSelected()) {
				return objectMap.get(aBtn.getText());
			}
		}
		return null;
	}
	
	static Double getInput(Component comp, double from, double to, double stepSize, double defaultValue) {
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
	
	
	private UserInput(Component comp, JPanel panel) {
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
			public void actionPerformed(ActionEvent e) {
				UserInput.this.setVisible(false);
			}
		}), c);
		
		setMinimumSize(new Dimension(100,-1));
		pack();
		setLocation((int) (comp.getLocationOnScreen().getX() + comp.getWidth() / 2 - getWidth() / 2), (int) (comp.getLocationOnScreen().getY() + comp.getHeight() / 2 - getHeight() / 2));
		setVisible(true);
	}
	
	public static void main(String[] args) {
		JFrame mainFrame = new JFrame();
		mainFrame.setVisible(true);
		mainFrame.setLocation(300,300);
		mainFrame.setSize(200,200);
		UserInput.getInput(mainFrame, true, false);
	}
}
