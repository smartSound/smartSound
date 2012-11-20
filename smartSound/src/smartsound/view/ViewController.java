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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import smartsound.common.IElement.NameValuePair;
import smartsound.common.IObserver;
import smartsound.common.PropertyMap;
import smartsound.common.Tuple;
import smartsound.controller.AbstractController;
import smartsound.player.LoadingException;
import smartsound.view.gui.GUIController;

public class ViewController extends AbstractViewController implements IObserver
{

	private final AbstractController controller;
	private final List<GUIController> guis;
	private final Map<UUID, Map<String, Set<Hotkey>>> hotkeyMap;
	private final Map<UUID, Layout> layoutMap = new HashMap<UUID, Layout>();
	private final Map<UUID, Hotkey> uuidToActionMap = new HashMap<UUID, Hotkey>();
	private final Map<UUID, String> commentMap = new HashMap<UUID, String>();

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
		hotkeyMap = new HashMap<UUID, Map<String, Set<Hotkey>>>();
		this.controller = controller;
		LayoutManager.setViewController(this);

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
	public void addSetHotkey(final String hotkey, final UUID elementUUID, final Map<String, Object> values)
	{

		//TODO: Fix
		UUID key = elementUUID;
		if (!hotkeyMap.containsKey(elementUUID))
			hotkeyMap.put(key, new HashMap<String, Set<Hotkey>>());
		Map<String, Set<Hotkey>> subMap = hotkeyMap.get(key);

		if (!subMap.containsKey(hotkey)) {
			subMap.put(hotkey, new HashSet<Hotkey>());
		}

		Hotkey h = new SetHotkey(hotkey, elementUUID, values);
		subMap.get(hotkey).add(h);
		uuidToActionMap.put(h.getElementUUID(), h);
	}

	@Override
	public void addActHotkey(final String hotkey, final UUID elementUUID, final String... actionTypes) {

	}

	@Override
	public void executeHotkey(final String hotkey)
	{
		executeHotkey(null, hotkey);

		NameValuePair[] pairs = get((UUID) null, "ACTIVESET");
		assert pairs.length == 1;
		assert pairs[0].value instanceof UUID;

		UUID activeSet = (UUID) pairs[0].value;

		for (UUID uuid : hotkeyMap.keySet()) {
			if (uuid != null && activeSet.equals(get(uuid, "ROOTPARENT")[0].value)) {
				executeHotkey(uuid, hotkey);
			}
		}

	}

	private void executeHotkey(final UUID playListSetUUID, final String hotkey) {
		if (hotkeyMap.containsKey(playListSetUUID)
				&& hotkeyMap.get(playListSetUUID).containsKey(hotkey))
			for (Hotkey h : hotkeyMap.get(playListSetUUID).get(hotkey))
				h.executeHotkey(this);
	}

	@Override
	public List<Tuple<String, Action>> getHotkeys(final UUID sceneUUID, final Action parent) {
		/*List<Tuple<String,Action>> result = new LinkedList<Tuple<String,Action>>();

		for (Entry<UUID, Map<String, Set<Hotkey>>> mapEntry : hotkeyMap.entrySet())
			if (sceneUUID == null || sceneUUID.equals(mapEntry.getKey()))
				for (Entry<String,Set<Action>> entry : mapEntry.getValue().entrySet()) {
					for (Action action : entry.getValue()) {
						if (parent == null || parent.isParentOf(action))
							result.add(new Tuple<String,Action>(entry.getKey(), action));
					}

				}
		return result;*/
		return null;
	}

	@Override
	public List<Tuple<String,Action>> getHotkeys(final UUID sceneUUID) {
		/*List<Tuple<String, Action>> result = new LinkedList<Tuple<String, Action>>();

		if (hotkeyMap.containsKey(sceneUUID))
			for (Entry<String, Set<Action>> entry : hotkeyMap.get(sceneUUID).entrySet()) {
				for (Action action : entry.getValue()) {
					result.add(new Tuple<String, Action>(entry.getKey(), action));
				}

			}
		return result;*/
		return null;
	}

	@Override
	public void removeAllHotkeys() {
		hotkeyMap.clear();
	}

	@Override
	public void removeHotkey(final UUID elementUUID, final String hotkey, final Action action) {
		//TODO: Fix
		/*
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
		 */
	}

