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

import java.io.File;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import smartsound.common.Tuple;

import com.sun.jna.Native;

public class IrrKlangSingleton {
	
	static {
		
	}
	
	private static IrrKlang instance;
	
	private IrrKlangSingleton() { }
	
	public static IrrKlang getInstance() {
		if (instance == null) {
			String path = null;
			try {
				path = new File(IrrKlangSingleton.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
				System.out.println(path);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.setProperty("jna.library.path", new File(path).getAbsolutePath());
			instance = (IrrKlang) Native.loadLibrary("IrrklangWrapper", IrrKlang.class);
		}
		return instance;
	}
	
	public static List<Tuple<String,String>> getPathVariables() {
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
			path = new File(IrrKlangSingleton.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent() + "/irrKlang-1.3.0/bin/";
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String operatingSystem = System.getProperty("os.name");
		
		if (operatingSystem.contains("Linux")) {
			path += "linux-gcc";
		} else if (operatingSystem.contains("Windows")){
			path += "win32-gcc";
		} else if (operatingSystem.contains("Mac OS")) {
			path += "macosx-gcc";
		}
		
		return new File(path).getAbsolutePath();
	}
	
}
