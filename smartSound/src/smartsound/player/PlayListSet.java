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

import smartsound.common.PropertyMap;

public class PlayListSet extends PlayListSetElement {

	private static int instanceCounter = 1;
	private final Map<UUID, PlayListSetElement> elements = new HashMap<UUID, PlayListSetElement>();
	private float volume = 1.0f;
	private float parentVolume = 1.0f;
	private boolean autoPlay = false;
	private String name;



	public PlayListSet() {
		super(UUID.randomUUID());

		name = "Scene " + (instanceCounter++);
	}

	public PlayListSet(final PropertyMap pMap) throws LoadingException {
		super(pMap.getMapUUID());
		if (!pMap.get("type").equals(getClass().getCanonicalName()))
			throw new LoadingException();

		name = pMap.get("name");
		volume = Float.valueOf(pMap.get("volume"));
		autoPlay = Boolean.valueOf(pMap.get("autoplay"));

		PlayListSet set;
		PlayList list;
		for (PropertyMap map : pMap.getNestedMaps()) {
			if (map.get("type").equals(getClass().getCanonicalName())) {
				set = new PlayListSet(map);
				addPlayList(set);
			} else if (map.get("type").equals(PlayList.class.getCanonicalName())) {
				list = new PlayList(map);
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

	public List<PlayListSet> getNestedSets() {
		List<PlayListSet> result = new LinkedList<PlayListSet>();
		for (PlayListSetElement elem : elements.values()) {
			if (elem instanceof PlayListSet) {
				result.add((PlayListSet) elem);
			}
		}
		return result;
	}

	public List<PlayList> getNestedPlayLists() {
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

	@Override
	public PlayListSet getPlayListSet(final UUID playListSetUUID) {
		PlayListSetElement element = elements.get(playListSetUUID);
		if (element != null && element instanceof PlayListSet) {
			return (PlayListSet) element;
		}

		PlayListSet set = null;
		for (PlayListSetElement e : elements.values()) {
			set = e.getPlayListSet(playListSetUUID);
			if (set != null) {
				break;
			}
		}

		return set;
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
	public UUID getParent(final UUID child) {
		if (elements.containsKey(child))
			return getUUID();

		return null;
	}

	@Override
	public void remove(final UUID uuid) {
		elements.remove(uuid);
		update();
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
}
