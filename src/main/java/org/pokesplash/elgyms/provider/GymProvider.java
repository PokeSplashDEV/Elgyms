package org.pokesplash.elgyms.provider;

import com.google.gson.Gson;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.util.Utils;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public abstract class GymProvider {
	private static String PATH = Elgyms.BASE_PATH + "gyms/";
	private static HashMap<UUID, GymConfig> gyms = new HashMap<>();

	/**
	 * Method to fetch all gyms.
	 */
	public static void init() {
		try {
			File dir = Utils.checkForDirectory(PATH);

			String[] list = dir.list();

			// If no files, return.
			if (list.length == 0) {
				GymConfig gymConfig = new GymConfig();
				Utils.writeFileAsync(PATH,
						"example.json", Utils.newGson().toJson(gymConfig));
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
}
