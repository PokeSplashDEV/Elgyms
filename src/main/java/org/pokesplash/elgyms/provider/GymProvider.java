package org.pokesplash.elgyms.provider;

import com.google.gson.Gson;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.champion.ChampionConfig;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.gym.Leader;
import org.pokesplash.elgyms.gym.Queue;
import org.pokesplash.elgyms.util.Utils;

import java.io.File;
import java.util.*;

public abstract class GymProvider {
	private static String PATH = Elgyms.BASE_PATH + "gyms/";
	private static HashMap<String, GymConfig> gyms = new HashMap<>();
	private static HashMap<GymConfig, Queue> queues = new HashMap<>();
	private static HashSet<GymConfig> openGyms = new HashSet<>();
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

	public static HashSet<GymConfig> getOpenGyms() {
		return openGyms;
	}

	public static void openGym(GymConfig gym) {
		openGyms.add(gym);
	}

	public static void closeGym(GymConfig gym) {
		openGyms.remove(gym);
	}

	public static boolean hasOnlineLeader(GymConfig gym) {
		for (Leader leader : gym.getLeaders()) {
			if (Elgyms.server.getPlayerManager().getPlayer(leader.getUuid()) != null) {
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
			player.sendMessage(Text.literal("§6You are already in this queue."));
			return;
		}

		if (getQueueFromPlayer(player.getUuid()) != null) {
			player.sendMessage(Text.literal("§6You are already in another queue."));
			return;
		}


		queue.addToQueue(player.getUuid());
		queues.put(gymConfig, queue);

		// Sends message to the challenger.
		player.sendMessage(Text.literal(
				Utils.formatPlaceholders(Elgyms.lang.getChallengeMessageChallenger(),
		null, gymConfig.getBadge(), player, null, gymConfig)
		));

		// Send an announcements to the leaders.
		for (Leader leader : gymConfig.getLeaders()) {
			ServerPlayerEntity onlineLeader = Elgyms.server.getPlayerManager().getPlayer(leader.getUuid());

			if (onlineLeader != null) {
				onlineLeader.sendMessage(Text.literal(
					Utils.formatPlaceholders(
							Elgyms.lang.getChallengeMessageLeader(), null, gymConfig.getBadge(), player, null, gymConfig
					)
				));
			}
		}
	}

	public static void rejectChallenge(UUID challenger, ServerPlayerEntity leader) {
		Queue queue = getQueueFromPlayer(challenger);
		GymConfig gym = getGymFromPlayer(challenger);

		if (queue == null) {
			return;
		}

		queue.removeFromQueue(challenger);

		ServerPlayerEntity challengerPlayer = Elgyms.server.getPlayerManager().getPlayer(challenger);

		// Send messages to both challenger and leader.
		if (challengerPlayer != null) {
			challengerPlayer.sendMessage(Text.literal(
					Utils.formatPlaceholders(Elgyms.lang.getRejectChallengePlayer(), null, null,
							challengerPlayer, null, gym)
			));

			leader.sendMessage(Text.literal(
					Utils.formatPlaceholders(Elgyms.lang.getRejectChallengeLeader(), null, null,
							challengerPlayer, null, gym)
			));
		}
	}
}
