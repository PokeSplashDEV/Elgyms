package org.pokesplash.elgyms.gym;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Leader information.
 */
public class Leader {
	private UUID uuid; // UUID of the leader.
	private String name; // IGN of the leader.
	private Record record; // Wins / Losses of the leader.
	private ArrayList<JsonObject> team; // Leaders team.

	public Leader() {
		uuid = UUID.randomUUID();
		name = "minecraftIGN";
		record = new Record();
		team = new ArrayList<>();
	}

	public UUID getUuid() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Record getRecord() {
		return record;
	}

	public void setRecord(Record record) {
		this.record = record;
	}

	public ArrayList<JsonObject> getTeam() {
		return team;
	}

	public void setTeam(ArrayList<JsonObject> team) {
		this.team = team;
	}
}
