package org.pokesplash.elgyms.champion;

import org.pokesplash.elgyms.gym.Restriction;
import org.pokesplash.elgyms.type.Clause;

import java.util.HashSet;
import java.util.UUID;

public class ChampionRequirements {
	private boolean teamPreview; // Should team preview be shown.
	private HashSet<Clause> clauses; // Smogon clauses
	private Restriction restrictions; // Restrictions for the challenger.

	public ChampionRequirements() {
		clauses = new HashSet<>();
		clauses.add(Clause.SPECIES);
		restrictions = new Restriction();
	}

	public boolean isTeamPreview() {
		return teamPreview;
	}

	public HashSet<Clause> getClauses() {
		return clauses;
	}

	public Restriction getRestrictions() {
		return restrictions;
	}
}
