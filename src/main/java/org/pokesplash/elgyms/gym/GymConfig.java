package org.pokesplash.elgyms.gym;

import com.google.gson.Gson;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.provider.GymProvider;
import org.pokesplash.elgyms.type.Type;
import org.pokesplash.elgyms.util.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Config for a gym.
 */
public class GymConfig {
	private String id; // Unqiue ID of the gym.
	private String name; // The name of the gym.
	private String displayItem; // The item to display in the menu;
	private ArrayList<Type> types; // The Pokemon types of the gym.
	private boolean isE4; // Is this gym an E4?
	private int weight; // Weight relative to the other gyms.
	private String categoryName; // Category the gym is in.
	private double cooldown; // Cooldown in minutes.
	private Badge badge; // The badge of the gym.
	private Positions positions; // Positions of leader, challenger and spectator.
	private Requirements requirements; // Requirements for the gym.
	private GymRewards rewards; // Rewards for the gym.
	private HashSet<Leader> leaders; // Leaders of the gym.

	public void write() {
		Gson gson = Utils.newGson();
		String data = gson.toJson(this);
		String fileName = id + ".json";
		CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(Elgyms.BASE_PATH + "gyms/",
				fileName, data);

		if (!futureWrite.join()) {
			Elgyms.LOGGER.fatal("Could not write " + fileName + " for " + Elgyms.MOD_ID + ".");
		} else {
			GymProvider.addGym(this);
		}
	}

	public GymConfig() {
		id = "gym1";
		name = "Gym 1";
		displayItem = "cobblemon:azure_ball";
		types = new ArrayList<>();
		types.add(Type.BUG);
		types.add(Type.DARK);
		isE4 = false;
		weight = 1;
		categoryName = "Normal";
		badge = new Badge();
		cooldown = 60;
		positions = new Positions();
		requirements = new Requirements();
		rewards = new GymRewards();
		leaders = new HashSet<>();
		leaders.add(new Leader());
	}

	public GymConfig(String name, String categoryName) {
		id = nameToId(name);
		this.name = name;
		displayItem = "cobblemon:poke_ball";
		types = new ArrayList<>();
		isE4 = false;
		weight = 1;
		this.categoryName = categoryName;
		badge = new Badge();
		cooldown = 60;
		positions = new Positions();
		requirements = new Requirements();
		rewards = new GymRewards();
		leaders = new HashSet<>();
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Badge getBadge() {
		return badge;
	}

	public void setBadge(Badge badge) {
		this.badge = badge;
		write();
	}

	public void setName(String name) {
		GymProvider.deleteGym(this);
		this.name = name;
		this.id = nameToId(name);
		write();
		GymProvider.addGym(this);
	}

	public ArrayList<Type> getTypes() {
		return types;
	}

	public void setTypes(ArrayList<Type> types) {
		this.types = types;
		write();
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
		write();
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
		write();
	}

	public double getCooldown() {
		return cooldown;
	}

	public void setCooldown(double cooldown) {
		this.cooldown = cooldown;
		write();
	}

	public Positions getPositions() {
		return positions;
	}

	public void setPositions(Positions positions) {
		this.positions = positions;
		write();
	}

	public Requirements getRequirements() {
		return requirements;
	}

	public void setRequirements(Requirements requirements) {
		this.requirements = requirements;
		write();
	}

	public GymRewards getRewards() {
		return rewards;
	}

	public void setRewards(GymRewards rewards) {
		this.rewards = rewards;
		write();
	}

	public HashSet<Leader> getLeaders() {
		return leaders;
	}


	public void addLeader(Leader leader) {
		leaders.add(leader);
		write();
	}

	public Leader getLeader(UUID uuid) {
		for (Leader leader : leaders) {
			if (leader.getUuid().equals(uuid)) {
				return leader;
			}
		}
		return null;
	}

	public void updateLeader(Leader leader) {
		removeLeader(leader.getUuid());
		addLeader(leader);
	}

	public boolean containsLeader(UUID uuid) {
		for (Leader leader : leaders) {
			if (leader.getUuid().equals(uuid)) {
				return true;
			}
		}
		return false;
	}

	public void removeLeader(UUID uuid) {
		for (Leader leader : leaders) {
			if (leader.getUuid().equals(uuid)) leaders.remove(leader);
			write();
		}
	}

	public String getDisplayItem() {
		return displayItem;
	}

	public void setDisplayItem(String displayItem) {
		this.displayItem = displayItem;
		write();
	}

	public static String nameToId(String name) {
		return Utils.formatMessage(name, false).replaceAll(" ", "").toLowerCase();
	}

	public boolean isE4() {
		return isE4;
	}
}
