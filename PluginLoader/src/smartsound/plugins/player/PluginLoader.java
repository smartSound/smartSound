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

package smartsound.plugins.player;

import java.util.List;

import smartsound.common.Tuple;

/**
 * Each plugin has to implement a sub class of <c>PluginLoader</c> which can
 * then be used to determine the needed environment as well as a subclass of
 * <c>SoundEngine</c>.
 * @author André Becker
 *
 */
public abstract class PluginLoader {
	/**
	 * @return A subclass of <c>SoundEngine</c>.
	 */
	public abstract Class<? extends SoundEngine> getEngineClass();
	
	/**
	 * @return A list of tuples which contains the needed environment. Each
	 * 	tuple is a pair of the name of the environment variable and the
	 * 	needed content. There may be multiple entries for each environment
	 * 	variable and an entry should not contain any delimiters used for
	 * 	multiple values of an environment variables.
	 */
	public abstract List<Tuple<String,String>> neededEnvironment();
}
