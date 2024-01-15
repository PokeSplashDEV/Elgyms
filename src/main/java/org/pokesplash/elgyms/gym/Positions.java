package org.pokesplash.elgyms.gym;

/**
 * Holds all positions for a gym.
 */
public class Positions {
	private Position leader;
	private Position challenger;
	private Position spectator;

	public Positions() {
		leader = new Position();
		challenger = new Position();
		spectator = new Position();
	}

	public Position getLeader() {
		return leader;
	}

	public Position getChallenger() {
		return challenger;
	}

	public Position getSpectator() {
		return spectator;
	}
}
