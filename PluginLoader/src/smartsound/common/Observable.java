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

package smartsound.common;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public abstract class Observable {
	private final List<IObserver> observers = new LinkedList<IObserver>();
	private final UUID uuid;

	public Observable(final UUID uuid) {
		this.uuid = uuid;
	}

	public void addObserver(final IObserver observer) {
		if (!observers.contains(observer)) {
			observers.add(observer);
		}
	}

	public void removeObserver(final IObserver observer) {
		observers.remove(observer);
	}

	public UUID getUUID() {
		return uuid;
	}

	public void update() {
		for (IObserver observer : observers) {
			observer.update(getUUID());
		}
	}

	protected void removeAllObservers() {
		observers.clear();
	}
}
