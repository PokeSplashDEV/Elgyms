package org.pokesplash.elgyms.badge;

import com.google.gson.Gson;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.gym.GymRewards;
import org.pokesplash.elgyms.gym.Leader;
import org.pokesplash.elgyms.gym.Positions;
import org.pokesplash.elgyms.gym.Requirements;
import org.pokesplash.elgyms.provider.BadgeProvider;
import org.pokesplash.elgyms.type.Type;
import org.pokesplash.elgyms.util.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Config for a gym.
 */
public class PlayerBadges {
	private UUID uuid; // Players uuid;
	private String name; // The name of the player.
	ArrayList<UUID> badgeIDs; // The IDs of the badges the player owns.

	public void write() {
		Gson gson = Utils.newGson();
		String data = gson.toJson(this);
		String fileName = uuid + ".json";
		CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(Elgyms.BASE_PATH + "badges/",
				fileName, data);

		if (!futureWrite.join()) {
			Elgyms.LOGGER.fatal("Could not write " + fileName + " for " + Elgyms.MOD_ID + ".");
		} else {
			BadgeProvider.addBadge(this);
		}
	}

	public PlayerBadges(UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
		badgeIDs = new ArrayList<>();
		write();
	}

	public UUID getUuid() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		write();
	}

	public ArrayList<UUID> getBadgeIDs() {
		return badgeIDs;
	}

	public void addBadge(UUID badge) {
		badgeIDs.add(badge);
		write();
	}

	public void removeBadge(UUID badge) {
		badgeIDs.remove(badge);
		write();
	}
}
