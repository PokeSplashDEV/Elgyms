package org.pokesplash.elgyms.gym;

import org.pokesplash.elgyms.type.Clause;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Holds all restrictions for either a leader or challenger.
 */
public class Restriction {
	private HashSet<BannedPokemon> bannedPokemon; // Pokemon to be banned
	private HashSet<String> bannedItems; // Items to be banned
	private HashSet<String> bannedMoves; // Moves to be banned
	private HashSet<String> bannedAbilities; // Abilities to be banned

	public Restriction() {


		bannedPokemon = new HashSet<>();
		bannedPokemon.add(new BannedPokemon());

		bannedItems = new HashSet<>();
		bannedItems.add("cobblemon:kings_rock");

		bannedMoves = new HashSet<>();
		bannedMoves.add("endeavor");

		bannedAbilities = new HashSet<>();
		bannedAbilities.add("magicguard");
	}

	public HashSet<BannedPokemon> getBannedPokemon() {
		return bannedPokemon;
	}

	public HashSet<String> getBannedItems() {
		return bannedItems;
	}

	public HashSet<String> getBannedMoves() {
		return bannedMoves;
	}

	public HashSet<String> getBannedAbilities() {
		return bannedAbilities;
	}
}
