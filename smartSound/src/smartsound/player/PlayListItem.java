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

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import smartsound.common.PropertyMap;
import smartsound.player.events.ITimeEventListener;
import smartsound.player.events.TimeEventHandler;
import smartsound.plugins.player.IPlayer;
import smartsound.plugins.player.ISound;

/**
 * This class handles playing, fading and overlapping of sounds. It also starts
 * the following sound, either determined by self-repeat, chaining or some
 * mechanism chosen by the <c>PlayList</c>.
 * @author André Becker
 *
 */
public class PlayListItem implements ITimeEventListener {

	private PlayerControllerSettings settings;

	private UUID uuid = UUID.randomUUID();
	private boolean repeatItem = false;
	private UUID chainWith = null;

	private volatile PlayList parent;
	private final ISound sound;

	private List<PlayerWrapper> wrappers = new LinkedList<PlayerWrapper>();

	private boolean dispose = false;

	public PlayListItem(final ISound sound, final PlayerControllerSettings settings, final PlayList parent) {
		this.settings = settings;
		this.parent = parent;
		this.sound = sound;
	}

	/**
	 * Copys (or moves) another <c>PlayListItem</c>.
	 * @param playListItem The other <c>PlayListItem</c>.
	 * @param move If set to <c>true</c> the <c>playListItem</c>'s
	 * 	<c>PlayerWrapper</c>s are reused.
	 */
	public PlayListItem(final PlayListItem playListItem, final boolean move) {
		this.settings = playListItem.settings;
		this.repeatItem = playListItem.repeatItem;
		this.chainWith = playListItem.chainWith;

		this.sound = playListItem.sound;

		setParent(playListItem.parent);

		if (move) {
			wrappers = new LinkedList<PlayerWrapper>(playListItem.wrappers);
			if (playListItem.isActive()) {
				for (PlayerWrapper wrapper : wrappers) {
					TimeEventHandler.add(this, wrapper);
				}
			}
		}
	}

	/**
	 * Creates a <c>PlayListItem</c> from a given <c>PropertyMap</c>.
	 * @param map The <c>PropertyMap</c>.
	 * @throws LoadingException If an error occurs during loading.
	 */
	public PlayListItem(final PropertyMap map) throws LoadingException {
		if (!map.get("type").equals(getClass().getCanonicalName())) {
			throw new LoadingException();
		}

		uuid = map.getMapUUID();
		repeatItem = Boolean.parseBoolean(map.get("repeat"));
		if (!map.get("chain_with").equals("null")) {
			chainWith = UUID.fromString(map.get("chain_with"));
		}


		this.sound = new FilePathSound(map.getNestedMaps().get(0));
	}

	/**
	 * @return The settings. This object is shared by all <c>PlayListItem</c>s
	 * 	in a <c>PlayList</c>.
	 */
	public PlayerControllerSettings getSettings() {
		return settings;
	}

	/**
	 * Sets the <c>PlayerControllerSettings</c> for this <c>PlayListItem</c>.
	 * This object is shared by all <c>PlayListItem</c>s of a
	 * <c>PlayList</c>.
	 * @param settings The settings.
	 */
	public void setSettings(final PlayerControllerSettings settings) {
		this.settings = settings;
	}

	/**
	 * Returns if this item is played repeatedly.
	 * @return <c>true</c> if the given item ist repeated, <c>false</c>
	 * 	otherwise.
	 */
	public boolean isRepeatItem() {
		return repeatItem;
	}

	/**
	 * Sets if this item is played repeatedly.
	 * @param isRepeating Set to <c>true</c> to repeat the item.
	 */
	public void setRepeatItem(final boolean repeatItem) {
		this.repeatItem = repeatItem;
		parent.playListChanged();
	}

	/**
	 * Returns the <c>UUID</c> of an item this item is chained with.
	 * @return The other item's <c>UUID</c> or <c>null</c> if no chaining
	 * 	exists.
	 */
	public UUID getChainWith() {
		return chainWith;
	}

	/**
	 * Sets the the chaining between this item and another one.
	 * @param chainWith The <c>UUID</c> of the item to be chained with or
	 * <c>null</c> to disable chaining.
	 */
	public void setChainWith(final UUID chainWith) {
		this.chainWith = chainWith;
		parent.playListChanged();
	}

	/**
	 * @return The <c>UUID</c> identifying this item.
	 */
	public UUID getUUID() {
		return uuid;
	}

	/**
	 * @return The <c>PlayList</c> which contains this item.
	 */
	public PlayList getParent() {
		return parent;
	}

	/**
	 * @return The <c>ISound</c> played by this item.
	 */
	public ISound getSound() {
		return sound;
	}

