package org.pokesplash.elgyms.gym;

import org.pokesplash.elgyms.config.Reward;

import java.util.ArrayList;

/**
 * Holds rewards for a gym.
 */
public class GymRewards {
	private Reward firstTime; // First time rewards
	private Reward prestige; // Rewards for beating it after they prestige.
	private Reward loss; // Loss commands / broadcast

	public GymRewards() {
		firstTime = new Reward();
		prestige = new Reward();
		loss = new Reward();
	}

	public Reward getFirstTime() {
		return firstTime;
	}

	public Reward getPrestige() {
		return prestige;
	}

	public Reward getLoss() {
		return loss;
	}
}
