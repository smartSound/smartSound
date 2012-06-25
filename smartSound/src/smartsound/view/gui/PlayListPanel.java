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
    private static final String PLAYLISTCARD = "PLAYLIST";
    private static final String SETTINGSCARD = "SETTINGS";
    private static final long serialVersionUID = 0xee7b24ca5ba3db42L;
    private static final int NO_OF_IMAGES = 5;
    protected boolean movedClockwise;
    protected boolean draggingSpeaker;
    private IGUILadder parent;

    public PlayListPanel(IGUILadder parent, Border border, PlayListDataModel playListDataModel)
    {
    	setBorder(border);
    	this.parent = parent;
        movedClockwise = false;
        draggingSpeaker = false;
        playList = new PlayList(this, playListDataModel);
        playList.setPanel(this);
        CardLayout layout = new CardLayout();
        setLayout(layout);
        
        add(new JScrollPane(playList), "PLAYLIST");
        JPanel settingsPanel = new SettingsPanel(this, playListDataModel);
        add(settingsPanel, "SETTINGS");
        
        this.parent = parent;
    }

    protected int posToAngle(Point point)
    {
        PlayListPanelBorder border = (PlayListPanelBorder)getBorder();
        return border.posToAngle(point, 0, 0, getWidth(), getHeight());
    }

    public void setShowVolume(boolean showVolume)
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
	public void propagateHotkey(KeyEvent event) {
		parent.propagateHotkey(event);
	}

	@Override
	public void propagatePopupMenu(JPopupMenu menu, MouseEvent e) {
		parent.propagatePopupMenu(menu, e);
	}


}
