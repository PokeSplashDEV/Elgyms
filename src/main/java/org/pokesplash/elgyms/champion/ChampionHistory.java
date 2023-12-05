package org.pokesplash.elgyms.champion;

import com.google.gson.Gson;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.util.Utils;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

/**
 * Stores all history of champions.
 */
public class ChampionHistory {
	private ArrayList<ChampionHistoryItem> history; // List of previous champion runs.

	public ChampionHistory() {
		history = new ArrayList<>();
	}

	public ArrayList<ChampionHistoryItem> getHistory() {
		return history;
	}

	public void addHistory(ChampionHistoryItem historyItem) {
		history.add(historyItem);
		write();
	}

	private void write() {
		Gson gson = Utils.newGson();
		String data = gson.toJson(this);
		CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(Elgyms.BASE_PATH,
				"championHistory.json", data);

		if (!futureWrite.join()) {
			Elgyms.LOGGER.fatal("Could not write championHistory.json for " + Elgyms.MOD_ID + ".");
		}
	}

	public void init() {
		Utils.readFileAsync(Elgyms.BASE_PATH,
				"championHistory.json", el -> {
					Gson gson = Utils.newGson();
					ChampionHistory cfg = gson.fromJson(el, ChampionHistory.class);
					history = cfg.getHistory();
				});
	}


}
