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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import smartsound.common.IAddObserver;
import smartsound.common.IChangeObserver;
import smartsound.common.IElement;
import smartsound.common.IRemoveObserver;
import smartsound.common.PropertyMap;
import smartsound.controller.ElementManager;

public class PlayListSet extends PlayListSetElement {

	private static int instanceCounter = 1;
	private final Map<UUID, PlayListSetElement> elements = new HashMap<UUID, PlayListSetElement>();
	private float volume = 1.0f;
	private float parentVolume = 1.0f;
	private boolean autoPlay = false;
	private String name;

	private final List<IAddObserver> addObservers = new LinkedList<IAddObserver>();
	private final List<IRemoveObserver> removeObservers = new LinkedList<IRemoveObserver>();
	private final List<IChangeObserver> changeObservers = new LinkedList<IChangeObserver>();

	public PlayListSet(final PlayListSetElement parent) {
		super(UUID.randomUUID(), parent);

		name = "Scene " + (instanceCounter++);
	}

	public PlayListSet(final PropertyMap pMap, final PlayListSetElement parent) throws LoadingException {
		super(pMap.getMapUUID(), parent);
		if (!pMap.get("type").equals(getClass().getCanonicalName()))
			throw new LoadingException();

		name = pMap.get("name");
		volume = Float.valueOf(pMap.get("volume"));
		autoPlay = Boolean.valueOf(pMap.get("autoplay"));

		PlayListSet set;
		PlayList list;
		for (PropertyMap map : pMap.getNestedMaps()) {
			if (map.get("type").equals(getClass().getCanonicalName())) {
				set = new PlayListSet(map, this);
				addPlayList(set);
			} else if (map.get("type").equals(PlayList.class.getCanonicalName())) {
				list = new PlayList(map, this);
				addPlayList(list);
			}
		}
	}

	public void addPlayList(final PlayListSetElement element) {
		element.setParentVolume(volume * parentVolume);
		elements.put(element.getUUID(), element);
		update();
	}

	@Override
	public UUID getUUID() {
		return super.getUUID();
	}

	@Override
	public void play() {
		for (PlayListSetElement element : elements.values()) {
			if (element.getAutoPlay())
				element.play();
		}
	}

	@Override
	public void pause() {
		for (PlayListSetElement element : elements.values()) {
			element.pause();
		}
	}

	@Override
	public void stop() {
		for (PlayListSetElement element : elements.values()) {
			element.stop();
		}
	}

	@Override
	public void setParentVolume(final float volume) {
		parentVolume = volume;
	}

	@Override
	public boolean getAutoPlay() {
		return autoPlay;
	}

	@Override
	public void setAutoPlay(final boolean autoPlay) {
		this.autoPlay = autoPlay;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
		update();
	}

	@Override
	public PropertyMap getPropertyMap() {
		PropertyMap result = new PropertyMap(getUUID());
		result.put("type", getClass().getCanonicalName());

		result.put("autoplay", String.valueOf(autoPlay));
		result.put("name", name);
		result.put("volume", String.valueOf(volume));
		for (PlayListSetElement element : elements.values()) {
			result.addPropertyMap(element.getPropertyMap());
		}

		return result;
	}

	protected List<PlayListSet> getNestedSets() {
		List<PlayListSet> result = new LinkedList<PlayListSet>();
		for (PlayListSetElement elem : elements.values()) {
			if (elem instanceof PlayListSet) {
				result.add((PlayListSet) elem);
			}
		}
		return result;
	}

	protected List<PlayList> getNestedPlayLists() {
		List<PlayList> result = new LinkedList<PlayList>();
		for (PlayListSetElement elem : elements.values()) {
			if (elem instanceof PlayList) {
				result.add((PlayList) elem);
			}
		}
		return result;
	}

	public PlayList getPlayList(final UUID playListUUID) {
		PlayListSetElement element = elements.get(playListUUID);
		if (element != null && element instanceof PlayList) {
			return (PlayList) element;
		}
		PlayList playList = null;
		for (PlayListSetElement e : elements.values()) {
			if (e instanceof PlayListSet)
				playList = ((PlayListSet) e).getPlayList(playListUUID);

			if (playList != null)
				break;
		}
		return playList;
	}

