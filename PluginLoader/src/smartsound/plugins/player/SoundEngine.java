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

import smartsound.plugins.player.IPlayer;
import smartsound.plugins.player.ISound;

/**
 * Each sound engine plugin must implement a subclass of <c>SoundEngine<c>.
 * This class is used to retrieve instances of <c>IPlayer</c> 
 * @author André Becker
 *
 */
public abstract class SoundEngine {
	
	/**
	 * Retrieves an instance of <c>IPlayer</c> to play 2D sounds.
	 * @param entry An instance of <c>ISound</c> to be played. 
	 * @return The <c>IPlayer</c> instance.
	 */
	public abstract IPlayer getPlayer2D(ISound entry);
	
	/**
	 * Retrieves an instance of <c>IPlayer</c> to play 3D sounds.
	 * @param entry An instance of <c>ISound</c> to be played. 
	 * @return The <c>IPlayer</c> instance.
	 */
	public abstract IPlayer getPlayer3D(ISound entry);
	
	/**
	 * Sets the master volume. This MAY be the system volume.
	 * @param volume The master volume as value between 0 and 1.0.
	 */
	public abstract void setMasterVolume(float volume);
	
	/**
	 * Stops all players.
	 */
	public abstract void stopAllPlayers();
	
	/**
	 * Pauses (or resumes) all players.
	 * @param paused If <c>true</c> all players are paused, if <c>false</c>
	 * 	all paused players are resumed.
	 */
	public abstract void setAllPlayersPaused(boolean paused);
}
