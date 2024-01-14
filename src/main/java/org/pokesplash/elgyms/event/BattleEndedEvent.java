package org.pokesplash.elgyms.event;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import kotlin.Unit;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.badge.PlayerBadges;
import org.pokesplash.elgyms.battle.BattleData;
import org.pokesplash.elgyms.config.CategoryConfig;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.provider.BadgeProvider;
import org.pokesplash.elgyms.provider.GymProvider;
import org.pokesplash.elgyms.util.ElgymsUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class BattleEndedEvent {
    public void registerEvent() {
        CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.NORMAL, el -> {

            // If its not a PVP battle, return.
            if (!el.getBattle().isPvP()) {
                return Unit.INSTANCE;
            }

            // If it's not a gym battle, return.
            if (!GymProvider.isGymBattle(el.getBattle().getBattleId())) {
                return Unit.INSTANCE;
            }


            // Ends the battle and returns the team.
            BattleData battleData = GymProvider.endGymBattle(el.getBattle().getBattleId());

            if (battleData == null) {
                return Unit.INSTANCE;
            }

            // Gets a list of all winner UUIDs.
            ArrayList<UUID> winners = ElgymsUtils.getBattleActorIds(el.getWinners());

            GymConfig gym = battleData.getGym();

            // If the leader didn't win. Add badges.
            if (ElgymsUtils.didChallengerWin(winners, battleData.getLeaderId())) {

                CategoryConfig category = Elgyms.config.getCategoryByName(gym.getCategoryName());

                for (UUID winner : winners) {

                    PlayerBadges badges = BadgeProvider.getBadges(winner);

                    // TODO fix name here.
                    if (badges == null) {
                        BadgeProvider.addBadge(new PlayerBadges(winner, ""));
                    }

                    // Adds the badge
                    BadgeProvider.getBadges(winner).addBadge(category, gym.getBadge());
                }


                // TODO announce challenger beat gym.
            } else {
                // Sets the cooldown timer for the gym.
                ArrayList<UUID> loserIds = ElgymsUtils.getBattleActorIds(el.getLosers());

                long duration = (long) (gym.getCooldown() * 60 * 1000);

                for (UUID loser : loserIds) {

                    PlayerBadges badges = BadgeProvider.getBadges(loser);

                    // TODO fix name here.
                    if (badges == null) {
                        BadgeProvider.addBadge(new PlayerBadges(loser, ""));
                    }

                    // Adds the badge
                    BadgeProvider.getBadges(loser).setCooldown(gym, new Date().getTime() + duration);

                }


                // TODO announce challenger lost in gym.
            }



            return Unit.INSTANCE;
        });
    }
}
