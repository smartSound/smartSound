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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import smartsound.common.PropertyMap;
import smartsound.plugins.player.IPlayer;
import smartsound.plugins.player.ISound;

/**
 * This class handles a play list. It does not only wraps the play list
 * entries, but also partly handles the determination of the entries' order.
 * @author André Becker
 *
 */
public class PlayList extends PlayListSetElement {

	protected boolean repeatList = false;
	protected boolean stopAfterEachSound = false;
	protected boolean randomizeList = false;
	private static int nameCounter = 1;
	private String name;
	private final PlayerControllerSettings playerControllerSettings = new PlayerControllerSettings();
	private float volume = 1.0f;
	private float parentVolume;

	private final List<PlayListItem> itemList = new ArrayList<PlayListItem>();
	private boolean autoPlay = false;

	public PlayList() {
		super(UUID.randomUUID());
		setName("Playlist " + nameCounter++);
	}

	/**
	 * Creates a <c>PlayList</c> from a <c>PropertyMap</c>.
	 * @param map The <c>PropertyMap</c>.
	 * @throws LoadingException If an error occurs during the creation of this
	 * 	<c>PlayList</c> or of one of the embedded <c>PlayListItem</c>s.
	 */
	public PlayList(final PropertyMap map) throws LoadingException {
		super(map.getMapUUID());
		if (!map.get("type").equals(getClass().getCanonicalName())) {
			throw new LoadingException();
		}

		name = map.get("name");
		repeatList = Boolean.parseBoolean(map.get("repeat"));
		stopAfterEachSound = Boolean.parseBoolean(map.get("stop_after_each_sound"));
		randomizeList = Boolean.parseBoolean(map.get("randomize"));
		autoPlay = Boolean.parseBoolean(map.get("autoplay"));

		playerControllerSettings.setFadeInLength(Integer.parseInt(map.get("fade_in_length")));
		playerControllerSettings.setFadeOutLength(Integer.parseInt(map.get("fade_out_length")));
		playerControllerSettings.setOverlapTime(Integer.parseInt(map.get("overlap_length")));
		setVolume(Float.parseFloat(map.get("max_volume")));
		playerControllerSettings.setRandomizeVolumeFrom(Float.parseFloat(map.get("volume_from")));
		playerControllerSettings.setRandomizeVolumeTo(Float.parseFloat(map.get("volume_to")));

		for (PropertyMap pMap : map.getNestedMaps()) {
			this.add(new PlayListItem(pMap));
		}
	}

	/**
	 * @return The <c>UUID</c> identifying this <c>PlayList</c>.
	 */
	@Override
	public UUID getUUID() {
		return super.getUUID();
	}

	/**
	 * @return The number of <c>PlayListItem</c>s in this <c>PlayList</c>.
	 */
	public int getSize() {
		return itemList.size();
	}

	/**
	 * Either resumes a paused <c>PlayListItem</c> or plays the first entry in
	 * the <c>PlayList</c>.
	 */
	@Override
	public void play() {
		stop();
		for (PlayListItem entry : itemList) {
			if (entry.isActive()) {
				entry.play();
				return;
			}
		}

		int index = 0;
		if (!itemList.isEmpty() && randomizeList) {
			index = new Random().nextInt(itemList.size());
		}
		
		play(index);
	}

	/**
	 * Plays a specific <c>PlayListItem</c>.
	 * @param index The <c>PlayListItem</c>'s index.
	 */
	public void play(final int index) {
		if (index < itemList.size()) {
			play(itemList.get(index));
		}
	}

	private void play(final PlayListItem item) {
		stop();
		item.play();
	}

	/**
	 * Stops all playing <c>PlayListItem</c>s.
	 */
	@Override
	public void stop() {
		for (PlayListItem entry : itemList) {
			entry.stop();
		}
		playListChanged();
	}

	/**
	 * Pauses all playing <c>PlayListItem</c>s.
	 */
	@Override
	public void pause() {

		for (PlayListItem entry : itemList) {
			entry.pause();
		}
		playListChanged();
	}

	/**
	 * Sets the <c>PlayList</c>'s volume.
	 * @param volume The volume as a value between 0 and 1.0.
	 */
	@Override
	public void setVolume(final float volume) {
		this.volume = volume;
		updateVolume();
	}

	private void updateVolume() {
		playerControllerSettings.setVolume(volume * parentVolume);
		playListChanged();
	}

	/**
	 * @return The <c>PlayList</c>'s volume.
	 */
	@Override
	public float getVolume() {
		return volume;
	}

