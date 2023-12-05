package org.pokesplash.elgyms.provider;

import com.google.gson.Gson;
import net.minecraft.server.network.ServerPlayerEntity;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.badge.PlayerBadges;
import org.pokesplash.elgyms.champion.ChampionConfig;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.gym.Leader;
import org.pokesplash.elgyms.util.Utils;

import java.io.File;
import java.util.*;

public abstract class BadgeProvider {
	private static String PATH = Elgyms.BASE_PATH + "badges/";
	private static HashMap<UUID, PlayerBadges> badges = new HashMap<>();

	/**
	 * Method to fetch all gyms.
	 */
	public static void init() {

		try {
			File dir = Utils.checkForDirectory(PATH);

			String[] list = dir.list();

			// If no files, return.
			if (list.length == 0) {
				return;
			}

			for (String file : list) {

				Utils.readFileAsync(PATH,
						file, el -> {
							Gson gson = Utils.newGson();
							PlayerBadges badge = gson.fromJson(el, PlayerBadges.class);
							badges.put(badge.getUuid(), badge);
						});
			}

			Elgyms.LOGGER.info(Elgyms.MOD_ID + " badges successfully read.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static PlayerBadges getBadges(ServerPlayerEntity player) {
		if (!badges.containsKey(player.getUuid())) {
			badges.put(player.getUuid(), new PlayerBadges(player.getUuid(), player.getDisplayName().getString()));
		}
		return badges.get(player.getUuid());
	}

	public static void addBadge(PlayerBadges playerBadges) {
		badges.put(playerBadges.getUuid(), playerBadges);
	}

	public static void  updateName(UUID uuid, String name) {
		for (PlayerBadges playerBadges : badges.values()) {
			if (playerBadges.getUuid().equals(uuid) &&
			!playerBadges.getName().equalsIgnoreCase(name)) {
				playerBadges.setName(name);
			}
		}
	}
}
