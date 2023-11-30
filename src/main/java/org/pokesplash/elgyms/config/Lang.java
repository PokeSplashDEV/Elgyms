package org.pokesplash.elgyms.config;

import com.google.gson.Gson;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.util.Utils;

import java.util.concurrent.CompletableFuture;

public class Lang {
	private String title;
	private String fillerMaterial;

	public Lang() {
		title = Elgyms.MOD_ID;
		fillerMaterial = "minecraft:white_stained_glass_pane";
	}

	public String getTitle() {
		return title;
	}

	public String getFillerMaterial() {
		return fillerMaterial;
	}

	/**
	 * Method to initialize the config.
	 */
	public void init() {
		CompletableFuture<Boolean> futureRead = Utils.readFileAsync(Elgyms.BASE_PATH, "lang.json",
				el -> {
					Gson gson = Utils.newGson();
					Lang lang = gson.fromJson(el, Lang.class);
					title = lang.getTitle();
					fillerMaterial = lang.getFillerMaterial();
				});

		if (!futureRead.join()) {
			Elgyms.LOGGER.info("No lang.json file found for " + Elgyms.MOD_ID + ". Attempting to " +
					"generate " +
					"one.");
			Gson gson = Utils.newGson();
			String data = gson.toJson(this);
			CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(Elgyms.BASE_PATH, "lang.json", data);

			if (!futureWrite.join()) {
				Elgyms.LOGGER.fatal("Could not write lang.json for " + Elgyms.MOD_ID + ".");
			}
			return;
		}
		Elgyms.LOGGER.info(Elgyms.MOD_ID + " lang file read successfully.");
	}
}
