package org.pokesplash.elgyms.champion;

import com.google.gson.Gson;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.gym.Leader;
import org.pokesplash.elgyms.util.Utils;

import java.util.concurrent.CompletableFuture;

/**
 * Config for the champion system.
 */
public class ChampionConfig {
	private boolean enable; // Enable Champion.
	private double inactivityDemotionTime; // Time of inactivity before the leader is demoted.
	private boolean allowInactivityReports; // Allow players to report a champion for inactivity.
	private boolean defendingGivesRewards; // Should the champion get rewards for defending their title.
	private ChampionRewards rewards; // The rewards.
	private Leader champion; // The current Champion.

	public ChampionConfig() {
		enable = true;
		inactivityDemotionTime = 168;
		allowInactivityReports = true;
		defendingGivesRewards = true;
		rewards = new ChampionRewards();
		champion = new Leader();
	}

	private void write() {
		Gson gson = Utils.newGson();
		String data = gson.toJson(this);
		CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(Elgyms.BASE_PATH,
				"champion.json", data);

		if (!futureWrite.join()) {
			Elgyms.LOGGER.fatal("Could not write champion.json for " + Elgyms.MOD_ID + ".");
		}
	}

	public void init() {
		CompletableFuture<Boolean> futureRead = Utils.readFileAsync(Elgyms.BASE_PATH,
				"champion.json", el -> {
					Gson gson = Utils.newGson();
					ChampionConfig cfg = gson.fromJson(el, ChampionConfig.class);
					enable = cfg.isEnable();
					inactivityDemotionTime = cfg.getInactivityDemotionTime();
					allowInactivityReports = cfg.isAllowInactivityReports();
					defendingGivesRewards = cfg.isDefendingGivesRewards();
					rewards = cfg.getRewards();
					champion = cfg.getChampion();
				});

		if (!futureRead.join()) {
			Elgyms.LOGGER.info("No champion.json file found for " + Elgyms.MOD_ID + ". Attempting to generate" +
					"one");
			write();
			return;
		}
		Elgyms.LOGGER.info(Elgyms.MOD_ID + " champion file read successfully");
	}

	public boolean isEnable() {
		return enable;
	}

	public double getInactivityDemotionTime() {
		return inactivityDemotionTime;
	}

	public boolean isAllowInactivityReports() {
		return allowInactivityReports;
	}

	public boolean isDefendingGivesRewards() {
		return defendingGivesRewards;
	}

	public ChampionRewards getRewards() {
		return rewards;
	}

	public Leader getChampion() {
		return champion;
	}

	public void setChampion(Leader champion) {
		this.champion = champion;
		write();
	}
}
