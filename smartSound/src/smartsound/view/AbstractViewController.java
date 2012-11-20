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

package smartsound.view;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import smartsound.common.IElement.NameValuePair;
import smartsound.common.IObserver;
import smartsound.common.PropertyMap;
import smartsound.common.Tuple;
import smartsound.player.LoadingException;
import smartsound.view.gui.GUIController;

public abstract class AbstractViewController
{

	public AbstractViewController()
	{
	}

	public abstract UUID add(final UUID parent, final String elementType, final Object... params);

	public abstract void remove(final UUID uuid);

	public abstract NameValuePair[] get(final UUID uuid, final String... propertyNames);

	public abstract void set(final UUID uuid, final NameValuePair... params);

	public abstract void act(final UUID uuid, final String... actionTypes);

	public abstract void addObserver(IObserver observer, UUID observableUUID);

	public abstract List<Tuple<String,Action>> getHotkeys(UUID sceneUUID, Action parent);

	public abstract Action getSaveAction();

	public abstract Action getLoadAction();

	public abstract void addGUI(GUIController guicontroller);

	public abstract void addSetHotkey(String hotkey, UUID elementUUID, Map<String, Object> values);

	public abstract void addActHotkey(String hotkey, UUID elementUUID, String... actions);

	public abstract void executeHotkey(String s);

	public abstract void removeAllHotkeys();

	public abstract void removeHotkey(UUID elementUUID, String hotkey, Action action);

	public abstract PropertyMap getPropertyMap();

	public abstract void loadFromPropertyMap(PropertyMap pMap) throws LoadingException;

	public abstract String getHotkey(Action action);

	//	public abstract void removeHotkey(Action action);

	public abstract void reloadView();

	public enum PositionType {
		ABOVE, LEFT
	}

	public abstract List<Tuple<String, Action>> getHotkeys(UUID sceneUUID);

	public abstract List<Tuple<Action, String>> getHotkeyComments();

	public abstract void setHotkeyDescription(Action action, String description);
}
