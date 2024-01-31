package org.pokesplash.elgyms.command.champion.champion;

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

public class Quit {
	public LiteralCommandNode<ServerCommandSource> build() {
		return CommandManager.literal("quit")
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
				.executes(this::run)
				.build();
	}

	public int run(CommandContext<ServerCommandSource> context) {

		try {
			if (!context.getSource().isExecutedByPlayer()) {
				context.getSource().sendMessage(Text.literal("This command must be ran by a player."));
				return 1;
			}

			ChampionConfig championConfig = GymProvider.getChampion();

			// Adds the defeated champion to history.
			Elgyms.championHistory.addHistory(new ChampionHistoryItem(championConfig.getChampion()));

			// Sets the new champion to the player.
			championConfig.setChampion(null);

			// Runs the rewards.
			championConfig.runDemotionRewards(context.getSource().getPlayer().getName().getString());

			// Runs the demotion broadcast
			championConfig.runDemoteBroadcast(context.getSource().getPlayer());
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
						"§b§lUsage:\n§3- champ quit", context.getSource().isExecutedByPlayer()
				))
		);

		return 1;
	}
}
