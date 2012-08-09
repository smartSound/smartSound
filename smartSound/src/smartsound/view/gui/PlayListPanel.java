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

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.Border;


public class PlayListPanel extends JPanel implements IGUILadder
{

	protected PlayList playList;
	public static final String PLAYLISTCARD = "PLAYLIST";
	public static final String SETTINGSCARD = "SETTINGS";
	private static final long serialVersionUID = 0xee7b24ca5ba3db42L;
	protected boolean movedClockwise;
	protected boolean draggingSpeaker;
	private IGUILadder parent;

	public PlayListPanel(final IGUILadder parent, final Border border, final PlayListDataModel playListDataModel)
	{
		setBorder(border);
		this.parent = parent;
		movedClockwise = false;
		draggingSpeaker = false;
		playList = new PlayList(this, playListDataModel);
		playList.setPanel(this);
		CardLayout layout = new CardLayout();
		setLayout(layout);

		add(new JScrollPane(playList), PLAYLISTCARD);
		JPanel settingsPanel = new SettingsPanel(this, playListDataModel);
		add(settingsPanel, SETTINGSCARD);
		setOpaque(false);
		this.parent = parent;
	}

	protected int posToAngle(final Point point)
	{
		PlayListPanelBorder border = (PlayListPanelBorder)getBorder();
		return border.posToAngle(point, 0, 0, getWidth(), getHeight());
	}

	public void setShowVolume(final boolean showVolume)
	{
		PlayListPanelBorder border = (PlayListPanelBorder)getBorder();
		border.setShowVolume(showVolume);
		repaint();
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
		parent.propagatePopupMenu(menu, e);
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(getBackground());
		g2d.fillRoundRect(2, 0, getWidth() - 5, getHeight() - 4, 16, 16);
		g2d.setColor(getForeground());
	}

	public boolean getUsesDrag() {
		return draggingSpeaker;
	}

	@Override
	public void updateMinimumSize() {
		setMinimumSize(new Dimension(295,330));
		parent.updateMinimumSize();
	}

}
