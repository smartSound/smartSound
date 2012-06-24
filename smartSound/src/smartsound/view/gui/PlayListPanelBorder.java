/* 
 *	Copyright (C) 2012 Andr� Becker
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

import java.awt.AlphaComposite;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.UUID;

import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import smartsound.controller.Launcher;
import smartsound.view.Action;

public class PlayListPanelBorder extends TitledBorder implements MouseListener, MouseMotionListener, MouseWheelListener, IGUILadder, ListDataListener
{

	private IGUILadder parent;
	private PlayListPanel panel; //ONLY for determination of border insets
	private float lastKnownVolume;
	protected boolean movedClockwise = false;
	private UUID playListUUID;
	private Action playAction;
    private Action stopAction;
    private Action repeatListAction;
	
    public PlayListPanelBorder(IGUILadder parent, PlayListDataModel model, String title)
    {
        super(title);
        this.parent = parent;
        this.panel = new PlayListPanel(this, this, model);
        this.title = title;
        playListUUID = model.getUUID();
        model.addListDataListener(this);
        
        imageList = new Image[5];
        imageList[0] = (new ImageIcon((new File((new StringBuilder(String.valueOf(Launcher.getImageDir()))).append("/arrow-play.png").toString())).getAbsolutePath())).getImage();
        imageList[1] = (new ImageIcon((new File((new StringBuilder(String.valueOf(Launcher.getImageDir()))).append("/box-stop.png").toString())).getAbsolutePath())).getImage();
        imageList[2] = (new ImageIcon((new File((new StringBuilder(String.valueOf(Launcher.getImageDir()))).append("/repeat.png").toString())).getAbsolutePath())).getImage();
        imageList[3] = (new ImageIcon((new File((new StringBuilder(String.valueOf(Launcher.getImageDir()))).append("/settings.png").toString())).getAbsolutePath())).getImage();
        imageList[4] = (new ImageIcon((new File((new StringBuilder(String.valueOf(Launcher.getImageDir()))).append("/speaker.png").toString())).getAbsolutePath())).getImage();

        activityList = new boolean[imageList.length];
        updateButtons();
        
        playAction = parent.getGUIController().getPlayPlayListAction(playListUUID);
    	stopAction = parent.getGUIController().getStopAction(playListUUID);
    	repeatListAction = parent.getGUIController().getRepeatListAction(playListUUID);
        
        panel.addMouseMotionListener(this);
        panel.addMouseWheelListener(this);
        panel.addMouseListener(this);
        
        
    }

    private int getRight()
    {
    	return super.getBorderInsets(panel).right;
        //return 8;
    }

    private int getBottom()
    {
    	return super.getBorderInsets(panel).bottom;
        //return 8;
    }

    private int getLeft()
    {
    	return super.getBorderInsets(panel).left;
        //return 8;
    }

    private int getTop(Image imageList[])
    {
        int minimum = 16;
        int maximumHeight = 0;
        Image aimage[];
        int j = (aimage = imageList).length;
        for(int i = 0; i < j; i++)
        {
            Image img = aimage[i];
            maximumHeight = Math.max(maximumHeight, img.getHeight(null));
        }

        return Math.max(minimum, maximumHeight + 4);
    }

    private Rectangle getImageRect(int index, int x, int y, int width, int height)
    {
        if(index >= imageList.length)
            return null;
        int leftBorder = (x + width) - getRight() / 2;
        int i = 0;
        for(i = imageList.length - 1; i >= index; i--)
        {
            Image img = imageList[i];
            leftBorder -= 10 + img.getWidth(null);
        }

        return new Rectangle(leftBorder, y + 2 /*+ (getTop(imageList) / 2) - imageList[index].getHeight(null) / 2*/, imageList[index].getWidth(null), imageList[index].getHeight(null));
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
    {
        super.paintBorder(c, g, x, y, width, height);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setBackground(c.getBackground());
        Composite comp = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(10, 0.2F));
        int dx = getLeft() / 2;
        int dy = getTop(imageList) / 2;
        //g2d.drawRect(x + dx, y + dy, width - getLeft() / 2 - getRight() / 2 - 1, height - getTop(imageList) / 2 - getBottom() / 2 - 1);
        g2d.setComposite(comp);
        Font font = g2d.getFont();
        g2d.setFont(new Font(font.getName(), 1, 12));
        Rectangle2D rect = g2d.getFontMetrics().getStringBounds(title, g2d);
        //g2d.clearRect(x + dx + 10, (y + dy) - (g2d.getFontMetrics().getAscent() + g2d.getFontMetrics().getDescent()) / 2, (int)rect.getWidth(), (int)rect.getHeight());
        dy += -(g2d.getFontMetrics().getAscent() + g2d.getFontMetrics().getDescent()) / 2 + g2d.getFontMetrics().getAscent();
        //g2d.drawString(title, x + dx + 10, y + dy);
        for(int i = imageList.length - 1; i >= 0; i--)
        {
            Rectangle imageRect = getImageRect(i, x, y, width, height);
            if(!activityList[i])
                g2d.setComposite(AlphaComposite.getInstance(10, 0.5F));
            g2d.drawImage(imageList[i], imageRect.x, imageRect.y, null);
            g2d.setComposite(comp);
        }

        if(showVolume)
        {
            Rectangle imageRect = getImageRect(imageList.length - 1, x, y, width, height);
            int min = Math.min(imageRect.width, imageRect.height);
            min = (min / 2) * 2 - 1;
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillOval(imageRect.x, imageRect.y, min, min);
            g2d.setColor(Color.BLUE);
            g2d.fillArc(imageRect.x, imageRect.y, min, min, 90, (int)(-volume * 360D));
            g2d.setColor(Color.BLACK);
            g2d.drawOval(imageRect.x, imageRect.y, min, min);
            g2d.setColor(Color.WHITE);
            String percentage = String.valueOf((int)(volume * 100D));
            font = g2d.getFont();
            g2d.setFont(new Font(font.getName(), font.getStyle(), 10));
            FontMetrics fm = g2d.getFontMetrics();
            int stringWidth = fm.stringWidth(percentage);
            int textY = ((imageRect.y + ((imageRect.y + min + 1) - imageRect.y) / 2) - (fm.getAscent() + fm.getDescent()) / 2) + fm.getAscent();
            g2d.drawString(percentage, imageRect.x + (min - stringWidth) / 2, textY);
        }
    }

    public int posToIconIndex(Point pos)
    {
    	int x = 0,y = 0;
    	int width = panel.getWidth();
    	int height = panel.getHeight();
        for(int i = 0; i < imageList.length; i++)
        {
            Rectangle rect = getImageRect(i, x, y, width, height);
            if(rect != null && rect.contains(pos))
                return i;
        }

        return -1;
    }

    public void setActive(int index, boolean active)
    {
        if(index >= activityList.length)
        {
            return;
        } else
        {
            activityList[index] = active;
            return;
        }
    }

    public void setShowVolume(boolean show)
    {
        showVolume = show;
    }

    public void setVolume(double volume)
    {
        this.volume = volume;
    }

    public int posToAngle(Point point, int x, int y, int width, int height)
    {
        Rectangle imageRect = getImageRect(imageList.length - 1, x, y, width, height);
        int imageCenterX = imageRect.x + imageRect.width / 2;
        int imageCenterY = imageRect.y + imageRect.height / 2;
        double theta = Math.atan2(point.y - imageCenterY, point.x - imageCenterX);
        for(theta += 1.5707963267948966D; theta < 0.0D; theta += 6.2831853071795862D);
        return (int)Math.toDegrees(theta);
    }

    private static final long serialVersionUID = 0xb06ba5eaf1981e0cL;
    private static final int OFFSET = 10;
    private Image imageList[];
    private boolean activityList[];
    private String title;
    private boolean showVolume;
    private double volume;

	@Override
	public void mouseClicked(MouseEvent e) {
		PlayListPanel panel = (PlayListPanel)e.getSource();
        int index = posToIconIndex(e.getPoint());
        if(posToIconIndex(e.getPoint()) != -1)
            if(index == 0)
                playAction.execute();
            else
            if(index == 1)
                stopAction.execute();
            else
            if(index == 2)
                repeatListAction.execute(!parent.getGUIController().isRepeatList(playListUUID));
            else
            if(index == 3)
                ((CardLayout)panel.getLayout()).next(panel);
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		setShowVolume(false);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		PlayListPanel panel = (PlayListPanel)e.getSource();
        int index = posToIconIndex(e.getPoint());
        if(index == 4)
            panel.draggingSpeaker = true;		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
        PlayListPanel panel = (PlayListPanel)e.getSource();
        panel.draggingSpeaker = false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
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
        updateVolume(percentage);
        panel.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		setShowVolume(false);
        PlayListPanel panel = (PlayListPanel)e.getSource();
        int index = posToIconIndex(e.getPoint());
        if(index == -1)
        {
            panel.setCursor(Cursor.getDefaultCursor());
        } else
        {
            panel.setCursor(Cursor.getPredefinedCursor(12));
            if(index == 4)
                setShowVolume(true);
            panel.repaint();
        }
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		PlayListPanel panel = (PlayListPanel)e.getSource();
        if(posToIconIndex(e.getPoint()) != 4)
        {
            return;
        } else
        {
            float newVolume = lastKnownVolume;
            newVolume += 0.02F * (float)e.getUnitsToScroll();
            newVolume = Math.max(0.0F, newVolume);
            newVolume = Math.min(1.0F, newVolume);
            setShowVolume(true);
            updateVolume(newVolume);
            return;
        }
	}
	
    public void updateVolume(float volume)
    {
        lastKnownVolume = volume;
        setVolume(volume);
    }
    
    public PlayListPanel getPanel() {
    	return this.panel;
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
	
	private void updateButtons() {
		setActive(0, true);
		setActive(1, true);
		setActive(2, getGUIController().isRepeatList(playListUUID));
		setActive(3, true);
		setActive(4, true);
		updateVolume(getGUIController().getVolume(playListUUID));
		panel.repaint();
	}

	@Override
	public void contentsChanged(ListDataEvent arg0) {
		updateButtons();
	}

	@Override
	public void intervalAdded(ListDataEvent arg0) {}

	@Override
	public void intervalRemoved(ListDataEvent arg0) {}
}
