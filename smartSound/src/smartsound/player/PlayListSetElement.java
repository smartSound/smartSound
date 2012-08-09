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

package smartsound.player;

import java.util.UUID;

import smartsound.common.Observable;
import smartsound.common.PropertyMap;

public abstract class PlayListSetElement extends Observable {

	public PlayListSetElement(final UUID uuid) {
		super(uuid);
	}

	public abstract void dispose();

	public abstract String getName();

	public abstract float getVolume();

	public abstract void setVolume(float volume);

	public abstract void setParentVolume(float volume);

	public abstract void play();

	public abstract boolean getAutoPlay();

	public abstract void setAutoPlay(boolean autoPlay);

	public abstract boolean isActive();

	public abstract void pause();

	public abstract void stop();

	public abstract void setName(String name);

	public abstract PropertyMap getPropertyMap();

	public abstract PlayListSet getPlayListSet(UUID playListSetUUID);

	public abstract UUID getParent(UUID child);

	public abstract void remove(UUID uuid);

}
