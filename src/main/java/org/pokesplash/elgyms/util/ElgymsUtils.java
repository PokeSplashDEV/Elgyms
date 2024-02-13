package org.pokesplash.elgyms.util;

import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.abilities.AbilityTemplate;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.moves.Moves;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.world.World;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.champion.ChampionConfig;
import org.pokesplash.elgyms.config.E4Team;
import org.pokesplash.elgyms.exception.GymException;
import org.pokesplash.elgyms.gym.*;
import org.pokesplash.elgyms.provider.E4Provider;
import org.pokesplash.elgyms.provider.GymProvider;
import org.pokesplash.elgyms.type.Clause;
import org.pokesplash.elgyms.type.Type;

import java.util.*;

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

	public static ArrayList<Pokemon> setLevelAndPp(List<Pokemon> pokemon, int level) {

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

	public static void checkLeaderRequirements(ServerPlayerEntity player, ArrayList<Pokemon> pokemon, GymConfig gym) throws Exception {

		// Makes sure team limit isn't exceeded.
		matchesPokemonSize(player, pokemon, gym.getRequirements());


		// Checks Pokemon match the gyms types.
		for (Pokemon mon : pokemon) {
			if (!matchesType(mon, gym)) {
				throw new Exception("Pokemon " + mon.getDisplayName().getString() + " does not share a type with this" +
						" gym");

			}
		}

		// Checks clauses aren't broken
		checkClauses(player, pokemon, gym.getRequirements().getClauses());

		// Checks that Modded Pokemon are valid.
		checkModded(pokemon, gym);

		// Checks for banned aspects in leader restrictions.
		checkBannedAspects(player, pokemon, gym.getRequirements().getLeaderRestrictions());

		// If leaders inherit challenger restrictions, check for challenger banned aspects too.
		if (gym.getRequirements().isLeadersInheritPlayerRestrictions()) {
			checkBannedAspects(player, pokemon, gym.getRequirements().getChallengerRestrictions());
		}

	}

	public static void checkChallengerRequirements(ServerPlayerEntity player, List<Pokemon> pokemon, GymConfig gym) throws GymException {

		// Checks for level requirements.
		if (!gym.getRequirements().isRaiseToCap()) {
			for (Pokemon mon : pokemon) {
				if (mon.getLevel() > gym.getRequirements().getPokemonLevel()) {
					throw new GymException(Utils.formatClauses(Elgyms.lang.getPrefix() +
									Elgyms.lang.getLevelCapClause(), player, mon,
							gym.getRequirements().getPokemonLevel(), gym.getRequirements().getTeamSize(),
							null, null, null, null));
				}
			}
		}

		// Makes sure team limit isn't exceeded.
		matchesPokemonSize(player, pokemon, gym.getRequirements());

		// Checks clauses aren't broken
		checkClauses(player, pokemon, gym.getRequirements().getClauses());

		// Checks for banned aspects in challenger restrictions.
		checkBannedAspects(player, pokemon, gym.getRequirements().getChallengerRestrictions());

		// If the gym is E4, compare the players team.
		if (gym.isE4()) {
			checkE4Team(player, pokemon);
		}

	}

	public static void checkChampionRequirements(ServerPlayerEntity player, List<Pokemon> pokemon) throws GymException {

		ChampionConfig config = GymProvider.getChampion();

		// Checks clauses aren't broken
		checkClauses(player, pokemon, config.getRequirements().getClauses());

		// Checks for banned aspects in challenger restrictions.
		checkBannedAspects(player, pokemon, config.getRequirements().getRestrictions());

		checkE4Team(player, pokemon);
	}

	private static boolean matchesPokemonSize(ServerPlayerEntity player, List<Pokemon> pokemons, Requirements requirements) throws GymException {
		if (pokemons.size() > requirements.getTeamSize()) {
			throw new GymException(Utils.formatClauses(Elgyms.lang.getPrefix() +
							Elgyms.lang.getMaxTeamSizeClause(), player, null,
					requirements.getPokemonLevel(), requirements.getTeamSize(),
					null, null, null, null));
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

	private static boolean checkClauses(ServerPlayerEntity player, List<Pokemon> pokemonList, HashSet<Clause> clauses) throws GymException {

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
					throw new GymException(Utils.formatClauses(Elgyms.lang.getPrefix() +
									Elgyms.lang.getSpeciesClause(), player, species.create(1),
							null, null,
							null, null, null, null));
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
						throw new GymException(Utils.formatClauses(Elgyms.lang.getPrefix() +
										Elgyms.lang.getOhkoClause(), player, mon,
								null, null,
								null, null, null, null));
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
					throw new GymException(Utils.formatClauses(Elgyms.lang.getPrefix() +
									Elgyms.lang.getItemClause(), player, null,
							null, null,
							null, item.getName().getString(), null, null));
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
						throw new GymException(Utils.formatClauses(Elgyms.lang.getPrefix() +
										Elgyms.lang.getEvasionClause(), player, mon,
								null, null,
								move.getName(), null, null, null));
					}
				}
			}
		}

		// Moody Clause
		if (clauses.contains(Clause.MOODY)) {
			AbilityTemplate ability = Abilities.INSTANCE.get("moody");

			for (Pokemon mon : pokemonList) {
				if (mon.getAbility().getTemplate().equals(ability)) {
					throw new GymException(Utils.formatClauses(Elgyms.lang.getPrefix() +
									Elgyms.lang.getMoodyClause(), player, mon,
							null, null,
							null, null, ability.getName(), null));
				}
			}
		}

		// Swagger Clause
		if (clauses.contains(Clause.SWAGGER)) {
			for (Pokemon mon : pokemonList) {
				for (Move move : mon.getMoveSet().getMoves()) {
					if (move.getName().equalsIgnoreCase("swagger")) {
						throw new GymException(Utils.formatClauses(Elgyms.lang.getPrefix() +
										Elgyms.lang.getSwaggerClause(), player, mon,
								null, null,
								move.getName(), null, null, null));
					}
				}
			}
		}

		// Legendary Clause
		if (clauses.contains(Clause.LEGENDARY)) {
			for (Pokemon pokemon : pokemonList) {
				if (pokemon.isLegendary()) {
					throw new GymException(Utils.formatClauses(Elgyms.lang.getPrefix() +
									Elgyms.lang.getLegendaryClause(), player, pokemon,
							null, null,
							null, null, null, null));
				}
			}
		}

		// Ultrabeast Clause
		if (clauses.contains(Clause.ULTRABEAST)) {
			for (Pokemon pokemon : pokemonList) {
				if (pokemon.isUltraBeast()) {
					throw new GymException(Utils.formatClauses(Elgyms.lang.getPrefix() +
									Elgyms.lang.getUltraBeastClause(), player, pokemon,
							null, null,
							null, null, null, null));
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
						throw new GymException(Utils.formatClauses(Elgyms.lang.getPrefix() +
										Elgyms.lang.getSpeciesClause(), player, pokemon,
								null, null,
								null, null, null, null));
					}
				}
			}
		}

		return true;
	}

	private static boolean checkModded(ArrayList<Pokemon> pokemon, GymConfig gym) throws GymException {

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
				throw new GymException("§c" + mon.getDisplayName().getString() + " has too many full IVs.");
			}

			if (maxIVStats > 0) {
				moddedAmount += 1;
			}
		}

		if (moddedAmount > gym.getRequirements().getMaxModdedPokemon()) {
			throw new GymException("§cYou are only allowed " + gym.getRequirements().getMaxModdedPokemon() + " modded " +
					"Pokemon");
		}

		return true;
	}

	private static boolean checkBannedAspects(ServerPlayerEntity player, List<Pokemon> pokemons, Restriction restriction) throws GymException {
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
					throw new GymException(Utils.formatClauses(Elgyms.lang.getPrefix() +
									Elgyms.lang.getBannedPokemon(), player, mon,
							null, null,
							null, null, null, null));
				}
			}


			// Checks for banned held items.
			for (String itemString : restriction.getBannedItems()) {
				Item bannedItem = Utils.parseItemId(itemString).getItem();

				if (mon.heldItem().getItem().equals(bannedItem)) {
					throw new GymException(Utils.formatClauses(Elgyms.lang.getPrefix() +
									Elgyms.lang.getBannedItem(), player, mon,
							null, null,
							null, bannedItem.getName().getString(), null, null));
				}
			}

			// Checks for banned Moves.
			for (String moveString : restriction.getBannedMoves()) {
				MoveTemplate bannedMove = Moves.INSTANCE.getByName(moveString);

				for (Move move : mon.getMoveSet().getMoves()) {
					if (move.getTemplate().equals(bannedMove)) {
						throw new GymException(Utils.formatClauses(Elgyms.lang.getPrefix() +
										Elgyms.lang.getBannedMove(), player, mon,
								null, null,
								bannedMove.getName(), null, null, null));
					}
				}
			}

			// Checks for banned abilities.
			for (String abilityString : restriction.getBannedAbilities()) {
				AbilityTemplate bannedAbility = Abilities.INSTANCE.get(abilityString);
				if (mon.getAbility().getTemplate().equals(bannedAbility)) {
					throw new GymException(Utils.formatClauses(Elgyms.lang.getPrefix() +
									Elgyms.lang.getBannedAbility(), player, mon,
							null, null,
							null, null, bannedAbility.getName(), null));
				}
			}

		}
		return true;
	}

	private static boolean checkE4Team(ServerPlayerEntity player, List<Pokemon> pokemon) throws GymException {
		E4Team team = E4Provider.getTeam(player.getUuid());

		if (team == null) {
			return true;
		}

		ArrayList<String> partySpecies = new ArrayList<>();

		for (Pokemon mon : pokemon) {
			partySpecies.add(mon.getSpecies().getName());
		}

		if (!team.getSpecies().containsAll(partySpecies)) {
			throw new GymException(Utils.formatClauses(Elgyms.lang.getPrefix() +
							Elgyms.lang.getIllegalE4Team(), player, null,
					null, null,
					null, null, null, team.getSpecies()));
		}

		return true;
	}

	public static boolean didChallengerWin(List<UUID> winners, UUID leader) {

		for (UUID winner : winners) {
			if (winner.equals(leader)) {
				return false;
			}
		}

		return true;
	}

	public static ArrayList<UUID> getBattleActorIds(List<BattleActor> winners) {

		ArrayList<UUID> winnerIds = new ArrayList<>();

		for (BattleActor actor : winners) {
			Iterator<UUID> winnerUUIDs = actor.getPlayerUUIDs().iterator();

			while (winnerUUIDs.hasNext()) {
				winnerIds.add(winnerUUIDs.next());
			}
		}

		return winnerIds;
	}

	public static Position getPosition(ServerPlayerEntity player) {
		Position position = new Position();
		position.setX(player.getX());
		position.setY(player.getY());
		position.setZ(player.getZ());
		position.setYaw(player.getYaw());
		position.setPitch(player.getPitch());
		position.setWorld(player.getWorld().getRegistryKey().getValue());
		return position;
	}

	public static void teleportToPosition(ServerPlayerEntity player, Position position) {
		Iterator<RegistryKey<World>> worlds = Elgyms.server.getWorldRegistryKeys().iterator();

		ServerWorld world = null;

		while (worlds.hasNext()) {
			RegistryKey<World> next = worlds.next();
			if (next.getValue().equals(position.getWorld())) {
				world = Elgyms.server.getWorld(next);
				break;
			}

		}

		if (world != null) {
			player.teleport(world, position.getX(), position.getY(), position.getZ(),
					position.getYaw(), position.getPitch());
		}
	}

	public static Collection<Text> parse(Pokemon pokemon) {
		Collection<Text> lore = new ArrayList<>();
		Style dark_aqua = Style.EMPTY.withColor(TextColor.parse("dark_aqua"));
		Style dark_green = Style.EMPTY.withColor(TextColor.parse("dark_green"));
		Style dark_purple = Style.EMPTY.withColor(TextColor.parse("dark_purple"));
		Style gold = Style.EMPTY.withColor(TextColor.parse("gold"));
		Style gray = Style.EMPTY.withColor(TextColor.parse("gray"));
		Style green = Style.EMPTY.withColor(TextColor.parse("green"));
		Style red = Style.EMPTY.withColor(TextColor.parse("red"));
		Style light_purple = Style.EMPTY.withColor(TextColor.parse("light_purple"));
		Style yellow = Style.EMPTY.withColor(TextColor.parse("yellow"));
		Style white = Style.EMPTY.withColor(TextColor.parse("white"));

		lore.add(Text.translatable("cobblemon.ui.info.species").setStyle(dark_green).append(": ")
				.append(pokemon.getSpecies().getTranslatedName().setStyle(green)));

		MutableText types = Text.empty().setStyle(green);
		for (ElementalType type : pokemon.getSpecies().getTypes()) {
			types.append(" ").append(type.getDisplayName());
		}
		lore.add(Text.translatable("cobblemon.ui.info.type").setStyle(dark_green).append(":").append(types));

		lore.add(Text.translatable("cobblemon.ui.info.nature").setStyle(dark_green).append(": ")
				.append(Text.translatable(pokemon.getNature().getDisplayName()).setStyle(green)));

		MutableText ability = Text.translatable("cobblemon.ui.info.ability").setStyle(dark_green).append(": ")
				.append(Text.translatable(pokemon.getAbility().getDisplayName()).setStyle(green));
		lore.add(ability);

		lore.add(Text.translatable("cobblemon.ui.stats").setStyle(gray).append(": "));

		lore.add(Text.translatable("cobblemon.ui.stats.hp").setStyle(light_purple)
				.append(" §8- §3IV: §a" +
						(pokemon.getIvs().get(Stats.HP) == null ? "0" :
								pokemon.getIvs().get(Stats.HP))
						+ " §cEV: §a" + (pokemon.getEvs().get(Stats.HP) == null ? "0" : pokemon.getEvs().get(Stats.HP))));
		lore.add(Text.translatable("cobblemon.ui.stats.atk").setStyle(red)
				.append(" §8- §3IV: §a" + (pokemon.getIvs().get(Stats.ATTACK) == null ? "0" :
						+ pokemon.getIvs().get(Stats.ATTACK)) + " §cEV: §a" +
						(pokemon.getEvs().get(Stats.ATTACK) == null ? "0" : pokemon.getEvs().get(Stats.ATTACK))));
		lore.add(Text.translatable("cobblemon.ui.stats.def").setStyle(gold)
				.append(" §8- §3IV: §a" + (pokemon.getIvs().get(Stats.DEFENCE) == null ? "0" :
						+ pokemon.getIvs().get(Stats.DEFENCE)) + " §cEV: §a" +
						(pokemon.getEvs().get(Stats.DEFENCE) == null ? "0" :
								pokemon.getEvs().get(Stats.DEFENCE))));
		lore.add(Text.translatable("cobblemon.ui.stats.sp_atk").setStyle(dark_purple)
				.append(" §8- §3IV: §a" + (pokemon.getIvs().get(Stats.SPECIAL_ATTACK) == null ? "0" :
						+ pokemon.getIvs().get(Stats.SPECIAL_ATTACK)) + " §cEV: §a" +
						(pokemon.getEvs().get(Stats.SPECIAL_ATTACK) == null ? "0" :
								pokemon.getEvs().get(Stats.SPECIAL_ATTACK))));
		lore.add(Text.translatable("cobblemon.ui.stats.sp_def").setStyle(yellow)
				.append(" §8- §3IV: §a" + (pokemon.getIvs().get(Stats.SPECIAL_DEFENCE) == null ? "0" :
						+ pokemon.getIvs().get(Stats.SPECIAL_DEFENCE)) + " §cEV: §a" +
						(pokemon.getEvs().get(Stats.SPECIAL_DEFENCE) == null ? "0" :
								pokemon.getEvs().get(Stats.SPECIAL_DEFENCE))));
		lore.add(Text.translatable("cobblemon.ui.stats.speed").setStyle(dark_aqua)
				.append(" §8- §3IV: §a" + (pokemon.getIvs().get(Stats.SPEED) == null ? "0" :
						+ pokemon.getIvs().get(Stats.SPEED)) + " §cEV: §a" +
						(pokemon.getEvs().get(Stats.SPEED) == null ? "0" :
								pokemon.getEvs().get(Stats.SPEED))));

		lore.add(Text.translatable("cobblemon.ui.stats.friendship").setStyle(dark_green)
				.append(": §a" + pokemon.getFriendship()));

		lore.add(Text.translatable("cobblemon.ui.moves").setStyle(gold).append(": "));
		for (Move move : pokemon.getMoveSet().getMoves()) {
			lore.add(Text.translatable(move.getTemplate().getDisplayName().getString()).setStyle(white));
		}

		return lore;
	}

	/**
	 * Converts Pokemon JsonObjects to Pokemon
	 * @param objects A list of Pokemon JsonObjects to convert.
	 * @return A list of Pokemon from the JsonObjects.
	 */
	public static List<Pokemon> fromJson(List<JsonObject> objects) {
		List<Pokemon> pokemon = new ArrayList<>();

		for (JsonObject object : objects) {
			Pokemon mon = new Pokemon().loadFromJSON(object);
			mon.heal();
			pokemon.add(mon);
		}

		return pokemon;
	}

	public static List<JsonObject> toJson(List<Pokemon> pokemons) {
		List<JsonObject> objects = new ArrayList<>();

		for (Pokemon pokemon : pokemons) {
			if (pokemon != null) {
				objects.add(pokemon.saveToJSON(new JsonObject()));
			}
		}

		return objects;
	}
}