	public boolean addPlayList(final UUID parentSetUUID, final PlayListSetElement playListSetElement) {
		PlayListSetElement element = elements.get(parentSetUUID);

		if (element != null && element instanceof PlayListSet) {
			((PlayListSet) element).addPlayList(playListSetElement);
			return true;
		}

		for (PlayListSetElement e : elements.values())
			if (e instanceof PlayListSet
					&& ((PlayListSet) e).addPlayList(parentSetUUID, playListSetElement))
				return true;

		return false;

	}

	@Override
	public boolean isActive() {
		for (PlayListSetElement element : elements.values())
			if (element.isActive())
				return true;
		return false;
	}

	@Override
	public void setVolume(final float volume) {
		this.volume = volume;
		for (PlayListSetElement element : elements.values()) {
			element.setParentVolume(volume);
		}
	}

	@Override
	public float getVolume() {
		return volume;
	}

	@Override
	public void dispose() {
		for (PlayListSetElement element : elements.values())
			element.dispose();
		removeAllObservers();
	}

	@Override
	public IElement add(final String elementType, final Object... params) {
		IElement result = null;
		switch (elementType) {
		case "PLAYLIST" :
			PlayList pl = new PlayList(this);
			addPlayList(pl);
			result = pl;
			ElementManager.add(pl);
			notifyAddObservers(pl.getUUID());
			break;
		case "PLAYLISTSET" :
			PlayListSet pls = new PlayListSet(this);
			addPlayList(pls);
			result = pls;
			notifyAddObservers(pls.getUUID());
			break;
		case "ADDOBSERVER" :
			addObserver((IAddObserver) params[0]);
			break;
		case "CHANGEOBSERVER":
			addObserver((IChangeObserver) params[0]);
			break;
		case "REMOVEOBSERVER":
			addObserver((IRemoveObserver) params[0]);
			break;
		default:
			System.err.println("Unknown element type '" + elementType + "'");
		}
		return result;
	}

	private void addObserver(final IAddObserver obs) {
		addObservers.add(obs);
	}

	private void addObserver(final IRemoveObserver obs) {
		removeObservers.add(obs);
	}

	private void addObserver(final IChangeObserver obs) {
		changeObservers.add(obs);
		fullUpdate(obs);
	}

	private void fullUpdate(final IChangeObserver obs) {
		String[] propertyNames = { "NAME", "VOLUME", "AUTOPLAY", "PARENT", "ACTIVE"};

		NameValuePair[] pairs = get(propertyNames);

		Map<String, Object> values = new HashMap<String, Object>();
		for (NameValuePair pair : pairs) {
			values.put(pair.name, pair.value);
		}
		obs.elementChanged(getUUID(), values);
	}

	private void notifyAddObservers(final UUID newElementUUID) {
		for (IAddObserver obs : addObservers) {
			obs.elementAdded(getUUID(), newElementUUID);
		}
	}

	private void notifyRemoveObservers() {
		for (IRemoveObserver obs : removeObservers) {
			obs.elementRemoved(getUUID());
		}
	}

	@Override
	protected void notifyChangeObservers(final String... propertyNames) {
		NameValuePair[] pairs = get(propertyNames);

		Map<String, Object> values = new HashMap<String, Object>();
		for (NameValuePair pair : pairs) {
			values.put(pair.name, pair.value);
		}
		notifyChangeObservers(values);
	}

	private void notifyChangeObservers(final Map<String, Object> values) {
		for (IChangeObserver obs : changeObservers) {
			obs.elementChanged(getUUID(), values);
		}
	}

	@Override
	public void remove() {
		for (IElement child : elements.values()) {
			ElementManager.remove(child.getUUID());
		}
	}

	@Override
	public Object get(final String propertyName) {
		switch (propertyName) {
		case "PLAYLISTS":
			return getNestedPlayLists();
		case "PLAYLISTSETS":
			return getNestedSets();
		default:
			return super.get(propertyName);
		}
	}

	@Override
	public void set(final String propertyName, final Object value) {
		switch (propertyName) {
		default:
			super.set(propertyName, value);
		}
	}
}