	/**
	 * Inserts an instance of <c>ISound</c> at a given position.
	 * @param sound The <c>ISound</c>.
	 * @param index The index.
	 */
	public void insert(final ISound sound, final int index) {
		itemList.add(index, new PlayListItem(sound, playerControllerSettings, this));
		playListChanged();
	}

	/**
	 * Gets the time sounds are faded in.
	 * @result The fade in time in milliseconds.
	 */
	public int getFadeInLength() {
		return playerControllerSettings.getFadeInLength();
	}

	/**
	 * Sets the time sounds are faded in.
	 * @param fadeIn The fade in time in milliseconds.
	 */
	public void setFadeInLength(final int fadeIn) {
		playerControllerSettings.setFadeInLength(fadeIn);
		playListChanged();
	}

	/**
	 * Gets the time sounds are faded out.
	 * @result The fade out time in milliseconds.
	 */
	public int getFadeOutLength() {
		return playerControllerSettings.getFadeOutLength();
	}

	/**
	 * Sets the time sounds are faded out.
	 * @param fadeOut The fade out time in milliseconds.
	 */
	public void setFadeOutLength(final int fadeOutLength) {
		playerControllerSettings.setFadeOutLength(fadeOutLength);
		playListChanged();
	}

	/**
	 * Gets the time for overlapping. If <c>overlap</c> is set to a value n,
	 * then the last n milliseconds of one sound are played simoultaneously
	 * with the first n milliseconds of the next one.
	 * @result The overlap time.
	 */
	public int getOverlapTime() {
		return playerControllerSettings.getOverlapTime();
	}

	/**
	 * Sets the time for overlapping. If <c>overlap</c> is set to a value n,
	 * then the last n milliseconds of one sound are played simoultaneously
	 * with the first n milliseconds of the next one.
	 * @param overlap The overlap time.
	 */
	public void setOverlapTime(final int overlapTime) {
		playerControllerSettings.setOverlapTime(overlapTime);
		playListChanged();
	}

	/**
	 * Returns if this <c>PlayList</c> is repeated.
	 * @return <c>true</c> if the <c>PlayList</c> is repeated.
	 */
	public boolean isRepeatList() {
		return repeatList;
	}

	/**
	 * Sets if this <c>PlayList</c> is repeated. This setting has no effect
	 * if <c>isRandomizeList</c> returns <c>true</c>, if an entry is played
	 * repeatedly or if a loop is induced through setting of chaining of
	 * entries.
	 * @param repeat If set to <c>true</c> the <c>PlayList</c> will be
	 * 	repeated.
	 */
	public void setRepeatList(final boolean repeatList) {
		this.repeatList = repeatList;
		playListChanged();
	}

	/**
	 * If this returns <c>true</c> then the <c>PlayList</c> stops after each
	 * played entry if it is not repeated or chained with another entry.
	 * @return <c>true</c> if the <c>PlayList</c> stops after each sound.
	 */
	public boolean isStopAfterEachSound() {
		return stopAfterEachSound;
	}

	/**
	 * Sets if the <c>PlayList</c> stops after each played entry.
	 * @param stopAfterEachSound If set to <c>true</c> the <c>PlayList</c>
	 * 	stops after each played entry.
	 */
	public void setStopAfterEachSound(final boolean stopAfterEachSound) {
		this.stopAfterEachSound = stopAfterEachSound;
		playListChanged();
	}

	/**
	 * If this returns <c>true</c> the <c>PlayList</c> is randomized. This
	 * means that, as long as an entry is neither set to repeating nor chained
	 * with another entry, the next played entry ist determined randomly.
	 * @return <c>true</c> if the <c>PlayList</c> is randomized.
	 */
	public boolean isRandomizeList() {
		return randomizeList;
	}

	/**
	 * Sets randomization for a specific <c>PlayList</c>.
	 * @param randomize If <c>true</c> the <c>PlayList</c> is randomized.
	 */
	public void setRandomizeList(final boolean randomizeList) {
		this.randomizeList = randomizeList;
		playListChanged();
	}

	void playListChanged() {
		update();
	}

