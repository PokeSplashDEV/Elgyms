package org.pokesplash.elgyms.badge;

import com.google.gson.Gson;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.config.CategoryConfig;
import org.pokesplash.elgyms.gym.Badge;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.provider.BadgeProvider;
import org.pokesplash.elgyms.util.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Config for a gym.
 */
public class PlayerBadges {
	private UUID uuid; // Players uuid;
	private String name; // The name of the player.
	private HashMap<String, ArrayList<Badge>> badgeIDs; // The IDs of the badges the player owns, sorted by category
	private HashMap<String, Boolean> prestige; // List of categories and if the player has prestiged.
	private HashMap<UUID, Long> cooldown; // Cooldowns per gym.

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
		badgeIDs = new HashMap<>();
		prestige = new HashMap<>();
		write();
		cooldown = new HashMap<>();
	}

	public UUID getUuid() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public Long getCooldown(GymConfig gym) {
		return cooldown.get(gym.getBadge().getId());
	}

	public void setCooldown(GymConfig gym, long cooldown) {
		this.cooldown.put(gym.getBadge().getId(), cooldown);
		write();
	}

	public void removeCooldown(GymConfig gymConfig) {
		cooldown.remove(gymConfig.getBadge().getId());
		write();
	}

	public void setName(String name) {
		this.name = name;
		write();
	}

	public ArrayList<Badge> getBadgeIDs(CategoryConfig categoryConfig) {
		if (!badgeIDs.containsKey(categoryConfig.getName())) {
			badgeIDs.put(categoryConfig.getName(), new ArrayList<>());
			setPrestiged(categoryConfig, false);
		}

		return badgeIDs.get(categoryConfig.getName());
	}

	public boolean containsBadge(UUID uuid) {
		for (ArrayList<Badge> badgeArrayList : badgeIDs.values()) {
			for (Badge badge : badgeArrayList) {
				if (badge.getId().equals(uuid)) {
					return true;
				}
			}
		}
		return false;
	}

	public void addBadge(CategoryConfig categoryConfig, Badge badge) {
		if (!badgeIDs.containsKey(categoryConfig.getName())) {
			badgeIDs.put(categoryConfig.getName(), new ArrayList<>());
			setPrestiged(categoryConfig, false);
		}

		ArrayList<Badge> values = badgeIDs.get(categoryConfig.getName());

		values.add(badge);

		badgeIDs.put(categoryConfig.getName(), values);
		write();
	}

	public void removeBadge(CategoryConfig categoryConfig, Badge badge) {
		if (!badgeIDs.containsKey(categoryConfig.getName())) {
			badgeIDs.put(categoryConfig.getName(), new ArrayList<>());
			setPrestiged(categoryConfig, false);
		}

		ArrayList<Badge> values = badgeIDs.get(categoryConfig.getName());

		values.remove(badge);

		badgeIDs.put(categoryConfig.getName(), values);
		write();
	}

	public boolean isPrestiged(CategoryConfig categoryConfig) {
		if (!prestige.containsKey(categoryConfig.getName())) {
			setPrestiged(categoryConfig, false);
		}
		return prestige.get(categoryConfig.getName());
	}

	public void setPrestiged(CategoryConfig categoryConfig, boolean isPrestiged) {
		prestige.put(categoryConfig.getName(), isPrestiged);
		write();
	}


}
