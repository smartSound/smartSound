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

package smartsound.plugins.player;

import smartsound.common.PropertyMap;

/**
 * This interface represents a sound that can be played by an instance of
 * <c>IPlayer</c>.
 * @author André Becker
 *
 */
public interface ISound {

	/**
	 * @return The path to the sound file.
	 */
	public String getFilePath();
	
	/**
	 * @return The start time of the sound.
	 */
	public int getStartTime();
	
	/**
	 * Sets the sounds start time.
	 * @param startTime The start time in milliseconds.
	 */
	public void setStartTime(int startTime);
	
	/**
	 * @return The end time of the sound, meaning the time when the sound stops
	 * playing.
	 */
	public int getEndTime();
	
	/**
	 * Sets the end time of the sound
	 * @param endTime The end time in milliseconds.
	 */
	public void setEndTime(int endTime);

	/**
	 * @return A <c>PropertyMap</c> which can be used to restore this object.
	 */
	public PropertyMap getPropertyMap();
}
