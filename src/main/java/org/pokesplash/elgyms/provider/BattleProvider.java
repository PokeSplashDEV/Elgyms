package org.pokesplash.elgyms.provider;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.party.PartyPosition;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.api.storage.pc.PCStore;
import com.cobblemon.mod.common.battles.*;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.google.gson.JsonObject;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.badge.PlayerBadges;
import org.pokesplash.elgyms.battle.BattleData;
import org.pokesplash.elgyms.battle.BattleTeam;
import org.pokesplash.elgyms.champion.ChampBattleData;
import org.pokesplash.elgyms.champion.ChampionConfig;
import org.pokesplash.elgyms.config.CategoryConfig;
import org.pokesplash.elgyms.config.E4Team;
import org.pokesplash.elgyms.exception.GymException;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.gym.Position;
import org.pokesplash.elgyms.type.Clause;
import org.pokesplash.elgyms.util.ElgymsUtils;

import java.util.*;

/**
 * Class that controls the battle mechanics for the gym.
 */
public class BattleProvider {

    private static final HashMap<UUID, BattleData> activeBattles = new HashMap<>(); // Battle Id / Leader UUID
    private static ChampBattleData champBattle = null; // The BattleData of the current champion battle.


    public static void beginBattle(ServerPlayerEntity challenger, ServerPlayerEntity leader, GymConfig gym,
                                   boolean giveLeaderPokemon) {

        // Checks the challenger team is valid.
        try {
            ElgymsUtils.checkChallengerRequirements(challenger,
                    toList(Cobblemon.INSTANCE.getStorage().getParty(challenger)), gym);

            // Creates the battle teams needed for the battle.
            BattleTeam challengerTeam = new BattleTeam(challenger, gym);
            BattleTeam leaderTeam = new BattleTeam(leader, gym.getLeader(leader.getUuid()).getTeam());


            if (giveLeaderPokemon) {
                // Gives the leader their Pokemon.
                getLeaderTeam(leader, gym);
            }


            // Gets the gym positions for leaders and challengers.
            Position leaderPosition = gym.getPositions().getLeader();
            Position challengerPosition = gym.getPositions().getChallenger();

            // Teleport the players to their positions.
            ElgymsUtils.teleportToPosition(leader, leaderPosition);
            ElgymsUtils.teleportToPosition(challenger, challengerPosition);

            // Remove player from queue
            GymProvider.getQueueFromGym(gym).removeFromQueue(challenger.getUuid());

            // Creates the rules.
            Set<String> rules = new HashSet<>();
            rules.add(BattleRules.OBTAINABLE);
            rules.add(BattleRules.UNOBTAINABLE);
            rules.add(BattleRules.PAST);

            // Adds sleep clause if the gym requires it.
            if (gym.getRequirements().getClauses().contains(Clause.SLEEP)) {
                rules.add("Sleep Clause Mod");
            }

            BattleFormat format = new BattleFormat("cobblemon", BattleTypes.INSTANCE.getSINGLES(), rules, 9);

            // Starts the battle.
            BattleStartResult result = BattleRegistry.INSTANCE.startBattle(
                    format,
                    new BattleSide(leaderTeam.getBattleActor()),
                    new BattleSide(challengerTeam.getBattleActor()),
                    false
            );

            // Checks the battle started.
            boolean success = result instanceof SuccessfulBattleStart;

            // if the battle started, track the battle ID.
            if (success) {
                UUID battleId = ((SuccessfulBattleStart) result).getBattle().getBattleId();

                PlayerBadges badges = BadgeProvider.getBadges(challenger);

                CategoryConfig category = Elgyms.config.getCategoryByName(gym.getCategoryName());

                activeBattles.put(battleId, new BattleData(battleId, leader.getUuid(),
                        challenger.getName().getString(), gym, badges.isPrestiged(category),
                        ElgymsUtils.getPosition(leader), ElgymsUtils.getPosition(challenger)));
            }


        } catch (Exception e) {
            sendErrors(challenger, leader, e);
        }
    }

