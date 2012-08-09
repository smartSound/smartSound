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

import java.util.List;
import java.util.UUID;

import smartsound.common.IObserver;
import smartsound.common.Observable;
import smartsound.player.ItemData;

/**
 * This abstract class is a facade to divide model and view.
 * @author André Becker
 *
 */
public abstract class AbstractController extends Observable implements IObserver {

	public AbstractController() {
		super(null);
	}

	/**
	 * Adds an observer to an <c>Observable</c>.
	 * @param observer The observer.
	 * @param uuid The <c>UUID</c> identifying the <c>Observable</c>.
	 */
	public abstract void addObserver(IObserver observer, UUID uuid);

	/**
	 * Returns an object wrapping information about a specific entry of a
	 * <c>PlayList</c>, identified by its index.
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 * @param index Index of the entry in the given <c>PlayList</c>.
	 * @return An <c>ItemData</c> object which wraps information about the
	 * 			entry.
	 */
	public abstract ItemData getItemData(UUID playListUUID, int index);

	/**
	 * Gets the number of entries in a given <c>PlayList</c>
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 * @return The number of entries.
	 */
	public abstract int getSize(UUID playListUUID);

	/**
	 * Adds an entry, identified by a file path, to a given <c>PlayList</c>.
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 * @param index The index where the new entry shall be inserted.
	 * @param filePath The path to the file.
	 */
	public abstract void addItem(UUID playListUUID, int index, String filePath);

	/**
	 * Adds an entry, identified by a file path, to a given <c>PlayList</c>.
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 * @param filePath
	 */
	public abstract void addItem(UUID playListUUID, String filePath);

	/**
	 * Removes an entry from a given <c>PlayList</c> from a specific index.
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 * @param index The index of the entry to be removed.
	 * @param stop If the entry denoted by <c>index</c> is currently played
	 * 			and this parameter is set to <c>true</c> playing will be
	 * 			stopped. Else the entry is played to its end.
	 */
	public abstract void removeItem(UUID playListUUID, int index, boolean stop);

	/**
	 * Plays a specific entry in a <c>PlayList</c>.
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 * @param index The entry's index.
	 */
	public abstract void play(UUID playListUUID, int index);

	/**
	 * Plays the first entry of the given <c>PlayList</c>.
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 */
	public abstract void play(UUID playListUUID);

	/**
	 * Get the <c>UUID</c> identifying a <c>PlayList</c> entry based on the
	 * containing <c>PlayList</c> and the current index.
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 * @param itemUUID The <c>UUID</c> identifying the entry.
	 * @return
	 */
	public abstract int getItemIndex(UUID playListUUID, UUID itemUUID);

	/**
	 * Stops a <c>PlayList</c>.
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 */
	public abstract void stop(UUID playListUUID);

	/**
	 * Adds a new <c>PlayList</c> and returns its identifier.
	 * @param parentSet The <c>UUID</c> of the parent <c>PlayListSet</c>.
	 * 	May not be <c>null</c>.
	 * @return The <c>UUID</c> identifying the new <c>PlayList</c>.
	 */
	public abstract UUID addPlayList(UUID parentSet);

	/**
	 * Adds a new <c>PlayListSet</c> and returns its identifier.
	 * @param parentSet The <c>UUID</c> of the parent <c>PlayListSet</c>. May
	 * 	be <c>null<c> to add a top level <c>PlayListSet</c>.
	 * @return The <c>UUID</c> identifying the new <c>PlayList</c>.
	 */
	public abstract UUID addPlayListSet(UUID parentSetUUID);

	/**
	 * Removes a <c>PlayList</c>.
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 */
	public abstract void deletePlayList(UUID playListUUID);

	/**
	 * Adds entries from one <c>PlayList</c> to another.
	 * @param sourcePlayListUUID The <c>UUID</c> identifying the
	 * 	<c>PlayList</c> which contains the entries to import.
	 * @param itemUUIDs A list of <c>UUID</c>s identifying the entries to
	 * 	import.
	 * @param playListUUID The <c>UUID</c> of the <c>PlayList</c> which imports
	 * 	the entries.
	 * @param targetIndex The index of the first entry in <c>itemUUIDs</c> in
	 * 	the target <c>PlayList</c> after the import.
	 * @param copy If set to <c>false</c> the entries will be removed from the
	 * 	source <c>PlayList</c>.
	 */
	public abstract void importItems(UUID sourcePlayListUUID, List<UUID> itemUUIDs,
			UUID playListUUID, int targetIndex, boolean copy);

