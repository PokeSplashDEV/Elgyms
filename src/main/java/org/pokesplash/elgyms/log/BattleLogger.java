package org.pokesplash.elgyms.log;

import com.google.gson.Gson;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.util.Utils;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class BattleLogger {
    private ArrayList<BattleLog> logs;

    public BattleLogger() {
        logs = new ArrayList<>();
    }


    public void init() {
        Utils.readFileAsync(Elgyms.BASE_PATH,
                "logs.json", el -> {
                    Gson gson = Utils.newGson();
                    BattleLogger cfg = gson.fromJson(el, BattleLogger.class);
                    logs = cfg.getLogs();
                });
    }

    private void write() {
        Gson gson = Utils.newGson();
        String data = gson.toJson(this);
        CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(Elgyms.BASE_PATH,
                "logs.json", data);

        if (!futureWrite.join()) {
            Elgyms.LOGGER.fatal("Could not write logs for " + Elgyms.MOD_ID + ".");
        }
    }

    public ArrayList<BattleLog> getLogs() {
        return logs;
    }

    public void addLog(BattleLog log) {
        logs.add(log);
        write();
    }
}
