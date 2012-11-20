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

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import smartsound.common.Tuple;
import smartsound.player.SoundEngineSingleton;
import smartsound.plugins.player.PluginLoader;
import smartsound.settings.Global;

/**
 * Launches the main application. The plugin for the sound engine is chosen
 * and the process is restarted if necessary (if the plugin needs certain
 * environment variables to be set).
 * @author André Becker
 *
 */
public class Launcher {

	private final static Logger LOGGER = Logger.getLogger(Launcher.class.getName());


	private static void setLookAndFeel() {
		UIManager.put("nimbusBase", new Color(113, 98, 70));
		UIManager.put("control", new Color(240,216,154));
		UIManager.put("menu", new Color(240,216,154));
		UIManager.put("nimbusBlueGrey", new Color(113, 98, 70));
		UIManager.put("background", new Color(113, 98, 70));
		UIManager.put("List.background", new Color(113, 98, 70));
		try {
			UIManager.setLookAndFeel(NimbusLookAndFeel.class.getCanonicalName());
			UIManager.getLookAndFeelDefaults().put("PopupMenu[Enabled].backgroundPainter",
					new FillPainter(new Color(240,216,154)));
			UIManager.put("PopupMenu.background", new Color(240,216,154));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		setLookAndFeel();

		String pluginName = null;
		if (args.length > 0) {
			pluginName = args[0];
		}
		PluginDescription plugin = getPlugin(pluginName);

		String workingDirectory = getMainDir();
		String operatingSystem = System.getProperty("os.name");
		System.setProperty("java.io.tmpdir", new File(workingDirectory + "/tmp").getAbsolutePath());

		StringBuilder builder = new StringBuilder();
		builder.append("\r\n");
		builder.append("Operating System:\t");
		builder.append(operatingSystem);
		builder.append("\r\n");

		builder.append("CWD:\t\t\t");
		builder.append(workingDirectory);
		builder.append("\r\n");

		builder.append("Version:\t\t");
		builder.append(System.getProperty("java.version"));
		builder.append("\r\n");

		builder.append("Arch:\t\t\t");
		builder.append(System.getProperty("sun.arch.data.model"));
		builder.append("\r\n");

		builder.append("Home:\t\t\t");
		builder.append(System.getProperty("java.home"));
		builder.append("\r\n");

		LOGGER.info(builder.toString());

		if (operatingSystem.contains("Linux")) {
		} else if (operatingSystem.contains("Windows")){
		} else if (operatingSystem.contains("Mac OS")) {
		}

		String environmentVariableContent;


		PluginLoader pluginLoader = getPluginLoader(plugin);
		boolean restartRequired = false;
		for (Tuple<String,String> tuple: pluginLoader.neededEnvironment()) {
			environmentVariableContent = System.getenv(tuple.first);
			if (environmentVariableContent == null || !environmentVariableContent.contains(tuple.second)) {
				restartRequired = true;
			}
			LOGGER.info(tuple.first + " set to " + environmentVariableContent);
		}

		if (restartRequired) {
			restart(pluginLoader.neededEnvironment(), plugin.getName());
		} else {
			SoundEngineSingleton.setSoundEngineClass(pluginLoader.getEngineClass());

			launch();
		}
	}

	private static PluginDescription getPlugin(String pluginName) {
		File pluginDir = new File("plugins");

		Stack<File> dirStack = new Stack<File>();
		dirStack.add(pluginDir);

		File current;

		List<PluginDescription> pluginList = new LinkedList<PluginDescription>();
		while (!dirStack.isEmpty()) {
			current = dirStack.pop();

			if (current.isDirectory()) {
				for (File f : current.listFiles()) {
					dirStack.push(f);
				}
			} else if (current.getName().equals("plugin.cfg")) {
				pluginList.add(new PluginDescription(current));
			}
		}

		try {
			if (pluginName == null) {
				pluginName = Global.getInstance().getProperty("plugin");
			}
			if (pluginName != null) {
				for (PluginDescription desc : pluginList) {
					if (desc.getName().equals(pluginName)) {
						return desc;
					}
				}
			}
		} catch (IOException e) {
		}

		PluginDescription plugin = PluginChooser.getPlugin(pluginList);
		return plugin;
	}

	private static void restart(final List<Tuple<String, String>> neededEnvironment, final String pluginName) {
		try {
			ProcessBuilder pb = new ProcessBuilder(getJrePath(), "-jar", getMainDir() + "/smartSound.jar", pluginName);

			Map<String, String> env = pb.environment();


			String delimiter = ":";

			String operatingSystem = System.getProperty("os.name");
			if (operatingSystem.contains("Linux")) {
				delimiter = ":";
			} else if (operatingSystem.contains("Windows")){
				env.clear();
				delimiter = ";";
			} else if (operatingSystem.contains("Mac OS")) {
				delimiter = ";";
			}

			String content;
			for (Tuple<String,String> tuple : neededEnvironment) {
				content = env.get(tuple.first);
				if (content == null) {
					content = "";
				}

				if (!content.endsWith(delimiter)) {
					content += delimiter;
				}
				content += tuple.second;

				env.put(tuple.first, content);
			}

			final Process process = pb.start();

			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;

			new Thread() {
				@Override
				public void run() {
					InputStream inputStream = process.getErrorStream();
					InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
					String errLine;
					String timeString = new SimpleDateFormat("YYMMddHHmmss").format(new Date());
					File file = new File("./tmp/" + timeString + ".log");
					FileWriter writer = null;
					try {
						file.createNewFile();
						writer = new FileWriter(file, true);
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					try {
						while ((errLine = bufferedReader.readLine()) != null) {
							System.err.println(errLine);
							writer.write(errLine);
							writer.write(System.getProperty("line.separator"));
							writer.flush();
						}
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();

			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static PluginLoader getPluginLoader(final PluginDescription desc) {
		try {
			ClassLoader loader = URLClassLoader.newInstance(new URL[] { desc.getPluginJar().toURI().toURL() },
					Launcher.class.getClassLoader());
			Class<?> ClassToLoad = Class.forName(desc.getClassName(), true, loader);
			Class<? extends PluginLoader> PluginLoaderClass = ClassToLoad.asSubclass(PluginLoader.class);
			Constructor<? extends PluginLoader> ctr = PluginLoaderClass.getConstructor();
			return ctr.newInstance();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		}
		return null;
	}

	private static String getJrePath() {
		String operatingSystem = System.getProperty("os.name");

		if (!System.getProperty("java.version").startsWith("1.7")) {
			JOptionPane.showMessageDialog(null, "You are running an old version of Java. Please install Java 7.\r\n" +
					"If Java 7 is already installed, you should remove all old versions.");
			System.exit(0);
		}

		if ((operatingSystem.contains("Windows") && System.getProperty("sun.arch.data.model").equals("64"))) {
			JOptionPane.showMessageDialog(null, "You are using Windows and a 64Bit.\r\n\r\n" +
					"If you have a 32 Bit version of Java installed, please select the Java executable from the following Menu.\r\n" +
					"On Windows systems, it usually can be found under C:\\Program Files (x86)\\Java\\jre7\\bin\\java.exe");
			JFileChooser chooser = new JFileChooser();
			int returnValue = chooser.showOpenDialog(null);
			if (returnValue == JFileChooser.CANCEL_OPTION) {
				System.exit(0);
			}
			return chooser.getSelectedFile().getAbsolutePath();
		}

		return new File(System.getProperty("java.home") + "/bin/java").getAbsolutePath();
	}

	public static void launch() {
		new DefaultController();
	}

	public static String getMainDir() {
		return System.getProperty("user.dir");
	}

	public static String getImageDir() {
		String path = getMainDir();
		path += "/images";

		return new File(path).getAbsolutePath();
	}

}