	/**
	 * Returns <c>true</c> if the specified entry is not stopped (meaning
	 * playing or paused).
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 * @param itemUUID The entry's <c>UUID</c>.
	 * @return <c>false</c> if the entry is stopped - <c>true</c> otherwise.
	 */
	public abstract boolean itemIsActive(UUID playListUUID, UUID itemUUID);

	/**
	 * Returns the name - e.g. the file name or the title - for a given entry.
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 * @param itemUUID The <c>UUID</c> identifying the entry.
	 * @return The textual representation for the entry.
	 */
	public abstract String getItemName(UUID playListUUID, UUID itemUUID);

	/**
	 * Returns the <c>UUID</c> of an entry the given entry is chained with.
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 * @param itemUUID The <c>UUID</c> identifying the entry.
	 * @return The other entry's <c>UUID</c> or <c>null</c> if no chaining
	 * 	exists.
	 */
	public abstract UUID getItemChainWith(UUID playListUUID, UUID itemUUID);

	/**
	 * Sets the the chaining between two entries.
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 * @param itemUUID The <c>UUID</c> identifying the entry.
	 * @param chainWith The <c>UUID</c> of the entry to be chained with.
	 */
	public abstract void setItemChainWith(UUID playListUUID, UUID itemUUID,
			UUID chainWith);

	/**
	 * Returns if the given entry is played repeatedly.
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 * @param itemUUID The <c>UUID</c> identifying the entry.
	 * @return <c>true</c> if the given entry ist repeated, <c>false</c>
	 * 	otherwise.
	 */
	public abstract boolean itemIsRepeating(UUID playListUUID, UUID itemUUID);

	/**
	 * Sets if the given entry is played repeatedly.
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 * @param itemUUID The <c>UUID</c> identifying the entry.
	 * @param isRepeating Set to to <c>true</c> to repeat the entry.
	 */
	public abstract void setItemIsRepeating(UUID playListUUID, UUID itemUUID, boolean isRepeating);

	/**
	 * Returns if a given <c>PlayList</c> is repeated.
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 * @return <c>true</c> if the <c>PlayList</c> is repeated.
	 */
	public abstract boolean isRepeatList(UUID playListUUID);

	/**
	 * Sets if a given <c>PlayList</c> is repeated. This setting has no effect
	 * if <c>getRandomizeList</c> returns <c>true</c>, if an entry is played
	 * repeatedly because <c>itemIsRepeating</c> returns <c>true</c> or if
	 * a loop is induced through setting of chaining of entries.
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 * @param repeat If set to <c>true</c> the given <c>PlayList</c> will be
	 * 	repeated.
	 */
	public abstract void setRepeatList(UUID playListUUID, boolean repeat);

	/**
	 * If this returns <c>true</c> the <c>PlayList</c> is randomized. This
	 * means that, as long as an entry is neither set to repeating nor chained
	 * with another entry, the next played entry ist determined randomly.
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 * @return <c>true</c> if the <c>PlayList</c> is randomized.
	 */
	public abstract boolean isRandomizeList(UUID playListUUID);

	/**
	 * Sets randomization for a specific <c>PlayList</c>.
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 * @param randomize If <c>true</c> the <c>PlayList</c> is randomized.
	 */
	public abstract void setRandomizeList(UUID playListUUID, boolean randomize);

	/**
	 * Sets the minimum volume for a specific <c>PlayList</c>. The actual
	 * volume of a played entry is chosen uniformly from the interval
	 * [<c>randomizeVolumeFrom</c>, <c>randomizeVolumeTo</c>].
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 * @param from The minimum volume in the range from 0 to 1.0.
	 */
	public abstract void setRandomizeVolumeFrom(UUID playListUUID, float from);

	/**
	 * Sets the maximum volume for a specific <c>PlayList</c>. The actual
	 * volume of a played entry is chosen uniformly from the interval
	 * [<c>randomizeVolumeFrom</c>, <c>randomizeVolumeTo</c>].
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 * @param to The maximum volume in the range from 0 to 1.0.
	 */
	public abstract void setRandomizeVolumeTo(UUID playListUUID,  float to);

	/**
	 * Sets if the <c>PlayList</c> stops after each played entry.
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 * @param stopAfterEachSound If set to <c>true</c> the <c>PlayList</c>
	 * 	stops after each played entry.
	 */
	public abstract void setStopAfterEachSound(UUID playListUUID, boolean stopAfterEachSound);

