package smartsound.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import smartsound.common.IElement;
import smartsound.common.IElement.NameValuePair;

public class ElementManager {
	private ElementManager() {}

	private static ElementManager inst;

	private final Map<UUID, IElement> elementMap = new HashMap<UUID, IElement>();

	private void act_(final UUID element, final String... actionTypes) {
		if (elementMap.containsKey(element)) {
			elementMap.get(element).act(actionTypes);
		}
	}

	private UUID add_(final UUID parent, final String elementType, final Object... params) {
		if (elementMap.containsKey(parent)) {
			IElement addedElement = elementMap.get(parent).add(elementType, params);
			System.out.println(addedElement);
			if (addedElement != null) {
				elementMap.put(addedElement.getUUID(), addedElement);
				return addedElement.getUUID();
			}
		}

		return null;
	}

	private void add_(final IElement element) {
		elementMap.put(element.getUUID(), element);
	}

	private void remove_(final UUID uuid) {
		if (elementMap.containsKey(uuid)) {
			elementMap.get(uuid).remove();
		}
		elementMap.remove(uuid);
	}

	private NameValuePair[] get_(final UUID uuid, final String... propertyNames) {
		if (!elementMap.containsKey(uuid))
			return null;

		return elementMap.get(uuid).get(propertyNames);
	}

	private void set_(final UUID uuid, final NameValuePair... params) {
		if (elementMap.containsKey(uuid)) {
			elementMap.get(uuid).set(params);
		}
	}

	private static ElementManager getInstance() {
		if (inst == null) {
			inst = new ElementManager();
		}
		return inst;
	}

	public static void act(final UUID element, final String... actionTypes) {
		getInstance().act_(element, actionTypes);
	}

	public static UUID add(final UUID parent, final String elementType, final Object... params) {
		return getInstance().add_(parent,  elementType, params);
	}

	public static void add(final IElement element) {
		getInstance().add_(element);
	}

	public static void remove(final UUID uuid) {
		getInstance().remove_(uuid);
	}

	public static NameValuePair[] get(final UUID uuid, final String... propertyNames) {
		return getInstance().get_(uuid, propertyNames);
	}

	public static void set(final UUID uuid, final NameValuePair... params) {
		getInstance().set_(uuid, params);
	}

}
