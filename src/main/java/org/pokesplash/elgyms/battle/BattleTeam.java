package org.pokesplash.elgyms.battle;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.battles.BattleRegistry;
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.google.gson.JsonObject;
import net.minecraft.server.network.ServerPlayerEntity;
import org.pokesplash.elgyms.exception.GymException;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.provider.BattleProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to store data for a player before a battle. Used to
 * create the battle with.
 */
public class BattleTeam {
    PlayerBattleActor battleActor; // Storage of battle actor.

    /**
     * Constructor for challengers.
     * @param player The player to start a battle with.
     */
    public BattleTeam(ServerPlayerEntity player, GymConfig gym) throws GymException {
        // Gets the players party and creates a list of battle pokemon.
        PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(player);

        List<BattlePokemon> battlePokemon;

        // If its raise to cap, make the battle pokemon the correct level.
        if (gym.getRequirements().isRaiseToCap()) {
            battlePokemon = raiseToCap(BattleProvider.toList(party), gym.getRequirements().getPokemonLevel());
        } else { // Otherwise just convert the team.
            battlePokemon = party.toBattleTeam(true, true, null);
        }

        battleActor = new PlayerBattleActor(player.getUuid(), battlePokemon);

        checkErrors(player);
    }

    /**
     * Constructor for leader
     * @param player The leader of the gym.
     * @param pokemonObjects A list of JsonObjects that represent the leaders team.
     */
    public BattleTeam(ServerPlayerEntity player, List<JsonObject> pokemonObjects) throws GymException {

        // Creates battle pokemon from the json objects.
        List<BattlePokemon> battlePokemon;

        // If its raise to cap, make the battle pokemon the correct level.
        battlePokemon = convertToBattlePokemon(convertJsonObjects(pokemonObjects));

        battleActor = new PlayerBattleActor(player.getUuid(), battlePokemon);

        checkErrors(player);
    }

    /**
     * Method used to change the level of thte players Pokemon before the battle.
     * @param party The party of the player.
     * @param level The level to set the party to.
     * @return A list of battle pokemon with the correct levels.
     */
    private List<BattlePokemon> raiseToCap(List<Pokemon> party, int level) {

        ArrayList<BattlePokemon> battlePokemon = new ArrayList<>();

        for (Pokemon pokemon : party) {

            Pokemon newPokemon = pokemon.clone(false, true);

            newPokemon.setLevel(level);

            battlePokemon.add(BattlePokemon.Companion.safeCopyOf(newPokemon));
        }

        return battlePokemon;
    }

    /**
     * Converts Pokemon JsonObjects to Pokemon
     * @param objects A list of Pokemon JsonObjects to convert.
     * @return A list of Pokemon from the JsonObjects.
     */
    private List<Pokemon> convertJsonObjects(List<JsonObject> objects) {
        List<Pokemon> pokemon = new ArrayList<>();

        for (JsonObject object : objects) {
            pokemon.add(new Pokemon().loadFromJSON(object).initialize());
        }

        return pokemon;
    }

    /**
     * Converts a list of Pokemon to BattlePokemon.
     * @param pokemon The list of Pokemon to convert.
     * @return The converted list as a list of BattlePokemon.
     */
    private List<BattlePokemon> convertToBattlePokemon(List<Pokemon> pokemon) {
        List<BattlePokemon> battlePokemon = new ArrayList<>();

        for (Pokemon mon : pokemon) {
            battlePokemon.add(BattlePokemon.Companion.safeCopyOf(mon));
        }

        return battlePokemon;
    }

    /**
     * Checks for errors with the players team.
     * @param player The player to set the errors for.
     */
    private void checkErrors(ServerPlayerEntity player) throws GymException {

        // Checks player has at least 1 Pokemon
        if (battleActor.getPokemonList().isEmpty()) {
            throw new GymException("§c" + player.getName().getString() +  " has no Pokemon.");
        }

        // Checks the challenger isn't already in a battle.
        if (BattleRegistry.INSTANCE.getBattleByParticipatingPlayer(player) != null) {
            throw new GymException("§c" + player.getName().getString() +  " is currently in a battle.");
        }
    }

    /**
     * Getters
     */

    public PlayerBattleActor getBattleActor() {
        return battleActor;
    }
}
