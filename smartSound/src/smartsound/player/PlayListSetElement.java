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

import smartsound.common.IElement;
import smartsound.common.Observable;
import smartsound.common.PropertyMap;
import smartsound.controller.AbstractController;

public abstract class PlayListSetElement extends Observable implements IElement {

	protected PlayListSetElement parent;

	public PlayListSetElement(final UUID uuid, final PlayListSetElement parent) {
		super(uuid);
		this.parent = parent;
	}

	public abstract void dispose();

	protected abstract String getName();

	protected abstract float getVolume();

	protected abstract void setVolume(float volume);

	protected abstract void setParentVolume(float volume);

	public abstract void play();

	protected abstract boolean getAutoPlay();

	protected abstract void setAutoPlay(boolean autoPlay);

	protected abstract boolean isActive();

	public abstract void pause();

	public abstract void stop();

	protected abstract void setName(String name);

	public abstract PropertyMap getPropertyMap();

	protected abstract void notifyChangeObservers(String... propertyNames);


	@Override
	public final void act(final String... actionTypes) {
		for (String s : actionTypes) {
			act(s);
		}
	}

	protected void act(final String actionType) {
		switch(actionType) {
		case "PAUSE":
			pause();
			break;
		case "PLAY":
			play();
			break;
		case "STOP":
			stop();
			break;
		}
	}

	@Override
	public final NameValuePair[] get(final String... propertyNames) {
		NameValuePair[] result = new NameValuePair[propertyNames.length];

		for (int i = 0; i < propertyNames.length; i++) {
			result[i] = NameValuePair.create(propertyNames[i], get(propertyNames[i]));
		}

		return result;
	}

	protected Object get(final String propertyName) {
		switch (propertyName) {
		case "AUTOPLAY":
			return getAutoPlay();
		case "NAME":
			return getName();
		case "PARENT":
			return parent == null ? null : parent.getUUID();
		case "VOLUME":
			return getVolume();
		case "ACTIVE":
			return isActive();
		case "ROOTPARENT":
			return parent == null || (parent instanceof AbstractController) ? getUUID() : parent.get("ROOTPARENT");
		}
		return null;
	}

	@Override
	public final void set(final NameValuePair... params) {
		String[] propertyNames = new String[params.length];
		int i = 0;
		for (NameValuePair nvp : params) {
			propertyNames[i] = nvp.name;
			set(nvp.name, nvp.value);
		}
		notifyChangeObservers(propertyNames);
	}

	protected void set(final String propertyName, final Object value) {
		switch (propertyName) {
		case "AUTOPLAY":
			if (value instanceof Boolean)
				setAutoPlay((Boolean) value);
		case "NAME":
			if (value instanceof String)
				setName((String) value);
		case "PARENT":
			if (value instanceof PlayListSetElement)
				parent = (PlayListSetElement) value;
		case "VOLUME":
			if (value instanceof Float) {
				setVolume((Float) value);
			}
		}
	}

}