	/**
	 * Returns the next <c>PlayistItem</c> to be played, based on the
	 * <c>UUID</c> of the current one.
	 * @param current The <c>UUID</c> identifiying the current
	 * 	<c>PlayListITem</c>.
	 * @return The next <c>PlayListItem</c> or <c>null</c> if the end of the
	 * 	list has been reached and <c>isRepeatList()</c> is <c>false</c>.
	 */
	PlayListItem getNextEntry(final UUID current) {
		PlayListItem currentEntry = getEntry(current);

		if (currentEntry == null) {
			return null;
		}

		PlayListItem nextItem = null;

		if (stopAfterEachSound) {
			nextItem = null;
		} else if (randomizeList) {
			Random rnd = new Random();

			int i = rnd.nextInt(itemList.size());
			int j = itemList.indexOf(currentEntry);
			while (itemList.size() > 1 && i == j) {
				i = rnd.nextInt(itemList.size());
			}
			nextItem = itemList.get(i);
		} else {
			int i = itemList.indexOf(currentEntry) + 1;
			if (i < itemList.size()) {
				nextItem = itemList.get(i);
			} else {
				if (repeatList) {
					nextItem = itemList.get(0);
				}
			}
		}

		return nextItem;
	}

	/**
	 * Retrieves a new <c>IPlayer</c> instance for a given instance of
	 * <c>ISound</c> from the sound engine.
	 * @param sound The <c>ISound</c>.
	 * @return The new <c>IPlayer</c> instance
	 */
	IPlayer getPlayer(final ISound sound) {
		return SoundEngineSingleton.getInstance().getPlayer2D(sound);
	}

	/**
	 * Returns the <c>PlayListItem</c> object to a given <c>UUID</c>.
	 * @param uuid The <c>UUID</c> identifying the <c>PlayListItem</c>.
	 * @return
	 */
	public PlayListItem getEntry(final UUID uuid) {

		for (PlayListItem entry : itemList) {
			if (entry.getUUID().equals(uuid)) {
				return entry;
			}
		}

		return null;
	}

	/**
	 * Returns the <c>PlayListItem</c> object at a given index.
	 * @param index The <c>PlayListItem</c>'s index.
	 * @return The <c>PlayListItem</c>.
	 */
	public PlayListItem getEntryAt(final int index) {
		return itemList.get(index);
	}

	private void remove(final PlayListItem item, final boolean stop, final boolean removeChaining) {

		if (removeChaining) {
			for (PlayListItem pItem : itemList) {
				if (pItem.getChainWith() != null && pItem.getChainWith().equals(item.getUUID())) {
					pItem.setChainWith(null);
				}
			}
		}
		itemList.remove(item);

		if (stop) {
			item.stop();
		}

		item.setParent(null);
		playListChanged();
	}

	/**
	 * Removes the <c>PlayListItem</c> with a given <c>UUID</c>.
	 * @param itemUUID The <c>UUID</c> identifying the <c>PlayListItem</c>.
	 * @param stop If set to <c>true</c> the <c>PlayListItem</c> will be
	 * 	stopped in case it is currently played.
	 */
	public void remove(final UUID itemUUID, final boolean stop) {
		remove(itemUUID, stop, true);
	}

	/**
	 * Removes the <c>PlayListItem</c> with a given <c>UUID</c>.
	 * @param itemUUID The <c>UUID</c> identifying the <c>PlayListItem</c>.
	 * @param stop If set to <c>true</c> the <c>PlayListItem</c> will be
	 * 	stopped in case it is currently played.
	 * @param removeChaining If set to <c>true</c> all links to the
	 * 	<c>PlayListItem</c> will be removed.
	 */
	public void remove(final UUID itemUUID, final boolean stop, final boolean removeChaining) {
		remove(getPlayListItem(itemUUID), stop, removeChaining);
	}

	/**
	 * Removes the <c>PlayListItem</c> at a given index.
	 * @param index The <c>PlayListItem</c>'s index.
	 * @param stop If set to <c>true</c> the <c>PlayListItem</c> will be
	 * 	stopped in case it is currently played.
	 */
	public void remove(final int index, final boolean stop) {
		remove(itemList.get(index), stop, true);
	}

	/**
	 * Adds a <c>PlayListItem</c> to this <c>PlayList</c> at a given index.
	 * @param index The index.
	 * @param item The <c>PlayListItem</c>.
	 */
	public void add(final int index, final PlayListItem item) {
		item.setParent(this);
		item.setSettings(playerControllerSettings);
		itemList.add(index, item);
		playListChanged();
	}

	/**
	 * Adds a <c>PlayListItem</c> to the end of the <c>PlayList</c>.
	 * @param item The <c>PlayListItem</c>.
	 */
	public void add(final PlayListItem item) {
		item.setParent(this);
		item.setSettings(playerControllerSettings);
		itemList.add(item);
		playListChanged();
	}

