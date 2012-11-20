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

package smartsound.controller;

import java.util.UUID;

import smartsound.common.IObserver;
import smartsound.player.PlayListSetElement;


/**
 * This abstract class is a facade to divide model and view.
 * @author André Becker
 *
 */
public abstract class AbstractController extends PlayListSetElement implements IObserver {

	public AbstractController() {
		super(null, null);
	}

	public void act(final UUID uuid, final String... actionTypes) {
		ElementManager.act(uuid, actionTypes);
	}

	public final UUID add(final UUID parent, final String elementType, final Object... params) {
		return ElementManager.add(parent, elementType, params);
	}

	public final void remove(final UUID uuid) {
		ElementManager.remove(uuid);
	}

	public NameValuePair[] get(final UUID uuid, final String... propertyNames) {
		return ElementManager.get(uuid, propertyNames);
	}

	public void set(final UUID uuid, final NameValuePair... params) {
		ElementManager.set(uuid, params);
	}

	/**
	 * Adds an observer to an <c>Observable</c>.
	 * @param observer The observer.
	 * @param uuid The <c>UUID</c> identifying the <c>Observable</c>.
	 */
	public abstract void addObserver(IObserver observer, UUID uuid);

	/**
	 * Saves the <c>PlayList</c>s to the specified path.
	 * @param savePath The path to the file to save.
	 */
	public abstract void save(String savePath);

	/**
	 * Loads the <c>PlayList</c>s from the specified path.
	 * @param loadPath The path to the file to load from.
	 */
	public abstract void load(String loadPath);
}
