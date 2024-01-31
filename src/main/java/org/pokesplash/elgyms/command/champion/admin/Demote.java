package org.pokesplash.elgyms.command.champion.admin;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.badge.PlayerBadges;
import org.pokesplash.elgyms.champion.ChampionHistoryItem;
import org.pokesplash.elgyms.command.CommandHandler;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.gym.Leader;
import org.pokesplash.elgyms.provider.BadgeProvider;
import org.pokesplash.elgyms.provider.GymProvider;
import org.pokesplash.elgyms.util.LuckPermsUtils;
import org.pokesplash.elgyms.util.Utils;

public class Demote {
	public LiteralCommandNode<ServerCommandSource> build() {
		return CommandManager.literal("demote")
				.requires(ctx -> {
					if (ctx.isExecutedByPlayer()) {
						return LuckPermsUtils.hasPermission(ctx.getPlayer(), CommandHandler.basePermission +
								".admin.champion.demote");
					} else {
						return true;
					}
				})
				.executes(this::usage)
				.then(CommandManager.argument("player", StringArgumentType.string())
						.suggests((ctx, builder) -> {
							for (PlayerBadges badges : BadgeProvider.getBadges().values()) {
								builder.suggest(badges.getName());
							}
							return builder.buildFuture();
						})
						.executes(this::run)).build();
	}

	public int run(CommandContext<ServerCommandSource> context) {

		try {
			if (!context.getSource().isExecutedByPlayer()) {
				context.getSource().sendMessage(Text.literal("This command must be ran by a player"));
				return 1;
			}

			String player = StringArgumentType.getString(context, "player");

			PlayerBadges badges = BadgeProvider.getBadges(player);

			if (badges == null) {
				context.getSource().sendMessage(Text.literal(
						"§cPlayer " + player + "§c could not be found."
				));
				return 1;
			}

			if (!badges.getUuid().equals(GymProvider.getChampion().getChampion().getUuid())) {
				context.getSource().sendMessage(Text.literal(
						"§cPlayer " + player + "§c is not the current Champion."
				));
				return 1;
			}

			Elgyms.championHistory.addHistory(new ChampionHistoryItem(GymProvider.getChampion().getChampion()));
			GymProvider.getChampion().setChampion(null);
			GymProvider.getChampion().runDemotionRewards(badges.getName());

			context.getSource().sendMessage(Text.literal("§2Successfully removed " + badges.getName() +
					" from Champion."));
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
										"§b§lUsage:\n§3- champion promote <player>",
										context.getSource().isExecutedByPlayer()
								))
		);
		return 1;
	}
}
