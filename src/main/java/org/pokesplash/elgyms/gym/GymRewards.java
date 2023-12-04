package org.pokesplash.elgyms.gym;

import org.pokesplash.elgyms.config.Reward;

import java.util.ArrayList;

/**
 * Holds rewards for a gym.
 */
public class GymRewards {
	private Reward firstTime; // First time rewards
	private Reward prestige; // Rewards for beating it after they prestige.
	private ArrayList<String> lossCommands; // Commands to run if the challenger loses.

	public GymRewards() {
		firstTime = new Reward();
		prestige = new Reward();

		lossCommands = new ArrayList<>();
		lossCommands.add("say {player} lost to gym {gym}");
	}

	public Reward getFirstTime() {
		return firstTime;
	}

	public Reward getPrestige() {
		return prestige;
	}

	public ArrayList<String> getLossCommands() {
		return lossCommands;
	}
}