	/**
	 * @return <c>true</c> if the item is playing or paused.
	 */
	public boolean isActive() {
		boolean result = false;

		for (PlayerWrapper pWrapper : wrappers) {
			if (pWrapper.getStatus() != PlayerControllerStatus.STOPPING) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * Starts or resumes this item.
	 */
	public void play() {
		if (!isActive()) {
			startPlaying();
		} else {
			resume();
		}
		parent.playListChanged();
	}

	/**
	 * Pauses this item.
	 */
	public synchronized void pause() {
		int fadeOutEnd;
		IPlayer player;

		for (PlayerWrapper pWrapper : wrappers) {
			player = pWrapper.getPlayer();
			fadeOutEnd = Math.min(player.getPlayPosition() + settings.getFadeOutLength(), player.getPlayLength());
			pWrapper.setFadeOutEnd(fadeOutEnd);

			pWrapper.setStatus(PlayerControllerStatus.PAUSING);
		}
		parent.playListChanged();

	}

	/**
	 * Stops this item.
	 */
	public void stop() {
		int fadeOutEnd;
		IPlayer player;

		for (PlayerWrapper pWrapper : wrappers) {
			player = pWrapper.getPlayer();
			fadeOutEnd = Math.min(player.getPlayPosition() + settings.getFadeOutLength(), player.getPlayLength());
			pWrapper.setFadeOutEnd(fadeOutEnd);

			pWrapper.setStatus(PlayerControllerStatus.STOPPING);
		}
		parent.playListChanged();
	}

	/**
	 * Sets the current playing position. Not implemented yet!
	 * @param position The position in milliseconds
	 */
	public void setPlayPosition(final int position) {
		//TODO: setPlayPosition
	}

	private void setCurrentVolume(final PlayerWrapper pWrapper) {
		if (settings == null) {
			return;
		}

		float volume = settings.getVolume();

		IPlayer player = pWrapper.getPlayer();
		int currentPosition = player.getPlayPosition();

		if (currentPosition < pWrapper.getFadeInBegin() + settings.getFadeInLength()) {
			if (currentPosition <= pWrapper.getFadeInBegin()) {
				volume = 0;
			}
			if (settings.getFadeInLength() > 0) {
				volume *= (Float.valueOf(currentPosition) - pWrapper.getFadeInBegin()) / settings.getFadeInLength();
			}

		}

		if (currentPosition > pWrapper.getFadeOutEnd() - settings.getFadeOutLength()) {
			if (currentPosition >= pWrapper.getFadeOutEnd()) {
				volume = 0;
			}
			if (settings.getFadeOutLength() > 0) {
				volume *= (Float.valueOf(pWrapper.getFadeOutEnd()) - currentPosition) / settings.getFadeOutLength();
			}
		}

		pWrapper.setVolume(volume);
		player.setVolume(volume * pWrapper.getVolumeFactor());
	}

	@Override
	public synchronized boolean receiveTimeEvent(final long currentTime, final Object obj) {
		if (!(obj instanceof PlayerWrapper) || dispose) {
			return false;
		}

		PlayerWrapper pWrapper = (PlayerWrapper) obj;

		IPlayer player = pWrapper.getPlayer();

		if (settings != null &&
				!pWrapper.isNextSoundStarted()
				&& (player.isFinished() || player.getPlayLength() - player.getPlayPosition() <= settings
				.getOverlapTime())) {
			pWrapper.setNextSoundStarted(true);
			nextSound();
		}

		setCurrentVolume(pWrapper);

		if (pWrapper.getStatus() == PlayerControllerStatus.PAUSING
				&& pWrapper.getVolume() == 0) {
			player.pause();
			parent.playListChanged();
		}

		if ((pWrapper.getStatus() == PlayerControllerStatus.STOPPING
				&& pWrapper.getVolume() == 0)
				|| player.isFinished())  {
			player.stop();

			wrappers.remove(pWrapper);
			if (parent != null) {
				parent.playListChanged();
			}
			return false;
		}
		return true;
	}

	private void nextSound() {

		if (repeatItem) {
			startPlaying();
			return;
		}

		PlayListItem entry;
		if (chainWith != null) {
			entry = parent.getEntry(chainWith);
			if (entry != null) {
				entry.startPlaying();
				return;
			}
		}

		entry = parent.getNextEntry(uuid);
		if (entry != null) {
			entry.startPlaying();
		}

	}

	private void startPlaying() {
		IPlayer player = parent.getPlayer(sound);
		if (player == null) { //Sound cannot be played
			nextSound();
			return;
		}
		Random rnd = new Random();

		int range = (int) (settings.getRandomizeVolumeTo() - settings
				.getRandomizeVolumeFrom() * 100) ;

		int randomized;
		if (range <= 0) {
			randomized = 0;
		} else {
			randomized = rnd.nextInt(range);
		}
		float randomFactor = ((settings.getRandomizeVolumeFrom() * 100) + randomized)  / 100.0f;

		PlayerWrapper pWrapper = new PlayerWrapper(player, settings.getVolume(), randomFactor);
		setCurrentVolume(pWrapper);
		wrappers.add(pWrapper);
		player.play();

		TimeEventHandler.add(this, pWrapper);
		parent.playListChanged();
	}

	private void resume() {
		IPlayer player;

		for (PlayerWrapper pWrapper : wrappers) {
			if (pWrapper.getStatus() == PlayerControllerStatus.STOPPING) {
				continue;
			}
			player = pWrapper.getPlayer();
			pWrapper.setFadeInBegin(player.getPlayPosition());
			pWrapper.setFadeOutEnd(player.getPlayLength());
			setCurrentVolume(pWrapper);
			player.play();
		}
	}

	/**
	 * Sets the <c>PlayList</c> containing this item.
	 * @param parent The parent <c>PlayList</c>.
	 */
	public void setParent(final PlayList parent) {
		this.parent = parent;
	}

	@Override
	public String toString() {
		return sound.toString();
	}

	/**
	 * @return A <c>PropertyMap</c> containing all information to restore this
	 * <c>PlayListItem</c>.
	 * 
	 */
	public PropertyMap getPropertyMap() {
		PropertyMap map = new PropertyMap(uuid);

		map.put("type", getClass().getCanonicalName());
		map.put("repeat", String.valueOf(this.repeatItem));
		map.put("chain_with", String.valueOf(chainWith));

		map.addPropertyMap(sound.getPropertyMap());

		return map;
	}

	public void dispose() {
		dispose  = true;
	}
}
