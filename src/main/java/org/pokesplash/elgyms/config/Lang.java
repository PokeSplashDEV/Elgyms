package org.pokesplash.elgyms.config;

import com.google.gson.Gson;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.type.Type;
import org.pokesplash.elgyms.util.Utils;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class Lang {
	private String title;
	private String backButton;
	private String completed;
	private String incompleted;
	private HashMap<Type, String> types;

	public Lang() {
		title = "§3Gyms";
		backButton = "§3Back";
		completed = "§eCompleted";
		incompleted = "§cIncomplete";

		types = new HashMap<>();
		for (Type type : Type.values()) {
			types.put(type, Utils.capitaliseFirst(type.name()));
		}
	}

	public String getTitle() {
		return title;
	}

	public String getBackButton() {
		return backButton;
	}

	public String getType(Type type) {
		return types.get(type);
	}

	public String getCompleted() {
		return completed;
	}

	public String getIncompleted() {
		return incompleted;
	}

	public HashMap<Type, String> getTypes() {
		return types;
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
					backButton = lang.getBackButton();
					types = lang.getTypes();
					completed = lang.getCompleted();
					incompleted = lang.getIncompleted();
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
