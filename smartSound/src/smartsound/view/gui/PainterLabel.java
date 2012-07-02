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
import java.io.File;
import javax.swing.*;
import smartsound.controller.Launcher;
import smartsound.player.ItemData;


public class PainterLabel extends DefaultListCellRenderer
{

    public PainterLabel()
    {
        repeating = false;
        chained = false;
    }

    public void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g;
        g2d.setBackground(getBackground());
        g2d.setPaint(getBackground());
        g2d.fill(cellRect);
        g2d.setPaint(getForeground());
        g2d.draw(cellRect);
        String displayTitle = title;
        int height = titleRect.height;
        Font font = g2d.getFont();
        for(; g2d.getFontMetrics().getHeight() > height; g2d.setFont(new Font(font.getName(), font.getStyle(), font.getSize() - 1)))
            font = g2d.getFont();

        int width = titleRect.width;
        if(width - g2d.getFontMetrics().stringWidth(displayTitle) < 0)
        {
            displayTitle = (new StringBuilder(String.valueOf(displayTitle.substring(0, displayTitle.length() - 1)))).append("...").toString();
            displayTitle = (new StringBuilder(String.valueOf(displayTitle))).append("...").toString();
        }
        for(; width - g2d.getFontMetrics().stringWidth(displayTitle) < 0; displayTitle = (new StringBuilder(String.valueOf(displayTitle.substring(0, displayTitle.length() - 4)))).append("...").toString());
        int y = (int)((((double)titleRect.y + titleRect.getHeight() / 2D) - (double)((g2d.getFontMetrics().getAscent() + g2d.getFontMetrics().getDescent()) / 2)) + (double)g2d.getFontMetrics().getAscent());
        g2d.drawString(displayTitle, titleRect.x, y);
        Composite comp = g2d.getComposite();
        if(!repeating)
            g2d.setComposite(AlphaComposite.getInstance(10, 0.5F));
        Image image = (new ImageIcon((new File((new StringBuilder(String.valueOf(Launcher.getImageDir()))).append("/repeat_item.png").toString())).getAbsolutePath())).getImage();
        g2d.drawImage(image, repeatRect.x, repeatRect.y, repeatRect.width, repeatRect.height, null);
        g2d.setComposite(comp);
        if(!chained)
            g2d.setComposite(AlphaComposite.getInstance(10, 0.5F));
        image = (new ImageIcon((new File((new StringBuilder(String.valueOf(Launcher.getImageDir()))).append("/chain.png").toString())).getAbsolutePath())).getImage();
        g2d.drawImage(image, chainRect.x, chainRect.y, chainRect.width, chainRect.height, null);
        g2d.setComposite(comp);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
        title = value.toString();
        setText(" ");
        PlayList playList = (PlayList)list;
        ItemData entry = (ItemData)value;
        setSize(playList.getWidth(), playList.getFixedCellHeight());
        if(isSelected)
        {
            setBackground(list.getSelectionBackground());
            setForeground(Color.BLACK);
        } else
        {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        Font font = getFont();
        setFont(new Font(font.getName(), 0, font.getSize()));
        if(entry.isActive())
        {
            setForeground(activeColor);
            setFont(new Font(font.getName(), 1, font.getSize()));
        }
        cellRect = playList.getCellRect();
        titleRect = playList.getTitleRect();
        repeatRect = playList.getRepeatRect();
        chainRect = playList.getChainRect();
        repeating = entry.isRepeating();
        chained = entry.getChainWith() != null;
        return this;
    }

    private static final Color activeColor;
    private String title;
    private Rectangle cellRect;
    private Rectangle titleRect;
    private Rectangle repeatRect;
    private Rectangle chainRect;
    boolean repeating;
    boolean chained;

    static 
    {
        activeColor = Color.BLUE;
    }
}
