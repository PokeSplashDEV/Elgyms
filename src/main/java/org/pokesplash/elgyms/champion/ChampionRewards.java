package org.pokesplash.elgyms.champion;

import org.pokesplash.elgyms.config.Reward;

/**
 * Rewards for the champion system.
 */
public class ChampionRewards {
	private Reward winner; // Rewards for the winner.
	private Reward loser; // Rewards for the loser.
	private Reward demotion; // Rewards for demotion.

	public ChampionRewards() {
		winner = new Reward();
		loser = new Reward();
		demotion = new Reward();
	}

	public Reward getWinner() {
		return winner;
	}

	public Reward getLoser() {
		return loser;
	}

	public Reward getDemotion() {
		return demotion;
	}
}
