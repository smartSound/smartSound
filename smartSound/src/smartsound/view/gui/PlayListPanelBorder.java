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
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import smartsound.common.IElement.NameValuePair;
import smartsound.view.gui.IconManager.IconType;

public class PlayListPanelBorder extends TitledBorder implements MouseListener,
MouseMotionListener, MouseWheelListener, IGUILadder, ListDataListener {

	private final IGUILadder parent;
	private final PlayListPanel panel;
	private float lastKnownVolume;
	protected boolean movedClockwise = false;
	private final UUID playListUUID;
	private boolean showSettings = false;

	private static final long serialVersionUID = 0xb06ba5eaf1981e0cL;
	private Image currentImageList[];
	private final Image imageList[];
	private final Image imageActiveList[];
	private final Image imageMouseOverList[];
	private final boolean activityList[];
	private boolean showVolume;
	private float volume;
	private boolean dragged = false;
	private int lastKnownIndex = -1;

	private static final int PLAY = 0;
	private static final int STOP = 1;
	private static final int REPEAT = 2;
	private static final int SETTINGS = 3;
	private static final int SPEAKER = 4;

	public PlayListPanelBorder(final IGUILadder parent, final UUID layoutUUID) {
		super("");
		this.parent = parent;
		this.panel = new PlayListPanel(this, layoutUUID);
		this.panel.setBorder(this);
		PlayListDataModel model = panel.getModel();
		super.setTitle(model.getTitle());
		playListUUID = model.getUUID();
		model.addListDataListener(this);

		imageList = new Image[5];
		imageList[PLAY] = resize(IconManager.getImage(IconType.PLAY));
		imageList[STOP] = resize(IconManager.getImage(IconType.STOP));
		imageList[REPEAT] = resize(IconManager.getImage(IconType.REPEAT));
		imageList[SETTINGS] = resize(IconManager.getImage(IconType.SETTINGS));
		imageList[SPEAKER] = resize(IconManager.getImage(IconType.VOLUME));

		imageActiveList = new Image[imageList.length];
		imageMouseOverList = new Image[imageList.length];
		currentImageList = Arrays.copyOf(imageList, imageList.length);

		BufferedImage tmp;
		for (int i = 0; i < imageList.length; i++) {
			tmp = new BufferedImage(imageList[i].getWidth(null),
					imageList[i].getHeight(null), BufferedImage.TYPE_INT_ARGB);
			tmp.createGraphics().drawImage(imageList[i], 0, 0, null);

			imageMouseOverList[i] = GlowUtils.addShadow(tmp, new Color(0xFF,0xFF,0xFF,0x80), 1, 3, true);
		}

		for (int i = 0; i < imageList.length; i++) {
			tmp = new BufferedImage(imageList[i].getWidth(null),
					imageList[i].getHeight(null), BufferedImage.TYPE_INT_ARGB);
			tmp.createGraphics().drawImage(imageList[i], 0, 0, null);

			imageActiveList[i] = GlowUtils.addGlow(tmp, new Color(0xDAA520), 4, 128, true);
		}

		activityList = new boolean[imageList.length];


		panel.addMouseMotionListener(this);
		panel.addMouseWheelListener(this);
		panel.addMouseListener(this);

		NameValuePair[] pairs = getGUIController().get(playListUUID, "VOLUME");
		assert pairs.length == 1;
		assert pairs[0].value instanceof Float;

		setVolume((Float) pairs[0].value);
		updateButtons();
		movedClockwise = volume > 0.5d;

		new Thread() {
			@Override
			public void run() {
				while (true) {
					if (lastKnownIndex > -1 && posToIconIndex(panel.getMousePosition()) == -1) {
						lastKnownIndex = -1;
						updateIcons();
					}
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {}
				}
			}
		}.start();
	}

	private Image resize(final Image image) {
		float ratio = getRatio(image);

		int realHeight = image.getHeight(null);
		int maxHeight = getBorderInsets(panel).top;
		realHeight = Math.min(realHeight, maxHeight);

		return image.getScaledInstance((int) (ratio * realHeight), realHeight, Image.SCALE_DEFAULT);
	}

	@Override
	public Insets getBorderInsets(final Component c) {
		Insets insets = super.getBorderInsets(c);
		return new Insets(insets.top, insets.left, insets.bottom, insets.right);
	}

	private Rectangle getImageRect(final int index, final int x, final int y, final int width,
			final int height) {
		if (index >= imageList.length)
			return null;

		Insets insets = getBorderInsets(panel);
		int maxHeight = insets.top;


		int leftBorder = (x + width) - insets.right / 2; //left side of the image rect
		int i = 0;
		for (i = imageList.length - 1; i >= index; i--) {
			Image img = imageList[i];
			leftBorder -= 10 + img.getWidth(null);
		}
		return new Rectangle(leftBorder, y + (maxHeight - imageList[index].getHeight(null)) / 2, imageList[index].getWidth(null), imageList[index].getHeight(null));
	}

	private float getRatio(final Image image) {
		return image.getWidth(null) / (float)  image.getHeight(null);
	}

	private Rectangle getTitleRect(final int x, final int y, final int width, final int height) {
		Rectangle firstRect = getImageRect(0, x, y, width, height);

		int dx = x + 17;
		Rectangle result = new Rectangle(dx, firstRect.y, firstRect.x - dx - 5, firstRect.height);
		return result;
	}

	private void setToolTips(final int currentIndex) {
		String toolTipText;
		switch(currentIndex) {
		case PLAY:
			toolTipText = "Start playing [Autoplay: " + (activityList[PLAY] ? "on" : "off") + "]";
			break;
		case STOP:
			toolTipText = "Stop";
			break;
		case REPEAT:
			toolTipText = "Repeat list [" + (activityList[REPEAT] ? "on" : "off") + "]";
			break;
		case SETTINGS:
			toolTipText = "Show settings";
			break;
		case SPEAKER:
			toolTipText = "Set volume [" + (int) (volume*100) + "%]";
			break;
		default:
			toolTipText = null;
		}
		panel.setToolTipText(toolTipText);
	}

	@Override
	public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int width,
			final int height) {
		getBorder().paintBorder(c, g, x, y, width, height);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setBackground(c.getBackground());
		Composite comp = g2d.getComposite();
		g2d.setComposite(AlphaComposite.getInstance(10, 0.2F));
		g2d.setComposite(comp);
		Font font = g2d.getFont();
		g2d.setFont(new Font(font.getName(), 1, 12));

		Rectangle imageRect;
		for (int i = imageList.length - 1; i >= 0; i--) {
			imageRect = getImageRect(i, x, y, width, height);
			g2d.drawImage(currentImageList[i], imageRect.x, imageRect.y, null);
			g2d.setComposite(comp);
		}

		Rectangle titleRect = getTitleRect(x, y, width, height);
		FontMetrics fm = g2d.getFontMetrics();

		String showTitle = title;
		if (titleRect.width <= 0) {
			showTitle = "...";
		} else {
			while (fm.stringWidth(showTitle) > titleRect.width) {
				if (showTitle.endsWith("...")) {
					showTitle = showTitle.substring(0, showTitle.length() - 3);
				}
				showTitle = showTitle.substring(0, showTitle.length() - 1);
				showTitle += "...";
				if (showTitle.length() == 3) {
					break;
				}
			}
		}

		int textY = ((titleRect.y + (titleRect.height) / 2) - (fm
				.getAscent() + fm.getDescent()) / 2) + fm.getAscent();
		g2d.drawString(showTitle, titleRect.x,
				textY);

		if (showVolume) {
			imageRect = getImageRect(imageList.length - 1, x, y,
					width, height);
			int min = Math.min(imageRect.width, imageRect.height);
			min = (min / 2) * 2 - 1;
			g2d.setColor(Color.LIGHT_GRAY);
			g2d.fillOval(imageRect.x, imageRect.y, min, min);
			g2d.setColor(Color.BLUE);
			g2d.fillArc(imageRect.x, imageRect.y, min, min, 90,
					(int) (-volume * 360D));
			g2d.setColor(Color.BLACK);
			g2d.drawOval(imageRect.x, imageRect.y, min, min);
			g2d.setColor(Color.WHITE);
			String percentage = String.valueOf((int) (volume * 100D));
			font = g2d.getFont();
			g2d.setFont(new Font(font.getName(), font.getStyle(), 10));
			fm = g2d.getFontMetrics();
			int stringWidth = fm.stringWidth(percentage);
			textY = ((imageRect.y + ((imageRect.y + min + 1) - imageRect.y) / 2) - (fm
					.getAscent() + fm.getDescent()) / 2) + fm.getAscent();
			g2d.drawString(percentage, imageRect.x + (min - stringWidth) / 2,
					textY);
		}
	}

	public int posToIconIndex(final Point pos) {
		if (pos == null)
			return -1;
		int x = 0, y = 0;
		int width = panel.getWidth();
		int height = panel.getHeight();
		for (int i = 0; i < imageList.length; i++) {
			Rectangle rect = getImageRect(i, x, y, width, height);
			if (rect != null && rect.contains(pos))
				return i;
		}

		return -1;
	}

	public void setActive(final int index, final boolean active) {
		if (index >= activityList.length) {
			return;
		} else {
			activityList[index] = active;
			return;
		}
	}

	public void setShowVolume(final boolean show) {
		showVolume = show;
		panel.repaint();
	}

	public void setVolume(final float volume) {
		this.volume = volume;
		lastKnownVolume = volume;
	}

	public int posToAngle(final Point point, final int x, final int y, final int width, final int height) {
		Rectangle imageRect = getImageRect(imageList.length - 1, x, y, width,
				height);
		int imageCenterX = imageRect.x + imageRect.width / 2;
		int imageCenterY = imageRect.y + imageRect.height / 2;
		double theta = Math.atan2(point.y - imageCenterY, point.x
				- imageCenterX);
		for (theta += 1.5707963267948966D; theta < 0.0D; theta += 6.2831853071795862D);
		return (int) Math.toDegrees(theta);
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		if (posToIconIndex(e.getPoint()) < 0 && e.getClickCount() == 2) {
			getGUIController().act(playListUUID, "PLAY");
		}
	}

	@Override
	public void mouseEntered(final MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(final MouseEvent arg0) {
		panel.setCursor(Cursor.getDefaultCursor());
		setShowVolume(false);
	}

	@Override
	public void mousePressed(final MouseEvent e) {
		PlayListPanel panel = (PlayListPanel) e.getSource();
		int index = posToIconIndex(e.getPoint());
		if (index == 4 && e.getButton() == MouseEvent.BUTTON1)
			panel.draggingSpeaker = true;
		dragged = false;
		if (e.isPopupTrigger()) {
			showPopup(e);
		}
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
		PlayListPanel panel = (PlayListPanel) e.getSource();
		panel.draggingSpeaker = false;

		if (!dragged) {
			int index = posToIconIndex(e.getPoint());
			if (posToIconIndex(e.getPoint()) != -1
					&& e.getButton() == MouseEvent.BUTTON1) {
				if (index == PLAY)
					getGUIController().act(playListUUID, "PLAY");
				else if (index == STOP)
					getGUIController().act(playListUUID, "STOP");
				else if (index == REPEAT) {
					NameValuePair[] pairs = getGUIController().get(playListUUID, "REPEAT");
					assert pairs.length == 1;
					assert (pairs[0].value instanceof Boolean);

					getGUIController().set(playListUUID, "REPEAT", !(Boolean) pairs[0].value);
				}
				else if (index == SETTINGS) {
					((CardLayout) panel.getLayout()).next(panel);
					showSettings = !showSettings;
					updateButtons();
				}
			} else if (e.isPopupTrigger()) {
				showPopup(e);
			}
			SwingUtilities.getWindowAncestor(panel).requestFocus();
		}
	}

	private void showPopup(final MouseEvent e) {
		JPopupMenu popup = new JPopupMenu();
		JMenu hotkeyMenu = new JMenu("Hotkeys");
		hotkeyMenu.setIcon(new ImageIcon(IconManager.getImage(IconType.HOTKEY)));
		popup.add(hotkeyMenu);
		int index = posToIconIndex(e.getPoint());
		String description = null;
		hotkeyMenu.add(new TitledSeparator("Add hotkeys", false));
		description = "Play";
		hotkeyMenu.add(new AddMenuItem(new AbstractAction(description) {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				Window wnd = SwingUtilities.getWindowAncestor(panel);
				HotkeyDialog dialog = new HotkeyDialog(wnd);
				KeyEvent event = dialog.getEvent();
				if (event.getKeyCode() == KeyEvent.VK_ESCAPE)
					return;

				parent.getGUIController().addActHotkey(event, playListUUID, "PLAY");
				wnd.toFront();
			}

		}));
		description = "Stop";
		hotkeyMenu.add(new AddMenuItem(new AbstractAction(description) {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				Window wnd = SwingUtilities.getWindowAncestor(panel);
				HotkeyDialog dialog = new HotkeyDialog(wnd);
				KeyEvent event = dialog.getEvent();
				if (event.getKeyCode() == KeyEvent.VK_ESCAPE)
					return;
				parent.getGUIController().addActHotkey(event, playListUUID, "STOP");
				wnd.toFront();
			}

		}));
		description = "Set repeat";
		hotkeyMenu.add(new AddMenuItem(new AbstractAction(description) {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				Window wnd = SwingUtilities.getWindowAncestor(panel);
				HotkeyDialog dialog = new HotkeyDialog(wnd);
				KeyEvent event = dialog.getEvent();
				if (event.getKeyCode() == KeyEvent.VK_ESCAPE)
					return;
				Object[] options = { true, false };
				String result = (String) UserInput.getInput(panel,
						"Turn on", "Turn off");
				Map<String, Object> values = new HashMap<String, Object>();
				values.put("REPEAT", result.equals("Turn on"));
				parent.getGUIController().addSetHotkey(event, playListUUID, values);
				wnd.toFront();
			}

		}));

		description = "Set volume";
		hotkeyMenu.add(new AddMenuItem(new AbstractAction(description) {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				Window wnd = SwingUtilities.getWindowAncestor(panel);
				HotkeyDialog dialog = new HotkeyDialog(wnd);
				KeyEvent event = dialog.getEvent();
				if (event.getKeyCode() == KeyEvent.VK_ESCAPE)
					return;
				NameValuePair[] pairs = getGUIController().get(playListUUID, "VOLUME");
				assert pairs.length == 1;
				assert pairs[0].value instanceof Float;

				Double result = UserInput
						.getInput(panel, 0, 100, 1, (Float) pairs[0].value * 100);
				float volume;
				try {
					volume = result.floatValue() / 100.0f;
					volume = Math.max(volume, 0);
					volume = Math.min(volume, 100);
				} catch (NumberFormatException e) {
					return;
				}
				Map<String, Object> values = new HashMap<String, Object>();
				values.put("VOLUME", (100*volume));
				parent.getGUIController().addSetHotkey(event, playListUUID, values);
				wnd.toFront();
			}
		}));

		String itemTitle;
		List<JMenuItem> menuItemList = new LinkedList<JMenuItem>();
		/*
		for (Tuple<String, Action> tuple : getGUIController().getHotkeys(
				playAction)) {
			itemTitle = tuple.second.getDescription();
			menuItemList.add(new RemoveHotkeyMenuItem(tuple.second, itemTitle,
					tuple.first, playListUUID, getGUIController()));
		}

		for (Tuple<String, Action> tuple : getGUIController().getHotkeys(
				stopAction)) {
			itemTitle = tuple.second.getDescription();
			menuItemList.add(new RemoveHotkeyMenuItem(tuple.second, itemTitle,
					tuple.first, playListUUID, getGUIController()));
		}

		for (Tuple<String, Action> tuple : getGUIController().getHotkeys(
				repeatListAction)) {
			itemTitle = tuple.second.getDescription();
			menuItemList.add(new RemoveHotkeyMenuItem(tuple.second, itemTitle,
					tuple.first, playListUUID, getGUIController()));
		}

		for (Tuple<String, Action> tuple : getGUIController().getHotkeys(
				volumeAction)) {
			itemTitle = tuple.second.getDescription();
			menuItemList.add(new RemoveHotkeyMenuItem(tuple.second, itemTitle,
					tuple.first, playListUUID, getGUIController()));
		}
		 */

		if (!menuItemList.isEmpty()) {
			hotkeyMenu.add(new TitledSeparator("Remove hotkeys", false));
		}

		for (JMenuItem item : menuItemList) {
			hotkeyMenu.add(item);
		}

		if (hotkeyMenu.getItemCount() == 0) {
			hotkeyMenu.setEnabled(false);
		}

		propagatePopupMenu(popup, e);
	}

	@Override
	public void mouseDragged(final MouseEvent e) {
		PlayListPanel panel = (PlayListPanel) e.getSource();
		dragged = true;
		if (!panel.draggingSpeaker)
			return;
		int degrees = panel.posToAngle(e.getPoint());
		float percentage = (float) (degrees / 360D);
		if (percentage < 0.25D && movedClockwise)
			percentage = 1.0F;
		else if (percentage > 0.75D && !movedClockwise)
			percentage = 0.0F;
		else
			movedClockwise = percentage > 0.5F;

			setShowVolume(true);
			updateVolume(percentage);
			panel.repaint();
	}

	@Override
	public void mouseMoved(final MouseEvent e) {
		setShowVolume(false);
		PlayListPanel panel = (PlayListPanel) e.getSource();
		int index = posToIconIndex(e.getPoint());
		if (index == -1) {
			panel.setCursor(Cursor.getDefaultCursor());
		} else {
			panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			if (index == 4)
				setShowVolume(true);
		}
		updateIcons();
		panel.repaint();
	}

	private void updateIcons() {
		Point p = panel.getMousePosition();
		lastKnownIndex = posToIconIndex(p);

		currentImageList = Arrays.copyOf(imageList, imageList.length);
		if (lastKnownIndex > -1)
			currentImageList[lastKnownIndex] = imageMouseOverList[lastKnownIndex];

		for (int i = 0; i < imageList.length; i++)
			if (activityList[i])
				currentImageList[i] = imageActiveList[i];

		setToolTips(lastKnownIndex);
		panel.repaint();
	}

	@Override
	public void mouseWheelMoved(final MouseWheelEvent e) {
		PlayListPanel panel = (PlayListPanel) e.getSource();
		if (posToIconIndex(e.getPoint()) != 4) {
			return;
		} else {
			float newVolume = lastKnownVolume;
			newVolume += 0.02F * e.getUnitsToScroll();
			newVolume = Math.max(0.0F, newVolume);
			newVolume = Math.min(1.0F, newVolume);
			setShowVolume(true);
			updateVolume(newVolume);
			return;
		}
	}

	public void updateVolume(final float volume) {
		lastKnownVolume = volume;
		getGUIController().set(playListUUID, "VOLUME", volume);
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
	public void propagateHotkey(final KeyEvent event) {
		parent.propagateHotkey(event);
	}

	@Override
	public void propagatePopupMenu(final JPopupMenu menu, final MouseEvent e) {
		JCheckBoxMenuItem autoPlayItem = new JCheckBoxMenuItem(new AbstractAction("Autoplay", new ImageIcon(IconManager.getImage(IconType.PLAY))) {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				NameValuePair[] pairs = getGUIController().get(playListUUID, "AUTOPLAY");
				assert pairs.length == 1;
				assert pairs[0].value instanceof Boolean;

				NameValuePair[] setPair = new NameValuePair[1];
				setPair[0] =  NameValuePair.create("AUTOPLAY", !(Boolean) pairs[0].value);

				getGUIController().set(playListUUID, setPair);
			}
		});
		NameValuePair[] pairs = getGUIController().get(playListUUID, "AUTOPLAY");
		assert pairs.length == 1;
		assert pairs[0].value instanceof Boolean;

		autoPlayItem.setSelected((Boolean) pairs[0].value);
		menu.add(autoPlayItem);

		menu.addSeparator();

		menu.add(new AbstractAction("Add file", new ImageIcon(IconManager.getImage(IconType.ADD))) {
			@Override
			public void actionPerformed(final ActionEvent e) {
				getGUIController().addFiles(playListUUID);
			}
		});

		menu.add(new AbstractAction("Add directory", new ImageIcon(IconManager.getImage(IconType.ADD))) {
			@Override
			public void actionPerformed(final ActionEvent e) {
				getGUIController().addDirectory(playListUUID);
			}
		});

		menu.addSeparator();

		menu.add(new AbstractAction("Remove playlist", new ImageIcon(IconManager.getImage(IconType.REMOVE))) {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				getGUIController().remove(playListUUID);
			}
		});

		menu.add(new AbstractAction("Rename playlist") {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				StringInputDialog dialog = new StringInputDialog(SwingUtilities.getWindowAncestor(panel), "Choose a new name for the playlist and press Enter", title);
				dialog.setVisible(true);
				String input = dialog.getTextInput();

				NameValuePair[] setPair = new NameValuePair[1];
				setPair[0].value = NameValuePair.create("TITLE", input);
				getGUIController().set(playListUUID, setPair);
			}
		});



		parent.propagatePopupMenu(menu, e);
	}

	private void updateButtons() {

		NameValuePair[] pairs = new NameValuePair[4];
		pairs = getGUIController().get(playListUUID, "AUTOPLAY", "REPEAT", "VOLUME", "NAME");
		assert pairs.length == 4;
		assert pairs[0].value instanceof Boolean;
		assert pairs[1].value instanceof Boolean;
		assert pairs[2].value instanceof Float;
		assert pairs[3].value instanceof String;

		setActive(PLAY, (Boolean) pairs[0].value);
		setActive(STOP, false);
		setActive(REPEAT, (Boolean) pairs[1].value);
		setActive(SETTINGS, showSettings);
		setActive(SPEAKER, false);
		setVolume((Float) pairs[2].value);
		title = (String) pairs[3].value;

		updateIcons();
	}

	@Override
	public void contentsChanged(final ListDataEvent arg0) {
		updateButtons();
	}

	@Override
	public void intervalAdded(final ListDataEvent arg0) {
	}

	@Override
	public void intervalRemoved(final ListDataEvent arg0) {
	}

	@Override
	public void updateMinimumSize() {
		parent.updateMinimumSize();
	}

	public void movedTo(final PlayListPanel playListPanel, final int x, final int y) {
		((PlayListSetPanel) parent).movedTo(playListPanel, x, y);
	}
}
