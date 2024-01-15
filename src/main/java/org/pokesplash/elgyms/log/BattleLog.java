package org.pokesplash.elgyms.log;

import org.pokesplash.elgyms.gym.Badge;

import java.util.UUID;

public class BattleLog {
    private Badge gymBadge;
    private UUID leaderId;
    private String challengerName;
    private boolean isChallengerWinner;

    public BattleLog(Badge gymBadge, UUID leaderId, String challengerName, boolean isChallengerWinner) {
        this.gymBadge = gymBadge;
        this.leaderId = leaderId;
        this.challengerName = challengerName;
        this.isChallengerWinner = isChallengerWinner;
    }

    public Badge getGymBadge() {
        return gymBadge;
    }

    public UUID getLeaderId() {
        return leaderId;
    }

    public String getChallengerName() {
        return challengerName;
    }

    public boolean isChallengerWinner() {
        return isChallengerWinner;
    }
}
