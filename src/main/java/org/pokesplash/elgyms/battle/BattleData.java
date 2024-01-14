package org.pokesplash.elgyms.battle;

import org.pokesplash.elgyms.gym.GymConfig;

import java.util.UUID;

public class BattleData {

    private UUID battleId;
    private UUID leaderId;
    private GymConfig gym;

    public BattleData(UUID battleId, UUID leaderId, GymConfig gym) {
        this.battleId = battleId;
        this.leaderId = leaderId;
        this.gym = gym;
    }

    public UUID getBattleId() {
        return battleId;
    }

    public UUID getLeaderId() {
        return leaderId;
    }

    public GymConfig getGym() {
        return gym;
    }
}
