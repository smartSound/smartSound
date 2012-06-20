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
import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;


interface IrrKlang extends Library{
	//Pointer ISound_getSoundSource(Pointer sound);

	void ISound_setIsPaused(Pointer sound, boolean paused);

	boolean ISound_getIsPaused(Pointer sound);

	void ISound_stop(Pointer sound);

	float ISound_getVolume(Pointer sound);

	void ISound_setVolume(Pointer sound, float volume);

	void ISound_setPan(Pointer sound, float pan);

	float ISound_getPan(Pointer sound);

	boolean ISound_isLooped(Pointer sound);

	void ISound_setIsLooped(Pointer sound, boolean looped);

	boolean ISound_isFinished(Pointer sound);

	void ISound_setMinDistance(Pointer sound, float min);

	float ISound_getMinDistance(Pointer sound);

	void ISound_setMaxDistance(Pointer sound, float max);

	float ISound_getMaxDistance(Pointer sound);

	//void ISound_setPosition(Pointer sound, Pointer position);

	//Pointer ISound_getPosition(Pointer sound);

	//void ISound_setVelocity(Pointer sound, Pointer vel);

	//Pointer ISound_getVelocity(Pointer sound);

	int ISound_getPlayPosition(Pointer sound);

	boolean ISound_setPlayPosition(Pointer sound, IntByReference pos);

	boolean ISound_setPlaybackSpeed(Pointer sound, float speed);

	float ISound_getPlaybackSpeed(Pointer sound);

	int ISound_getPlayLength(Pointer sound);

	Pointer ISound_getSoundEffectControl(Pointer sound);

	
	String ISoundEngine_getDriverName(Pointer engine);

	Pointer ISoundEngine_play2D(Pointer engine, String soundFileName, boolean playLooped, boolean startPaused, boolean track, int streamMode, boolean enableSoundEffects);

	Pointer ISoundEngine_play3D(Pointer engine,String soundFileName,Pointer pos, boolean playLooped, boolean startPaused, boolean track, int streamMode, boolean enableSoundEffects);

	void ISoundEngine_stopAllSounds(Pointer engine);

	void ISoundEngine_setAllSoundsPaused(Pointer engine, boolean bPaused);

	void ISoundEngine_setSoundVolume(Pointer engine, float volume);
	
	float ISoundEngine_getSoundVolume(Pointer engine);

	void ISoundEngine_setListenerPosition(Pointer engine, Pointer pos,Pointer lookdir, Pointer velPerSecond,Pointer upVector);

	void ISoundEngine_update(Pointer engine);

	boolean ISoundEngine_loadPlugins(Pointer engine, String path);

	Pointer createIrrKlangDevice();
}
