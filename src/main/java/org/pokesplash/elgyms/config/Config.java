package org.pokesplash.elgyms.config;

import com.google.gson.Gson;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.util.Utils;

import java.util.concurrent.CompletableFuture;

public class Config {
	private boolean isExample;

	public Config() {
		isExample = true;
	}

	public void init() {
		CompletableFuture<Boolean> futureRead = Utils.readFileAsync(Elgyms.BASE_PATH,
				"config.json", el -> {
					Gson gson = Utils.newGson();
					Config cfg = gson.fromJson(el, Config.class);
					isExample = cfg.isExample();
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

	public boolean isExample() {
		return isExample;
	}
}
