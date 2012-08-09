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

package smartsound.view;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import smartsound.common.IObserver;
import smartsound.common.PropertyMap;
import smartsound.common.Tuple;
import smartsound.controller.AbstractController;
import smartsound.player.ItemData;
import smartsound.player.LoadingException;
import smartsound.view.Layout.Type;
import smartsound.view.gui.GUIController;

public class ViewController extends AbstractViewController implements IObserver
{

	private final AbstractController controller;
	private final List<GUIController> guis;
	private final Map<UUID, Map<String, Set<Action>>> hotkeyMap;
	private final Map<UUID, Layout> layoutMap = new HashMap<UUID, Layout>();
	private final Map<UUID, Action> uuidToActionMap = new HashMap<UUID, Action>();
	private final Map<UUID, String> commentMap = new HashMap<UUID, String>();
	private Layout rootLayout;

	private static final Map<String, Class<?>> primitiveTypesMap = new HashMap<String,Class<?>>();
	static {
		Class<?>[] types = {
				boolean.class, byte.class, char.class, double.class,
				float.class, int.class, long.class, short.class,
				boolean[].class, byte[].class, char[].class, double[].class,
				float[].class, int[].class, long[].class, short[].class
		};
		for (Class<?> c : types) {
			primitiveTypesMap.put(c.getCanonicalName(), c);
		}
	}

	public ViewController(final AbstractController controller)
	{
		guis = new LinkedList<GUIController>();
		hotkeyMap = new HashMap<UUID, Map<String, Set<Action>>>();
		this.controller = controller;
		rootLayout = new Layout(this, null, Type.ROW);

		controller.addObserver(rootLayout, null);
		controller.addObserver(this);
	}

	@Override
	public void addGUI(final GUIController gui)
	{
		guis.add(gui);
	}

	@Override
	public void addObserver(final IObserver observer, final UUID observableUUID)
	{
		controller.addObserver(observer, observableUUID);
	}

	@Override
	public ItemData getItemData(final UUID playListUUID, final int index)
	{
		return controller.getItemData(playListUUID, index);
	}

	@Override
	public int getSize(final UUID playListUUID)
	{
		return controller.getSize(playListUUID);
	}

	@Override
	public void addItem(final UUID playListUUID, final int index, final String filePath)
	{
		controller.addItem(playListUUID, index, filePath);
	}

	@Override
	public void addItem(final UUID playListUUID, final String filePath)
	{
		controller.addItem(playListUUID, filePath);
	}

	@Override
	public void removeItem(final UUID playListUUID, final int index, final boolean stop)
	{
		controller.removeItem(playListUUID, index, stop);
	}

	@Override
	public int getItemIndex(final UUID playListUUID, final UUID itemUUID)
	{
		return controller.getItemIndex(playListUUID, itemUUID);
	}

	@Override
	public UUID addPlayList(final UUID parentSetUUID)
	{
		UUID uuid = controller.addPlayList(parentSetUUID);
		return uuid;
	}

	@Override
	public void deletePlayList(final UUID playListUUID)
	{
		controller.deletePlayList(playListUUID);
	}

	@Override
	public void importItems(final UUID sourcePlayListUUID, final List<UUID> itemUUIDs, final UUID playListUUID, final int targetIndex, final boolean copy)
	{
		controller.importItems(sourcePlayListUUID, itemUUIDs, playListUUID, targetIndex, copy);
	}

	@Override
	public boolean itemIsActive(final UUID playListUUID, final UUID itemUUID)
	{
		return controller.itemIsActive(playListUUID, itemUUID);
	}

	@Override
	public String getItemName(final UUID playListUUID, final UUID itemUUID)
	{
		return controller.getItemName(playListUUID, itemUUID);
	}

	@Override
	public UUID getItemChainWith(final UUID playListUUID, final UUID itemUUID)
	{
		return controller.getItemChainWith(playListUUID, itemUUID);
	}