	/**
	 * Returns the index of a <c>PlayListItem</c> identified by it's
	 * <c>UUID</c>.
	 * @param uuid The <c>UUID</c> identifying the <c>PlayListItem</c>.
	 * @return The index.
	 */
	public int getEntryIndex(final UUID uuid) {
		int i = 0;
		for (PlayListItem entry : itemList) {
			if (entry.getUUID().equals(uuid)) {
				return i;
			}
			i++;
		}
		return -1;
	}

	/**
	 * Returns the <c>UUID</c> of the <c>PlayListItem</c> at a given index.
	 * @param index  The index.
	 * @return The <c>UUID</c> identifying the <c>PlayListItem</c>.
	 */
	public UUID UUIDfromIndex(final int index) {
		if (index >= itemList.size()) {
			return null;
		}
		return itemList.get(index).getUUID();
	}

	/**
	 * Adds a new <c>PlayListItem</c> at a given index, created from a given
	 * file path.
	 * @param index The index.
	 * @param filePath The file path.
	 */
	public void add(final int index, final String filePath) {
		this.itemList.add(index, new PlayListItem(new FilePathSound(filePath), playerControllerSettings, this));
		playListChanged();
	}

	/**
	 * Adds a new <c>PlayListItem</c> at the end of the <c>PlayList</c>,
	 * created from a given file path.
	 * @param filePath The file path.
	 */
	public void add(final String filePath) {
		this.itemList.add(new PlayListItem(new FilePathSound(filePath), playerControllerSettings, this));
		playListChanged();
	}

	/**
	 * Returns the <c>PlayListItem</c> object identified by a given
	 * <c>UUID</c>.
	 * @param itemUUID The <c>UUID</c> identifying the <c>PlayListItem</c>.
	 * @return The <c>PlayListItem</c> or <c>null</c> if no item exists for
	 * 	this <c>UUID</c>.
	 */
	public PlayListItem getPlayListItem(final UUID itemUUID) {
		for (PlayListItem item : itemList) {
			if (item.getUUID().equals(itemUUID)) {
				return item;
			}
		}
		return null;
	}

	/**
	 * Returns if the given item is played repeatedly.
	 * @param itemUUID The <c>UUID</c> identifying the <c>PlayListItem</c>.
	 * @return <c>true</c> if the given item ist repeated, <c>false</c>
	 * 	otherwise.
	 */
	public boolean itemIsRepeating(final UUID itemUUID) {
		PlayListItem item = getEntry(itemUUID);
		if (item == null) {
			//TODO: Throw Exception
			return false;
		}
		return item.isRepeatItem();
	}

	/**
	 * Returns the name - e.g. the file name or the title - for a given item.
	 * @param itemUUID The <c>UUID</c> identifying the <c>PlayListItem</c>.
	 * @return The textual representation for the item.
	 */
	public String getItemName(final UUID itemUUID) {
		PlayListItem item = getEntry(itemUUID);
		if (item == null) {
			//TODO: Throw Exception
			return null;
		}
		return item.toString();
	}

	/**
	 * Returns the <c>UUID</c> of an item the given item is chained with.
	 * @param itemUUID The <c>UUID</c> identifying the entry.
	 * @return The other item's <c>UUID</c> or <c>null</c> if no chaining
	 * 	exists.
	 */
	public UUID getItemChainWith(final UUID itemUUID) {
		PlayListItem item = getEntry(itemUUID);
		if (item == null) {
			//TODO: Throw Exception
			return null;
		}
		return item.getChainWith();
	}

	/**
	 * Sets the the chaining between two items.
	 * @param itemUUID The <c>UUID</c> identifying the item.
	 * @param chainWith The <c>UUID</c> of the item to be chained with or
	 * <c>null</c> to disable chaining.
	 */
	public void setItemChainWith(final UUID itemUUID, final UUID chainWith) {
		PlayListItem item = getEntry(itemUUID);
		if (item == null) {
			//TODO: Throw Exception
			return;
		}
		item.setChainWith(chainWith);
	}

	/**
	 * Returns <c>true</c> if the specified item is not stopped (meaning
	 * playing or paused).
	 * @param itemUUID The item's <c>UUID</c>.
	 * @return <c>false</c> if the item is stopped - <c>true</c> otherwise.
	 */
	public boolean itemIsActive(final UUID itemUUID) {
		PlayListItem item = getEntry(itemUUID);
		if (item == null) {
			//TODO: Throw Exception
			return false;
		}
		return item.isActive();
	}

