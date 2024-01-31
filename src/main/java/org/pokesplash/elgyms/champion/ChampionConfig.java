package org.pokesplash.elgyms.champion;

import com.google.gson.Gson;
import net.minecraft.server.network.ServerPlayerEntity;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.gym.*;
import org.pokesplash.elgyms.util.Utils;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Config for the champion system.
 */
public class ChampionConfig {
	private boolean enable; // Enable Champion.
	private Badge badge; // The badge for champion.
	private UUID requiredBadge; // The badge required to challenge the champion;
	private int displaySlot; // The slot where the champion will be displayed.
	private double inactivityDemotionTime; // Time of inactivity before the leader is demoted.
	private boolean allowInactivityReports; // Allow players to report a champion for inactivity.
	private boolean defendingGivesRewards; // Should the champion get rewards for defending their title.
	private Positions positions; // The positions of the champion, challenger and spectators.
	private ChampionRequirements requirements; // The requirements for the battle.
	private ChampionRewards rewards; // The rewards.
	private String championSuccessBroadcast; // The message ran when the champion wins.
	private String championLossBroadcast; // The message ran when the champion wins.
	private String championDemoteBroadcast; // The message ran when a champion is demoted.
	private Leader champion; // The current Champion.

	public ChampionConfig() {
		enable = true;
		badge = new Badge();
		inactivityDemotionTime = 168;
		allowInactivityReports = true;
		defendingGivesRewards = true;
		displaySlot = 16;
		requiredBadge = UUID.randomUUID();
		positions = new Positions();
		requirements = new ChampionRequirements();
		rewards = new ChampionRewards();
		championSuccessBroadcast = "§2{winner} defended their title against {loser} to stay Champion!";
		championLossBroadcast = "§2{winner} beat {loser} to become the new Champion!";
		championDemoteBroadcast = "§2{player} was demoted from Champion. The spot is now open!";
		champion = new Leader();
	}

	public void write() {
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
					badge = cfg.getBadge();
					displaySlot = cfg.getDisplaySlot();
					inactivityDemotionTime = cfg.getInactivityDemotionTime();
					allowInactivityReports = cfg.isAllowInactivityReports();
					defendingGivesRewards = cfg.isDefendingGivesRewards();
					requiredBadge = cfg.getRequiredBadge();
					positions = cfg.getPositions();
					requirements = cfg.getRequirements();
					rewards = cfg.getRewards();
					champion = cfg.getChampion();
					championSuccessBroadcast = cfg.getChampionSuccessBroadcast();
					championLossBroadcast = cfg.getChampionLossBroadcast();
					championDemoteBroadcast = cfg.getChampionDemoteBroadcast();

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

	public Badge getBadge() {
		return badge;
	}

	public int getDisplaySlot() {
		return displaySlot;
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

	public Positions getPositions() {
		return positions;
	}

	public ChampionRequirements getRequirements() {
		return requirements;
	}

	public UUID getRequiredBadge() {
		return requiredBadge;
	}

	public String getChampionSuccessBroadcast() {
		return championSuccessBroadcast;
	}

	public String getChampionLossBroadcast() {
		return championLossBroadcast;
	}

	public String getChampionDemoteBroadcast() {
		return championDemoteBroadcast;
	}

	public void runLoserRewards(ServerPlayerEntity loser) {

		if (rewards.getLoser().isEnableBroadcast()) {
			Utils.broadcastMessage(Utils.formatPlaceholders(rewards.getLoser().getBroadcastMessage(),
					null, null, loser, null, null, null));
		}

		Utils.runCommands(rewards.getLoser().getCommands(), loser, null, null, null);
	}

	public void runWinnerRewards(ServerPlayerEntity winner) {

		if (rewards.getWinner().isEnableBroadcast()) {
			Utils.broadcastMessage(Utils.formatPlaceholders(rewards.getWinner().getBroadcastMessage(),
					null, null, winner, null, null, null));
		}

		Utils.runCommands(rewards.getWinner().getCommands(), winner, null, null, null);
	}

	public void runDemotionRewards(ServerPlayerEntity demoted) {

		if (rewards.getDemotion().isEnableBroadcast()) {
			Utils.broadcastMessage(Utils.formatPlaceholders(rewards.getDemotion().getBroadcastMessage(),
					null, null, demoted, null, null, null));
		}

		Utils.runCommands(rewards.getDemotion().getCommands(), demoted, null, null, null);
	}

	public void runWinBroadcast(ServerPlayerEntity winner, ServerPlayerEntity loser) {
		Utils.broadcastMessage(championSuccessBroadcast
				.replaceAll("\\{winner\\}", winner.getName().getString())
				.replaceAll("\\{loser\\}", loser.getName().getString()));
	}

	public void runLossBroadcast(ServerPlayerEntity winner, ServerPlayerEntity loser) {
		Utils.broadcastMessage(championLossBroadcast
				.replaceAll("\\{winner\\}", winner.getName().getString())
				.replaceAll("\\{loser\\}", loser.getName().getString()));
	}

	public void runDemoteBroadcast(ServerPlayerEntity champion) {
		Utils.broadcastMessage(championLossBroadcast
				.replaceAll("\\{player\\}", champion.getName().getString()));
	}
}