	@Override
	public Action getItemChainWithAction(final UUID playListUUID, final UUID itemUUID, final String description)
	{
		Method method;
		try
		{
			method = ViewController.class.getMethod("setItemChainWith", new Class[] {
					UUID.class, UUID.class, UUID.class
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		Object params[] = {
				playListUUID, itemUUID
		};
		return new Action(method, this, description, params);
	}

	public void setItemChainWith(final UUID playListUUID, final UUID source, final UUID target)
	{
		controller.setItemChainWith(playListUUID, source, target);
	}

	@Override
	public boolean itemIsRepeating(final UUID playListUUID, final UUID itemUUID)
	{
		return controller.itemIsRepeating(playListUUID, itemUUID);
	}

	@Override
	public Action getItemIsRepeatingAction(final UUID playListUUID, final UUID itemUUID, final String description)
	{
		Method method;
		try
		{
			method = ViewController.class.getMethod("setItemIsRepeating", new Class[] {
					UUID.class, UUID.class, boolean.class
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		Object params[] = {
				playListUUID, itemUUID
		};
		return new Action(method, this, description, params);
	}

	public void setItemIsRepeating(final UUID playListUUID, final UUID itemUUID, final boolean repeating)
	{
		controller.setItemIsRepeating(playListUUID, itemUUID, repeating);
	}

	@Override
	public boolean isRepeatList(final UUID playListUUID)
	{
		return controller.isRepeatList(playListUUID);
	}

	@Override
	public Action getRepeatListAction(final UUID playListUUID, final String description)
	{
		Method method;
		try
		{
			method =ViewController.class.getMethod("setRepeatList", new Class[] {
					UUID.class, boolean.class
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		Object params[] = {
				playListUUID
		};
		return new Action(method, this, description, params);
	}

	public void setRepeatList(final UUID playListUUID, final boolean repeatList)
	{
		controller.setRepeatList(playListUUID, repeatList);
	}

	@Override
	public boolean isRandomizeList(final UUID playListUUID)
	{
		return controller.isRandomizeList(playListUUID);
	}

	@Override
	public boolean isStopAfterEachSound(final UUID playListUUID)
	{
		return controller.isStopAfterEachSound(playListUUID);
	}

	@Override
	public float getRandomizeVolumeFrom(final UUID playListUUID)
	{
		return controller.getRandomizeVolumeFrom(playListUUID);
	}

	@Override
	public float getRandomizeVolumeTo(final UUID playListUUID)
	{
		return controller.getRandomizeVolumeTo(playListUUID);
	}

	@Override
	public int getFadeIn(final UUID playListUUID)
	{
		return controller.getFadeIn(playListUUID);
	}

	@Override
	public int getFadeOut(final UUID playListUUID)
	{
		return controller.getFadeOut(playListUUID);
	}

	@Override
	public int getOverlap(final UUID playListUUID)
	{
		return controller.getOverlap(playListUUID);
	}

	@Override
	public float getVolume(final UUID playListUUID)
	{
		return controller.getVolume(playListUUID);
	}

	@Override
	public Action getPlayAction(final UUID playListUUID, final int index, final String description)
	{
		Method method;
		try
		{
			method = ViewController.class.getMethod("play", new Class[] {
					UUID.class, int.class
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		Object params[] = {
				playListUUID, Integer.valueOf(index)
		};
		return new Action(method, this, description, params);
	}

	public void play(final UUID playListUUID, final int index)
	{
		controller.play(playListUUID, index);
	}

	@Override
	public Action getPlayAction(final UUID playListUUID, final String description)
	{
		Method method;
		try
		{
			method = ViewController.class.getMethod("play", new Class[] {
					UUID.class
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		Object params[] = {
				playListUUID
		};
		return new Action(method, this, description, params);
	}

	public void play(final UUID playListUUID)
	{
		controller.play(playListUUID);
	}

	@Override
	public Action getRandomizeListAction(final UUID playListUUID, final String description)
	{
		Method method;
		try
		{
			method = ViewController.class.getMethod("setRandomizeList", new Class[] {
					UUID.class, boolean.class
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		Object params[] = {
				playListUUID
		};
		return new Action(method, this, description, params);
	}

	public void setRandomizeList(final UUID playListUUID, final boolean randomize)
	{
		controller.setRandomizeList(playListUUID, randomize);
	}

	@Override
	public Action getRandomizeVolumeFromAction(final UUID playListUUID, final String description)
	{
		Method method;
		try
		{
			method = ViewController.class.getMethod("setRandomizeVolumeFrom", new Class[] {
					UUID.class, float.class
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		Object params[] = {
				playListUUID
		};
		return new Action(method, this, description, params);
	}

	public void setRandomizeVolumeFrom(final UUID playListUUID, final float value)
	{
		controller.setRandomizeVolumeFrom(playListUUID, value);
	}

	@Override
	public Action getRandomizeVolumeToAction(final UUID playListUUID, final String description)
	{
		Method method;
		try
		{
			method = ViewController.class.getMethod("setRandomizeVolumeTo", new Class[] {
					UUID.class, float.class
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		Object params[] = {
				playListUUID
		};
		return new Action(method, this, description, params);
	}

	public void setRandomizeVolumeTo(final UUID playListUUID, final float value)
	{
		controller.setRandomizeVolumeTo(playListUUID, value);
	}

	@Override
	public Action getStopAfterEachSoundAction(final UUID playListUUID, final String description)
	{
		Method method;
		try
		{
			method = ViewController.class.getMethod("setStopAfterEachSound", new Class[] {
					UUID.class, boolean.class
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		Object params[] = {
				playListUUID
		};
		return new Action(method, this, description, params);
	}

	public void setStopAfterEachSound(final UUID playListUUID, final boolean stopAfterEachSound)
	{
		controller.setStopAfterEachSound(playListUUID, stopAfterEachSound);
	}

	@Override
	public Action getFadeInAction(final UUID playListUUID, final String description)
	{
		Method method;
		try
		{
			method = ViewController.class.getMethod("setFadeIn", new Class[] {
					UUID.class, int.class
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		Object params[] = {
				playListUUID
		};
		return new Action(method, this, description, params);
	}

	public void setFadeIn(final UUID playListUUID, final int value)
	{
		controller.setFadeIn(playListUUID, value);
	}

	@Override
	public Action getFadeOutAction(final UUID playListUUID, final String description)
	{
		Method method;
		try
		{
			method = ViewController.class.getMethod("setFadeOut", new Class[] {
					UUID.class, int.class
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		Object params[] = {
				playListUUID
		};
		return new Action(method, this, description, params);
	}

	public void setFadeOut(final UUID playListUUID, final int value)
	{
		controller.setFadeOut(playListUUID, value);
	}

	@Override
	public Action getOverlapAction(final UUID playListUUID, final String description)
	{
		Method method;
		try
		{
			method = ViewController.class.getMethod("setOverlap", new Class[] {
					UUID.class, int.class
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		Object params[] = {
				playListUUID
		};
		return new Action(method, this, description, params);
	}

	public void setOverlap(final UUID playListUUID, final int value)
	{
		controller.setOverlap(playListUUID, value);
	}

	@Override
	public Action getVolumeAction(final UUID playListUUID, final String description)
	{
		Method method;
		try
		{
			method = ViewController.class.getMethod("setVolume", new Class[] {
					UUID.class, float.class
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		Object params[] = {
				playListUUID
		};
		return new Action(method, this, description, params);
	}

	public void setVolume(final UUID playListUUID, final float value)
	{
		controller.setVolume(playListUUID, value);
	}

	@Override
	public Action getSaveAction()
	{
		Method method;
		try
		{
			method = ViewController.class.getMethod("save", new Class[] {
					String.class
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		Object params[] = new Object[0];
		return new Action(method, this, "Save", params);
	}

	public void save(final String filePath)
	{
		controller.save(filePath);
	}

	@Override
	public Action getLoadAction()
	{
		Method method;
		try
		{
			method = ViewController.class.getMethod("load", new Class[] {
					String.class
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		Object params[] = new Object[0];
		return new Action(method, this, "Load", params);
	}

	public void load(final String filePath)
	{
		controller.load(filePath);
	}

	@Override
	public Action getStopAction(final UUID playListUUID, final String description)
	{
		Method method;
		try
		{
			method = ViewController.class.getMethod("stop", new Class[] {
					UUID.class
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		Object params[] = {
				playListUUID
		};
		return new Action(method, this, description, params);
	}

	public void stop(final UUID playListUUID)
	{
		controller.stop(playListUUID);
	}

	@Override
	public Action getPlayIndexAction(final UUID playListUUID, final String description)
	{
		Method method;
		try
		{
			method = ViewController.class.getMethod("play", new Class[] {
					UUID.class, int.class
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		Object params[] = {
				playListUUID
		};
		return new Action(method, this, description, params);
	}

	@Override
	public Action getPlayAction(final UUID playListUUID, final UUID itemUUID, final String description)
	{
		Method method;
		try
		{
			method = ViewController.class.getMethod("play", new Class[] {
					UUID.class, UUID.class
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		Object params[] = {
				playListUUID, itemUUID
		};
		return new Action(method, this, description, params);
	}

	@Override
	public Action getPlayItemAction(final UUID playListUUID, final String description) {
		Method method;
		try
		{
			method = ViewController.class.getMethod("play", new Class[] {
					UUID.class, UUID.class
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		Object params[] = {
				playListUUID
		};
		return new Action(method, this, description, params);
	}

	public void play(final UUID playListUUID, final UUID itemUUID)
	{
		controller.play(playListUUID, controller.getItemIndex(playListUUID, itemUUID));
	}

	@Override
	public List<UUID> getPlayListUUIDs(final UUID parentPlayListSet)
	{
		return controller.getPlayListUUIDs(parentPlayListSet);
	}

	@Override
	public void setHotkey(final UUID uuid, final String hotkey, final Action action)
	{
		UUID key = controller.getRootParent(uuid);
		if (!hotkeyMap.containsKey(key))
			hotkeyMap.put(key, new HashMap<String, Set<Action>>());
		Map<String, Set<Action>> subMap = hotkeyMap.get(key);

		if (!subMap.containsKey(hotkey)) {
			subMap.put(hotkey, new HashSet<Action>());
		}
		subMap.get(hotkey).add(action);
		uuidToActionMap.put(action.getUUID(), action);
	}

	@Override
	public void executeHotkey(final String hotkey)
	{
		executeHotkey(null, hotkey);

		UUID key = getActivePlayListSet();
		if (key != null)
			executeHotkey(key, hotkey);

	}

	private void executeHotkey(final UUID playListSetUUID, final String hotkey) {
		if (hotkeyMap.containsKey(playListSetUUID)
				&& hotkeyMap.get(playListSetUUID).containsKey(hotkey))
			for (Action action : hotkeyMap.get(playListSetUUID).get(hotkey))
				action.execute();
	}

	private UUID getActivePlayListSet() {
		for (UUID uuid : controller.getPlayListSetUUIDs(null)) {
			if (isActive(uuid))
				return uuid;
		}
		return null;
	}

	@Override
	public void removePlayList(final UUID playListUUID)
	{
		GUIController gui;
		for(Iterator<GUIController> iterator = guis.iterator(); iterator.hasNext(); gui.removePlayList(playListUUID))
			gui = iterator.next();

	}

	@Override
	public void shiftElement(final UUID elementUUID, final int x, final int y, final PositionType alignment) {
		UUID parentUUID = controller.getParent(elementUUID);

		if (parentUUID != null)
			layoutMap.get(parentUUID).shiftElement(elementUUID, x, y, alignment);
		else {
			rootLayout.shiftElement(elementUUID, x, y, alignment);
		}
	}

	@Override
	public List<Tuple<String, Action>> getHotkeys(final UUID sceneUUID, final Action parent) {
		List<Tuple<String,Action>> result = new LinkedList<Tuple<String,Action>>();

		for (Entry<UUID, Map<String, Set<Action>>> mapEntry : hotkeyMap.entrySet())
			if (sceneUUID == null || sceneUUID.equals(mapEntry.getKey()))
				for (Entry<String,Set<Action>> entry : mapEntry.getValue().entrySet()) {
					for (Action action : entry.getValue()) {
						if (parent == null || parent.isParentOf(action))
							result.add(new Tuple<String,Action>(entry.getKey(), action));
					}

				}
		return result;
	}

	@Override
	public List<Tuple<String,Action>> getHotkeys(final UUID sceneUUID) {
		List<Tuple<String, Action>> result = new LinkedList<Tuple<String, Action>>();

		if (hotkeyMap.containsKey(sceneUUID))
			for (Entry<String, Set<Action>> entry : hotkeyMap.get(sceneUUID).entrySet()) {
				for (Action action : entry.getValue()) {
					result.add(new Tuple<String, Action>(entry.getKey(), action));
				}

			}
		return result;
	}

	@Override
	public void removeAllHotkeys() {
		hotkeyMap.clear();
	}

	@Override
	public void removeHotkey(final UUID elementUUID, final String hotkey, final Action action) {
		UUID playListSetUUID = controller.getRootParent(elementUUID);

		if (!hotkeyMap.containsKey(playListSetUUID))
			return;

		if (!hotkeyMap.get(playListSetUUID).containsKey(hotkey))
			return;

		Set<Action> actionSet = hotkeyMap.get(playListSetUUID).get(hotkey);
		actionSet.remove(action);
		if (actionSet.isEmpty())
			hotkeyMap.get(playListSetUUID).remove(hotkey);

		if (hotkeyMap.get(playListSetUUID).isEmpty())
			hotkeyMap.remove(playListSetUUID);
	}

	@Override
	public PropertyMap getPropertyMap() {
		PropertyMap result = new PropertyMap(UUID.randomUUID());
		result.put("type", ViewController.class.getCanonicalName());

		PropertyMap actionPropertyMap;
		String actionUUIDs;
		for (Entry<UUID, Map<String, Set<Action>>> uuidMapEntry : hotkeyMap.entrySet())
			for (Entry<String, Set<Action>> entry : uuidMapEntry.getValue().entrySet()) {
				actionUUIDs = "";
				for (Action action: entry.getValue()) {
					actionPropertyMap = actionToPropertyMap(action);
					result.addPropertyMap(actionPropertyMap);
					actionUUIDs += actionPropertyMap.getMapUUID();
					actionUUIDs += " ";
				}
				actionUUIDs = actionUUIDs.trim();
				result.put("hotkey:" + uuidMapEntry.getKey() + "," + entry.getKey(), actionUUIDs);
			}

		for (Layout layout : layoutMap.values()) {
			result.addPropertyMap(layout.getPropertyMap());
		}

		for (Entry<UUID, String> entry : commentMap.entrySet()) {
			result.put("comment:" + entry.getKey(), entry.getValue());
		}

		result.put("rootlayout", rootLayout.getUUID().toString());
		result.addPropertyMap(rootLayout.getPropertyMap());
		return result;
	}

	private PropertyMap actionToPropertyMap(final Action action) {
		PropertyMap result = new PropertyMap(action.getUUID());
		result.put("type", Action.class.getCanonicalName());
		Method method = action.getMethod();
		result.put("method", method.getName());

		String methodParams = "";
		Class<?>[] types = method.getParameterTypes();
		for (int i = 0; i < types.length; i++) {
			methodParams += types[i].getCanonicalName();
			if (i != types.length - 1) {
				methodParams += " ";
			}
		}
		result.put("parametertypes", methodParams);

		Object[] defaultParams = action.getDefaultParameters();
		String defaultParamsString = "";
		for (int i = 0; i < defaultParams.length; i++) {
			defaultParamsString += defaultParams[i];
			if (i != defaultParams.length - 1) {
				defaultParamsString += " ";
			}
		}
		result.put("defaultparameters", defaultParamsString);
		result.put("description", action.getDescription());
		return result;
	}

	private Action propertyMapToAction(final PropertyMap pMap) {
		String[] classNames = pMap.get("parametertypes").split(" ");
		Class<?>[] classes = new Class[classNames.length];

		Method method;
		try {
			for (int i = 0; i < classNames.length; i++) {
				classes[i] = stringToClass(classNames[i]);
			}

			method = ViewController.class
					.getMethod(pMap.get("method"), classes);
		} catch (ClassNotFoundException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
			return null;
		}



		String[] defaultParamsString = pMap.get("defaultparameters").split(" ");
		Object[] defaultParams = new Object[defaultParamsString.length];
		for (int i = 0; i < defaultParams.length; i++) {
			defaultParams[i] = stringToParam(classes[i], defaultParamsString[i]);
		}

		Action result = new Action(method, this, pMap.get("description"), defaultParams);
		result.setUUID(pMap.getMapUUID());

		return result;
	}

	private Class<?> stringToClass(final String className) throws ClassNotFoundException {
		Class<?> result = primitiveTypesMap.get(className);
		return result != null ? result : Class.forName(className);
	}

	private Object stringToParam(final Class<?> paramType, final String stringRepresentation) {
		if (paramType == boolean.class || paramType == Boolean.class) {
			return Boolean.valueOf(stringRepresentation);
		} else if (paramType == UUID.class) {
			return UUID.fromString(stringRepresentation);
		} else if (paramType == Float.class || paramType == float.class) {
			return Float.valueOf(stringRepresentation);
		} else if (paramType == Double.class || paramType == double.class) {
			return Double.valueOf(stringRepresentation);
		} else if (paramType == Integer.class || paramType == int.class) {
			return Integer.valueOf(stringRepresentation);
		} else if (paramType == String.class) {
			return stringRepresentation;
		}

		return null;
	}

	@Override
	public void loadFromPropertyMap(final PropertyMap pMap) throws LoadingException {
		if (!pMap.get("type").equals(getClass().getCanonicalName())) {
			throw new LoadingException();
		}

		removeAllHotkeys();
		layoutMap.clear();
		uuidToActionMap.clear();
		Layout layout;
		Action action;
		UUID rootLayoutUUID = UUID.fromString(pMap.get("rootlayout"));
		for (PropertyMap map : pMap.getNestedMaps()) {
			if (map.get("type").equals(Action.class.getCanonicalName())) {
				action = propertyMapToAction(map);
				uuidToActionMap.put(action.getUUID(), action);
			} else if (map.get("type").equals(Layout.class.getCanonicalName())) {
				layout = new Layout(this, map);
				if (layout.getUUID().equals(rootLayoutUUID)) {
					rootLayout = layout;
				} else {
					layoutMap.put(layout.getParentUUID(), layout);
				}
				controller.addObserver(this, layout.getParentUUID());
				controller.addObserver(layout, layout.getParentUUID());
			}
		}

		String keyString;
		String[] keySplit;
		UUID uuidKey;
		String hotkey;
		Map<String, Set<Action>> actionSetMap;
		for (String key : pMap.getKeys()) {
			if (key.startsWith("hotkey:")) {
				keyString = key.replaceFirst("hotkey:", "");
				keySplit = keyString.split(",");
				assert keySplit.length == 2;

				uuidKey = keySplit[0].equals("null") ? null : UUID.fromString(keySplit[0]);
				hotkey = keySplit[1];

				if (!hotkeyMap.containsKey(uuidKey))
					hotkeyMap.put(uuidKey, new HashMap<String, Set<Action>>());

				actionSetMap = hotkeyMap.get(uuidKey);

				if (!actionSetMap.containsKey(hotkey)) {
					actionSetMap.put(hotkey, new HashSet<Action>());
				}
				for (String split : pMap.get(key).split(" "))
					actionSetMap.get(hotkey).add(uuidToActionMap.get(UUID.fromString(split)));
			} else if (key.startsWith("comment:")) {
				keyString = key.replaceFirst("comment:", "");
				commentMap.put(UUID.fromString(keyString), pMap.get(key));
			}
		}
	}

	@Override
	public String getHotkey(final Action action) {
		for (Map<String, Set<Action>> actionSetMap : hotkeyMap.values())
			for (Entry<String,Set<Action>> entry : actionSetMap.entrySet()) {
				if (entry.getValue().contains(action)) {
					return entry.getKey();
				}
			}
		return null;
	}

	@Override
	public void removeHotkey(final Action action) {
		for (Map<String, Set<Action>> actionSetMap : hotkeyMap.values())
			for (Entry<String,Set<Action>> entry : actionSetMap.entrySet()) {
				if(entry.getValue().remove(action)) {
					if (entry.getValue().isEmpty()) {
						hotkeyMap.remove(entry.getKey());
					}
					return;
				}
			}
	}

	@Override
	public Action getSetRepeatItemAction(final UUID playListUUID, final String description) {
		Method method;
		try {
			method = ViewController.class.getMethod("setItemIsRepeating",
					new Class[] { UUID.class, UUID.class, boolean.class });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		Object params[] = { playListUUID };
		return new Action(method, this, description, params);
	}

	@Override
	public Action getSetItemChainWithAction(final UUID playListUUID, final String description) {
		Method method;
		try
		{
			method = ViewController.class.getMethod("setItemChainWith", new Class[] {
					UUID.class, UUID.class, UUID.class
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		Object params[] = {
				playListUUID
		};
		return new Action(method, this, description, params);
	}

	@Override
	public List<UUID> getPlayListSetUUIDs(final UUID parentSetUUID) {
		return controller.getPlayListSetUUIDs(parentSetUUID);
	}

	@Override
	public String getTitle(final UUID uuid) {
		return controller.getTitle(uuid);
	}

	@Override
	public UUID getParent(final UUID child) {
		return controller.getParent(child);
	}

	@Override
	public Layout getLayout(final UUID parentSetUUID) {
		if (parentSetUUID == null)
			return rootLayout;
		return layoutMap.get(parentSetUUID).copy();
	}

	@Override
	public UUID addPlayListSet(final UUID parentSetUUID) {
		return controller.addPlayListSet(parentSetUUID);
	}

	@Override
	public void reloadView() {
		for (GUIController gui : guis) {
			gui.reload();
		}
	}

	@Override
	public void setTitle(final UUID uuid, final String newTitle) {
		controller.setTitle(uuid, newTitle);
	}

	@Override
	public void remove(final UUID uuid) {
		controller.remove(uuid);
	}

	@Override
	public void update(final UUID uuid) {
		if (uuid == null) {
			Set<UUID> currentSet = new HashSet<UUID>();
			for (UUID u : controller.getPlayListSetUUIDs(null)) {
				currentSet.add(u);
				refreshLayout(u);
			}
			currentSet.removeAll(rootLayout.getUUIDSet());

			for (UUID u : currentSet) {
				rootLayout.addComponent(u, Layout.AUTOALIGN, 0);
			}

			return;
		}

		refreshLayout(uuid);
	}

	private void refreshLayout(final UUID uuid) {
		Layout layout = layoutMap.get(uuid);
		if (layout == null) {
			layout = new Layout(this, uuid, Type.GRID);
			controller.addObserver(layout, uuid);
			layoutMap.put(uuid, layout);
		}
	}

	@Override
	public void addLayoutObserver(final ILayoutObserver observer, final UUID observableUUID) {
		if (observableUUID == null) {
			rootLayout.addLayoutObserver(observer);
			return;
		}

		layoutMap.get(observableUUID).addLayoutObserver(observer);
	}

	@Override
	public void removeLayoutObserver(final ILayoutObserver observer,
			final UUID observableUUID) {
		if (observableUUID == null) {
			rootLayout.removeLayoutObserver(observer);
			return;
		}

		layoutMap.get(observableUUID).removeLayoutObserver(observer);
	}

	@Override
	public boolean isActive(final UUID elementUUID) {
		return controller.isActive(elementUUID);
	}

	@Override
	public void setAutoplay(final UUID elementUUID, final boolean autoplay) {
		controller.setAutoplay(elementUUID, autoplay);
	}

	@Override
	public boolean getAutoplay(final UUID elementUUID) {
		return controller.getAutoplay(elementUUID);
	}

	@Override
	public void setActive(final UUID playListSetUUID) {
		controller.setActive(playListSetUUID);
	}

	@Override
	public List<Tuple<Action, String>> getHotkeyComments() {
		List<Tuple<Action, String>> result = new LinkedList<Tuple<Action, String>>();
		for (Entry<UUID, String> descriptionMapEntry : commentMap.entrySet()) {
			System.out.println("Added " + uuidToActionMap.get(descriptionMapEntry.getKey()) + "," + descriptionMapEntry.getValue());
			result.add(new Tuple<Action, String>(uuidToActionMap.get(descriptionMapEntry.getKey()), descriptionMapEntry.getValue()));
		}
		return result;
	}

	@Override
	public void setHotkeyDescription(final Action action, final String description) {
		System.out.println("Putting " + action + "," + description);
		commentMap.put(action.getUUID(), description);
	}
}
