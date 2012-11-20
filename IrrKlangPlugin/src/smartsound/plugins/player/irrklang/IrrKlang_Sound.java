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

package smartsound.plugins.player.irrklang;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public class IrrKlang_Sound {

	private final Pointer sound;
	private final IrrKlang irrKlang;
	
	IrrKlang_Sound(final Pointer sound) {
		irrKlang = IrrKlangSingleton.getInstance();
		this.sound = sound;
	}
	
	public void setIsPaused(final boolean paused) {
		irrKlang.ISound_setIsPaused(sound, paused);
	}
	
	public boolean getIsPaused() {
		return irrKlang.ISound_getIsPaused(sound);
	}
	
	public void stop() {
		irrKlang.ISound_stop(sound);
	}
	
	public float getVolume() {
		return irrKlang.ISound_getVolume(sound);
	}
	
	public void setVolume(final float volume) {
		irrKlang.ISound_setVolume(sound, volume);
	}
	
	public void setPan(final float pan) {
		irrKlang.ISound_setPan(sound, pan);
	}
	
	public float getPan() {
		return irrKlang.ISound_getPan(sound);
	}
	
	public boolean isLooped() {
		return irrKlang.ISound_isLooped(sound);
	}
	
	public void setIsLooped(final boolean looped) {
		irrKlang.ISound_setIsLooped(sound, looped);
	}
	
	public boolean isFinished() {
		return irrKlang.ISound_isFinished(sound);
	}
	
	public void setMinDistance(final float min) {
		irrKlang.ISound_setMinDistance(sound, min);
	}
	
	public float getMinDistance() {
		return irrKlang.ISound_getMinDistance(sound);
	}
	
	public void setMaxDistance(final float max) {
		irrKlang.ISound_setMaxDistance(sound, max);
	}
	
	public float getMaxDistance() {
		return irrKlang.ISound_getMaxDistance(sound);
	}
	
	public int getPlayPosition() {
		return irrKlang.ISound_getPlayPosition(sound);
	}
	
	public boolean setPlayPosition(final int pos) {
		return irrKlang.ISound_setPlayPosition(sound, new IntByReference(pos));
	}
	
	public boolean setPlaybackSpeed(final float speed) {
		return irrKlang.ISound_setPlaybackSpeed(sound, speed);
	}
	
	public float getPlaybackSpeed()  {
		return irrKlang.ISound_getPlaybackSpeed(sound);
	}
	
	public int getPlayLength() {
		return irrKlang.ISound_getPlayLength(sound);
	}
	
	public IrrKlang_SoundEffectControl getSoundEffectControl() {
		Pointer p = irrKlang.ISound_getSoundEffectControl(sound);
		return new IrrKlang_SoundEffectControl(p);
	}

}