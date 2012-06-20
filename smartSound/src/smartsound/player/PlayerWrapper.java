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

import smartsound.plugins.player.IPlayer;

/**
 * This class wraps an instance of <c>IPlayer</c>. It encapsules data needed
 * for fading.
 * @author André Becker
 *
 */
public class PlayerWrapper {
	
	private IPlayer player;
	private float volume;
	private float volumeFactor;
	private int fadeInBegin;
	private int fadeOutEnd;
	private boolean nextSoundStarted = false;
	private PlayerControllerStatus status = PlayerControllerStatus.STOPPED;
	
	/**
	 * @return The status of this particular player.
	 */
	public PlayerControllerStatus getStatus() {
		return status;
	}


	/**
	 * Sets the player's status.
	 * @param status The status.
	 */
	public void setStatus(PlayerControllerStatus status) {
		this.status = status;
	}


	/**
	 * Creates a new <c>PlayerWrapper</c>.
	 * @param player The player.
	 * @param volume The current volume for the player. This includes
	 * 	modification due to fading.
	 * @param volumeFactor The player's volume factor. This is used (i.e. not
	 * 	equal to 1) if volume randomization is used.
	 */
	public PlayerWrapper(IPlayer player, float volume, float volumeFactor) {
		this.player = player;
		this.volume = volume;
		this.volumeFactor = volumeFactor;
		
		this.fadeInBegin = 0;
		this.fadeOutEnd = player.getPlayLength();
	}


	/**
	 * @return The player's current volume.
	 */
	public float getVolume() {
		return volume;
	}

	/**
	 * Sets the player's current volume.
	 * @param volume The volume as a value between 0 and 1.0.
	 */
	public void setVolume(float volume) {
		this.volume = volume;
	}

	/**
	 * Gets the volume factor (used for volume randomization). The player's
	 * actual volume is determined by <c>getVolume()</c>*<c>getVolumeFactor()</c>.
	 * @return
	 */
	public float getVolumeFactor() {
		return volumeFactor;
	}

	/**
	 * Sets the volume factor for volume randomization.
	 * @param volumeFactor The volume factor.
	 */
	public void setVolumeFactor(float volumeFactor) {
		this.volumeFactor = volumeFactor;
	}

	/**
	 * @return The time (in milliseconds) when the fade in begins. The default
	 * 	value for this is 0.
	 */
	public int getFadeInBegin() {
		return fadeInBegin;
	}

	/**
	 * Sets the time when the fade in begins.
	 * @param fadeInBegin The fade in begin in milliseconds.
	 */
	public void setFadeInBegin(int fadeInBegin) {
		this.fadeInBegin = fadeInBegin;
	}

	/**
	 * @return The time (in milliseconds) when the fade out ends. The default
	 * 	value for this is <c>IPlayer.getPlayLength()</c>.
	 */
	public int getFadeOutEnd() {
		return fadeOutEnd;
	}

	/**
	 * Sets the time when the fade out ends.
	 * @param fadeOutEnd The fade out end in milliseconds.
	 */
	public void setFadeOutEnd(int fadeOutEnd) {
		this.fadeOutEnd = fadeOutEnd;
	}


	/**
	 * @return <c>true</c> if the next sound has already been started.
	 */
	public boolean isNextSoundStarted() {
		return nextSoundStarted;
	}

	/**
	 * @param nextSoundStarted Set to <c>true</c> if the next sound has already
	 * 	has been started.
	 */
	public void setNextSoundStarted(boolean nextSoundStarted) {
		this.nextSoundStarted = nextSoundStarted;
	}

	/**
	 * @return The wrapped instance of <c>IPlayer</c>.
	 */
	public IPlayer getPlayer() {
		return player;
	}
}
