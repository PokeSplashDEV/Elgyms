package org.pokesplash.elgyms.champion;

import org.pokesplash.elgyms.gym.Leader;
import org.pokesplash.elgyms.gym.Record;

import java.util.Date;
import java.util.UUID;

/**
 * History of a single champion.
 */
public class ChampionHistoryItem {
	private UUID uuid; // UUID of the leader.
	private String name; // IGN of the leader.
	private Record record; // Wins / Losses of the leader.
	private long startDate; // The start date of the leader.
	private long endDate; // The end date of the leader.

	public ChampionHistoryItem(Leader leader) {
		this.uuid = leader.getUuid();
		this.record = leader.getRecord();
		this.startDate = leader.getStartDate();
		this.endDate = new Date().getTime();
	}

	public UUID getUuid() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public Record getRecord() {
		return record;
	}

	public long getStartDate() {
		return startDate;
	}

	public long getEndDate() {
		return endDate;
	}
}
