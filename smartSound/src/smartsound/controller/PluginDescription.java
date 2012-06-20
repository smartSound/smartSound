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

package smartsound.controller;

import java.io.*;
import java.util.Properties;

public class PluginDescription
{
    private File pluginJar;
    private String name;
    private String version;
    private String className;
    private String description;

	public PluginDescription(File f) {
		try {
			description = "";
			BufferedReader rdr;
			Properties properties = new Properties();
			BufferedInputStream stream = new BufferedInputStream(
					new FileInputStream(f));
			properties.load(stream);
			stream.close();
			name = properties.getProperty("name");
			version = properties.getProperty("version");
			className = properties.getProperty("class");
			assert properties.getProperty("type").equals("soundengine");

			File dir = f.getParentFile();
			pluginJar = new File((new StringBuilder(String.valueOf(dir
					.getAbsolutePath()))).append("/")
					.append(properties.getProperty("filename")).toString());
			stream = new BufferedInputStream(new FileInputStream(
					(new StringBuilder()).append(dir).append("/desc-en.html")
							.toString()));
			rdr = new BufferedReader(new FileReader((new StringBuilder())
					.append(dir).append("/desc-en.html").toString()));

			String line;
			while ((line = rdr.readLine()) != null) {
				description += line;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

    public File getPluginJar()
    {
        return pluginJar;
    }

    public String getName()
    {
        return name;
    }

    public String getVersion()
    {
        return version;
    }

    public String getClassName()
    {
        return className;
    }

    public String getDescription()
    {
        return description;
    }

    public String toString()
    {
        return name;
    }



}
