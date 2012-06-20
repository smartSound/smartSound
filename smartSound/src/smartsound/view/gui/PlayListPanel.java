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

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import smartsound.controller.Launcher;


public class PlayListPanel extends JPanel
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
        setBorder(new PlayListPanelBorder("Playlist", imageList));
        add(new JScrollPane(playList), "PLAYLIST");
        JPanel settingsPanel = new SettingsPanel(playListDataModel);
        add(settingsPanel, "SETTINGS");
        MouseAdapter motionAdapter = new MouseAdapter() {

            public void mouseMoved(MouseEvent e)
            {
                setShowVolume(false);
                PlayListPanel panel = (PlayListPanel)e.getSource();
                int index = panel.posToIconIndex(e.getPoint());
                if(index == -1)
                {
                    panel.setCursor(Cursor.getDefaultCursor());
                } else
                {
                    panel.setCursor(Cursor.getPredefinedCursor(12));
                    if(index == 4)
                        setShowVolume(true);
                }
            }

            public void mouseExited(MouseEvent e)
            {
                setShowVolume(false);
            }
        }
;
        addMouseMotionListener(motionAdapter);
        addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e)
            {
                PlayListPanel panel = (PlayListPanel)e.getSource();
                int index = panel.posToIconIndex(e.getPoint());
                if(panel.posToIconIndex(e.getPoint()) != -1)
                    if(index == 0)
                        panel.play();
                    else
                    if(index == 1)
                        panel.stop();
                    else
                    if(index == 2)
                        panel.toggleRepeating();
                    else
                    if(index == 3)
                        ((CardLayout)panel.getLayout()).next(panel);
            }

            public void mousePressed(MouseEvent e)
            {
                PlayListPanel panel = (PlayListPanel)e.getSource();
                int index = panel.posToIconIndex(e.getPoint());
                if(index == 4)
                    panel.draggingSpeaker = true;
            }

            public void mouseReleased(MouseEvent e)
            {
                PlayListPanel panel = (PlayListPanel)e.getSource();
                panel.draggingSpeaker = false;
            }
        }
);
        addMouseMotionListener(new MouseMotionAdapter() {

            public void mouseDragged(MouseEvent e)
            {
                PlayListPanel panel = (PlayListPanel)e.getSource();
                if(!panel.draggingSpeaker)
                    return;
                int degrees = panel.posToAngle(e.getPoint());
                float percentage = (float)((double)degrees / 360D);
                if((double)percentage < 0.25D && movedClockwise)
                    percentage = 1.0F;
                else
                if((double)percentage > 0.75D && !movedClockwise)
                    percentage = 0.0F;
                else
                    movedClockwise = percentage > 0.5F;
                setShowVolume(true);
                panel.setVolume(percentage);
                repaint();
            }
        }
);
        addMouseWheelListener(new MouseAdapter() {

            public void mouseWheelMoved(MouseWheelEvent e)
            {
                PlayListPanel panel = (PlayListPanel)e.getSource();
                if(panel.posToIconIndex(e.getPoint()) != 4)
                {
                    return;
                } else
                {
                    float newVolume = lastKnownVolume;
                    newVolume += 0.02F * (float)e.getUnitsToScroll();
                    newVolume = Math.max(0.0F, newVolume);
                    newVolume = Math.min(1.0F, newVolume);
                    setShowVolume(true);
                    panel.setVolume(newVolume);
                    return;
                }
            }
        }
);
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
        lastKnownVolume = volume;
        border.setVolume(volume);
    }


}
