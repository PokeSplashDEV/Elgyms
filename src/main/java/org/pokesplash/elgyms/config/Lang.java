package org.pokesplash.elgyms.config;

import com.google.gson.Gson;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.util.Utils;

import java.util.concurrent.CompletableFuture;

public class Lang {
	private String prefix;
	private String openGymMessage;
	private String closeGymMessage;
	private String challengeMessageChallenger;
	private String challengeMessageLeader;

	public Lang() {
		prefix = "§b[§3Gyms§b]";
		openGymMessage = "§bThe {gym} has been opened.";
		closeGymMessage = "§3The {gym} has been closed.";
		challengeMessageChallenger = "§3You have challenged the {gym} gym.";
		challengeMessageLeader = "§3{player} has challenged the {gym} gym.";
	}


	/**
	 * Method to initialize the config.
	 */
	public void init() {
		CompletableFuture<Boolean> futureRead = Utils.readFileAsync(Elgyms.BASE_PATH, "lang.json",
				el -> {
					Gson gson = Utils.newGson();
					Lang lang = gson.fromJson(el, Lang.class);
					prefix = lang.getPrefix();
					openGymMessage = lang.getOpenGymMessage();
					closeGymMessage = lang.getCloseGymMessage();
					challengeMessageChallenger = lang.getChallengeMessageChallenger();
					challengeMessageLeader = lang.getChallengeMessageLeader();
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

	public String getPrefix() {
		return prefix;
	}

	public String getOpenGymMessage() {
		return openGymMessage;
	}

	public String getCloseGymMessage() {
		return closeGymMessage;
	}

	public String getChallengeMessageLeader() {
		return challengeMessageLeader;
	}

	public String getChallengeMessageChallenger() {
		return challengeMessageChallenger;
	}
}
