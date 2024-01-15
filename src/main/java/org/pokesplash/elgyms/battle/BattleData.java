package org.pokesplash.elgyms.battle;

import org.pokesplash.elgyms.gym.GymConfig;

import java.util.UUID;

public class BattleData {

    private UUID battleId;
    private UUID leaderId;
    private String challengerName;
    private GymConfig gym;

    public BattleData(UUID battleId, UUID leaderId, String challengerName, GymConfig gym) {
        this.battleId = battleId;
        this.leaderId = leaderId;
        this.challengerName = challengerName;
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

    public String getChallengerName() {
        return challengerName;
    }
}
