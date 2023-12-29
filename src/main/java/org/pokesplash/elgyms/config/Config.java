package org.pokesplash.elgyms.config;

import com.google.gson.Gson;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.util.Utils;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class Config {
	private boolean forceStartBattle;
	private boolean teleportBack;
	private boolean enableBroadcasts;
	private ArrayList<CategoryConfig> categories;



	public Config() {
		forceStartBattle = true;
		teleportBack = true;
		enableBroadcasts = true;
		categories = new ArrayList<>();
		categories.add(new CategoryConfig());
	}

	public void init() {
		CompletableFuture<Boolean> futureRead = Utils.readFileAsync(Elgyms.BASE_PATH,
				"config.json", el -> {
					Gson gson = Utils.newGson();
					Config cfg = gson.fromJson(el, Config.class);
					forceStartBattle = cfg.isForceStartBattle();
					teleportBack = cfg.isTeleportBack();
					enableBroadcasts = cfg.isEnableBroadcasts();
					categories = cfg.getCategories();
				});

		if (!futureRead.join()) {
			Elgyms.LOGGER.info("No config.json file found for " + Elgyms.MOD_ID + ". Attempting to generate" +
					" " +
					"one");
			Gson gson = Utils.newGson();
			String data = gson.toJson(this);
			CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(Elgyms.BASE_PATH,
					"config.json", data);

			if (!futureWrite.join()) {
				Elgyms.LOGGER.fatal("Could not write config for " + Elgyms.MOD_ID + ".");
			}
			return;
		}
		Elgyms.LOGGER.info(Elgyms.MOD_ID + " config file read successfully");
	}

	public boolean isForceStartBattle() {
		return forceStartBattle;
	}

	public boolean isTeleportBack() {
		return teleportBack;
	}

	public boolean isEnableBroadcasts() {
		return enableBroadcasts;
	}

	public ArrayList<CategoryConfig> getCategories() {
		return categories;
	}

	public CategoryConfig getCategoryByName(String name) {
		for (CategoryConfig categoryConfig : categories) {
			if (categoryConfig.getName().equalsIgnoreCase(name)) {
				return categoryConfig;
			}
		}
		return null;
	}
}
