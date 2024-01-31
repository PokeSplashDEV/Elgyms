package org.pokesplash.elgyms.command.badge.leader;

import ca.landonjw.gooeylibs2.api.UIManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.badge.PlayerBadges;
import org.pokesplash.elgyms.command.CommandHandler;
import org.pokesplash.elgyms.provider.BadgeProvider;
import org.pokesplash.elgyms.util.LuckPermsUtils;
import org.pokesplash.elgyms.util.Utils;

public class OtherBadge {
	public CommandNode<ServerCommandSource> build() {
		return CommandManager.argument("player", StringArgumentType.string())
				.suggests((ctx, builder) -> {
					for (ServerPlayerEntity player :
							ctx.getSource().getServer().getPlayerManager().getPlayerList()) {
						builder.suggest(player.getName().getString());
					}
					return builder.buildFuture();
				})
				.requires(ctx -> {
					if (ctx.isExecutedByPlayer()) {
						return LuckPermsUtils.hasPermission(ctx.getPlayer(), CommandHandler.basePermission +
								".leader.badges.other");
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
			}

			String playerName = StringArgumentType.getString(context, "player");

			PlayerBadges badges = BadgeProvider.getBadges(playerName);

			if (badges == null) {
				context.getSource().sendMessage(Text.literal(Utils.formatMessage(
						"§cCould not find player " + playerName, context.getSource().isExecutedByPlayer()
				)));
				return 1;
			}

			UIManager.openUIForcefully(context.getSource().getPlayer(),
					new org.pokesplash.elgyms.ui.Badges().getPage(badges, true));
		} catch (Exception e) {
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
						"§b§lUsage:\n§3- badges <player>", context.getSource().isExecutedByPlayer()
				))
		);

		return 1;
	}
}
