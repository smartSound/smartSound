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

import java.util.UUID;

public class ActHotkey extends Hotkey {

	private final String[] actionTypes;

	protected ActHotkey(final String hotkey, final UUID elementUUID, final String... actionTypes) {
		super(hotkey, elementUUID);
		this.actionTypes = actionTypes;
	}

	@Override
	public void executeHotkey(final AbstractViewController controller) {
		controller.act(getElementUUID(), actionTypes);
	}

}
