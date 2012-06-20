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
import java.awt.geom.Rectangle2D;
import javax.swing.border.EmptyBorder;

public class PlayListPanelBorder extends EmptyBorder
{

    public PlayListPanelBorder(String title, Image imageList[])
    {
        super(getTop(imageList), getLeft(), getBottom(), getRight());
        this.imageList = imageList;
        activityList = new boolean[imageList.length];
        this.title = title;
    }

    private static int getRight()
    {
        return 8;
    }

    private static int getBottom()
    {
        return 8;
    }

    private static int getLeft()
    {
        return 8;
    }

    private static int getTop(Image imageList[])
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
        int leftBorder = (x + width) - right / 2;
        int i = 0;
        for(i = imageList.length - 1; i >= index; i--)
        {
            Image img = imageList[i];
            leftBorder -= 10 + img.getWidth(null);
        }

        return new Rectangle(leftBorder, (y + top / 2) - imageList[index].getHeight(null) / 2, imageList[index].getWidth(null), imageList[index].getHeight(null));
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
    {
        super.paintBorder(c, g, x, y, width, height);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setBackground(c.getBackground());
        Composite comp = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(10, 0.2F));
        int dx = left / 2;
        int dy = top / 2;
        g2d.drawRect(x + dx, y + dy, width - left / 2 - right / 2 - 1, height - top / 2 - bottom / 2 - 1);
        g2d.setComposite(comp);
        Font font = g2d.getFont();
        g2d.setFont(new Font(font.getName(), 1, 12));
        Rectangle2D rect = g2d.getFontMetrics().getStringBounds(title, g2d);
        g2d.clearRect(x + dx + 10, (y + dy) - (g2d.getFontMetrics().getAscent() + g2d.getFontMetrics().getDescent()) / 2, (int)rect.getWidth(), (int)rect.getHeight());
        dy += -(g2d.getFontMetrics().getAscent() + g2d.getFontMetrics().getDescent()) / 2 + g2d.getFontMetrics().getAscent();
        g2d.drawString(title, x + dx + 10, y + dy);
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
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillOval(imageRect.x, imageRect.y, imageRect.width, imageRect.height);
            g2d.setColor(Color.BLUE);
            g2d.fillArc(imageRect.x, imageRect.y, imageRect.width, imageRect.height, 90, (int)(-volume * 360D));
            g2d.setColor(Color.BLACK);
            g2d.drawOval(imageRect.x, imageRect.y, imageRect.width, imageRect.height);
            g2d.setColor(Color.WHITE);
            String percentage = String.valueOf((int)(volume * 100D));
            font = g2d.getFont();
            g2d.setFont(new Font(font.getName(), font.getStyle(), 10));
            FontMetrics fm = g2d.getFontMetrics();
            int stringWidth = fm.stringWidth(percentage);
            int textY = ((imageRect.y + ((imageRect.y + imageRect.height + 1) - imageRect.y) / 2) - (fm.getAscent() + fm.getDescent()) / 2) + fm.getAscent();
            g2d.drawString(percentage, imageRect.x + (imageRect.width - stringWidth) / 2, textY);
        }
    }

    public int posToIconIndex(Point pos, int x, int y, int width, int height)
    {
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
}
