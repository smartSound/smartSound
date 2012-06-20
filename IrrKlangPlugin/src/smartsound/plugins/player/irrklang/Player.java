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

import smartsound.plugins.player.ISound;

public class Player implements smartsound.plugins.player.IPlayer {


	private IrrKlang_Sound jirrklang_sound; 
	private ISound sound;
	private int startTime;
	
	Player(IrrKlang_Sound jirrklang_sound, ISound sound) {
		this.jirrklang_sound = jirrklang_sound;
		this.sound = sound;
		this.startTime = sound.getStartTime();
		
		jirrklang_sound.setPlayPosition(startTime);
	}

	@Override
	public void play() {
		jirrklang_sound.setIsPaused(false);
	}

	@Override
	public void pause() {
		jirrklang_sound.setIsPaused(true);
	}

	@Override
	public boolean getPaused() {
		return jirrklang_sound.getIsPaused();
	}

	@Override
	public void stop() {
		//TODO Free ressources
		jirrklang_sound.stop();
	}

	@Override
	public float getVolume() {
		//TODO adjust scale
		return jirrklang_sound.getVolume();
	}

	@Override
	public void setVolume(float volume) {
		// TODO adjust scale
		jirrklang_sound.setVolume(volume);
	}

	@Override
	public float getPan() {
		//TODO adjust scale
		return jirrklang_sound.getPan();
	}

	@Override
	public void setPan(float pan) {
		// TODO adjust scale
		jirrklang_sound.setPan(pan);
	}

	@Override
	public boolean isFinished() {
		return jirrklang_sound.isFinished();
	}

	@Override
	public int getPlayPosition() {
		return jirrklang_sound.getPlayPosition() - startTime;
	}

	@Override
	public void setPlayPosition(int position) {
		jirrklang_sound.setPlayPosition(position + startTime);
	}

	@Override
	public float getPlaybackSpeed() {
		// TODO adjust scale
		return jirrklang_sound.getPlaybackSpeed();
	}

	@Override
	public void setPlaybackSpeed(float speed) {
		jirrklang_sound.setPlaybackSpeed(speed);
		
	}

	@Override
	public int getPlayLength() {
		return Math.min(jirrklang_sound.getPlayLength(), sound.getEndTime()) - startTime;
	}
	
	@Override
	public ISound getPlayListEntry() {
		return this.sound;
	}
}