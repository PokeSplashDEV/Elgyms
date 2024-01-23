package org.pokesplash.elgyms.config;

import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.google.gson.Gson;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.provider.E4Provider;
import org.pokesplash.elgyms.util.Utils;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Holds E4 data for a player.
 */
public class E4Team {
    private UUID player; // The players UUID.
    private ArrayList<String> species; // The species of the player.

    /**
     * Constructor the create the players E4 team.
     * @param player the player who owns the team.
     * @param species The species of Pokemon on the players team.
     */
    public E4Team(UUID player, ArrayList<String> species) {
        this.player = player;
        this.species = species;
        write();
    }

    public E4Team(UUID player, PlayerPartyStore party) {
        this.player = player;

        ArrayList<String> pokemonSpecies = new ArrayList<>();

        for (int x=0; x < 6; x++) {
            Pokemon pokemon = party.get(x);

            if (pokemon == null) {
                continue;
            }

            pokemonSpecies.add(pokemon.getSpecies().getName());
        }

        this.species = pokemonSpecies;
        write();
    }

    /**
     * Writes the current team to file.
     */
    public void write() {
        Gson gson = Utils.newGson();
        String data = gson.toJson(this);
        String fileName = player + ".json";
        CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(Elgyms.BASE_PATH + "e4/",
                fileName, data);

        if (!futureWrite.join()) {
            Elgyms.LOGGER.fatal("Could not write " + fileName + " for " + Elgyms.MOD_ID + ".");
        } else {
            E4Provider.addTeam(this);
        }
    }

    /**
     * Getters
     */

    public UUID getPlayer() {
        return player;
    }

    public ArrayList<String> getSpecies() {
        return species;
    }
}
