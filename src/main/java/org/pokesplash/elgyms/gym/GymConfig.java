package org.pokesplash.elgyms.gym;

import com.google.gson.Gson;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.type.Type;
import org.pokesplash.elgyms.util.Utils;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

/**
 * Config for a gym.
 */
public class GymConfig {
	private String id; // Unqiue ID of the gym.
	private String name; // The name of the gym.
	private ArrayList<Type> types; // The Pokemon types of the gym.
	private int weight; // Weight relative to the other gyms.
	private String categoryName; // Category the gym is in.
	private double cooldown; // Cooldown in minutes.
	private int wildcardAmount; // Amount of Pokemon that do not share a type are allowed.
	private Positions positions; // Positions of leader, challenger and spectator.
	private Requirements requirements; // Requirements for the gym.
	private GymRewards rewards; // Rewards for the gym.
	private ArrayList<Leader> leaders; // Leaders of the gym.

	public void write() {
		Gson gson = Utils.newGson();
		String data = gson.toJson(this);
		String fileName = id + ".json";
		CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(Elgyms.BASE_PATH + "gyms/",
				fileName, data);

		if (!futureWrite.join()) {
			Elgyms.LOGGER.fatal("Could not write " + fileName + " for " + Elgyms.MOD_ID + ".");
		}
	}

	public GymConfig() {
		id = "gym1";
		name = "Gym 1";
		types = new ArrayList<>();
		types.add(Type.BUG);
		types.add(Type.DARK);
		weight = 1;
		categoryName = "Normal";
		cooldown = 60;
		wildcardAmount = 1;
		positions = new Positions();
		requirements = new Requirements(id);
		rewards = new GymRewards();
		leaders = new ArrayList<>();
		leaders.add(new Leader());
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		write();
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

	public int getWildcardAmount() {
		return wildcardAmount;
	}

	public void setWildcardAmount(int wildcardAmount) {
		this.wildcardAmount = wildcardAmount;
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

	public ArrayList<Leader> getLeaders() {
		return leaders;
	}

	public void setLeaders(ArrayList<Leader> leaders) {
		this.leaders = leaders;
		write();
	}
}
