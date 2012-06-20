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

import java.io.File;
import java.util.UUID;

import smartsound.common.PropertyMap;
import smartsound.plugins.player.ISound;

/**
 * This implementation of <c>ISound</c> directly wraps a file.
 * @author André Becker
 *
 */
public class FilePathSound implements ISound {

	private File file;
	private int startTime = 0;
	private int endTime = Integer.MAX_VALUE;
	private UUID uuid = UUID.randomUUID();
	
	/**
	 * @param file The path to the sound as a <c>File</c> object.
	 */
	public FilePathSound(File file) {
		this.file = file;
	}
	
	/**
	 * @param filePath The path to the sound as <c>String</c>.
	 */
	public FilePathSound(String filePath) {
		this.file = new File(filePath);
	}
	
	/**
	 * Loads this sound from a <c>PropertyMap</c>.
	 * @param map The <c>PropertyMap</c> containing the data for this object.
	 * @throws LoadingException
	 */
	public FilePathSound(PropertyMap map) throws LoadingException {
		if (!map.get("type").equals(getClass().getCanonicalName())) {
			throw new LoadingException();
		}

		uuid = map.getMapUUID();
		
		file = new File(map.get("file_path"));
		startTime = Integer.parseInt(map.get("start_time"));
		endTime = Integer.parseInt(map.get("end_time"));
	}

	@Override
	public String getFilePath() {
		return file.getAbsolutePath();
	}

	@Override
	public int getStartTime() {
		return startTime;
	}

	@Override
	public int getEndTime() {
		return endTime;
	}

	@Override
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	@Override
	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}
	
	@Override
	public String toString() {
		return file.getName();
	}

	@Override
	public PropertyMap getPropertyMap() {
		PropertyMap map = new PropertyMap(uuid);
		
		map.put("type", getClass().getCanonicalName());
		map.put("file_path", file.getAbsolutePath());
		map.put("start_time", String.valueOf(startTime));
		map.put("end_time", String.valueOf(endTime));
		
		return map;
	}
	
	

}
