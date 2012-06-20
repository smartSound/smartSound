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

/**
 * This class encapsules all settings for a <c>PlayList</c>.
 * @author André Becker
 *
 */
public class PlayerControllerSettings {
	private int fadeInLength;
	private int fadeOutLength;
	private int overlapTime;
	private float maxVolume = 0.5f;
	private float randomizeVolumeFrom = 1;
	private float randomizeVolumeTo = 1;
	
	public PlayerControllerSettings(int fadeInLength, int fadeOutLength, int overlapTime) {
		this.fadeInLength = fadeInLength;
		this.fadeOutLength = fadeOutLength;
		this.overlapTime = overlapTime;
	}
	
	public PlayerControllerSettings(int fadeInLength, int fadeOutLength, int overlapTime, float randomizeVolumeFrom, float randomizeVolumeTo) {
		this(fadeInLength, fadeOutLength, overlapTime);
		this.randomizeVolumeFrom = randomizeVolumeFrom;
		this.randomizeVolumeTo = randomizeVolumeTo;
	}

	public PlayerControllerSettings() {
		this(0,0,0,1,1);
	}

	/**
	 * Gets the minimum volume for this specific <c>PlayList</c>. The actual
	 * volume of a played entry is chosen uniformly from the interval
	 * [<c>randomizeVolumeFrom</c>, <c>randomizeVolumeTo</c>].
	 * @return The minimum volume in the range from 0 to 1.0.
	 */
	public float getRandomizeVolumeFrom() {
		return randomizeVolumeFrom;
	}

	/**
	 * Sets the minimum volume for this specific <c>PlayList</c>. The actual
	 * volume of a played entry is chosen uniformly from the interval
	 * [<c>randomizeVolumeFrom</c>, <c>randomizeVolumeTo</c>].
	 * @param from The minimum volume in the range from 0 to 1.0.
	 */
	public void setRandomizeVolumeFrom(float randomizeVolumeFrom) {
		this.randomizeVolumeFrom = randomizeVolumeFrom;
		randomizeVolumeTo = Math.max(randomizeVolumeFrom, randomizeVolumeTo);
	}

	/**
	 * Gets the maximum volume for this specific <c>PlayList</c>. The actual
	 * volume of a played entry is chosen uniformly from the interval
	 * [<c>randomizeVolumeFrom</c>, <c>randomizeVolumeTo</c>].
	 * @return The maximum volume in the range from 0 to 1.0.
	 */
	public float getRandomizeVolumeTo() {
		return randomizeVolumeTo;
	}

	/**
	 * Sets the maximum volume for this specific <c>PlayList</c>. The actual
	 * volume of a played entry is chosen uniformly from the interval
	 * [<c>randomizeVolumeFrom</c>, <c>randomizeVolumeTo</c>].
	 * @param to The maximum volume in the range from 0 to 1.0.
	 */
	public void setRandomizeVolumeTo(float randomizeVolumeTo) {
		this.randomizeVolumeTo = randomizeVolumeTo;
		randomizeVolumeFrom = Math.min(randomizeVolumeFrom, randomizeVolumeTo);
	}

	/**
	 * Gets the time sounds are faded in.
	 * @result The fade in time in milliseconds. 
	 */
	public int getFadeInLength() {
		return fadeInLength;
	}

	/**
	 * Sets the time sounds are faded in.
	 * @param fadeIn The fade in time in milliseconds. 
	 */
	public void setFadeInLength(int fadeInLength) {
		this.fadeInLength = fadeInLength;
	}

	/**
	 * Gets the time sounds are faded out.
	 * @result The fade out time in milliseconds.
	 */
	public int getFadeOutLength() {
		return fadeOutLength;
	}

	/**
	 * Sets the time sounds are faded out.
	 * @param fadeOut The fade out time in milliseconds.
	 */
	public void setFadeOutLength(int fadeOutLength) {
		this.fadeOutLength = fadeOutLength;
	}

	/**
	 * Gets the time for overlapping. If <c>overlap</c> is set to a value n,
	 * then the last n milliseconds of one sound are played simoultaneously
	 * with the first n milliseconds of the next one.
	 * @result The overlap time.
	 */
	public int getOverlapTime() {
		return overlapTime;
	}

	/**
	 * Sets the time for overlapping. If <c>overlap</c> is set to a value n,
	 * then the last n milliseconds of one sound are played simoultaneously
	 * with the first n milliseconds of the next one.
	 * @param overlap The overlap time.
	 */
	public void setOverlapTime(int overlapTime) {
		this.overlapTime = overlapTime;
	}

	/**
	 * Gets the volume for this specific <c>PlayList</c>.
	 * @result The volume, as a value between 0 and 1.0.
	 */
	public float getVolume() {
		return maxVolume;
	}

	/**
	 * Sets the volume for this specific <c>PlayList</c>.
	 * @param volume The volume, as a value between 0 and 1.0.
	 */
	public void setVolume(float maxVolume) {
		this.maxVolume = maxVolume;
	}
	
}