	/**
	 * Sets the time sounds are faded in.
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 * @param fadeIn The fade in time in milliseconds.
	 */
	public abstract void setFadeIn(UUID playListUUID, int fadeIn);

	/**
	 * Sets the time sounds are faded out.
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 * @param fadeOut The fade out time in milliseconds.
	 */
	public abstract void setFadeOut(UUID playListUUID, int fadeOut);

	/**
	 * Sets the time for overlapping. If <c>overlap</c> is set to a value n,
	 * then the last n milliseconds of one sound are played simoultaneously
	 * with the first n milliseconds of the next one.
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 * @param overlap The overlap time.
	 */
	public abstract void setOverlap(UUID playListUUID, int overlap);

	/**
	 * If this returns <c>true</c> then the <c>PlayList</c> stops after each
	 * played entry if it is not repeated or chained with another entry.
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 * @return <c>true</c> if the <c>PlayList</c> stops after each sound.
	 */
	public abstract boolean isStopAfterEachSound(UUID playListUUID);

	/**
	 * Gets the minimum volume for a specific <c>PlayList</c>. The actual
	 * volume of a played entry is chosen uniformly from the interval
	 * [<c>randomizeVolumeFrom</c>, <c>randomizeVolumeTo</c>].
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 * @return The minimum volume in the range from 0 to 1.0.
	 */
	public abstract float getRandomizeVolumeFrom(UUID playListUUID);

	/**
	 * Gets the maximum volume for a specific <c>PlayList</c>. The actual
	 * volume of a played entry is chosen uniformly from the interval
	 * [<c>randomizeVolumeFrom</c>, <c>randomizeVolumeTo</c>].
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 * @return The maximum volume in the range from 0 to 1.0.
	 */
	public abstract float getRandomizeVolumeTo(UUID playListUUID);

	/**
	 * Gets the time sounds are faded in.
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 * @result The fade in time in milliseconds.
	 */
	public abstract int getFadeIn(UUID playListUUID);

	/**
	 * Gets the time sounds are faded out.
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 * @result The fade out time in milliseconds.
	 */
	public abstract int getFadeOut(UUID playListUUID);

	/**
	 * Gets the time for overlapping. If <c>overlap</c> is set to a value n,
	 * then the last n milliseconds of one sound are played simoultaneously
	 * with the first n milliseconds of the next one.
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 * @result The overlap time.
	 */
	public abstract int getOverlap(UUID playListUUID);

	/**
	 * Sets the volume for a specific <c>PlayList</c>.
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 * @param volume The volume, as a value between 0 and 1.0.
	 */
	public abstract void setVolume(UUID playListUUID, float volume);

	/**
	 * Gets the volume for a specific <c>PlayList</c>.
	 * @param playListUUID The <c>UUID</c> identifying the <c>PlayList</c>.
	 * @result The volume, as a value between 0 and 1.0.
	 */
	public abstract float getVolume(UUID playListUUID);

	/**
	 * Saves the <c>PlayList</c>s to the specified path.
	 * @param savePath The path to the file to save.
	 */
	public abstract void save(String savePath);

	/**
	 * Loads the <c>PlayList</c>s from the specified path.
	 * @param loadPath The path to the file to load from.
	 */
	public abstract void load(String loadPath);

	/**
	 * Retrieves all <c>PlayList</c>s directly contained in a
	 * <c>PlayListSet</c>.
	 * @param parentSetUUID The <c>UUID</c> of the parent set.
	 * @return A list of <c>UUID</c>s identifying <c>PlayList</c>s.
	 */
	public abstract List<UUID> getPlayListUUIDs(UUID parentSetUUID);

	/**
	 * Retrieves all <c>PlayListSet</c>s directly contained in another
	 * <c>PlayListSet</c>.
	 * @param parentSetUUID The <c>UUID</c> of the parent set. If set to
	 * 	<c>null</c> the root sets are returned.
	 * @return A list of <c>UUID</c>s identifying <c>PlayListSet</c>s.
	 */
	public abstract List<UUID> getPlayListSetUUIDs(UUID parentSetUUID);

	public abstract String getTitle(UUID uuid);

	public abstract UUID getParent(UUID child);

	public abstract void setTitle(UUID uuid, String newTitle);

	public abstract void remove(UUID uuid);

	public abstract boolean isActive(UUID elementUUID);

	public abstract void setAutoplay(UUID elementUUID, boolean autoplay);

	public abstract boolean getAutoplay(UUID elementUUID);

	public abstract UUID getRootParent(UUID uuid);

	public abstract void setActive(UUID playListSetUUID);
}