    public static void beginChampionBattle(ServerPlayerEntity challenger, ServerPlayerEntity leader) {

        // Checks the challenger team is valid.
        try {

            ChampionConfig championConfig = GymProvider.getChampion();

            ElgymsUtils.checkChampionRequirements(challenger,
                    toList(Cobblemon.INSTANCE.getStorage().getParty(challenger)));

            // Creates the battle teams needed for the battle.
            BattleTeam challengerTeam = new BattleTeam(challenger);
            BattleTeam leaderTeam = new BattleTeam(leader, championConfig.getChampion().getTeam());

            // Gets the gym positions for leaders and challengers.
            Position leaderPosition = championConfig.getPositions().getLeader();
            Position challengerPosition = championConfig.getPositions().getChallenger();

            // Teleport the players to their positions.
            ElgymsUtils.teleportToPosition(leader, leaderPosition);
            ElgymsUtils.teleportToPosition(challenger, challengerPosition);

            // Remove player from queue
            GymProvider.getChampQueue().removeFromQueue(challenger.getUuid());

            // Creates the rules.
            Set<String> rules = new HashSet<>();
            rules.add(BattleRules.OBTAINABLE);
            rules.add(BattleRules.UNOBTAINABLE);
            rules.add(BattleRules.PAST);

            // Adds sleep clause if the gym requires it.
            if (championConfig.getRequirements().getClauses().contains(Clause.SLEEP)) {
                rules.add("Sleep Clause Mod");
            }

            BattleFormat format = new BattleFormat("cobblemon", BattleTypes.INSTANCE.getSINGLES(), rules, 9);

            // Starts the battle.
            BattleStartResult result = BattleRegistry.INSTANCE.startBattle(
                    format,
                    new BattleSide(leaderTeam.getBattleActor()),
                    new BattleSide(challengerTeam.getBattleActor()),
                    false
            );

            // Checks the battle started.
            boolean success = result instanceof SuccessfulBattleStart;

            // if the battle started, track the battle ID.
            if (success) {
                UUID battleId = ((SuccessfulBattleStart) result).getBattle().getBattleId();

                champBattle = new ChampBattleData(battleId, leader.getUuid(), challenger.getName().getString(),
                        ElgymsUtils.getPosition(leader), ElgymsUtils.getPosition(challenger));
            }
        } catch (Exception e) {
            sendErrors(challenger, leader, e);
        }
    }

    public static ArrayList<Pokemon> getLeaderTeam(ServerPlayerEntity player, GymConfig gym) throws Exception {

        ArrayList<JsonObject> leaderTeam = gym.getLeader(player.getUuid()).getTeam();

        // If they don't have a team, throw an error.
        if (leaderTeam.isEmpty()) {
            throw new GymException("You have no team.");
        }

        ArrayList<Pokemon> pokemons = new ArrayList<>();

        // Add the leaders gym Pokemon to their party.
        for (JsonObject pokemonObject : leaderTeam) {
            Pokemon leaderPokemon = new Pokemon().loadFromJSON(pokemonObject);

            pokemons.add(leaderPokemon);
        }

        return pokemons;
    }

    public static ArrayList<Pokemon> getChampTeam() throws Exception {

        ArrayList<JsonObject> leaderTeam = GymProvider.getChampion().getChampion().getTeam();

        // If they don't have a team, throw an error.
        if (leaderTeam.isEmpty()) {
            throw new GymException("You have no team.");
        }

        ArrayList<Pokemon> pokemons = new ArrayList<>();

        // Add the leaders gym Pokemon to their party.
        for (JsonObject pokemonObject : leaderTeam) {
            Pokemon leaderPokemon = new Pokemon().loadFromJSON(pokemonObject);

            pokemons.add(leaderPokemon);
        }

        return pokemons;
    }

    public static boolean isGymBattle(UUID battleId) {
        return activeBattles.containsKey(battleId);
    }

    public static boolean isChampBattle(UUID battleId) {
        return battleId.equals(champBattle.getBattleId());
    }

    public static BattleData endGymBattle(UUID battleId) {

        // Get the leaders UUID and remove the battle from active battles.
        return activeBattles.remove(battleId);
    }

    public static ChampBattleData getChampBattle() {
        return champBattle;
    }

    public static void endChampBattle() {
        champBattle = null;
    }

    /**
     * Method used to send errors to the players or log to console.
     * @param player1 The first player to send the message to.
     * @param player2 The second player to send the message to.
     * @param error The error to send.
     */
    private static void sendErrors(ServerPlayerEntity player1, ServerPlayerEntity player2, Exception error) {

        Text generalError = Text.literal("§cSomething went wrong, the error was logged to the console.");

        if (error instanceof GymException) {
            player1.sendMessage(Text.literal(error.getMessage()));
            player2.sendMessage(Text.literal(error.getMessage()));
        } else {
            error.printStackTrace();
            player1.sendMessage(generalError);
            player2.sendMessage(generalError);
        }
    }

    /**
     * Converts a party to a list of Pokemon.
     * @param party The party to convert.
     * @return A list of pokemon from the party given.
     */
    public static List<Pokemon> toList(PlayerPartyStore party) {

        List<Pokemon> pokemon = new ArrayList<>();

        for (int x=0; x < 6; x++) {
            if (party.get(x) != null) {
                pokemon.add(party.get(x));
            }
        }

        return pokemon;
    }
}
