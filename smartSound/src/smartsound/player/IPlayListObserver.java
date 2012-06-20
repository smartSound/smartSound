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

/**
 * This interface describes an observer for a <c>PlayList</c>.
 * @author André Becker
 *
 */
public interface IPlayListObserver {
	
	/**
	 * This method is called by a <c>PlayList</c> on each subscribed
	 * <c>IPlayListObserver</c> when the <c>PlayList</c> has changed.
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 */
	public void playListChanged(UUID playListUUID);
}
