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

import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class IconManager {

	private static final String iconDir = "images/";

	private IconManager() {}

	public static Image getImage(final IconType type) {
		String path = iconDir;
		switch(type) {
		case ADD:
			path += "icon_add_shadow.png";
			break;
		case ADD_PLAYLIST:
			path += "icon_add_playlist_shadow.png";
			break;
		case COPY:
			path += "icon_copy_shadow.png";
			break;
		case FAVORITE:
			path += "icon_fav_shadow.png";
			break;
		case HOTKEY:
			path += "icon_hotkeys_shadow.png";
			break;
		case LIBRARY:
			path += "icon_bib1_shadow.png";
			break;
		case LINK:
			path += "icon_link_shadow.png";
			break;
		case LOAD:
			path += "icon_load_shadow.png";
			break;
		case PASTE:
			path += "icon_paste_shadow.png";
			break;
		case PAUSE:
			path += "icon_pause_shadow.png";
			break;
		case PLAY:
			path += "icon_play_shadow.png";
			break;
		case REMOVE:
			path += "icon_remove_shadow.png";
			break;
		case REPEAT:
			path += "icon_repeat_shadow.png";
			break;
		case SAVE:
			path += "icon_save_shadow.png";
			break;
		case SETTINGS:
			path += "icon_settings_shadow.png";
			break;
		case STOP:
			path += "icon_stop_shadow.png";
			break;
		case VOLUME:
			path += "icon_volume_shadow.png";
			break;
		default:
			return null;
		}
		Image result;
		try {
			result = ImageIO.read(new File(path));
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}

		return result;
	}

	private static class WaiterThread extends Thread implements ImageObserver {
		boolean loaded = false;

		@Override
		public void run() {
			while (!loaded);
		}

		@Override
		public boolean imageUpdate(final Image arg0, final int infoflags, final int x, final int y,
				final int width, final int height) {
			if (loaded)
				return true;
			loaded = ((infoflags & ImageObserver.WIDTH) == ImageObserver.WIDTH)
					&& ((infoflags & ImageObserver.HEIGHT) == ImageObserver.HEIGHT);
			return loaded;
		}
	}

	public static enum IconType {
		ADD, ADD_PLAYLIST, COPY, FAVORITE, HOTKEY, LIBRARY, LINK, LOAD, PASTE, PAUSE, PLAY, REMOVE, REPEAT, SAVE, SETTINGS, STOP, VOLUME
	}
}
