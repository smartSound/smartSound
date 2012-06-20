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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import smartsound.plugins.player.SoundEngine;

/**
 * Makes a singleton out of a subclass of <c>SoundEngine</c>.
 * @author André Becker
 *
 */
public class SoundEngineSingleton {
	
	private static Class<? extends SoundEngine> SoundEngineClass;
	
	private static SoundEngine instance;
	
	public static SoundEngine getInstance() {
		if (instance == null) {
			try {
				Constructor<? extends SoundEngine> ctr = SoundEngineClass.getConstructor();
				instance = ctr.newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return instance;
	}
	
	public static void setSoundEngineClass(Class<? extends SoundEngine> SoundEngineClass) {
		SoundEngineSingleton.SoundEngineClass = SoundEngineClass;
	}
}
