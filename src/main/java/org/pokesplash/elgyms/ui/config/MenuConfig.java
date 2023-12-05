package org.pokesplash.elgyms.ui.config;

import com.google.gson.Gson;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.config.Lang;
import org.pokesplash.elgyms.util.Utils;

import java.util.concurrent.CompletableFuture;

public class MenuConfig {
	private int categoryRows;
	private String fillerMaterial;
	private int backButtonPosition;
	private String backButtonMaterial;

	public MenuConfig() {
		categoryRows = 3;
		fillerMaterial = "minecraft:white_stained_glass_pane";
		backButtonPosition = 0;
		backButtonMaterial = "minecraft:barrier";
	}

	/**
	 * Method to initialize the config.
	 */
	public void init() {
		CompletableFuture<Boolean> futureRead = Utils.readFileAsync(Elgyms.BASE_PATH, "menu.json",
				el -> {
					Gson gson = Utils.newGson();
					MenuConfig cfg = gson.fromJson(el, MenuConfig.class);
					categoryRows = cfg.getCategoryRows();
					fillerMaterial = cfg.getFillerMaterial();
					backButtonPosition = cfg.getBackButtonPosition();
					backButtonMaterial = cfg.getBackButtonMaterial();
				});

		if (!futureRead.join()) {
			Elgyms.LOGGER.info("No menu.json file found for " + Elgyms.MOD_ID + ". Attempting to " +
					"generate " +
					"one.");
			Gson gson = Utils.newGson();
			String data = gson.toJson(this);
			CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(Elgyms.BASE_PATH, "menu.json", data);

			if (!futureWrite.join()) {
				Elgyms.LOGGER.fatal("Could not write menu.json for " + Elgyms.MOD_ID + ".");
			}
			return;
		}
		Elgyms.LOGGER.info(Elgyms.MOD_ID + " menu file read successfully.");
	}

	public int getCategoryRows() {
		return categoryRows;
	}

	public String getFillerMaterial() {
		return fillerMaterial;
	}

	public int getBackButtonPosition() {
		return backButtonPosition;
	}

	public String getBackButtonMaterial() {
		return backButtonMaterial;
	}
}
