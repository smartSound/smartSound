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

package smartsound.common;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.freeware.inifiles.INIFile;

/**
 * This class handles (key,value) pairs and a list of nested <c>PropertyMap</c>s.
 * @author André Becker
 *
 */
public class PropertyMap {
	private final Map<String,String> propertymap = new HashMap<String,String>();
	private final List<PropertyMap> nestedMaps = new LinkedList<PropertyMap>();
	private UUID mapUUID;

	/**
	 * Creates a new, empty <c>PropertyMap</c>.
	 * @param mapUUID a <c>UUID</c> identifying this <c>PropertyMap</c>.
	 */
	public PropertyMap(final UUID mapUUID) {
		this.mapUUID = mapUUID;
	}

	/**
	 * Creates a <c>PropertyMap</c> from an INI-File.
	 * @param filePath Path to the INI-File.
	 */
	public PropertyMap(final String filePath) {
		INIFile ini = new INIFile(filePath);
		String root = ini.getStringProperty("GLOBALS", "ROOT");

		load(ini, root);
	}

	private PropertyMap() {}

	private void load(final INIFile ini, final String uuid) {
		mapUUID = UUID.fromString(uuid);
		for (String prop : ini.getPropertyNames(uuid)) {
			System.out.println(prop + " : " + ini.getStringProperty(uuid,prop));
			propertymap.put(prop, ini.getStringProperty(uuid, prop));
		}

		String children = ini.getStringProperty(uuid, "CHILDREN");
		children = children.trim();
		String[] childrenArr = children.split(" ");

		PropertyMap map;
		for (String child : childrenArr) {
			if (!child.equals("")) {
				map = new PropertyMap();
				map.load(ini, child);
				nestedMaps.add(map);
			}
		}
	}

	/**
	 * Adds a (key,value) pair to the map.
	 * @param key
	 * @param value
	 */
	public void put(final String key, final String value) {
		propertymap.put(key,  value);
	}

	/**
	 * Gets the value stored for a specific key, or <c>null</c> if no such
	 * value exists.
	 * @param key The key.
	 * @return The value.
	 */
	public String get(final String key) {
		return propertymap.get(key);
	}

	@Override
	public String toString() {
		return "PropertyMap [propertymap=" + propertymap + ", nestedMaps="
				+ nestedMaps + ", mapUUID=" + mapUUID + "]";
	}

	/**
	 * @return The <c>UUID</c> identifying this <c>PropertyMap</c>.
	 */
	public UUID getMapUUID() {
		return this.mapUUID;
	}

	/**
	 * @return A reference to the list of nested <c>PropertyMap</c>s.
	 */
	public List<PropertyMap> getNestedMaps() {
		return nestedMaps;
	}

	/**
	 * Adds a new <c>PropertyMap</c>.
	 * @param propertyMap The <c>PropertyMap</c>.
	 */
	public void addPropertyMap(final PropertyMap propertyMap) {
		nestedMaps.add(propertyMap);
	}

	private void saveToIni(final INIFile ini) {
		for (Entry<String, String> entry : propertymap.entrySet()) {
			ini.setStringProperty(mapUUID.toString(), entry.getKey(), entry.getValue(), "");
		}

		String children = "";
		for (PropertyMap map : nestedMaps) {
			children += map.getMapUUID();
			children += " ";
			map.saveToIni(ini);
		}
		ini.setStringProperty(mapUUID.toString(), "CHILDREN", children, "");
	}

	/**
	 * Saves this <c>PropertyMap</c> and all nested ones to an INI file.
	 * @param savePath The file path to the INI file.
	 */
	public void saveToIni(final String savePath) {
		File file = new File(savePath);
		if (file.exists())
			file.delete();
		INIFile ini = new INIFile(savePath);
		saveToIni(ini);
		ini.setStringProperty("GLOBALS", "ROOT", mapUUID.toString(), "");
		ini.save();
	}

	/**
	 * @return A list of all keys in this map.
	 */
	public List<String> getKeys() {
		return new LinkedList<String>(propertymap.keySet());
	}
}
