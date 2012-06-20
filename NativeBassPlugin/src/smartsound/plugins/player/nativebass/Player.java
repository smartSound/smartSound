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

import static jouvieje.bass.Bass.BASS_ChannelPlay;
import static jouvieje.bass.Bass.BASS_ChannelStop;
import static jouvieje.bass.Bass.BASS_ChannelPause;
import static jouvieje.bass.Bass.BASS_ChannelGetAttribute;
import static jouvieje.bass.Bass.BASS_ChannelSetAttribute;
import static jouvieje.bass.Bass.BASS_ChannelGetLength;
import static jouvieje.bass.Bass.BASS_ChannelGetPosition;
import static jouvieje.bass.Bass.BASS_ChannelSetPosition;
import static jouvieje.bass.Bass.BASS_ChannelIsActive;
import static jouvieje.bass.Bass.BASS_ChannelBytes2Seconds;
import static jouvieje.bass.Bass.BASS_ChannelSeconds2Bytes;
import static jouvieje.bass.defines.BASS_ATTRIB.BASS_ATTRIB_VOL;
import static jouvieje.bass.defines.BASS_ATTRIB.BASS_ATTRIB_PAN;
import static jouvieje.bass.defines.BASS_POS.BASS_POS_BYTE;
import static jouvieje.bass.defines.BASS_ACTIVE.BASS_ACTIVE_STOPPED;

import java.nio.FloatBuffer;

import jouvieje.bass.structures.HSTREAM;
import smartsound.plugins.player.IPlayer;
import smartsound.plugins.player.ISound;

public class Player implements IPlayer {

	private HSTREAM hstream;
	private ISound sound;
	
	public Player(HSTREAM hstream, ISound sound) {
		this.hstream = hstream;
		this.sound = sound;
	}

	@Override
	public float getPan() {
		FloatBuffer buf = FloatBuffer.wrap(new float[1]);
		BASS_ChannelGetAttribute(hstream.asInt(), BASS_ATTRIB_PAN, buf);
		return buf.get();
	}

	@Override
	public boolean getPaused() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getPlayLength() {
		long bytes = BASS_ChannelGetLength(hstream.asInt(), BASS_POS_BYTE);
		return (int) (BASS_ChannelBytes2Seconds(hstream.asInt(), bytes) * 1000);
	}

	@Override
	public ISound getPlayListEntry() {
		return sound;
	}

	@Override
	public int getPlayPosition() {
		long bytes = BASS_ChannelGetPosition(hstream.asInt(), BASS_POS_BYTE);
		return (int) (BASS_ChannelBytes2Seconds(hstream.asInt(), bytes) * 1000);
	}

	@Override
	public float getPlaybackSpeed() {
		return 1.0f;
	}

	@Override
	public float getVolume() {
		FloatBuffer buf = FloatBuffer.wrap(new float[1]);
		BASS_ChannelGetAttribute(hstream.asInt(), BASS_ATTRIB_VOL, buf);
		return buf.get();
	}

	@Override
	public boolean isFinished() {
		return BASS_ACTIVE_STOPPED == BASS_ChannelIsActive(hstream.asInt());
	}

	@Override
	public void pause() {
		BASS_ChannelPause(hstream.asInt());
	}

	@Override
	public void play() {
		BASS_ChannelPlay(hstream.asInt(), false);
	}

	@Override
	public void setPan(float pan) {
		BASS_ChannelSetAttribute(hstream.asInt(), BASS_ATTRIB_PAN, pan);
	}

	@Override
	public void setPlayPosition(int position) {
		long bytePos = BASS_ChannelSeconds2Bytes(hstream.asInt(), position / 1000.0);
		BASS_ChannelSetPosition(hstream.asInt(), bytePos, BASS_POS_BYTE);
	}

	@Override
	public void setPlaybackSpeed(float speed) {
		// Not supported yet
	}

	@Override
	public void setVolume(float volume) {
		BASS_ChannelSetAttribute(hstream.asInt(), BASS_ATTRIB_VOL, volume);
	}

	@Override
	public void stop() {
		BASS_ChannelStop(hstream.asInt());
	}

}
