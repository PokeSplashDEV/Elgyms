package org.pokesplash.elgyms.provider;

import com.google.gson.Gson;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.champion.ChampionConfig;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.gym.Leader;
import org.pokesplash.elgyms.ui.CategorySelect;
import org.pokesplash.elgyms.util.Utils;

import java.io.File;
import java.util.*;

public abstract class GymProvider {
	private static String PATH = Elgyms.BASE_PATH + "gyms/";
	private static HashMap<String, GymConfig> gyms = new HashMap<>();
	private static ChampionConfig champion = new ChampionConfig();

	/**
	 * Method to fetch all gyms.
	 */
	public static void init() {

		champion.init(); // Initialize the champion config.

		try {
			File dir = Utils.checkForDirectory(PATH);

			String[] list = dir.list();

			// If no files, return.
			if (list.length == 0) {
				GymConfig gymConfig = new GymConfig();
				gymConfig.write();
				gyms.put(gymConfig.getId(), gymConfig);
				return;
			}

			for (String file : list) {

				Utils.readFileAsync(PATH,
						file, el -> {
							Gson gson = Utils.newGson();
							GymConfig gym = gson.fromJson(el, GymConfig.class);
							gyms.put(gym.getId(), gym);
						});
			}

			Elgyms.LOGGER.info(Elgyms.MOD_ID + " gyms successfully read.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static HashMap<String, GymConfig> getGyms() {
		return gyms;
	}

	public static ArrayList<GymConfig> getGymsByCategory(String categoryName) {

		ArrayList<GymConfig> filteredGyms = new ArrayList<>();
		for (GymConfig gymConfig : gyms.values()) {
			if (gymConfig.getCategoryName().equalsIgnoreCase(categoryName)) {
				filteredGyms.add(gymConfig);
			}
		}

		Collections.sort(filteredGyms, Comparator.comparingInt(GymConfig::getWeight));

		return filteredGyms;
	}

	public static ChampionConfig getChampion() {
		return champion;
	}

	public static void addGym(GymConfig gymConfig) {
		gyms.put(gymConfig.getId(), gymConfig);
	}

	public static void  updateName(UUID uuid, String name) {
		for (GymConfig gymConfig : gyms.values()) {
			for (Leader leader : gymConfig.getLeaders()) {
				if (leader.getUuid().equals(uuid) &&
						!leader.getName().equalsIgnoreCase(name)) {
					leader.setName(name);
					gymConfig.write(); // TODO test
				}
			}
		}
	}
}
