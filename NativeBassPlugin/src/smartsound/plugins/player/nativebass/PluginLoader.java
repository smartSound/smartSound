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

import java.io.File;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import smartsound.common.Tuple;

public class PluginLoader extends smartsound.plugins.player.PluginLoader {

	@Override
	public Class<? extends smartsound.plugins.player.SoundEngine> getEngineClass() {
		return SoundEngine.class;
	}

	@Override
	public List<Tuple<String, String>> neededEnvironment() {
		String operatingSystem = System.getProperty("os.name");
		
		String variable ="";
		
		if (operatingSystem.contains("Linux")) {
			variable = "LD_LIBRARY_PATH";
		} else if (operatingSystem.contains("Windows")){
			variable = "PATH";
		} else if (operatingSystem.contains("Mac OS")) {
			variable = "DYLD_LIBRARY_PATH";
		}
		
		List<Tuple<String,String>> result = new LinkedList<Tuple<String,String>>();
		result.add(new Tuple<String,String>(variable, getLibraryDirectory()));
		return result;
	}

	public static String getLibraryDirectory() {
		String path = null;
		try {
			path = new File(PluginLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent() + "/NativeBass/";
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String operatingSystem = System.getProperty("os.name");
		String arch = System.getProperty("sun.arch.data.model");
		
		if (operatingSystem.contains("Linux")) {
			if (arch.equals("64")) {
				path += "linux64";
			} else {
				path += "linux32";
			}
		} else if (operatingSystem.contains("Windows")){
			if (arch.equals("64")) {
				path += "win64";
			} else {
				path += "win32";
			}
		} else if (operatingSystem.contains("Mac OS")) {
			path += "mac";
		}
		
		return new File(path).getAbsolutePath();
	}
	
}
