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

public class IrrKlang_SoundEngine {

	private Pointer engine;
	private IrrKlang irrKlang;
	
	public IrrKlang_SoundEngine() {
		irrKlang = IrrKlangSingleton.getInstance();
		engine = irrKlang.createIrrKlangDevice();
		System.out.println(loadPlugins());
	}
	
	public String getDriverName() {
		return irrKlang.ISoundEngine_getDriverName(engine);
	}
	
	public IrrKlang_Sound play2D(String soundFileName, boolean playLooped, boolean startPaused, boolean track, int streamMode, boolean enableSoundEffects) {
		Pointer p = irrKlang.ISoundEngine_play2D(engine, soundFileName, playLooped, startPaused, track, streamMode, enableSoundEffects);
		if (p == Pointer.NULL) {
			return null;
		}
		return new IrrKlang_Sound(p);
	}
	
	public IrrKlang_Sound play3D(String soundFileName,Pointer pos, boolean playLooped, boolean startPaused, boolean track, int streamMode, boolean enableSoundEffects) {
		Pointer p = irrKlang.ISoundEngine_play3D(pos, soundFileName, pos, playLooped, startPaused, track, streamMode, enableSoundEffects);
		if (p == Pointer.NULL) {
			return null;
		}
		return new IrrKlang_Sound(p);
	}
	
	public void stopAllSounds() {
		irrKlang.ISoundEngine_stopAllSounds(engine);
	}
	
	public void setAllSoundsPaused(boolean bPaused) {
		irrKlang.ISoundEngine_setAllSoundsPaused(engine, bPaused);
	}
	
	public void setSoundVolume(float volume) {
		irrKlang.ISoundEngine_setSoundVolume(engine, volume);
	}
	
	public float getSoundVolume() {
		return irrKlang.ISoundEngine_getSoundVolume(engine);
	}
	
	public void update() {
		irrKlang.ISoundEngine_update(engine);
	}
	
	private boolean loadPlugins() {
		return irrKlang.ISoundEngine_loadPlugins(engine, IrrKlangSingleton.getLibraryDirectory());
	}
}
