package org.pokesplash.elgyms.champion;

import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.gym.Position;

import java.util.UUID;

public class ChampBattleData {

    private UUID battleId;
    private UUID leaderId;
    private String challengerName;
    private Position leaderBack;
    private Position challengerBack;

    public ChampBattleData(UUID battleId, UUID leaderId, String challengerName, Position leaderBack, Position challengerBack) {
        this.battleId = battleId;
        this.leaderId = leaderId;
        this.challengerName = challengerName;
        this.leaderBack = leaderBack;
        this.challengerBack = challengerBack;
    }

    public UUID getBattleId() {
        return battleId;
    }

    public UUID getLeaderId() {
        return leaderId;
    }

    public String getChallengerName() {
        return challengerName;
    }


    public Position getLeaderBack() {
        return leaderBack;
    }

    public Position getChallengerBack() {
        return challengerBack;
    }
}