	/**
	 * Sets if the given item is played repeatedly.
	 * @param itemUUID The <c>UUID</c> identifying the item.
	 * @param isRepeating Set to <c>true</c> to repeat the item.
	 */
	public void setItemIsRepeating(final UUID itemUUID, final boolean isRepeating) {
		PlayListItem item = getEntry(itemUUID);
		if (item == null) {
			//TODO: Throw Exception
			return;
		}
		item.setRepeatItem(isRepeating);
	}

	/**
	 * Sets the minimum volume for this <c>PlayList</c>. The actual
	 * volume of a played item is chosen uniformly from the interval
	 * [<c>randomizeVolumeFrom</c>, <c>randomizeVolumeTo</c>].
	 * @param from The minimum volume in the range from 0 to 1.0.
	 */
	public void setRandomizeVolumeFrom(final float from) {
		playerControllerSettings.setRandomizeVolumeFrom(from);
		playListChanged();
	}

	/**
	 * Sets the maximum volume this <c>PlayList</c>. The actual
	 * volume of a played item is chosen uniformly from the interval
	 * [<c>randomizeVolumeFrom</c>, <c>randomizeVolumeTo</c>].
	 * @param to The maximum volume in the range from 0 to 1.0.
	 */
	public void setRandomizeVolumeTo(final float to) {
		playerControllerSettings.setRandomizeVolumeTo(to);
		playListChanged();
	}

	/**
	 * Gets the minimum volume this <c>PlayList</c>. The actual
	 * volume of a played item is chosen uniformly from the interval
	 * [<c>randomizeVolumeFrom</c>, <c>randomizeVolumeTo</c>].
	 * @return The minimum volume in the range from 0 to 1.0.
	 */
	public float getRandomizeVolumeFrom() {
		return playerControllerSettings.getRandomizeVolumeFrom();
	}

	/**
	 * Gets the maximum volume for a specific <c>PlayList</c>. The actual
	 * volume of a played item is chosen uniformly from the interval
	 * [<c>randomizeVolumeFrom</c>, <c>randomizeVolumeTo</c>].
	 * @return The maximum volume in the range from 0 to 1.0.
	 */
	public float getRandomizeVolumeTo() {
		return playerControllerSettings.getRandomizeVolumeTo();
	}

	/**
	 * @return A <c>PropertyMap</c> containing information for restoring this
	 * 	<c>PlayList</c> and all embedded objects.
	 */
	@Override
	public PropertyMap getPropertyMap() {
		PropertyMap map = new PropertyMap(getUUID());

		map.put("type", getClass().getCanonicalName());
		map.put("name", name);
		map.put("repeat", String.valueOf(repeatList));
		map.put("stop_after_each_sound", String.valueOf(stopAfterEachSound));
		map.put("randomize", String.valueOf(randomizeList));
		map.put("fade_in_length", String.valueOf(playerControllerSettings.getFadeInLength()));
		map.put("fade_out_length", String.valueOf(playerControllerSettings.getFadeOutLength()));
		map.put("overlap_length", String.valueOf(playerControllerSettings.getOverlapTime()));
		map.put("max_volume", String.valueOf(playerControllerSettings.getVolume()));
		map.put("volume_from", String.valueOf(playerControllerSettings.getRandomizeVolumeFrom()));
		map.put("volume_to", String.valueOf(playerControllerSettings.getRandomizeVolumeTo()));
		map.put("autoplay", String.valueOf(autoPlay));

		for (PlayListItem item : itemList) {
			map.addPropertyMap(item.getPropertyMap());
		}
		return map;
	}

	@Override
	public void setParentVolume(final float volume) {
		parentVolume = volume;
		updateVolume();
	}

	@Override
	public boolean getAutoPlay() {
		return autoPlay ;
	}

	@Override
	public void setAutoPlay(final boolean autoPlay) {
		this.autoPlay = autoPlay;
		playListChanged();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
		playListChanged();
	}

	@Override
	public PlayListSet getPlayListSet(final UUID playListSetUUID) {
		return null;
	}

	@Override
	public UUID getParent(final UUID child) {
		return null;
	}

	@Override
	public void remove(final UUID uuid) {
		remove(uuid, true);
	}

	@Override
	public boolean isActive() {
		for (PlayListItem item : itemList) {
			if (item.isActive())
				return true;
		}
		return false;
	}

	@Override
	public void dispose() {
		removeAllObservers();
	}
}
