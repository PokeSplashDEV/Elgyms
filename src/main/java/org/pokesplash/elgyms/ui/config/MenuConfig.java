package org.pokesplash.elgyms.ui.config;

import com.google.gson.Gson;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.type.Type;
import org.pokesplash.elgyms.util.Utils;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class MenuConfig {
	private String title;
	private String backButton;
	private String completed;
	private String incompleted;
	private String requirements;
	private int categoryRows;
	private String fillerMaterial;
	private int backButtonPosition;
	private String backButtonMaterial;
	private String challengeButtonTitle;
	private String challengeButtonMaterial;
	private String cancelChallengeButtonTitle;
	private String cancelChallengeButtonMaterial;
	private String closedTitle;
	private String closedButtonMaterial;
	private String rulesTitle;
	private int rulesButtonIndex;
	private String rulesButtonMaterial;
	private HashMap<Type, String> types;
	private String badgeTitle;
	private String cooldownTitle;
	private String cooldownMaterial;
	private String leaderTitle;

	public MenuConfig() {
		categoryRows = 3;
		fillerMaterial = "minecraft:white_stained_glass_pane";
		backButtonPosition = 0;
		backButtonMaterial = "minecraft:barrier";
		challengeButtonMaterial = "minecraft:lime_stained_glass_pane";
		closedTitle = "§cThis gym is currently closed.";
		closedButtonMaterial = "minecraft:red_stained_glass_pane";
		rulesTitle = "§6Rules";
		rulesButtonIndex = 12;
		rulesButtonMaterial = "minecraft:book";
		title = "§3Gyms";
		backButton = "§3Back";
		completed = "§eYou have beaten this gym.";
		incompleted = "§cIncomplete";
		challengeButtonTitle = "§6Challenge";
		cancelChallengeButtonTitle = "§6Cancel";
		cancelChallengeButtonMaterial = "minecraft:orange_stained_glass_pane";
		requirements = "§cBadges Required: {badges}";
		badgeTitle = "§3Badges";
		cooldownTitle = "§cYou have a {cooldown} cooldown";
		cooldownMaterial = "minecraft:red_stained_glass_pane";
		leaderTitle = "§bLeaders";

		types = new HashMap<>();
		for (Type type : Type.values()) {
			types.put(type, Utils.capitaliseFirst(type.name()));
		}
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
					challengeButtonMaterial = cfg.getChallengeButtonMaterial();
					rulesButtonMaterial = cfg.getRulesButtonMaterial();
					title = cfg.getTitle();
					backButton = cfg.getBackButton();
					types = cfg.getTypes();
					completed = cfg.getCompleted();
					incompleted = cfg.getIncompleted();
					challengeButtonTitle = cfg.getChallengeButtonTitle();
					requirements = cfg.getRequirements();
					rulesTitle = cfg.getRulesTitle();
					rulesButtonIndex = cfg.getRulesButtonIndex();
					closedTitle = cfg.getClosedTitle();
					closedButtonMaterial = cfg.getClosedButtonMaterial();
					cancelChallengeButtonTitle = cfg.getCancelChallengeButtonTitle();
					cancelChallengeButtonMaterial = cfg.getCancelChallengeButtonMaterial();
					badgeTitle = cfg.getBadgeTitle();
					cooldownTitle = cfg.getCooldownTitle();
					cooldownMaterial = cfg.getCooldownMaterial();
					leaderTitle = cfg.getLeaderTitle();
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

	public String getChallengeButtonTitle() {
		return challengeButtonTitle;
	}

	public String getRulesButtonMaterial() {
		return rulesButtonMaterial;
	}

	public String getTitle() {
		return title;
	}

	public String getBackButton() {
		return backButton;
	}

	public String getCompleted() {
		return completed;
	}

	public String getIncompleted() {
		return incompleted;
	}

	public String getRequirements() {
		return requirements;
	}

	public String getChallengeButtonMaterial() {
		return challengeButtonMaterial;
	}

	public HashMap<Type, String> getTypes() {
		return types;
	}

	public String getType(Type type) {
		return types.get(type);
	}

	public int getRulesButtonIndex() {
		return rulesButtonIndex;
	}

	public String getRulesTitle() {
		return rulesTitle;
	}

	public String getClosedButtonMaterial() {
		return closedButtonMaterial;
	}

	public String getClosedTitle() {
		return closedTitle;
	}

	public String getCancelChallengeButtonTitle() {
		return cancelChallengeButtonTitle;
	}

	public String getCancelChallengeButtonMaterial() {
		return cancelChallengeButtonMaterial;
	}

	public String getBadgeTitle() {
		return badgeTitle;
	}

	public String getCooldownTitle() {
		return cooldownTitle;
	}

	public String getCooldownMaterial() {
		return cooldownMaterial;
	}

	public String getLeaderTitle() {
		return leaderTitle;
	}
}