	@Override
	public PropertyMap getPropertyMap() {
		PropertyMap result = new PropertyMap(UUID.randomUUID());
		/*result.put("type", ViewController.class.getCanonicalName());

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
		}*/

		//result.put("rootlayout", rootLayout.getUUID().toString());
		//result.addPropertyMap(rootLayout.getPropertyMap());
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
	public void loadFromPropertyMap(final PropertyMap pMap)
			throws LoadingException {
		if (!pMap.get("type").equals(getClass().getCanonicalName())) {
			throw new LoadingException();
		}
		/*
		 * removeAllHotkeys(); layoutMap.clear(); uuidToActionMap.clear();
		 * Layout layout; Action action; UUID rootLayoutUUID =
		 * UUID.fromString(pMap.get("rootlayout")); for (PropertyMap map :
		 * pMap.getNestedMaps()) { if
		 * (map.get("type").equals(Action.class.getCanonicalName())) { action =
		 * propertyMapToAction(map); uuidToActionMap.put(action.getUUID(),
		 * action); } else if
		 * (map.get("type").equals(Layout.class.getCanonicalName())) { layout =
		 * new Layout(this, map); if (layout.getUUID().equals(rootLayoutUUID)) {
		 * rootLayout = layout; } else { layoutMap.put(layout.getParentUUID(),
		 * layout); } controller.addObserver(this, layout.getParentUUID());
		 * controller.addObserver(layout, layout.getParentUUID()); } }
		 * 
		 * String keyString; String[] keySplit; UUID uuidKey; String hotkey;
		 * Map<String, Set<Action>> actionSetMap; for (String key :
		 * pMap.getKeys()) { if (key.startsWith("hotkey:")) { keyString =
		 * key.replaceFirst("hotkey:", ""); keySplit = keyString.split(",");
		 * assert keySplit.length == 2;
		 * 
		 * uuidKey = keySplit[0].equals("null") ? null :
		 * UUID.fromString(keySplit[0]); hotkey = keySplit[1];
		 * 
		 * if (!hotkeyMap.containsKey(uuidKey)) hotkeyMap.put(uuidKey, new
		 * HashMap<String, Set<Action>>());
		 * 
		 * actionSetMap = hotkeyMap.get(uuidKey);
		 * 
		 * if (!actionSetMap.containsKey(hotkey)) { actionSetMap.put(hotkey, new
		 * HashSet<Action>()); } for (String split : pMap.get(key).split(" ")) {
		 * actionSetMap
		 * .get(hotkey).add(uuidToActionMap.get(UUID.fromString(split))); } }
		 * else if (key.startsWith("comment:")) { keyString =
		 * key.replaceFirst("comment:", "");
		 * commentMap.put(UUID.fromString(keyString), pMap.get(key)); } }
		 */
	}

	@Override
	public String getHotkey(final Action action) {
		/*
		for (Map<String, Set<Action>> actionSetMap : hotkeyMap.values())
			for (Entry<String,Set<Action>> entry : actionSetMap.entrySet()) {
				if (entry.getValue().contains(action)) {
					return entry.getKey();
				}
			}
		 */
		return null;
	}

	//	@Override
	//	public void removeHotkey(final Action action) {
	//		Map<String, Set<Action>> actionSetMap = null;
	//		for (Entry<UUID, Map<String, Set<Action>>> hotkeyMapEntry : hotkeyMap.entrySet()) {
	//			actionSetMap = hotkeyMapEntry.getValue();
	//			for (Entry<String,Set<Action>> entry : actionSetMap.entrySet()) {
	//				if(entry.getValue().remove(action)) {
	//					if (entry.getValue().isEmpty()) {
	//						hotkeyMap.remove(hotkeyMapEntry.getKey());
	//					}
	//					return;
	//				}
	//			}
	//		}
	//	}

	@Override
	public void reloadView() {
		for (GUIController gui : guis) {
			gui.reload();
		}
	}

	@Override
	public void remove(final UUID uuid) {
		controller.remove(uuid);
	}

	@Override
	public void update(final UUID uuid) {
		//TODO: Fix

		/*
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
		 */
	}

	@Override
	public List<Tuple<Action, String>> getHotkeyComments() {
		/*
		List<Tuple<Action, String>> result = new LinkedList<Tuple<Action, String>>();
		for (Entry<UUID, String> descriptionMapEntry : commentMap.entrySet()) {
			System.out.println("Added " + uuidToActionMap.get(descriptionMapEntry.getKey()) + "," + descriptionMapEntry.getValue());
			result.add(new Tuple<Action, String>(uuidToActionMap.get(descriptionMapEntry.getKey()), descriptionMapEntry.getValue()));
		}
		return result;*/
		return null;
	}

	@Override
	public void setHotkeyDescription(final Action action, final String description) {
		System.out.println("Putting " + action + "," + description);
		commentMap.put(action.getUUID(), description);
	}

	@Override
	public UUID add(final UUID parent, final String elementType, final Object... params) {
		return controller.add(parent, elementType, params);
	}

	@Override
	public NameValuePair[] get(final UUID uuid, final String... propertyNames) {
		return controller.get(uuid, propertyNames);
	}

	@Override
	public void set(final UUID uuid, final NameValuePair... params) {
		controller.set(uuid, params);
	}

	@Override
	public void act(final UUID uuid, final String... actionTypes) {
		controller.act(uuid, actionTypes);
	}
}
