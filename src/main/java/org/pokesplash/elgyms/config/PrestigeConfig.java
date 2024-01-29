package org.pokesplash.elgyms.config;

import java.util.UUID;

/**
 * Config for prestige.
 */
public class PrestigeConfig {
	private boolean canPrestige; // The category can Prestige.
	private UUID requiredBadge; // The badge required to prestige.
	private double cooldown; // The cooldown, hours for the player to prestige again.
	private Reward rewards; // The rewards for prestige.


	/**
	 * Generates placeholder fields.
	 */
	public PrestigeConfig() {
		canPrestige = true;
		cooldown = 168;
		rewards = new Reward();
		requiredBadge = UUID.randomUUID();
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

	public UUID getRequiredBadge() {
		return requiredBadge;
	}
}
