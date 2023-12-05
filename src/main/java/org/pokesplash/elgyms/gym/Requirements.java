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
	private int maxModdedPokemon; // The maximum amount of modded Pokemon
	private int maxFullIVs; // The maximum amount of full IVs modded pokemon can have.
	private boolean leadersInheritPlayerRestrictions; // Should leaders inherit challenger restrictions.
	private Restriction challengerRestrictions; // Restrictions for the challenger.
	private Restriction leaderRestrictions; // Restrictions for the leader.

	public Requirements(String uuid) {
		requiredGymIDs = new HashSet<>();
		requiredGymIDs.add(uuid);

		clauses = new HashSet<>();
		clauses.add(Clause.SPECIES);

		maxModdedPokemon = 3;
		maxFullIVs = 4;
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

	public void setRequiredGymIDs(HashSet<String> requiredGymIDs) {
		this.requiredGymIDs = requiredGymIDs;
	}

	public void setPokemonLevel(int pokemonLevel) {
		this.pokemonLevel = pokemonLevel;
	}

	public void setRaiseToCap(boolean raiseToCap) {
		this.raiseToCap = raiseToCap;
	}

	public void setTeamSize(int teamSize) {
		this.teamSize = teamSize;
	}

	public void setTeamPreview(boolean teamPreview) {
		this.teamPreview = teamPreview;
	}

	public void setClauses(HashSet<Clause> clauses) {
		this.clauses = clauses;
	}

	public int getMaxModdedPokemon() {
		return maxModdedPokemon;
	}

	public void setMaxModdedPokemon(int maxModdedPokemon) {
		this.maxModdedPokemon = maxModdedPokemon;
	}

	public int getMaxFullIVs() {
		return maxFullIVs;
	}

	public void setMaxFullIVs(int maxFullIVs) {
		this.maxFullIVs = maxFullIVs;
	}

	public void setLeadersInheritPlayerRestrictions(boolean leadersInheritPlayerRestrictions) {
		this.leadersInheritPlayerRestrictions = leadersInheritPlayerRestrictions;
	}

	public void setChallengerRestrictions(Restriction challengerRestrictions) {
		this.challengerRestrictions = challengerRestrictions;
	}

	public void setLeaderRestrictions(Restriction leaderRestrictions) {
		this.leaderRestrictions = leaderRestrictions;
	}
}
