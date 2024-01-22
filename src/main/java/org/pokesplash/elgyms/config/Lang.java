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
	private String rejectChallengePlayer;
	private String rejectChallengeLeader;
	private String cancelChallenge;
	private String cooldownMessage;
	private String levelCapClause;
	private String maxTeamSizeClause;
	private String speciesClause;
	private String ohkoClause;
	private String itemClause;
	private String evasionClause;
	private String moodyClause;
	private String swaggerClause;
	private String legendaryClause;
	private String ultraBeastClause;
	private String endlessBattleClause;
	private String bannedPokemon;
	private String bannedItem;
	private String bannedMove;
	private String bannedAbility;

	public Lang() {
		prefix = "§b[§3Gyms§b]";
		openGymMessage = "§bThe {gym} has been opened.";
		closeGymMessage = "§3The {gym} has been closed.";
		challengeMessageChallenger = "§3You have challenged the {gym} gym.";
		challengeMessageLeader = "§3{player} has challenged the {gym} gym.";
		rejectChallengePlayer = "§cYou were rejected for the {gym} challenge.";
		rejectChallengeLeader = "§3You removed {player} from the queue.";
		cancelChallenge = "§3You have been removed from the {gym} queue.";
		cooldownMessage = "§cYou still have a {cooldown} cooldown";
		levelCapClause = "§c{player} - {pokemon} must be under the level cap: Lvl {level}";
		maxTeamSizeClause = "§c{player} - Only {teamSize} Pokemon are allowed in this gym.";
		speciesClause = "§c{player} - A player cannot have two Pokemon with the same National Pokédex number on a team.";
		ohkoClause = "§c{player} - {pokemon} may not have the moves Fissure, Guillotine, Horn Drill, or Sheer Cold in its moveset.";
		itemClause = "§c{player} - A player cannot have two of the same items on a team.";
		evasionClause = "§c{player} - {pokemon} may not have either Double Team or Minimize in its moveset.";
		moodyClause = "§c{player} - {pokemon} cannot have the ability Moody.";
		swaggerClause = "§c{player} - {pokemon} cannot use the move Swagger.";
		legendaryClause = "§c{player} - Players cannot use Legendary Pokemon";
		ultraBeastClause = "§c{player} - Players cannot use Ultra Beast Pokemon";
		endlessBattleClause =
				"§c{player} - Players cannot intentionally prevent an opponent from being able to end the game without forfeiting.";
		bannedPokemon = "§c{player} - {pokemon} is banned in this gym";
		bannedItem = "§c{player} - {item} is banned in this gym.";
		bannedMove = "§c{player} - {move} is banned in this gym.";
		bannedAbility = "§c{player} - {ability} is banned in this gym.";
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
					rejectChallengePlayer = lang.getRejectChallengePlayer();
					rejectChallengeLeader = lang.getRejectChallengeLeader();
					cancelChallenge = lang.getCancelChallenge();
					cooldownMessage = lang.getCooldownMessage();
					levelCapClause = lang.getLevelCapClause();
					maxTeamSizeClause = lang.getMaxTeamSizeClause();
					speciesClause = lang.getSpeciesClause();
					ohkoClause = lang.getOhkoClause();
					itemClause = lang.getItemClause();
					evasionClause = lang.getEvasionClause();
					moodyClause = lang.getMoodyClause();
					swaggerClause = lang.getSwaggerClause();
					legendaryClause = lang.getLegendaryClause();
					ultraBeastClause = lang.getUltraBeastClause();
					endlessBattleClause = lang.getEndlessBattleClause();
					bannedPokemon = lang.getBannedPokemon();
					bannedItem = lang.getBannedItem();
					bannedMove = lang.getBannedMove();
					bannedAbility = lang.getBannedAbility();
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
		return prefix + " ";
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

	public String getRejectChallengePlayer() {
		return rejectChallengePlayer;
	}

	public String getRejectChallengeLeader() {
		return rejectChallengeLeader;
	}

	public String getCancelChallenge() {
		return cancelChallenge;
	}

	public String getCooldownMessage() {
		return cooldownMessage;
	}

	public String getLevelCapClause() {
		return levelCapClause;
	}

	public String getMaxTeamSizeClause() {
		return maxTeamSizeClause;
	}

	public String getSpeciesClause() {
		return speciesClause;
	}

	public String getOhkoClause() {
		return ohkoClause;
	}

	public String getItemClause() {
		return itemClause;
	}

	public String getEvasionClause() {
		return evasionClause;
	}

	public String getMoodyClause() {
		return moodyClause;
	}

	public String getSwaggerClause() {
		return swaggerClause;
	}

	public String getLegendaryClause() {
		return legendaryClause;
	}

	public String getUltraBeastClause() {
		return ultraBeastClause;
	}

	public String getEndlessBattleClause() {
		return endlessBattleClause;
	}

	public String getBannedPokemon() {
		return bannedPokemon;
	}

	public String getBannedItem() {
		return bannedItem;
	}

	public String getBannedMove() {
		return bannedMove;
	}

	public String getBannedAbility() {
		return bannedAbility;
	}
}
