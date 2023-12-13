package org.pokesplash.elgyms.config;

/**
 * Config for prestige.
 */
public class PrestigeConfig {
	private boolean canPrestige; // The category can Prestige.
	private double cooldown; // The cooldown, hours for the player to prestige again.
	private Reward rewards; // The rewards for prestige.


	/**
	 * Generates placeholder fields.
	 */
	public PrestigeConfig() {
		canPrestige = true;
		cooldown = 168;
		rewards = new Reward();
	}

	/**
	 * Getters
	 */

	public boolean isCanPrestige() {
		return canPrestige;
	}

	public double getCooldown() {
		return cooldown;
	}

	public Reward getRewards() {
		return rewards;
	}
}
