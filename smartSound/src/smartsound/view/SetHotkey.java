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

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import smartsound.common.IElement.NameValuePair;

public class SetHotkey extends Hotkey {

	private final Map<String, Object> values;

	protected SetHotkey(final String hotkey, final UUID elementUUID, final Map<String, Object> values) {
		super(hotkey, elementUUID);
		this.values = values;
	}

	@Override
	public void executeHotkey(final AbstractViewController controller) {
		NameValuePair[] pairs = new NameValuePair[values.size()];
		int i = 0;
		for (Entry<String, Object> entry : values.entrySet()) {
			pairs[i++] = NameValuePair.create(entry.getKey(), entry.getValue());
		}
		controller.set(getElementUUID(), pairs);
	}

}
