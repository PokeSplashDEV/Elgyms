package org.pokesplash.elgyms.battle;

import org.pokesplash.elgyms.gym.GymConfig;

import java.util.UUID;

public class BattleData {

    private UUID battleId;
    private UUID leaderId;
    private String challengerName;
    private GymConfig gym;
    private boolean isPrestige;

    public BattleData(UUID battleId, UUID leaderId, String challengerName, GymConfig gym, boolean isPrestige) {
        this.battleId = battleId;
        this.leaderId = leaderId;
        this.challengerName = challengerName;
        this.gym = gym;
        this.isPrestige = isPrestige;
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

    public boolean isPrestige() {
        return isPrestige;
    }
}
