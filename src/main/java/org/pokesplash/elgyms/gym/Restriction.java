package org.pokesplash.elgyms.gym;

import org.pokesplash.elgyms.type.Clause;

import java.util.ArrayList;

/**
 * Holds all restrictions for either a leader or challenger.
 */
public class Restriction {
	private ArrayList<Clause> clauses; // Smogon clauses
	private ArrayList<BannedPokemon> bannedPokemon; // Pokemon to be banned
	private ArrayList<String> bannedItems; // Items to be banned
	private ArrayList<String> bannedMoves; // Moves to be banned
	private ArrayList<String> bannedAbilities; // Abilities to be banned

	public Restriction() {
		clauses = new ArrayList<>();
		clauses.add(Clause.SPECIES);

		bannedPokemon = new ArrayList<>();
		bannedPokemon.add(new BannedPokemon());

		bannedItems = new ArrayList<>();
		bannedItems.add("cobblemon:kings_rock");

		bannedMoves = new ArrayList<>();
		bannedMoves.add("endeavor");

		bannedAbilities = new ArrayList<>();
		bannedAbilities.add("magicguard");
	}

	public ArrayList<Clause> getClauses() {
		return clauses;
	}

	public ArrayList<BannedPokemon> getBannedPokemon() {
		return bannedPokemon;
	}

	public ArrayList<String> getBannedItems() {
		return bannedItems;
	}

	public ArrayList<String> getBannedMoves() {
		return bannedMoves;
	}

	public ArrayList<String> getBannedAbilities() {
		return bannedAbilities;
	}
}
