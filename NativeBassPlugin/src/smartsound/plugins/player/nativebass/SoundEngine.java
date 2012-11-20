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

package smartsound.plugins.player.nativebass;

import static jouvieje.bass.Bass.BASS_Init;
import static jouvieje.bass.Bass.BASS_StreamCreateFile;
import jouvieje.bass.Bass;
import jouvieje.bass.BassInit;
import jouvieje.bass.structures.HSTREAM;
import smartsound.plugins.player.IPlayer;
import smartsound.plugins.player.ISound;

public class SoundEngine extends smartsound.plugins.player.SoundEngine {

	public SoundEngine() {
		System.setProperty("java.library.path", PluginLoader.getLibraryDirectory());
		BassInit.loadLibraries();
		BASS_Init(-1, 44100, 0, null, null);
	}

	@Override
	public IPlayer getPlayer2D(final ISound sound) {
		HSTREAM hstream = BASS_StreamCreateFile(false, sound.getFilePath(), 0, 0, 0);
		return hstream != null ? new Player(hstream, sound) : null;
	}

	@Override
	public IPlayer getPlayer3D(final ISound arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAllPlayersPaused(final boolean paused) {
		if (paused) {
			Bass.BASS_Pause();
		} else {
			Bass.BASS_Start();
		}
	}

	@Override
	public void setMasterVolume(final float volume) {
		Bass.BASS_SetVolume(volume);
	}

	@Override
	public void stopAllPlayers() {
		Bass.BASS_Stop();
	}

	@Override
	public String getName() {
		return "NativeBass";
	}

}
