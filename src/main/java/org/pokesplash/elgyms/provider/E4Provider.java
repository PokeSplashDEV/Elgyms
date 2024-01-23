package org.pokesplash.elgyms.provider;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.google.gson.Gson;
import net.minecraft.server.network.ServerPlayerEntity;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.badge.PlayerBadges;
import org.pokesplash.elgyms.config.E4Team;
import org.pokesplash.elgyms.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Tracks players teams.
 */
public abstract class E4Provider {
	private static String PATH = Elgyms.BASE_PATH + "e4/";
	private static HashMap<UUID, E4Team> teams = new HashMap<>();

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
							E4Team team = gson.fromJson(el, E4Team.class);
							teams.put(team.getPlayer(), team);
						});
			}

			Elgyms.LOGGER.info(Elgyms.MOD_ID + " E4 teams successfully read.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds a new E4 team for a player
	 * @param team The team to add.
	 */
	public static void addTeam(E4Team team) {
		teams.put(team.getPlayer(), team);
	}

	/**
	 * Gets an E4 team for a player
	 * @param player The player to get the team for.
	 * @return The team if the player has one, otherwise null.
	 */
	public static E4Team getTeam(UUID player) {
		return teams.get(player);
	}

	/**
	 * Deleted an old players team.
	 * @param team The team to delete.
	 */
	public static void deleteTeam(E4Team team) {
		E4Team oldTeam = teams.remove(team.getPlayer());

		if (oldTeam != null) {
			Utils.deleteFile(Elgyms.BASE_PATH + "e4/", team.getPlayer() + ".json");
		}
	}


	/**
	 * Converts a list of Pokemon to a list of species of the pokemon.
	 * @param pokemon The list of Pokemon to convert.
	 * @return The list of species of Pokemon.
	 */
	public static ArrayList<String> getSpecies(List<Pokemon> pokemon) {

		ArrayList<String> species = new ArrayList<>();

		for (Pokemon mon : pokemon) {
			species.add(mon.getSpecies().getName());
		}

		return species;
	}
}
