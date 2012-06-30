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

package smartsound.settings;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;



public class Global {
	
	private Global() throws IOException {
		File file = new File(configFilePath);
		file.createNewFile();
		properties.load(new FileReader(file));
	}

	private static Global instance;
	private static String configFilePath = "Settings.cfg";
	
	private Properties properties = new Properties();
	
	public String getProperty(String propertyName) {
		return properties.getProperty(propertyName);
	}
	
	/**
	 * Sets a a value for a specific property and saves the changes to the
	 * settings file.
	 * @param propertyName The property's name.
	 * @param value The value.
	 * @throws IOException Is thrown if the settings file could net be written.
	 */
	public void setProperty(String propertyName, String value) throws IOException {
		properties.setProperty(propertyName, value);
		properties.store(new FileWriter(configFilePath), "");
	}
	
	/**
	 * Removes a property from the settings and writes the changes back to the
	 * settings file.
	 * @param propertyName The property name.
	 * @throws IOException If the changes could not be written to the file.
	 */
	public void removeProperty(String propertyName) throws IOException {
		properties.remove(propertyName);
		properties.store(new FileWriter(configFilePath), "");
	}
	
	/**
	 * @return A singleton for this class.
	 * @throws IOException If the settings file had to be read and could not be
	 * 	opened.
	 */
	public static Global getInstance() throws IOException {
		if (instance == null) {
			instance = new Global();
		}
		return instance;
	}
}
