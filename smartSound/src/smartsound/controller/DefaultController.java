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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import smartsound.common.IAddObserver;
import smartsound.common.IElement;
import smartsound.common.IObserver;
import smartsound.common.PropertyMap;
import smartsound.player.LoadingException;
import smartsound.player.PlayListSet;
import smartsound.view.AbstractViewController;
import smartsound.view.ViewController;
import smartsound.view.gui.GUIController;

/**
 * Default implementation of <c>AbstractController</c>. Creates a view for
 * user input.
 * @author André Becker
 *
 */
public class DefaultController extends AbstractController implements IElement {

	private final UUID controllerUUID = UUID.randomUUID();
	private final AbstractViewController viewController;
	private final Map<UUID, PlayListSet> rootPlayListSets = new HashMap<UUID, PlayListSet>();
	private final List<IAddObserver> addObservers = new LinkedList<IAddObserver>();
	private UUID activeSet = null;
	private float mainVolume = 1.0f;
	private boolean autoPlay = false;

	/**
	 * 
	 * @param parent The <c>JFrame</c> to which the controller adds its view
	 * 	elements.
	 */
	public DefaultController() {
		ElementManager.add(this);
		viewController = new ViewController(this);
		viewController.addGUI(new GUIController(viewController));
		UUID set = ElementManager.add(null, "PLAYLISTSET");
		setActive(set);
	}

	@Override
	public void save(final String savePath) {
		getPropertyMap().saveToIni(savePath);
	}

	@Override
	public void load(final String loadPath) {

		for (PlayListSet set : rootPlayListSets.values()) {
			set.remove();
		}
		assert rootPlayListSets.isEmpty();


		PropertyMap map = new PropertyMap(loadPath);
		PlayListSet set;
		boolean firstSet = true;
		try {
			for (PropertyMap pMap : map.getNestedMaps()) {
				if (pMap.get("type").equals(PlayListSet.class.getCanonicalName())) {
					set = new PlayListSet(pMap, this);
					rootPlayListSets.put(set.getUUID(), set);
					ElementManager.add(set);
					if (firstSet) { //TODO: Check concerning autoplay
						firstSet = false;
						activeSet = set.getUUID();
					}
				} else if (pMap.get("type").equals(ViewController.class.getCanonicalName())) {
					viewController.loadFromPropertyMap(pMap);
				}
			}
		} catch (LoadingException e) {
			e.printStackTrace();
		}

		viewController.reloadView();
	}

	protected List<UUID> getPlayListSetUUIDs() {
		return new LinkedList<UUID>(rootPlayListSets.keySet());
	}


	//TODO: ??
	@Override
	public void update(final UUID arg0) {
		// TODO Auto-generated method stub

	}

	//TODO: Needed?
	protected void setActive(final UUID playListSetUUID) {
		activeSet = playListSetUUID;
	}


	@Override
	public IElement add(final String elementType, final Object... params) {
		switch (elementType) {
		case "PLAYLISTSET":
			PlayListSet set = new PlayListSet(this);
			rootPlayListSets.put(set.getUUID(), set);
			ElementManager.add(set);
			notifyAddObservers(set.getUUID());
			return set;
		case "ADDOBSERVER":
			assert params.length == 1;
			assert params[0] instanceof IAddObserver;
			addObservers.add((IAddObserver) params[0]);
			fullUpdate((IAddObserver) params[0]);
			break;
		}
		return null;
	}

	private void fullUpdate(final IAddObserver obs) {
		for (UUID uuid : rootPlayListSets.keySet()) {
			obs.elementAdded(null, uuid);
		}
	}

	private void notifyAddObservers(final UUID newUUID) {
		for (IAddObserver obs : addObservers) {
			obs.elementAdded(null, newUUID);
		}
	}

	@Override
	public void remove() {
		//TODO: Implement shutdown
		throw new UnsupportedOperationException();
	}

	@Override
	protected Object get(final String propertyName) {
		switch (propertyName) {
		case "PLAYLISTSETS":
			return getPlayListSetUUIDs();
		case "ACTIVESET":
			for (PlayListSet set : rootPlayListSets.values()) {
				if (set.isActive())
					return set.getUUID();
			}
			return null;
		default:
			return super.get(propertyName);
		}
	}

	@Override
	protected void act(final String actionType) {
		switch(actionType) {
		default:
			super.act(actionType);
		}
	}

	@Override
	protected void set(final String propertyName, final Object value) {
		switch (propertyName) {
		default:
			super.set(propertyName, value);
		}
	}

	//TODO: Needed?
	@Override
	public void addObserver(final IObserver observer, final UUID uuid) {
		// TODO Auto-generated method stub
	}

	@Override
	public void dispose() {
		// TODO needed?

	}

	@Override
	protected String getName() {
		return "ROOT";
	}

	@Override
	protected float getVolume() {
		return this.mainVolume;
	}

	@Override
	protected void setVolume(final float volume) {
		this.mainVolume = volume;
	}

	@Override
	protected void setParentVolume(final float volume) {
		//Do nothing, no parent existing.
		return;
	}

	@Override
	public void play() {
		for (PlayListSet pls : rootPlayListSets.values()) {
			if (pls.getAutoPlay()) {
				pls.play();
				return;
			}
		}
	}

	@Override
	protected boolean getAutoPlay() {
		return autoPlay;
	}

	@Override
	protected void setAutoPlay(final boolean autoPlay) {
		this.autoPlay = autoPlay;
	}

	@Override
	protected boolean isActive() {
		for (PlayListSet pls : rootPlayListSets.values()) {
			if (pls.isActive())
				return true;
		}
		return false;
	}

	@Override
	public void pause() {
		for (PlayListSet pls: rootPlayListSets.values()) {
			pls.pause();
		}
	}

	@Override
	public void stop() {
		for (PlayListSet pls: rootPlayListSets.values()) {
			pls.stop();
		}
	}

	@Override
	protected void setName(final String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public PropertyMap getPropertyMap() {
		PropertyMap map = new PropertyMap(controllerUUID);

		for (PlayListSet set : rootPlayListSets.values()) {
			map.addPropertyMap(set.getPropertyMap());
		}

		map.addPropertyMap(viewController.getPropertyMap());

		return map;
	}

	@Override
	protected void notifyChangeObservers(final String... propertyNames) {
		// TODO: Do nothing?
	}

}
