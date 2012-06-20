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

public interface IPlayer {
	
	/**
	 * @return The <c>ISound</c> of the player.
	 */
	public ISound getPlayListEntry();
	
	/**
	 * Plays (or resumes) the sound.
	 */
	public void play();
	
	/**
	 * Pauses the sound.
	 */
	public void pause();
	
	/**
	 * @return <c>true</c> if the sound is paused.
	 */
	public boolean getPaused();
	
	/**
	 * Stops the sound and frees all resources used by it.
	 */
	public void stop();
	
	/**
	 * @return The current volume as value between 0 and 1.0.
	 */
	public float getVolume();
	
	/**
	 * Sets the current volume.
	 * @param volume The volume as value between 0 and 1.0.
	 */
	public void setVolume(float volume);
	
	/**
	 * @return The stereo balance as value between -1 and 1.
	 */
	public float getPan();
	
	/**
	 * Sets the pan.
	 * @param pan The balance as value between -1 and 1.
	 */
	public void setPan(float pan);
	
	/**
	 * @return <c>true</c> if the sound has stopped.
	 */
	public boolean isFinished();
	
	/**
	 * @return The current play position in milliseconds.
	 */
	public int getPlayPosition();
	
	/**
	 * Sets the current play position.
	 * @param position The play position in milliseconds.
	 */
	public void setPlayPosition(int position);
	
	/**
	 * @return The playback speed, where 1.0 equals the normal speed.
	 */
	public float getPlaybackSpeed();
	
	/**
	 * Sets the playback speed.
	 * @param speed The playback speed, 1.0 equals 100% of the normal speed.
	 */
	public void setPlaybackSpeed(float speed);
	
	/**
	 * @return The length of the sound in milliseconds.
	 */
	public int getPlayLength();
	
}
