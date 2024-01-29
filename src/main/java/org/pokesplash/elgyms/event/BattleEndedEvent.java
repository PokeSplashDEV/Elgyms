package org.pokesplash.elgyms.event;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import kotlin.Unit;
import net.minecraft.server.network.ServerPlayerEntity;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.badge.PlayerBadges;
import org.pokesplash.elgyms.battle.BattleData;
import org.pokesplash.elgyms.champion.ChampBattleData;
import org.pokesplash.elgyms.champion.ChampionConfig;
import org.pokesplash.elgyms.champion.ChampionHistoryItem;
import org.pokesplash.elgyms.config.CategoryConfig;
import org.pokesplash.elgyms.config.E4Team;
import org.pokesplash.elgyms.config.Reward;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.gym.GymRewards;
import org.pokesplash.elgyms.gym.Leader;
import org.pokesplash.elgyms.gym.Record;
import org.pokesplash.elgyms.log.BattleLog;
import org.pokesplash.elgyms.provider.BadgeProvider;
import org.pokesplash.elgyms.provider.BattleProvider;
import org.pokesplash.elgyms.provider.E4Provider;
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

            // If it's a gym battle, sort it out.
            if (BattleProvider.isGymBattle(el.getBattle().getBattleId())) {
                // Ends the battle, removes the team, returns the data of the battle.
                BattleData battleData = BattleProvider.endGymBattle(el.getBattle().getBattleId());

                if (battleData == null) {
                    return Unit.INSTANCE;
                }

                // Gets a list of all winner UUIDs.
                ArrayList<UUID> winners = ElgymsUtils.getBattleActorIds(el.getWinners());

                GymConfig gym = battleData.getGym();

                GymRewards rewards = gym.getRewards();

                CategoryConfig category = Elgyms.config.getCategoryByName(gym.getCategoryName());

                Record leaderRecord = gym.getLeader(battleData.getLeaderId()).getRecord();

                // If the leader didn't win. Add badges.
                if (ElgymsUtils.didChallengerWin(winners, battleData.getLeaderId())) {

                    leaderRecord.setLosses(leaderRecord.getLosses() + 1);
                    gym.write();

                    for (UUID winner : winners) {

                        PlayerBadges badges = BadgeProvider.getBadges(winner);

                        // Gets the challenger.
                        ServerPlayerEntity challenger = Elgyms.server.getPlayerManager().getPlayer(winner);

                        String challengerName = challenger != null ? challenger.getName().getString() : "";

                        if (badges == null) {
                            BadgeProvider.addBadge(new PlayerBadges(winner, challengerName));
                        }

                        // If the gym is E4 and the challenger doesn't have a team, set their E4 team to their current team.
                        if (gym.isE4() && E4Provider.getTeam(challenger.getUuid()) == null) {

                            PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(challenger);

                            E4Provider.addTeam(new E4Team(challenger.getUuid(), party));
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

                    leaderRecord.setWins(leaderRecord.getWins() + 1);
                    gym.write();

                    // Sets the cooldown timer for the gym.
                    ArrayList<UUID> loserIds = ElgymsUtils.getBattleActorIds(el.getLosers());

                    long duration = (long) (gym.getCooldown() * 60 * 1000);

                    for (UUID loser : loserIds) {

                        PlayerBadges badges = BadgeProvider.getBadges(loser);

                        // Gets the challenger.
                        ServerPlayerEntity challenger = Elgyms.server.getPlayerManager().getPlayer(loser);

                        String challengerName = challenger != null ? challenger.getName().getString() : "";

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
            }

            // If it's a gym battle, sort it out.

            if (BattleProvider.isChampBattle(el.getBattle().getBattleId())) {
                // Ends the battle, removes the team, returns the data of the battle.
                ChampBattleData battleData = BattleProvider.getChampBattle();

                if (battleData == null) {
                    return Unit.INSTANCE;
                }

                // Gets a list of all winner UUIDs.
                ArrayList<UUID> winners = ElgymsUtils.getBattleActorIds(el.getWinners());

                ChampionConfig championConfig = GymProvider.getChampion();

                ServerPlayerEntity champion =
                        Elgyms.server.getPlayerManager().getPlayer(championConfig.getChampion().getUuid());

                Record championRecord = championConfig.getChampion().getRecord();

                // If the champion can't be found, just return.
                if (champion == null) {
                    return Unit.INSTANCE;
                }

                // If the leader didn't win. Add badges.
                if (ElgymsUtils.didChallengerWin(winners, battleData.getLeaderId())) {

                    UUID winnerUUID = winners.get(0);

                    ServerPlayerEntity winner = Elgyms.server.getPlayerManager().getPlayer(winnerUUID);

                    // If the winner can't be found, just return.
                    if (winner == null) {
                        return Unit.INSTANCE;
                    }

                    // Adds a loss to the champions record.
                    championRecord.setLosses(championRecord.getLosses() + 1);

                    // Adds the defeated champion to history.
                    Elgyms.championHistory.addHistory(new ChampionHistoryItem(championConfig.getChampion()));

                    // Sets the new champion to the player.
                    championConfig.setChampion(new Leader(winner.getUuid()));
                    championConfig.write();

                    // Runs the rewards.
                    championConfig.runWinnerRewards(winner);
                    championConfig.runLoserRewards(champion);

                    // Runs a broadcast the say the champion lost.
                    championConfig.runLossBroadcast(winner, champion);

                    Elgyms.battleLogger.addLog(
                            new BattleLog(championConfig.getBadge(), battleData.getLeaderId(),
                                    battleData.getChallengerName(), true));
                } else {
                    // Gets the loser
                    ArrayList<UUID> loserIds = ElgymsUtils.getBattleActorIds(el.getLosers());

                    UUID loserUUID = loserIds.get(0);

                    ServerPlayerEntity loser = Elgyms.server.getPlayerManager().getPlayer(loserUUID);

                    // If the loser can't be found, just return.
                    if (loser == null) {
                        return Unit.INSTANCE;
                    }

                    // Adds a win to the champions record.
                    championRecord.setWins(championRecord.getWins() + 1);
                    championConfig.write();

                    // Runs the rewards.
                    championConfig.runWinnerRewards(champion);
                    championConfig.runLoserRewards(loser);

                    // Runs a broadcast the say the champion won.
                    championConfig.runWinBroadcast(champion, loser);

                    Elgyms.battleLogger.addLog(
                            new BattleLog(championConfig.getBadge(), battleData.getLeaderId(),
                                    battleData.getChallengerName(), false));
                }

                BattleProvider.endChampBattle();
            }
            return Unit.INSTANCE;
        });
    }
}
