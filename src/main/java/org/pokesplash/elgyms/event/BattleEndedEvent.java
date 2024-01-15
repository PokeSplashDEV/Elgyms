package org.pokesplash.elgyms.event;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import kotlin.Unit;
import net.minecraft.server.network.ServerPlayerEntity;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.badge.PlayerBadges;
import org.pokesplash.elgyms.battle.BattleData;
import org.pokesplash.elgyms.config.CategoryConfig;
import org.pokesplash.elgyms.config.Reward;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.gym.GymRewards;
import org.pokesplash.elgyms.log.BattleLog;
import org.pokesplash.elgyms.provider.BadgeProvider;
import org.pokesplash.elgyms.provider.GymProvider;
import org.pokesplash.elgyms.util.ElgymsUtils;
import org.pokesplash.elgyms.util.Utils;

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


            // Ends the battle, removes the team, returns the data of the battle.
            BattleData battleData = GymProvider.endGymBattle(el.getBattle().getBattleId());

            if (battleData == null) {
                return Unit.INSTANCE;
            }

            // Gets a list of all winner UUIDs.
            ArrayList<UUID> winners = ElgymsUtils.getBattleActorIds(el.getWinners());

            GymConfig gym = battleData.getGym();

            GymRewards rewards = gym.getRewards();

            CategoryConfig category = Elgyms.config.getCategoryByName(gym.getCategoryName());

            // If the leader didn't win. Add badges.
            if (ElgymsUtils.didChallengerWin(winners, battleData.getLeaderId())) {

                for (UUID winner : winners) {

                    PlayerBadges badges = BadgeProvider.getBadges(winner);

                    // Gets the challenger.
                    ServerPlayerEntity challenger = Elgyms.server.getPlayerManager().getPlayer(winner);

                    String challengerName = challenger != null ? challenger.getName().getString() : "";

                    if (badges == null) {
                        BadgeProvider.addBadge(new PlayerBadges(winner, challengerName));
                    }

                    // Adds the badge
                    BadgeProvider.getBadges(winner).addBadge(category, gym.getBadge());

                    // Gets the correct reward.
                    Reward reward = battleData.isPrestige() ? rewards.getPrestige() : rewards.getFirstTime();

                    // Broadcasts the message
                    if (reward.isEnableBroadcast()) {
                        Utils.broadcastMessage(Utils.formatPlaceholders(reward.getBroadcastMessage(), null,
                                gym.getBadge(), challenger, category, gym, null));
                    }


                    // Run commands
                    Utils.runCommands(reward.getCommands(), challenger, gym.getBadge(), category, gym);
                }

                Elgyms.battleLogger.addLog(
                        new BattleLog(gym.getBadge(), battleData.getLeaderId(),
                                battleData.getChallengerName(), true));
            } else {
                // Sets the cooldown timer for the gym.
                ArrayList<UUID> loserIds = ElgymsUtils.getBattleActorIds(el.getLosers());

                long duration = (long) (gym.getCooldown() * 60 * 1000);

                for (UUID loser : loserIds) {

                    PlayerBadges badges = BadgeProvider.getBadges(loser);

                    // Gets the challenger.
                    ServerPlayerEntity challenger = Elgyms.server.getPlayerManager().getPlayer(loser);

                    String challengerName = challenger != null ? challenger.getName().getString() : "";

                    // TODO fix name here.
                    if (badges == null) {
                        BadgeProvider.addBadge(new PlayerBadges(loser, challengerName));
                    }

                    // Adds the badge
                    BadgeProvider.getBadges(loser).setCooldown(gym, new Date().getTime() + duration);

                    // Gets the correct reward.
                    Reward reward = rewards.getLoss();

                    // Broadcasts the message
                    if (reward.isEnableBroadcast()) {
                        Utils.broadcastMessage(Utils.formatPlaceholders(reward.getBroadcastMessage(), null,
                                gym.getBadge(), challenger, category, gym, null));
                    }


                    // Run commands
                    Utils.runCommands(reward.getCommands(), challenger, gym.getBadge(), category, gym);

                }

                Elgyms.battleLogger.addLog(
                        new BattleLog(gym.getBadge(), battleData.getLeaderId(),
                                battleData.getChallengerName(), false));
            }



            return Unit.INSTANCE;
        });
    }
}
