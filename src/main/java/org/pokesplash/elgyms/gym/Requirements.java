package org.pokesplash.elgyms.gym;

import it.unimi.dsi.fastutil.Hash;
import org.pokesplash.elgyms.type.Clause;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

public class Requirements {
	private HashSet<String> requiredGymIDs; // IDs of the gyms required before this can be challenged.
	private int pokemonLevel; // Level Pokemon should be.
	private boolean raiseToCap; // Should Pokemon be set to the level of the gym.
	private int teamSize; // Amount of Pokemon on a team.
	private boolean teamPreview; // Should team preview be shown.
	private HashSet<Clause> clauses; // Smogon clauses
	private boolean leadersInheritPlayerRestrictions; // Should leaders inherit challenger restrictions.
	private Restriction challengerRestrictions; // Restrictions for the challenger.
	private Restriction leaderRestrictions; // Restrictions for the leader.

	public Requirements(String uuid) {
		requiredGymIDs = new HashSet<>();
		requiredGymIDs.add(uuid);

		clauses = new HashSet<>();
		clauses.add(Clause.SPECIES);

		pokemonLevel = 100;
		raiseToCap = true;
		teamSize = 6;
		teamPreview = true;
		leadersInheritPlayerRestrictions = true;
		challengerRestrictions = new Restriction();
		leaderRestrictions = new Restriction();
	}

	public HashSet<String> getRequiredGymIDs() {
		return requiredGymIDs;
	}

	public int getPokemonLevel() {
		return pokemonLevel;
	}

	public boolean isRaiseToCap() {
		return raiseToCap;
	}

	public int getTeamSize() {
		return teamSize;
	}

	public boolean isTeamPreview() {
		return teamPreview;
	}

	public boolean isLeadersInheritPlayerRestrictions() {
		return leadersInheritPlayerRestrictions;
	}

	public Restriction getChallengerRestrictions() {
		return challengerRestrictions;
	}

	public Restriction getLeaderRestrictions() {
		return leaderRestrictions;
	}

	public HashSet<Clause> getClauses() {
		return clauses;
	}
}
