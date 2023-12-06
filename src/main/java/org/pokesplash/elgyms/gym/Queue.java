package org.pokesplash.elgyms.gym;

import java.util.ArrayList;
import java.util.UUID;

public class Queue {
	private ArrayList<UUID> queue;

	public Queue() {
		queue = new ArrayList<>();
	}

	public ArrayList<UUID> getQueue() {
		return queue;
	}

	public void addToQueue(UUID player) {
		if (!queue.contains(player)) {
			queue.add(player);
		}
	}

	public void removeFromQueue(UUID player) {
		queue.remove(player);
	}

	public boolean isInQueue(UUID player) {
		return queue.contains(player);
	}
}
