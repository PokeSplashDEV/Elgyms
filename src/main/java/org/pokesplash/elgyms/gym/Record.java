package org.pokesplash.elgyms.gym;

public class Record {
	private int wins; // Wins of a gym leader.
	private int losses; // Losses of a gym leader.

	public Record() {
		wins = 0;
		losses = 0;
	}

	public int getWins() {
		return wins;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}

	public int getLosses() {
		return losses;
	}

	public void setLosses(int losses) {
		this.losses = losses;
	}
}
