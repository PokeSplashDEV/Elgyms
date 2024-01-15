package org.pokesplash.elgyms.provider;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.party.PartyPosition;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.api.storage.pc.PCStore;
import com.cobblemon.mod.common.battles.BattleBuilder;
import com.cobblemon.mod.common.battles.BattleStartError;
import com.cobblemon.mod.common.battles.BattleStartResult;
import com.cobblemon.mod.common.battles.SuccessfulBattleStart;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.badge.PlayerBadges;
import org.pokesplash.elgyms.battle.BattleData;
import org.pokesplash.elgyms.champion.ChampionConfig;
import org.pokesplash.elgyms.exception.GymException;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.gym.Leader;
import org.pokesplash.elgyms.gym.Queue;
import org.pokesplash.elgyms.util.ElgymsUtils;
import org.pokesplash.elgyms.util.Utils;

import java.io.File;
import java.util.*;

public abstract class GymProvider {
	private static String PATH = Elgyms.BASE_PATH + "gyms/";
	private static HashMap<String, GymConfig> gyms = new HashMap<>();
	private static HashMap<GymConfig, Queue> queues = new HashMap<>();
	private static HashSet<GymConfig> openGyms = new HashSet<>();
	private static HashMap<UUID, BattleData> activeBattles = new HashMap<>(); // Battle Id / Leader UUID
	private static ChampionConfig champion = new ChampionConfig();

