package org.pokesplash.elgyms.command.champion.champion;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.champion.ChampionConfig;
import org.pokesplash.elgyms.champion.ChampionHistoryItem;
import org.pokesplash.elgyms.gym.Leader;
import org.pokesplash.elgyms.provider.BadgeProvider;
import org.pokesplash.elgyms.provider.GymProvider;
import org.pokesplash.elgyms.util.Utils;

public class Give {
	public LiteralCommandNode<ServerCommandSource> build() {
		return CommandManager.literal("give")
				.requires(ctx -> {
					if (ctx.isExecutedByPlayer()) {
						// See's if the player executing the command is the champion.
						if (GymProvider.getChampion().getChampion() == null) {
							return false;
						} else {
							return GymProvider.getChampion().getChampion().getUuid().equals(ctx.getPlayer().getUuid());
						}
					} else {
						return true;
					}
				})
				.executes(this::usage)
				.then(CommandManager.argument("player", StringArgumentType.string())
						.suggests((ctx, builder) -> {
							for (ServerPlayerEntity player : Elgyms.server.getPlayerManager().getPlayerList()) {
								builder.suggest(player.getName().getString());
							}
							return builder.buildFuture();
						})
						.executes(this::run))
				.build();
	}

	public int run(CommandContext<ServerCommandSource> context) {

		try {
			if (!context.getSource().isExecutedByPlayer()) {
				context.getSource().sendMessage(Text.literal("This command must be ran by a player."));
				return 1;
			}

			String challengerName = StringArgumentType.getString(context, "player");

			ServerPlayerEntity newChampion = Elgyms.server.getPlayerManager().getPlayer(challengerName);

			// Checks the new champion is a player.
			if (newChampion == null) {
				context.getSource().sendMessage(Text.literal("§cPlayer " + challengerName + " could not be found."));
				return 1;
			}

			ChampionConfig championConfig = GymProvider.getChampion();

			// Checks the challenger has the required badge to challenger the champion.
			if (!BadgeProvider.getBadges(newChampion).containsBadge(championConfig.getRequiredBadge())) {
				context.getSource().sendMessage(Text.literal( Elgyms.lang.getPrefix() + "§c"
						+ newChampion.getName().getString() +
						" does not have the required badge to be champion."));
				return 1;
			}

			// Adds the defeated champion to history.
			Elgyms.championHistory.addHistory(new ChampionHistoryItem(championConfig.getChampion()));

			// Sets the new champion to the player.
			PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(newChampion);
			championConfig.setChampion(new Leader(newChampion.getUuid(), party));

			// Runs the rewards.
			championConfig.runWinnerRewards(newChampion.getName().getString());
			championConfig.runLoserRewards(context.getSource().getPlayer().getName().getString());

			// Runs a broadcast the say the champion lost.
			championConfig.runLossBroadcast(newChampion, context.getSource().getPlayer());
		}
		catch (Exception e) {
			context.getSource().sendMessage(Text.literal("§cSomething went wrong."));
			Elgyms.LOGGER.error(e.getStackTrace());
		}

		return 1;
	}

	public int usage(CommandContext<ServerCommandSource> context) {
		context.getSource().sendMessage(
				Text.literal(
						Elgyms.lang.getPrefix() +
						Utils.formatMessage(
						"§b§lUsage:\n§3- champ give <player>", context.getSource().isExecutedByPlayer()
				))
		);

		return 1;
	}
}
