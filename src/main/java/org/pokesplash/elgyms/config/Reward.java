package org.pokesplash.elgyms.config;

import java.util.ArrayList;

/**
 * Reward config
 */
public class Reward {
	private boolean enableBroadcast; // Should a broadcast happen.
	private String broadcastMessage; // The message to broadcast when the prestige is
	private ArrayList<String> commands; // A list of commands to run when a player prestiges.

	public Reward() {
		enableBroadcast = true;
		broadcastMessage = "{player} just prestiged in the {category} gyms!";

		commands = new ArrayList<>();
		commands.add("give {player} minecraft:diamond 1");
	}

	public boolean isEnableBroadcast() {
		return enableBroadcast;
	}

	public String getBroadcastMessage() {
		return broadcastMessage;
	}

	public ArrayList<String> getCommands() {
		return commands;
	}
}
