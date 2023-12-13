package org.pokesplash.elgyms.util;

import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.abilities.AbilityTemplate;
import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.moves.Moves;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.pokesplash.elgyms.gym.BannedPokemon;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.gym.Requirements;
import org.pokesplash.elgyms.gym.Restriction;
import org.pokesplash.elgyms.type.Clause;
import org.pokesplash.elgyms.type.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public abstract class ElgymsUtils {
	public static ArrayList<String> getRulesLore(GymConfig gym) {

		Requirements requirements = gym.getRequirements();

		ArrayList<String> rules = new ArrayList<>();

		rules.add("§6Level: §e" + requirements.getPokemonLevel());
		rules.add(requirements.isRaiseToCap() ? "§aRaise To Cap" : "§cNo Raise To Cap");
		rules.add("§3Pokemon Amount: §b" + requirements.getTeamSize());
		rules.add(requirements.isTeamPreview() ? "§aTeam Preview" : "§cNo Team Preview");
		rules.add("§5Clauses:");
		for (Clause clause : requirements.getClauses()) {
			rules.add("§d" + Utils.capitaliseFirst(clause.name()));
		}
		return rules;
	}

	public static ArrayList<Pokemon> setLevelAndPp(ArrayList<Pokemon> pokemon, int level) {

		ArrayList<Pokemon> leaderTeam = new ArrayList<>();

		for (Pokemon mon : pokemon) {
			Pokemon newPokemon = mon.clone(true, true);
			newPokemon.setLevel(level);

			for (Move move : newPokemon.getMoveSet().getMoves()) {
				move.setRaisedPpStages(3);
				move.update();
			}


			leaderTeam.add(newPokemon);
		}

		return leaderTeam;
	}

	public static boolean checkLeaderRequirements(ArrayList<Pokemon> pokemon, GymConfig gym) throws Exception {

		// Makes sure team limit isn't exceeded.
		matchesPokemonSize(pokemon, gym);



		// Checks Pokemon match the gyms types.
		for (Pokemon mon : pokemon) {
			if (!matchesType(mon, gym)) {
				throw new Exception("Pokemon " + mon.getDisplayName().getString() + " does not share a type with this" +
						" gym");

			}
		}

		// Checks clauses aren't broken
		checkClauses(pokemon, gym);

		// Checks that Modded Pokemon are valid.
		checkModded(pokemon, gym);

		// Checks for banned aspects in leader restrictions.
		checkBannedAspects(pokemon, gym.getRequirements().getLeaderRestrictions());

		// If leaders inherit challenger restrictions, check for challenger banned aspects too.
		if (gym.getRequirements().isLeadersInheritPlayerRestrictions()) {
			checkBannedAspects(pokemon, gym.getRequirements().getChallengerRestrictions());
		}

		return true;
	}

	public static boolean checkChallengerRequirements(ArrayList<Pokemon> pokemon, GymConfig gym) throws Exception {

		// Checks for level requirements.
		if (!gym.getRequirements().isRaiseToCap()) {
			for (Pokemon mon : pokemon) {
				if (mon.getLevel() > gym.getRequirements().getPokemonLevel()) {
					throw new Exception("All Pokemon must be under the level cap: Lvl " + gym.getRequirements().getPokemonLevel());
				}
			}
		}

		// Makes sure team limit isn't exceeded.
		matchesPokemonSize(pokemon, gym);

		// Checks clauses aren't broken
		checkClauses(pokemon, gym);

		// Checks for banned aspects in challenger restrictions.
		checkBannedAspects(pokemon, gym.getRequirements().getChallengerRestrictions());

		return true;
	}

	private static boolean matchesPokemonSize(ArrayList<Pokemon> pokemons, GymConfig gym) throws Exception {
		if (pokemons.size() > gym.getRequirements().getTeamSize()) {
			throw new Exception("Only " + gym.getRequirements().getTeamSize() + " Pokemon are allowed in this gym.");
		}
		return true;
	}

	private static boolean matchesType(Pokemon pokemon, GymConfig gym) {
		Iterator<ElementalType> iterator = pokemon.getTypes().iterator();

		boolean matchesType = false;

		while (iterator.hasNext()) {
			String pokemonType = iterator.next().getName();
			for (Type type : gym.getTypes()) {
				if (type.toString().equalsIgnoreCase(pokemonType)) {
					matchesType = true;
					break;
				}
			}
		}

		return matchesType;
	}

	private static boolean checkClauses(ArrayList<Pokemon> pokemonList, GymConfig gymConfig) throws Exception {

		HashSet<Clause> clauses = gymConfig.getRequirements().getClauses();

		// Species Clause
		if (clauses.contains(Clause.SPECIES)) {
			HashMap<Species, Integer> speciesCount = new HashMap<>(); // Counts the amount of each species.
			for (Pokemon mon : pokemonList) {
				if (!speciesCount.containsKey(mon.getSpecies())) {
					speciesCount.put(mon.getSpecies(), 0);
				}
				speciesCount.put(mon.getSpecies(), speciesCount.get(mon.getSpecies()) + 1);
			}

			for (Species species : speciesCount.keySet()) {
				if (speciesCount.get(species) > 1) {
					throw new Exception("A player cannot have two Pokemon with the same National Pokédex number on a team.");
				}
			}
		}

		// OHKO Clause
		if (clauses.contains(Clause.OHKO)) {

			ArrayList<String> ohkoMoves = new ArrayList<>();
			ohkoMoves.add("fissure");
			ohkoMoves.add("guillotine");
			ohkoMoves.add("horndrill");
			ohkoMoves.add("sheercold");


			for (Pokemon mon : pokemonList) {
				for (Move move : mon.getMoveSet().getMoves()) {
					if (ohkoMoves.contains(move.getName())) {
						throw new Exception("A Pokemon may not have the moves Fissure, Guillotine, Horn Drill, or Sheer Cold in its moveset.");
					}
				}
			}
		}

		// Item clause
		if (clauses.contains(Clause.ITEM)) {
			HashMap<Item, Integer> itemCount = new HashMap<>(); // Counts the amount of each item
			for (Pokemon mon : pokemonList) {
				if (!itemCount.containsKey(mon.heldItem().getItem())) {
					itemCount.put(mon.heldItem().getItem(), 0);
				}
				itemCount.put(mon.heldItem().getItem(), itemCount.get(mon.heldItem().getItem()) + 1);
			}

			for (Item item : itemCount.keySet()) {
				if (itemCount.get(item) > 1 && !item.equals(Items.AIR)) {
					throw new Exception("A player cannot have two of the same items on a team.");
				}
			}
		}

		// Evasion clause
		if (clauses.contains(Clause.EVASION)) {
			ArrayList<String> evasionMoves = new ArrayList<>();
			evasionMoves.add("doubleteam");
			evasionMoves.add("minimize");

			for (Pokemon mon : pokemonList) {
				for (Move move : mon.getMoveSet().getMoves()) {
					if (evasionMoves.contains(move.getName())) {
						throw new Exception("A Pokemon may not have either Double Team or Minimize in its moveset.");
					}
				}
			}
		}

		// Moody Clause
		if (clauses.contains(Clause.MOODY)) {
			AbilityTemplate ability = Abilities.INSTANCE.get("moody");

			for (Pokemon mon : pokemonList) {
				if (mon.getAbility().getTemplate().equals(ability)) {
					throw new Exception("A team cannot have a Pokemon with the ability Moody.");
				}
			}
		}

		// Swagger Clause
		if (clauses.contains(Clause.SWAGGER)) {
			for (Pokemon mon : pokemonList) {
				for (Move move : mon.getMoveSet().getMoves()) {
					if (move.getName().equalsIgnoreCase("swagger")) {
						throw new Exception("Players cannot use the move Swagger.");
					}
				}
			}
		}

		// Legendary Clause
		if (clauses.contains(Clause.LEGENDARY)) {
			for (Pokemon pokemon : pokemonList) {
				if (pokemon.isLegendary()) {
					throw new Exception("Players cannot use Legendary Pokemon");
				}
			}
		}

		// Ultrabeast Clause
		if (clauses.contains(Clause.ULTRABEAST)) {
			for (Pokemon pokemon : pokemonList) {
				if (pokemon.isUltraBeast()) {
					throw new Exception("Players cannot use Ultra Beast Pokemon");
				}
			}
		}

		// endless battle clause
		if (clauses.contains(Clause.ENDLESS_BATTLE)) {
			for (Pokemon pokemon : pokemonList) {

				Item leppaBerry = CobblemonItems.LEPPA_BERRY;

				// If leppa and harvest, throw error.
				if (pokemon.heldItem().getItem().equals(leppaBerry)) {
					boolean endlessBattle = false;

					// If also has harvest
					if (pokemon.getAbility().getTemplate().equals(Abilities.INSTANCE.get("harvest"))) {
						endlessBattle = true;
					}

					// If has recycle
					for (Move move : pokemon.getMoveSet().getMoves()) {
						if (move.getTemplate().equals(Moves.INSTANCE.getByName("recycle"))) {
							endlessBattle = true;
						}
					}

					if (endlessBattle) {
						throw new Exception("Players cannot intentionally prevent an opponent from being able to end the game without forfeiting.");
					}
				}
			}
		}

		return true;
	}

	private static boolean checkModded(ArrayList<Pokemon> pokemon, GymConfig gym) throws Exception {

		ArrayList<Stat> stats = new ArrayList<>();
		stats.add(Stats.HP);
		stats.add(Stats.ATTACK);
		stats.add(Stats.DEFENCE);
		stats.add(Stats.SPECIAL_ATTACK);
		stats.add(Stats.SPECIAL_DEFENCE);
		stats.add(Stats.SPEED);

		int moddedAmount = 0;

		for (Pokemon mon : pokemon) {

			int maxIVStats = 0;

			// Checks how many 16+ IVs there are;
			for (Stat stat : stats) {
				if (mon.getIvs().get(stat) != null && mon.getIvs().get(stat) > 15) {
					maxIVStats ++;
				}
			}

			if (maxIVStats > gym.getRequirements().getMaxFullIVs()) {
				throw new Exception(mon.getDisplayName().getString() + " has too many full IVs.");
			}

			if (maxIVStats > 0) {
				moddedAmount += 1;
			}
		}

		if (moddedAmount > gym.getRequirements().getMaxModdedPokemon()) {
			throw new Exception("You are only allowed " + gym.getRequirements().getMaxModdedPokemon() + " modded " +
					"Pokemon");
		}

		return true;
	}

	private static boolean checkBannedAspects(ArrayList<Pokemon> pokemons, Restriction restriction) throws Exception {
		for (Pokemon mon : pokemons) {

			// Checks for banned Pokemon
			for (BannedPokemon bannedPokemon : restriction.getBannedPokemon()) {

				boolean matchesSpecies = mon.getSpecies().getName().equalsIgnoreCase(bannedPokemon.getPokemon());

				boolean matchesForm = mon.getForm().getName().equalsIgnoreCase(bannedPokemon.getForm());

				boolean matchesAbility = mon.getAbility().getName().equalsIgnoreCase(bannedPokemon.getAbility());


				boolean validSpecies = bannedPokemon.getPokemon().isEmpty() || matchesSpecies;

				boolean validForm = bannedPokemon.getForm().isEmpty() || matchesForm;

				boolean validAbility = bannedPokemon.getAbility().isEmpty() || matchesAbility;

				if (validSpecies && validForm && validAbility) {
					throw new Exception("Your " + mon.getSpecies().getName() + " is banned in this gym");
				}
			}


			// Checks for banned held items.
			for (String itemString : restriction.getBannedItems()) {
				Item bannedItem = Utils.parseItemId(itemString).getItem();

				if (mon.heldItem().getItem().equals(bannedItem)) {
					throw new Exception(bannedItem.getName().getString() + " is banned in this gym.");
				}
			}

			// Checks for banned Moves.
			for (String moveString : restriction.getBannedMoves()) {
				MoveTemplate bannedMove = Moves.INSTANCE.getByName(moveString);

				for (Move move : mon.getMoveSet().getMoves()) {
					if (move.getTemplate().equals(bannedMove)) {
						throw new Exception(bannedMove.getName() + " is banned in this gym.");
					}
				}
			}

			// Checks for banned abilities.
			for (String abilityString : restriction.getBannedAbilities()) {
				AbilityTemplate bannedAbility = Abilities.INSTANCE.get(abilityString);
				if (mon.getAbility().getTemplate().equals(bannedAbility)) {
					throw new Exception(bannedAbility.getName() + " is banned in this gym.");
				}
			}

		}
		return true;
	}
}
