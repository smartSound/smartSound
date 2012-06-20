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

/**
 * A class implementing this interface can be informed by time
 * events.
 * @author André Becker
 *
 */
public interface ITimeEventListener {
	
	/**
	 * Process the time event.
	 * @param currentTime The current system time in milliseconds.
	 * @param obj An optional object that has been passed when the
	 * 	<c>ITimeEventListener</c> subscribed.
	 * @return <c>false</c> if the <c>ITImeEventListener</c> wishes
	 * 	unsubscribe from the <c>TimeEventHandler</c>.
	 */
	public boolean receiveTimeEvent(long currentTime, Object obj);
}