	/**
	 * Method to fetch all gyms.
	 */
	public static void init() {

		gyms = new HashMap<>();

		champion.init(); // Initialize the champion config.

		try {
			File dir = Utils.checkForDirectory(PATH);

			String[] list = dir.list();

			// If no files, return.
			if (list.length == 0) {
				return;
			}

			for (String file : list) {

				Utils.readFileAsync(PATH,
						file, el -> {
							Gson gson = Utils.newGson();
							GymConfig gym = gson.fromJson(el, GymConfig.class);
							gyms.put(gym.getId(), gym);
							queues.put(gym, new Queue());
						});
			}

			Elgyms.LOGGER.info(Elgyms.MOD_ID + " gyms successfully read.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static HashMap<String, GymConfig> getGyms() {
		return gyms;
	}

	public static ArrayList<GymConfig> getGymsByCategory(String categoryName) {

		ArrayList<GymConfig> filteredGyms = new ArrayList<>();
		for (GymConfig gymConfig : gyms.values()) {
			if (gymConfig.getCategoryName().equalsIgnoreCase(categoryName)) {
				filteredGyms.add(gymConfig);
			}
		}
		filteredGyms.sort(Comparator.comparingInt(GymConfig::getWeight));

		return filteredGyms;
	}

	public static ArrayList<GymConfig> getGymsByLeader(UUID leader) {
		ArrayList<GymConfig> filteredGyms = new ArrayList<>();
		for (GymConfig gymConfig : gyms.values()) {
			for (Leader leader1 : gymConfig.getLeaders()) {
				if (leader1.getUuid().equals(leader)) {
					filteredGyms.add(gymConfig);
				}
			}
		}
		filteredGyms.sort(Comparator.comparingInt(GymConfig::getWeight));

		return filteredGyms;
	}
	public static ChampionConfig getChampion() {
		return champion;
	}

	public static void addGym(GymConfig gymConfig) {
		gyms.put(gymConfig.getId(), gymConfig);
	}

	public static Queue getQueueFromGym(GymConfig gym) {
		return queues.get(gym);
	}

	public static Queue getQueueFromPlayer(UUID player) {
		for (Queue queue : queues.values()) {
			if (queue.isInQueue(player)) {
				return queue;
			}
		}
		return null;
	}

	public static GymConfig getGymFromPlayer(UUID player) {
		for (GymConfig gym : queues.keySet()) {
			Queue queue = queues.get(gym);
			if (queue.isInQueue(player)) {
				return gym;
			}
		}
		return null;
	}

	public static GymConfig getGymFromBadge(UUID badgeId) {
		for (GymConfig gym : queues.keySet()) {
			if (gym.getBadge().getId().equals(badgeId)) {
				return gym;
			}
		}
		return null;
	}

	public static HashSet<GymConfig> getOpenGyms() {
		return openGyms;
	}

	public static void openGym(GymConfig gym, ServerPlayerEntity player) {
		openGyms.add(gym);
		Utils.broadcastMessage(Utils.formatPlaceholders(
				Elgyms.lang.getPrefix() +
						Elgyms.lang.getOpenGymMessage(), null, null, player,
				null, gym, null
		));
	}

	public static void openAllGyms(ServerPlayerEntity player) {
		ArrayList<GymConfig> leaderGyms = GymProvider.getGymsByLeader(player.getUuid());
		leaderGyms.removeAll(GymProvider.getOpenGyms());
		for (GymConfig gymConfig : leaderGyms) {
			GymProvider.openGym(gymConfig, player);

		}
	}

	public static void closeGym(GymConfig gym, ServerPlayerEntity player) {
		openGyms.remove(gym);
		Utils.broadcastMessage(Utils.formatPlaceholders(
				Elgyms.lang.getPrefix() +
						Elgyms.lang.getCloseGymMessage(), null, null, player,
				null, gym, null
		));
	}

	public static void closeAllGyms(ServerPlayerEntity player) {
		// Closes all the players gyms.
		ArrayList<GymConfig> leaderGyms = GymProvider.getGymsByLeader(player.getUuid());
		for (GymConfig gymConfig : leaderGyms) {
			if (!GymProvider.hasOtherOnlineLeaders(gymConfig, player)) {
				GymProvider.closeGym(gymConfig, player);

			}
		}
	}


	public static boolean hasOtherOnlineLeaders(GymConfig gym, ServerPlayerEntity player) {
		for (Leader leader : gym.getLeaders()) {
			if (Elgyms.server.getPlayerManager().getPlayer(leader.getUuid()) != null &&
			!leader.getUuid().equals(player.getUuid())) {
				return true;
			}
		}
		return false;
	}

	public static GymConfig getGymById(String id) {
		return gyms.get(id);
	}

	public static boolean deleteGym(GymConfig gym) {
		gyms.remove(gym.getId());
		queues.remove(gym);
		openGyms.remove(gym);
		return Utils.deleteFile(PATH, gym.getId() + ".json");
	}

	public static void challengeGym(ServerPlayerEntity player, GymConfig gymConfig) {
		Queue queue = queues.get(gymConfig);

		if (queue.isInQueue(player.getUuid())) {
			player.sendMessage(Text.literal(Elgyms.lang.getPrefix() + "§6You are already in this queue."));
			return;
		}

		if (getQueueFromPlayer(player.getUuid()) != null) {
			player.sendMessage(Text.literal(Elgyms.lang.getPrefix() + "§6You are already in another queue."));
			return;
		}

		PlayerBadges badges = BadgeProvider.getBadges(player);

		if (badges.getCooldown(gymConfig) != null && badges.getCooldown(gymConfig) > new Date().getTime()) {
			player.sendMessage(Text.literal(Elgyms.lang.getPrefix() + Utils.formatPlaceholders(
					Elgyms.lang.getCooldownMessage(), null, null, player,
					Elgyms.config.getCategoryByName(gymConfig.getCategoryName()), gymConfig, badges.getCooldown(gymConfig)
			)));
			return;
		}


		queue.addToQueue(player.getUuid());
		queues.put(gymConfig, queue);

		// Sends message to the challenger.
		player.sendMessage(Text.literal(
				Utils.formatPlaceholders(Elgyms.lang.getPrefix() + Elgyms.lang.getChallengeMessageChallenger(),
		null, gymConfig.getBadge(), player, null, gymConfig, badges.getCooldown(gymConfig))
		));

		// Send an announcements to the leaders.
		for (Leader leader : gymConfig.getLeaders()) {
			ServerPlayerEntity onlineLeader = Elgyms.server.getPlayerManager().getPlayer(leader.getUuid());

			if (onlineLeader != null) {
				onlineLeader.sendMessage(Text.literal(
					Utils.formatPlaceholders(
							Elgyms.lang.getPrefix() +
							Elgyms.lang.getChallengeMessageLeader(), null, gymConfig.getBadge(), player, null, gymConfig
					, badges.getCooldown(gymConfig))
				));
			}
		}
	}

	public static void cancelChallenge(UUID challenger) {
		Queue queue = getQueueFromPlayer(challenger);

		if (queue == null) {
			return;
		}

		queue.removeFromQueue(challenger);
	}

	public static void rejectChallenge(UUID challenger, ServerPlayerEntity leader) {
		cancelChallenge(challenger);

		ServerPlayerEntity challengerPlayer = Elgyms.server.getPlayerManager().getPlayer(challenger);

		GymConfig gym = getGymFromPlayer(challenger);

		// Send messages to both challenger and leader.
		if (challengerPlayer != null) {
			challengerPlayer.sendMessage(Text.literal(
					Utils.formatPlaceholders(Elgyms.lang.getPrefix() + Elgyms.lang.getRejectChallengePlayer(), null,
							null,
							challengerPlayer, null, gym, null)
			));

			leader.sendMessage(Text.literal(
					Utils.formatPlaceholders(Elgyms.lang.getPrefix() + Elgyms.lang.getRejectChallengeLeader(), null, null,
							challengerPlayer, null, gym, null)
			));
		}
	}

	public static void beginBattle(ServerPlayerEntity challenger, ServerPlayerEntity leader, GymConfig gym) {

		try {
			// Gets a list of all challenger Pokemon
			ArrayList<Pokemon> challengerPokemon = new ArrayList<>();
			PlayerPartyStore challengerParty = Cobblemon.INSTANCE.getStorage().getParty(challenger);
			for (int x=0; x < 6; x++) {
				Pokemon pokemon = challengerParty.get(x);

				if (pokemon == null) {
					continue;
				}

				challengerPokemon.add(pokemon);
			}

			// Checks challenger Pokemon are valid.
			ElgymsUtils.checkChallengerRequirements(challengerPokemon, gym);
		} catch (Exception e) {
			// Sends the error to the challenger. Tells leader that the challenger team is illegal.
			challenger.sendMessage(Text.literal("§c" + e.getMessage()));
			leader.sendMessage(Text.literal("§c" + "The challenger has an illegal team."));
			return;
		}


		try {
			// Gives the leader their Pokemon.
			giveLeaderPokemon(leader, gym);

			// Remove player from queue
			getQueueFromGym(gym).removeFromQueue(challenger.getUuid());

			// Start the battle
			BattleStartResult result = BattleBuilder.INSTANCE.pvp1v1(challenger, leader);

			// Checks the battle started.
			boolean success = result instanceof SuccessfulBattleStart;

			// if the battle started, track the battle ID.
			if (success) {
				UUID battleId = ((SuccessfulBattleStart) result).getBattle().getBattleId();
				activeBattles.put(battleId, new BattleData(battleId, leader.getUuid(),
						challenger.getName().getString(), gym));
			} else {
				// Otherwise throw an error.
				BattleStartError error = (BattleStartError) result;

				World leaderWorld = leader.getEntityWorld();

				Entity leaderEntity = Elgyms.server.getWorld(leaderWorld.getRegistryKey()).getEntity(leader.getUuid());

				if (leaderEntity != null) {
					throw new Exception(error.getMessageFor(leaderEntity).getString());
				}
			}

		} catch (Exception e) {
			// Sends error to leader. Tells challenger something went wrong.
			leader.sendMessage(Text.literal("§c" + e.getMessage()));
			challenger.sendMessage(Text.literal("§c" + "Something went wrong, the leader has more info."));

			if (!(e instanceof GymException)) {
				Elgyms.LOGGER.error(e.getMessage());
			}
		}
	}

	private static void giveLeaderPokemon(ServerPlayerEntity player, GymConfig gym) throws Exception {
		PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(player);
		PCStore pcStore = Cobblemon.INSTANCE.getStorage().getPC(player.getUuid());

		ArrayList<JsonObject> leaderTeam = gym.getLeader(pcStore.getUuid()).getTeam();

		// If they don't have a team, throw an error.
		if (leaderTeam.isEmpty()) {
			throw new GymException("You have no team.");
		}

		// Move the leaders Pokemon to their PC.
		for (int x = 0; x < 6; x++) {
			Pokemon pokemon = party.get(x);

			if (pokemon == null) {
				continue;
			}

			party.remove(new PartyPosition(x));

			pcStore.add(pokemon);
		}

		// Add the leaders gym Pokemon to their party.
		for (JsonObject pokemonObject : leaderTeam) {
			Pokemon leaderPokemon = new Pokemon().loadFromJSON(pokemonObject).initialize();

			party.add(leaderPokemon);
		}
	}

	public static boolean isGymBattle(UUID battleId) {
		return activeBattles.containsKey(battleId);
	}

	public static BattleData endGymBattle(UUID battleId) {

		// Get the leaders UUID and remove the battle from active battles.
		BattleData battleData = GymProvider.activeBattles.remove(battleId);

		if (battleData == null) {
			return null;
		}

		// get rid of the leaders Pokemon.
		try {
			PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(battleData.getLeaderId());

			ArrayList<Species> expectedLeaderTeam = new ArrayList<>();
			// Gets the expected Pokemon Species for the leaders team.
			for (JsonObject pokemonObject : battleData.getGym().getLeader(battleData.getLeaderId()).getTeam()) {
				expectedLeaderTeam.add(new Pokemon().loadFromJSON(pokemonObject).initialize().getSpecies());
			}

			// Checks each pokemon in the leaders party is from their gym team.
			for (int x=0; x < 6; x++) {
				Pokemon pokemon = party.get(x);

				if (pokemon == null) {
					continue;
				}

				// If the pokemon in their party matches their gym team, remove it.
				if (expectedLeaderTeam.contains(pokemon.getSpecies())) {
					party.remove(new PartyPosition(x));
					expectedLeaderTeam.remove(pokemon.getSpecies());
				}
			}

			// logs that not all Pokemon have been returned.
			if (!expectedLeaderTeam.isEmpty()) {
				// TODO Log not all Pokemon have been returned.
			}

			// Returns the leaders UUID.
			return battleData;

		} catch (Exception e) {
			Elgyms.LOGGER.error(e.getMessage());
			return null;
		}
	}
}
