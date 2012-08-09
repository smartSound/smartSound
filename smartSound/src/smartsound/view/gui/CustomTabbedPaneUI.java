package smartsound.view.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JComponent;

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

public class CustomTabbedPaneUI extends javax.swing.plaf.basic.BasicTabbedPaneUI {

	protected int dropTargetIndex = -1;

	public CustomTabbedPaneUI() {
		super();
		contentBorderInsets = new Insets(0,0,0,0);
		selectedTabPadInsets = new Insets(0,0,0,0);
	}

	@Override
	protected void paintTab(final Graphics g, final int tabPlacement, final Rectangle[] rects,
			final int tabIndex, final Rectangle iconRect, final Rectangle textRect) {
		Graphics2D g2d = (Graphics2D) g;
		new NonTitledBorder().paintBorder(tabPane, g, rects[tabIndex].x, rects[tabIndex].y, rects[tabIndex].width, rects[tabIndex].height);
		if (tabIndex != tabPane.getSelectedIndex()) {
			g2d.setBackground(new Color(240,216,154));
			g2d.clearRect(rects[tabIndex].x + 4, rects[tabIndex].y + rects[tabIndex].height - 8, rects[tabIndex].width - 8, 8);
		}
		Rectangle rect = new Rectangle(rects[tabIndex]);
		rect.x = rect.x + 8;
		rect.y = rect.y + 4;
		rect.width = 40;

		paintText(g, tabPlacement, g.getFont(), g.getFontMetrics(), tabIndex, tabPane.getTitleAt(tabIndex), getTabBounds(tabPane, tabIndex), tabIndex == tabPane.getSelectedIndex());
		Rectangle imageRect = rects[tabIndex].getBounds();
		if (getIconForTab(tabIndex) != null) {
			imageRect.x = rects[tabIndex].x + rects[tabIndex].width / 2
					- getIconForTab(tabIndex).getIconWidth() / 2;
			imageRect.width = getIconForTab(tabIndex).getIconWidth();
			imageRect.height = getIconForTab(tabIndex).getIconHeight();
			imageRect.y += 4;
			paintIcon(g, tabPlacement, tabIndex, getIconForTab(tabIndex), imageRect, false);
		}

	}

	@Override
	protected Insets getTabAreaInsets(final int tabPlacement) {
		return new Insets(10,0,0,0);
	}

	@Override
	protected void paintText(final Graphics g, final int tabPlacement, final Font font, final FontMetrics fm, final int tabIndex, final String title, final Rectangle rect, final boolean selected) {
		double stringLength = fm.getStringBounds(title, g).getWidth();
		int x = rect.x + (int) (rect.width/2 - stringLength/2);
		Color oldColor = g.getColor();
		if (((TabbedPane) tabPane).getGUIController().isActive(((TabbedPane) tabPane).getUUIDAt(tabIndex))) {
			g.setColor(Color.BLUE);
			g.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
		}
		g.drawString(title, x, rect.y + 20);

		g.setColor(oldColor);
		g.setFont(font);
	}

	@Override
	public void paint(final Graphics g, final JComponent c) {
		if (tabPane.getComponentCount() == 0)
			return;
		super.paint(g, c);

		if (dropTargetIndex > -1 && dropTargetIndex < rects.length) {
			Rectangle rect = rects[dropTargetIndex];
			g.drawLine(rect.x, rect.y, rect.x, rect.y + rect.height);
		}
	}

	@Override
	protected int getTabRunIndent(final int tabPlacement, final int run) {
		return 10;

	}

	@Override
	protected void paintTabBorder(final Graphics g, final int tabPlacement, final int tabIndex, final int x, final int y, final int w, final int h, final boolean isSelected) {

	}

	@Override
	protected int getTabRunOffset(final int tabPlacement, final int tabCount, final int tabIndex, final boolean forward) {
		return 30;
	}

	@Override
	protected void paintContentBorder(final Graphics g, final int tabPlacement, final int selectedIndex)  {
		super.paintContentBorder(g, tabPlacement, selectedIndex);
		Insets in = getContentBorderInsets(selectedIndex);
		Graphics2D g2d = (Graphics2D) g;
		int y = calculateTabAreaHeight(tabPlacement, runCount, calculateMaxTabHeight(tabPlacement));
		new NonTitledBorder().paintBorder(tabPane, g, 0, y - 7, tabPane.getBounds().width, tabPane.getBounds().height - y + 7);
		Rectangle contentRect = new Rectangle(0, y - 7, tabPane.getBounds().width, tabPane.getBounds().height - y + 7);

		if (tabPane.getSelectedIndex() == -1)
			return;

		Rectangle rect = getTabBounds(tabPane, tabPane.getSelectedIndex());
		rect = rect.intersection(contentRect);
		g2d.setBackground(new Color(240,216,154));
		g2d.clearRect(rect.x + 4, rect.y, rect.width - 8, rect.height);
	}

	@Override
	protected void paintContentBorderTopEdge(final Graphics g, final int tabPlacement, final int selectedIndex, final int x, final int y, final int w, final int h)  {

	}

	@Override
	protected void paintContentBorderBottomEdge(final Graphics g, final int tabPlacement, final int selectedIndex, final int x, final int y, final int w, final int h)  {

	}

	@Override
	protected void paintContentBorderLeftEdge(final Graphics g, final int tabPlacement, final int selectedIndex, final int x, final int y, final int w, final int h)  {

	}

	@Override
	protected void paintContentBorderRightEdge(final Graphics g, final int tabPlacement, final int selectedIndex, final int x, final int y, final int w, final int h)  {

	}

	@Override
	protected int calculateTabHeight(final int tabPlacement, final int tabIndex, final int fontHeight) {
		return 35;
	}

	@Override
	protected Insets getContentBorderInsets(final int i) {
		return new Insets(0,10,10,10);
	}

	@Override
	protected int getBaselineOffset() {
		return 0;
	}

	@Override
	protected int calculateTabWidth(final int tabPlacement, final int tabIndex, final FontMetrics metrics)  {
		return 20 + metrics.stringWidth(tabPane.getTitleAt(tabIndex)) + (getIconForTab(tabIndex) == null ? 0 : getIconForTab(tabIndex).getIconWidth());
	}

	public int mouseOverTab() {
		return getRolloverTab();
	}

}
