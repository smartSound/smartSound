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
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import smartsound.controller.Launcher;


public class PlayListPanel extends JPanel implements IGUILadder
{
	
	protected PlayList playList;
    private static final String PLAYLISTCARD = "PLAYLIST";
    private static final String SETTINGSCARD = "SETTINGS";
    private static final long serialVersionUID = 0xee7b24ca5ba3db42L;
    private static final int NO_OF_IMAGES = 5;
    public static final int PLAY = 0;
    public static final int STOP = 1;
    public static final int REPEAT = 2;
    public static final int SETTINGS = 3;
    public static final int SPEAKER = 4;
    protected boolean movedClockwise;
    protected boolean draggingSpeaker;
    private float lastKnownVolume;
    private IGUILadder parent;

    public PlayListPanel(GUIController controller, PlayListDataModel playListDataModel)
    {
        movedClockwise = false;
        draggingSpeaker = false;
        playList = new PlayList(controller, playListDataModel);
        playList.setPanel(this);
        CardLayout layout = new CardLayout();
        setLayout(layout);
        Image imageList[] = new Image[5];
        imageList[0] = (new ImageIcon((new File((new StringBuilder(String.valueOf(Launcher.getImageDir()))).append("/arrow-play.png").toString())).getAbsolutePath())).getImage();
        imageList[1] = (new ImageIcon((new File((new StringBuilder(String.valueOf(Launcher.getImageDir()))).append("/box-stop.png").toString())).getAbsolutePath())).getImage();
        imageList[2] = (new ImageIcon((new File((new StringBuilder(String.valueOf(Launcher.getImageDir()))).append("/repeat.png").toString())).getAbsolutePath())).getImage();
        imageList[3] = (new ImageIcon((new File((new StringBuilder(String.valueOf(Launcher.getImageDir()))).append("/settings.png").toString())).getAbsolutePath())).getImage();
        imageList[4] = (new ImageIcon((new File((new StringBuilder(String.valueOf(Launcher.getImageDir()))).append("/speaker.png").toString())).getAbsolutePath())).getImage();
        PlayListPanelBorder border = new PlayListPanelBorder(this, "Playlist", imageList);
        setBorder(border);
        add(new JScrollPane(playList), "PLAYLIST");
        JPanel settingsPanel = new SettingsPanel(playListDataModel);
        add(settingsPanel, "SETTINGS");
        MouseAdapter motionAdapter = new MouseAdapter() {

            public void mouseMoved(MouseEvent e)
            {
                
            }

            public void mouseExited(MouseEvent e)
            {
                
            }
        }
;
        addMouseMotionListener(border);
        addMouseListener(border);
        addMouseMotionListener(border);
        addMouseWheelListener(border);
        playList.contentsChanged(null);
    }

    protected int posToAngle(Point point)
    {
        PlayListPanelBorder border = (PlayListPanelBorder)getBorder();
        return border.posToAngle(point, 0, 0, getWidth(), getHeight());
    }

    protected void stop()
    {
        playList.stop();
    }

    protected void play()
    {
        playList.play();
    }

    protected void toggleRepeating()
    {
        playList.toggleRepeating();
    }

    protected int posToIconIndex(Point pos)
    {
        PlayListPanelBorder border = (PlayListPanelBorder)getBorder();
        return border.posToIconIndex(pos, 0, 0, getWidth(), getHeight());
    }

    public void setActive(int index, boolean active)
    {
        PlayListPanelBorder border = (PlayListPanelBorder)getBorder();
        border.setActive(index, active);
    }

    public void setShowVolume(boolean showVolume)
    {
        PlayListPanelBorder border = (PlayListPanelBorder)getBorder();
        border.setShowVolume(showVolume);
        repaint();
    }

    public void setVolume(float volume)
    {
        PlayListPanelBorder border = (PlayListPanelBorder)getBorder();
        playList.setVolume(volume);
        border.setVolume(volume);
        repaint();
    }

    public void updateVolume(float volume)
    {
        PlayListPanelBorder border = (PlayListPanelBorder)getBorder();
        border.updateVolume(volume);
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
