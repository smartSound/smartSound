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

import smartsound.plugins.player.IPlayer;
import smartsound.plugins.player.ISound;

public class SoundEngine extends smartsound.plugins.player.SoundEngine {

	IrrKlang_SoundEngine engine = new IrrKlang_SoundEngine(); 
	
	@Override
	public IPlayer getPlayer2D(ISound sound) {
		return new Player(engine.play2D(sound.getFilePath(), false, true, true, 0, true), sound);
	}

	@Override
	public IPlayer getPlayer3D(ISound sound) {
		//TODO: implement
		return null;
	}

	@Override
	public void setAllPlayersPaused(boolean paused) {
		engine.setAllSoundsPaused(paused);
	}

	@Override
	public void setMasterVolume(float volume) {
		engine.setSoundVolume(volume);
	}

	@Override
	public void stopAllPlayers() {
		engine.stopAllSounds();
	}

}
