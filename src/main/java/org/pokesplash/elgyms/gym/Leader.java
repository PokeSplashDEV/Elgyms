package org.pokesplash.elgyms.gym;

import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.google.gson.JsonObject;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.provider.BattleProvider;
import org.pokesplash.elgyms.util.ElgymsUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Leader information.
 */
public class Leader {
	private UUID uuid; // UUID of the leader.
	private Record record; // Wins / Losses of the leader.
	private long startDate; // The start date of the leader.
	private ArrayList<JsonObject> team; // Leaders team.

	public Leader() {
		uuid = UUID.randomUUID();
		record = new Record();
		startDate = new Date().getTime();
		team = new ArrayList<>();
	}

	public Leader(UUID uuid) {
		this.uuid = uuid;
		record = new Record();
		startDate = new Date().getTime();
		team = new ArrayList<>();
	}

	public Leader(UUID uuid, PlayerPartyStore party) {
		this.uuid = uuid;
		record = new Record();
		startDate = new Date().getTime();
		team = new ArrayList<>();
		for (Pokemon pokemon : ElgymsUtils.setLevelAndPp(BattleProvider.toList(party), 100)) {
			team.add(pokemon.saveToJSON(new JsonObject()));
		}
	}

	public UUID getUuid() {
		return uuid;
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

	public void setTeam(ArrayList<Pokemon> team) {

		ArrayList<JsonObject> jsonObjects = new ArrayList<>();

		for (Pokemon pokemon : team) {
			jsonObjects.add(pokemon.saveToJSON(new JsonObject()));
		}

		this.team = jsonObjects;
	}

	public long getStartDate() {
		return startDate;
	}
}
