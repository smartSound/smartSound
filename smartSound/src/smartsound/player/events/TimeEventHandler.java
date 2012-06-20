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

package smartsound.player.events;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author André Becker
 *
 */
public class TimeEventHandler extends Thread {

	private static TimeEventHandler instance;

	private Set<TimeEventTuple> listenerSet = Collections
			.synchronizedSet(new HashSet<TimeEventTuple>());
	private List<TimeEventTuple> addList = new LinkedList<TimeEventTuple>();
	private volatile boolean abort = false;

	private TimeEventHandler() {
	}

	public static void add(ITimeEventListener listener, Object obj) {
		if (instance == null) {
			instance = new TimeEventHandler();
			instance.start();
		}
		synchronized (instance) {
			instance.addList.add(new TimeEventTuple(listener, obj));
		}
	}

	private static void remove(ITimeEventListener listener, Object obj) {
		if (instance != null) {
			synchronized (instance) {
				for (TimeEventTuple tuple : instance.listenerSet) {
					if (listener == tuple.getListener() && obj == tuple.getObj()) {
						instance.listenerSet.remove(tuple);
						break;
					}
				}
				
				/*if (instance.listenerSet.isEmpty()) {
					instance.abort = true;
					instance.interrupt();
					instance = null;
				}*/
			}
		}
	}

	@Override
	public void run() {
		long startTime;
		long sleepTime;
		List<TimeEventTuple> removeList = new LinkedList<TimeEventTuple>();
		while (!abort) {
			try {
				startTime = System.currentTimeMillis();
				synchronized (instance) {
					for (TimeEventTuple tuple : listenerSet) {
						if (!tuple.getListener().receiveTimeEvent(System.currentTimeMillis(), tuple.getObj())) {
							removeList.add(tuple);
						}
					}
					while (!addList.isEmpty()) {
						listenerSet.add(addList.remove(0));
					}
					for (TimeEventTuple tuple : removeList) {
						remove(tuple.getListener(), tuple.getObj());
					}
					removeList.clear();
				}
				// TODO comment
				sleepTime = 100 - System.currentTimeMillis() + startTime;
				if (sleepTime > 0) {
					sleep(sleepTime);
				}
			} catch (InterruptedException e) {
			}
		}
	}
}
